define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'views/warehouse/receive/deliverynotelines/controller/OverviewController'
], function(Gloria, $, _, Handlebars, Marionette, OverviewController) {
    
	Gloria.module('WarehouseApp.Receive.DeliveryNoteLineInformation', function(DeliveryNoteLineInformation, Gloria, Backbone, Marionette, $, _, OverviewController) {
		
		this.startWithParent = false;
		
		DeliveryNoteLineInformation.on('start', function(options) {
			options || (options = {});
			if(!options.deliveryNoteModel) return;			
			this.overviewController = new OverviewController();
			this.overviewController.control(options);
		});
		
		DeliveryNoteLineInformation.on('stop', function() {
			this.overviewController && this.overviewController.destroy();
			this.overviewController = null;
		});
		
	}, OverviewController);	
	
	return Gloria.WarehouseApp.Receive.DeliveryNoteLineInformation;
});
