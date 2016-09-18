/**
 * ProcureLineDetail Page in Gloria
 * 
 * This is the Parent view- which loads subviews like GeneralPartInfo, WarehouseInfo, ProcureInfo, RequestInfo
 */
define(['app',
	    'jquery',    
	    'underscore',
        'backbone',
	    'i18next',
	    'handlebars',
	    'marionette',
	    'jquery-validation',
	    'backbone.syphon',
	    'utils/DateHelper',
        'hbs!views/procurement/detail/view/procurement-detail'       
], function(Gloria, $, _, Backbone, i18n, Handlebars, Marionette, Validation, Syphon, DateHelper, compiledTemplate) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementDetailView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
			    this.procureType = null;
	            this.listenTo(Gloria.ProcurementApp, 'FromStockGrid:select', this.gridSelectHandler);
	            this.listenTo(Gloria.ProcurementApp, 'procurelineDetails:AdditionalQuantity:change', this.additionalQuantityChangeHandler);
	            this.listenTo(Gloria.ProcurementApp, 'procureLineModel:fetched', this.updateWarning);
	            this.listenTo(Gloria.ProcurementApp, 'procureLineModel:fetched', this.updateInformation);
	            this.materialLineKeys = '';
            },
	
	        regions : {
	        	procureGeneralPartInfo : '#procureGeneralPartInfo',
	        	procureWarehouseInfo : '#procureWarehouseInfo',
	        	procureProcureInfo : '#procureProcureInfo',
	        	procureRequestInfo : '#procureRequestInfo'
	        },
	
	        events : {	        	
	        	'click #cancel' : 'goToPreviousRoute',
	        	'click #save' : 'saveClickHandler',
	        	'click #procure' : 'procureClickHandler',
	        	'click #close' : 'goToPreviousRoute',
	        	'click #edit' : 'editClickHandler',
	        	'click #saveEditMode' : 'saveOnEditClickHandler'	        	
	        },
	        
	        updateWarning: function(model) {
	        	if(model && model.get('hasUnread')) {
	        		this.$('#warning').removeClass('hidden');
	        	} else {
	        		this.$('#warning').addClass('hidden');
	        	}
	        },
	        
	        updateInformation: function(model) {
	        	if(model && model.get('procureCommmentExist')) {
	        		this.$('#information').removeClass('hidden');
	        	} else {
	        		this.$('#information').addClass('hidden');
	        	}
	        },
	        
	        gridSelectHandler : function(selectedModels, procureLineModel) {	
	        	this.setCriteriaList(selectedModels);
				this.setFromStockRequest(selectedModels, procureLineModel);
				if(selectedModels && selectedModels.length) {
					this.$('button#save').attr("disabled", true);
				} else {
					this.$('button#save').removeAttr("disabled");
				}
			},
			
			additionalQuantityChangeHandler : function(additionalQuantity) {
				if(/*this.quantitySelectedFromStockGrid && */this.selectedModels && this.procureLineModel) {
				    this.setFromStockRequest(this.selectedModels, this.procureLineModel, additionalQuantity);
				}
			},
			
			setCriteriaList: function(selectedModels) {
				var criteriaList = '';
				_.each(selectedModels, function(model) {
					criteriaList = (criteriaList ? criteriaList + ',' : '') + model.get('materialLineKeys');
				});
				this.materialLineKeys = criteriaList;
			},
			
			setFromStockRequest: function(selectedModels, procureLineModel, additionalQuantity) {
				this.selectedModels = selectedModels;
				this.procureLineModel = procureLineModel;
				this.usageQtyCounter = 0;
				this.additionalReleasedQtyCounter = 0;
				
				 /* TO CONDITION :&& (model.get('referenceId') == procureLineModel.get('referenceId')) */
				
				_.each(selectedModels,function(model) {
				    if((model.get('materialType') == 'USAGE')) {
				        this.usageQtyCounter += parseFloat(model.get('quantity'));
				        } else if ((model.get('materialType') == 'ADDITIONAL') && (model.get('projectId') != procureLineModel.get('projectId')))
				            {
				            this.usageQtyCounter += parseFloat(model.get('quantity'));
				        }else {
				            this.additionalReleasedQtyCounter += parseFloat(model.get('quantity'));
				        }
				},this);
				
				var additionalQuantitys = this.$('input#additionalQuantity');
                var additionalQty = additionalQuantity || (additionalQuantitys && additionalQuantitys.length && additionalQuantitys.val()) || 0;
                if(additionalQty && !isNaN(additionalQty)) {
                    additionalQty = parseInt(additionalQty, 10);
                } else {
                    additionalQty = 0;
                }   
                
				this.updateFromStockQty(procureLineModel,additionalQty);
				this.updateFromStockUsageQty(procureLineModel,additionalQty);
				
				
				
				if((this.additionalReleasedQtyCounter + this.usageQtyCounter) >=  procureLineModel.get('usageQty') + additionalQty) { // Update View
                    procureLineModel.set('procureType', 'FROM_STOCK');
                    this.selectedProcureType = 'FROM_STOCK';
                    $('#procureType').val('FROM_STOCK').attr('disabled', 'disabled');
                    $('#procureTypeSpan').text(i18n.t('Gloria.i18n.procurement.sourceOptions.FROM_STOCK'));
                    $('.js-fromStockNotFields').each(function(index, el) {
                        $(el).hide();
                    });
                    Gloria.ProcurementApp.trigger('procureLineform:finalwarehouse:hide', true);
                } else if(this.selectedProcureType == 'FROM_STOCK') {
                    procureLineModel.set('procureType', this.procureType);
                    this.selectedProcureType = this.procureType;
                    $('select#procureType').val(procureLineModel.get('procureType')).removeAttr('disabled');
                    $('.js-fromStockNotFields').each(function(index, el) {
                        $(el).show();
                    });
                    Gloria.ProcurementApp.trigger('procureLineform:source:changed', procureLineModel.get('procureType'));
                    Gloria.ProcurementApp.trigger('procureLineform:finalwarehouse:hide', false);
                }
			},
			
			updateFromStockQty: function(procureLineModel, additionalQty) {
			    if(!this.procureType) {
		             this.procureType = procureLineModel.get('procureType');
			    }
				$('#fromStockQty').text(Math.min(this.additionalReleasedQtyCounter,(procureLineModel.get('usageQty') + additionalQty)));
			},
			

			updateFromStockUsageQty: function(procureLineModel, additionalQty) {
                if(!this.procureType) {
                     this.procureType = procureLineModel.get('procureType');
                }
                
                var totalquantity = procureLineModel.get('usageQty') + additionalQty;
                var borrowQuantityfinal =  Math.min((totalquantity - Math.min(this.additionalReleasedQtyCounter,(procureLineModel.get('usageQty') + additionalQty))),this.usageQtyCounter);
                $('#fromStockProjectQty').text(borrowQuantityfinal);
            },
	        
	        openAllAccordions: function() {
	            this.$('.panel-collapse').each(function(){
                    var currentAccordion = $(this);
                    if($(this).hasClass('collapse')){
                        $(this).removeClass('collapse').addClass('in');
                        $(this).css( "height", "auto" );
                    }                   
                });
	        },
												        
			saveIfValid : function() {
				var that = this;
				var formData = Backbone.Syphon.serialize(this);
				if(!formData.procureLine.procureType && that.selectedProcureType) {
					_.extend(formData.procureLine, {procureType : that.selectedProcureType});
				}
				Gloria.ProcurementApp.trigger('procureLineform:submit', formData.procureLine, formData.internalInfo,
					formData.externalInfo, _.toArray(formData.procureRequest));
			},

	        saveClickHandler : function(e) {
	            e.preventDefault();
	            this.openAllAccordions();
	            if(this.saveClickValidatior()) {
		           this.saveIfValid();
	            }
	        },
	        
	        saveOnEditClickHandler : function(e) {
	        	e.preventDefault();
	        	this.openAllAccordions();
	            if(this.saveOnEditClickValidatior()) {
	            	this.saveIfValid();
	            }
			},
			
			saveOnEditClickValidatior : function() {
				var validator = this.validator();
				$("#glAccount").rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_004')
	    			}
	        	});
				$("#wbsCode").rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_005')
	    			}
	        	});
				$("#costCenter").rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_006')
	    			}
	        	});
	        	return validator.form();
			},
	        
	        procureClickHandler : function(e) {
	            e.preventDefault();
	            var that = this;
	            this.openAllAccordions();
	            if(this.isValidForm()) {
	            	var formData = Backbone.Syphon.serialize(this);
	            	 if(!formData.procureLine.procureType && that.selectedProcureType) {
	 					_.extend(formData.procureLine, {procureType : that.selectedProcureType});
	 				}
	            	Gloria.ProcurementApp.trigger('procureLineform:submit', formData.procureLine, formData.internalInfo,
	            		formData.externalInfo,  _.toArray(formData.procureRequest), 'procure', that.materialLineKeys);
	            }
	        },
	        
	        saveClickValidatior: function() {
	        	var validator = this.validator();
	        	this.unregisterValidator();	
	        	var additionalQuantitys = this.$('input#additionalQuantity');
	        	additionalQuantitys && additionalQuantitys.length && additionalQuantitys.rules('add', {
                    digits: true,
                    messages: {
                        digits: i18n.t('errormessages:errors.GLO_ERR_023')
                    }
                });
	        	return validator.form();
	        },
	        
	        editClickHandler: function(e) {
	            e.preventDefault();
	            Gloria.ProcurementApp.trigger('procurement:editmode', true);
	            this.$('button#edit').addClass('hidden');
	            this.$('button#saveEditMode').removeClass('hidden');
	            this.$('button#close').hide();
	            this.$('button#cancel').show();
	        },
	
	        goToPreviousRoute : function() {
	        	Gloria.trigger('goToPreviousRoute');
	        },
	        
	        // Register Validation
	        registerValidator : function() {
	            if ($('select#procureType').length) {
    	            $('select#procureType').rules('add', {
    	    			required: true,
    	    			messages: {
    	    				required: i18n.t('errormessages:errors.GLO_ERR_014')
    	    			}
    	        	});
	            }
	            $('input#purchaseOrganisationCode').rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_015')
	    			}
	        	});
	    		$('input#buyerCode').rules('add', {
	    			required: true,
	    			digits: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_016'),
	    				digits: i18n.t('errormessages:errors.GLO_ERR_016')
	    			}
	        	});
	    		$('select[id^="supplierCounterPartId"]').rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_017')
	    			}
	        	});
	    		$('input#consignorId').rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_018')
	    			}
	        	});
	    		$('input#orderNo').rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_019')
	    			}
	        	});
	    		$('input#orderStaDate').rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_069')
	    			}
	        	});
	    		$('input#requiredStaDate').rules('add', {
	        		required: true,
	        		GPSdate: true,
	        		PreviousDateCheck:true,
                    messages: {
                    	required: i18n.t('errormessages:errors.GLO_ERR_007'),
                    	GPSdate: i18n.t('errormessages:errors.GLO_ERR_007'),
                    	PreviousDateCheck: i18n.t('errormessages:errors.GLO_ERR_076')
                    }
	        	});
				$('input#unitPrice').rules('add', {
					required : function(element) {
						var isPriceRequired = true;
						if ($('select[id^="currency_"]').val() != '') {
							var allPriceElements = $('.js-price');
							allPriceElements.each(function() {
								var currentPriceField = $(this);
								if (currentPriceField.val() != '0') {
									isPriceRequired = false;
								}
							});
						}
						return isPriceRequired;
					},
					messages : {
						required : i18n.t('errormessages:errors.GLO_ERR_020')
					}
				});
				$('.js-price').each(function(){
	    			$(this).rules('add', {
	    				min: 0,
		    			messages: {
		    				min: i18n.t('errormessages:errors.GLO_ERR_021')
		    			}
	    			});
	    		});
				$('select#currency').each(function() {
    				$(this).rules('add', {
    					required : function(element) {
						var validPriceInAtleastOneField = true;
						var priceElements = $('.js-price');
						priceElements.each(function() {
							var currentPriceElement = $(this);
							if (currentPriceElement.val() != '') {
									validPriceInAtleastOneField = false;
								}
							});
						return validPriceInAtleastOneField;
						},
						messages : {
							required : i18n.t('errormessages:errors.GLO_ERR_022')
						}
					});
				});
				$('input#additionalQuantity').length && $('input#additionalQuantity').rules('add', {
    				min: 0,	    				
	    			messages: {
	    				min: i18n.t('errormessages:errors.GLO_ERR_023')
	    			}
				});
				$('select#dangerousGoods').rules('add', {
	    			required: true,
	    			messages: {
	    				required: i18n.t('errormessages:errors.GLO_ERR_024')
	    			}
	        	});
				if($('select[id^="warehouseId_"]').length) {
					$('select[id^="warehouseId_"]').rules('add', {
						required: true,
						messages: {
							required: i18n.t('errormessages:errors.GLO_ERR_025')
						}
					});
				}
				if($('select#qualityDocument').length) {
					$('select#qualityDocument').each(function(){
						$(this).rules('add', {
							required: false
						});
					});
				}
			},
	        
			unregisterValidator : function() {
    			var procureTypes = this.$('select#procureType');
    			procureTypes && procureTypes.length && procureTypes.rules('remove');			
				
				var warehouseIds = this.$('select[id^="warehouseId_"]');
				warehouseIds && warehouseIds.length && warehouseIds.rules('remove');
				
				var supplierCounterPartIds = this.$('select#supplierCounterPartId');
				supplierCounterPartIds && supplierCounterPartIds.length && supplierCounterPartIds.rules('remove');
				
				var consignorIds = this.$('input#consignorId');
				consignorIds && consignorIds.length && consignorIds.rules('remove');
				
				var qualityDocuments = this.$('select#qualityDocument');
				qualityDocuments && qualityDocuments.length && qualityDocuments.rules('remove');
				
				var dangerousGoods = this.$('select#dangerousGoods');
				dangerousGoods && dangerousGoods.length && dangerousGoods.rules('remove');
				
				var additionalQuantitys = this.$('input#additionalQuantity');
				additionalQuantitys && additionalQuantitys.length && additionalQuantitys.rules('remove');
				
				var requiredStaDate = this.$('input#requiredStaDate');
				requiredStaDate && requiredStaDate.length && requiredStaDate.rules('remove');
				
				var prices = this.$('.js-price');
				prices && prices.length && prices.rules('remove');
				
				var unitPrices = this.$('input#unitPrice');
				unitPrices && unitPrices.length && unitPrices.rules('remove');
				
				var currencys = this.$('select#currency');
				currencys && currencys.length && currencys.rules('remove');
				
				var purchaseOrganisationCode = this.$('input#purchaseOrganisationCode');
				purchaseOrganisationCode && purchaseOrganisationCode.length && purchaseOrganisationCode.rules('remove');
				
				var buyerCodes = this.$('input#buyerCode');
				buyerCodes && buyerCodes.length && buyerCodes.rules('remove');
				
				var orderNo = this.$('input#orderNo');
				orderNo && orderNo.length && orderNo.rules('remove');
			},
			
	        validator : function() {
	        	Backbone.$.validator.addMethod('GPSdate', function(value, element) {		
    				var isValidGPSDate= true;
    				var isSourceExternal = $('#procureType').val() == "EXTERNAL";	    				
    				if(isSourceExternal){
	    				var selectedDate = new Date(value);
	    				var today = new Date();
	    	    		var futureYear = (selectedDate.getFullYear() - today.getFullYear());
	    	    		if(futureYear>3){
	    	    			isValidGPSDate= false;
	    	    		}else if(futureYear<3){
	    	    			isValidGPSDate= true;
	    	    		}else{
	    	    			 var futureMonth = (selectedDate.getMonth() + 1);
	    	    			 var currentMonth= (today.getMonth() + 1);
	    	    			 if(futureMonth>currentMonth){
	 	    	    			isValidGPSDate= false;
	    	    			 }else if(futureMonth<currentMonth){
	 	    	    			isValidGPSDate= true;
	    	    			 }else{
	    	    				 var futureDate = selectedDate.getDate();
	    	    				 var currentDate = today.getDate();
	    	    				 if(futureDate>currentDate){
	 	 	    	    			isValidGPSDate= false;
	    	    				 }else{
	 	 	    	    			isValidGPSDate= true;
 	 	    	    			}
	    	    			 }
	    	    		}
    				}
    				return isValidGPSDate;
	        	});
	        	
	          Backbone.$.validator.addMethod('PreviousDateCheck', function(value, element) {      
                return DateHelper.isDateInThePast(value);
                });

				return $('form').validate({
					ignore: ':hidden, .select2-focusser.select2-offscreen',
					rules: {
						
					},
					messages: {
						
					},
			        showErrors: function (errorMap, errorList) {
			        	if(errorList.length > 0) {
			        		Gloria.trigger('showAppMessageView', {
			        			type : 'error',
			        			title : i18n.t('errormessages:general.title'),
			        			message : errorList
			        		});
			        	}
			        },
			        onfocusin: false,
			        onfocusout: false,
			        onkeyup: false,
			        onclick: false
				});
			},
			
			isValidForm : function() {
				var validator = this.validator();
				this.unregisterValidator();
				this.registerValidator();
				return validator.form();
			},
	        
	        render : function() {
	            this.$el.html(compiledTemplate());  
	            return this;
	        }
	    });
	});
	
    return Gloria.ProcurementApp.View.ProcurementDetailView;
});
