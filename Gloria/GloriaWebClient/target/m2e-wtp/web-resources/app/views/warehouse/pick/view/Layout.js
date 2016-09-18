define(['app',
        'jquery',
        'underscore',
        'handlebars', 
        'marionette',
        'hbs!views/warehouse/pick/view/layout'], 
function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
    Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.Layout = Marionette.LayoutView.extend({
            
        	  regions: {
                  moduleInfo : '#moduleInfo',
                  pickPane : '#pickPane',
                  inpickPane: '#inpickPane',
                  shipPane: '#shipPane'
              },

              events: {
                  'click #pickShipTab > ul a' : 'handlePickShipTabClick'
              },
            
              handlePickShipTabClick: function(e) {            	
                e.preventDefault();
                var subModule = e.currentTarget.hash.split("#")[1];
                Backbone.history.navigate('warehouse/'+subModule, {
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
                  var tabId = '#pickShipTab ' + (this.module ? 'a[href="#' + this.module + '"]' : 'a:first');
                  this.$(tabId).tab('show');
              }
			
        });
    });
    
    return Gloria.WarehouseApp.View.Layout;    
});