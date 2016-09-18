define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'bootstrap',
		'datepicker',
		'moment',
		'views/deliverycontrol/myorderoverview/details/view/ActualDeliveryItemView'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, Datepicker, moment, ActualDeliveryItemView) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ActualDeliveryView = Marionette.CollectionView.extend({

			id : "ActualDeliveryList",

			className : "panel-group",

			childView : ActualDeliveryItemView,
			
			initialize : function(options) {
				this.collection = options.collection;
			},
			
			evets : {
				//'click a[href^="#poActualDelivery"]' : 'handleActualDeliveryClick'
			},
			
			handleActualDeliveryClick : function(vent) {
				var id = vent.currentTarget.hash.split('#poActualDelivery')[1];
				Gloria.DeliveryControlApp.trigger('ActualDelivery:showDocuments', id);
				Gloria.DeliveryControlApp.trigger('ActualDelivery:showProblemDesc', id);
			},
			
			onShow : function() {
				var that = this;
				$('a[href^="#poActualDelivery"]').click(function(e) {
					if(e.currentTarget.className.indexOf('collapsed') != -1) {
						that.handleActualDeliveryClick(e);
					}
				});
			}
		});
	});

	return Gloria.DeliveryControlApp.View.ActualDeliveryView;
});
