define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'backbone.syphon',
        'jquery-validation',
        'utils/UserHelper',
        'hbs!views/mobile/warehouse/view/store/to-store'
],function(Gloria,  $, _, Handlebars, Backbone, Marionette, i18n, Syphon, Validation, UserHelper, compiledTemplate) {

	Gloria.module('WarehouseApp.Store.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ToStoreView = Marionette.View.extend({

			initialize : function(options) {
				this.model = options.model;
				this.whSiteId = options.whSiteId;
				this.listenTo(Gloria.WarehouseApp, 'Store:BinLocation:invaid', this.invaidBinLocation);
            	this.listenTo(Gloria, 'scanner:scanned', this.onScan);
            },
            
            onScan: function(options) {
                if(!options || !options.data) return;
                this.$('input#suggestedBinLocation').val(options.data);
            },
            
            invaidBinLocation : function() {
        	 	this.hideErrors();
        	 	var binLocField= $('div.form-group.suggestedBinLocation');
        	 	binLocField.addClass('has-error'); 
        	 	binLocField.find('.help-inline').text(i18n.t('Gloria.i18n.warehouse.store.validation.invalidBinLocation'));
            },
            
			buttonEvents  : function() {
				var that = this;
				return {
					previous : {
						label : 'Gloria.i18n.buttons.previous',
						className : "btn-primary leftAlign",
						callback : function(e) {
							e.preventDefault();			
							Gloria.WarehouseApp.trigger('Store:ToStoreGridView:show');
						}
					},
					store : {
						label : 'Gloria.i18n.buttons.store',
						className : "btn-primary rightAlign",
						isHidden : !UserHelper.getInstance().hasPermission('edit', ['MobileStore']),
						callback : function(e) {
							e.preventDefault();
							e.stopImmediatePropagation();
							if(that.isValidForm()) {
								var formData = Backbone.Syphon.serialize(that);								
								Gloria.WarehouseApp.trigger('Store:ToStoreView:save', formData, that.model);	
							}
						}
					}
				};
			},
			
			validator : function() {			
				var that = this;
				//Validation to check qty > 0
				$.validator.addMethod('isValidNumber', function(num, element) {
					num = num.replace(/\s+/g, '');
	                return this.optional(element) || num > 0;
	            });
				
				// Validation for storedQuantity quantity less than or equal to total quantity
				$.validator.addMethod('rQLessThanEqualToEQ', function(num, element) {
				    num = num.replace(/\s+/g, '');
				    var totalStoredQty = (parseInt(num));
				    var totalExpectedQty = parseInt(that.model.get('quantity'));
					return this.optional(element) || (totalStoredQty <= totalExpectedQty) ;					
	            });
				
				return this.$el.find('#binLocationForm').validate({
					rules: {						
						quantity: {
							required: true,
							rQLessThanEqualToEQ: true,
							isValidNumber: true,
						    digits: true
						}
					},
					messages: {						
						quantity: {
							required: i18n.t('Gloria.i18n.warehouse.receive.validation.receivedQtyRequired'),
							isValidNumber: i18n.t('Gloria.i18n.warehouse.receive.validation.receivedQtyRequired'),
							rQLessThanEqualToEQ: i18n.t('Gloria.i18n.warehouse.receive.validation.rQLessThanEqualToEQ'),
						    digits: i18n.t('Gloria.i18n.warehouse.receive.validation.receivedQtyRequired')
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
					controlGroup.find('.help-inline').text(error.message);
				}, this);
			},
	
			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
				this.$('.help-inline').text('');
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {},
					hasEditAccess : UserHelper.getInstance().hasPermission('edit', ['MobileStore'])
				}));
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			},
			
			onDestroy : function() {
				Gloria.WarehouseApp.trigger('Store:ToStoreView:unlock', this.model, this.whSiteId);
			}
		});
	});

	return Gloria.WarehouseApp.Store.View.ToStoreView;
});
