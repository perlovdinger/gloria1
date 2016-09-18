define(['app',
        'jquery',
        'underscore',
        'handlebars', 
        'marionette',
        'hbs!views/warehouse/receive/view/layout'], 
function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
    Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.Layout = Marionette.LayoutView.extend({
            
            regions: {
                moduleInfo : '#moduleInfo',
                toReceivePane : '#toReceivePane',
                notReceivedPane: '#notReceivedPane',
                receivedPane: '#receivedPane',
                receivedButton : '#receivedButton',
            },

            events: {
                'click #receiveTab a' : 'handleReceiveTabClick'
            },
            
            handleReceiveTabClick: function(e) {
                e.preventDefault();             
                Backbone.history.navigate('warehouse/receive/'+e.currentTarget.hash.split("#")[1], {
                    trigger: true
                });
            },
            
            initialize: function(options) {
                this.module = options.module;
            },

            render: function() {
                this.$el.html(compiledTemplate());
                return this;
            },
            
            onShow: function() {                       
                var tabId = '#receiveTab ' + (this.module ? 'a[href="#' + this.module + '"]' : 'a:first');
                this.$(tabId).tab('show');
            }
        });
    });
    
    return Gloria.WarehouseApp.Receive.View.Layout;    
});