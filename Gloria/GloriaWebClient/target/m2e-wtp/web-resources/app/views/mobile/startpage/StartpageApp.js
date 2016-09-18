define(['app', 'utils/marionette/GloriaRouter', 'controllers/controllerManager'],
function(Gloria, GloriaRouter, ControllerManager) {

	Gloria.module('StartpageApp', function(StartpageApp, Gloria, Backbone, Marionette, $, _) {
		
	    StartpageApp.title = "Gloria.i18n.home";
	    
		StartpageApp.Router = GloriaRouter.extend({
			appRoutes : {
			    // Default view
			    '(:module)' : 'showStartpageView',
			}
		});

		var API = {
	        showStartpageView : function(args, startpageController) {
	        	if (startpageController) {
	                startpageController.control(args);
	            } else {
	            	Gloria.controllerManager.getController('mobileStartpageController', this.showStartpageView, args);
	            }
	        }
		};

		Gloria.addInitializer(function() {		
			this.controllerManager = ControllerManager.getInstance();
			new StartpageApp.Router({
				controller : API
			});
		});
	});

	return Gloria.StartpageApp;
});