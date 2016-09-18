define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'utils/backbone/GloriaPageableCollection',
        'utils/backbone/GloriaModel',
        'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaPageableCollection, GloriaModel, UserHelper) {

	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var module;
		var qiSettingView;
		var qiSettingsButtonView;
		var qiSettingsGridView;
		var qiSettingsCollection;
		var whSiteId;
		
		/**
		 * Prepare QI Settings View
		 */
		var prepareQISettingsView = function() {
			require([ 'collections/WarehouseCollection' ], function(WarehouseCollection) {
		    	var isSiteWarehouseMapped = false;
				var warehouseCollection = new WarehouseCollection();
				warehouseCollection.url = '/warehouse/v1/users/' + UserHelper.getInstance().getUserId() + '/warehouses';
				warehouseCollection.fetch({
					async : false,
					success : function(collection) {
						collection.each(function(model) {
							if (model.get('siteId') == UserHelper.getInstance().getDefaultWarehouse()) {
								isSiteWarehouseMapped = true;
								localStorage.setItem('Gloria.User.DefaultWarehouse.QISupport', (model.get('qiSupported')));
							}
						});
						if(!isSiteWarehouseMapped) {
							localStorage.setItem('Gloria.User.DefaultWarehouse.QISupport', false);
						}
						var isWHQISupport =  localStorage.getItem('Gloria.User.DefaultWarehouse.QISupport');
						if(isWHQISupport != "true") {
							Backbone.history.navigate('#', {
							    trigger: true
							});
				    	} else {
				    		// Initialize Data Source Objects
							initializeDataSourceObjects();
							// Set Page Layout
							setPageLayout();
				    	}
					}
				});
			});
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			qiSettingsCollection = new GloriaPageableCollection([], {
				state : {
					pageSize : function() {	// Check if any pageSize is already stored
						var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.QISettings' + module
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				}
			});
			whSiteId = UserHelper.getInstance().getDefaultWarehouse();
		};
    	
		/**
		 * Set Page Layout
		 */ 
		var setPageLayout = function() {
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', {itemId : module}); // Skipping module as part of Breadcrumb
			require(['views/warehouse/qisettings/view/QISettingsView'], function(QISettingView) {
				qiSettingView = new QISettingView({
					module : module
				});
				qiSettingView.on('show', function() {
					showQISettingModuleView();
					showQISettingContentView();
				}, this);
				Gloria.basicLayout.content.show(qiSettingView);
			});
		};
		
		/**
		 * Show QI Settings Module View
		 */
		var showQISettingModuleView = function() {
			require([ 'views/warehouse/common/WarehouseModuleView'], function(WarehouseModuleView) {
				var warehouseModuleView = new WarehouseModuleView({
					module : 'qisettings'
				});
				qiSettingView.moduleInfo.show(warehouseModuleView);
			});
		};
		
		/**
		 * Show QI Settings Content View
		 */
		var showQISettingContentView = function() {
			showQISettingsButtonView(module);
			showQISettingsGridView(module);
		};
		
		/**
		 * Show QI Settings Common Button View
		 */
		var showQISettingsButtonView = function(module) {
			require(['views/warehouse/qisettings/view/QISettingsButtonView'], function(QISettingsButtonView) {
				qiSettingsButtonView = new QISettingsButtonView({
					module : module
				});
				if (qiSettingView.buttonDiv) {
					qiSettingView.buttonDiv.empty();
				}
				var buttonId = '#' + module + 'Button';
				qiSettingView.addRegion('buttonDiv', buttonId);
				qiSettingView.buttonDiv.show(qiSettingsButtonView);
			});
		};
		
		/**
		 * Show QI Settings Common Grid View
		 * @Param module
		 */
		var showQISettingsGridView = function(module) {
			require(['views/warehouse/qisettings/view/QISettingsGridView'], function(QISettingsGridView) {
				qiSettingsGridView = new QISettingsGridView({
					module : module,
					collection : qiSettingsCollection
				});
				qiSettingsGridView.on('show', function() {
					processQISettingsGridInfo(module);
				}, this);
				if(qiSettingView.gridDiv) {
					qiSettingView.gridDiv.empty();
				}
				var gridId = '#' + module + 'Grid';
				qiSettingView.addRegion('gridDiv', gridId);
				qiSettingView.gridDiv.show(qiSettingsGridView);
			});
		};
		
		/**
		 * Process QI Settings Grid Information
		 * @Param module
		 */
		var processQISettingsGridInfo = function(module) {
			switch (module) {
			case 'project':
				qiSettingsCollection.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionprojects';
				break;
			case 'supplier':
				qiSettingsCollection.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionsuppliers';
				break;
			default:
				qiSettingsCollection.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionparts';
				break;
			}
			qiSettingsCollection.fetch();
		};
		
		/**
		 * Show Part View
		 * @Param modelData
		 */
		var showPartInfo = function(modelData) {
			require(['views/warehouse/qisettings/view/AddEditPartView'], function(AddEditPartView) {
				var addEditPartView = new AddEditPartView({
					model : modelData,
					collection : qiSettingsCollection
				});
				Gloria.basicModalLayout.content.show(addEditPartView);
			});
		};
		
		/**
		 * Remove Part Information
		 * @Param modelData
		 */
		var removePartInfo = function(modelData) {
			if (modelData) { // DELETE
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionparts/' + modelData.id;
				modelData.destroy({
					success : function() {
						Gloria.WarehouseApp.trigger('QISettingsGrid:clearSelectedModels');
						Gloria.WarehouseApp.trigger('QISettingsButton:ResetButtons');
	                    // Refetch qiSettingsCollection and refresh the Grid
	                    qiSettingsCollection.fetch();
					}
				});
			}
		};
		
		/**
		 * Save Part Information
		 * @Param modelData
		 * @Param formData
		 */
		var savePartInfo = function(modelData, formData) {
			var saveInfo = function(model, data) {
				model.save(formData, {
					success : function(response) {
						Gloria.WarehouseApp.trigger('QISettingsModal:part:saved', true, response);
						Gloria.WarehouseApp.trigger('QISettingsGrid:clearSelectedModels');
						Gloria.WarehouseApp.trigger('QISettingsButton:ResetButtons');
	                    // Refetch qiSettingsCollection and refresh the Grid
	                    qiSettingsCollection.fetch();
					},
					validationError : function(errorMessage, errors) {
						Gloria.WarehouseApp.trigger('QISettingsModal:part:saved', false, errorMessage);
					},
					error : function(error, errorMessage) {
						if(errorMessage.status != 400) {//@Important: handle all errors except validation error
														//as we have defined validationError to handle such (400) case
							Gloria.WarehouseApp.trigger('QISettingsModal:part:saved', false, error);
						}
                    }
				});
			};			
			if (modelData) { // PUT
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionparts/' + modelData.id;
				saveInfo(modelData, formData);
			} else { // POST
				var modelData = new GloriaModel();
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionparts';
				formData.id = null;
				saveInfo(modelData, formData);
			}
		};

		/**
		 * Show Project Information
		 * @Param modelData
		 */
		var showProjectInfo = function(modelData) {
			require(['views/warehouse/qisettings/view/AddEditProjectView'], function(AddEditProjectView) {
				var addEditProjectView = new AddEditProjectView({
					model : modelData,
					collection : qiSettingsCollection
				});
				Gloria.basicModalLayout.content.show(addEditProjectView);
			});
		};
		
		/**
		 * Remove Project Information
		 * @Param modelData
		 */
		var removeProjectInfo = function(modelData) {
			if (modelData) { // DELETE
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionprojects/' + modelData.id;
				modelData.destroy({
					success : function() {
						Gloria.WarehouseApp.trigger('QISettingsModal:project:removed', true);
						Gloria.WarehouseApp.trigger('QISettingsGrid:clearSelectedModels');
						Gloria.WarehouseApp.trigger('QISettingsButton:ResetButtons');
	                    // Refetch qiSettingsCollection and refresh the Grid
	                    qiSettingsCollection.fetch();
					}
				});
			}
		};
		
		/**
		 * Save Project Information
		 * @Param modelData
		 * @Param formData
		 */
		var saveProjectInfo = function(modelData, formData) {
			var saveInfo = function(model, data) {
				model.save(data, {
					success : function(response) {
						Gloria.WarehouseApp.trigger('QISettingsModal:project:saved', true);
						Gloria.WarehouseApp.trigger('QISettingsGrid:clearSelectedModels');
						Gloria.WarehouseApp.trigger('QISettingsButton:ResetButtons');
	                    // Refetch qiSettingsCollection and refresh the Grid
	                    qiSettingsCollection.fetch();
					},
					validationError : function(errorMessage, errors) {
						Gloria.WarehouseApp.trigger('QISettingsModal:project:saved', false, errorMessage);
					},
					error : function(error, errorMessage) {
						if(errorMessage.status != 400) {//@Important: handle all errors except validation error
														//as we have defined validationError to handle such (400) case
							Gloria.WarehouseApp.trigger('QISettingsModal:project:saved', false, error);
						}
                    }
				});
			};
			if (modelData) { // PUT
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionprojects/' + modelData.id;
				saveInfo(modelData, formData);
			} else { // POST
				var modelData = new GloriaModel();
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionprojects';
				formData.id = null;
				saveInfo(modelData, formData);
			}
		};
		
		/**
		 * Show Supplier Information
		 * @Param modelData
		 */
		var showSupplierInfo = function(modelData) {
			require(['views/warehouse/qisettings/view/AddEditSupplierView'], function(AddEditSupplierView) {
				var addEditSupplierView = new AddEditSupplierView({
					model : modelData,
					collection : qiSettingsCollection
				});
				Gloria.basicModalLayout.content.show(addEditSupplierView);
			});
		};
		
		/**
		 * Remove Supplier Information
		 * @Param modelData
		 */
		var removeSupplierInfo = function(modelData) {
			if (modelData) { // DELETE
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionsuppliers/' + modelData.id;
				modelData.destroy({
					success : function() {
						Gloria.WarehouseApp.trigger('QISettingsModal:supplier:removed', true);
						Gloria.WarehouseApp.trigger('QISettingsGrid:clearSelectedModels');
						Gloria.WarehouseApp.trigger('QISettingsButton:ResetButtons');
	                    // Refetch qiSettingsCollection and refresh the Grid
	                    qiSettingsCollection.fetch();
					}
				});
			}
		};
		
		/**
		 * Save Supplier Information
		 * @Param modelData
		 * @Param formData
		 */
		var saveSupplierInfo = function(modelData, formData) {
			var saveInfo = function(model, data) {
				model.save(data, {
					success : function(response) {
						Gloria.WarehouseApp.trigger('QISettingsModal:supplier:saved', true);
						Gloria.WarehouseApp.trigger('QISettingsGrid:clearSelectedModels');
						Gloria.WarehouseApp.trigger('QISettingsButton:ResetButtons');
	                    // Refetch qiSettingsCollection and refresh the Grid
	                    qiSettingsCollection.fetch();
					},
					validationError : function(errorMessage, errors) {
						Gloria.WarehouseApp.trigger('QISettingsModal:supplier:saved', false, errorMessage);
					},
					error : function(error, errorMessage) {
						if(errorMessage.status != 400) {//@Important: handle all errors except validation error
														//as we have defined validationError to handle such (400) case
							Gloria.WarehouseApp.trigger('QISettingsModal:supplier:saved', false, error);
						}
                    }
				});
			};
			if (modelData) { // PUT
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionsuppliers/' + modelData.id;
				saveInfo(modelData, formData);
			} else { // POST
				var modelData = new GloriaModel();
				modelData.url = '/warehouse/v1/warehouses/' + whSiteId + '/qualityinspectionsuppliers';
				formData.id = null;
				saveInfo(modelData, formData);
			}
		};
		
		Controller.QISettingsController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:part:show', showPartInfo);
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:part:remove', removePartInfo);
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:part:save', savePartInfo);
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:project:show', showProjectInfo);
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:project:remove', removeProjectInfo);
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:project:save', saveProjectInfo);
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:supplier:show', showSupplierInfo);
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:supplier:remove', removeSupplierInfo);
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:supplier:save', saveSupplierInfo);
			},

			control : function(page) {
				if(page) {
					module = page;
				} else {
					module = 'part';
					Backbone.history.navigate('warehouse/qisettings/part', {
	                    replace : true
	                });
				}
				prepareQISettingsView();
			},

			onDestroy : function() {
				module = null;
				qiSettingView = null;
				qiSettingsButtonView = null;
				qiSettingsGridView = null;
				qiSettingsCollection = null;
				whSiteId = null;
			}
		});
	});

	return Gloria.WarehouseApp.Controller.QISettingsController;
});
