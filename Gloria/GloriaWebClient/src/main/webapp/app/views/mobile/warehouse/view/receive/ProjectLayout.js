define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'bootstrap',
        'hbs!views/mobile/warehouse/view/receive/to-receive'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Bootstrap, Template) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ProjectLayout = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
				this.deliveryNoteID = options.deliveryNoteID;
			},

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
							Backbone.history.loadUrl('warehouse/receive/' + that.module + '/' + that.deliveryNoteID);
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

	return Gloria.WarehouseApp.Receive.View.ProjectLayout;
});
