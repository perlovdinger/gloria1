define([ 'app', 'handlebars', 'backbone', 'utils/marionette/GloriaRouter', 'controllers/controllerManager' ],
		function(Gloria, Handlebars, Backbone, GloriaRouter, ControllerManager) {

	Gloria.module('TestingUtilityApp', function(TestingUtilityApp,
			Gloria, Backbone, Marionette, $, _) {

	    TestingUtilityApp.title = "Gloria.i18n.testingutility.testingutility";
	    
		TestingUtilityApp.Router = GloriaRouter.extend({
			appRoutes : {
				'testingutility/message' : 'showMessageLoad',
				'testingutility/migration(/:tab)' : 'showDataMigration'
			}
		});

		var API = {

			showMessageLoad : function(testingUtilityController) {
				if (testingUtilityController) {
					testingUtilityController.control();
				} else {
					Gloria.controllerManager.getController('testingUtilityController', this.showMessageLoad);
				}
			},
			
			showDataMigration : function(tab, dataMigrationController) {
				if (dataMigrationController) {
					dataMigrationController.control(tab);
				} else {
					Gloria.controllerManager.getController('dataMigrationController', this.showDataMigration, tab);
				}
			}
		};

		Gloria.addInitializer(function() {
			this.controllerManager = ControllerManager.getInstance();
			new TestingUtilityApp.Router({
				controller : API
			});
		});
	});

	return Gloria.TestingUtilityApp;
});