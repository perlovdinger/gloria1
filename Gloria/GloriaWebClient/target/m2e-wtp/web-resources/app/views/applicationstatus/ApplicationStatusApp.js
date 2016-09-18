define(['app',
        'backbone',
        'marionette',
        'utils/marionette/GloriaRouter',
        'controllers/controllerManager'
], function(Gloria, Backbone, Marionette, GloriaRouter, ControllerManager) {

    Gloria.module('ApplicationStatusApp', function(ApplicationStatusApp, Gloria, Backbone, Marionette, $, _) {
        
        ApplicationStatusApp.title = "Gloria.i18n.applicationStatus";
        
        ApplicationStatusApp.Router = GloriaRouter.extend({
            
            publicRouter : true,
            
            appRoutes : {
                'applicationStatus' : 'applicationStatusView'
            }
        });

        var API = {
                applicationStatusView : function(applicationStatusController) {
                    if (applicationStatusController) {
                        applicationStatusController.control();
                    } else {
                        Gloria.controllerManager.getController('applicationStatusController', this.applicationStatusView);
                }
            }
        };
        
        Gloria.addInitializer(function() {
            this.controllerManager = ControllerManager.getInstance();
            new ApplicationStatusApp.Router({
                controller : API
            });         
        });
    });

    return Gloria.ApplicationStatusApp;
});
