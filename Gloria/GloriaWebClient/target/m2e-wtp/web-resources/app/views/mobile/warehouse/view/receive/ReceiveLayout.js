define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'bootstrap',
		'hbs!views/mobile/warehouse/view/receive/receive-layout'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, compiledTemplate) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ReceiveLayout = Marionette.LayoutView.extend({

			regions : {
				regularInfo : '#regularInfo',
				transferInfo : '#transferInfo',
				returnInfo : '#returnInfo',
				returntransferInfo : '#returntransferInfo'
			},

			events : {
				'touchstart #receiveTab > ul > li > a' : 'handleReceiveTabClick'
			},

			initialize : function(options) {
				this.module = options.module;
			},

			handleReceiveTabClick : function(e) {
				var tab = e.currentTarget.hash.split("#")[1];
				Gloria.WarehouseApp.trigger('receive:TabChanged');
				Backbone.history.navigate('warehouse/receive/' + tab, {
					trigger : true
				});
			},

			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module
				}));
				return this;
			},

			onShow : function() {
				var tabId = '#receiveTab ' + (this.module ? 'a[href="#' + this.module + '"]' : 'a:first');
				this.$(tabId).tab('show');
			}
		});
	});

	return Gloria.WarehouseApp.Receive.View.ReceiveLayout;
});
