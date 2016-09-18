define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'moment',
	    'i18next',
	    'backbone.syphon',
	    'hbs!views/deliverycontrol/myorderoverview/details/view/procurement-gen-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, moment, i18n, Syphon, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementGenInfoView = Marionette.ItemView.extend({
	
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

    return Gloria.DeliveryControlApp.View.ProcurementGenInfoView;
});
