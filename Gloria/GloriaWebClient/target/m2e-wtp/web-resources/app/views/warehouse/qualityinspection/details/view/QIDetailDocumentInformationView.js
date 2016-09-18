define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery.fileupload',
		'hbs!views/warehouse/qualityinspection/details/view/qidetail-document-information'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, FileUpload, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.QIDetailDocumentInformationView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
				this.collection = options.collection;
			},
			
			regions : {
				qualityDocInfo : '#qualityDocInfo'
			},			
			
			
			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {}
				}));
				return this;
			},
			
			onShow : function() {
				
			}
		});
	});

	return Gloria.WarehouseApp.View.QIDetailDocumentInformationView;
});
