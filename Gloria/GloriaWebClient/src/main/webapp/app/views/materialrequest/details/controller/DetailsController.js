define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'i18next',
		'utils/UserHelper',
		'views/materialrequest/model/MaterialRequestModel',
		'collections/MaterialRequestLineCollection',
		'models/MaterialRequestLineModel',
		'utils/backbone/GloriaModel'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, MaterialRequestModel, MaterialRequestLineCollection, MaterialRequestLineModel, GloriaModel) {

	Gloria.module('MaterialRequestApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var materialRequestId;
		var detailsLayoutView;
		var materialLayoutView;
		var materialRequestModel;
		var materialRequestLineCollection;
		
		/**
		 * Open Material Request Details
		 */
		var openMaterialRequestDetails = function() {
			Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', {changeId : ''});
			prepareMaterialRequestDetails();
		};
		
		/**
		 * Create Material Request Details
		 */
		var createMaterialRequestDetails = function() {
			Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', materialRequestId ? {itemId : materialRequestId} : null);
			prepareMaterialRequestDetails();
		};
		
		/**
		 * Prepare Material Request Details
		 */
		var prepareMaterialRequestDetails = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			materialRequestModel = new MaterialRequestModel();
			materialRequestLineCollection = new MaterialRequestLineCollection([], {
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.materialrequest.MaterialInfo'
								+ (materialRequestModel.get('type') && materialRequestModel.get('type').toUpperCase() || 'SINGLE')
								+ 'Grid'  + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				}
			});
			materialRequestLineCollection.materialRequest = materialRequestModel;
		};
		
		/**
		 * Set Page Layout
		 */
		var setPageLayout = function() {
			require(['views/materialrequest/details/view/DetailsLayoutView'], function(DetailsLayoutView) {
				detailsLayoutView = new DetailsLayoutView();
				detailsLayoutView.on('show', function() {
					prepareGeneralInformation();
				}, this);				
				Gloria.basicLayout.content.show(detailsLayoutView);
			});
		};
		
		/**
		 * Prepare General Information
		 */
		prepareGeneralInformation = function() {
			if(materialRequestId) { // Open Material Request
				materialRequestModel.fetch({
					url : '/materialrequest/v1/materialrequests/' + materialRequestId + '/current',
					success : function(model, res, settings) {
						if(settings.xhr.status == 204) { // Redirect to Page Not Found if resource is not available
							Gloria.trigger('Error:404');
							return;
						} else {
							materialRequestId = model.get('id');
				            $.extend(materialRequestLineCollection.state, {
								pageSize : function() {
									var object = JSON.parse(window.localStorage.getItem('Gloria.materialrequest.MaterialInfo'
											+ (materialRequestModel.get('type') && materialRequestModel.get('type').toUpperCase() || 'SINGLE')
											+ 'Grid'  + '.' + UserHelper.getInstance().getUserId()));
									return (object && object['pageSize']) || 10; // Default 10
								}(),
								currentPage : 1
							});
				            
							var whichGrid = 'MaterialInfo' + materialRequestModel.get('type').toUpperCase() + 'Grid';
				            materialRequestLineCollection.filterKey = whichGrid;

							materialRequestLineCollection.url = '/materialrequest/v1/materialrequests/' + model.get('id') + '/current/materialrequestlines',
							materialRequestLineCollection.materialRequest = materialRequestModel;
							Gloria.trigger('showBreadcrumbView', {
								mtrlRequestVersion : model.get('mtrlRequestVersion') || ''
							});
							showGeneralInformation();
							prepareMaterialGridInfo();
							showDetailsControlButtonsView();
						}
					}
				});
			} else { // Create Material Request
				showGeneralInformation();
				prepareMaterialGridInfo();
				showDetailsControlButtonsView();
			}
		};
		
		/**
		 * Show General Information
		 */
		showGeneralInformation = function() {
			require(['views/materialrequest/details/view/GeneralInformation'], function(GeneralInformation) {
				var generalInformation = new GeneralInformation({
					model : materialRequestModel
				});
				detailsLayoutView.generalInformation.show(generalInformation);
			});
		};

		/**
		 * Prepare Material Grid Information
		 */
		prepareMaterialGridInfo = function() {
			require(['views/materialrequest/details/view/MaterialLayoutView'], function(MaterialLayoutView) {
				materialLayoutView = new MaterialLayoutView();
				materialLayoutView.on('show', function() {
					showMaterialButtonView();
					prepareMaterialGridView();
				}, this);
				detailsLayoutView.gridPane.show(materialLayoutView);
			});
		};
		
		/**
		 * Show Material Button View
		 */
		var showMaterialButtonView = function() {
			require([ 'views/materialrequest/details/view/MaterialButtonsView' ], function(MaterialButtonsView) {
				var buttonOptions = {permittedActions: {}};
				if (materialRequestModel.isNew() || materialRequestModel.isEditableOrUpdated()) {
					buttonOptions = {
						permittedActions : {
							addNew : true
						}
					};					
				}
				var materialButtonsView = new MaterialButtonsView({
					buttonOptions: buttonOptions,
					collection: materialRequestLineCollection
				});
				materialLayoutView.buttonPane.show(materialButtonsView);
			});
		};
		
		/**
		 * Show Material Grid View
		 */
		var prepareMaterialGridView = function() {
			require([ 'views/materialrequest/details/view/MaterialInfoGridView' ], function(MaterialInfoGridView) {
				// Instantiate the MaterialRequestsGrid
				var materialInfoGridView = new MaterialInfoGridView({
					model : materialRequestModel,
					collection : materialRequestLineCollection
				});
				materialInfoGridView.on('show', function() {
					processGridInformation({
						callback : function(collection) {
							if(collection.length > 0) {
								Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:buttons:enable', ['send']);
							}
						}
					});
				}, this);
				materialLayoutView.gridPane.show(materialInfoGridView);
			});
		};
		
		/**
		 * Process Material Grid View
		 */
		var processGridInformation = function(options) {
			options || (options = {});
			if (!materialRequestModel.isNew()) {
				materialRequestLineCollection.fetch({
					url : '/materialrequest/v1/materialrequests/' + materialRequestModel.get('id') + '/current/materialrequestlines',
					wait : true,
					cache : false,
					reset : options.reset || true,
					success : function(collection) {
						options.callback && options.callback(collection);
					}
				});				
			}
		};
		
		/**
		 * Show Details Control Buttons View
		 */
		var showDetailsControlButtonsView = function() {
			require([ 'views/materialrequest/details/view/DetailsButtonsView' ], function(DetailsButtonsView) {
				var detailsButtonsView = new DetailsButtonsView({
					model: materialRequestModel,
					collection: materialRequestLineCollection
				});
				detailsLayoutView.controlButtons.show(detailsButtonsView);
			});
		};
		
		/**
		 * This method is called when MTR type is changed in details page dropdown
		 */
		var onChangeMTRType = function(materialRequestModel, newType){
			materialRequestModel.set({type: newType});				
			if(newType) { // Only show on valid selection
				materialLayoutView.$el.show();
				prepareMaterialGridView();
				Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:buttons:enable', ['save']);
			} else {
				materialLayoutView.$el.hide();
				Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:buttons:disable', ['save']);
			}
		};
		
		/**
		 * Save Material General Information
		 */		
		var saveGeneralInformation = function(data, exportExcel) {
			if (data.id && data.id != '') {
				materialRequestModel.save(data, {
					url : '/materialrequest/v1/materialrequests/' + data.id,
					success : function(response) {
						if (!response.isEditable()) {
							Backbone.history.fragment = null;
						}
						downloadExcel(response.id, exportExcel);
						Backbone.history.navigate('materialrequest/overview', {
							trigger : true
						});
					},
					validationError : function(errorMessage, errors) {
						Gloria.trigger('showAppMessageView', {
							type : 'error',
							title : i18n.t('errormessages:general.title'),
							message : errorMessage
						});
					}
				});
			} else {
				data.id = undefined;
				materialRequestModel.save(data, {
					url : '/materialrequest/v1/materialrequests',
					success : function(model) {
						downloadExcel(model.id, exportExcel);
						Backbone.history.navigate('materialrequest/overview', {
							trigger : true
						});
					},
					validationError : function(errorMessage, errors) {
						Gloria.trigger('showAppMessageView', {
							type : 'error',
							title : i18n.t('errormessages:general.title'),
							message : errorMessage
						});
					}
				});
			}
		};
		
		/**
		 * Send General Information
		 */
		var sendGeneralInformation = function(data, exportExcel) {
			var isValidMc;
		
			// this check should be skipped if it is not new Version
			if(data.isNew == 'true' && data.materialControllerUserId) {
				  $.ajax({
					  async:false,
		                url:'/materialrequest/v1/materialrequests/'+data.companyCode+'/isValidMC/'+data.materialControllerUserId, 
		                success:function(data) {
		                	isValidMc = data;
		                }
		              });
			} else {
				// assign isValidMc to valid 
				isValidMc = 'true';
			}
			
		
			if(isValidMc == 'true') {
			materialRequestModel.save(data, {
				success : function(response) {
					materialRequestModel.url = '/materialrequest/v1/materialrequests/' + materialRequestModel.get('id') + '/current?action=send';
					materialRequestModel.save({}, {
						success : function(response) {
							downloadExcel(response.id, exportExcel);
							Backbone.history.navigate('materialrequest/overview', {
								trigger : true
							});
						},
						validationError : function(errorMessage, errors) {
							Gloria.trigger('showAppMessageView', {
								type : 'error',
								title : i18n.t('errormessages:general.title'),
								message : errorMessage
							});
						}
					});
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'),
						message : errorMessage
					});
				}
			});
			} else {
				Gloria.trigger('showAppMessageView', {
	    			type : 'error',
	    			message : new Array({
	    				message : i18n.t('Gloria.i18n.errors.GLO_ERR_113')
	    			})
	    		});
			}
		};
		
		/**
		 * Delete Material Request
		 */
		var deleteMaterialRequest = function() {
			materialRequestModel.destroy({
				success : function(model) {
					Backbone.history.navigate('materialrequest/overview', {
						trigger : true
					});
				}
			});
		};
		
		/**
		 * Download Excel
		 */
		var downloadExcel = function(materialRequestId, shouldExport) {
			if (!materialRequestId)
				return;
			if (shouldExport) {
				window.location.href = '/GloriaUIServices/api/materialrequest/v1/materialrequests/' + materialRequestId + '/current/materialrequestlines/excel';
			}
		};
		
		/**
		 * Process Add Edit Material Information
		 */
		var processAddEditMaterialInformation = function(model) {
			// Save request header if it is not yet created!
			if(materialRequestModel.isNew()) {
				this.listenToOnce(Gloria.MaterialRequestApp, 'MaterialRequestDetails:headerInfo:collected', function(headerInfo) {
					if(headerInfo) {
						delete headerInfo.id;
						saveRequestHeaderInfo(headerInfo, function() {
							Backbone.history.navigate('materialrequest/overview/openrequest/' + materialRequestModel.get('id'), {
								trigger : false,
								replace : true
							});
							materialRequestLineCollection.url = '/materialrequest/v1/materialrequests/' + materialRequestModel.get('id') + '/current/materialrequestlines',
							showDetailsControlButtonsView();
							prepareMaterialGridView();
							showAddEditMaterialInformation();
						});
					}
				});
				Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:headerInfo:collect');
			} else {
				showAddEditMaterialInformation(model);
			}
		};
		
		/**
		 * Save Request Header Information
		 */
		var saveRequestHeaderInfo = function(headerInfo, callback) {
			materialRequestModel.save(headerInfo, {
				url : '/materialrequest/v1/materialrequests',
				success : function(response) {
					// Current materialRequestId is the id we save now!
					prepareGeneralInformation();
					materialRequestId = response.get('id');
					callback && callback();
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						title : i18n.t('errormessages:general.title'),
						message : errorMessage
					});
				}
			});
		};
		
		/**
		 * This method will construct and show the Add material information modal window
		 */
		var showAddEditMaterialInformation = function(selectedModel) {
			require([ 'views/materialrequest/details/view/AddEditMaterialInfoView' ], function(AddEditMaterialInfoView) {
				var addEditMaterialInfoView = new AddEditMaterialInfoView({
					materialRequestModel: materialRequestModel,
					materialRequestLineCollection : materialRequestLineCollection,
					model : selectedModel,
					addAnother: (selectedModel && selectedModel.id) ? false : true
				});
				Gloria.basicModalLayout.content.show(addEditMaterialInfoView);
				addEditMaterialInfoView.on('hide', function() {
					Gloria.basicModalLayout.content.reset();
				});
			});
		};
		
		/**
		 * Process Excel Import
		 */
		var processExcelImport = function(callback) {
			// Save request header if it is not yet created!
			if(materialRequestModel.isNew()) {
				this.listenToOnce(Gloria.MaterialRequestApp, 'MaterialRequestDetails:headerInfo:collected', function(headerInfo) {
					if(headerInfo) {
						delete headerInfo.id;
						saveRequestHeaderInfo(headerInfo, function() {
							Backbone.history.navigate('materialrequest/overview/openrequest/' + materialRequestModel.get('id'), {
								trigger : false,
								replace : true
							});
							materialRequestLineCollection.url = '/materialrequest/v1/materialrequests/' + materialRequestModel.get('id') + '/current/materialrequestlines',
							materialRequestLineCollection.materialRequest = materialRequestModel;
							prepareMaterialGridView();
							showDetailsControlButtonsView();
							callback && callback();
						});
					}				
				});
				Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:headerInfo:collect');
			} else {
				callback && callback();
			}
		};
		
		/**
		 * This method will be called on Adding or Editing a material information line(MaterialRequestLine)
		 */
		var saveMaterialInformation = function(model, data) {
			var saveInfo = function(model, data) {
				model.save(data, {
					url : '/materialrequest/v1/materialrequests/' + materialRequestId + '/current/materialrequestlines',
					success : function(response) {
						prepareMaterialGridView();
						Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:saved', true);
					},
					validationError : function(errorMessage, errors) {
						prepareMaterialGridView();
						Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:saved', false, errorMessage);
					},
					error : function() {
						Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:saved', false);
					}
				});
			};
			if(model) { // PUT
				saveInfo(model, data);
			} else { // POST
				var modelTemp = new MaterialRequestLineModel();
				data.id = undefined;
				saveInfo(modelTemp, data);
			}
		};
		
		/**
		 * This method will be called on Removing a material information line(MaterialRequestLine)
		 */
		var removeMaterialInformation = function(modelsToDelete) {
		    if(!modelsToDelete) return;
            var ids = '';
			_.each(modelsToDelete, function(model) {
				ids = (ids ? ids + ',' : '') + model.get('id');
			});
            var modelTemp = new MaterialRequestLineModel();
            modelTemp.set('id', ids);
            modelTemp.destroy({
            	url : '/materialrequest/v1/materialrequests/' + materialRequestId + '/current/materialrequestlines/' + ids,
				success : function(model) {
					prepareMaterialGridView();
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : new Array({
		    				message : i18n.t('Gloria.i18n.processFailed')
		    			})
		    		});
				}
			});
		};
		
		/**
		 * Create New Version Material Request
		 */
		var newVersionMaterialRequest = function() {
			if (!materialRequestModel || !materialRequestModel.isNewVersionAvailable())
				return;
			materialRequestModel.save(null, {
				url : '/materialrequest/v1/materialrequests/' + materialRequestModel.get('id') + '/current?action=newVersion',
				success : function(model) {
					Gloria.trigger('reloadPage');
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : new Array({
		    				message : i18n.t('Gloria.i18n.processFailed')
		    			})
		    		});
				}
			});
		};
		
		/**
		 * Revert Material Request
		 */
		var revertMaterialRequest = function() {
			if (!materialRequestModel || !materialRequestModel.isRevertable())
				return;
			materialRequestModel.save(null, {
				url : materialRequestModel.url() + '/current?action=revert',
				success : function(model) {
					Backbone.history.navigate('materialrequest/overview', {
						trigger : true
					});
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : new Array({
		    				message : i18n.t('Gloria.i18n.processFailed')
		    			})
		    		});
				}
			});
		};
		
		/**
		 * Cancel Material Request
		 */
		var cancelMaterialRequest = function() {
			if (!materialRequestModel) return;
			materialRequestModel.save(null, {			
				url : '/materialrequest/v1/materialrequests/' + materialRequestModel.get('id') + '/current?action=cancel',
				success : function(model) {
					Backbone.history.navigate('materialrequest/overview', {
						trigger : true
					});
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : new Array({
		    				message : i18n.t('Gloria.i18n.processFailed')
		    			})
		    		});
				}
			});
		};
		
		/**
		 * Copy & Create Material Request
		 */
		var copyAndCreateNewMaterialRequest = function() {
			var modelTemp = new GloriaModel();
			modelTemp.fetch({
				url : '/materialrequest/v1/materialrequests/' + materialRequestModel.get('id') + '/current',
				data : {action : 'copy'},
				success : function(response) {
					var location = 'materialrequest/overview/openrequest/' + response.get('id');
					Backbone.history.navigate(location, {
						trigger : true
					});
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : new Array({
		    				message : i18n.t('Gloria.i18n.processFailed')
		    			})
		    		});
				}
			});
		};
		
		/**
		 * Refresh Matrial Information
		 */
		var refreshMatrialInformation = function() {
			processGridInformation();
		};
		
		/**
		 * Reload Matrial Information and Reset Buttons
		 */
		var reloadButtonAndGrid = function() {
			materialRequestLineCollection.fetch({
				success: function() {
					Gloria.MaterialRequestApp.trigger('MaterialInfo:clearSelectedModels');
					Gloria.MaterialRequestApp.trigger('MaterialInfo:ResetButtons');
				}
			});
		};
		
		/**
		 * undo remove
		 */
		var undoRemove = function(model) {
			model.save({}, {
				url : '/materialrequest/v1/materialrequests/' + materialRequestId + '/current/materialrequestlines/' + model.id + '/undoRemove',
				type : 'PUT',
				success : function() {
					prepareMaterialGridView();
					Gloria.MaterialRequestApp.trigger('MaterialInfo:ResetButtons');
				}
			});
		};
		
		Controller.DetailsController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
			    this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:typeChange', onChangeMTRType);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:save', saveGeneralInformation);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:send', sendGeneralInformation);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:delete', deleteMaterialRequest);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:newVersion', newVersionMaterialRequest);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:revert', revertMaterialRequest);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:cancelMaterialRequest', cancelMaterialRequest);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:materialInfo:addOredit', processAddEditMaterialInformation);
			    this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:materialInfo:remove', removeMaterialInformation);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:importExcel:process', processExcelImport);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:materialInfo:save', saveMaterialInformation);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:copyAndCreate', copyAndCreateNewMaterialRequest);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:excel:imported', refreshMatrialInformation);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:materialInfo:reload', reloadButtonAndGrid);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:materialInfo:undoRemove', undoRemove);
			},

			control : function(options) {
				options || (options = {});
				materialRequestId = options.id;
				action = options.action	|| 'createrequest';
				if (action === 'openrequest') {
					openMaterialRequestDetails.call(this);
				} else {
					createMaterialRequestDetails.call(this);
				}
			},

			onDestroy : function() {
				materialRequestId = null;
				detailsLayoutView = null;
				materialLayoutView = null;
				materialRequestModel = null;
				materialRequestLineCollection = null;
			}
		});
	});

	return Gloria.MaterialRequestApp.Controller.DetailsController;
});
