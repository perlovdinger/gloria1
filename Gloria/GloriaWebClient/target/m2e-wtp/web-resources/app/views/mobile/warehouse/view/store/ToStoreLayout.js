define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'bootstrap',
		'utils/UserHelper',
		'hbs!views/mobile/warehouse/view/store/to-store-layout'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Bootstrap, UserHelper, Template) {

	Gloria.module('WarehouseApp.Store.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ToStoreLayout = Marionette.LayoutView.extend({

			regions : {
				grid : '#grid'
			},

			buttonEvents : function() {
				var that = this;
				return {
					previous : {
						label : 'Gloria.i18n.buttons.previous',
						className : "btn-primary leftAlign",
						callback : function(e) {
							e.preventDefault();
							Backbone.history.loadUrl('warehouse/store');
						}
					},
					storeSameBinLocation : {
						label : 'Gloria.i18n.buttons.storeSameBinLocation',
						className : "btn-primary rightAlign",
						isHidden : !UserHelper.getInstance().hasPermission('edit', ['MobileStore']),
						callback : function(e) {
							e.preventDefault();
							Gloria.WarehouseApp.trigger('Store:BinLocationView:show');
						}
					}
				};
			},

			render : function() {
				this.$el.html(Template());
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.Store.View.ToStoreLayout;
});
