define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'backbone.syphon',
		'jquery-validation',
		'hbs!views/mobile/warehouse/view/store/store'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Syphon, Validation, compiledTemplate) {

	Gloria.module('WarehouseApp.Store.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.StoreView = Marionette.View.extend({

			initialize : function(options) {
				options || (options = {});
				this.listenTo(Gloria.WarehouseApp, 'Store:materialline:notfound', this.noMaterialLineFound);
				this.listenTo(Gloria, 'scanner:scanned', this.onScan);
			},
			
			onScan: function(scannedData) {		
				if(!scannedData || !scannedData.data) return;
				if(scannedData && ('AZTEC' == scannedData.type || 'QR' == scannedData.type)) {
					var data;				
					try{
						data = JSON.parse(scannedData.data.trim() || '{}');
					} catch(e) {
						alert(e.message);
					}					
					this.$('input#partAffiliation').val(data.a);
					this.$('input#partNumber').val(data.no);
					this.$('input#partVersion').val(data.v);
					this.$('input#partName').val(data.n);
					this.$('input#partModification').val(data.m);
				} else {
					this.$('input#transportLabel').val(scannedData.data);
				}				
				this.next();				
			},
            
            events: {
				'shown.bs.collapse': 'updateIcon',
				'hidden.bs.collapse': 'updateIcon'
			},
			
			updateIcon: function(e) {
				var element;
				if(e.type == 'shown') {
					element = this.$(e.target).parent().find('.glyphicon-chevron-right');
					element.removeClass('glyphicon-chevron-right').addClass('glyphicon-chevron-down');
				} else {
					element = this.$(e.target).parent().find('.glyphicon-chevron-down');
					element.removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-right');
				}
			},

			buttonEvents : function() {				
				return {
					next : {
						label : 'Gloria.i18n.buttons.next',
						className : 'btn-primary rightAlign',
						callback : _.bind(this.next, this)
					}
				};
			},
			
			next: function(e) {
				e && e.preventDefault();
				this.$('div#partInfo').collapse('show');
				this.$('div#transportLabel').collapse('show');
				if (this.isValidForm()) {
					var formData = Backbone.Syphon.serialize(this).materialLine;
					Gloria.WarehouseApp.trigger('Store:material:search', formData);
				}
			},

			registerValidator : function() {
				this.$('#storeForm input[id^="partAffiliation"]').each(function() {
					$(this).rules('add', {
						partNumberTransportLabelRequired : true,
						messages : {
							partNumberTransportLabelRequired : i18n.t('Gloria.i18n.validInput')
						}
					});
				});
				this.$('#storeForm input[id^="partNumber"]').each(function() {
					$(this).rules('add', {
						partNumberTransportLabelRequired : true,
						messages : {
							partNumberTransportLabelRequired : i18n.t('Gloria.i18n.validInput')
						}
					});
				});				
				this.$('#storeForm input[id^="partVersion"]').each(function() {
					$(this).rules('add', {
						partNumberTransportLabelRequired : true,
						messages : {
							partNumberTransportLabelRequired : i18n.t('Gloria.i18n.validInput')
						}
					});
				});
				this.$('#storeForm input[id^="transportLabel"]').each(function() {
					$(this).rules('add', {
						partNumberTransportLabelRequired : true,
						messages : {
							partNumberTransportLabelRequired : i18n.t('Gloria.i18n.validInput')
						}
					});
				});
			},

			validator : function() {
				var that = this;
				Backbone.$.validator.addMethod('partNumberTransportLabelRequired', function(value, element) {
					var partNumberRequiredRequired = false;
					partNumberRequiredRequired = ($('input#transportLabel').val()) || ($('input#partAffiliation').val() 
							&& $('input#partNumber').val() && $('input#partVersion').val());
					return partNumberRequiredRequired;
				});

				return this.$('#storeForm').validate({
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

			noMaterialLineFound : function() {
				this.hideErrors();
				if (this.$('input#partAffiliation').val() || this.$('input#partNumber').val() || this.$('input#partVersion').val()
						|| this.$('input#partModification').val()) {
					this.$('div.form-group.partAffiliation').addClass('has-error');
					this.$('div.form-group.partNumber').addClass('has-error');
					this.$('div.form-group.partVersion').addClass('has-error');										
				}
				if (this.$('input#transportLabel').val()) {
					this.$('div.form-group.transportLabel').addClass('has-error');
				}
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
				this.$el.html(compiledTemplate());
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.Store.View.StoreView;
});
