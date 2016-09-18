define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'i18next',
		'views/warehouse/collection/MaterialLineCollection',
		'utils/backbone/GloriaCollection', 
		'utils/backbone/GloriaModel', 
		'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, MaterialLineCollection, Collection, Model, UserHelper) {
    
	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var storeView;
		var materialLineCollection;
		var adjustStoreModalView;
		var reserveMaterialLineIds;
		
		/**
		 * Prepare Warehouse Store.
		 */ 
		var prepareWarehouseStore = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
			window.sessionStorage.removeItem('storeMode');
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			materialLineCollection = new MaterialLineCollection([], {
				queryParams : {
					status : 'READY_TO_STORE,REQUESTED',
					calculateStockBalance : 'true',
					suggestBinLocation : 'true'
				},
				filterKey : 'StoreGrid',
				mode: 'server'
			});
		};
		
		/**
		 * Set Page Layout
		 */ 
		var setPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', null);
			require(['views/warehouse/store/view/StoreView'], function(StoreView) {
				storeView = new StoreView();
				storeView.on('show', function() {
					showStoreContent();
				}, this);
				Gloria.basicLayout.content.show(storeView);
			});
		};
		
		/**
		 * Show/Render Store Module and Grid View.
		 */
		var showStoreContent = function() {
			require(['views/warehouse/common/WarehouseModuleView', 'views/warehouse/store/view/StoreGridView'],
			function(WarehouseModuleView, StoreGridView) {
				var warehouseModuleView = new WarehouseModuleView({
					module : 'store',
					control : 'store' // Button control
				});
				// Attach to moduleInfo region
				storeView.moduleInfo.show(warehouseModuleView);	
				var storeGridView = new StoreGridView({
					collection : materialLineCollection,
					isToStoreModule : false
				});
				storeGridView.on('show', function() {
					// Grid skeleton/set up is completed, now prepare to fetch page data!
		            materialLineCollection.fetch();
				}, this);
				// Attach to gridInfo region
				if (storeView.gridInfo) {
					storeView.gridInfo.empty();
				}
				var gridId = '#materialToStoreGrid';
				storeView.addRegion("gridInfo", gridId);
				storeView.gridInfo.show(storeGridView);
			});
		};
		
		/**
		 * Prepare Warehouse To Store
		 */
		var prepareToStoreView = function(models) {
			var reserveMaterialLines = new Collection();
			reserveMaterialLineIds = _.pluck(models, 'id').join(',');
			reserveMaterialLines.url = '/warehouse/v1/materiallines/' + reserveMaterialLineIds +'/lock?userId='
				+ UserHelper.getInstance().getUserId()+ '&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
			Backbone.sync('update', reserveMaterialLines, {
				success : function(model, resp, xhr) {
					// Update Data Source Objects - materialLineCollection
					updateDataSourceObjects(models);
					// Update Page Layout
					updateToStorePageLayout(storeView);					
				},
				error : function() {
					Gloria.WarehouseApp.trigger('Store:StoreInfoSaved', false);
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
		    			message : errorMessage
		    		});
				}
			});	
			
		};
		
		/**
		 * Update Data Source Objects
		 */
		var updateDataSourceObjects = function(models) {
			if(models) {
				materialLineCollection.reset([]);				
				materialLineCollection.set(models, {silent : true});
			}
		};
		
		/**
		 * Update To Store Page Layout
		 */
		var updateToStorePageLayout = function(parentView) {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', null);
			window.sessionStorage.setItem('storeMode', 'edit');
			require(['views/warehouse/store/view/StoreView'], function(StoreView) {
				storeView = new StoreView();
				storeView.on('show', function() {
					showToStoreContent();
				}, this);
				Gloria.basicLayout.content.show(storeView);
			});
		};
		
		/**
		 * Re-render To Store Module and Grid View
		 */
		var showToStoreContent = function() {
			require(['views/warehouse/common/WarehouseModuleView', 'views/warehouse/store/view/StoreGridView'],
			function(WarehouseModuleView, StoreGridView) {
				var warehouseModuleView = new WarehouseModuleView({
					module : 'store',
					control : 'toStore' // Button control
				});
				
				// Attach to moduleInfo region
				storeView.moduleInfo.show(warehouseModuleView);				
				var storeGridView = new StoreGridView({
					collection : materialLineCollection,
					isToStoreModule : true
				});
				
				if (storeView.gridInfo) {
					storeView.gridInfo.empty();
				}
				var gridId = '#materialStoreGrid';
				storeView.addRegion("gridInfo", gridId);
				
				// Attach to gridInfo region
				storeView.gridInfo.show(storeGridView);
			});
		};
		
		var split = function(model, collection) {
		    if(materialLineCollection) {
		    	materialLineCollection.currentSplitCount = collection.currentSplitCount;
                var index = materialLineCollection.indexOf(model);
                materialLineCollection.remove(model);
                if(collection.length > 1) {
                	collection.each(function(model) {
        		        model.isSplitted = true;
					});
                }
                materialLineCollection.add(collection.models, {at: index});
                Gloria.WarehouseApp.trigger('Store:savedAdjustedMaterialLines', true);
            }
		};
		
		var saveStoreInfo = function(collection) {
			// Remove ids from the newly created models before sending to server.
			collection.each(function(thisModel) {
				if(thisModel.get('id').toString().indexOf('_') != -1) {
					thisModel.unset('id', {silent:true});
				}
			});
            Backbone.sync('update', collection, {
                url : '/procurement/v1/materiallines?action=store&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(),
                success : function(model, resp, xhr) {
                    Gloria.WarehouseApp.trigger('Store:StoreInfoSaved', true);
                },
                error : function() {
                    Gloria.WarehouseApp.trigger('Store:StoreInfoSaved', false);
                },
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
                        message : errorMessage
                    });
                }
            });
        };
		
		var cancelToStore = function(){
			var cancelReservedMaterialLines = new Collection();
			cancelReservedMaterialLines.url = '/warehouse/v1/materiallines/' + reserveMaterialLineIds +'/unlock?userId=' + UserHelper.getInstance().getUserId() + '&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
			Backbone.sync('update', cancelReservedMaterialLines, {
				success : function(model, resp, xhr) {
					Backbone.history.loadUrl(Backbone.history.fragment);
				},
				error : function() {
					Gloria.WarehouseApp.trigger('Store:StoreInfoSaved', false);
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
		    			message : errorMessage
		    		});
				}
			});	
			
		};
		
		var printStoreList = function() {
		    require(['hbs!views/warehouse/store/view/storePrintView'], function(StorePrintViewTemplate) {
		        var content = StorePrintViewTemplate({
                    materiallines: materialLineCollection.toJSON()
                });
		        Gloria.trigger('print', {
	                content: content
	            });        
		    });		
		};
		
		var showStoreAdjustModalView = function(model) {
		    var that = this;		   
		    var materialLineToSplit = model[0];
		    var storeCollection = new MaterialLineCollection(materialLineToSplit.toJSON());
			require(['views/warehouse/store/view/AdjustStoreModalView'], function(AdjustStoreModalView) {
				adjustStoreModalView = new AdjustStoreModalView({
					model : materialLineToSplit
				});
				adjustStoreModalView.on('show', function() {
					var currentSplitCount = materialLineCollection.currentSplitCount || 0;
					showAdjustStoreCollectionView(materialLineToSplit, storeCollection, currentSplitCount);
				}, that);
				Gloria.basicModalLayout.content.show(adjustStoreModalView);
			});
		};
		
		var showAdjustStoreCollectionView = function(model, collection, currentSplitCount) {
			require(['views/warehouse/store/view/AdjustStoreCollectionView'], function(AdjustStoreCollectionView) {				
				var adjustStoreCollectionView = new AdjustStoreCollectionView({
					model : model,			
					collection : collection,
					currentSplitCount : currentSplitCount
				});
				adjustStoreModalView.adjustStore.show(adjustStoreCollectionView);
			});
		};
		
		Controller.StoreController = Marionette.Controller.extend({
	       
	    	initialize : function() {
	            this.initializeListeners();
	        },
	        
	        initializeListeners : function() {
	            this.listenTo(Gloria.WarehouseApp, 'Store:ShowToStore', prepareToStoreView);
	            this.listenTo(Gloria.WarehouseApp, 'Store:SaveStoreInfo', saveStoreInfo);
	            this.listenTo(Gloria.WarehouseApp, 'Store:split', split);	            
	            this.listenTo(Gloria.WarehouseApp, 'Store:Store:Cancel', cancelToStore);	            
	            this.listenTo(Gloria.WarehouseApp, 'Store:print:storeList', printStoreList);	
				this.listenTo(Gloria.WarehouseApp, 'Store:showStoreAdjustModalView', showStoreAdjustModalView);
			},
	
	        control: function() {
	        	prepareWarehouseStore.call(this);
	        },
	        
	        onDestroy: function() {	            
	            storeView = null;
	            materialLineCollection = null;
	            adjustStoreModalView = null;
	            reserveMaterialLineIds = null;
	        }
	    });
	});
	
	return Gloria.WarehouseApp.Controller.StoreController;
});
