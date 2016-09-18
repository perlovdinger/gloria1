define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'utils/marionette/GloriaRouter',
        'controllers/controllerManager'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaRouter, ControllerManager) {

	Gloria.module('DeliveryControlApp', function(DeliveryControlApp, Gloria, Backbone, Marionette, $, _) {
		
	    DeliveryControlApp.title = "Gloria.i18n.deliverycontrol.deliverycontrol";
	    
	    DeliveryControlApp.routes = {				
			'deliverycontrol/myOrderOverview(/:page)' : 'showMyOrderOverview',
			'deliverycontrol/assigndc(/:page)' : 'showAssignDC',			
			'deliverycontrol/myOrderOverview/external/orderLineDetail(/:id)' : 'showOrderLineDetailPage',
			'deliverycontrol/myOrderOverview/internal/orderLineDetail(/:id)' : 'showOrderLineDetailPage',
			'deliverycontrol/myOrderOverview/completed/orderLineDetail(/:id)' : 'showOrderLineDetailPageForCompleted',
			// the below route is used when the referer page is material/linesoverview/linedetails/(:id)
			// and in this case the detail page should be in readonly mode regardless of user role. 
			'deliverycontrol/myOrderOverviewAsMaterial/myOrderOverviewDetails(/:id)' : 'showOrderLineDetailPageAsMaterialOverView'
		};
	    
		DeliveryControlApp.Router = GloriaRouter.extend({
			appRoutes : GloriaRouter.prototype.transformAppRoutes(DeliveryControlApp.routes)
		});

		var API = {
	        
			showMyOrderOverview : function(page, myOrderOverviewController) {
	            if (myOrderOverviewController) {
	            	Backbone.history.navigate('deliverycontrol/myOrderOverview/' + (page || 'external'), {
		                replace : true
		            });
	            	myOrderOverviewController.control(page);
	            } else {
	            	Gloria.controllerManager.getController('myOrderOverviewController', this.showMyOrderOverview, page);
	            }
	        },
	        
	        showAssignDC : function(page, assignDCController) {
	            if (assignDCController) {
	            	assignDCController.control(page);
	            } else {
	            	Gloria.controllerManager.getController('assignDCController', this.showAssignDC, page);
	            }
	        },
	        
	        showOrderLineDetailPage : function(options, orderOverviewDetailsController) {
	        	var thisOption = _.isObject(options) ? options : {editable: true, orderLineId: options};
	            if (orderOverviewDetailsController) {
	            	orderOverviewDetailsController.control(thisOption);
	            } else {
	            	Gloria.controllerManager.getController('orderOverviewDetailsController', this.showOrderLineDetailPage, options);
	            }
	        },
	        
	        showOrderLineDetailPageForCompleted : function(options, orderOverviewDetailsController) {
	        	var thisOption = _.isObject(options) ? options : {editable: true, orderLineId: options, module: 'completed'};
	            if (orderOverviewDetailsController) {
	            	orderOverviewDetailsController.control(thisOption);
	            } else {
	            	Gloria.controllerManager.getController('orderOverviewDetailsController', this.showOrderLineDetailPageForCompleted, options);
	            }
	        },
	        
	        showOrderLineDetailPageAsMaterialOverView : function(orderLineId, orderOverviewDetailsController) {
	           return this.showOrderLineDetailPage({editable: false, orderLineId: orderLineId}, orderOverviewDetailsController);
	        }
		};

		Gloria.addInitializer(function() {
			this.controllerManager = ControllerManager.getInstance();
			new DeliveryControlApp.Router({
				controller : API
			});			
		});
	});

	return Gloria.DeliveryControlApp;
});
