define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
	    'bootstrap',
	    'i18next',
	    'hbs!views/material/details/view/order-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, i18n, compiledTemplate) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.OrderInfoView = Backbone.Marionette.ItemView.extend({
	        
	    	initialize : function(options) {
	    		this.model = options.model;
	    	},
	        
	        render : function() {
	        	this.$el.html(compiledTemplate({
	        		data: this.model ? this.model.toJSON() : {}
	        	}));
	            return this;
	        }
	    });
	});
    
	return Gloria.MaterialApp.View.OrderInfoView;
 });
