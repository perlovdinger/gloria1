define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'bootstrap',
		'jquery-validation',
		'backbone.syphon',
		'utils/dialog/dialog',
		'hbs!views/warehouse/receive/details/view/receive-details'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Bootstrap, Validation, Syphon, Dialog, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ReceiveDetailsView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.listenTo(Gloria.WarehouseApp, 'ReceiveDetails:showaccordion', this.hideAccordion);
			},
			
			regions : {
				partInfoContent : '#partInfoContent',
				orderInfoContent : '#orderInfoContent',
				transferInfoContent : '#transferInfoContent',
				shipInfoContent : '#shipInfoContent',
				returnInfoContent : '#returnInfoContent',
				receiveInfoContent : '#receiveInfoContent',
				documentsInfoContent : '#documentsInfoContent',
				problemInfoContent : '#problemInfoContent'
			},

			events : {				
				'click #printLabel' : 'handlePrintLabel',
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick',
				'input #damagedQuantity' : 'checkDamagedPartsCheckbox'
			},
			
			hideAccordion : function(model) {
				var receiveType = model.get('receiveType') && model.get('receiveType').toUpperCase();
				switch (receiveType) {
				case 'TRANSFER':					
					$('#PartInfo').show();
					$('#orderInfo').remove();
					$('#transferInfo').show();
					$('#shipInfo').remove();
					$('#returnInfo').remove();
					$('#receiveInfo').show();
					$('#documentsInfo').show();
					$('#problemInfo').show();
					break;
				case 'RETURN':
					$('#PartInfo').show();
					$('#orderInfo').remove();
					$('#transferInfo').remove();
					$('#shipInfo').show();
					$('#returnInfo').show();
					$('#receiveInfo').remove();
					$('#documentsInfo').remove();
					$('#problemInfo').show();
					break;
				case 'RETURN_TRANSFER':
					$('#PartInfo').show();
					$('#orderInfo').remove();
					$('#transferInfo').remove();
					$('#shipInfo').show();
					$('#returnInfo').show();
					$('#receiveInfo').remove();
					$('#documentsInfo').remove();
					$('#problemInfo').show();
					break;
				default: // REGULAR
					$('#PartInfo').show();
					$('#orderInfo').show();
					$('#transferInfo').remove();
					$('#shipInfo').remove();
					$('#returnInfo').remove();
					$('#receiveInfo').show();
					$('#documentsInfo').show();
					$('#problemInfo').show();
					break;
				}
			},

			handleCancelClick : function() {
				Dialog.show({
					title: i18n.t('Gloria.i18n.warehouse.receive.details.text.cancel.headerReceiveDetails'),
					message: '<b>' + i18n.t('Gloria.i18n.warehouse.receive.details.text.cancel.mainTextReceiveDetails') + '</b>'
								+ '<br/><br/>' + i18n.t('Gloria.i18n.warehouse.receive.details.text.cancel.subTextReceiveDetails'),
					buttons: {
		                yes: {
		                    label: i18n.t('Gloria.i18n.buttons.yes'),
		                    className: "btn btn-primary",
		                    callback: function(e) {
		                        e.preventDefault();
		                        window.history.back();
		                        return true;
		                    }
		                },
		                no: {
		                    label: i18n.t('Gloria.i18n.buttons.no'),
		                    className: "btn btn-default",
		                    callback: function(e) {
		                        e.preventDefault();
		                        return false;
		                    }
		                }
		            }
				});
			},
			
			handleSaveClick : function(e) {
				e.preventDefault();
				if (this.isValidForm()) {					
					var formData = Backbone.Syphon.serialize(this);
					Gloria.WarehouseApp.trigger('ReceiveDetails:save', formData.deliveryNoteLine);
				}
			},
			
			handlePrintLabel : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('ReceiveDetails:printPL');
			},
			
			checkDamagedPartsCheckbox: function() {
				if(Number($('#damagedQuantity').val()) > 0) {
					$('#hasDamagedParts').prop('checked', true);
				} else {
					$('#hasDamagedParts').prop('checked', false);
				}
			},
			
			validator : function() {				

				$.validator.addMethod('positiveNumber', function(value, element) {
					return Number(value) >= 0;
				});
				
				$.validator.addMethod('valueGreaterThanZero', function(value, element) {
					return Number(value) > 0;
				});
				$.validator.addMethod('dQLessThanEqualToOQ', function(value, element) {
					return Number(value) <= Number($('#orderLineQuantityHidden').val());
				});
				
				$.validator.addMethod('problemDescRequired', function(value, element) {		
					var isProblemDescRequiredCheck = true;					
					var isValueGreaterThanZero = Number($('#damagedQuantity').val()) > 0;
					if(isValueGreaterThanZero){
						var isProblemDescription = false;
						if($('#problemDescription').val()){
							isProblemDescription = true;
						}
						isProblemDescRequiredCheck = isValueGreaterThanZero && isProblemDescription;
					}	
					return isProblemDescRequiredCheck;
				});
				
				return this.$el.find('form').validate({
					ignore: '',
					rules: {
						'deliveryNoteLine[deliveryNoteQuantity]': {
							valueGreaterThanZero: true,
						    digits: true
						},
						'deliveryNoteLine[damagedQuantity]': {
							positiveNumber: true,
							dQLessThanEqualToOQ: true,
						    digits: true
						},
						'deliveryNoteLine[problemDescription]': {
							problemDescRequired: true
						}
					},
					messages: {						
						'deliveryNoteLine[deliveryNoteQuantity]': {
							valueGreaterThanZero: i18n.t('errormessages:errors.GLO_ERR_046'),
							required: i18n.t('errormessages:errors.GLO_ERR_046'),
							digits: i18n.t('errormessages:errors.GLO_ERR_046')
						},
						'deliveryNoteLine[damagedQuantity]': {
							positiveNumber: i18n.t('errormessages:errors.GLO_ERR_047'),
							dQLessThanEqualToOQ: i18n.t('errormessages:errors.GLO_ERR_048'),
							digits: i18n.t('errormessages:errors.GLO_ERR_047')
						},
						'deliveryNoteLine[problemDescription]': {							
							problemDescRequired: i18n.t('errormessages:errors.GLO_ERR_049')
						}
					},
					showErrors: function (errorMap, errorList) {
						Gloria.trigger('showAppMessageView', {
		        			type : 'error',
		        			title : i18n.t('errormessages:general.title'),
		        			message : errorList
		        		});
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},
			
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.ReceiveDetailsView;
});
