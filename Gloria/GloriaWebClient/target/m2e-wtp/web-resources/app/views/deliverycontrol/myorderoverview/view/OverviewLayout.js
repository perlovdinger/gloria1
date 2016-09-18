define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'bootstrap',
	    'hbs!views/deliverycontrol/myorderoverview/view/overview-layout'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

	    View.OverviewLayout = Marionette.LayoutView.extend({
	    	
	    	className: 'row',
	
	    	regions : {
    			moduleInfo : '#moduleInfo',    			
    			userSelector: 'div#userInfoContainer',
	    		externalButton : '#externalButton',
	    		externalGrid : '#externalGrid',
	    		internalButton : '#internalButton',
	    		internalGrid : '#internalGrid',
	    		completedButton : '#completedButton',
	    		completedGrid : '#completedGrid'
			},
	
			events : {
				'click #myOrderOverviewTab > ul > li > a' : 'handleOverviewTabClick',
				'change #userInfoContainer select' : 'handleUserChange'
	        },
	        
	        initialize : function(options) {
	        	this.module = options.module;
	        },

	        handleOverviewTabClick : function(e) {
	        	var tab = e.currentTarget.hash.split("#")[1];
	        	Backbone.history.navigate('deliverycontrol/myOrderOverview/' + tab, {
					trigger : true
				});
			},
			
			handleUserChange : function(e) {
				e.preventDefault();
				var value = e.currentTarget.value;
				var userId = value.split(';')[0];
				var userTeam = value.split(';')[1];
				Gloria.DeliveryControlApp.trigger('MyOrderOverview:user:change', userId, userTeam);
			},
			
	        render : function() {
	            this.$el.html(compiledTemplate({
	            	module : this.module
	            }));
	            return this;
	        }
	    });
	});
	
    return Gloria.DeliveryControlApp.View.OverviewLayout;
});
