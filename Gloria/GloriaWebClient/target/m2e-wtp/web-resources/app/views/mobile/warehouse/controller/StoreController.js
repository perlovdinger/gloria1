define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'i18next',
		'utils/UserHelper',
		'utils/backbone/GloriaCollection',
		'utils/backbone/GloriaModel'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, GloriaCollection, GloriaModel) {
	
	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var materialLineCollection = undefined;
		var materialLine = undefined;
		var searchMaterialLinesData = undefined;

		/**
		 * Route : warehouse/store
		 * Prepare Store Module
		 */
		var prepareStoreView = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};

		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			if (!materialLineCollection) {
				materialLineCollection = new GloriaCollection();
			}
		};

		/**
		 * Set Page Layout
		 */
		var setPageLayout = function() {
			require([ 'views/mobile/warehouse/view/store/StoreView' ], function(StoreView) {
				var storeView = new StoreView();
				Gloria.basicLayout.content.show(storeView);
			});
		};

		/**
		 * Search Material Lines by part number / transport label
		 */
		var searchMaterialLines = function(data, afterStore) {
			if(afterStore) {
				data = searchMaterialLinesData;
			} else {
				searchMaterialLinesData = data;
			}
			
			materialLineCollection.url = '/warehouse/v1/materiallines?status=READY_TO_STORE&whSiteId='
					+ UserHelper.getInstance().getDefaultWarehouse()
				    + '&transportLabel=' + data.transportLabel
					+ '&calculateStockBalance=true&suggestBinLocation=true'
					+ '&partAffiliation=' + data.partAffiliation + '&partNo=' + data.partNumber
					+ '&partModification=' + data.partModification + '&partVersion=' + data.partVersion;
					//+ '&partName=' + data.partName;
			materialLineCollection.fetch({
				success : function(collection) {
					if (collection.length > 0) {
						showToStoreGridView();
					} else if(afterStore) {
						Backbone.history.loadUrl('warehouse/store');
					} else if (collection.length > 0 && (collection.models.length == 1)) {
						prepareToStoreView(collection.models[0]);
					} else {
						Gloria.WarehouseApp.trigger('Store:materialline:notfound');
					}
				}
			});
		};

		/**
		 * Show To Store Grid View
		 */
		var showToStoreGridView = function() {
			require([ 'views/mobile/warehouse/view/store/ToStoreLayout', 'views/mobile/warehouse/view/store/ToStoreGridView' ],
			function(ToStoreView, ToStoreGridView) {
				var toStoreView = new ToStoreView();
				toStoreView.on('show', function() {
					var toStoreGridView = new ToStoreGridView({
						collection : materialLineCollection
					});
					toStoreView.grid.show(toStoreGridView);
				});
				Gloria.basicLayout.content.show(toStoreView);
			});
		};
		
		/**
		 * Prepare To Store View
		 */
		var prepareToStoreView = function(model) {
			materialLine = model;
			lockMaterialLine(model, showToStoreView);
		};

		/**
		 * Show To Store View
		 */
		var showToStoreView = function(model) {
			require([ 'views/mobile/warehouse/view/store/ToStoreView' ], function(ToStoreView) {
				var toStoreView = new ToStoreView({
					model : model,
					whSiteId : UserHelper.getInstance().getDefaultWarehouse()
				});
				Gloria.basicLayout.content.show(toStoreView);
			});
		};
		
		/**
		 * Lock Material Line
		 */
		var lockMaterialLine = function(materialLine, callback) {
			materialLine.save(null, {
				url : '/warehouse/v1/materiallines/' + materialLine.id +'/lock?userId='
				+ UserHelper.getInstance().getUserId()+ '&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(),
				success : function() {
					materialLine.isLocked = true;
					callback && callback(materialLine);
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : errorMessage
		    		});
				}
			});
		};
		
		/**
		 * Unlock Material Line
		 */
		var unlockMaterialLine = function(materialLine, whSiteId) {
			materialLine.save(null, {
				url : '/warehouse/v1/materiallines/' + materialLine.id +'/unlock?userId='
				+ UserHelper.getInstance().getUserId()+ '&whSiteId=' + whSiteId,
				success : function() {
					materialLine.isLocked = false;
				}
			});
		};
		
		/**
		 * Save To Store Information
		 */
		var saveToStoreInfo = function(data, materialModel) {
			var binLocationModel = new GloriaModel();
			binLocationModel.fetch({
				url : '/warehouse/v1/binlocations/' + data.suggestedBinLocation,
				data : {
					whSiteId : UserHelper.getInstance().getDefaultWarehouse(),
					zoneType : 'STORAGE'
				},
				success : function(model) {
					if (model && model.id) {
						materialLineCollection.each(function(thisModel) {
							if (thisModel.id == materialModel.id) {
								thisModel.set('binlocation', model.id);
								thisModel.set('storedQuantity', data.quantity);
							}
						});
						materialLineCollection.url = '/procurement/v1/materiallines?action=store&whSiteId='
							+ UserHelper.getInstance().getDefaultWarehouse();
						Backbone.sync('update', materialLineCollection, {
							success : function(model, resp, xhr) {
								Gloria.WarehouseApp.trigger('Store:material:search', null, true);
							}
						});
					} else {
						Gloria.WarehouseApp.trigger('Store:BinLocation:invaid');
					}
				},
				validationError : function() {
					Gloria.WarehouseApp.trigger('Store:BinLocation:invaid');
				}
			});
		};

		/**
		 * Show Bin Location View
		 */
		var showBinLocationView = function() {
			require([ 'views/mobile/warehouse/view/store/BinLocationView' ], function(BinLocationView) {
				var binLocationView = new BinLocationView();
				Gloria.basicLayout.content.show(binLocationView);
			});
		};

		/**
		 * Save Bin Location Information
		 */
		var saveAllInSameBinLocation = function(data) {
			var binLocationModel = new GloriaModel();
			binLocationModel.fetch({
				url : '/warehouse/v1/binlocations/' + data.suggestedBinLocation,
				data : {
					whSiteId : UserHelper.getInstance().getDefaultWarehouse(),
					zoneType : 'STORAGE'
				},
				success : function(model) {
					if (model && model.id) {
						if (materialLineCollection) {
							materialLineCollection.url = '/procurement/v1/materiallines?action=store&whSiteId='
									+ UserHelper.getInstance().getDefaultWarehouse();
							materialLineCollection.each(function(thisModel) {
								thisModel.set({
									binlocation : model.id,
									binlocationCode : model.code
								});
								thisModel.set('storedQuantity', thisModel.get('quantity'));
							});
							materialLineCollection.save({
								success : function(storedMaterialLines) {									
									Backbone.history.loadUrl('warehouse/store');
								},
								validationError : function(e) {
									Gloria.WarehouseApp.trigger('Store:BinLocation:invaid');
								}
							});
						}
					} else {
						Gloria.WarehouseApp.trigger('Store:BinLocation:invaid');
					}
				},
				validationError : function() {
					Gloria.WarehouseApp.trigger('Store:BinLocation:invaid');
				}
			});
		};

		Controller.StoreController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.WarehouseApp, 'Store:material:search', searchMaterialLines);
				this.listenTo(Gloria.WarehouseApp, 'Store:ToStoreGridView:show', showToStoreGridView);
				this.listenTo(Gloria.WarehouseApp, 'Store:ToStoreView:show', prepareToStoreView);
				this.listenTo(Gloria.WarehouseApp, 'Store:ToStoreView:save', saveToStoreInfo);
				this.listenTo(Gloria.WarehouseApp, 'Store:BinLocationView:show', showBinLocationView);
				this.listenTo(Gloria.WarehouseApp, 'Store:BinLocationView:save', saveAllInSameBinLocation);
				this.listenTo(Gloria.WarehouseApp, 'Store:ToStoreView:unlock', unlockMaterialLine);
			},

			control : function(options) {
				options || (options = {});
				Gloria.trigger('change:title', 'Gloria.i18n.warehouse.store.store');
				prepareStoreView.call(this);
			},

			onDestroy : function() {
				if(materialLine && materialLine.isLocked) { // Unlocking resource which might have been locked!
					unlockMaterialLine(materialLine, UserHelper.getInstance().getDefaultWarehouse());
				}
				materialLineCollection = null;
				materialLine = null;
				searchMaterialLinesData = null;
			}
		});
	});

	return Gloria.WarehouseApp.Controller.StoreController;
});
