define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
        'i18next',
        'utils/UserHelper',
        'models/ProcureLineModel',
        'utils/backbone/GloriaPageableCollection',
        'views/procurement/common/FromStockCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, ProcureLineModel, GloriaPageableCollection, FromStockCollection) {
	
	Gloria.module('ProcurementApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
				
		var procurementDetailView;
		var procurementGeneralPartInfoView;
		var procurementProcureInfoView;
		var procurementWarehouseInfoView;
		var procurementExternalInternalInfoView;
		var procureLineId;
		
		var procurementRequestInfoView;
		
		var procureStatus;
		var procureLineModel;
		var hasRolePROCURE_INTERNAL;
		var hasRoleDELIVERY;
		var fromStockCollection;
		var materialCollection;
		
		/**
		 * Prepare Procurement Line Detail. Initialize Data Source Objects which are going to be used as
		 * data transfer objects and also set the page layout.
		 * 
		 * UnassignedRequestView is the main Layout which adds two regions: moduleInfo & gridInfo to the page,
		 * so that the respective views can be attached later on!
		 */ 
		var setPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView',  {itemId : procureLineId});
			var userRoles = UserHelper.getInstance().getUserRoles();
            _.each(userRoles, function(ur) {
				if(ur.roleName === 'PROCURE-INTERNAL') {
					hasRolePROCURE_INTERNAL = true;
				}
				if(ur.roleName === 'DELIVERY') {
					hasRoleDELIVERY = true;
				}
			});
			require(['views/procurement/detail/view/ProcurementDetailView'], function(ProcurementDetailView) {
				procurementDetailView = new ProcurementDetailView();
				procurementDetailView.on('show', function() {
				    prepareProcurementGeneralPartInfoView(procureLineId);
				}, this);
				Gloria.basicLayout.content.show(procurementDetailView);
			});
		};
		
		
		
		var isChangedByIgnoringType = function(attributes, changedAttributes) {
			function setToEmpty(val){
			    return (val === undefined || val == null) ? '' : val;
			}
			for (var attr in changedAttributes) {
				if(setToEmpty(attributes[attr]) == setToEmpty(changedAttributes[attr])) {
					continue;
				} else {
					return true;
				};
			};
			return false;
		};
		
		/**
         * Save Procure Line Model
         */
		var saveProcureLineModel = function(procureLineModelData, internalInfo, externalInfo, action, materialLineKeys) {    
			// Set internalInfo if it is internal or else set externalInfo
            if(procureLineModelData.procureType == 'INTERNAL') {
            	procureLineModel.set(internalInfo);
            } else if(procureLineModelData.procureType == 'EXTERNAL') {
            	procureLineModel.set(externalInfo);   
            };
            
            procureLineModelData.edited = 'true';
            
            var params = '?' + (typeof action !== 'undefined' ? '&action=' + action : '');
            if(materialLineKeys) {
            	params = params + '&fromStockMaterials=' + materialLineKeys;
            }
            procureLineModel.save(procureLineModelData, {
            	url: '/procurement/v1/procurelines/' + procureLineModel.get('id') + params,
                success : function(resp) {
                    Gloria.trigger('goToPreviousRoute');
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
         * Procure Line Detail View Submit
         */
		var procureLineDetailViewSubmit = function(procureLineModel, internalInfo, externalInfo, procureRequestData, action, materialLineKeys) {
		    var that = this;
	        var counter = 0;
	        _.each(procureRequestData, function(requestData) {
	            var requestModel = materialCollection.get(requestData.id);
	            requestModel.url = '/procurement/v1/procurelines/' + procureLineId + '/materials/' + requestData.id;	
	            requestModel.save(requestData, {
				    success : function(resp) {
				        counter++;
				        if(counter == procureRequestData.length) {
				            Gloria.ProcurementApp.trigger('procurementSaved:TriggerProcureLineModelSave',
				            	procureLineModel, internalInfo, externalInfo, action, materialLineKeys);
				        };
				    },
				    validationError : function(errorMessage, errors) {
						Gloria.trigger('showAppMessageView', {
							type : 'error',
							message : errorMessage
						});
					}			    					
				});
		    });
		};
		
		/**
         * Procure line Details Remove
         */
		var procurelineDetailsRemove = function(toRemoveItems) {
			if(!toRemoveItems || !toRemoveItems.length) return;
			Gloria.trigger('hideAppMessageView');
			var procureDetailCollection = toRemoveItems[0].collection;
			if(toRemoveItems.length === procureDetailCollection.length) {
				Gloria.trigger('showAppMessageView', {
	    			type : 'error',
	    			title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
	    			message : new Array({
            			element: null,
            			message: i18n.t('Gloria.i18n.errors.GLO_ERR_13')
	    			})
	    		});
				return;
			}
			
			require(['collections/ProcureDetailCollection'], function(ProcureDetailCollection) {
				var deletedMaterialCollection = new ProcureDetailCollection(toRemoveItems);
				deletedMaterialCollection.url = '/procurement/v1/procurelines/' + procureLineId + '/materials/' + _.pluck(toRemoveItems, 'id').join(',');
				Backbone.sync('delete', deletedMaterialCollection, {					
					success: function() {
					    procureLineModel.fetch();
						if(procureDetailCollection) {
							procureDetailCollection.remove(toRemoveItems);
							prepareProcurementGeneralPartInfoView(procureLineId);
						}
					},
					validationError : function(errorMessage, errors) {
						Gloria.trigger('showAppMessageView', {
			    			type : 'error',
			    			title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
			    			message : errorMessage
			    		});
					}
				});
			});
		};
		
		/**
         * Prepare Procurement General Part Info View
         */
		var prepareProcurementGeneralPartInfoView = function(procureLineId) {
			var that = this;
			require(['views/procurement/detail/view/ProcurementGeneralPartInfoView'],
    			function(ProcurementGeneralPartInfoView) {        	
    				procureLineModel = new ProcureLineModel({id : procureLineId}); 
    	        	procurementGeneralPartInfoView = new ProcurementGeneralPartInfoView({
    	        		procureLineModel: procureLineModel
    				});
    	        	procureLineModel.fetch({
    	        		success : function(model, res, settings) {
    	        			if(settings.xhr.status == 204) { // Redirect to Page Not Found if resource is not available
    							Gloria.trigger('Error:404');
    							return;
    						} else {
    							procureStatus = model.get('status');
                                showProcurementGeneralPartInfoView.call(that, model);
                                Gloria.ProcurementApp.trigger('procureLineModel:fetched', model);
    						}
    					}
    	        	});
			    }
			);
		};
		
		/**
         * Prepare Procurement Warehouse Info View
         */
		var prepareProcurementWarehouseInfoView = function(procureLine) {
			require(['views/procurement/detail/view/ProcurementWarehouseInfoView'], function(ProcurementWarehouseInfoView) {        	
				procurementWarehouseInfoView = new ProcurementWarehouseInfoView({
					procureLineModel: procureLine
				});
				showProcurementWarehouseInfoView({procureLineId : procureLineId});
			});
		};
		
		/**
         * Prepare Procurement Procure Info View
         */
		var prepareProcurementProcureInfoView = function(procureLine){
			require(['views/procurement/detail/view/ProcurementProcureInfoView'], function(ProcurementProcureInfoView) {
				procurementProcureInfoView = new ProcurementProcureInfoView({
					procureLineModel: procureLine,
					hasRolePI: hasRolePROCURE_INTERNAL,
					hasRoleDELIVERY : hasRoleDELIVERY
				});
				showProcurementProcureInfoView();
			});
		};
		
		/**
         * Prepare Procurement External Internal Info View
         */
		var prepareProcurementExternalInternalInfoView = function(procureLine) {
			require(['views/procurement/detail/view/ProcurementExternalInternalInfoView'], function(ProcurementExternalInternalInfoView) {   	
				procurementExternalInternalInfoView = new ProcurementExternalInternalInfoView({
					procureLineModel: procureLine
				});      
				showProcurementExternalInternalInfoView();
			});
		};
		
		/**
         * Prepare Procurement Request Info View
         */
		var prepareProcurementRequestInfoView = function(procureLine) {
			var that = this;
			require(['collections/ProcureDetailCollection', 'views/procurement/detail/view/ProcurementRequestInfoView'],
			function(ProcureDetailCollection, ProcurementRequestInfoView, SupplierCounterPartCollection) {
				materialCollection = new ProcureDetailCollection();
				materialCollection.url = '/procurement/v1/procurelines/' + procureLineId + '/materials';
				procurementRequestInfoView = new ProcurementRequestInfoView({
					status : procureStatus,
					procureLineModel : procureLineModel,
					collection : materialCollection
				});
				materialCollection.fetch({
					success : function(collection) {
					    showProcurementRequestInfoView.call(that, {
							status : procureStatus,
							procureDetailCollection : collection
						});
					    markProcureLineAsRead();
					    showPartVersionUpdatedAlert(collection);
					}
				});
			});
		};
		
		/**
         * Show Part Version Updated Alert
         */
		var showPartVersionUpdatedAlert = function(collection) {
			_.any(collection.toJSON(), function(info, i) {
				if (info.alertPartVersion) {
					Gloria.trigger('showAppMessageView', {
						type : 'warning',
						title : i18n.t('Gloria.i18n.material.details.text.partVersionUpdatedHeader'),
						message : new Array({
							message : i18n.t('Gloria.i18n.material.details.text.partVersionUpdatedText')
						})
					});
					return true;
				}
			});
		};
		
		/**
	     * The callback to handle layout management. This callback will be called when the respective model is fetched.
	     */
		var showProcurementGeneralPartInfoView = function(procureLine) {
			Gloria.trigger('showBreadcrumbView',  {pPartNumber : procureLine.get('pPartNumber')});
			procurementDetailView.procureGeneralPartInfo.show(procurementGeneralPartInfoView);	
			prepareProcurementWarehouseInfoView(procureLine);	 
			prepareProcurementProcureInfoView(procureLine);
			//prepareProcurementExternalInternalInfoView(procureLine);
			prepareProcurementRequestInfoView(procureLine);
		};	
		
		/**
         * Show Procurement Warehouse Info View
         */
		var showProcurementWarehouseInfoView = function(options) {
			procurementDetailView.procureWarehouseInfo.show(procurementWarehouseInfoView);			
		};	
		
		/**
         * Show Procurement Procure Info View
         */
		var showProcurementProcureInfoView = function(){
			procurementDetailView.procureProcureInfo.on('show', function() {
				// Check FromStock, if available show it
				if(procureLineModel.get('status') != 'PROCURED' && !procureLineModel.get('forStock')) {
					processFromStock(procureLineModel.get('companyCode'));
				}
			}, this);
			procurementDetailView.procureProcureInfo.show(procurementProcureInfoView);			
		};
		
		/**
         * Process FromStock
         */
		var processFromStock = function(companyCode) {
			    fromStockCollection = new FromStockCollection([], {
				queryParams: {
			        partNumber : procureLineModel.get('pPartNumber'),
			        partVersion : procureLineModel.get('pPartVersion'),
			        project : procureLineModel.get('projectId'),
			        partAffiliation : procureLineModel.get('pPartAffiliation'),
			        partModification : procureLineModel.get('pPartModification')
			    },
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.procurement.FromStockGrid' + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				}
			});
			    
			//fromStockCollection.url = '/procurement/v1/materiallines/available?companyCode='+companyCode;
			fromStockCollection.queryParams.companyCode = companyCode;
			
			fromStockCollection.fetch({			   
				success: function(response) {
					if(response.length > 0) {
						require(['views/procurement/detail/view/FromStockGridView'], function(FromStockGridView) {
							try {
    						    var fromStockGridView = new FromStockGridView({
    							    procureLineModel : procureLineModel,
    								collection : fromStockCollection
    							});
    						    Gloria.ProcurementApp.trigger('showFromStockMessage');
    							procurementProcureInfoView.fromStock.show(fromStockGridView);
    							//Gloria.ProcurementApp.trigger('highlight:usedmaterialtype', fromStockCollection);
							} catch(e) {
							    // No el found.
							}
						});
					}
				}
			});
		};
		
		/**
         * Show Procurement External Internal Info View
         */
		var showProcurementExternalInternalInfoView = function(){
			procurementDetailView.procureExternalInternalInfo.show(procurementExternalInternalInfoView);
		};
		
		/**
         * Show Procurement Request Info View
         */
		var showProcurementRequestInfoView = function(options) {			
			procurementDetailView.procureRequestInfo.show(procurementRequestInfoView);		
		};
		
		/**
         * Show Multiple Update Modal View
         */
		var showMultipleUpdateModalView = function(selectedTOs) {
			require(['views/procurement/detail/view/WarehouseModalView'], function(WarehouseModalView) {
				var warehouseModalView = new WarehouseModalView({
					selectedTOs : selectedTOs
				});
				Gloria.basicModalLayout.content.show(warehouseModalView);
			});
		};
		
		/**
         * Mark Procure Line As Read
         */
		var markProcureLineAsRead = function(options) {
			if(procureLineModel.get('hasUnread')) {
				procureLineModel.save({
					hasUnread: false
				}, {
					success: function() {
						// Update the version of the material which has 'isRead' equals to false, 
						// since that particular model/s will get updated on server after sending the above save request on procurelinemodel
						_.each(materialCollection.where({isRead: false}), function(model) {
							var version = model.get('version');
							model.set({
								isRead: true,
								version: version + 1
							});
						});
					}
				});
			}
		};
		
		Controller.ProcurementDetailController = Marionette.Controller.extend({
			
			initialize : function() {
				this.initializeListeners();
			},
			
			initializeListeners : function() {
			    this.listenTo(Gloria.ProcurementApp, 'procureLineform:submit', procureLineDetailViewSubmit);
			    this.listenTo(Gloria.ProcurementApp, 'procurelineDetails:remove', procurelineDetailsRemove);
			    this.listenTo(Gloria.ProcurementApp, 'procurementSaved:TriggerProcureLineModelSave', saveProcureLineModel);
			    this.listenTo(Gloria.ProcurementApp, 'procurelineDetails:MultipleUpdate', showMultipleUpdateModalView);
			},
			
			control : function(procureId) {
				procureLineId = procureId;
				setPageLayout.call(this);
			},
			
			onDestroy: function() {			    
			    procurementDetailView = null;			    
		        procurementGeneralPartInfoView = null;		        
		        procurementProcureInfoView = null;		        
		        procurementWarehouseInfoView = null;		        
		        procurementExternalInternalInfoView = null;
		        procureLineId = null;		        
		        procurementRequestInfoView = null;		        
		        procureStatus = null;
		        hasRolePROCURE_INTERNAL = null;
		        hasRoleDELIVERY = null;
		        fromStockCollection = null;
			}
		});
	});
	
	return Gloria.ProcurementApp.Controller.ProcurementDetailController;
});