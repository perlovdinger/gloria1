define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'bootstrap',
	    'hbs!views/deliverycontrol/assign/view/assign-dc'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

	    View.AssignDCView = Marionette.LayoutView.extend({
	
	        initialize : function(options) {
	        	this.module = options.module;
	        	this.followUpType = options.followUpType;
	        },
	
	        events : {
				'click #assignOrReassignTab > ul > li > a' : 'handleAssignTabClick'
	        },
	        
	        handleAssignTabClick : function(e) {
	        	Backbone.history.navigate('deliverycontrol/assigndc/' + e.currentTarget.hash.split("#")[1], {
					trigger : true
				});
			},
	
	        render : function() {
	            this.$el.html(compiledTemplate({
	            	followUpType : this.followUpType
	            }));
	            return this;
	        },
	        
	        onShow : function() {
				var tabId = this.module ? '#assignOrReassignTab a[href="#' + this.module + '"]' : '#assignOrReassignTab a:first';
			    this.$(tabId).tab('show');
			}
	    });
	});
	
    return Gloria.DeliveryControlApp.View.AssignDCView;
});
