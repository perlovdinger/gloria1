define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'hbs!views/applicationstatus/view/applicationstatus'
], function(Gloria, $, i18n, _, Handlebars, Backbone, Marionette, compiledTemplate) {
    
    Gloria.module('ApplicationStatusApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.ApplicationStatusView = Marionette.LayoutView.extend({
        
            initialize : function(options) {
                this.appStatusObject = options.appStatusObject;
            },
            
            events : {
            },
            
            render : function() {
                $( "#pageHeader" ).replaceWith( $( "#applicationStatusHeader" ) )
                this.$el.html(compiledTemplate({
                    appStatusObject : this.appStatusObject
                }));
                return this;
            },
            
            onShow : function() {                           
            }
        });
     });
    
    return Gloria.ApplicationStatusApp.View.ApplicationStatusView;
});
