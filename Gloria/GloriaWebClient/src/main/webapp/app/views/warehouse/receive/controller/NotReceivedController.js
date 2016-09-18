/**
`	 * NotReceivedController.
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'utils/UserHelper',
		'views/warehouse/collection/OrderLineCollection'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, UserHelper, OrderLineCollection) {

	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var orderLineCollection;
		var layout;
		
		/**
		 * Prepare Not Received Requests.
		 * Initialize Data Source Objects which are going to be used as data transfer objects
		 * and set page layout.
		 */ 
		var prepareNotReceivedRequests = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects.
		 * These objects are going to be used by the page/components.
		 */
		var initializeDataSourceObjects = function() {
			orderLineCollection = new OrderLineCollection([], {
				queryParams : {
					status : 'PLACED,RECEIVED_PARTLY'
				},
				state : {
					pageSize : function() {	// Check if any pageSize is already stored
						var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.NotReceivedGrid'
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : 'NotReceivedGrid'
			});
		};
		
		/**
		 * Set Page Layout.
		 */ 
		var setPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView');
			require(['views/warehouse/receive/view/Layout', 'views/warehouse/receive/view/ToReceiveLayout'], function(Layout, ToReceiveLayout) {
				layout = new Layout({
					module : 'notReceived'
				});
				
				layout.on('show', function() {
					showModuleView();
				}, this);
				
				Gloria.basicLayout.content.show(layout);
				
				toReceiveLayout = new ToReceiveLayout();
				toReceiveLayout.on('show', function() {
					prepareNotReceivedGrid();
				}, this);
				layout.notReceivedPane.show(toReceiveLayout);
			});
		};
		
		/**
		 * Show Module View.
		 */
		var showModuleView = function() {
			require(['views/warehouse/common/WarehouseModuleView'], function(WarehouseModuleView) {
				var warehouseModuleView = new WarehouseModuleView({
					module : 'receive',
					control : 'notReceived'
				});
				// Attach to moduleInfo region
				layout.moduleInfo.show(warehouseModuleView);
			});
		};
		
		/**
		 * Prepare notReceived Grid View.
		 */
		var prepareNotReceivedGrid = function(parentView) {
			require(['views/warehouse/receive/view/ReceiveButtonView', 'views/warehouse/receive/view/NotReceivedGridView'],
				function(ReceiveButtonView, NotReceivedGridView) {
				var receiveButtonView = new ReceiveButtonView({
					module : 'notReceived'
				});
    			var notReceivedGridView = new NotReceivedGridView({
    				collection : orderLineCollection
    			});
    			notReceivedGridView.on('show', function() {
    				processNotReceivedGridInfo();
    			}, this);
    			toReceiveLayout.general.show(receiveButtonView);
    			toReceiveLayout.grid.show(notReceivedGridView);
    		});
		};

		/**
		 * Process Not Received Grid Information.
		 */
		var processNotReceivedGridInfo = function() {
			orderLineCollection.fetch({
				reset : true
			});
		};
		
		Controller.NotReceivedController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {

			},

			control : function() {
				prepareNotReceivedRequests.call(this);
			},
			
			onDestroy: function() {
			    orderLineCollection = null;
			    layout = null;
			}
		});
	});

	return Gloria.WarehouseApp.Controller.NotReceivedController;
});
