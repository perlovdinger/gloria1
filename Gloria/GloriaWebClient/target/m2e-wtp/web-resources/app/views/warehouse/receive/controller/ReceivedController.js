/**
 * ReceivedController
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
		'utils/UserHelper',
		'utils/backbone/GloriaPageableCollection',
		'utils/backbone/GloriaModel'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, GloriaPageableCollection, GloriaModel) {

	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var layout;
		var receivedCollection;
		
		/**
		 * Prepare Received Requests
		 */
		var prepareReceivedRequests = function() {
			initializeDataSourceObjects();
			setPageLayout();
		};

		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			receivedCollection = new GloriaPageableCollection([], {
				queryParams : {
					status : 'RECEIVED,PARTIAL_CANCELLED'
				},
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.ReceivedGrid'
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : 'received'
			});
			receivedCollection.url = '/procurement/v1/goodsreceiptlines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
		};
		
		/**
		 * Set Page Layout
		 */
		var setPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView');
			require(['views/warehouse/receive/view/Layout', 'views/warehouse/receive/view/ToReceiveLayout'], function(Layout, ToReceiveLayout) {
				layout = new Layout({
					module : 'received'
				});
				layout.on('show', function() {
					showModuleView();
				}, this);
				Gloria.basicLayout.content.show(layout);
				toReceiveLayout = new ToReceiveLayout();
				toReceiveLayout.on('show', function() {
					showReceivedTabContent();
				}, this);
				layout.receivedPane.show(toReceiveLayout);
			});
		};
		
		/**
		 * Show Module View
		 */
		var showModuleView = function() {
			require(['views/warehouse/common/WarehouseModuleView'], function(WarehouseModuleView) {
				var warehouseModuleView = new WarehouseModuleView({
					module : 'receive',
					control : 'received'
				});
				layout.moduleInfo.show(warehouseModuleView);
			});
		};
		
		/**
		 * Show Received Tab Content
		 */
		var showReceivedTabContent = function() {
			require(['views/warehouse/receive/view/ReceiveButtonView', 'views/warehouse/receive/view/ReceivedGridView'],
			function(ReceiveButtonView, ReceivedGridView) {
				var receiveButtonView = new ReceiveButtonView({
					module : 'received'
				});
				var receivedGridView = new ReceivedGridView({
					collection : receivedCollection
				});
				receivedGridView.on('show', function() {
					processReceivedGridInfo();
				}, this);
				layout.receivedButton.show(receiveButtonView);
				layout.receivedPane.show(receivedGridView);
			});
		};
		
		/**
		 * Process Received Tab Content
		 */
		var processReceivedGridInfo = function() {
			receivedCollection.fetch();
		};
		
		/**
		 * Process Good Receipt
		 */
		var prepareGoodReceipt = function(model) {
			var thisModel = new GloriaModel({id : model.id});
			thisModel.fetch({
				url : '/procurement/v1/goodsreceiptlines/' + model.id,
				success: function(response) {
					if(response && response.get('cancelable')) {
						showGoodReceipt(response);
					} else {
						Gloria.trigger('showAppMessageView', {
			    			type : 'error',
			    			title : i18n.t('Gloria.i18n.warehouse.receive.text.cancelNotPossibleTitle'),
			    			message : new Array({
			    				message : i18n.t('Gloria.i18n.warehouse.receive.text.cancelNotPossibleText')
			    			})
			    		});
					}
				}
			});
		};
		
		/**
		 * Show Good Receipt
		 */
		var showGoodReceipt = function(model) {
			require([ 'views/warehouse/receive/view/CancelGoodReceiptDialogView' ], function(CancelGoodReceiptDialogView) {
				var goodReceiptDialogView = new CancelGoodReceiptDialogView({
					model : model
				});
				Gloria.basicModalLayout.content.show(goodReceiptDialogView);
			});
		};
		
		/**
		 * Cancel Good Receipt
		 */
		var cancelGoodReceipt = function(model, data) {
			model.save(data, {
				url : '/procurement/v1/goodsreceiptlines/' + model.id + '/cancel',
				success : function(response) {
					Gloria.WarehouseApp.trigger('ReceivedGrid:clearselection');
					Gloria.WarehouseApp.trigger('ReceiveButton:refresh');
					processReceivedGridInfo();
				}
			});
		};
		
		Controller.ReceivedController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.WarehouseApp, 'Received:GoodReceipt:show', prepareGoodReceipt);
				this.listenTo(Gloria.WarehouseApp, 'Received:GoodReceipt:cancel', cancelGoodReceipt);
			},

			control : function() {
				prepareReceivedRequests.call(this);
			},

			onDestroy : function() {
				layout = null;
				receivedCollection = null;
			}
		});
	});

	return Gloria.WarehouseApp.Controller.ReceivedController;
});
