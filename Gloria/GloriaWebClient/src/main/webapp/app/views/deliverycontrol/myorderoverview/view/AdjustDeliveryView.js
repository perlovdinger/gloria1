define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'i18next',
		'bootstrap',
		'datepicker',
		'moment',
		'views/deliverycontrol/myorderoverview/view/AdjustDeliveryItemView'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, BootStrap, Datepicker, moment, AdjustDeliveryItemView) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AdjustDeliveryView = Marionette.CollectionView.extend({

			id : "delivery-list",
			
			initialize: function(options) {
				this.orderLine = options.model;
			    this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:AdjusttedDeliveries:clicked', this.onSave);
			},
			
			collectionEvents: {
			    'add remove change:expectedQuantity': 'triggerModifiedCollection'
			},

			childView : AdjustDeliveryItemView,
			
			childViewOptions: function(model, index) {			    
			    return {
			      model: model,
			      index: index
			    };
			},
			
			triggerModifiedCollection: function() {
			    Gloria.DeliveryControlApp.trigger('MyOrderOverview:update:split', this.collection);
			},
			
			onSave : function(options) {				
				Gloria.DeliveryControlApp.trigger('MyOrderOverview:saveAdjustedDeliveries', {orderLine: options.orderLine, deliveries: this.collection});
			},
			
			showWarningMessage : function() {
				if((this.orderLine.get('allowedQuantity')- this.orderLine.get('receivedQuantity')) != this.countTotalExpectedQty()) {
					$('#warningMessage').show();
				} else {
					$('#warningMessage').hide();
				}
				
				if(this.orderLine.get('deliveryDeviation')) {
					$('#deviationMessage').show();
				} else {
					$('#deviationMessage').hide();
				}
			},
			
			countTotalExpectedQty : function() {
				var count = 0;
				this.collection.each(function(model) {
					count += parseInt(model.get('expectedQuantity'), 10);
				});
				return count;
			},
			
			onShow: function() {
			    this.showWarningMessage();
			    this.triggerModifiedCollection();
			}
		});
	});

	return Gloria.DeliveryControlApp.View.AdjustDeliveryView;
});
