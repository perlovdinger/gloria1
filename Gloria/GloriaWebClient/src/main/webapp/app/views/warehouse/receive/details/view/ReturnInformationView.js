define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'datepicker',
        'moment',
		'hbs!views/warehouse/receive/details/view/return-information'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Datepicker, moment, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ReturnInformationView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			},

			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {}
				}));
				this.$('.date').datepicker();
				return this;
			},
			
			onDestroy: function() {
                this.$('.date').datepicker('remove');
            }
		});
	});

	return Gloria.WarehouseApp.View.ReturnInformationView;
});
