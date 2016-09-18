define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'views/mobile/collection/PartBalanceCollection',
        'utils/DateHelper',
        'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, PartBalanceCollection, DateHelper, UserHelper) {

	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var partBalanceCollection = undefined;

		var toMoveView = function(binlocation) {
			Gloria.basicLayout.content.empty();
			require([ 'views/mobile/warehouse/view/move/ToMoveView' ], function(ToMoveView) {
				partBalanceCollection = new PartBalanceCollection( [], {
					queryParams : {
						userId : UserHelper.getInstance().getUserId()
					}
				});
				var toMoveView = new ToMoveView({
					collection : partBalanceCollection
				});
				Gloria.basicLayout.content.show(toMoveView);
			});
		};

		var showMoveView = function(collection) {
			if (!collection || collection.length == 0)
				return;
			var inventoryModel = collection.first();
			Gloria.basicLayout.content.empty();
			require([ 'views/mobile/warehouse/view/move/MoveView' ], function(MoveView) {
				var moveView = new MoveView({
					model : inventoryModel
				});
				Gloria.basicLayout.content.show(moveView);
			});
		};

		var loadInventory = function(data) {
			partBalanceCollection.fetch({
				url : '/warehouse/v1/binlocation/' + data.binLocation + '/partbalance?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse()
						+ '&userId=' + UserHelper.getInstance().getUserId()
						+ '&partNo=' + data.partNumber + '&partAffiliation=' + data.partAffiliation
						+ '&partModification=' + data.partModification + '&partVersion=' + data.partVersion,
						//+ '&partName=' + data.partName,
				reset : true,
				success : function(collection) {
					Gloria.WarehouseApp.trigger('loaded:inventory', collection);
				},
				error : function() {
					Gloria.WarehouseApp.trigger('loaded:inventory');
				}
			});
		};

		var moveInventory = function(partbalanceInfo, selectedPartBalanceModel) {
			if (partbalanceInfo) {
				Backbone.sync('update', selectedPartBalanceModel, {
					url : '/warehouse/v1/partbalance/' + selectedPartBalanceModel.id
							+ '?action=MOVE&quantity=' + partbalanceInfo.moveQuantity
							+ '&binLocationCode=' + partbalanceInfo.newBinLocation
							+ '&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(),
					success : function(model) {
						Gloria.trigger('reloadPage');
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

		Controller.MoveController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.WarehouseApp, 'load:inventory', loadInventory);
				this.listenTo(Gloria.WarehouseApp, 'show:move:inventory', showMoveView);
				this.listenTo(Gloria.WarehouseApp, 'move:inventory', moveInventory);
			},

			control : function(options) {
				Gloria.trigger('change:title', 'Gloria.i18n.warehouse.inventory.toMove.move');
				toMoveView.call(this);
			},

			onDestroy : function() {
				partBalanceCollection = null;
			}
		});
	});

	return Gloria.WarehouseApp.Controller.MoveController;
});
