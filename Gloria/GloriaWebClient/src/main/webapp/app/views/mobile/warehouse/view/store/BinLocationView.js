define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'i18next',
		'backbone.syphon',
		'jquery-validation',
		'hbs!views/mobile/warehouse/view/store/bin-location'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Syphon, Validation, compiledTemplate) {
	
	Gloria.module('WarehouseApp.Store.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.BinLocationView = Marionette.View.extend({

			initialize : function(options) {
				this.listenTo(Gloria.WarehouseApp, 'Store:BinLocation:invaid', this.invaidBinLocation);
			},

			invaidBinLocation : function() {
				this.hideErrors();
				var binLocField = $('div.form-group.suggestedBinLocation');
				binLocField.addClass('has-error');
				binLocField.find('.help-inline').text(i18n.t('Gloria.i18n.warehouse.store.validation.invalidBinLocation'));
			},

			events : {
				'focusout #suggestedBinLocation' : 'checkIfBinLocationExist'
			},

			checkIfBinLocationExist : function(e) {
				e.preventDefault();
				if (this.$('#suggestedBinLocation').val().trim().length == 0) {
					this.disableStore();
				} else {
					this.enableStore();
				}
			},

			disableStore : function() {
				if (!$('#store').hasClass('disabled')) {
					$('#store').addClass('disabled');
				}
				this.hideErrors();
			},

			enableStore : function() {
				if ($('#store').hasClass('disabled')) {
					$('#store').removeClass('disabled');
				}
				this.hideErrors();
			},

			buttonEvents : function() {
				var that = this;
				return {
					previous : {
						label : 'Gloria.i18n.buttons.previous',
						className : 'btn-primary leftAlign',
						callback : function(e) {
							e.preventDefault();
							Gloria.WarehouseApp.trigger('Store:ToStoreGridView:show');
						}
					},
					store : {
						label : 'Gloria.i18n.buttons.store',
						className : "btn-primary rightAlign disabled",
						callback : function(e) {
							e.preventDefault();
							if (that.isValidForm()) {
								var formData = Backbone.Syphon.serialize(that);
								Gloria.WarehouseApp.trigger('Store:BinLocationView:save', formData);
							}
						}
					}
				};
			},

			validator : function() {
				var that = this;
				return this.$el.find('#binlocation').validate({
					rules : {

					},
					messages : {

					},
					showErrors : function(errorMap, errorList) {
						that.showErrors(errorList);
					}
				});
			},

			isValidForm : function() {
				return this.validator().form();
			},

			showErrors : function(errors) {
				this.hideErrors();
				_.each(errors, function(error) {
					var controlGroup = this.$('.' + error.element.id);
					controlGroup.addClass('has-error');
					controlGroup.find('.help-inline').text(error.message);
				}, this);
			},

			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
				this.$('.help-inline').text('');
			},

			render : function() {
				this.$el.html(compiledTemplate());
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.Store.View.BinLocationView;
});
