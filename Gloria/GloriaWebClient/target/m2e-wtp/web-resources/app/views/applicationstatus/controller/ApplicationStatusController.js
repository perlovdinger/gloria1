define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette'
], function(Gloria, $, _, Handlebars, Backbone, Marionette) {

    Gloria.module('ApplicationStatusApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
        
        var appStatusObject;
        
        var prepareAppStatusView = function() {
            initializeDataSourceObjects();
            setPageLayout();
        };
        
        var setPageLayout = function() {
            require([ 'views/applicationstatus/view/ApplicationStatusView' ], function(ApplicationStatusView) {
                applicationStatusView = new ApplicationStatusView({
                    appStatusObject : appStatusObject
                });
                Gloria.basicModalLayout.closeAndReset();
                Gloria.basicLayout.header.empty();
                Gloria.basicLayout.header.setHeaderForApplicationStatus();
                Gloria.basicLayout.breadcrumb.empty();
                Gloria.basicLayout.content.show(applicationStatusView);
            });
        };
        
        
        /**
         * Initialize Data Source Objects. No need of collection as it returns string
         */
        var initializeDataSourceObjects = function() {
            
            $.ajax({
                url:'/common/v1/app/status', 
                success:function(data) {
                    appStatusObject = data;
                }
              });
        };
        
        
        Controller.ApplicationStatusController = Marionette.Controller.extend({

            initialize : function() {
                this.initializeListeners();
            },

            initializeListeners : function() {
            },

            control : function(page) {
                prepareAppStatusView.call(this);
            },

            onDestroy : function() {
                appStatusObject = null;
            }
        });
    });

    return Gloria.ApplicationStatusApp.Controller.ApplicationStatusController;
});
