define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'hbs!views/material/details/view/attachment-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AttachmentsInfoView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			},
			
			regions : {
				receiveDocInfo : '#receiveDocInfo',
				problemDocInfo : '#problemDocInfo',
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

	return Gloria.WarehouseApp.View.AttachmentsInfoView;
});
