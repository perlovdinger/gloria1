define(['app',
        'jquery',
        'underscore',
        'handlebars', 
        'marionette',
        'hbs!views/warehouse/receive/view/to-receive-layout'], 
function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
    Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.ToReceiveLayout = Marionette.LayoutView.extend({
            
            regions: {                
                general : '#general',
                grid: '#grid'
            },

            events: {
                
            },
            
            initialize: function(options) {
                this.module = options.module;
            },

            render: function() {
                this.$el.html(compiledTemplate());
                return this;
            },
            
            onShow: function() {                           
                
            }
        });
    });
    
    return Gloria.WarehouseApp.Receive.View.ToReceiveLayout;    
});