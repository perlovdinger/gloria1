define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars',
        'marionette',
		'bootstrap',
		'jquery-validation',
		'hbs!views/warehouse/receive/view/cancel-good-receipt-dialog'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, Validation, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.CancelGoodReceiptDialogView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			},

			className : 'modal',

			id : 'cancel-modal',

			events : {
				'click #sendCancellation' : 'sendCancellation',
				'click #cancel' : 'cancel'
			},

			sendCancellation : function(e) {
				if (this.isValidForm()) {
					var formData = Backbone.Syphon.serialize(this);
					Gloria.WarehouseApp.trigger('Received:GoodReceipt:cancel', this.model, formData.data);
					this.$el.modal('hide');
				}
			},

			cancel : function() {
				this.$el.modal('hide');
			},

			validator : function() {
				var that = this;
				return this.$el.find('form').validate({
					rules : {
						'data[quantityCancelled]': {
							required: true,
			    			min: 1,
			    			max: that.model.get('quantity'),
			    			digits: true
						}
					},
					messages : {
						'data[quantityCancelled]': {
							required: i18n.t('errormessages:errors.GLO_ERR_012'),
							min: i18n.t('errormessages:errors.GLO_ERR_012'),
							max: i18n.t('errormessages:errors.GLO_ERR_012'),
							digits: i18n.t('errormessages:errors.GLO_ERR_012')
						}
					},
					showErrors : function(errorMap, errorList) {
						that.showErrors(errorList);
					}
				});
			},

			isValidForm : function() {
				return this.validator().form();
			},

			showErrors : function(errorList) {
				Gloria.trigger('showAppMessageView', {
					modal : true,
					type : 'error',
					message : errorList
				});
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					data : that.model ? that.model.toJSON() : {}
				}));
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
			},

			onShow : function() {
				this.$el.modal('show');
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
			}
		});
	});

	return Gloria.WarehouseApp.View.CancelGoodReceiptDialogView;
});