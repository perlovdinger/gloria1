define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'utils/UserHelper',
        'utils/backbone/GloriaPageableCollection',
        'utils/backbone/GloriaCollection',
        'utils/backbone/GloriaModel'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, GloriaPageableCollection, Collection, GloriaModel) {

	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var pickListId = undefined;
		var pickListsCollection = undefined;
		var pickListModel = undefined;
		var materialLineModel = undefined;
		var materialLineCollection = undefined;
		
		/**
		 * Route : warehouse/pick Prepare Pick View
		 */
		var preparePickView = function() {
			// Initialize Pick Data Source Objects
			initializePickDataSourceObjects();
			// Set Page Layout
			setPickPageLayout();
		};
		
		/**
		 * Initialize Pick Data Source Objects
		 */
		var initializePickDataSourceObjects = function() {
			pickListsCollection = new GloriaPageableCollection([], {
				state : {
					pageSize : 5,
					currentPage : 1
				}
			});
		};
		
		/**
		 * Set Pick Page Layout
		 */
		var setPickPageLayout = function() {
			Gloria.basicLayout.content.reset();
			require([ 'views/mobile/warehouse/view/pick/PickListLayout' ], function(PickListLayout) {
				var pickListLayout = new PickListLayout();
				pickListLayout.on('show', function() {
					processPickInfo(pickListLayout);
				}, this);
				Gloria.basicLayout.content.show(pickListLayout);
			});
		};
		
		/**
		 * Show Pick Grid View
		 */
		var showPickGridView = function(pickListLayout) {
			require([ 'views/mobile/warehouse/view/pick/PickListGridView' ], function(PickListGridView) {
				var pickListGridView = new PickListGridView({
					collection : pickListsCollection
				});
				pickListLayout.grid.show(pickListGridView);
			});
		};
		
		/**
		 * Process Pick Information
		 */
		var processPickInfo = function(pickListLayout) {
			pickListsCollection.url = '/procurement/v1/picklists?status=REQUESTED&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
			pickListsCollection.fetch({
				success : function() {
					showPickGridView(pickListLayout);
				}
			});
		};
		
		/**
		 * Route : warehouse/pick/list/:id Prepare Pick
		 * List View
		 */
		var preparePickListView = function() {
			initializePickListDataSourceObjects();
			lockMaterialLinesForAPickList();
		};
		
		/**
		 * Initialize Pick List Data Source Objects
		 */
		var initializePickListDataSourceObjects = function() {
			materialLineCollection = new Collection();
			pickListModel = new GloriaModel({
                id : pickListId
            });
		};
		
		/**
		 * Lock Material Lines
		 */
		var lockMaterialLinesForAPickList = function() {
			var reserveMaterialLinesForAPickList = new Collection();
			reserveMaterialLinesForAPickList.url = '/procurement/v1/picklists/' + pickListId
					+ '/lock?userId=' + UserHelper.getInstance().getUserId();
			Backbone.sync('update', reserveMaterialLinesForAPickList, {
				success : function(model, resp, xhr) {
					fetchMaterialLines(pickListId);
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						message : errorMessage
					});
				}
			}).fail(function() {
				Backbone.history.navigate('warehouse/pick', {replace: true, trigger: true});
			});
		};
		
		/**
		 * Fetch Material Lines
		 */
		var fetchMaterialLines = function(pickListId) {
			materialLineCollection.url = '/procurement/v1/picklists/' + pickListId
					+ '/materiallines?status=REQUESTED&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
			materialLineCollection.fetch({
				success : function(collection) {
					collection.each(function(model) {
						model.set('pickedQuantity',	model.get('quantity'));
					});
					prepareAndShowPickListView(pickListId);
				}
			});
		};
		
		/**
		 * Prepare and Show Pick List View
		 */
		var prepareAndShowPickListView = function(id) {
		    pickListModel.fetch({
				url : '/procurement/v1/picklists/' + id + '?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(),
				success : function(response) {
					require([ 'views/mobile/warehouse/view/pick/PickListView' ], function(PickListView) {
						var pickListView = new PickListView({
							collection : materialLineCollection,
							pickListTotalItems : materialLineCollection.length,
							shipSkippable : response && response.get('shipSkippable')
						});
						Gloria.basicLayout.content.show(pickListView);
					});
				}
			});
		};
				
		/**
		 * Pick
		 */
		var pick = function(partModel) {
			if (partModel) {
				partModel.url = '/procurement/v1/materiallines/' + partModel.id
						+ '?action=pick&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
				partModel.save(null, {
					success : function(model) {
						materialLineCollection.remove(model);
						if(materialLineCollection.length == 0) {
							Backbone.history.navigate('warehouse/pick', {
								trigger : true
							});
						}
					},
					validationError : function(errorMessage, errors) {
						Gloria.trigger('showAppMessageView', {
							type : 'error',
							message : errorMessage
						});
					}
				});
			}
		};

		/**
		 * Unlock Material Lines
		 */
		var unlockMaterialLinesForAPickList = function() {
			var reserveMaterialLinesForAPickList = new Collection();
			reserveMaterialLinesForAPickList.url = '/procurement/v1/picklists/' + pickListId
				+ '/unlock?userId=' + UserHelper.getInstance().getUserId();
			Backbone.sync('update', reserveMaterialLinesForAPickList, {
				validationError : function(errorMessage, errors) {
					if(!errorMessage.length) return; 
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						message : errorMessage
					});
				}
			});
		};
		

		/*common print pull label pop up for PickPullLabelprintListView*/
		
		
		var pickPullLabelprintList = function(search) {
	            require([ 'views/mobile/warehouse/view/pick/PickPullLabelprintListView' ], function(PickPullLabelprintListView) {
	                var pickPullLabelprintListView = new PickPullLabelprintListView({
	                    pickListId : pickListId,
	                    model:pickListModel,
	                    module :'list'
	                });        
	                Gloria.basicModalLayout.content.show(pickPullLabelprintListView);
	            });
	        };
		

	        var printPullLabelprintListData = function(id, qty) {
                var url= '/common/v1/picklists/' + id + '/pullabels?whSiteId='+ UserHelper.getInstance().getDefaultWarehouse();
                if (qty) {
                    url =url+'&quantity=' + qty;
                } else {
                    url =url;
                }
                pickListModel.save({}, {
                    url : url
                });
            };
	        
	        var pickPullLabelprintPart = function(matrialLineModel) {
	            this.materialLineModel = matrialLineModel;
                require([ 'views/mobile/warehouse/view/pick/PickPullLabelprintListView' ], function(PickPullLabelprintListView) {
                    var pickPullLabelprintListView = new PickPullLabelprintListView({
                       pickListId : pickListId,
                        model:pickListModel,
                        module :'part'
                    });        
                    Gloria.basicModalLayout.content.show(pickPullLabelprintListView);
                });
            };
	        
        var pickPullLabelprintPartData = function(id,qty) {
            var url='/common/v1/picklists/' + id + '/materiallines/' + this.materialLineModel.id + '/pullabels?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
            if(qty){
                url=url+'&quantity='+qty;
            }
            else{
                url=url;
            }
            pickListModel.save({}, {
                url : url
            });
        };
        
		
		
		
		var unlock = function(options) {
			if(options.previous.hash.indexOf('warehouse/pick/') != -1) {
				unlockMaterialLinesForAPickList();
			}
		};
		
		/**
		 * Pick And Mark as Ship
		 */
		var pickAndMarkAsShip = function(partModel) {
			partModel.url = '/procurement/v1/materiallines/' + partModel.id + '?action=pickShip&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
			partModel.save(null, {
				success : function(model) {
					Backbone.history.navigate('warehouse/pick', {
						trigger : true
					});
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						message : errorMessage
					});
				}
			});
		};

		Controller.PickController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.WarehouseApp, 'Pick:materialLine:pick', pick);
				this.listenTo(Gloria.WarehouseApp, 'Pick:PullLabel:print:List', pickPullLabelprintList);
				this.listenTo(Gloria.WarehouseApp, 'Pick:PullLabel:print:List:qty', printPullLabelprintListData);
				this.listenTo(Gloria.WarehouseApp, 'Pick:PullLabel:print:Part', pickPullLabelprintPart);
				this.listenTo(Gloria.WarehouseApp, 'Pick:PullLabel:print:part:qty', pickPullLabelprintPartData);
				this.listenTo(Gloria, 'route:changed', unlock);
				this.listenTo(Gloria.WarehouseApp, 'Pick:materialLine:pickAndMarkAsShip', pickAndMarkAsShip);
			},

			control : function(options) {
				options || (options = {});
				Gloria.trigger('change:title', 'Gloria.i18n.warehouse.pick.pick');
				if (options.pickListId) {
					pickListId = options.pickListId;
					preparePickListView.call(this);
				} else {
					preparePickView.call(this);
				}
			},

			onDestroy : function() {
				pickListId = null;
				pickListsCollection = null;
				pickListModel = null;
				materialLineCollection = null;
			}
		});
	});

	return Gloria.WarehouseApp.Controller.PickController;
});
