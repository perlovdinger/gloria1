define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'i18next',
		'utils/UserHelper',
		'utils/backbone/GloriaCollection',
		'views/deliverycontrol/assign/collection/DeliveryFollowUpTeamFilterCollection', 
		'views/deliverycontrol/assign/collection/OrderCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, GloriaCollection, DeliveryFollowUpTeamFilterCollection, OrderCollection) {

	Gloria.module('DeliveryControlApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var module;
		var deliveryFollowUpTeamModel;
		var deliveryFollowUpTeamFilterCollection;
		var orderCollection;
		var assignDCLayout;
		var assignDCView;

		/**
		 * Prepare AssignDC.
		 */
		var prepareAssignDCLayout = function() {
			Gloria.trigger('showBreadcrumbView');
			Gloria.basicLayout.content.reset();
			require(['views/deliverycontrol/assign/view/AssignDCLayout'], function(AssignDCLayout) {
				assignDCLayout = new AssignDCLayout();		
				assignDCLayout.on('show', function() {
					showModuleView();
					showTeamView();
				});
				Gloria.basicLayout.content.show(assignDCLayout);
			});
		};
		
		/**
		 * Show Module View.
		 */
		var showModuleView = function() {
			require(['views/deliverycontrol/common/DeliveryControlModuleView'], function(DeliveryControlModuleView) {
				var deliveryControlModuleView = new DeliveryControlModuleView({
					module : 'assigndc'
				});
				assignDCLayout.moduleInfo.show(deliveryControlModuleView);
			});
		};

		/**
		 * Show Team View.
		 */
		var showTeamView = function() {
			require(['views/deliverycontrol/assign/view/FilterSelectorView'], function(FilterSelectorView) {
				var filterSelectorView = new FilterSelectorView({
					module : 'assigndc'
				});
				filterSelectorView.on('show', function() {
					prepareAssignDC();
				});
				assignDCLayout.teamInfo.show(filterSelectorView);
			});
		};

		/**
		 * Prepare Assign DC.
		 */
		prepareAssignDC = function() {
			require(['views/deliverycontrol/assign/model/DeliveryFollowUpTeamModel'], function(DeliveryFollowUpTeamModel) {
				deliveryFollowUpTeamModel = new DeliveryFollowUpTeamModel();
				var currentUser = UserHelper.getInstance().getUserId();
				var savedTeamInfo = window.localStorage.getItem('Gloria.User.DefaultTeam.' + currentUser);
				var savedTeamInfoObj = JSON.parse(savedTeamInfo);
				if(savedTeamInfoObj) {
					deliveryFollowUpTeamModel.url = deliveryFollowUpTeamModel.urlRoot + '/'	+ savedTeamInfoObj.name;
					deliveryFollowUpTeamModel.fetch({
						success : function(model) {
							initializeDataSourceObjects();
							setPageLayout();
						}
					});
				} else {
					initializeDataSourceObjects();
					setPageLayout();
				}
			});
		};
		
		/**
		 * Initialize Data Source Objects.
		 */
		var initializeDataSourceObjects = function() {
			var whichGrid = deliveryFollowUpTeamModel.get('followUpType') == 'SUPPLIER' ? 'supplierGrid' : 'projectGrid';
			deliveryFollowUpTeamFilterCollection = new DeliveryFollowUpTeamFilterCollection([], {
				state : {
					pageSize : function() {	// Check if any pageSize is already stored
						var object = JSON.parse(window.localStorage.getItem('Gloria.deliverycontrol.' + whichGrid
								+ '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : whichGrid
			});
			orderCollection = new OrderCollection([], {
				queryParams: {
					status : 'RECEIVED_PARTLY,PLACED'
				},
				state : {
					pageSize : function() {	// Check if any pageSize is already stored
						var object = JSON.parse(window.localStorage.getItem('Gloria.deliverycontrol.PoGrid'
								+ '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : 'PoGrid'
			});
		};

		/**
		 * Set Page Layout.
		 */
		var setPageLayout = function() {
			var followUpType = deliveryFollowUpTeamModel.get('followUpType');
			// Any module passed from user / defaulted to followUpType / order
			module = module || (followUpType && followUpType.toLowerCase()) || 'order';
			require(['views/deliverycontrol/assign/view/AssignDCView'], function(AssignDCView) {
				assignDCView = new AssignDCView({
					module : module,
					followUpType : deliveryFollowUpTeamModel ? deliveryFollowUpTeamModel.get('followUpType') : ''
				});		
				assignDCView.on('show', function() {
					showAssignDCButtonView(module);
					showAssignDCGridView(module);
				});
				assignDCLayout.assignDC.show(assignDCView);
			});
		};

		/**
		 * Show AssignDC Button View
		 */
		var showAssignDCButtonView = function(subModule) {
			require(['views/deliverycontrol/assign/view/AssignDCButtonView'], function(AssignDCButtonView) {
				var assignDCButtonView = new AssignDCButtonView({
					module : subModule
				});
				if (assignDCView.buttonDiv) {
					assignDCView.buttonDiv.empty();
				}
				var buttonId = '#' + subModule + 'Button';
				assignDCView.addRegion("buttonDiv", buttonId);
				assignDCView.buttonDiv.show(assignDCButtonView);
			});
		};

		/**
		 * Show AssignDC Grid View
		 */
		var showAssignDCGridView = function(subModule) {
			switch (subModule) {
			case 'supplier':
			case 'project':
				showSupplierProjectGridView(subModule, assignDCView);
				break;
			case 'order':
				showPOGridView(subModule, assignDCView);
				break;
			default:
				throw new TypeError('Tab value not recognized!');
			}
		};

		/**
		 * Show Supplier/Project Grid View
		 */
		var showSupplierProjectGridView = function(subModule, parentView) {
			require(['views/deliverycontrol/assign/view/SupplierProjectGridView'], function(SupplierProjectGridView) {
				var supplierProjectGridView = new SupplierProjectGridView({
					module : subModule,
					collection : deliveryFollowUpTeamFilterCollection,
					team : deliveryFollowUpTeamModel,
					filter : subModule
				});
				supplierProjectGridView.on('show', function() {
					processSupplierProjectGridView(subModule, parentView);
				}, this);
				showGridView(subModule, parentView, supplierProjectGridView);
			});
		};

		/**
		 * Process Supplier/Project Information
		 */
		var processSupplierProjectGridView = function(subModule, assignDCView) {
			deliveryFollowUpTeamFilterCollection.url = '/common/v1/deliveryfollowupteams/'
					+ deliveryFollowUpTeamModel.id + '/deliveryfollowupteamfilters';
			deliveryFollowUpTeamFilterCollection.fetch();
		};

		/**
		 * Show PO Grid View
		 */
		var showPOGridView = function(subModule, parentView) {
			require(['views/deliverycontrol/assign/view/PoGridView'], function(PoGridView) {
				var poGridView = new PoGridView({
					collection : orderCollection,
					filter : subModule
				});
				poGridView.on('show', function() {
					processPOGridView(subModule, parentView);
				}, this);
				showGridView(subModule, parentView, poGridView);
			});
		};

		/**
		 * Process PO Information
		 */
		var processPOGridView = function(subModule, assignDCView) {
			var currentUser = UserHelper.getInstance().getUserId();
			var savedTeamInfo = window.localStorage.getItem('Gloria.User.DefaultTeam.' + currentUser);
			var savedTeamInfoObj = JSON.parse(savedTeamInfo);
		    orderCollection.url = '/material/v1/orderlines'+ (savedTeamInfoObj ? '?deliveryControllerTeamId=' + savedTeamInfoObj.id : '');
			orderCollection.fetch();
		};

		/**
		 * Show Grid View.
		 */
		var showGridView = function(subModule, parentView, gridView) {
			if (parentView.gridDiv) {
				parentView.gridDiv.empty();
			}
			var gridId = '#' + subModule + 'Grid';
			parentView.addRegion("gridDiv", gridId);
			parentView.gridDiv.show(gridView);
		};

		/**
		 * Show AddEdit Modal View.
		 */
		var showAddEditModalView = function(module, model) {
			var currentUser = UserHelper.getInstance().getUserId();
			var savedTeamInfo = window.localStorage.getItem('Gloria.User.DefaultTeam.' + currentUser);
			require(['views/deliverycontrol/assign/view/AddEditModalView'], function(AddEditModalView) {
				var addEditModalView = new AddEditModalView({
					module : module,
					model : model,
					deliveryFollowUpTeamModel : deliveryFollowUpTeamModel,
					team : savedTeamInfo ? JSON.parse(savedTeamInfo) : {},
					collection: deliveryFollowUpTeamFilterCollection
				});
				Gloria.basicModalLayout.content.show(addEditModalView);
			});
		};

		/**
		 * Show Change Modal View.
		 */
		var showChangeModalView = function(module, ids) {
			var currentUser = UserHelper.getInstance().getUserId();
			var savedTeamInfo = window.localStorage.getItem('Gloria.User.DefaultTeam.' + currentUser);
			require(['views/deliverycontrol/assign/view/ChangeModalView'],	function(ChangeModalView) {
				var changeModalView = new ChangeModalView({
					module : module,
					ids : ids,
					model : deliveryFollowUpTeamModel,
					team : savedTeamInfo ? JSON.parse(savedTeamInfo) : {},
				});
				Gloria.basicModalLayout.content.show(changeModalView);
			});
		};

		/**
		 * Show Delete Modal View.
		 */
		var showDeleteModalView = function(module, model) {
			require(['views/deliverycontrol/assign/view/DeleteModalView'], function(DeleteModalView) {
				var deleteModalView = new DeleteModalView({
					module : module,
					model : model,
					team : deliveryFollowUpTeamModel
				});
				Gloria.basicModalLayout.content.show(deleteModalView);
			});
		};

		/**
		 * Show Reassign Modal View.
		 */
		var showReassignModalView = function(models) {
			require(['views/deliverycontrol/assign/view/ReassignModalView'], function(ReassignModalView) {
				var reassignModalView = new ReassignModalView({
					models : models,
					team : deliveryFollowUpTeamModel
				});
				Gloria.basicModalLayout.content.show(reassignModalView);
			});
		};

		/**
		 * Save Delivery Controller Info.
		 */
		var saveDeliveryControllerInfo = function(module, modelData, formData) {
			var saveInfo = function(model, data) {
				model.save(data, {
					success : function(response) {
						deliveryFollowUpTeamFilterCollection.add(response, {merge: true});
						Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:saved', true);
						deliveryFollowUpTeamFilterCollection.fetch({cache: false});
					},						
					validationError : function(errorMessage, errors) {
						Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:saved', false, errorMessage);
					},
					error : function(error, errorMessage) {
						if(errorMessage.status != 400) {//@Important: handle all errors except validation error
														//as we have defined validationError to handle such (400) case
							Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:saved', false, error);
						}
                    }
				});
			};
			if (!formData.id) { // POST
				formData.id = null;
				require(['views/deliverycontrol/assign/model/DeliveryFollowUpTeamFilterModel'], function(DeliveryFollowUpTeamFilterModel) {
					var model = new DeliveryFollowUpTeamFilterModel();
					model.url = '/common/v1/deliveryfollowupteams/' + deliveryFollowUpTeamModel.get('id')
								+ '/deliveryfollowupteamfilters';
					saveInfo(model, formData);
				});
			} else { // PUT
				modelData.url = '/common/v1/deliveryfollowupteams/' +  deliveryFollowUpTeamModel.get('id')
								+ '/deliveryfollowupteamfilters/' + modelData.id;
				saveInfo(modelData, formData);
			}
		};

		/**
		 * Delete Delivery Controller Info.
		 */
		var deleteDeliveryControllerInfo = function(module, model) {
			model.destroy({
				url : '/common/v1/deliveryfollowupteams/' + deliveryFollowUpTeamModel.get('id') + '/deliveryfollowupteamfilters/' + model.id,
				success : function(data) {
					deliveryFollowUpTeamFilterCollection.remove(deliveryFollowUpTeamFilterCollection.get(model.id));
					Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:deleted');
				}
			});
		};

		/**
		 * Change Delivery Controller Info.
		 */
		var changeDeliveryControllerInfo = function(module, data) {
			deliveryFollowUpTeamModel.save(data, {
				success : function(model) {
					Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:changed');
				},
				validationError: function(errorMessage) {
					Gloria.trigger('showAppMessageView', { 
			            type : 'error',
			            title : i18n.t('errormessages:general.title'),
			            message: errorMessage
			        });
				}
			});
		};

		/**
		 * Reassign Delivery Controller Info.
		 */
		var reassignDeliveryControllerInfo = function(models, data) {
			_.each(models, function(model) {
				model.set(data);
			});
			var collection = new GloriaCollection(models);
			Backbone.sync('update', collection, {
				url : '/procurement/v1/orderlines/assign',
				success : function(response) {
					Gloria.DeliveryControlApp.trigger('PoGrid:clearselection');
					Gloria.DeliveryControlApp.trigger('ControlButton:refresh');					
					processPOGridView();
				},
				error : function() {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : new Array({
		    				message : i18n.t('Gloria.i18n.processFailed')
		    			})
		    		});
				}
			});
		};

		Controller.AssignDCController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.DeliveryControlApp, 'AddEditModal:show', showAddEditModalView);
				this.listenTo(Gloria.DeliveryControlApp, 'ChangeModal:show', showChangeModalView);
				this.listenTo(Gloria.DeliveryControlApp, 'DeleteModal:show', showDeleteModalView);
				this.listenTo(Gloria.DeliveryControlApp, 'ReassignModal:show',	showReassignModalView);
				this.listenTo(Gloria.DeliveryControlApp, 'DeliveryControllerInfo:save', saveDeliveryControllerInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'DeliveryControllerInfo:delete', deleteDeliveryControllerInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'DeliveryControllerInfo:change', changeDeliveryControllerInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'DeliveryControllerInfo:reassign', reassignDeliveryControllerInfo);
			},

			control : function(page) {
				module = page;
				prepareAssignDCLayout.call(this);
			},
			
			onDestroy: function() {
				module = null;
			    deliveryFollowUpTeamModel = null;
		        deliveryFollowUpTeamFilterCollection = null;
		        orderCollection = null;
		        assignDCLayout = null;
		        assignDCView = null;
			}
		});
	});

	return Gloria.DeliveryControlApp.Controller.AssignDCController;
});
