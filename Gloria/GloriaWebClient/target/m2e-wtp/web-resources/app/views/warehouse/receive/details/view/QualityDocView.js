define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery.fileupload',
		'bootstrap',
		'hbs!views/warehouse/receive/details/view/quality-doc'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, FileUpload, Bootstrap, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.QualityDocView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.collection.off('add remove change');
			},
			
			events : {
	            'click .quality-docs' : 'deleteQualityDoc'
	        },
	        
	        deleteQualityDoc : function(evt) {
				Gloria.trigger('hideAppMessageView');
				var docId = evt.currentTarget.id;
				Gloria.WarehouseApp.trigger('ReceiveDetails:DeleteQualityDoc', docId);
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					metas : this.collection ? this.collection.toJSON() : []
				}));
				return this;
			},
			
			onShow : function() {
				this.collection.on('add remove change', this.render);
			}
		});
	});

	return Gloria.WarehouseApp.View.QualityDocView;
});
