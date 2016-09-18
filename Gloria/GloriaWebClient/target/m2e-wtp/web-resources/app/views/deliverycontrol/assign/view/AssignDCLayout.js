define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'bootstrap',
		'hbs!views/deliverycontrol/assign/view/assign-dc-layout'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AssignDCLayout = Marionette.LayoutView.extend({

			regions : {
				moduleInfo : '#moduleInfo',
				teamInfo : '#teamInfoContainer',
				assignDC : '#assignDC'
			},
			
			events : {
				'change #teamInfoContainer' : 'handleTeamChange'
	        },
	        
	        className: 'row',
	        
	        handleTeamChange : function(e) {
	        	var thisUrl = Backbone.history.fragment.indexOf('order') > -1 ? 'deliverycontrol/assigndc/order' : 'deliverycontrol/assigndc';
	        	Backbone.history.navigate(thisUrl, {
	        		replace : true
	        	});
	        	Backbone.history.loadUrl(thisUrl);
			},

			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});

	return Gloria.DeliveryControlApp.View.AssignDCLayout;
});
