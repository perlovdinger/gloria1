define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'jquery-validation',
		'hbs!views/mobile/warehouse/view/receive/print-pl-modal'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PrintPLModalView = Marionette.View.extend({

			className : 'modal',

			id : 'PrintPLModal',

			events : {
				'change #printSelect' : 'handlePrintSelectChange',
				'touchstart #print' : 'handlePrintClick',
				'touchstart #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.collection = options.collection;
			},
			
			handlePrintSelectChange : function(e) {
				e.preventDefault();
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
					Gloria.WarehouseApp.trigger('Receive:PartLabel:print', this.collection, printQty);
					this.$el.modal('hide');
				}
			},
			
			handleCancelClick : function(e) {
				e.preventDefault();
				this.$el.modal('hide');
			},
			
			validator : function() {
				var that = this;				
				return this.$el.find('form').validate({
					showErrors: function (errorMap, errorList) {
			        	that.showErrors(errorList);
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},

			showErrors : function(errors) {
				this.hideErrors();
				if (errors.length != 0) {
					$('#appModalMessage').find('.help-inline').text(i18n.t('Gloria.i18n.validInput')).addClass('has-error');
				};
				_.each(errors, function(error) {
					var controlGroup = this.$('[name="' + error.element.name + '"]').closest('.form-group');
					controlGroup.addClass('has-error');
				}, this);
			},

			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
				$('#appModalMessage').find('.help-inline').text('');
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
