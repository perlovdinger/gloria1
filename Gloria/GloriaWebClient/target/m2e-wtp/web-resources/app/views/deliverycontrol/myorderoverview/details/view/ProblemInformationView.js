define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery.fileupload',
		'hbs!views/deliverycontrol/myorderoverview/details/view/problem-information'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, FileUpload, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ProblemInformationView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			},
			
			regions : {
				problemDocInfo : '#problemDocInfo'
			},

			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {}
				}));
				return this;
			}
		});
	});

	return Gloria.DeliveryControlApp.View.ProblemInformationView;
});
