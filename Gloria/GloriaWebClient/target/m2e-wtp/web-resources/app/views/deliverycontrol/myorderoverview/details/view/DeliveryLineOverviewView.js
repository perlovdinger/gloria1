define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
        'bootstrap',
        'datepicker',
        'moment',        
        'views/deliverycontrol/myorderoverview/details/view/DeliveryLineDetailView'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, Datepicker, moment, DeliveryLineDetailView) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

	    View.DeliveryLineOverviewView = Marionette.CollectionView.extend({
	    	
	    	id: "poDeliveryLineOverviewList",
	    	
	    	className: "panel-group",
	        
	    	childView: DeliveryLineDetailView,      
	        
	    	initialize : function(options){
	        	this.deliveryLineOverviewCollection = options.collection;        	        	
	        }
	    });
	});
	
    return Gloria.DeliveryControlApp.View.DeliveryLineOverviewView;
});