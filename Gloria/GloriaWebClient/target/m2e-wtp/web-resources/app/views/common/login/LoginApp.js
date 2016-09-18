define(['app',
        'jquery',
        'handlebars',
        'backbone',
        'bootstrap',
        'utils/marionette/GloriaRouter'
], function(Gloria, $, Handlebars, Backbone, Bootstrap, GloriaRouter) {
	Gloria.module('LoginApp', function(LoginApp, Gloria, Backbone, Marionette, $, _) {
		LoginApp.Router = GloriaRouter.extend({
			appRoutes : {
				'login(/:successUrl)' : 'showLogin'
			}
		});
		
		var API = {
				
	        /**
	         * @param successUrl the url that the user will be rerouted to on successful login
	         */
	        showLogin : function(successUrl) {
	            require(['views/login/LoginController'], function(LoginController) {
	            	LoginController.showLogin(successUrl);
	            });
	        }
		};
		
		Gloria.addInitializer(function() {
			new LoginApp.Router({
				controller : API
			});
		});
	});
	
	return Gloria.LoginApp;
});