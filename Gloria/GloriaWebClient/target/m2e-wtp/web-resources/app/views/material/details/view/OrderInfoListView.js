define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
	    'bootstrap',
	    'i18next',
	    'views/material/details/view/OrderInfoView'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, i18n, OrderInfoView) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.OrderInfoListView = Backbone.Marionette.CollectionView.extend({
	        
			id: 'OrderInfoList',
			
			childView: OrderInfoView,
			
	    	initialize : function(options) {
	    		this.collection = options.collection;
	    	}
	    });
	});
    
	return Gloria.MaterialApp.View.OrderInfoListView;
 });
