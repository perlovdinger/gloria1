define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
        'hbs!views/deliverycontrol/common/delivery-control-module' 
], function(Gloria, $, _, Handlebars, Backbone, Marionette, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.DeliveryControlModuleView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
			},

			render : function() {
				//this.$el.html(compiledTemplate());
				Gloria.trigger('show:breadcrumb:moduleView');					
				return this;
			}
		});
	});
	
	return Gloria.DeliveryControlApp.View.DeliveryControlModuleView;
});
