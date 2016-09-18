define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery.fileupload',
		'bootstrap',
		'hbs!views/warehouse/qualityinspection/details/view/qidetail-inspection-doc'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, FileUpload, Bootstrap, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.QIDetailInspectionDocView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.collection.off('add remove change');
			},
			
			events : {
	            'click .problem-docs' : 'deleteProblemDoc'
	        },
	        
	        deleteProblemDoc : function(evt) {
				Gloria.trigger('hideAppMessageView');
				var docId = evt.currentTarget.id;
				Gloria.WarehouseApp.trigger('QualityInspectionDetails:DeleteProblemDoc', docId);
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

	return Gloria.WarehouseApp.View.QIDetailInspectionDocView;
});
