define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'utils/marionette/GloriaRouter',
		'controllers/controllerManager'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaRouter, ControllerManager) {

	Gloria.module('WarehouseApp', function(WarehouseApp, Gloria, Backbone, Marionette, $, _) {

		WarehouseApp.Router = GloriaRouter.extend({
			appRoutes : {
				'warehouse/receive/:module' : 'showReceiveView',
				'warehouse/receive/:module/:deliveryNoteId' : 'showToReceiveView',
				'warehouse/store' : 'showStoreView',
				'warehouse/pick' : 'showPickView',
				'warehouse/pick/:pickListId' : 'showPickListView',
				'warehouse/move' : 'showMoveView'
			}
		});

		var API = {

			showReceiveView : function(module, receiveController) {
				if (receiveController) {
					receiveController.control({
						page : module
					});
				} else {
					Gloria.controllerManager.getController('receiveController', this.showReceiveView, module);
				}
			},

			showToReceiveView : function(module, deliveryNoteId, receiveController) {
				if (receiveController) {
					receiveController.control({
						module : module,
						deliveryNoteId : deliveryNoteId
					});
				} else {
					Gloria.controllerManager.getController('receiveController', this.showToReceiveView, module, deliveryNoteId);
				}
			},
						
			showStoreView : function(storeController) {
				if (storeController) {
					storeController.control();
				} else {
					Gloria.controllerManager.getController('storeController', this.showStoreView);
				}
			},

			showPickView : function(pickController) {
				if (pickController) {
					pickController.control();
				} else {
					Gloria.controllerManager.getController('pickController', this.showPickView);
				}
			},

			showPickListView : function(pickListId, pickController) {
				if (pickController) {
					pickController.control({
						pickListId : pickListId
					});
				} else {
					Gloria.controllerManager.getController('pickController', this.showPickListView, pickListId);
				}
			},

			showMoveView : function(moveController) {
				if (moveController) {
					moveController.control();
				} else {
					Gloria.controllerManager.getController('moveController', this.showMoveView);
				}
			}

		};

		var initializeListeners = function() {

		};

		Gloria.addInitializer(function() {
			initializeListeners();
			this.controllerManager = ControllerManager.getInstance();
			new WarehouseApp.Router({
				controller : API
			});
		});

	});

	return Gloria.WarehouseApp;
});
