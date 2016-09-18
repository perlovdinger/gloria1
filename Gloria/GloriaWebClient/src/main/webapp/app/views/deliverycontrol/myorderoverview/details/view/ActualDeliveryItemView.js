define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'bootstrap',
		'jquery.fileupload',
		'datepicker',
		'moment',
		'i18next',
		'hbs!views/deliverycontrol/myorderoverview/details/view/actual-delivery'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, fileupload, Datepicker, moment, i18n, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ActualDeliveryItemView = Marionette.LayoutView.extend({

			initialize : function(options) {
	    		 this.model = options.model;
	    	},
	    	
	    	className: "panel panel-default",
	    	
	    	render : function() {    	
			 	this.$el.html(compiledTemplate({ 
	 				'data' : this.model ? this.model.toJSON() : {}
	 		    }));
			    return this;
			}
		});
	});

	return Gloria.DeliveryControlApp.View.ActualDeliveryItemView;
});
