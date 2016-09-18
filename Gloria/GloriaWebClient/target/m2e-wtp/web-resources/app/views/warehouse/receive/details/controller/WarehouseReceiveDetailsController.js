define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
        'i18next',
        'utils/UserHelper',
        'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, Collection) {
    
	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var deliveryNoteLineId = undefined;
		var deliveryNoteLineModel = undefined;
		var qualityDocCollection = undefined;
		var problemNoteDocCollection = undefined;
		var receiveDetailsView = undefined;
		
		/**
		 * Prepare Warehouse Receive Details.
		 * Initialize Data Source Objects which are going to be used as data transfer objects
		 * and set page layout.
		 */ 
		var prepareWarehouseReceiveDetails = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			require(['models/DeliveryNoteLineModel', 'collections/QualityDocCollection', 'collections/ProblemNoteDocCollection'],
				function(DeliveryNoteLineModel, QualityDocCollection, ProblemNoteDocCollection) {
				deliveryNoteLineModel = new DeliveryNoteLineModel({
					id : deliveryNoteLineId
				});
				qualityDocCollection = new QualityDocCollection();
				qualityDocCollection.url = '/documents/v1/deliverynotelines/' + deliveryNoteLineId + '/receivedocs';
				problemNoteDocCollection = new ProblemNoteDocCollection();
				problemNoteDocCollection.url = '/documents/v1/deliverynotelines/' + deliveryNoteLineId + '/problemdocs';
			});
		};
		
		/**
		 * Set Page Layout
		 */ 
		var setPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', deliveryNoteLineId ? {itemId : deliveryNoteLineId} : null);
			require([ 'views/warehouse/receive/details/view/ReceiveDetailsView' ], function(ReceiveDetailsView) {
				receiveDetailsView = new ReceiveDetailsView();
				receiveDetailsView.on('show', function() {
					processWarehouseReceiveDetails();
				}, this);
				Gloria.basicLayout.content.show(receiveDetailsView);
			});
		};
		
		/**
		 * Process Warehouse Receive Details
		 */
		var processWarehouseReceiveDetails = function() {
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
		                	if(model && (model.get('receiveType') == 'REGULAR' || model.get('receiveType') == 'TRANSFER')) {
		                		// Delivery Subline information is required to show Delivery Note Number and Validation against Damaged Quantity.
		                		deliveryNoteLineModel.directsends.fetch({
		            	            url: '/material/v1/deliverynotelines/' + model.id + '/deliverynotesublines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(),
		                    		async : false
		            	        });
		                	}
		                	Gloria.WarehouseApp.trigger('ReceiveDetails:hideAccordion', model);
		                    Gloria.WarehouseApp.trigger('ReceiveDetails:showaccordion', model);
						}
	                }
	            });
			});
		};
		
		var showReceiveDetails = function(model) {
			var receiveType = model.get('receiveType') && model.get('receiveType').toUpperCase();
			switch (receiveType) {
			case 'TRANSFER':
				showPartInformation(model);
				showTransferInformation(model);	
				showReceiveInformation(model);
				showDocumentInformation(model);
				showProblemInformation(model);
				break;
			case 'RETURN':
				showPartInformation(model);
				showShipInformation(model);
				showReturnInformation(model);
				showProblemInformation(model);
				break;
			case 'RETURN_TRANSFER':
				showPartInformation(model);
				showShipInformation(model);
				showReturnInformation(model);
				showProblemInformation(model);
				break;
			default: // REGULAR
				showPartInformation(model);
				showOrderInformation(model);
				showReceiveInformation(model);
				showDocumentInformation(model);
				showProblemInformation(model);
				break;
			}
		};
		
		var showPartInformation = function(model) {
			require(['views/warehouse/receive/details/view/PartInformationView'], function(PartInformationView) {
				var partInformationView = new PartInformationView({
					model : model
				});
				receiveDetailsView.partInfoContent.show(partInformationView);
			});
		};
		
		var showTransferInformation = function(model) {
			require(['views/warehouse/receive/details/view/TransferInformationView'], function(TransferInformationView) {
				var transferInformationView = new TransferInformationView({
					model : model
				});
				receiveDetailsView.transferInfoContent.show(transferInformationView);
			});
		};
		
		var showShipInformation = function(model) {
			require(['views/warehouse/receive/details/view/ShipInformationView'], function(ShipInformationView) {
				var shipInformationView = new ShipInformationView({
					model : model
				});
				receiveDetailsView.shipInfoContent.show(shipInformationView);
			});
		};
		
		var showReturnInformation = function(model) {
			require(['views/warehouse/receive/details/view/ReturnInformationView'], function(ReturnInformationView) {
				var returnInformationView = new ReturnInformationView({
					model : model
				});
				receiveDetailsView.returnInfoContent.show(returnInformationView);
			});
		};
		
		var showOrderInformation = function(model) {
			require(['views/warehouse/receive/details/view/OrderInformationView'], function(OrderInformationView) {
				var orderInformationView = new OrderInformationView({
					model : model
				});
				receiveDetailsView.orderInfoContent.show(orderInformationView);
			});
		};
		
		var showReceiveInformation = function(model) {
			require(['views/warehouse/receive/details/view/ReceiveInformationView'], function(ReceiveInformationView) {
				var receiveInformationView = new ReceiveInformationView({
					model : model
				});
				receiveDetailsView.receiveInfoContent.show(receiveInformationView);
			});
		};
		
		var showDocumentInformation = function(model) {
			require(['views/warehouse/receive/details/view/DocumentInformationView'], function(DocumentInformationView) {
				var documentInformationView = new DocumentInformationView({
					model : model,
					collection : qualityDocCollection
				});
				documentInformationView.on('show', function() {
					prepareDocumentInformation(documentInformationView, model);
				}, this);
				receiveDetailsView.documentsInfoContent.show(documentInformationView);
			});
		};
		
		/**
		 * Prepare Document Information
		 */
		var prepareDocumentInformation = function(parentView, model) {
			require(['views/warehouse/receive/details/view/QualityDocView'], function(QualityDocView) {
				var qualityDocView = new QualityDocView({
					model : model,
					collection : qualityDocCollection
				});
				qualityDocView.on('show', function() {
					processDocumentInformation();
				}, this);
				parentView.qualityDocInfo.show(qualityDocView);
			});
		};
		
		/**
		 * Process Document Information
		 */
		var processDocumentInformation = function() {
			qualityDocCollection.fetch();
		};
		
		/**
		 * Delete Quality Document
		 */
		var deleteQualityDoc = function(docId) {
			qualityDocCollection.get(docId).destroy({
				url : '/documents/v1/receivedocs/' + docId,
				success : function(response) {
					qualityDocCollection.remove(qualityDocCollection.get(docId));
				}
			});
		};
		
		var showProblemInformation = function(model) {
			require(['views/warehouse/receive/details/view/ProblemInformationView'], function(ProblemInformationView) {
				var problemInformationView = new ProblemInformationView({
					model : model,
					collection : problemNoteDocCollection
				});
				problemInformationView.on('show', function() {
					prepareProblemInformation(problemInformationView, model);
				}, this);
				receiveDetailsView.problemInfoContent.show(problemInformationView);
			});
		};
		
		/**
		 * Prepare Problem Information
		 */
		var prepareProblemInformation = function(parentView, model) {
			require(['views/warehouse/receive/details/view/ProblemDocView'], function(ProblemDocView) {
				var problemDocView = new ProblemDocView({
					model : model,
					collection : problemNoteDocCollection
				});
				problemDocView.on('show', function() {
					processProblemInformation();
				}, this);
				parentView.problemDocInfo.show(problemDocView);
			});
		};
		
		/**
		 * Process Problem Information
		 */
		var processProblemInformation = function() {
			problemNoteDocCollection.fetch();
		};
		
		
		/**
		 * Delete Problem Document
		 */
		var deleteProblemDoc = function(docId) {
			problemNoteDocCollection.get(docId).destroy({
				url : '/documents/v1/problemdocs/' + docId,
				success : function(response) {
					problemNoteDocCollection.remove(problemNoteDocCollection.get(docId));
				}
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
		
		var saveReceiveDetails = function(data) {
			deliveryNoteLineModel.set(data, {silent : true});
			var isChanged = isChangedByIgnoringType(deliveryNoteLineModel._previousAttributes, deliveryNoteLineModel.changedAttributes());
			if(isChanged) {
				deliveryNoteLineModel.save({receivedDetailsUpdated : true}, {
					success : function() {
						Gloria.trigger('goToPreviousRoute');
					},
					validationError: function(errorMessage) {
						Gloria.trigger('showAppMessageView', { 
				            type : 'error',
				            title : i18n.t('errormessages:general.title'),
				            message: errorMessage
				        });
					}
				});
			} else { // No need to call server!
				Gloria.trigger('goToPreviousRoute');
			}
		};
		
		var preparePartLabel = function() {
			require(['views/warehouse/receive/details/view/PrintPLModalView'], function(PrintPLModalView) {
				var printPLModalView = new PrintPLModalView({
					model : deliveryNoteLineModel
				});
				Gloria.basicModalLayout.content.show(printPLModalView);
			});
		};
		
		var printPartLabel = function(model, printQty) {
		    var collection = new Collection();
		    collection.add(model);
			collection.save({
				url : '/common/v1/deliverynotelines/partlabels?whSiteId='
					+ UserHelper.getInstance().getDefaultWarehouse() + (printQty ? ('&quantity=' + printQty) : ''),
        		success : function(response) {
        			var messages = new Array();
					var item = {
							message : i18n.t('Gloria.i18n.warehouse.receive.details.printSuccessfully')
						};
					messages.push(item);
        			Gloria.trigger('showAppMessageView', {
		    			type : 'success',
		    			message : messages
		    		});
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
		
		Controller.WarehouseReceiveDetailsController = Marionette.Controller.extend({
	       
	    	initialize : function() {
	            this.initializeListeners();
	        },
	        
	        initializeListeners : function() {
	        	this.listenTo(Gloria.WarehouseApp, 'ReceiveDetails:showaccordion', showReceiveDetails);
	        	this.listenTo(Gloria.WarehouseApp, 'ReceiveDetails:DeleteQualityDoc', deleteQualityDoc);
	        	this.listenTo(Gloria.WarehouseApp, 'ReceiveDetails:DeleteProblemDoc', deleteProblemDoc);
	        	this.listenTo(Gloria.WarehouseApp, 'ReceiveDetails:save', saveReceiveDetails);
	        	this.listenTo(Gloria.WarehouseApp, 'ReceiveDetails:printPL', preparePartLabel);
	        	this.listenTo(Gloria.WarehouseApp, 'ReceiveDetails:printPL:print', printPartLabel);
	        },
	
	        control: function(id) {
	        	if(!id) {
	        		throw new TypeError('id must be supplied!');
	        	}
	        	deliveryNoteLineId = id;
	        	prepareWarehouseReceiveDetails.call(this);
	        },
	        
	        onDestroy: function() {
	            deliveryNoteLineId = null;
	            deliveryNoteLineModel = null;
	            qualityDocCollection = null;
	            problemNoteDocCollection = null;	            
	            receiveDetailsView = null;
	        }
	    });
	});
	
	return Gloria.WarehouseApp.Controller.WarehouseReceiveDetailsController;
});
