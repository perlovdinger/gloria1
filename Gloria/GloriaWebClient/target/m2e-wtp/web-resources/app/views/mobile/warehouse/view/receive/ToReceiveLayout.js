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

		View.ToReceiveLayout = Marionette.LayoutView.extend({

			initialize : function(options) {
				options || (options = {});
				this.module = options.module;
			},

			regions : {
				grid : '#grid'
			},
			
			className : 'fixedMargin',

			buttonEvents : function() {
				var that = this;
				return {
					previous : {
						label : 'Gloria.i18n.buttons.previous',
						className : 'btn-primary leftAlign',
						callback : function(e) {
							e.preventDefault();
							Backbone.history.navigate('warehouse/receive/' + that.module, {
								trigger : true,
								replace : true
							});
						}
					},
					print : {
						label : 'Gloria.i18n.buttons.printAllLabel',
						className : 'btn-primary centerAlign',
						callback : function(e) {
							e.preventDefault();
							Gloria.WarehouseApp.trigger('Receive:PartLabel:show');
						}
					},
					receiveLater : {
						label : 'Gloria.i18n.buttons.receiveLater',
						className : 'btn-primary rightAlign',
						callback : function(e) {
							e.preventDefault();
							Gloria.WarehouseApp.trigger('Receive:parts:later');
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

	return Gloria.WarehouseApp.Receive.View.ToReceiveLayout;
});
