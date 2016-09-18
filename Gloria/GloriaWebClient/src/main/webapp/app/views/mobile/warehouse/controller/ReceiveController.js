define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'i18next',
		'utils/UserHelper',
		'utils/backbone/GloriaPageableCollection',
		'utils/backbone/GloriaCollection',
		'utils/backbone/GloriaModel',
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, GloriaPageableCollection, Collection, Model) {

	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var module = undefined;
		
		var receiveLayout = undefined;
		var receiveView = undefined;
		var toReceiveView = undefined;
		var orderCollection = undefined;
		var orderModel = undefined;
		var transferReturnCollection = undefined;
		var transferReturnModel = undefined;
		var orderLineCollection = undefined;
		
		var deliveryNoteID = undefined;
		var deliveryNoteLineCollection = undefined;
		var deliveryNoteModel = undefined;
		
		var regularTabViewModel = undefined;
		var receiveType;

		/**
		 * Route : warehouse/receive/:module
		 * Prepare Receive Module (Regular, Transfer, Return)
		 */
		var prepareReceiveView = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};

		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			// Regular
			if(!orderCollection) {
				orderCollection = new Collection();
			}
			if (!orderModel) {
				orderModel = new Model();
			}
			// Transfer & Return
			if(!transferReturnCollection) {
				transferReturnCollection =  new GloriaPageableCollection([], {
					state : {
						pageSize : 5,
						currentPage : 1
					}
				});
			}
			if (!transferReturnModel) {
				transferReturnModel = new Model();
			}
			//
			if(!orderLineCollection) {
				orderLineCollection = new GloriaPageableCollection([], {
					state : {
						pageSize : 5,
						currentPage : 1
					}
				});
			}
			
			if(module == 'regular') {
				if (!regularTabViewModel) {
					regularTabViewModel = new Model();
				}
			} else {
				regularTabViewModel = null;
			}		
		};

		/**
		 * Set Page Layout
		 */
		var setPageLayout = function() {
			Gloria.basicLayout.content.reset();
			require([ 'views/mobile/warehouse/view/receive/ReceiveLayout' ], function(ReceiveLayout) {
				receiveLayout = new ReceiveLayout({
					module : module
				});
				receiveLayout.on('show', function() {
					showReceiveView();
				}, this);
				Gloria.basicLayout.content.show(receiveLayout);
			});
		};

		/**
		 * Show Receive View (Regular, Transfer, Return)
		 */
		var showReceiveView = function() {
			require([ 'views/mobile/warehouse/view/receive/ReceiveView' ], function(ReceiveView) {
				receiveView = new ReceiveView({
					module : module,
					orderModel : orderModel,
					transferReturnModel : transferReturnModel,
					deliveryNoteModel : deliveryNoteModel,
					regularTabViewModel: regularTabViewModel
				});
				if (module == 'regular') {
					receiveLayout.transferInfo.empty();
					receiveLayout.returnInfo.empty();
					receiveLayout.returntransferInfo.empty();
					receiveLayout.regularInfo.show(receiveView);
				} else if (module == 'transfer') {
					receiveLayout.regularInfo.empty();
					receiveLayout.returnInfo.empty();
					receiveLayout.returntransferInfo.empty();
					receiveLayout.transferInfo.show(receiveView);
				} else if (module == 'return') {
					receiveLayout.regularInfo.empty();
					receiveLayout.transferInfo.empty();
					receiveLayout.returntransferInfo.empty();
					receiveLayout.returnInfo.show(receiveView);
				} else if (module == 'returntransfer') {
					receiveLayout.regularInfo.empty();
					receiveLayout.transferInfo.empty();
					receiveLayout.returnInfo.empty();
					receiveLayout.returntransferInfo.show(receiveView);
				}
			});
		};
		
		/**
		 * Fetch Orderline Information (Regular, Transfer, Return)
		 */
		var fetchOrderline = function(query) {
			if(module == 'regular') {
				fetchOrderByOrderNumber(query);
			} else {
				fetchOrderByDispatchNumber(query);
			}
		};

		/**
		 * Fetch Orderline Information (Regular)
		 */
		var fetchOrderByOrderNumber = function(orderNumber) {
			orderCollection.fetch({
				url : '/material/v1/orders/warehouse?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(),
				data : {
					orderNo : orderNumber
				},
				success : function(collection) {
					//orderModel.clear(); // Clear and set the result!
					if (collection && collection.length > 0) {
						var exactModel = collection.findWhere({orderNo : orderNumber});
						if(exactModel) {
							orderModel.set(exactModel.toJSON());
							Gloria.WarehouseApp.trigger('Revice:order:exist');
						} else {
							orderModel.clear();
							orderModel.set({orderNo : orderNumber});
							Gloria.WarehouseApp.trigger('Revice:order:notexist');
						}
					} else {
						orderModel.clear();
						orderModel.set({orderNo : orderNumber});
						Gloria.WarehouseApp.trigger('Revice:order:notexist');
					}
				}
			});
		};

		/**
		 * Fetch Orderline Information (Transfer, Return-Regular, Retutn-Transfer)
		 */
		var fetchOrderByDispatchNumber = function(dispatchNumber) {
			if (dispatchNumber) {
				var whSiteId = UserHelper.getInstance().getDefaultWarehouse();
				if (module == 'transfer') {
					transferReturnCollection.queryParams.deliveryAddressId = whSiteId;
					transferReturnCollection.queryParams.shipmentType = 'TRANSFER';
					transferReturnCollection.queryParams.status = 'IN_TRANSFER';
					delete transferReturnCollection.queryParams.whSiteId;
				} else if (module == 'return') {
					transferReturnCollection.queryParams.whSiteId = whSiteId;
					transferReturnCollection.queryParams.shipmentType = 'SHIPMENT';
					transferReturnCollection.queryParams.status = 'SHIPPED';
					delete transferReturnCollection.queryParams.deliveryAddressId;
				} else if (module == 'returntransfer') {
					transferReturnCollection.queryParams.whSiteId = whSiteId;
					transferReturnCollection.queryParams.shipmentType = 'TRANSFER';
					transferReturnCollection.queryParams.status = 'IN_TRANSFER';
					delete transferReturnCollection.queryParams.deliveryAddressId;
				}
				transferReturnCollection.queryParams.search = dispatchNumber;
				transferReturnCollection.state.currentPage = 1;
				transferReturnCollection.fetch({
					url : '/warehouse/v1/materiallines/transferReturn',
					reset : true,
					wait : true,
					success : function(response) {
						//transferReturnModel.clear(); // Clear and set the result!
						if (response && response.length > 0) {
							var exactModel = response.findWhere({dispatchNoteNo : dispatchNumber});
							if(exactModel) {
								transferReturnModel.set(exactModel.toJSON());
								Gloria.WarehouseApp.trigger('Revice:order:exist');
							} else {
								transferReturnModel.clear();
								transferReturnModel.set({dispatchNoteNo : dispatchNumber});
								Gloria.WarehouseApp.trigger('Revice:order:notexist');
							}
						} else {
							transferReturnModel.clear();
							transferReturnModel.set({dispatchNoteNo : dispatchNumber});
							Gloria.WarehouseApp.trigger('Revice:order:notexist');
						}
					}
				});
			}
			return transferReturnModel;
		};

		/**
		 * Show Look-up View (Regular, Transfer, Return)
		 */
		var showOrderLineLookupView = function(search) {
			require([ 'views/mobile/warehouse/view/receive/OrderSearchView' ], function(OrderSearchView) {
				var orderSearchView = new OrderSearchView({
					search : search
				});
				Gloria.basicLayout.content.show(orderSearchView);
			});
		};

		/**
		 * Search (Regular, Transfer, Return)
		 */
		var searchOrderLine = function(query) {
			if (module == 'regular') {
				fetchOrderLines(query);
			} else {
				fetchDeliveryNoteLinesTransferReturn(query);
			}
		};

		/**
		 * Fetch Orderline Information (Regular)
		 */
		var fetchOrderLines = function(query) {
			if (query) {
				orderLineCollection.url = '/warehouse/v1/orderlines/current?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
				orderLineCollection.queryParams.status = 'PLACED,RECEIVED_PARTLY';
				orderLineCollection.queryParams.orderQueryStr = query;
				orderLineCollection.state.currentPage = 1;
				orderLineCollection.fetch({
					success : function(collection) {
						showOrderLineGridView(module, collection);
					}
				});
			}
		};

		/**
		 * Fetch Orderline Information (Transfer, Return-Regular, Retutn-Transfer)
		 */
		var fetchDeliveryNoteLinesTransferReturn = function(query) {
			if (query) {
				var whSiteId = UserHelper.getInstance().getDefaultWarehouse();
				if (module == 'transfer') {
					transferReturnCollection.queryParams.deliveryAddressId = whSiteId;
					transferReturnCollection.queryParams.shipmentType = 'TRANSFER';
					transferReturnCollection.queryParams.status = 'IN_TRANSFER';
					delete transferReturnCollection.queryParams.whSiteId;
				} else if (module == 'return') {
					transferReturnCollection.queryParams.whSiteId = whSiteId;
					transferReturnCollection.queryParams.shipmentType = 'SHIPMENT';
					transferReturnCollection.queryParams.status = 'SHIPPED';
					delete transferReturnCollection.queryParams.deliveryAddressId;
				} else if (module == 'returntransfer') {
					transferReturnCollection.queryParams.whSiteId = whSiteId;
					transferReturnCollection.queryParams.shipmentType = 'TRANSFER';
					transferReturnCollection.queryParams.status = 'IN_TRANSFER';
					delete transferReturnCollection.queryParams.deliveryAddressId;
				}
				transferReturnCollection.queryParams.search = query;
				transferReturnCollection.state.currentPage = 1;
				transferReturnCollection.url = '/warehouse/v1/materiallines/transferReturn';
				transferReturnCollection.fetch({
					success : function(collection) {
						showOrderLineGridView(module, collection);
					}
				});
			}
		};

		/**
		 * Show Orderline Grid View (Regular, Transfer, Return)
		 */
		var showOrderLineGridView = function(module, thisCollection) {
			require([ 'views/mobile/warehouse/view/receive/OrderLineLayout', 'views/mobile/warehouse/view/receive/OrderLinesGridView' ],
			function(OrderLineLayout, OrderLinesGridView) {
				var orderLineLayout = new OrderLineLayout({
					module : module
				});
				orderLineLayout.on('show', function() {
					var orderLinesGridView = new OrderLinesGridView({
						module : module,
						collection : thisCollection
					});
					orderLineLayout.grid.show(orderLinesGridView);
				}, this);
				Gloria.basicLayout.content.show(orderLineLayout);
			});
		};

		/**
		 * Set selected Orderline Information (Regular, Transfer, Return)
		 */
		var selectOrderLine = function(id) {
			if (module == 'regular') {
				orderModel = orderLineCollection.get(id);
			} else {
				transferReturnModel = transferReturnCollection.get(id);
			}
			prepareReceiveView();
		};
		
		/**
		 * Route : warehouse/receive/:module/:deliveryNoteID
		 * To Receive View (Regular, Transfer, Return)
		 */
		var prepareToReceiveView = function() {
			// ToReceive Module (Regular, Transfer & Return)
			if(!deliveryNoteLineCollection) {
				deliveryNoteLineCollection = new Collection();
			}
			require([ 'views/mobile/warehouse/view/receive/ToReceiveLayout' ],
			function(ToReceiveView, ToReceiveGrid) {
				toReceiveView = new ToReceiveView({
					module : module
				});
				toReceiveView.on('show', function() {
					fetchDeliveryNoteLines();
				});
				Gloria.basicLayout.content.show(toReceiveView);
			});
		};
		
		/**
		 * Fetch Delivery Note Lines (Regular, Transfer, Return)
		 */
		var fetchDeliveryNoteLines = function(afterReceive) {
			deliveryNoteLineCollection.url = '/material/v1/deliverynotes/' + deliveryNoteID 
				+ '/deliverynotelines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse()+'&receiveType='+receiveType;
			deliveryNoteLineCollection.fetch({
				success : function(collection) {
					if(afterReceive && collection && collection.length > 0) {
						Gloria.trigger('reloadPage');
					} else if(afterReceive) {
						orderModel && orderModel.clear();
						transferReturnModel && transferReturnModel.clear();
						Backbone.history.navigate('warehouse/receive/' + module, {
							trigger : true,
							replace : true
						});
					} else {
						showToReceiveGridView(toReceiveView, collection);
					}					
				}
			});
		};
		
		/**
		 * Show To Receive Grid View (Regular, Transfer, Return)
		 */
		var showToReceiveGridView = function(toReceiveView, collection) {
			require([ 'views/mobile/warehouse/view/receive/ToReceiveGrid' ], function(ToReceiveGrid) {
				var toReceiveGrid = new ToReceiveGrid({
					module : module,
					collection : collection
				});
				toReceiveView.grid.show(toReceiveGrid);
			});
		};

		/**
		 * Prepare Quantity View (Regular, Transfer, Return)
		 */
		var prepareQuantityView = function(deliveryNoteLineID) {
			var deliverynotesublines = new Collection();
			deliverynotesublines.url = '/material/v1/deliverynotelines/' + deliveryNoteLineID
				+ '/deliverynotesublines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
			deliverynotesublines.fetch({
				success : function(collection) {
					showQuantityLayout(deliveryNoteLineCollection.get(deliveryNoteLineID), collection);
				}
			});
		};
		
		/**
		 * Show Quantity Layout View (Regular, Transfer, Return)
		 */
		var showQuantityLayout = function(deliveryNoteLine, deliveryNoteSublines) {
			require([ 'views/mobile/warehouse/view/receive/QuantityLayoutView' ], function(QuantityLayoutView) {
				var quantityLayoutView = new QuantityLayoutView({
					module : module,
					model : deliveryNoteLine,
					deliveryNoteID : deliveryNoteID
				});
				quantityLayoutView.on('show', function() {
					showQuantityCollectionView(quantityLayoutView, deliveryNoteLine, deliveryNoteSublines);
				}, this);
				Gloria.basicLayout.content.show(quantityLayoutView);
			});
		};
		
		/**
		 * Show Quantity Collection View (Regular, Transfer, Return)
		 */
		var showQuantityCollectionView = function(quantityLayoutView, model, collection) {
			require([ 'views/mobile/warehouse/view/receive/QuantityCollectionView' ], function(QuantityCollectionView) {
				var quantityCollectionView = new QuantityCollectionView({
					module : module,
					model : model,
					collection : collection.deepClone()
				});
				quantityLayoutView.qtyCollRegion.show(quantityCollectionView);
			});
		};
		
		/**
		 * Save Receive Info (Regular, Transfer, Return)
		 */
		var saveReceiveInfo = function(data) {
			deliveryNoteModel = new Model();
			deliveryNoteModel.set('whSiteId', UserHelper.getInstance().getDefaultWarehouse());
			deliveryNoteModel.save(data, {
				url : '/material/v1/deliverynotes',
				type : 'PUT',
				success : function(response) {
					deliveryNoteID = response.id;
					Backbone.history.navigate('warehouse/receive/' + module + '/' + deliveryNoteID, {
						trigger : true,
						replace : true
					});
				},
				validationError : showValidationError
			});
			receiveType = deliveryNoteModel.get('receiveType');
		};

		/**
		 * Receive Order (Regular, Transfer, Return)
		 */
		var receiveOrder = function(dnlModel) {
			var dnlCollection = new Collection();
			dnlCollection.add(dnlModel);
			dnlCollection.url = '/material/v1/deliverynotelines?receive=true';
			dnlCollection.save({
				success : function(coll) {
					fetchDeliveryNoteLines(true);					
				},
				validationError : showValidationError
			});
		};
		
		/**
		 * Show Validation Error
		 */
		var showValidationError = function(errorMessage, errors) {
			Gloria.trigger('showAppMessageView', {
				type : 'error',
				message : errorMessage
			});
		};

		/**
		 * Save Delivery Note Sub-Line (Regular, Transfer, Return)
		 */
		var saveDeliveryNoteSubLine = function(DNSLModel, DNLModel) {
			DNSLModel.url = '/material/v1/deliverynotelines/' + DNLModel.id + '/deliverynotesublines/' + DNSLModel.id;
			DNSLModel.save({}, {
				success : function() {
					Gloria.WarehouseApp.trigger('Receive:dnsl:saved');
				},
				validationError : showValidationError
			});
		};
		
		/**
		 * Save Delivery Note Sub-Line Bin location (Regular, Transfer, Return)
		 */
		var saveDNSBinLocation = function(DNSLModel, DNLModel, binLocationCode) {
			var binLocationModel = new Model();
			binLocationModel.fetch({
				url : '/warehouse/v1/binlocations/' + binLocationCode,
				data : {
					whSiteId : UserHelper.getInstance().getDefaultWarehouse(),
					zoneType : 'STORAGE'
				},
				success : function(model) {
					if (model && model.id) {
						DNSLModel.set('binLocation', model.id);
						saveDeliveryNoteSubLine(DNSLModel, DNLModel);
					} else {
						Gloria.WarehouseApp.trigger('Receive:BinLocation:invalid', binLocationCode);
					}
				},
				validationError : function() {
					Gloria.WarehouseApp.trigger('Receive:BinLocation:invalid', binLocationCode);
				}
			});
		};

		var receiveLater = function() {
			orderModel && orderModel.clear();
			transferReturnModel && transferReturnModel.clear();
			deliveryNoteModel && deliveryNoteModel.clear();
			Backbone.history.navigate('warehouse/receive/regular', {
				trigger : true,
				replace : true
			});
		};
		
		/**
		 * Show Transport Label Modal
		 */
		var showTransportLabelModal = function() {
			require([ 'views/mobile/warehouse/view/receive/CreateTransportLabelView' ], function(CreateTransportLabelView) {
				var createTransportLabelView = new CreateTransportLabelView();
				Gloria.basicModalLayout.content.show(createTransportLabelView);
			});
		};

		/**
		 * Create Transport Label
		 */
		var createTransportLabel = function(number) {
			var transportLabels = new Collection();
			transportLabels.url = '/material/v1/transportlabels/?action=create&whSiteId='
				+ UserHelper.getInstance().getDefaultWarehouse() + '&transportLabelCopies=' + number;
			transportLabels.fetch({
				success : function(response) {
					if(response && response.length > 0) {
						response.each(function(model) {
							printTransportLabel(model.id);
						});
					}
				}
			});
		};

		/**
		 * Print Transport Label
		 */
		var printTransportLabel = function(transportlabelID) {
			var model = new Model();
			model.url = '/material/v1/transportlabels/' + transportlabelID
				+ '?action=printLabel&whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
			model.fetch({
				validationError : function(errorMessage, errors) {
					var errorMessage = new Array();
					var item = {
							message : i18n.t('Gloria.i18n.errors.GLO_ERR_69')
					};
					errorMessage.push(item);
					Gloria.trigger('showAppMessageView',{
						type : 'error',
						message : errorMessage
					});
				},
				error : function() {
					Gloria.WarehouseApp.trigger('DeliveryNoteLine:printTL:printed', false);
				}
			});
		};
		
		/**
		 * Show Part Label Modal
		 */
		var showPartLabelModal = function(models) {
			require([ 'views/mobile/warehouse/view/receive/PrintPLModalView' ], function(PrintPLModalView) {
				var collectionToPrint = deliveryNoteLineCollection;
				if(models && models.length) {
					collectionToPrint = deliveryNoteLineCollection.clone();
					collectionToPrint.reset(models);
				}
				
				var printPLModalView = new PrintPLModalView({
					collection : collectionToPrint
				});
				Gloria.basicModalLayout.content.show(printPLModalView);
			});
		};

		/**
		 * Print Part Label
		 */
		var printPartLabel = function(collection, printQty) {
			collection.save({
				url : '/common/v1/deliverynotelines/partlabels?whSiteId='
					+ UserHelper.getInstance().getDefaultWarehouse() + (printQty ? ('&quantity=' + printQty) : ''),
				validationError : function() {
					var errorMessage = new Array();
					var item = {
						message : i18n.t('Gloria.i18n.errors.GLO_ERR_69')
					};
					errorMessage.push(item);
					Gloria.trigger('showAppMessageView',{
						type : 'error',
						message : errorMessage
					});
				}
			});
		};

		/**
		 * Get Unique Items
		 */
		var getUniqueItems = function(collection, keyArr) {
			var finalModels = new Array();
			var plucked = collection.map(function(model) {
				return _.pick(model.toJSON(), keyArr);
			});
			var uniqueArray = _.uniq(plucked, function(item) {
				return JSON.stringify(item);
			});
			_.each(uniqueArray, function(item) {
				var thisModel = collection.findWhere(item);
				thisModel && finalModels.push(thisModel);
			});
			return finalModels;
		};

		/**
		 * Details (Transfer, Return-Regular, Retutn-Transfer)
		 */
		var selectPartDetails = function(model) {
			var projectCollection = new Collection();
			var partCollection = new Collection();
			partCollection.models = deliveryNoteLineCollection.where({
				partNumber : model.get('partNumber'),
				partVersion : model.get('partVersion')
			});
			projectCollection.set(getUniqueItems(partCollection, [ 'projectId', 'referenceId' ]));
			if (projectCollection.length == 1) {
				prepareQuantityView(model.get('id'));
			} else if (projectCollection.length > 1) {
				showProjectView(projectCollection);
			}
		};
		
		/**
		 * Show Project View
		 */
		var showProjectView = function(projectCollection) {
			require([ 'views/mobile/warehouse/view/receive/ProjectLayout', 'views/mobile/warehouse/view/receive/ProjectGridView' ],
			function(ProjectLayout, ProjectGridView) {
				var projectLayout = new ProjectLayout({
					module : module,
					deliveryNoteID : deliveryNoteID
				});
				projectLayout.on('show', function() {
					var projectGridView = new ProjectGridView({
						module : module,
						collection : projectCollection
					});
					projectLayout.grid.show(projectGridView);
				});
				Gloria.basicLayout.content.show(projectLayout);
			});
		};

		var clearDataOnWarehouseChange = function() {
			orderModel && orderModel.clear();
			transferReturnModel && transferReturnModel.clear();
			deliveryNoteModel && deliveryNoteModel.clear();
		};
		
		Controller.ReceiveController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				// Listeners to Search, Load Order(s) (Regular, Transfer, Return)
				this.listenTo(Gloria.WarehouseApp, 'Receive:search:show', showOrderLineLookupView);
				this.listenTo(Gloria.WarehouseApp, 'Receive:search:orderline', searchOrderLine);
				this.listenTo(Gloria.WarehouseApp, 'Receive:select:orderline', selectOrderLine);
				this.listenTo(Gloria.WarehouseApp, 'Receive:fetch:orderline', fetchOrderline);
				
				// Listeners to Save, Receive Order(s) from Quantity view (Regular, Transfer, Return)
				this.listenTo(Gloria.WarehouseApp, 'Receive:QuantityView:show', prepareQuantityView);
				this.listenTo(Gloria.WarehouseApp, 'Receive:order:save', saveReceiveInfo);
				this.listenTo(Gloria.WarehouseApp, 'Receive:order:receive', receiveOrder);
				this.listenTo(Gloria.WarehouseApp, 'Receive:dnsl:save', saveDeliveryNoteSubLine);
				this.listenTo(Gloria.WarehouseApp, 'Receive:dnsl:save:binLocation', saveDNSBinLocation);
				this.listenTo(Gloria.WarehouseApp, 'Receive:parts:select', selectPartDetails);
				this.listenTo(Gloria.WarehouseApp, 'Receive:parts:later', receiveLater);
				
				// Listeners to Create, Print Transport Label, Part Label
				this.listenTo(Gloria.WarehouseApp, 'Receive:TransLabel:show', showTransportLabelModal);
				this.listenTo(Gloria.WarehouseApp, 'Receive:TransLabel:create', createTransportLabel);
				this.listenTo(Gloria.WarehouseApp, 'Receive:PartLabel:show', showPartLabelModal);
				this.listenTo(Gloria.WarehouseApp, 'Receive:PartLabel:print', printPartLabel);
				
				this.listenTo(Gloria, 'Warehouse:select', clearDataOnWarehouseChange);
			},

			control : function(options) {
				options || (options = {});
				Gloria.trigger('change:title', 'Gloria.i18n.warehouse.receive.receive');
				if (options.page) { 					// Receive module for Regular, Transfer, Return
					module = options.page || 'regular';
					prepareReceiveView.call(this);
				} else if (options.deliveryNoteId) { 	// To Receive module for Regular, Transfer, Return
					module = options.module;
					deliveryNoteID = options.deliveryNoteId;
					prepareToReceiveView.call(this);
				}
			},

			onDestroy : function() {
				// Regular, Transfer, Return module
				module = null;
				
				// Receive module for Regular, Transfer, Return module
				receiveLayout = null;
				receiveView = null;
				toReceiveView = null;
				orderCollection = null;
				orderModel = null;
				transferReturnCollection = null;
				transferReturnModel = null;
				orderLineCollection = null;
				
				// To Receive module for Regular, Transfer, Return
				deliveryNoteID = null;
				deliveryNoteLineCollection = null;
				
				deliveryNoteModel = null;
				
				regularTabViewModel = null;
			}
		});
	});

	return Gloria.WarehouseApp.Controller.ReceiveController;
});
