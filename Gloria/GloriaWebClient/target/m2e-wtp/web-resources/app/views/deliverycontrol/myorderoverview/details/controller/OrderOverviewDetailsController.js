define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'utils/UserHelper',
		'utils/backbone/GloriaCollection',
		'utils/backbone/GloriaModel',
		'views/deliverycontrol/models/OrderLineModel',
		'views/deliverycontrol/myorderoverview/details/collection/MaterialCollection',
        'views/deliverycontrol/myorderoverview/details/collection/DeliveryNoteLineCollection',
        'views/deliverycontrol/myorderoverview/details/collection/OrderLineLogCollection',
        'views/deliverycontrol/myorderoverview/details/collection/OrderLogCollection',
        'collections/QualityDocCollection', 
        'collections/ProblemNoteDocCollection',
        'utils/backbone/GloriaPageableCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n,UserHelper, GloriaCollection, GloriaModel, 
		OrderLineModel, MaterialCollection, DeliveryNoteLineCollection, OrderLineLogCollection, OrderLogCollection,
		QualityDocCollection, ProblemNoteDocCollection, PageableCollection) {

	Gloria.module('DeliveryControlApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var module;
		var orderLineId;
		var isEditable;
		var orderLine;
		var deliveryScheduleCollection;
		var deliveryNoteLineCollection;
		var materialCollection;
		var orderLineLogCollection;
		var orderLogCollection;
		var qualityDocCollection;
		var problemNoteDocCollection;
		var orderLineDetailView;
		var poInfoLogView;		
		var isInternalOrder;
		var hasDeliveryRole;
		
		/**
		 * Prepare Order Line Details
		 * Initialize Data Source Objects which are going to be used as data transfer objects
		 * and set page layout.
		 */ 
		var prepareOrderLineDetails = function() {			
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function(options) {

			var userRoles = UserHelper.getInstance().getUserRoles();
			hasDeliveryRole = UserHelper.getInstance().checkPermission('DeliveryControlPartDetail', ['edit']);
            // if the referer page is material/linesoverview/linedetails/(:id)
			// isEditable is always false, so the detail page will be rendered in readonly mode regardless of user role. 
			isEditable = hasDeliveryRole && isEditable;
			
			orderLine = new OrderLineModel({id : orderLineId});
			
			deliveryScheduleCollection = new PageableCollection();
			
			materialCollection = new MaterialCollection();
			materialCollection.url = '/material/v1/orderlines/' + orderLineId + '/materials';
			
			deliveryNoteLineCollection = new DeliveryNoteLineCollection();
			
			orderLineLogCollection = new OrderLineLogCollection();
			orderLineLogCollection.url = '/material/v1/orderlines/' + orderLineId + '/orderlinelogs';
			
			orderLogCollection = new OrderLogCollection();
			orderLogCollection.url = '/material/v1/orderlines/' + orderLineId + '/orderlogs';
			
			qualityDocCollection = new QualityDocCollection();				
			problemNoteDocCollection = new ProblemNoteDocCollection();
		};
		
		/**
		 * Set Page Layout
		 */ 
		var setPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', {itemId : orderLineId});
			var thisModel = new GloriaModel({id: orderLineId});
			thisModel.save({}, {
				url : '/procurement/v1/orderlines/' + orderLineId + '/alerts'
			});
			require(['views/deliverycontrol/myorderoverview/details/view/OrderLineDetailView'], function(OrderLineDetailView) {			
				orderLineDetailView = new OrderLineDetailView({
					isEditable : isEditable,
					module : module,
					orderline : orderLine
				});
				orderLineDetailView.on('show', function() {
					showPartGeneralInfo();					
				}, this);
				Gloria.basicLayout.content.show(orderLineDetailView);			
			});
		};
		
		
		/**
		 * Show Part General Information
		 */ 
		var showPartGeneralInfo = function() {	
			processPartGeneralInfo();
		};
		
		/**
		 * Process Part General Information
		 */ 
		var processPartGeneralInfo = function() {
			require([ 'views/deliverycontrol/myorderoverview/details/view/PartGeneralInfoView' ],
			function(PartGeneralInfoView) {
				orderLine.fetch({
					success : function(model, res, settings) {
						if(settings.xhr.status == 204) { // Redirect to Page Not Found if resource is not available
							Gloria.trigger('Error:404');
							return;
						} else {
							if (orderLine.attributes.internalExternal == 'INTERNAL') {
							    isInternalOrder = true;
							} else {
								isInternalOrder = false;
							}
							var partGeneralInfoView = new PartGeneralInfoView({
								orderLineModel : orderLine,
								isInternalOrder : isInternalOrder,
								isEditable : isEditable,
								module: module
							});
							Gloria.trigger('showBreadcrumbView',  {partNumber : model.get('partNumber')});
							orderLineDetailView.partGeneralInfo.show(partGeneralInfoView);
							showPoInfoLog();
						}
					}
				});
			});
		};
		
		/**
		 * Show Log Information
		 */ 
		var showPoInfoLog = function() {			
			
			require(['views/deliverycontrol/myorderoverview/details/view/PoInfoLogView'], function(PoInfoLogView) {			
				poInfoLogView = new PoInfoLogView({
					isInternalOrder: isInternalOrder
				});
				poInfoLogView.on('show', function() {
					preparePoInfoLogContent('orderLineActionLog');
				}, this);
				orderLineDetailView.poInfoLog.show(poInfoLogView);
			});
		};
		
		/**
		 * Prepare Log Information Content
		 */ 
		var preparePoInfoLogContent = function(tab) {
			switch (tab) {
			case 'poActionLog':
				preparePoActionLog();
				break;
			case 'notesFromPurchase':
				prepareNotesFromPurchase();
				break;
			case 'notesFromProcurement':
				prepareNotesFromProcurement();
				break;
			default:
				prepareOrderLineActionLog();
				break;
			}
		};
		
		/**
		 * Prepare Order Line Action Log
		 */ 
		var prepareOrderLineActionLog = function() {
			require(['views/deliverycontrol/myorderoverview/details/view/OrderLineActionLogView'],
			function(OrderLineActionLogView) {
				var orderLineActionLogView = new OrderLineActionLogView({
					collection : orderLineLogCollection,
					isEditable : isEditable,
					module: module
				});
				orderLineActionLogView.on('show', function() {
					processOrderLineActionLog();
				}, this);
				poInfoLogView.orderLineActionLog.show(orderLineActionLogView);
			});
		};
		
		/**
		 * Process Order Line Action Log
		 */ 
		var processOrderLineActionLog = function() {
			orderLineLogCollection.fetch();
		};
		
		/**
		 * Prepare PO Action Log
		 */ 
		var preparePoActionLog = function() {
			require(['views/deliverycontrol/myorderoverview/details/view/PoActionLogView'],
			function(PoActionLog) {
				var poActionLog = new PoActionLog({
					isEditable : isEditable,
					module: module,
					collection : orderLogCollection
				});
				poActionLog.on('show', function() {
					processPoActionLog();
				}, this);
				poInfoLogView.poActionLog.show(poActionLog);
			});
		};
		
		/**
		 * Process PO Action Log
		 */ 
		var processPoActionLog = function() {
			orderLogCollection.fetch();
		};
		
		/**
		 * Prepare Notes From Purchase
		 */ 
		var prepareNotesFromPurchase = function() {
			require(['views/deliverycontrol/myorderoverview/details/view/PurchaseNotesView'],
			function(PurchaseNotesView) {
				var purchaseNotesView = new PurchaseNotesView({
					model : orderLine
				});
				poInfoLogView.notesFromPurchase.show(purchaseNotesView);
			});
		};
		
		/**
		 * Prepare Notes From Procurement
		 */ 
		var prepareNotesFromProcurement = function() {
			require(['views/deliverycontrol/myorderoverview/details/view/ProcurementNotesView'],
			function(ProcurementNotesView) {
				var procurementNotesView = new ProcurementNotesView({
					model : orderLine
				});
				poInfoLogView.notesFromProcurement.show(procurementNotesView);
			});
		};
		
		/**
		 * Prepare Delivery Lines Information
		 */ 
		var prepareDeliveryLinesInfo = function() {
			require(['views/deliverycontrol/myorderoverview/details/view/DeliveryLinePOInfoView'], function(DeliveryLinePOInfoView) {
				var deliveryLinePOInfoView = new DeliveryLinePOInfoView({
					model : orderLine,
					isEditable : isEditable,
					module: module
				});
				deliveryLinePOInfoView.on('show', function() {
					processDeliveryScheduleInfo(deliveryLinePOInfoView);
					processActualDeliveryInfo(deliveryLinePOInfoView);
				}, this);
				orderLineDetailView.poDelInfo.show(deliveryLinePOInfoView);
			});
		};
		
		/**
		 * Process Delivery Schedule Information
		 */ 
		var processDeliveryScheduleInfo = function(parentView) {
			deliveryScheduleCollection.fetch({
				url : '/material/v1/orderlines/' + orderLineId + '/deliveryschedules',
				success : function(collection) {
				    /*
					if(orderLine.get('orderStaChanged') || !orderLine.get('staAgreedDate')){
				        collection.each(function( model){
				            collection.get(model.get('id')).set('expectedDate', $('#staAgreedDate').val());
				        });
				    }
				    */
					if(collection.length > 0) {
						showDeliveryScheduleInfoView({
							parentView : parentView,
							collection : collection
						});
					}
				}
			});
		};
		
		/**
		 * Show Delivery Schedule Information
		 */ 
		var showDeliveryScheduleInfoView = function(options) {
			require(['views/deliverycontrol/myorderoverview/details/view/DeliveryScheduleView'], function(DeliveryScheduleView) {        	
	        	var deliveryScheduleView = new DeliveryScheduleView({					
						collection: options.collection,
						isEditable : isEditable,
						module: module
				});
	        	options.parentView.poDelSchedule.show(deliveryScheduleView);
	        });
		};
		
		/**
		 * Process Actually Delivery Information
		 */ 
		var processActualDeliveryInfo = function(parentView) {
			deliveryNoteLineCollection.fetch({
				url : '/material/v1/orderlines/' + orderLineId + '/deliverynotelines?status=RECEIVED',
				success : function(collection) {
					if(collection.length > 0) {
						showActualDeliveryInfo({
							parentView : parentView,
							collection : collection
						});
					}
				}
			});
		};
		
		/**
		 * Show Actually Delivery Information
		 */ 
		var showActualDeliveryInfo = function(options) {
			require(['views/deliverycontrol/myorderoverview/details/view/ActualDeliveryView'], function(ActualDeliveryView) {        	
	        	var actualDeliveryView = new ActualDeliveryView({					
						collection: options.collection
				});
	        	options.parentView.poActualDel.show(actualDeliveryView);
	        });
		};
		
		/**
		 * Prepare Procurement Information
		 */ 
		var prepareProcurementInfo = function() {
			require(['views/deliverycontrol/myorderoverview/details/view/ProcurementInfoView'], function(ProcurementInfoView) {
				var procurementInfoView = new ProcurementInfoView();
				procurementInfoView.on('show', function() {
					// procGenInfo & procReqInfo are two different regions, so these two views are called async way.
					prepareProcGeneralInfo({
						parentView : procurementInfoView, 
						orderLine : orderLine
					});
					prepareProcRequestInfo(procurementInfoView);
				}, this);
				orderLineDetailView.procInfo.show(procurementInfoView);
			});
		};
		
		/**
		 * Prepare Amendment Information
		 */ 
		var prepareAmendmentInfo = function() {
			var collection = new GloriaCollection();
			collection.url = '/material/v1/orderlines/' + orderLineId + '/orderlineversions?orderBy=versionNo';
			require(['views/deliverycontrol/myorderoverview/details/view/AmendmentGridView'], function(AmendmentGridView) {
				var amendmentGridView = new AmendmentGridView({
					collection : collection
				});
				amendmentGridView.on('show', function() {
					collection.fetch();
				}, this);
				orderLineDetailView.poAmdInfo.show(amendmentGridView);
			});
		};
		
		/**
		 * Prepare History Information.
		 */
		var prepareHistoryInfo = function() {
			var historyCollection = new GloriaCollection();
			historyCollection.fetch({
				url : '/common/v1/orderlines/' + orderLineId + '/traceabilitys',
				success : function(response) {
					showHistoryInfo(response);
				}
			});
		};

		/**
		 * Show History Information.
		 */
		var showHistoryInfo = function(collection) {
			require([ 'views/deliverycontrol/myorderoverview/details/view/TraceabilityGridView' ], function(TraceabilityGridView) {
				var traceabilityGridView = new TraceabilityGridView({
					collection : collection
				});
				orderLineDetailView.historyInfo.show(traceabilityGridView);
			});
		};
		
		/**
		 * Prepare Procurement Information
		 */ 
		var prepareProcGeneralInfo = function(options) {
			require(['views/deliverycontrol/myorderoverview/details/view/ProcurementGenInfoView'], function(ProcurementGenInfoView){
				var procurementGenInfoView = new ProcurementGenInfoView({
					model : options.orderLine
				});
				options.parentView.procGenInfo.show(procurementGenInfoView);
			});
		};
		
		/**
		 * Prepare Procurement Request Information
		 */ 
		var prepareProcRequestInfo = function(parentView) {
			materialCollection.fetch({
				success : function(col) {
					showProcRequestInfo(parentView, col);
				}
			});
		};
		
		/**
		 * Show Procurement Request Information
		 */
		var showProcRequestInfo = function(parentView, col) {
			require(['views/deliverycontrol/myorderoverview/details/view/ProcurementReqInfoView'], function(ProcurementReqInfoView){
				var procurementReqInfoView = new ProcurementReqInfoView({
					collection : col
				});
				parentView.procReqInfo.show(procurementReqInfoView);
			});
		};

		/**
		 * After Order Line Save
		 */
		var afterOrderLineSave = function(deliveryschedulesInfo, orderStaDate, staAgreedDate, orderStaChanged) {
			var update = function(deliveryScheduleCollection) {
				Backbone.sync('update', deliveryScheduleCollection, {
					url : '/material/v1/orderlines/' + orderLineId + '/deliveryschedules',
					success : function() {
						Gloria.DeliveryControlApp.trigger('OrderLineDetail:saved', true);
					},
					error : function() {
						Gloria.DeliveryControlApp.trigger('OrderLineDetail:saved', false);
					},
                    validationError : function(errorMessage, errors) {
                        Gloria.trigger('showAppMessageView', {
                            type : 'error',
                            title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
                            message : errorMessage
                        });
                    }

				});
			};
			if (deliveryschedulesInfo && deliveryschedulesInfo.length > 0) {								
				deliveryScheduleCollection.fetch({
					url : '/material/v1/orderlines/' + orderLineId + '/deliveryschedules',
					success : function(collection) {						
						_.each(deliveryschedulesInfo, function(info) {
							collection.get(info.id).set(info);
						});
						update(collection);
					}
				});
				return;				
			} else if ((orderStaChanged || !staAgreedDate) && ($('#staAgreedDate').val() != orderStaDate)) {
				deliveryScheduleCollection.fetch({
					url : '/material/v1/orderlines/' + orderLineId + '/deliveryschedules',
					success : function(collection) {
						collection.each(function(model) {
							collection.get(model.get('id')).set('expectedDate', orderLine.get('staAgreedDate'));
						});
						update(collection);
					}
				});
				return;
			}
			update(deliveryScheduleCollection);
		};
		
		/**
		 * Save Order Line Information
		 */
		var saveOrderLineDetailView = function(orderLineInfo, deliveryschedulesInfo, orderStaDate) {
		    var staAgreedDate = orderLine.get('staAgreedDate');
		    var orderStaChanged = orderLine.get('orderStaChanged');
		    var contentEdited  = orderLine.set("contentEdited","true");
			orderLine.save(orderLineInfo, {
				url : '/procurement/v1/orderlines/' + orderLine.id + (module == 'completed' ? '/revoke' : ''),
			    validate : false,
				success : function() {
					Gloria.DeliveryControlApp.trigger('orderLineInfo:saved', deliveryschedulesInfo, orderStaDate, staAgreedDate, orderStaChanged);
				},
				error : function() {
					Gloria.DeliveryControlApp.trigger('OrderLineDetail:saved', false);
				},
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
                        message : errorMessage
                    });
                }

			});
		};
		
		/**
		 * Ignore Deviation
		 */
		var ignoreDeviation = function() {
			var ignoreDeliveryDeviationCollection = new  GloriaCollection();
			ignoreDeliveryDeviationCollection = orderLine;
			ignoreDeliveryDeviationCollection.set('deliveryDeviation', false,  {silent : true});
			ignoreDeliveryDeviationCollection.url  = '/procurement/v1/orderlines/'+orderLineId+'/ignore';
			ignoreDeliveryDeviationCollection.save({}, {
			    validate : false,
				success : function() {
					
				},
				error : function() {
					Gloria.DeliveryControlApp.trigger('OrderLineDetail:saved', false);
				},
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
                        message : errorMessage
                    });
                }

			});
		};
		
		/**
		 * Add Order Line Information
		 */
		var addOrderLineLog = function(logMessage) {
			require(['views/deliverycontrol/myorderoverview/details/model/OrderLineLogModel'], function(OrderLineLogModel) {        	
	        	var orderLineLogModel = new OrderLineLogModel();
	        	orderLineLogModel.url = '/material/v1/orderlines/' + orderLineId + '/orderlinelog';
	        	var dataToBeSaved = {
	        		eventValue : logMessage
	        	};
	        	orderLineLogModel.save(dataToBeSaved, {
	        		success : function(model) {
	        			orderLineLogCollection.add(model, {at : 0});
					},
					error : function() {
						Gloria.DeliveryControlApp.trigger('OrderLineDetail:addedOrderLineLog', false);
					}
	        	});
	        });
		};
		
		/**
		 * Add Order Information
		 */
		var addOrderLog = function(logMessage) {
			require(['views/deliverycontrol/myorderoverview/details/model/OrderLogModel'], function(OrderLogModel) {        	
	        	var orderLogModel = new OrderLogModel();
	        	orderLogModel.url = '/material/v1/orderlines/' + orderLineId + '/orderlog';
	        	var dataToBeSaved = {
	        		eventValue : logMessage
	        	};
	        	orderLogModel.save(dataToBeSaved, {
	        		success : function(model) {
	        			orderLogCollection.add(model, {at : 0});
					},
					error : function() {
						Gloria.DeliveryControlApp.trigger('OrderLineDetail:addedOrderLog', false);
					}
	        	});
	        });
		};
		
		/**
		 * Prepare Documents Information
		 */
		var prepareDocumentsInfo = function(id) {
			require(['views/deliverycontrol/myorderoverview/details/view/DocumentInformationView'], function(DocumentInformationView) {
				var documentInformationView = new DocumentInformationView({
					model : deliveryNoteLineCollection.get(id)
				});
				documentInformationView.on('show', function() {
					prepareQualityDocumentInfo(documentInformationView, id);
				}, this);
				if (orderLineDetailView.documentDiv) {
					orderLineDetailView.documentDiv.empty();
				}
				var docDivId = '#document' + id;
				orderLineDetailView.addRegion('documentDiv', docDivId);
				orderLineDetailView.documentDiv.show(documentInformationView);
			});
		};

		/**
		 * Prepare Quality Documents Information
		 */
		var prepareQualityDocumentInfo = function(parentView, id) {
			require(['views/deliverycontrol/myorderoverview/details/view/QualityDocView'], function(QualityDocView) {
				var qualityDocView = new QualityDocView({
					collection : qualityDocCollection
				});
				qualityDocView.on('show', function() {
					processQualityDocumentInformation(id);
				}, this);
				parentView.qualityDocInfo.show(qualityDocView);
			});
		};
		
		/**
		 * Process Quality Documents Information
		 */
		var processQualityDocumentInformation = function(deliveryNoteLineId) {
			qualityDocCollection.url = '/documents/v1/deliverynotelines/' + deliveryNoteLineId + '/receivedocs';
			qualityDocCollection.fetch();
		};
		
		/**
		 * Prepare Problem Description Information
		 */
		var prepareProblemDescInfo = function(id) {
			require(['views/deliverycontrol/myorderoverview/details/view/ProblemInformationView'], function(ProblemInformationView) {
				var problemInformationView = new ProblemInformationView({
					model : deliveryNoteLineCollection.get(id)
				});
				problemInformationView.on('show', function() {
					prepareProblemDescriptionInfo(problemInformationView, id);
				}, this);
				if (problemInformationView.documentDiv) {
					problemInformationView.documentDiv.empty();
				}
				var probDivId = '#probDesc' + id;
				orderLineDetailView.addRegion('probDescDiv', probDivId);
				orderLineDetailView.probDescDiv.show(problemInformationView);
			});
		};
		
		/**
		 * Prepare Problem Description Information
		 */
		var prepareProblemDescriptionInfo = function(parentView, id) {
			require(['views/deliverycontrol/myorderoverview/details/view/ProblemDocView'], function(ProblemDocView) {
				var problemDocView = new ProblemDocView({
					collection : problemNoteDocCollection
				});
				problemDocView.on('show', function() {
					processProblemDocInformation(id);
				}, this);
				parentView.problemDocInfo.show(problemDocView);
			});
		};
		
		/**
		 * Process Problem Description Information
		 */
		var processProblemDocInformation = function(deliveryNoteLineId) {
			problemNoteDocCollection.url = '/documents/v1/deliverynotelines/' + deliveryNoteLineId + '/problemdocs';
			problemNoteDocCollection.fetch();
		};
		
		/**
		 * Prepare DS Documents Information
		 */
		var prepareDSDocumentsInfo = function(id) {
			require(['views/deliverycontrol/myorderoverview/details/view/DeliverySheduleDocView',
			         'views/deliverycontrol/myorderoverview/details/collection/DeliveryScheduleDocCollection'],
			function(DeliverySheduleDocView, DeliveryScheduleDocCollection) {
				var deliveryScheduleDocCollection = new DeliveryScheduleDocCollection();
				deliveryScheduleDocCollection.url = '/documents/v1/deliveryschedules/' + id + '/attacheddocs';				
				var docDivId = '#attachedDoc' + id;
				orderLineDetailView.addRegion('attachedDoc', docDivId);
				var deliverySheduleDocView = new DeliverySheduleDocView({
					collection : deliveryScheduleDocCollection,
					isEditable : isEditable,
					module: module
				});
				deliverySheduleDocView.on('show', function() {
					processDeliverySheduleDocInformation(deliveryScheduleDocCollection);
				}, this);
				orderLineDetailView.attachedDoc.show(deliverySheduleDocView);
			});
		};
		
		/**
		 * Process Delivery Shedule Doc Information
		 */
		var processDeliverySheduleDocInformation = function(docCollection) {
			docCollection.fetch();
		};
		
		/**
		 * Delete Attached Doc
		 */
		var deleteAttachedDoc = function(docCollection, docId) {
			docCollection.get(docId).destroy({
				url : '/documents/v1/attacheddocs/' + docId,
				success : function(response) {
					docCollection.remove(docCollection.get(docId));
				}
			});
		};
		
		/**
		* Go back to respective (internal/external) my order overview page
		*/
		var goBackToMyOrderOverview = function(orderLineInfo) {
		    var contentEdited  = orderLine.set("contentEdited","true");
            orderLine.save(null, {
                url : '/procurement/v1/orderlines/' + orderLine.id + (module == 'completed' ? '/revoke' : ''),
                validate : false,
                success : function() {
                    Gloria.DeliveryControlApp.trigger('orderLineInfo:saved');
                },
                error : function() {
                    Gloria.DeliveryControlApp.trigger('OrderLineDetail:saved', false);
                },
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'), 
                        message : errorMessage
                    });
                }

            });
		    
			Backbone.history.navigate('deliverycontrol/myOrderOverview/' + (isInternalOrder ? 'internal' : 'external'), {
				trigger : true
			});
		};
		
		Controller.OrderOverviewDetailsController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {			
				this.listenTo(Gloria.DeliveryControlApp, 'orderlinedetail:showaccordion:deliveryLinesInformation', prepareDeliveryLinesInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'orderlinedetail:showaccordion:procurementInformation', prepareProcurementInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'orderlinedetail:showaccordion:amendmentInformation', prepareAmendmentInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'orderlinedetail:showaccordion:historyInformation', prepareHistoryInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:save', saveOrderLineDetailView);
				this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:showPoInfoLog', preparePoInfoLogContent);
				this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:ignore', ignoreDeviation);
				this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:addOrderLineLog', addOrderLineLog);
				this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:addOrderLog', addOrderLog);
				this.listenTo(Gloria.DeliveryControlApp, 'DeliverySchedule:showDocuments', prepareDSDocumentsInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'ActualDelivery:showDocuments', prepareDocumentsInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'ActualDelivery:showProblemDesc', prepareProblemDescInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'DeliverySchedule:deleteAttachedDoc', deleteAttachedDoc);
				this.listenTo(Gloria.DeliveryControlApp, 'orderLineInfo:saved', afterOrderLineSave);
				this.listenTo(Gloria.DeliveryControlApp, 'orderlinedetail:back', goBackToMyOrderOverview);
			},

			control : function(options) {
				module = options.module;
				orderLineId = options.orderLineId;
				isEditable = options.editable;
				prepareOrderLineDetails.call(this);
			},
            
            onDestroy: function() {
            	module = null;
                orderLineId = null;
                isEditable = null;
                orderLine = null;
                deliveryScheduleCollection = null;
                deliveryNoteLineCollection = null;
                materialCollection = null;
                orderLineLogCollection = null;
                orderLogCollection = null;
                qualityDocCollection = null;
                problemNoteDocCollection = null;
                orderLineDetailView = null;
                poInfoLogView = null;
                isInternalOrder = null;
                hasDeliveryRole = null;
            }
		});
	});

	return Gloria.DeliveryControlApp.Controller.OrderOverviewDetailsController;
});
