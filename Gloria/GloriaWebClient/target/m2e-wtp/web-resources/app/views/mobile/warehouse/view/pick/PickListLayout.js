define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'bootstrap',
		'hbs!views/mobile/warehouse/view/pick/pickList-layout'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Bootstrap, Template) {

	Gloria.module('WarehouseApp.Pick.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PickListLayout = Marionette.LayoutView.extend({

			regions : {
				grid : '#grid'
			},
			
			className : 'fixedMargin',

			render : function() {
				this.$el.html(Template());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.Pick.View.PickListLayout;
});