define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'backbone.syphon',
		'jquery-validation',
		'utils/UserHelper',
		'hbs!views/mobile/warehouse/view/move/move'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Syphon, Validation, UserHelper, compiledTemplate) {

	Gloria.module('WarehouseApp.Move.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MoveView = Marionette.View.extend({
				
			initialize : function(options) {
				this.listenTo(Gloria, 'scanner:scanned', this.move);
				this.addValidatorMethod();
			},

			addValidatorMethod : function() {
				var isLessThanBalance = _.bind(this.isLessThanBalance,	this);
				Backbone.$.validator.addMethod('isLessThanBalance', function(val, element) {
					return isLessThanBalance(val, element);
				});
			},

			template : compiledTemplate,

			buttonEvents : function() {
				return {
					previous : {
						label : 'Gloria.i18n.buttons.previous',
						className : "btn-primary leftAlign",
						callback : _.bind(this.previous, this)
					},
					move : {
						label : 'Gloria.i18n.buttons.move',
						className : "btn-primary rightAlign",
						isHidden : !UserHelper.getInstance().hasPermission('edit', ['MobileMove']),
						callback : _.bind(this.move, this)
					}
				};
			},

			previous : function(e) {
				e.preventDefault();
				Gloria.trigger('reloadPage');
			},

			move : function(e) {
				e.preventDefault();
				if (this.isValidForm(this.validationSettings())) {
					var formData = Backbone.Syphon.serialize(this);
					Gloria.WarehouseApp.trigger('move:inventory', formData, this.model);
				}
			},

			isLessThanBalance : function(val, element) {
				var stockBalance = parseInt(this.model.get('quantity') || 0);
				return (val && parseInt(val) <= stockBalance);
			},

			validationSettings : function() {
				return {
					rules : {
						moveQuantity : {
							required : true,
							isLessThanBalance : true,
							digits : true
						}
					},
					messages : {
						moveQuantity : {
							required : '',
							isLessThanBalance : i18n.t('Gloria.i18n.warehouse.inventory.validation.moveQtyIsMoreThanStockBalance'),
							digits : i18n.t('Gloria.i18n.validInput')
						}
					},
					showErrors : _.bind(this.showErrors, this),
					onfocusin : false,
					onfocusout : false,
					onkeyup : false,
					onclick : false
				};
			},

			validator : function(options) {
				var validator = this.$('form').validate(options);
				return validator;
			},

			isValidForm : function(options) {
				return this.validator(options).form();
			},

			showErrors : function(errorsMap, errorsList) {
				this.hideErrors();
				_.each(errorsList,	function(error) {
					var controlGroup = this.$('.' + error.element.id);
					controlGroup.addClass('has-error');
					controlGroup.find('.help-inline').text(error.message);
				}, this);
			},

			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
				this.$('.help-inline').empty();
			},

			render : function() {
				this.$el.html(this.template({
					data : this.model.toJSON(),
					hasEditAccess : UserHelper.getInstance().hasPermission('edit', ['MobileMove'])
				}));
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.Move.View.MoveView;
});
