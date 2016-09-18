define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'hbs!views/warehouse/setup/view/zone/zone-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ZoneInfoView = Marionette.LayoutView.extend({

			regions : {
				controlButtons : '#controlButtons',
				gridModule : '#gridModule'
			},

			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.ZoneInfoView;
});