define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'i18next'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n) {
    
	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var deliveryNoteLineId;
		var deliveryNoteLineModel;
		var qualityDocCollection;
		var problemNoteDocCollection;
		var inspectionDocCollection;
		var qualityinspectionDetailsView;
		
		/**
		 * Prepare Warehouse QualityInspection Details.
		 * Initialize Data Source Objects which are going to be used as data transfer objects
		 * and set page layout.
		 */ 
		var prepareWarehouseQualityInspectionDetails = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			require(['models/DeliveryNoteLineModel', 'collections/QualityDocCollection', 'collections/ProblemNoteDocCollection', 'collections/InspectionDocCollection'],
				function(DeliveryNoteLineModel, QualityDocCollection, ProblemNoteDocCollection, InspectionDocCollection) {
				deliveryNoteLineModel = new DeliveryNoteLineModel({
					id : deliveryNoteLineId
				});
				qualityDocCollection = new QualityDocCollection();
				qualityDocCollection.url = '/documents/v1/deliverynotelines/' + deliveryNoteLineId + '/receivedocs';
				problemNoteDocCollection = new ProblemNoteDocCollection();
				problemNoteDocCollection.url = '/documents/v1/deliverynotelines/' + deliveryNoteLineId + '/problemdocs';
				inspectionDocCollection = new InspectionDocCollection();
				inspectionDocCollection.url = '/documents/v1/deliverynotelines/' + deliveryNoteLineId + '/qidocs';
			});
		};
		
		/**
		 * Set Page Layout
		 */ 
		var setPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', deliveryNoteLineId ? {itemId : deliveryNoteLineId} : null);
			require([ 'views/warehouse/qualityinspection/details/view/QualityInspectionDetailsView' ], function(QualityInspectionDetailsView) {
				qualityinspectionDetailsView = new QualityInspectionDetailsView();
				qualityinspectionDetailsView.on('show', function() {
					processQualityInspectionDetails();
				}, this);
				Gloria.basicLayout.content.show(qualityinspectionDetailsView);
			});
		};
		
		/**
		 * Process Warehouse QualityInspection Details
		 */
		var processQualityInspectionDetails = function() {
		    require(['models/DeliveryNoteLineModel'], function(DeliveryNoteLineModel) {
		        if(!deliveryNoteLineModel) {
	                deliveryNoteLineModel = new DeliveryNoteLineModel({
	                    id : deliveryNoteLineId
	                });
	            }
	            deliveryNoteLineModel.fetch({
	                success : function(model, res, settings) {
						if(settings.xhr.status == 204) { // Redirect to Page Not Found if resource is not available
							Gloria.trigger('Error:404');
							return;
						} else {
							Gloria.trigger('showBreadcrumbView',  {partNumber : model.get('partNumber')});
							Gloria.WarehouseApp.trigger('QualityInspectionDetails:showaccordion', model);
						}
	                }
	            }); 
		    });		    
		};
		
		/**
		 * Show Quality Inspection Details
		 */
		var showQualityInspectionDetails = function(model) {
			showPartInformation(model);	
			showDocumentInformation(model);
			showProblemDescAndAttachmentInformation(model);
			showProblemInformation(model);
		};
		
		/**
		 * Show Part Information
		 */
		var showPartInformation = function(model) {
			require(['views/warehouse/qualityinspection/details/view/QIDetailPartInformationView'], function(QIDetailPartInformationView) {
				var qiIDetailPartInformationView = new QIDetailPartInformationView({
					model : model
				});
				qualityinspectionDetailsView.partInfoContent.show(qiIDetailPartInformationView);
			});
		};		
		
		/**
		 * Show Document Information
		 */
		var showDocumentInformation = function(model) {
			require(['views/warehouse/qualityinspection/details/view/QIDetailDocumentInformationView'], function(QIDetailDocumentInformationView) {
				var qiDetailDocumentInformationView = new QIDetailDocumentInformationView({
					model : model,
					collection : qualityDocCollection
				});
				qiDetailDocumentInformationView.on('show', function() {
					prepareDocumentInformation(qiDetailDocumentInformationView, model);
				}, this);
				qualityinspectionDetailsView.documentsInfoContent.show(qiDetailDocumentInformationView);
			});
		};
		
		/**
		 * Prepare Document Information
		 */
		var prepareDocumentInformation = function(parentView, model) {
			require(['views/warehouse/qualityinspection/details/view/QIDetailQualityDocView'], function(QIDetailQualityDocView) {
				var qiDetailQualityDocView = new QIDetailQualityDocView({
					model : model,
					collection : qualityDocCollection
				});
				qiDetailQualityDocView.on('show', function() {
					processDocumentInformation();
				}, this);
				parentView.qualityDocInfo.show(qiDetailQualityDocView);
			});
		};
		
		/**
		 * Process Document Information
		 */
		var processDocumentInformation = function() {
			qualityDocCollection.fetch();
		};
		
		/**
		 * Show Problem Desc And Attachment Information
		 */
		var showProblemDescAndAttachmentInformation = function(model) {
			require(['views/warehouse/qualityinspection/details/view/QIDetailProblemInformationView'], function(QIDetailProblemInformationView) {
				var qiDetailProblemInformationView = new QIDetailProblemInformationView({
					model : model,
					collection : problemNoteDocCollection
				});
				qiDetailProblemInformationView.on('show', function() {
					prepareProblemDescAndAttachmentInformation(qiDetailProblemInformationView, model);
				}, this);
				qualityinspectionDetailsView.problemDescAndAttachmentInfoContent.show(qiDetailProblemInformationView);
			});
		};
		
		/**
		 * Prepare Problem Desc And Attachment Information
		 */
		var prepareProblemDescAndAttachmentInformation = function(parentView, model) {
			require(['views/warehouse/qualityinspection/details/view/QIDetailProblemDocView'], function(QIDetailProblemDocView) {
				var qiDetailProblemDocView = new QIDetailProblemDocView({
					model : model,
					collection : problemNoteDocCollection
				});
				qiDetailProblemDocView.on('show', function() {
					processProblemDescAndAttachmentInformation();
				}, this);
				parentView.problemDocInfo.show(qiDetailProblemDocView);
			});
		};
		
		/**
		 * Process Problem Desc And Attachment Information
		 */
		var processProblemDescAndAttachmentInformation = function() {
			problemNoteDocCollection.fetch();
		};
		
		/**
		 * Show Problem Information
		 */
		var showProblemInformation = function(model) {
			require(['views/warehouse/qualityinspection/details/view/QIDetailInspectionInformationView'], function(QIDetailInspectionInformationView) {
				var qiDetailInspectionInformationView = new QIDetailInspectionInformationView({
					model : model,
					collection : inspectionDocCollection
				});
				qiDetailInspectionInformationView.on('show', function() {
					prepareProblemInformation(qiDetailInspectionInformationView, model);
				}, this);
				qualityinspectionDetailsView.inspectionInfoContent.show(qiDetailInspectionInformationView);
			});
		};
		
		/**
		 * Prepare Problem Information
		 */
		var prepareProblemInformation = function(parentView, model) {
			require(['views/warehouse/qualityinspection/details/view/QIDetailInspectionDocView'], function(QIDetailInspectionDocView) {
				var qiDetailInspectionDocView = new QIDetailInspectionDocView({
					model : model,
					collection : inspectionDocCollection
				});
				qiDetailInspectionDocView.on('show', function() {
					processProblemInformation();
				}, this);
				parentView.inspectionDocInfo.show(qiDetailInspectionDocView);
			});
		};
		
		/**
		 * Process Problem Information
		 */
		var processProblemInformation = function() {
			inspectionDocCollection.fetch();
		};
		
		
		/**
		 * Delete Problem Document
		 */
		var deleteProblemDoc = function(docId) {
			inspectionDocCollection.get(docId).destroy({
				url : '/documents/v1/qidocs/' + docId,
				success : function(response) {
					inspectionDocCollection.remove(inspectionDocCollection.get(docId));
				}
			});
		};
		
		/**
		 * isChangedByIgnoringType
		 */
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
		 * Save Quality Inspection Details
		 */
		var saveQualityInspectionDetails = function(data) {	
			deliveryNoteLineModel.set(data);
			var isChanged = isChangedByIgnoringType(deliveryNoteLineModel._previousAttributes, deliveryNoteLineModel.changedAttributes());
			if(isChanged) {
				deliveryNoteLineModel.save({qiDetailsUpdated : true}, {
					validate : false,
					success : function(response) {
						Gloria.trigger('goToPreviousRoute');
					},
					validationError : function(errorMessage, errors) {
                        Gloria.trigger('showAppMessageView', {
                            type : 'error',
                            title : i18n.t('errormessages:general.title'),
                            message : errorMessage
                        });
                    }
				});
			} else { // No need to call server!
				Gloria.trigger('goToPreviousRoute');
			}
		};
	
		Controller.QualityInspectionDetailsController = Marionette.Controller.extend({
	       
	    	initialize : function() {
	            this.initializeListeners();
	        },
	        
	        initializeListeners : function() {
	        	this.listenTo(Gloria.WarehouseApp, 'QualityInspectionDetails:showaccordion', showQualityInspectionDetails);
	        	this.listenTo(Gloria.WarehouseApp, 'QualityInspectionDetails:DeleteProblemDoc', deleteProblemDoc, this);
	        	this.listenTo(Gloria.WarehouseApp, 'QualityInspectionDetails:save', saveQualityInspectionDetails, this);
	        },
	
	        control: function(id) {
	        	if(!id) {
	        		throw new TypeError('id must be supplied!');
	        	}
	        	deliveryNoteLineId = id;
	        	prepareWarehouseQualityInspectionDetails.call(this);
	        },
	       
			onDestroy: function() {
				deliveryNoteLineId = null;
				deliveryNoteLineModel = null;
				qualityDocCollection = null;
				problemNoteDocCollection = null;
				inspectionDocCollection = null;
				qualityinspectionDetailsView = null;
			}
	    });
	});
	
	return Gloria.WarehouseApp.Controller.QualityInspectionDetailsController;
});
