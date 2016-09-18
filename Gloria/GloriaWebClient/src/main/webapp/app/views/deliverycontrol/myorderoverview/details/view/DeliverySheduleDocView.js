define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery.fileupload',
		'bootstrap',
		'hbs!views/deliverycontrol/myorderoverview/details/view/delivery-shedule-doc'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, FileUpload, Bootstrap, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.DeliverySheduleDocView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.isEditable = options.isEditable;
				this.module = options.module;
				this.collection = options.collection;
				this.collection.off('add remove change');
			},
			
			events : {
	            'click .quality-docs' : 'deleteQualityDoc'
	        },
	        
	        deleteQualityDoc : function(evt) {
				var docId = evt.currentTarget.id;
				Gloria.DeliveryControlApp.trigger('DeliverySchedule:deleteAttachedDoc', this.collection, docId);
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					metas : this.collection ? this.collection.toJSON() : [],
					isEditable : this.isEditable && this.module != 'completed'
				}));
				return this;
			},
			
			onShow : function() {
				this.collection.on('add remove change', this.render);
			}
		});
	});

	return Gloria.DeliveryControlApp.View.DeliverySheduleDocView;
});
