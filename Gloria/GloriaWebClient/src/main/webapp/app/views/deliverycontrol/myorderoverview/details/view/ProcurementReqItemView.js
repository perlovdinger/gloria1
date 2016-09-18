define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'i18next',
	    'hbs!views/deliverycontrol/myorderoverview/details/view/procurement-req'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementReqItemView = Marionette.ItemView.extend({
			
			initialize : function(options){
	    		 this.reqModel = options.model;
	    	},
	    	
	    	className: "panel panel-default",
	    	
	    	formattedFinalWhSiteNames : function(finalWhSiteNames) {
            	var formattedWhSiteNames = '';
            	if(finalWhSiteNames) {            		
            		_.each(finalWhSiteNames, function(whSiteName) {
            			formattedWhSiteNames = formattedWhSiteNames + (formattedWhSiteNames ?  ', ' : '') + whSiteName;
					});
            	}
            	return formattedWhSiteNames;
			},
	    	
	    	render : function() {      	
			 	this.$el.html(compiledTemplate({ 
	 				'data' : this.reqModel ? this.reqModel.toJSON() : {},
	 				warehouseSiteNames : this.formattedFinalWhSiteNames(this.model.get('finalWhSiteNames'))
	 		    }));
			    return this;
			}
	    	
	    });
	});

    return Gloria.DeliveryControlApp.View.ProcurementReqItemView;
});
