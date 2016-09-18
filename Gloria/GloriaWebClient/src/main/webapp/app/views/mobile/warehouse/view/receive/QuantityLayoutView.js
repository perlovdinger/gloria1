define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'bootstrap',
		'i18next',
		'jquery-validation',
		'backbone.syphon',
		'utils/UserHelper',
		'hbs!views/mobile/warehouse/view/receive/quantity-layout'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, UserHelper, compiledTemplate) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.QuantityLayoutView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
				this.model = options.model;
				this.deliveryNoteID = options.deliveryNoteID;
			},

			regions : {
				qtyCollRegion : '#qtyCollRegion'
			},

			id : 'qty-collection',

			className : 'fixedMargin',
			
			events : {
				'focusin input.js-number' : 'resetQuantity',
				'focusout input.js-number' : 'setQuantity'
			},
			
			resetQuantity : function(e) {
				e.preventDefault();
				if(e.currentTarget.value == 0) {
					e.currentTarget.value = '';
				}
			},
			
			setQuantity : function(e) {
				e.preventDefault();
				if(e.currentTarget.value == '') {
					e.currentTarget.value = 0;
				}
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
					},
					print : {
						label : 'Gloria.i18n.buttons.printAllLabel',
						className : "btn-primary centerAlign",
						callback : _.bind(function(e) {
							e.preventDefault();
							Gloria.WarehouseApp.trigger('Receive:PartLabel:show', [this.model]);
						}, this)
					},
					receive : {
						label : 'Gloria.i18n.buttons.receive',
						className : "btn-primary rightAlign",
						isHidden : !UserHelper.getInstance().hasPermission('edit', ['MobileReceive']),
						callback : function(e) {
							e.preventDefault();
							if (that.isValidForm()) {
								$('#receive').addClass('disabled');
								var dmgQty = parseInt($('#quantityForm input[id^="damagedQuantity"]').val());
								if (dmgQty) {
									that.model.set('damagedQuantity', dmgQty);
									that.model.set('hasDamagedParts', true);
									that.model.set('problemDescription', 'Damaged Parts');
								} else {
									that.model.set('damagedQuantity', 0);
									that.model.set('hasDamagedParts', false);
									that.model.set('problemDescription', '');
								}
								Gloria.WarehouseApp.trigger('Receive:order:receive', that.model);
							}
						}
					}
				};
			},

			// Register Validation
			registerValidator : function() {
				this.$('#quantityForm input[id^="receivedQuantity"]').each(function() {
					$(this).rules('add', {
						required : true,
						positiveNumber : true,
						digits : true,
						messages : {
							required : i18n.t('Gloria.i18n.validInput'),
							positiveNumber : i18n.t('Gloria.i18n.validInput'),
							digits : i18n.t('Gloria.i18n.validInput')
						}
					});
				});

				this.$('#quantityForm input[id^="damagedQuantity"]').each(function() {
					$(this).rules('add', {
						isDmgQtyCorrect : true,
						digits : true,
						messages : {
							isDmgQtyCorrect : i18n.t('Gloria.i18n.validInput'),
							digits : i18n.t('Gloria.i18n.validInput')
						}
					});
				});
			},

			validator : function() {
				var that = this;
				Backbone.$.validator.addMethod('positiveNumber', function(value, element) {
					return Number(value) >= 0;
				});
				Backbone.$.validator.addMethod('isDmgQtyCorrect', function(value, element) {
					var receivedQtyValue = $('input[id^="receivedQuantity"]').val() || 0;
					var toShipQtyValue = 0;
					var dsTrueElement = $('input[id^="toShipQuantity"]');
					if (dsTrueElement.length == 1) {
						toShipQtyValue = dsTrueElement.val() || 0;
					}
					return parseInt(value) <= parseInt(receivedQtyValue) + parseInt(toShipQtyValue);
				});

				return this.$('#quantityForm').validate({
					ignore : null,
					onfocusin : false,
					onfocusout : false,
					onkeyup : false,
					onclick : false,
					showErrors : function(errorMap, errorList) {
						that.showErrors(errorList);
					}
				});
			},

			isValidForm : function() {
				var validator = this.validator();
				this.registerValidator();
				return validator.form();
			},

			showErrors : function(errors) {
				this.hideErrors();
				_.each(errors, function(error) {
					var controlGroup = $('#' + error.element.id).closest('div');
					controlGroup.addClass('has-error');
				}, this);
			},

			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
			},

			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					data : this.model.toJSON(),
					transferReturn : this.module == 'transfer' || this.module == 'return' || this.module == 'returntransfer',
					hasEditAccess : UserHelper.getInstance().hasPermission('edit', ['MobileReceive'])
				}));
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			},

			onShow : function() {
				if (this.model.get('qiMarking') == 'MANDATORY') {
					this.$el.find('#damagedQuantityNonQI').hide();
				}
			},

			onDestroy : function() {
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.Receive.View.QuantityLayoutView;
});
