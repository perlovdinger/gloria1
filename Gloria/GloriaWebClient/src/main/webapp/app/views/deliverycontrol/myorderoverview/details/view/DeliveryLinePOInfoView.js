define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
	    'bootstrap',
	    'hbs!views/deliverycontrol/myorderoverview/details/view/delivery-line-po-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, compiledTemplate) {
	
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

	    View.DeliveryLinePOInfoView = Marionette.LayoutView.extend({
	    	
	    	initialize : function(options){
	    		 this.model = options.model;
	    		 this.module = options.module;
		         this.isEditable = options.isEditable;
	    	},
	    	
	    	regions :{
	    		poDelSchedule: '#poDelSchedule',
	    		poActualDel: '#poActualDel',
	    	},
	    	
	        render : function() {	        	
	        	this.$el.html(compiledTemplate({
	        		data : this.model ? this.model.toJSON() : [],
	        		isDisabled : !this.isEditable || this.module == 'completed',
	        		show : (this.model.get('internalExternal') == 'EXTERNAL' && this.model.get('status') != 'PLACED') 
	        					|| this.model.get('internalExternal') == 'INTERNAL'
	        	}));
	            return this;
	        }
	    });
	});
    
	return Gloria.DeliveryControlApp.View.DeliveryLinePOInfoView;
 });
