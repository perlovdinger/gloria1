/**
 * Reports
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'utils/marionette/GloriaRouter',
		'controllers/controllerManager'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaRouter, ControllerManager) {

	Gloria.module('ReportsApp', function(ReportsApp, Gloria, Backbone, Marionette, $, _) {

		ReportsApp.title = "Gloria.i18n.reports.reports";

		ReportsApp.routes = {
				'reports/report(/:page)' : 'showReports'
		},

		ReportsApp.Router = GloriaRouter.extend({
			appRoutes : GloriaRouter.prototype.transformAppRoutes(ReportsApp.routes)
		});

		var API = {
			showReports : function(page, reportsController) {
	        	if (reportsController) {
	        		reportsController.control(page);
	            } else {
	            	Gloria.controllerManager.getController('reportsController', this.showReports, page);
	            }
			}
		};

		Gloria.addInitializer(function() {
			this.controllerManager = ControllerManager.getInstance();
			new ReportsApp.Router({
				controller : API
			});
		});
	});

	return Gloria.ReportsApp;
});
