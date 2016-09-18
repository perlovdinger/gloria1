define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
	    'bootstrap',
	    'i18next',
	    'hbs!views/deliverycontrol/myorderoverview/details/view/procurement-notes'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, i18n, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementNotesView = Marionette.View.extend({
	        
	    	initialize : function(options) {
	    		this.model = options.model;
	        },
	        
	        render : function() {
	        	this.$el.html(compiledTemplate({
	                'data' : this.model ? this.model.toJSON() : {}
	            }));
	            return this;
	        }
	    });
	});
    
	return Gloria.DeliveryControlApp.View.ProcurementNotesView;
 });
