define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'utils/marionette/GloriaRouter',
        'controllers/controllerManager',
        'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaRouter, ControllerManager, UserHelper) {

	Gloria.module('AdminTeamApp', function(AdminTeamApp, Gloria, Backbone, Marionette, $, _) {
		
		AdminTeamApp.title = "Gloria.i18n.admin.adminTeam";
	    
		AdminTeamApp.Router = GloriaRouter.extend({
			appRoutes : {
				'admin(/:tab)' : 'showeAdminTeamView'
			}
		});

		var API = {
			showeAdminTeamView : function(tab, adminTeamController) {
			    if(UserHelper.getInstance().hasPermission('view', ['TeamAdministration'])) {
			        if (adminTeamController) {
			            adminTeamController.control(tab);
			        } else {
			            Gloria.controllerManager.getController('adminTeamController', this.showeAdminTeamView, tab);
			        }
			    }
			}
		};
		
		Gloria.addInitializer(function() {
			this.controllerManager = ControllerManager.getInstance();
			new AdminTeamApp.Router({
				controller : API
			});			
		});
	});

	return Gloria.AdminTeamApp;
});
