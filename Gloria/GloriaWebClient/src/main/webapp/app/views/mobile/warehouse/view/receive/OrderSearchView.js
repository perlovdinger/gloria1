define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery-validation',
		'hbs!views/mobile/warehouse/view/receive/order-search'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Validation, compiledTemplate) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.OrderSearchView = Marionette.View.extend({

			initialize : function(options) {
				options || (options = {});
				this.search = options.search;
				this.deliveryNoteModel = options.deliveryNoteModel;
				this.listenTo(Gloria, 'scanner:scanned', this.onScan);
			},
			
			onScan: function(scannedData) {
				var ifValidScan = scannedData && (scannedData.type == '128' || scannedData.type == '39');
				if(ifValidScan && scannedData.data) {					
					this.$('#partNumber').val(scannedData.data);
					this.orderLookup();
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
							Backbone.history.loadUrl('warehouse/receive/' + that.search);
						}
					},
					search : {
						label : 'Gloria.i18n.buttons.search',
						className : "btn-primary rightAlign",
						callback : function(e) {
							e.preventDefault();
							that.orderLookup();
						}
					}
				};
			},

			orderLookup : function() {
				if (this.isValidForm()) {
					var partNumberOrAlias = this.$('#partNumber').val();
					$('#search').attr('disabled', 'disabled');
					Gloria.WarehouseApp.trigger('Receive:search:orderline', partNumberOrAlias, this.search);
				}
			},
			
			validator : function() {
				var that = this;
				return this.$el.find('form').validate({
					rules: {
						'partNumber': {
							required: true
						}
					},
					messages: {						
						'partNumber': {
							required: i18n.t('Gloria.i18n.warehouse.receive.validation.searchStringRequired')
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

			showErrors : function(errors) {
				this.hideErrors();
				_.each(errors, function(error) {
					var controlGroup = this.$('.' + error.element.id);
					controlGroup.addClass('has-error');
				}, this);
			},

			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
			},

			render : function() {
				this.$el.html(compiledTemplate({
					module : this.search == 'regular' ? 'regular' : 'transferreturn',
				}));
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			}
		});
		
	});

	return Gloria.WarehouseApp.Receive.View.OrderSearchView;
});
