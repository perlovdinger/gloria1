define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'bootstrap',
		'datepicker',
		'moment',
		'i18next',
		'jquery.fileupload',
		'views/deliverycontrol/myorderoverview/details/view/DeliveryScheduleItemView'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, Datepicker, moment, i18n, FileUpload, DeliveryScheduleItemView) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.DeliveryScheduleView = Marionette.CollectionView.extend({

			id : "DeliveryScheduleList",

			className : "panel-group",

			childView : DeliveryScheduleItemView,
			
			childViewOptions: function() {
				var that = this;
			    return {
			    	isEditable : this.isEditable && that.module != 'completed',
			    	expectedDate: this.expectedDate
			    };
			},

			initialize : function(options) {
				this.collection = options.collection;
				this.isEditable = options.isEditable;
				this.module = options.module;
				// If Orderline.staAgreedDate is not set, then expectedDate for each DeliverySchedule need to be set as Orderline.staAgreedDate when it changes for the first time
				// Here is a request to get the value of Orderline.staAgreedDate, if it has been empty and it is changed then its value set as expected date as initial value
				this.expectedDate = Gloria.reqres.request("OrderLineDetail:staAgreedDate");
			}
		});
	});

	return Gloria.DeliveryControlApp.View.DeliveryScheduleView;
});
