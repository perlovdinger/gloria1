define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'datepicker',
        'moment',
		'hbs!views/warehouse/receive/details/view/receive-information'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Datepicker, moment, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ReceiveInformationView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			},
			
			getDeliveryNoteQuantity : function() {
				if(this.model.get('version') == 1) {
					return this.getTotalToReceiveQty();
				} else {
					return this.model.get('deliveryNoteQuantity');
				}
			},
			
			getTotalToReceiveQty : function() {
				var toReceiveQty = 0;
				_.each(this.model.directsends.models, function(directsend) {
					toReceiveQty = toReceiveQty + directsend.get('toReceiveQty');
				});
				return toReceiveQty;
			},

			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model.toJSON(),
					deliveryNoteQuantity : this.getDeliveryNoteQuantity(),
					damagedQuantity : this.getTotalToReceiveQty(),
					directSend : Handlebars.helpers.t('Gloria.i18n.warehouse.receive.directSend.' + this.model.get('directSend')),
					dangerousGoodsFlag : Handlebars.helpers.t('Gloria.i18n.warehouse.receive.dangerousGoodsFlag.' + this.model.get('dangerousGoodsFlag'))
				}));
			 	this.$('.date').datepicker();
				return this;
			},
            
            onDestroy: function() {
                this.$('.date').datepicker('remove');
            }
		});
	});

	return Gloria.WarehouseApp.View.ReceiveInformationView;
});
