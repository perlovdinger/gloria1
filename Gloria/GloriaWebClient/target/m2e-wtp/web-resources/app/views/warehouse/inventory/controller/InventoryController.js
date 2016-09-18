define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'utils/UserHelper',
        'utils/DateHelper',        
        'collections/PartBalanceCollection',        
        'models/PartBalanceModel',   
        'collections/WarehouseCollection',
        'utils/backbone/GloriaCollection',
        'utils/backbone/GloriaModel'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, DateHelper, PartBalanceCollection, PartBalanceModel, WarehouseCollection, Collection, GloriaModel) {
    
    Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
    	
    	var partBalanceCollection;
    	var partBalanceModel;
    	var warehouseCollection;
    	var inventoryView;
    	var warehouseId;
    	var toMoveMaterialLines;
        
    	/**
		 * Prepare Inventory View
		 */ 
    	var prepareInventoryView = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			partBalanceCollection = new PartBalanceCollection([], {	
				queryParams : {
                    userId : UserHelper.getInstance().getUserId()
                },
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.InventoryGrid'
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : 'InventoryGrid'
			});
			warehouseCollection = new WarehouseCollection();
		};
    	
		/**
		 * Set Page Layout.
		 */ 
		var setPageLayout = function() {
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView');
			require(['views/warehouse/inventory/view/InventoryView'], function(InventoryView) {
				inventoryView = new InventoryView();
				inventoryView.on('show', function() {
					showInventoryModuleView();
					showInventoryGridView();
				}, this);
				Gloria.basicLayout.content.show(inventoryView);
			});
		};
		
		/**
		 * Show Inventory Action Buttons
		 */ 
		var showInventoryModuleView = function() {
			require([ 'views/warehouse/common/WarehouseModuleView'], function(WarehouseModuleView) {
				var warehouseModuleView = new WarehouseModuleView({
					module : 'inventory',
					control : 'inventory'
				});
				inventoryView.moduleInfo.show(warehouseModuleView);
			});
		};
		
		/**
		 * Show Inventory Grid Info
		 */ 
		var showInventoryGridView = function() {
			require(['views/warehouse/inventory/view/InventoryGridView'], function(InventoryGridView) {
				var inventoryGridView = new InventoryGridView({
					collection : partBalanceCollection
				});
				inventoryGridView.on('show', function() {
					processInventoryGridInfo();
				}, this);
				inventoryView.gridInfo.show(inventoryGridView);
			});
		};

		/**
		 * Process Inventory Grid Info
		 */
		var processInventoryGridInfo = function() {
			partBalanceCollection.fetch();		    
		};		
        
	
    	/**
		 * Show AdjustQty Modal
		 */
		var showAdjustQtyModal = function(data) {
			partBalanceModel= new PartBalanceModel();
			partBalanceModel = data;
			require(['views/warehouse/inventory/view/AdjustQtyStockBalanceView'], function(AdjustQtyStockBalanceView) {
				var adjustQtyStockBalanceView = new AdjustQtyStockBalanceView({
					model : data
				});
				Gloria.basicModalLayout.content.show(adjustQtyStockBalanceView);
			});
		};
		
		/**
		 * Show Move Part Modal
		 */
		var showMovePartModal = function(data) {
			require(['views/warehouse/inventory/view/MovePartModalView'], function(MovePartModalView) {
				var movePartModalView = new MovePartModalView({
					model : data
				});
				Gloria.basicModalLayout.content.show(movePartModalView);
			});
		};
		
		var moveInventory = function(partbalanceInfo, selectedPartBalanceModel) {			
		    Backbone.sync('update', selectedPartBalanceModel, {
                url : '/warehouse/v1/partbalance/'+selectedPartBalanceModel.id+ '?action=MOVE&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse() + '&quantity=' + partbalanceInfo.quantity + '&binLocationCode=' + partbalanceInfo.binLocationCode,
                success : function(model) {
                    Gloria.WarehouseApp.trigger('Inventory:moved', true);                  
                },
                error : function() {
                    Gloria.WarehouseApp.trigger('Inventory:moved', false);                  
                }
            });
        };
		
        var adjustInventory = function(data, comments) {
			partBalanceModel.attributes.quantity = data.quantity;
			var partBalanceModelId = partBalanceModel.id;
            var userId = UserHelper.getInstance().getUserId();
            
		    Backbone.sync('update', partBalanceModel, {
                url : '/warehouse/v1/partbalance/'+partBalanceModelId+'?userId='+userId+'&comments=' +comments,
                success : function(response) {
                    Gloria.WarehouseApp.trigger('Inventory:adjusted', true);                  
                },
                error : function() {
                    Gloria.WarehouseApp.trigger('Inventory:adjusted', false);                  
                }
            });
        };
		
		var showStorageRoomComboBox = function(options) {
			require(['views/warehouse/setup/view/common/StorageRoomSelector'], function(StorageRoomSelector) {
				new StorageRoomSelector(options);
			});
		};
		
		var showZoneComboBox = function(options) {
			require(['views/warehouse/setup/view/common/ZoneSelector'], function(ZoneSelector) {
				new ZoneSelector(options);
			});
		};
		
		var showAiseComboBox = function(options) {
			require(['views/warehouse/setup/view/common/AisleSelector'], function(AisleSelector) {
				new AisleSelector(options);
			});
		};
		
		var showBayComboBox = function(options) {
			require(['views/warehouse/inventory/helper/BaySelector'], function(BaySelector) {
				new BaySelector(options);
			});
		};
		
		var showLevelComboBox = function(options) {
			require(['views/warehouse/inventory/helper/LevelSelector'], function(LevelSelector) {
				new LevelSelector(options);
			});
		};
		
		var showPositionComboBox = function(options) {
			require(['views/warehouse/inventory/helper/PositionSelector'], function(PositionSelector) {
				new PositionSelector(options);
			});
		};
		
		var handleStorageRoomChange = function(storageRoomId) {
			showZoneComboBox({
				element : '#zone',
				warehouseId : warehouseId,
				storageRoomId : storageRoomId
			});
			showAiseComboBox({
				element : '#aisle',
				warehouseModel: warehouseCollection.first()
			});
			showBayComboBox({
				element : '#bay'
			});
			showLevelComboBox({
				element : '#level'
			});
			showPositionComboBox({
				element : '#position'
			});
		};
		
		var handleZoneChange = function(storageRoomId, zoneId) {
			showAiseComboBox({
				element : '#aisle',
				warehouseModel: warehouseCollection.first(),
				warehouseId : warehouseId,
				storageRoomId : storageRoomId,
				zoneId : zoneId
			});
			showBayComboBox({
				element : '#bay'
			});
			showLevelComboBox({
				element : '#level'
			});
			showPositionComboBox({
				element : '#position'
			});
		};
		
		var handleAisleChange = function(storageRoomId, zoneId, aisleId) {
			showBayComboBox({
				element : '#bay',
				warehouseId : warehouseId,
				storageRoomId : storageRoomId,
				zoneId : zoneId,
				aisleId : aisleId
			});
			showLevelComboBox({
				element : '#level'
			});
			showPositionComboBox({
				element : '#position'
			});
		};
		
		var handleBayChange = function(storageRoomId, zoneId, aisleId, bayId) {
			showLevelComboBox({
				element : '#level',
				warehouseId : warehouseId,
				storageRoomId : storageRoomId,
				zoneId : zoneId,
				aisleId : aisleId,
				bayId : bayId
			});
			showPositionComboBox({
				element : '#position'
			});
		};
		
		var handleLevelChange = function(storageRoomId, zoneId, aisleId, bayId, levelId) {
			showPositionComboBox({
				element : '#position',
				warehouseId : warehouseId,
				storageRoomId : storageRoomId,
				zoneId : zoneId,
				aisleId : aisleId,
				bayId : bayId,
				levelId : levelId
			});
		};
		
		var exportInventory = function() {
			window.location.href = '/GloriaUIServices/api/warehouse/v1/partbalance/excel?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
		};
		
		var preparePartLabelPrint = function(models) {
			require(['views/warehouse/inventory/view/PrintPLModalView'], function(PrintPLModalView) {
				var printPLModalView = new PrintPLModalView({
					models : models
				});
				Gloria.basicModalLayout.content.show(printPLModalView);
			});
		};
		
		var printPartLabel = function(models, printQty) {
			var collection = new Collection(models);
			collection.save({
				url : '/common/v1/partbalance/partlabels?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse() + (printQty ? ('&quantity=' + printQty) : ''),
        		success : function(response) {
        			console.log('Printed!');
        		},
        		validationError : function(errorMessage, errors) {
					var errorMessage = new Array();
					var item = {
							message : i18n.t('Gloria.i18n.errors.GLO_ERR_69')
						};
					errorMessage.push(item);
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : errorMessage
		    		});
                }
        	});
		};
		
		var prepareBinLocationPrint = function() {
			require(['views/warehouse/inventory/view/PrintBLModalView'], function(PrintBLModalView) {
				var printBLModalView = new PrintBLModalView();
				printBLModalView.on('show', function() {
					showZoneComboBox({
						element : '#zone',
						url : '/warehouse/v1/warehouses/' + UserHelper.getInstance().getDefaultWarehouse() + '/zones',
						suggestedZone : null
					});
					showAiseComboBox({
						element : '#aisle'
					});
					showBayComboBox({
						element : '#bay'
					});
					showLevelComboBox({
						element : '#level'
					});
				}, this);
				Gloria.basicModalLayout.content.show(printBLModalView);
			});
		};
		
		var handlePrintBLZoneChange = function(zoneId) {
			showAiseComboBox({
				element : '#aisle',
				url : '/warehouse/v1/warehouses/' + UserHelper.getInstance().getDefaultWarehouse() + '/zones/' + zoneId + '/aislerackrows',
				zoneId : zoneId
			});
			showBayComboBox({
				element : '#bay'
			});
			showLevelComboBox({
				element : '#level'
			});
		};
		
		var handlePrintBLAisleChange = function(aisleId) {
			showBayComboBox({
				element : '#bay',
				url : '/warehouse/v1/warehouses/' + UserHelper.getInstance().getDefaultWarehouse() + '/aislerackrows/' + aisleId + '/baysettings',
				aisleId : aisleId
			});
			showLevelComboBox({
				element : '#level'
			});
		};
		
		var handlePrintBLBayChange = function(bayId) {
			showLevelComboBox({
				element : '#level',
				url : '/warehouse/v1/warehouses/' + UserHelper.getInstance().getDefaultWarehouse() + '/baysettings/' + bayId + '/levels',
				bayId : bayId
			});
		};
		
		var printBinLocationLabel = function(queryParam) {
			var printCollection = new PartBalanceCollection([], {
				queryParams : queryParam,
				mode: 'client'
			});
			printCollection.fetch({
				url : '/warehouse/v1/binlocations/printlabels?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(),
				success : function(data) {

				},
				validationError : function(errorMessage, errors) {
					var errorMessage = new Array();
					var item = {
							message : i18n.t('Gloria.i18n.errors.GLO_ERR_69')
						};
					errorMessage.push(item);
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : errorMessage
		    		});
                }
			});
		};
		
        Controller.InventoryController = Marionette.Controller.extend({
            
            initialize : function() {
                this.initializeListeners();
            },
            
            initializeListeners : function() {
            	this.listenTo(Gloria.WarehouseApp, 'Inventory:MovePartModal:show', showMovePartModal);
            	this.listenTo(Gloria.WarehouseApp, 'Inventory:AdjustQtyModal:show', showAdjustQtyModal);            	
            	this.listenTo(Gloria.WarehouseApp, 'Inventory:StorageRoom:change', handleStorageRoomChange);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:Zone:change', handleZoneChange);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:Aisle:change', handleAisleChange);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:Bay:change', handleBayChange);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:Level:change', handleLevelChange);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:export', exportInventory);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:move', moveInventory);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:adjust', adjustInventory);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:printPL', preparePartLabelPrint);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:PL:print', printPartLabel);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:printBL', prepareBinLocationPrint);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:PrintBL:Zone:change', handlePrintBLZoneChange);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:PrintBL:Aisle:change', handlePrintBLAisleChange);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:PrintBL:Bay:change', handlePrintBLBayChange);
				this.listenTo(Gloria.WarehouseApp, 'Inventory:BL:print', printBinLocationLabel);
            },
            
            control : function() {
            	prepareInventoryView.call(this);
            },
            
            onDestroy : function() {
                partBalanceCollection = null;
                partBalanceModel = null;
                warehouseCollection = null;
                inventoryView = null;
                warehouseId = null;
            	toMoveMaterialLines = null;
            }
        });    
    });
    
    return Gloria.WarehouseApp.Controller.InventoryController;
});
