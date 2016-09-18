define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
        'utils/backbone/GloriaCollection',
        'utils/backbone/GloriaModel',
        'utils/backbone/GloriaPageableCollection',
        'i18next',
        'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Collection, Model, PageableCollection, i18n, UserHelper) {
    
	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
	
	    var warehouseId;
	    var zoneStorageRoomId;
	    var warehouseModel;
	    var storageRoomCollection;
	    var zoneCollection;
	    var aisleCollection;
	    var baySettingCollection;
	    var warehouseSetupView;
	    var zoneInfoView;

	    // Process Warehouse Setup and set Page Layout
	    var processWarehouseSetup = function() {
	        Gloria.basicModalLayout.closeAndReset();
	    	Gloria.basicLayout.content.reset();
	    	Gloria.trigger('showBreadcrumbView', {itemId : warehouseId});
	    	require([ 'views/warehouse/setup/view/common/WarehouseSetupView' ], function(WarehouseSetupView) {
	    		warehouseSetupView = new WarehouseSetupView({
	    			warehouseId : warehouseId
	    		});
				warehouseSetupView.on('show', function() {
					prepareWarehouseSetupInformation();
				}, this);
				Gloria.basicLayout.content.show(warehouseSetupView);
			});
		};

		// Prepare Warehouse Setup basic Information
		var prepareWarehouseSetupInformation = function() {
			var that = this;
			require(['models/WarehouseModel'], function(WarehouseModel) {
				if (warehouseModel) {
					warehouseModel.off('warehouseModel:fetched');
				} else {
					warehouseModel = new WarehouseModel();
				}
				
				warehouseModel.set('id', warehouseId);
				
				warehouseModel.on('warehouseModel:fetched', function() {
					setWarehouseSetupInformationView();
				}, that);	
				warehouseModel.fetch({
					success : function(warehouseModel) {
						warehouseModel.trigger('warehouseModel:fetched', {
							model : warehouseModel
						});
						Gloria.WarehouseApp.trigger('warehouseModel:fetched', warehouseModel);
					}
				});
			});
		};
		
		// Show Warehouse Setup basic Information
		var setWarehouseSetupInformationView = function() {
			require([ 'views/warehouse/setup/view/common/WarehouseInformationView'], function(WarehouseInformationView) {
				var warehouseInformationView = new WarehouseInformationView({
					model : warehouseModel
				});
				warehouseSetupView.warehouseBasicInfo.show(warehouseInformationView);
			});
		};
		
		// Process Storage Room Information and set Page Layout
		var processStorageRoomInformation = function() {
			require(['views/warehouse/setup/view/storageroom/StorageRoomInfoView'], function(StorageRoomInfoView) {
				storageRoomInfoView = new StorageRoomInfoView();
				storageRoomInfoView.on('show', function() {
					prepareStorageRoomInformation(storageRoomInfoView);
				}, this);
				warehouseSetupView.storageInfoContent.show(storageRoomInfoView);
			});
		};
		
		// Prepare Storage Room Information
		var prepareStorageRoomInformation = function(parentView) {
			var that = this;
			require(['collections/StorageRoomCollection'], function(StorageRoomCollection) {
				if (storageRoomCollection) {
					storageRoomCollection.off('storageRoomCollection:fetched');
					storageRoomCollection.off('add');
				} else {
					storageRoomCollection = new StorageRoomCollection([]);
				}
				storageRoomCollection.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms';
				storageRoomCollection.on('storageRoomCollection:fetched', function() {
					showStorageRoomInformation(parentView);					
				}, that);
				
				storageRoomCollection.fetch({
					success : function(collection) {					    
						storageRoomCollection.trigger('storageRoomCollection:fetched', {
							storageRoomCollection : collection
						});
					}
				});
			});
		};
		
		// Show Storage Room Information
		var showStorageRoomInformation = function(parentView) {
			require(['views/warehouse/setup/view/storageroom/StorageRoomButtonsView', 'views/warehouse/setup/view/storageroom/StorageRoomGridView'],
			function(StorageRoomButtonsView, StorageRoomGridView) {
				var storageRoomButtonsView = new StorageRoomButtonsView();
				parentView.controlButtons.show(storageRoomButtonsView);
				var storageRoomGridView = new StorageRoomGridView({
					collection : storageRoomCollection
				});
				parentView.gridModule.show(storageRoomGridView);
			});
		};
		
		// Show Edit StorageRoom Popup
		var showEditStorageRoomForm = function(selectedId) {
			require([ 'views/warehouse/setup/view/storageroom/StorageRoomAddNewEditView'],function(StorageRoomAddNewEditView) {
				var storageRoomAddNewEditView = new StorageRoomAddNewEditView({
					model : storageRoomCollection.get(selectedId),
					addAnother : false
				});
				Gloria.basicModalLayout.content.show(storageRoomAddNewEditView);
				storageRoomAddNewEditView.on('hide', function() {
					Gloria.basicModalLayout.content.reset();
				});
			});
		};
		
		// Show Edit Zone Popup
		var showEditZoneForm = function(selectedId) {
			require([ 'views/warehouse/setup/view/zone/ZoneAddNewEditView'],function(ZoneAddNewEditView) {
				var zoneAddNewEditView = new ZoneAddNewEditView({
				    collection: zoneCollection, 
					model : zoneCollection.get(selectedId),
					addAnother : false
				});
				Gloria.basicModalLayout.content.show(zoneAddNewEditView);
				zoneAddNewEditView.on('hide', function() {
					Gloria.basicModalLayout.content.reset();
				});
			});
		};
		
		var deleteZone = function(selectedId, storageRoomId) {
		    var model = zoneCollection.get(selectedId);
		    model.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' + storageRoomId +'/zones/' + selectedId;
		    model.destroy();
        };
		
		var removeStorageRoom = function(storageRoomIds) {
			var that = this;
			var errors = [];
			var modelsToDelete = _.map(storageRoomIds, function(id) {
				return storageRoomCollection.get(id);
			});

			var deleteXHRs = _.map(modelsToDelete, function(storageRoomModel) {
				return storageRoomModel.destroy({
					url :  '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' + storageRoomModel.id,
					error : function(model) {
						errors.push(model);
					}
				});
			});

			$.when.apply($, deleteXHRs).then(function() {
				processStorageRoomInformation();
			}, function(e) {
				processStorageRoomInformation();
			});
		};

		// Show AddNew StorageRoom Popup
		var showAddStorageRoomForm = function() {
			require([ 'views/warehouse/setup/view/storageroom/StorageRoomAddNewEditView' ], function(StorageRoomAddNewEditView) {
				var storageRoomAddNewEditView = new StorageRoomAddNewEditView({
					addAnother : true
				});
				Gloria.basicModalLayout.content.show(storageRoomAddNewEditView);
				storageRoomAddNewEditView.on('hide', function() {
					Gloria.basicModalLayout.content.reset();
				});
			});
		};
		
		// Show AddNew Zone Popup
		var showAddZoneForm = function() {
			require([ 'views/warehouse/setup/view/zone/ZoneAddNewEditView' ], function(ZoneAddNewEditView) {
				var zoneAddNewEditView = new ZoneAddNewEditView({
				    collection: zoneCollection,
					addAnother : true
				});
				Gloria.basicModalLayout.content.show(zoneAddNewEditView);
				zoneAddNewEditView.on('hide', function() {
					Gloria.basicModalLayout.content.reset();
				});
			});
		};

		// Process Zone Information and set Page Layout
		processZoneInformation = function() {
			require(['views/warehouse/setup/view/zone/ZoneInfoView'], function(ZoneInfoView) {
				zoneInfoView =  new ZoneInfoView();
				zoneInfoView.on('show', function() {
					//prepareZoneInformation(zoneInfoView);
					showZoneButtonGridInformation(zoneInfoView, new PageableCollection());
				}, this);
				warehouseSetupView.zoneInfoContent.show(zoneInfoView);
			});
		};
		
		// Prepare Zone Information
		prepareZoneInformation = function(zoneStorageRoomId) {
			var that = this;
			
			if (zoneCollection) {
				zoneCollection.off('zoneCollection:fetched');
			} else {
				zoneCollection = new Collection();
			}
			
			//Selected StorageRoom is a valid one 
			if (zoneStorageRoomId) {
				zoneCollection.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' + zoneStorageRoomId + '/zones';
				zoneCollection.on('zoneCollection:fetched', function() {
					showZoneGridInformation(zoneInfoView, zoneCollection);
				}, that);
				zoneCollection.fetch({
				    reset: true,
					success : function(collection) {					    
						zoneCollection.trigger('zoneCollection:fetched', {
							zoneCollection : collection
						});
					}
				});
			} else {
			    //Selected StorageRoom option is "Please Select"
				zoneCollection.reset();
				showZoneGridInformation(zoneInfoView, zoneCollection);
			}				
		};
		
		// Show Zone Grid Information
		var showZoneGridInformation = function(parentView, collection) {
			require(['views/warehouse/setup/view/zone/ZoneGridView'],
			function(ZoneGridView) {				
				var zoneGridView = new ZoneGridView({
					collection : collection
				});
				parentView.gridModule.show(zoneGridView);
			});
		};		
		
		// Show Zone Button & Grid Information
		var showZoneButtonGridInformation = function(parentView, collection) {
			require(['views/warehouse/setup/view/zone/ZoneButtonsView', 'views/warehouse/setup/view/zone/ZoneGridView'],
			function(ZoneButtonsView, ZoneGridView) {
				var zoneButtonsView = new ZoneButtonsView();
				zoneButtonsView.on('show', function() {
					showStorageRoomComboBox({
						element : '#zoneStorageRoom',
						warehouseId : warehouseId,
						suggestedStorageRoom : null
					});
				}, this);				
				parentView.controlButtons.show(zoneButtonsView);
				var zoneGridView = new ZoneGridView({
					collection : collection
				});
				parentView.gridModule.show(zoneGridView);
			});
		};
		
		// Process aisle Row Information
		processAisleInformation = function(aisleStorageRoomId, aisleZoneId) {
			
			if(aisleStorageRoomId && aisleZoneId) {
				aisleCollection.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' 
				                      + aisleStorageRoomId + '/zones/' + aisleZoneId + '/aislerackrows';
				aisleCollection.fetch();
				//Remove previous change listener 
				aisleCollection.off('change', null, this);
				aisleCollection.on('change', function(e) {
					// update the ONLY model changed!
					var changedModelId = e.id;
					var model = aisleCollection.get(changedModelId);
					var aisleId = model.get('id');						
					model.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' 
                                + aisleStorageRoomId + '/zones/' + aisleZoneId + '/aislerackrows/' + aisleId;
					model.save({}, {
						wait: true,
						silent: true,
						validationError: function(errorMessage) {
							Gloria.trigger('showAppMessageView', { 
					            type : 'error',
					            title : i18n.t('errormessages:general.title'),
					            message: errorMessage
					        });
						}
					});
				}, this);
			} else {
			    aisleCollection.url = undefined;
				aisleCollection.reset();
			}			
		};
		
		// Prepare Rack Row Information and set Page Layout
		prepareAisleInformation = function() {
			require(['views/warehouse/setup/view/aisle/AisleInfoView'], function(AisleInfoView) {
				aisleInfoView = new AisleInfoView();
				aisleInfoView.on('show', function() {
					aisleCollection = new Collection();
					showAisleInformation(aisleInfoView);
				}, this);
				warehouseSetupView.aisleInfoContent.show(aisleInfoView);
			});
		};
		
		// Show Aisle Row Information
		var showAisleInformation = function(parentView, collection) {
			require(['views/warehouse/setup/view/aisle/AisleButtonsView', 'views/warehouse/setup/view/aisle/AisleGridView'],
			function(AisleButtonsView, AisleGridView) {
				var aisleButtonsView = new AisleButtonsView({
				    warehouseModel: warehouseModel
				});
				aisleButtonsView.on('show', function() {
					showStorageRoomComboBox({
						element : '#aisleStorageRoom',
						warehouseId : warehouseId,
						suggestedStorageRoom : null
					});
					showZoneComboBox({
						element : '#aisleZone',
						warehouseId : warehouseId,
						suggestedStorageRoom : null
					});
				}, this);
				parentView.controlButtons.show(aisleButtonsView);
				aisleGridView = new AisleGridView({
				    warehouseModel: warehouseModel,
					collection : aisleCollection
				});
				parentView.gridModule.show(aisleGridView);
			});
		};
		
		// Process Level and Position Information and set Page Layout
		processLevelInformation = function(levelStorageRoomId,levelZoneId, levelAisleId) {	
				
			if(levelStorageRoomId && levelZoneId && levelAisleId) {
			    baySettingCollection.url = '/warehouse/v1/warehouses/' + warehouseId 
				                    + '/storagerooms/' + levelStorageRoomId + '/zones/' + levelZoneId 
				                    + '/aislerackrows/' + levelAisleId + '/baysettings';
			    baySettingCollection.fetch();
				//Remove previous change listener
			    baySettingCollection.off('change', null, this);
			    baySettingCollection.on('change', function(e) {
					// update the ONLY model changed!
					var changedModelId = e.id;
					var model = baySettingCollection.get(changedModelId);
					var levelId = model.get('id');
					model.url = '/warehouse/v1/warehouses/' + warehouseId 
                                + '/storagerooms/' + levelStorageRoomId + '/zones/' + levelZoneId 
                                + '/aislerackrows/' + levelAisleId + '/baysettings/' + levelId;
					model.save({}, {
						wait: true,
						silent: true,
						validationError: function(errorMessage) {
							Gloria.trigger('showAppMessageView', { 
					            type : 'error',
					            title : i18n.t('errormessages:general.title'),
					            message: errorMessage
					        });
						}
					});
				}, this);
			} else {
			    baySettingCollection.url = undefined;
			    baySettingCollection.reset();
			}
		};
		
		// Prepare Level and Position Information
		prepareLevelInformation = function() {
			require(['views/warehouse/setup/view/level/LevelInfoView'], 
			function(LevelInfoView) {
				levelInfoView = new LevelInfoView();
				levelInfoView.on('show', function() {
				    baySettingCollection = new Collection([], {
				        mode: 'client'
                    });	
					showLevelInformation(levelInfoView);
				}, this);
				warehouseSetupView.levelInfoContent.show(levelInfoView);
			});	
		};
		
		// Show Level and Position Information
		var showLevelInformation = function(parentView, collection ) {			
			require(['views/warehouse/setup/view/level/LevelButtonsView', 'views/warehouse/setup/view/level/LevelGridView'],
				function(LevelButtonsView, LevelGridView) {
					var levelButtonsView = new LevelButtonsView();
					levelButtonsView.on('show', function() {
						showStorageRoomComboBox({
							element : '#levelStorageRoom',
							warehouseId : warehouseId,
							suggestedStorageRoom : null
						});
						showZoneComboBox({
							element : '#levelZone',
							warehouseId : warehouseId,
							suggestedZone : null
						});
						showAiseComboBox({
							element : '#levelAisle',
							warehouseModel: warehouseModel,
							warehouseId : warehouseId,
							suggestedAisle : null
						});
					}, this);
					parentView.controlButtons.show(levelButtonsView);
					levelGridView = new LevelGridView({
						collection : baySettingCollection
					});
					parentView.gridModule.show(levelGridView);
			    }
			);
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
		
		var saveStorageRoom = function(data) {
			require([ 'models/StorageRoomModel' ], function(StorageRoomModel) {
				var model = undefined;			
				if (data.id && data.id != '') {
					model = new StorageRoomModel({
						id : data.id
					});
					model.url ='/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/'+ data.id;	
					model.fetch({
						success : function(resp) {
							resp.save(data, {
								success : function(response) {
									storageRoomCollection.add(response, {
										merge : true
									});
									Gloria.WarehouseApp.trigger('storageRoom:show:saved', {
										status : 'success',
										collection : storageRoomCollection
									});
								},
								error : function(error) {
									Gloria.WarehouseApp.trigger('storageRoom:show:saved', {
										status : 'error',
										collection : storageRoomCollection
									});
								},
								validationError: function(errorMessage) {
									Gloria.trigger('showAppMessageView', { 
							            type : 'error',
							            title : i18n.t('errormessages:general.title'),
							            message: errorMessage
							        });
								}
							});
						}
					});						
				} else {
					model = new StorageRoomModel();
					model.id = undefined;
					data.id = undefined;
					model.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms';
					model.save(data, {
						success : function(response) {
							storageRoomCollection.add(response);
							Gloria.WarehouseApp.trigger('storageRoom:show:saved', {
								status : 'success',
								collection : storageRoomCollection
							});
						},
						error : function(error) {
							Gloria.WarehouseApp.trigger('storageRoom:show:saved', {
								status : 'error',
								collection : storageRoomCollection
							});
						},
						validationError: function(errorMessage) {
							Gloria.trigger('showAppMessageView', { 
					            type : 'error',
					            title : i18n.t('errormessages:general.title'),
					            message: errorMessage
					        });
						}
					});
				}
			});
		};
		
		var validateZoneType = function(zoneId, zoneType) {		    	    
		    if(zoneId && zoneType == zoneCollection.get(zoneId).get('type')) return true;		    
		    
		    if(zoneType.toUpperCase() != "STORAGE" && zoneCollection.findWhere({type: zoneType})){
		        
                Gloria.WarehouseApp.trigger('zone:type:error', {
                    error: 'ZONE_TYPE_IS_EXISTED',
                    errorMessage: 'Gloria.i18n.warehouse.setup.header.zoneAddNewEditViewForm.zoneTypeIsExisted'
                });
                return false;
            }
		    return true;
		};
		
		var saveZone = function(data, zoneStorageRoomId) { 
		    if(!validateZoneType(data.id, data.type)) return;

			var zoneModel = undefined;	
			if (data.id && data.id != '') {
				zoneModel = new Model({
					id : data.id
				});
				zoneModel.url ='/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' + zoneStorageRoomId + '/zones/'+ data.id;		
				zoneModel.fetch({
					success : function(resp) {
						resp.save(data, {
							success : function(response) {
								zoneCollection.add(response, {
									merge : true
								});
								Gloria.WarehouseApp.trigger('zone:show:saved', {									
									collection : zoneCollection
								});
							},
							validationError: function(errorMessage) {
								Gloria.trigger('showAppMessageView', { 
						            type : 'error',
						            title : i18n.t('errormessages:general.title'),
						            message: errorMessage
						        });
							}
						});
					}
				});
			} else {
                data.id = undefined;
				zoneModel = new Model();
				zoneModel.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' + zoneStorageRoomId + '/zones';
				zoneModel.save(data, {
					success : function(response) {
						zoneCollection.add(response);
						Gloria.WarehouseApp.trigger('zone:show:saved', {
							status : 'success',
							collection : zoneCollection
						});
					},
					error : function(error) {
						Gloria.WarehouseApp.trigger('zone:show:saved', {
							status : 'error',
							collection : zoneCollection
						});
					},
					validationError: function(errorMessage) {
						Gloria.trigger('showAppMessageView', { 
				            type : 'error',
				            title : i18n.t('errormessages:general.title'),
				            message: errorMessage
				        });
					}
				});
			}
		};

		var handleLevelStorageRoomChange = function(levelStorageRoomId) {
			showZoneComboBox({
				element : '#levelZone',
				warehouseId : warehouseId,
				storageRoomId : levelStorageRoomId,
				suggestedZone : null
			});
			if(baySettingCollection) {
			    baySettingCollection.reset();
			}
		};
		
		var handleLevelZoneChange = function(levelStorageRoomId, levelZoneId) {
			showAiseComboBox({
				element : '#levelAisle',
				warehouseModel: warehouseModel,
				warehouseId : warehouseId,
				storageRoomId : levelStorageRoomId,
				zoneId : levelZoneId,
				suggestedAisle : null
			});
			if(baySettingCollection) {
			    baySettingCollection.reset();
			}
		};
		
		var handleAisleStorageRoomChange = function(aisleStorageRoomId) {
			showZoneComboBox({
				element : '#aisleZone',
				warehouseId : warehouseId,
				storageRoomId : aisleStorageRoomId,
				suggestedZone : null
			});
			if(aisleCollection) {
				aisleCollection.reset();
			}
		};

		var addAisleRow = function(aisleStorageRoomId, aisleZoneId) {
			var aisleModel = new Model();
			aisleModel.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' 
                        + aisleStorageRoomId + '/zones/' + aisleZoneId + '/aislerackrows';
			aisleModel.save({}, {
				success : function(response) {
					aisleCollection.add(response);
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
		
		var deleteAisleRow = function(selectedId, storageRoomId, zoneId) {		    
		    var model = aisleCollection.get(selectedId);
            model.url = '/warehouse/v1/warehouses/' + warehouseId + '/storagerooms/' + storageRoomId +'/zones/' + zoneId + '/aislerackrows/' + selectedId;
            model.destroy();
		};
		
		var addBaySetting = function(storageRoomId, zoneId, aisleId) {
            var baySettingModel = new Model();
            baySettingModel.url = '/warehouse/v1/warehouses/' + warehouseId 
                        + '/storagerooms/' + storageRoomId + '/zones/' + zoneId 
                        + '/aislerackrows/' + aisleId + '/baysettings';
            baySettingModel.save({}, {
                success : function(response) {
                    baySettingCollection.add(response);
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
        
        var deleteBaySetting = function(selectedId, storageRoomId, zoneId, aisleId) {
            var model = baySettingCollection.get(selectedId);
            model.url = '/warehouse/v1/warehouses/' + warehouseId 
                        + '/storagerooms/' + storageRoomId +'/zones/' + zoneId 
                        + '/aislerackrows/' + aisleId + '/baysettings/' + selectedId;
            model.destroy();
        };
        
        var saveWarehouse = function(printBarcode) {
            warehouseModel.save({}, {   
                url : '/warehouse/v1/warehouses/' + warehouseId 
                + '?action=generateBinLocations&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse() + '&printBarcodes=' + printBarcode ,
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
                        message : errorMessage
                    });
                }
            });
        };

		Controller.WarehouseSetupController = Marionette.Controller.extend({
	       
	    	initialize : function() {
	            this.initializeListeners();
	        },
	        
	        initializeListeners : function() {
	            this.listenTo(Gloria.WarehouseApp, 'storageInformation:showaccordion', processStorageRoomInformation);
	        	this.listenTo(Gloria.WarehouseApp, 'zoneInformation:showaccordion', processZoneInformation);
	        	this.listenTo(Gloria.WarehouseApp, 'aisleInformation:showaccordion', prepareAisleInformation);
	        	this.listenTo(Gloria.WarehouseApp, 'levelInformation:showaccordion', prepareLevelInformation);
	        	this.listenTo(Gloria.WarehouseApp, 'zone:show:add', showAddZoneForm); 
	        	this.listenTo(Gloria.WarehouseApp, 'zone:show:edit', showEditZoneForm); 
	        	this.listenTo(Gloria.WarehouseApp, 'zone:delete', deleteZone);
	        	this.listenTo(Gloria.WarehouseApp, 'storageRoom:show:add', showAddStorageRoomForm); 
	        	this.listenTo(Gloria.WarehouseApp, 'storageRoom:show:edit', showEditStorageRoomForm); 	
	        	this.listenTo(Gloria.WarehouseApp, 'storageRoom:show:remove', removeStorageRoom); 
	        	this.listenTo(Gloria.WarehouseApp, 'StorageRoom:change', prepareZoneInformation);
	        	this.listenTo(Gloria.WarehouseApp, 'storageRoom:show:save', saveStorageRoom);
				this.listenTo(Gloria.WarehouseApp, 'zone:show:save', saveZone);							
				this.listenTo(Gloria.WarehouseApp, 'AisleStorageRoom:change', handleAisleStorageRoomChange);
				this.listenTo(Gloria.WarehouseApp, 'AisleZone:change', processAisleInformation);
				this.listenTo(Gloria.WarehouseApp, 'aisleRow:add', addAisleRow);
				this.listenTo(Gloria.WarehouseApp, 'aisleRow:delete', deleteAisleRow);
				this.listenTo(Gloria.WarehouseApp, 'LevelStorageRoom:change', handleLevelStorageRoomChange);
				this.listenTo(Gloria.WarehouseApp, 'LevelZone:change', handleLevelZoneChange);
				this.listenTo(Gloria.WarehouseApp, 'LevelAisle:change', processLevelInformation);
				this.listenTo(Gloria.WarehouseApp, 'baySetting:add', addBaySetting);
                this.listenTo(Gloria.WarehouseApp, 'baySetting:delete', deleteBaySetting);
                this.listenTo(Gloria.WarehouseApp, 'warehouse:save', saveWarehouse);
	        },
	
	        control: function(whId) {
	        	if(whId) {
	        		warehouseId = whId;
	        		processWarehouseSetup.call(this);
	        	} else {
	        		throw new TypeError('Warehouse id must be supplied');
	        	}
	        },
	        
	        onDestroy : function() {
	            warehouseId = null;
	            zoneStorageRoomId = null;
	            warehouseModel = null;
	            storageRoomCollection = null;
	            zoneCollection = null;
	            aisleCollection = null;
	            baySettingCollection = null;
	            warehouseSetupView = null;
	            zoneInfoView = null;
	        }
	    });
	});
	
	return Gloria.WarehouseApp.Controller.WarehouseSetupController;
});
