define([ 'app',
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

	Gloria.module('MaterialApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var materialLineId;
		var materialDetailsView;
		var materialLineModel;
		var materialModel;
		var orderCollection;
		var historyCollection;

		/**
		 * Prepare Material Details View.
		 */
		var prepareMaterialDetailsView = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};

		/**
		 * Initialize Data Source Objects.
		 */
		var initializeDataSourceObjects = function() {
			materialLineModel = new GloriaModel({
				id : materialLineId
			});
			historyCollection = new GloriaCollection();
		};

		/**
		 * Set Page Layout.
		 */
		var setPageLayout = function() {
			Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView');
			require([ 'views/material/details/view/MaterialDetailsView' ], function(MaterialDetailsView) {
				materialDetailsView = new MaterialDetailsView();
				materialDetailsView.on('show', function() {
					renderDefaultAccordions(); // Show default Accordion
				}, this);
				Gloria.basicLayout.content.show(materialDetailsView);
			});
		};

		/**
		 * Process Accordions (for a given tab Id).
		 */
		var processAccordions = function(tabId) {
			switch (tabId) {
			case 'materialLineInformation':
				processMaterialLineInfo();
				break;
			case 'materialInformation':
				processMaterialInfo();
				break;
			case 'orderInformation':
				processOrderInfo();
				break;
			case 'contactInformation':
				processContactInfo();
				break;
			case 'attachmentInformation':
				processAttachmentInfo();
				break;
			case 'historyInformation':
				processHistoryInfo();
				break;
			default:
				break;
			}
		};

		/**
		 * Render Default Accordions.
		 */
		var renderDefaultAccordions = function(){
			materialLineModel.fetch({
				url : '/procurement/v1/materiallines/' + materialLineId,
				success : function(model, res, settings) {
					if(settings.xhr.status == 204) { // Redirect to Page Not Found if resource is not available
						Gloria.trigger('Error:404');
						return;
					} else {
						showMaterialLineInfo(model);
						processMaterialInfo();
					}
				}
			});
		}
		
		/**
		 * Process Material Line Information.
		 */
		var processMaterialLineInfo = function() {
			materialLineModel.fetch({
				url : '/procurement/v1/materiallines/' + materialLineId,
				success : function(response) {
					showMaterialLineInfo(response);
				}
			});
		};

		/**
		 * Process Material Line Information.
		 */
		var showMaterialLineInfo = function(model) {
			require([ 'views/material/details/view/MaterialLineInfoView' ], function(MaterialLineInfoView) {
				var materialLineInfoView = new MaterialLineInfoView({
					model : model
				});				
				materialDetailsView.materialLineInfo.show(materialLineInfoView);
			});
		};

		/**
		 * Process Material Information.
		 */
		var processMaterialInfo = function() {
			materialModel = new GloriaModel();
			materialModel.fetch({
				url : '/procurement/v1/materials/' + materialLineModel.get('materialOwnerId'),
				success : function(response) {
					showMaterialInfo(response);
					showPartVersionUpdatedAlert(materialLineModel);
					updatedAlertStatus(response);
				}
			});
		};
		
		/**
		 * Show Part Version Updated Alert.
		 */
		var showPartVersionUpdatedAlert = function(model) {
			if(model && model.get('alertPartVersion')) {
				Gloria.trigger('showAppMessageView', {
					type : 'warning',
					title : i18n.t('Gloria.i18n.material.details.text.partVersionUpdatedHeader'),
					message : new Array({
						message : i18n.t('Gloria.i18n.material.details.text.partVersionUpdatedText')
					})
				});
			}
		};
		
		/**
		 * Updated Alert Status.
		 */
		var updatedAlertStatus = function(model) {
			if(model) {
				model.save(null, {
					url : '/procurement/v1/materiallines/' + materialLineId + '/alerts'
				});
			}
		};
		
		/**
		 * Show Material Information.
		 */
		var showMaterialInfo = function(model) {
			require([ 'views/material/details/view/MaterialInfoView' ], function(MaterialInfoView) {
				var materialInfoView = new MaterialInfoView({
					model : model,
					materialLineModel : materialLineModel,
					userWithMCRole : UserHelper.getInstance().hasUserRole('PROCURE')
				});
				materialDetailsView.materialInfo.show(materialInfoView);
			});
		};

		/**
		 * Process Order Information.
		 */
		var processOrderInfo = function() {
			orderCollection = new GloriaCollection();
			orderCollection.fetch({
				url : '/procurement/v1/materials/' + materialLineModel.get('materialId') + '/orderlines',
				success : function(response) {
					showOrderInfo(response);
				}
			});
		};

		/**
		 * Show Order Information.
		 */
		var showOrderInfo = function(collection) {
			if(collection.length > 0) {
				require([ 'views/material/details/view/OrderInfoListView' ], function(OrderInfoListView) {
					var orderInfoListView = new OrderInfoListView({
						collection : collection
					});
					materialDetailsView.orderInfo.show(orderInfoListView);
				});
			}			
		};

		/**
		 * Process Contact Information.
		 */
		var processContactInfo = function() {
			if (!materialModel) { // If already fetched when showing Material Info, then do not fetch or else fetch it!
				materialModel = new GloriaModel();
				materialModel.fetch({
					url : '/procurement/v1/materials/' + materialLineModel.get('materialId'),
					success : function(response) {
						showContactInfo(response);
					}
				});
			} else {
				showContactInfo(materialModel);
			}
		};
		
		/**
		 * Show Contact Information.
		 */
		var showContactInfo = function(model) {
			require([ 'views/material/details/view/ContactInfoView' ], function(ContactInfoView) {
				var contactInfoView = new ContactInfoView({
					model : model
				});
				materialDetailsView.contactInfo.show(contactInfoView);
			});
		};
		
		/**
		 * Process Attachment Information.
		 */
		var processAttachmentInfo = function() {
			var statusList = ['CREATED', 'WAIT_TO_PROCURE', 'REQUISITION_SENT', 'ORDER_PLACED_INTERNAL', 'ORDER_PLACED_EXTERNAL'];
			if (materialLineModel && !_.contains(statusList, materialLineModel.get('status'))) {
				var deliveryNoteLineModel = new GloriaModel();
				deliveryNoteLineModel.fetch({
					url : '/material/v1/deliverynotelines/' + materialLineModel.get('deliveryNoteLineId'),
	                success : function(model, res, settings) {
	                	showAttachmentInfo(deliveryNoteLineModel);
	                }
	            });
			}
		};
		
		/**
		 * Show Attachment Information.
		 */
		var showAttachmentInfo = function(model) {
			require([ 'views/material/details/view/AttachmentsInfoView' ], function(AttachmentsInfoView) {
				var attachmentsInfoView = new AttachmentsInfoView({
					model : model
				});
				attachmentsInfoView.on('show', function() {
					processReceiveDocInfo(attachmentsInfoView);
					processProblemDocInfo(attachmentsInfoView);
					processQualityDocInfo(attachmentsInfoView);
				});
				materialDetailsView.attachmentInfo.show(attachmentsInfoView);
			});
		};
		
		/**
		 * Process Receive Document Information.
		 */
		var processReceiveDocInfo = function(parentView) {
			var receiveDocCollection = new GloriaCollection();
			receiveDocCollection.url = '/documents/v1/deliverynotelines/' + materialLineModel.get('deliveryNoteLineId') + '/receivedocs';
			require([ 'views/material/details/view/DocumentView' ], function(DocumentView) {
				var documentView = new DocumentView({
					collection : receiveDocCollection
				});
				documentView.on('show', function() {
					receiveDocCollection.fetch();
				}, this);
				parentView.receiveDocInfo.show(documentView);
			});
		};
		
		/**
		 * Process Problem Document Information.
		 */
		var processProblemDocInfo = function(parentView) {
			var problemDocCollection = new GloriaCollection();
			problemDocCollection.url = '/documents/v1/deliverynotelines/' + materialLineModel.get('deliveryNoteLineId') + '/problemdocs';
			require([ 'views/material/details/view/DocumentView' ], function(DocumentView) {
				var documentView = new DocumentView({
					collection : problemDocCollection
				});
				documentView.on('show', function() {
					problemDocCollection.fetch();
				}, this);
				parentView.problemDocInfo.show(documentView);
			});
		};
		
		/**
		 * Process Quality Document Information.
		 */
		var processQualityDocInfo = function(parentView) {
			var qualityDocCollection = new GloriaCollection();
			qualityDocCollection.url = '/documents/v1/deliverynotelines/' + materialLineModel.get('deliveryNoteLineId') + '/qidocs';
			require([ 'views/material/details/view/DocumentView' ], function(DocumentView) {
				var documentView = new DocumentView({
					collection : qualityDocCollection
				});
				documentView.on('show', function() {
					qualityDocCollection.fetch();
				}, this);
				parentView.qualityDocInfo.show(documentView);
			});
		};

		/**
		 * Process History Information.
		 */
		var processHistoryInfo = function() {
			historyCollection.fetch({
				url : '/common/v1/materiallines/' + materialModel.id + '/traceabilitys',
				success : function(response) {
					showHistoryInfo(response);
				}
			});
		};

		/**
		 * Show History Information.
		 */
		var showHistoryInfo = function(collection) {
			require([ 'views/material/details/view/TraceabilityGridView' ], function(TraceabilityGridView) {
				var traceabilityGridView = new TraceabilityGridView({
					collection : collection
				});
				materialDetailsView.historyInfo.show(traceabilityGridView);
			});
		};
		
		/**
		 * Save Material Line.
		 */
		var saveMaterialLine = function(data) {
			var partVersion = data.material.partVersion;
			var expirationDate = data.materialLine.expirationDate;			
			materialLineModel.save({expirationDate: expirationDate}, {
				url : '/procurement/v1/materiallines/' + materialLineId,
				success : function() {
					updateMaterial({partVersion: partVersion});
				}
			});			
		};
		
		// Temp method to support PROD issue related to Test Object mismatch for Migrated Orders!
		var saveTestObject = function(data) {
			var referenceId = data.material.referenceId;
			var outboundStartDate = data.materialRequest.outboundStartDate;
			materialLineModel.save({referenceId: referenceId,outboundStartDate:outboundStartDate}, {
				url : '/procurement/v1/materiallines/' + materialLineModel.get('id') + '/referenceId', // New REST service!
				success : function() {
					Gloria.MaterialApp.trigger('MaterialDetails:saved', true);
				}
			});
		};
		
		/**
		 * Update Material.
		 */
		var updateMaterial = function(data) {
			if(materialModel.get('partVersion') !== data.partVersion) {
				materialModel.save(data, {
					url : '/procurement/v1/materials/' + materialLineModel.get('materialId'),
					success : function() {
						Gloria.MaterialApp.trigger('MaterialDetails:saved', true);
					}
				});
			} else {
				Gloria.MaterialApp.trigger('MaterialDetails:saved', true);
			}
		};
		
		/**
		 * Show Pull Part Dialog
		 */
		var showPullPartDialog = function() {
			require([ 'views/material/overview/view/PullPartDialogView' ], function(PullPartDialogView) {
				var pullPartDialogView = new PullPartDialogView({
					model : materialLineModel
				});
				Gloria.basicModalLayout.content.show(pullPartDialogView);
			});
		};
		
		/**
		 * Pull Part
		 */
		var pullPart = function(model, data) {
			var dataAsQueryString = $.param(data);
			model.url = '/material/v1/materiallines/' + model.id + '/pull?' + dataAsQueryString;
			model.save(null, {
				success : function(response) {					
					Gloria.MaterialApp.trigger('MaterialRequestList:pulledPart');
				},
				validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        message : errorMessage
                    });
                }
			});
		};
		
		Controller.MaterialDetailsController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.MaterialApp, 'MaterialDetails:showaccordion', processAccordions);
				this.listenTo(Gloria.MaterialApp, 'MaterialDetails:save', saveMaterialLine);
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:pullPart:show', showPullPartDialog);
			    this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:pullPart', pullPart);
			    this.listenTo(Gloria.MaterialApp, 'MaterialDetails:save:testobject', saveTestObject);
			},

			control : function(id) {
				materialLineId = id;
				prepareMaterialDetailsView.call(this);
			},

			onDestroy : function() {
				materialLineId = null;
				materialDetailsView = null;
				materialLineModel = null;
				materialModel = null;
			}
		});
	});

	return Gloria.MaterialApp.Controller.MaterialDetailsController;
});
