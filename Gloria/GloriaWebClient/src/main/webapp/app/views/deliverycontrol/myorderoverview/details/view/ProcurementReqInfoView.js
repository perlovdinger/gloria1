define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'i18next',
	    'views/deliverycontrol/myorderoverview/details/view/ProcurementReqItemView'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, ProcurementReqItemView) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementReqInfoView = Marionette.CollectionView.extend({
	    	
	    	id: "ProcurementReqList",
	    	
	    	className: "panel-group",
	        
	    	childView: ProcurementReqItemView,
	        
	    	initialize : function(options) {
	        	this.procureReqCollection = options.collection;
	        },
	        
	        onShow : function() {
				if(this.procureReqCollection.length > 0) {
					var firstModel = this.procureReqCollection.at(0);
					var accordionId = '#ReqInfoAccordion' + firstModel.get('id');
					this.$el.find(accordionId).addClass('in');
				}
			}
	    });
	});

    return Gloria.DeliveryControlApp.View.ProcurementReqInfoView;
});
