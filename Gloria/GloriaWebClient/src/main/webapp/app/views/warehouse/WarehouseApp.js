define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'utils/marionette/GloriaRouter',
        'controllers/controllerManager',
        'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaRouter, ControllerManager, UserHelper) {

	Gloria.module('WarehouseApp', function(WarehouseApp, Gloria, Backbone, Marionette, $, _) {
		
	    WarehouseApp.title = "Gloria.i18n.warehouse.warehouse";
	    
	    WarehouseApp.routes = {
			'warehouse/setup(/:id)' : 'showWarehouseSetup',
			//'warehouse' : 'showWarehouseReceive',
			'warehouse/receive' : 'showWarehouseReceive',
			'warehouse/receive/toReceive(/:deliveryNote)' : {
				handler: 'warehouseToRecevie',
				confirmLeave: function(deliveryNote) {
					return !!deliveryNote;
				},
				leaveURL: 'warehouse/receive'
			},
			'warehouse/receive/notReceived' : 'warehouseNotRecevied',
			'warehouse/receive/received' : 'warehouseRecevied',
			'warehouse/receive/details(/:id)' : {
				handler: 'showWarehouseReceiveDetails',
				confirmLeave: true,
				leaveURL: 'warehouse/receive'
			},
			'warehouse/store' : {
				handler: 'showWarehouseStore',
				confirmLeave: function() {
					return window.sessionStorage.getItem('storeMode') === 'edit';
				},
				leaveURL: 'warehouse/store'
			},
			'warehouse/pickship' : 'showWarehousePickShip',
			'warehouse/pick' : 'showWarehousePick',
			'warehouse/ship' : 'showWarehouseShip',
			'warehouse/inpick' : 'showWarehouseShip',   //reusing the same Controller. Changing the modulename.
			'warehouse/pick/pickListDetails/:pickListId' : {
				handler: 'showWarehousePickDetails',
				confirmLeave: true,
				leaveURL: 'warehouse/pick'
			},
			'warehouse/ship/pickListDetails/:pickListId' : {
				handler: 'showWarehousePickDetails',
				confirmLeave: true,
				leaveURL: 'warehouse/ship'
			},
			'warehouse/ship/createDispatchNote/:requestListId' : {
				handler: 'showCreateDispatchNote',
				confirmLeave: true,
				leaveURL: 'warehouse/ship'
			},
			'warehouse/ship/openDispatchNote/:dispatchNoteId' : {
				handler: 'showOpenDispatchNote',
				confirmLeave: true,
				leaveURL: 'warehouse/ship'
			},
			'warehouse/inventory' : 'showWarehouseInventory',
			'warehouse/qualityinspection(/:tab)': 'showWarehouseQIOverview',
			'warehouse/qisettings(/:tab)' : 'showWarehouseQISettings',
			'warehouse/qualityinspection/mandatory/mandatoryDetails(/:id)' : {
				handler:'showQualityInspectionDetails',
				confirmLeave: true,
				leaveURL: 'warehouse/qualityinspection/mandatory'
			},
			'warehouse/qualityinspection/optional/optionalDetails(/:id)' : {
				handler:'showQualityInspectionDetails',
				confirmLeave: true,
				leaveURL: 'warehouse/qualityinspection/optional'
			},
			'warehouse/qualityinspection/blockedPart/blockedDetails(/:id)' : {
				handler:'showQualityInspectionDetails',
				confirmLeave: true,
				leaveURL: 'warehouse/qualityinspection/blockedPart'
			},
			'warehouse/qualityinspection/inStock/inStockDetails(/:id)' : {
                handler:'showQualityInspectionDetails',
                confirmLeave: true,
                leaveURL: 'warehouse/qualityinspection/inStock'
            },
			'warehouse/admin(/:tab)' : 'showWarehouseAdminView'
		};
	    
		WarehouseApp.Router = GloriaRouter.extend({
		    execute: function() {           
                loadInitialData();
                GloriaRouter.prototype.execute.apply(this, arguments);                  
            }, 
			appRoutes : GloriaRouter.prototype.transformAppRoutes(WarehouseApp.routes)
		});

		var API = {
			showWarehouseSetup : function(warehouseId, warehouseSetupController) {
				if (warehouseSetupController) {
					warehouseSetupController.control(warehouseId);
	            } else {
	            	Gloria.controllerManager.getController('warehouseSetupController', this.showWarehouseSetup, warehouseId);
	            }
			},
			
			showWarehouseReceive: function() {
				Backbone.history.navigate('warehouse/receive/toReceive', {
				    trigger : true,
                    replace : true
                });
			},

			showWarehousePickShip: function() {
				Backbone.history.navigate('warehouse/pick', {
				    trigger : true,
                    replace : true
                });
			},
			
			warehouseToRecevie: function(deliveryNote, warehouseToReceiveController) {			    
			    if (warehouseToReceiveController) {
			        warehouseToReceiveController.control(deliveryNote);
			        
                } else {
                    Gloria.controllerManager.getController('warehouseToReceiveController', this.warehouseToRecevie, deliveryNote);
                }
			},
			
			warehouseNotRecevied : function(warehouseNotReceivedController) {
				if (warehouseNotReceivedController) {
					warehouseNotReceivedController.control();
	            } else {
	            	Gloria.controllerManager.getController('warehouseNotReceivedController', this.warehouseNotRecevied);
	            }
			},
			
			warehouseRecevied : function(warehouseReceivedController) {
				if (warehouseReceivedController) {
					warehouseReceivedController.control();
	            } else {
	            	Gloria.controllerManager.getController('warehouseReceivedController', this.warehouseRecevied);
	            }
			},
			
			showWarehouseReceiveDetails : function(id, warehouseReceiveDetailsController) {
				if (warehouseReceiveDetailsController) {
					warehouseReceiveDetailsController.control(id);
	            } else {
	            	Gloria.controllerManager.getController('warehouseReceiveDetailsController', this.showWarehouseReceiveDetails, id);
	            }
			},
			
			showWarehouseStore : function(page, warehouseStoreController) {
				if (warehouseStoreController) {
					warehouseStoreController.control(page);
	            } else {
	            	Gloria.controllerManager.getController('warehouseStoreController', this.showWarehouseStore, page);
	            }
			},
			
			showWarehousePick: function(warehousePickController) {
				if (warehousePickController) {
					warehousePickController.control({subModule : 'pick'});
	            } else {
	            	Gloria.controllerManager.getController('warehousePickController', this.showWarehousePick);
	            }
			},
			
			showWarehouseShip: function(warehouseShipController) {
				if (warehouseShipController) {
					var subModule = location.hash.indexOf('warehouse/ship') == 1 ? "ship" : "inpick";
					warehouseShipController.control({subModule : subModule});
	            } else {
	            	Gloria.controllerManager.getController('warehouseShipController', this.showWarehouseShip);
	            }
			},
			
			showWarehousePickDetails : function(pickListId, warehousePickController) {
				if (warehousePickController) {
					warehousePickController.control({pickListId : pickListId});
	            } else {
	            	Gloria.controllerManager.getController('warehousePickController', this.showWarehousePickDetails, pickListId);
	            }
			},
			
			showCreateDispatchNote: function(requestListId, warehouseShipController) {
				if (warehouseShipController) {
					warehouseShipController.control({requestListId : requestListId});
	            } else {
	            	Gloria.controllerManager.getController('warehouseShipController', this.showCreateDispatchNote, requestListId);
	            }
			},
			
			showOpenDispatchNote: function(dispatchNoteId, warehouseShipController) {
				if (warehouseShipController) {
					warehouseShipController.control({dispatchNoteId : dispatchNoteId});
	            } else {
	            	Gloria.controllerManager.getController('warehouseShipController', this.showOpenDispatchNote, dispatchNoteId);
	            }
			},
			
			showWarehouseInventory : function(warehouseInventoryController) {
				if (warehouseInventoryController) {
					warehouseInventoryController.control();
	            } else {
	            	Gloria.controllerManager.getController('warehouseInventoryController', this.showWarehouseInventory);
	            }
			},
			
			showWarehouseQIOverview: function(tab, qualityInspectionOverviewController) {
				if (qualityInspectionOverviewController) {
					qualityInspectionOverviewController.control(tab);
	            } else {
	            	Gloria.controllerManager.getController('qualityInspectionOverviewController', this.showWarehouseQIOverview, tab);
	            }
			},
			
			showWarehouseQISettings : function(tab, warehouseQISettingsController) {
				if (warehouseQISettingsController) {
					warehouseQISettingsController.control(tab);
	            } else {
	            	Gloria.controllerManager.getController('warehouseQISettingsController', this.showWarehouseQISettings, tab);
	            }
			},
			
			showQualityInspectionDetails : function(id, qualityInspectionDetailsController) {
				if (qualityInspectionDetailsController) {
					qualityInspectionDetailsController.control(id);
	            } else {
	            	Gloria.controllerManager.getController('qualityInspectionDetailsController', this.showQualityInspectionDetails, id);
	            }
			},
			
			showWarehouseAdminView : function(tab, warehouseAdminController) {
				if (warehouseAdminController) {
					warehouseAdminController.control(tab);
	            } else {
	            	Gloria.controllerManager.getController('warehouseAdminController', this.showWarehouseAdminView, tab);
	            }
			}
		};
		
		var loadInitialData = function() {
            if(!UserHelper.getInstance().getDefaultWarehouse() && UserHelper.getInstance().getUser() && UserHelper.getInstance().hasPermission('view', ['WarehouseSettings'])) {                   
                UserHelper.getInstance().getUserWarehouses();
            }  
        }; 

        Gloria.addInitializer(function() {
			this.controllerManager = ControllerManager.getInstance();
			new WarehouseApp.Router({
				controller : API
			});			
		});
	});

	return Gloria.WarehouseApp;
});
