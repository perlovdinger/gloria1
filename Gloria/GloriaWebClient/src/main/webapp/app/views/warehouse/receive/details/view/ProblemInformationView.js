define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery.fileupload',
		'hbs!views/warehouse/receive/details/view/problem-information'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, FileUpload, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ProblemInformationView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
				this.collection = options.collection;
			},
			
			regions : {
				problemDocInfo : '#problemDocInfo'
			},
			
			hideError : function() {
				Gloria.trigger('hideAppMessageView');
			},
			
			showError : function(errorMessage, errors) {
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'),
					message : errorMessage
				});
			},
			
			configureProblemDocFileUpload : function() {
				var that = this;
				$('#problemdocupload').fileupload({
					url : '/documents/v1/deliverynotelines/' + that.model.id + '/problemdoc',
					dataType : 'json',
					singleFileUploads : 'true',
					add : function(e, data) {
						that.hideError();
						data.submit();
					},
					progressall : function(e, data) {
						
					},
					done : function(e, data) {
						that.collection.add(data.result);
					},
					fail : function(e, data) {

					},
					validationError : that.showError
				});
			},

			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {}
				}));
				return this;
			},
			
			onShow : function() {
				this.configureProblemDocFileUpload();
			}
		});
	});

	return Gloria.WarehouseApp.View.ProblemInformationView;
});
