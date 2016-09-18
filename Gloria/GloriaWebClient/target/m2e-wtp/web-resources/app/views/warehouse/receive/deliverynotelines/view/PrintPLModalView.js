define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'jquery-validation',
		'hbs!views/warehouse/receive/deliverynotelines/view/print-pl-modal'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PrintPLModalView = Marionette.View.extend({

			className : 'modal',

			id : 'PrintPLModal',

			events : {
				'change #printSelect' : 'handlePrintSelectChange',
				'click #print' : 'handlePrintClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.models = options.models;
			},
			
			handlePrintSelectChange : function(e) {
				e.preventDefault();
				this.hideErrors();
				if(e.currentTarget.value == '2') {
					$('#printQty').removeAttr('disabled');
				} else {
					$('#printQty').val('');
					$('#printQty').attr('disabled', 'disabled');
				};
			},
			
			handlePrintClick : function(e) {
				if (this.isValidForm()) {
					var printQty = $('#printQty').val();
					Gloria.WarehouseApp.trigger('DeliveryNoteLine:printPL:print', this.models, printQty);
					this.$el.modal('hide');
				}				
			},
			
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},
			
			validator : function() {
				var that = this;				
				return this.$el.find('form').validate({
					rules: {
						'printQty' : {
							required: true,
							digits: true,
							min : 1
						}
					},
					messages: {
						'printQty' : {
							required: i18n.t('errormessages:errors.GLO_ERR_044'),
							digits: i18n.t('errormessages:errors.GLO_ERR_044'),
							min: i18n.t('errormessages:errors.GLO_ERR_044')
						}
					},
					showErrors: function (errorMap, errorList) {
			        	that.showErrors(errorList);
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},

			showErrors : function(errorList) {
				this.hideErrors();
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					modal : true,
					title : i18n.t('errormessages:general.title'),
					message : errorList
				});
			},

			hideErrors : function() {
				Gloria.trigger('hideAppMessageView');
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate());
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
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.PrintPLModalView;
});
