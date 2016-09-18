define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'hbs!views/warehouse/receive/details/view/transfer-information'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.TransferInformationView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			},

			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {}
				}));
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.TransferInformationView;
});
