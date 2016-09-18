define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'moment',
	    'i18next',
	    'backbone.syphon',
	    'hbs!views/deliverycontrol/myorderoverview/details/view/procurement-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, moment, i18n, Syphon, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementInfoView = Marionette.LayoutView.extend({
		        
	        regions : {
	        	procGenInfo : '#procGenInfo',
	            procReqInfo : '#procReqInfo'
	        },
	
	        render : function() {
	            this.$el.html(compiledTemplate());  
	            return this;
	        }
	    });
	});

    return Gloria.DeliveryControlApp.View.ProcurementInfoView;
});
