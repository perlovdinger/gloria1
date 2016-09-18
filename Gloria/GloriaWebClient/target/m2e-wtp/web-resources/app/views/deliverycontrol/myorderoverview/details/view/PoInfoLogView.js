define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'moment',
	    'i18next',
	    'backbone.syphon',
	    'hbs!views/deliverycontrol/myorderoverview/details/view/po-info-log'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, moment, i18n, Syphon, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.PoInfoLogView = Marionette.LayoutView.extend({

	        regions : {
	        	orderLineActionLog : '#orderLineActionLog',
	        	poActionLog : '#poActionLog',
	        	notesFromPurchase : '#notesFromPurchase',
	        	notesFromProcurement : '#notesFromProcurement'
	        },
	        
	        initialize : function(options) {    
	            this.isInternalOrder = options.isInternalOrder;
	        },
	
	        events : {
	        	'click ul.nav.nav-tabs > li > a' : 'handleTabClick'
	        },
	        
	        handleTabClick : function(e) {
				var tab = e.currentTarget.hash.split('#')[1];
				Gloria.DeliveryControlApp.trigger('OrderLineDetail:showPoInfoLog', tab);
			},
	        
	        render : function() {
	            
	            this.$el.html(compiledTemplate({                  
            		'isInternalOrder': this.isInternalOrder
                }));  
	            return this;
	        }
	    });
	});

    return Gloria.DeliveryControlApp.View.PoInfoLogView;
});
