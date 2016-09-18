define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery.fileupload',
		'hbs!views/deliverycontrol/myorderoverview/details/view/document-information'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, FileUpload, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.DocumentInformationView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			},
			
			regions : {
				qualityDocInfo : '#qualityDocInfo'
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {}
				}));
				return this;
			}
		});
	});

	return Gloria.DeliveryControlApp.View.DocumentInformationView;
});
