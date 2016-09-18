define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'utils/marionette/GloriaRouter',
		'controllers/controllerManager'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaRouter, ControllerManager) {

		Gloria.module('MaterialRequestApp', function(MaterialRequestApp, Gloria, Backbone, Marionette, $, _) {

		MaterialRequestApp.title = "Gloria.i18n.materialrequest.materialrequest";
		
		MaterialRequestApp.routes = {
			'materialrequest/overview/createrequest(/:id)' : 'createMaterialRequest',
			'materialrequest/overview/openrequest(/:id)' : 'openMaterialRequest',
			'materialrequest/overview' : 'materialRequestOverview'
		};

		MaterialRequestApp.Router = GloriaRouter.extend({
			appRoutes : GloriaRouter.prototype.transformAppRoutes(MaterialRequestApp.routes)
		});

		var API = {
				
			materialRequestOverview : function(materialRequestOverviewController) {
				if (materialRequestOverviewController) {
					materialRequestOverviewController.control('overview');
				} else {
					Gloria.controllerManager.getController('materialRequestOverviewController', this.materialRequestOverview);
				}
			},

			createMaterialRequest : function(id) {
				this.materialRequestDetail({
					'action' : 'createrequest',
					'id' : id
				});
			},

			openMaterialRequest : function(id) {
				this.materialRequestDetail({
					'action' : 'openrequest',
					'id' : id
				});
			},

			materialRequestDetail : function(options, materialRequestDetailController) {
				if (materialRequestDetailController) {
					materialRequestDetailController.control(options);
				} else {
					Gloria.controllerManager.getController('materialRequestDetailController', this.materialRequestDetail, options);
				}
			}
		};

		Gloria.addInitializer(function() {
			this.controllerManager = ControllerManager.getInstance();
			new MaterialRequestApp.Router({
				controller : API
			});
		});
	});

	return Gloria.MaterialRequestApp;
});
