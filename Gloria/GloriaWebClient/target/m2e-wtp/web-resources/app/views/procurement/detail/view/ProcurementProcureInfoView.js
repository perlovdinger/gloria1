/** 
 * ProcurementProcureInfoView is a Subview in ProcureLineDetail Page in Gloria 
 */
define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'datepicker',
        'moment',
        'select2',
        'i18next',
        'utils/DateHelper',
        'utils/typeahead/CompanyCodeTypeaheadView',
        'utils/typeahead/GLAccountTypeaheadView',
        'utils/typeahead/WBSTypeaheadView',
        'utils/typeahead/CostCenterTypeaheadView',
        'utils/typeahead/SAPTypeaheadView',
        'views/procurement/helper/PurchaseOrgSelector',
		'views/procurement/common/buyercode/BuyerCodeSelector',
		'views/procurement/detail/view/SupplierCounterPartSelectorHelper',
        'views/procurement/common/qualitydocument/QualityDocumentSelector',
        'views/procurement/common/consignor/ConsignorSelector',
        'utils/handlebars/UnitOfPriceSelectorHelper',
		'utils/handlebars/CurrencySelectorHelper',
        'views/procurement/helper/PartAliasSelector',
		'hbs!views/procurement/detail/view/procurement-procure-info',
        'utils/backbone/GloriaCollection',
        'utils/UserHelper'/*,
        'models/ProcureLineFlagModel'*/
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Datepicker, moment, select2, i18n, DateHelper,
		CompanyCodeTypeaheadView, GLAccountTypeaheadView, WBSTypeaheadView, CostCenterTypeaheadView, SAPTypeaheadView,
		PurchaseOrgSelector, BuyerCodeSelector, supplierCounterPartSelectorHelper, qualityDocumentSelectorhelper, ConsignorSelector,
		UnitOfPriceSelectorHelper, currencySelectorHelper, PartAliasSelector, compiledTemplate, Collection, UserHelper/*, ProcureLineFlagModel*/) {
	
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ProcurementProcureInfoView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
				this.hasRolePI = options.hasRolePI;
				this.hasRoleDELIVERY = options.hasRoleDELIVERY;
				this.procureLineModel = options.procureLineModel;
				if(this.hasRolePI && this.procureLineModel.get('procureType') != 'INTERNAL' && this.procureLineModel.get('procureType') != 'FROM_STOCK') {
					this.procureLineModel.set('procureType', 'INTERNAL');
				};
				//this.hasRolePI && this.procureLineModel.set('procureType', 'INTERNAL'); // ONLY INTERNAL is applicable for PROCURE-INTERNAL
				this.status = options.status;
				this.isDisabled = (this.procureLineModel.get('status') === 'PROCURED' 
                    || this.procureLineModel.get('status') === 'PLACED' 
                    || this.procureLineModel.get('status') === 'RECEIVED'
                    || this.procureLineModel.get('status') === 'RECEIVED_PARTLY') ? true : false;
				this.listenTo(Gloria.ProcurementApp, 'procurement:editmode', this.enableEditMode);
				this.listenTo(Gloria.ProcurementApp, 'procureLineform:source:changed', this.updateViewOnSourceChange);
				this.listenTo(Gloria.ProcurementApp, 'showFromStockMessage', this.showFromStockMessage);
				//this.listenTo(this.procureLineModel, 'edit:started', this.changeEvent);
				//this.listenTo(this.procureLineModel, 'edit:ended', this.changeEvent);
				//this.procureLineModel.on('change', this.changeEvent);
			},
			
			regions : {
				fromStock : '#fromStock',
				fromStockMessage : '#fromStockMessage',
				companyCodeSelectorContainer : 'div#companyCodeSelectorContainer',
				glAccountSelectorContainer : 'div#glAccountSelectorContainer',
				wbsCodeSelectorContainer : 'div#wbsCodeSelectorContainer',
				costCenterSelectorContainer : 'div#costCenterSelectorContainer',
				internalOrderNoSAPSelectorContainer : 'div#internalOrderNoSAPSelectorContainer'
			},
			
			events : {
				'click .flagClass' : 'handleFlagChange',
			    'show .date' : 'stopPropagation',
                'change #procureType' : 'sourceChangeHandler',                
                'change input#consignorId' : 'consignorChangeHandler',
                'change input#purchaseOrganisationCode' : 'handlePurchaseOrgChange',
                'change select[id^="supplierCounterPartId"]' : 'handleSupplierCounterPartChange',
                'input input#additionalQuantity' : 'handleAdditionalQuantityChange'
            },
            
            handleFlagChange : function(e) {
				this.refreshFlagDropdown(e);
			},
			
			refreshFlagDropdown : function(e) {
				var currentFlagDropdown = $(e.currentTarget.parentElement).closest('div.dropdown');
	    		var hiddenInputName = "procureLine[statusFlag]" ;
	    		
				this.$el.find('.flagClass').unbind('click');	
				this.$el.find('.dropdown.open .dropdown-toggle').dropdown('toggle');
				var attributes = this.$el.find('#' + e.currentTarget.id + ' a i').prop("attributes");
				currentFlagDropdown.find('#flagSelect').empty();
	    		$.each(attributes, function() {
	    			currentFlagDropdown.find('#flagSelect').attr(this.name, this.value);
	    		}); 
	    		var selectedText = this.$el.find('#' + e.currentTarget.id + ' a').text();
	    		currentFlagDropdown.find('#flagSelectText').text(selectedText);    	
	    		this.selectedFlag = e.currentTarget.id;
	    		this.selectedFlag = this.selectedFlag == 'DEFAULT' ? '' : this.selectedFlag; 
	    		this.$el.find('input:hidden[name="'+ hiddenInputName+'"]').val( this.selectedFlag );    		
			},
			
            sourceOptions : [{
                "INTERNAL" : 'Gloria.i18n.procurement.sourceOptions.INTERNAL'
            }, {
                "EXTERNAL" : 'Gloria.i18n.procurement.sourceOptions.EXTERNAL'
            }, {
                "FROM_STOCK" : 'Gloria.i18n.procurement.sourceOptions.FROM_STOCK'
            }],
            
            stopPropagation : function(e) {
                e.stopPropagation();
                e.stopImmediatePropagation();
            },
            
            enableEditMode: function(editable) {
            	if(this.procureLineModel.get('status') === 'PLACED' && this.procureLineModel.get('procureType') === 'EXTERNAL') return;
				this.glAccountTypeaheadView(this.procureLineModel.get('companyCode'), false);
				this.wbsTypeaheadView(this.procureLineModel.get('companyCode'), this.procureLineModel.get('projectId'), false);
				this.costCenterTypeaheadView(this.procureLineModel.get('companyCode'), false);
				this.sapTypeaheadView(false);
				if(this.procureLineModel.get('procureType') === 'EXTERNAL') {
					this.$('#buyerCodeContainer').html('<input id="buyerCode" class="form-control" name="procureLine[buyerCode]" value="'
							+ this.procureLineModel.get('buyerCode') + '">');
					this.showBuyerCodes(this.procureLineModel.get('purchaseOrganisationCode'), false);
				}
            },
            
            showFromStockMessage : function (){
                $('#fromStockMessage').removeClass('hidden');
            },
            
            changeEvent:function() {
            	alert('change fired ???');
            },
            
            consignorChangeHandler: function(e) {
            	if(this.consignorIdContainer) {
            		var procureLineModelId = this.procureLineModel.id;
            		var unitPriceEl = this.$('input#unitPrice');
            		var unitOfPriceEl = this.$('select#unitOfPrice_' + procureLineModelId);
            		var currencyEl = this.$('select#currency_' + procureLineModelId);
            		
            		unitPriceEl.val('0');
            		unitOfPriceEl.val('1'); //setting default value as "per 1"
            		currencyEl.val('');
            		
            		var attr = this.consignorIdContainer.currentView.resultMap.id;
            		var criteria = {};
            		criteria[attr] = e.added.id;
            		var selectedSupplierModel = this.consignorIdContainer.currentView.collection.findWhere(criteria);
            		if(!selectedSupplierModel || !selectedSupplierModel.get) return;
            		
            		if(selectedSupplierModel.get('unitPrice')) {
            			unitPriceEl.val(selectedSupplierModel.get('unitPrice'));
            		}
            		if(selectedSupplierModel.get('priceUnit')) {
            			unitOfPriceEl.val(selectedSupplierModel.get('priceUnit'));
            		}
            		if(selectedSupplierModel.get('currency')) {
            			currencyEl.val(selectedSupplierModel.get('currency'));
            		}
            		if(selectedSupplierModel.get('alias')) {
            			Gloria.ProcurementApp.trigger('procurelineDetails:consignorChanged', e, selectedSupplierModel);
            		}
            	}
            },
            
            handlePurchaseOrgChange: function(e) {
            	this.$('#buyerCode').select2('enable', true);
                this.showBuyerCodes(e.target.value);
            },
            
            handleSupplierCounterPartChange: function(e) {
            	var selectedValue = '';
            	if($('#' + e.currentTarget.id + ' :selected').val()) {
            		selectedValue = $('#' + e.currentTarget.id + ' :selected').text().split(' - ')[0];
            	}
				Gloria.ProcurementApp.trigger('procurelineDetails:ShipToAndPPSuffix:change', selectedValue);
			},
			
			handleAdditionalQuantityChange: function(e) {
				Gloria.ProcurementApp.trigger('procurelineDetails:AdditionalQuantity:change', e.currentTarget.value);
			},
			
            sourceChangeHandler : function(e) {
               Gloria.trigger('hideAppMessageView');
               this.updateViewOnSourceChange(e.currentTarget.value);
            },
            
            updateViewOnSourceChange : function(value) {
                var internalFields = '.js-internalFields';
                var externalFields = '.js-externalFields';
                var fromStockNotFields = '.js-fromStockNotFields';
                this.showFieldsBasedOnSource(value, internalFields, externalFields, fromStockNotFields);
                //this.suggestProcureType(value);	
            },
            
            isIPstatus : function (flag) {
            	if(_.contains(['PENDING_AGREEMENT','REVIEW_LATER'],flag)) {
            	return true; 
            	} else { return false; 
            	}
            },
            
          /*  getFlags : function () {
            var NonIpFlags = new ProcureLineFlagModel();
            var flagSet = NonIpFlags.getProcureLineNonIPFlags();
            return flagSet;
            },*/
            
            showFieldsBasedOnSource : function(procureTypeValue, internalFields, externalFields, fromStockNotFields) {
                if(procureTypeValue == "INTERNAL") {
                    $(externalFields).hide();
                    $(internalFields).show();
                    this.$el.find('#procureType').val('INTERNAL');
                    if(this.status){
                        $(internalFields).attr("disabled", "true");
                    }
                    if (!this.isDisabled) {
                        // Not read-only
                        this.showAliasSelector();
                    }
                } else if(procureTypeValue == "EXTERNAL" || procureTypeValue == "EXTERNAL_FROM_STOCK") {
                    $(internalFields).hide();
                    $(externalFields).show();
                    this.$el.find('#procureType').val('EXTERNAL');
                    if(this.status){
                        $(externalFields).attr("disabled", true);
                    }
                } else if(procureTypeValue == "INTERNAL_FROM_STOCK") {
                	this.$el.find('#procureType').val('INTERNAL');
                    $(internalFields).show();
                    $(externalFields).hide();
                } else if(procureTypeValue == 'FROM_STOCK') {
                	 $(internalFields).hide();
                	 $(externalFields).hide();
                	 $(fromStockNotFields).hide();
                }
            },
			            
			render : function() {
				var that = this;
				if (this.procureLineModel) {
					this.$el.html(compiledTemplate({
					    'isDisabled' : that.isDisabled,
						'procureLineData' : this.procureLineModel.toJSON(),
						'sourceOptions' : this.sourceOptions,
						'supplierCounterPartSelector' : supplierCounterPartSelectorHelper.handlebarsHelper,
						'unitOfPriceSelector': UnitOfPriceSelectorHelper,
						'currencySelector' : currencySelectorHelper,
						'supplierCounterPart' : this.supplierCounterPart,
						'qualityDocumentSelector' : qualityDocumentSelectorhelper,
						'isSourceDisabled' : this.isDisabled || this.hasRolePI,
						'hasRolePI' : this.hasRolePI,
						'isIPflag' : this.isIPstatus(this.procureLineModel.toJSON().statusFlag) ? true:false,
						'isMCflag' : this.isIPstatus(this.procureLineModel.toJSON().statusFlag) ? false:true,
						/*'flagList' : this.getFlags(),
						'flagStatus' : this.procureLineModel.toJSON().statusFlag,*/
						'hasRoleDELIVERY' : this.hasRoleDELIVERY,
						'hasDeliveryTeam' : UserHelper.getInstance().getUserAttribute('delFollowUpTeam') ? true : false,
						'procureType': this.procureLineModel.get('procureType') ? 'Gloria.i18n.procurement.sourceOptions.' 
										+ this.procureLineModel.get('procureType').toUpperCase() : ''
						
					}));
					//shoudnt initalize datepicker if it is readonly
					if(this.isDisabled) {
						this.$('.date').datepicker('remove');
					} else {
						var today = DateHelper.getNextLocalizedDate();
		                this.$('.requiredStaDateDiv').datepicker('setStartDate',today);
		                this.$('.orderStaDateDiv').datepicker();
		                //this.$('.date').datepicker();
					}
				}
				if((!that.isSourceDisabled)){
					var suggestedProcureType = that.procureLineModel.get('procureType') && that.procureLineModel.get('procureType').toUpperCase();
					that.suggestProcureType(suggestedProcureType);				
				};
				

               
				
				return this;
			},
			
			//Editable Mode for Source Dropdown in ProcureLinedetails Screen 
			suggestProcureType : function(suggestedProcureType){
				var that = this;				
				if(suggestedProcureType.substr(0,8) == "EXTERNAL"){
					that.$el.find('#procureType').val('EXTERNAL');
				}else if(suggestedProcureType.substr(0,8) == "INTERNAL"){
					that.$el.find('#procureType').val('INTERNAL');
				}
			},
			// CompanyCode Typeahead View
			companyCodeTypeaheadView : function() {
				this.companyCodeSelectorContainer.show(new CompanyCodeTypeaheadView({
					type : this.hasRolePI ? 'INTERNAL_PROCURE' : 'MATERIAL_CONTROL',
					el : this.$('#companyCode'),
					disabled : true
				}));
			},
			
			// GLAccount Typeahead View
			glAccountTypeaheadView : function(companyCode, isDisabled) {
				this.glAccountSelectorContainer.show(new GLAccountTypeaheadView({
					el : this.$('#glAccount'),
					companyCode : companyCode,
					disabled : isDisabled,
					select2Options: {
    					width: 'off'
    				}
				}));
			},
			
			// WBS Typeahead View
			wbsTypeaheadView : function(companyCode, projectId, isDisabled) {
				this.wbsCodeSelectorContainer.show(new WBSTypeaheadView({
					el : this.$('#wbsCode'),
					companyCode : companyCode,
					projectId : projectId,
					disabled : isDisabled,
					select2Options: {
    					width: 'off'
    				}
				}));
			},
			
			// CostCenter Typeahead View
			costCenterTypeaheadView : function(companyCode, isDisabled) {
				this.costCenterSelectorContainer.show(new CostCenterTypeaheadView({
					el : this.$('#costCenter'),
					companyCode : companyCode,
					disabled : isDisabled,
					select2Options: {
    					width: 'off'
    				}
				}));
			},
			
			// SAP Typeahead View
			sapTypeaheadView : function(isDisabled) {
				this.internalOrderNoSAPSelectorContainer.show(new SAPTypeaheadView({
					el : this.$('#internalOrderNoSAP'),
					disabled : isDisabled,
					select2Options: {
    					width: 'off'
    				}
				}));
			},
			
			disableSourceOptions : function() {
				$('[id^="procureType"]').find('option').each(function(i, $item) {
					if(this.value == 'FROM_STOCK') {
						$(this).attr({
							disabled: true, 
						    style: "display:none;visibility:hidden"
						});					
					}
				});
			},
			
            showAliasSelector: function() {
            	var regionName = 'aliasContainer';
            	if(!this[regionName]) {
        			this.addRegion(regionName, '#' + regionName);
        			var aliasComboboxView = new PartAliasSelector({
        				el: this.$('#alias'),
        				volvoPartNumber: this.procureLineModel.get('pPartNumber')           			
        			});
        			this[regionName].show(aliasComboboxView);
            	}
            },
            
            showPurchaseOrg : function() {
            	var regionName = 'purchaseOrganisationCodeContainer';
            	if(!this[regionName]) {
        			this.addRegion(regionName, '#' + regionName);
        			var purchaseOrgCombobox = new PurchaseOrgSelector({
        				el: this.$('#purchaseOrganisationCode'),
        				disabled : this.isDisabled,
        				select2Options: {
        					width: 'off'
        				}
        			});
        			this[regionName].show(purchaseOrgCombobox);
            	}
            },
            
            showBuyerCodes : function(purchaseOrgCode, isDisabled) {
            	var regionName = 'buyerCodeContainer';
            	if(!this[regionName]) {
        			this.addRegion(regionName, '#' + regionName);
            	}
            	var buyerCodeCombobox = new BuyerCodeSelector({
                    purchaseOrganisationCode : purchaseOrgCode,
                    el: this.$('#buyerCode'),
                    disabled : isDisabled,
                    select2Options: {
    					width: 'off'
    				}
                });
                this[regionName].show(buyerCodeCombobox);
            },

            showShipTo : function() {
                if (this.isDisabled) {
                    // Read-only
                    var that = this;
                    var supplierCounterPartOptions = supplierCounterPartSelectorHelper.constructSupplierCounterPartSelectorByCompanyCode(this.procureLineModel.get('companyCode'));
                    var supplierCounterPart = this.procureLineModel.get('supplierCounterPartID');
                    $.each(supplierCounterPartOptions, function(index, scp) {
                        if (scp['id'] == supplierCounterPart) {
                            that.$('#supplierCounterPartID').append(scp['shipToId'] + ' - ' + scp['ppSuffix']);
                            return false;
                        }
                    });
                } else {
                	if($('#supplierCounterPartId option[value!=""]').length == 1) {
                    	$('#supplierCounterPartId option:last-child').attr('selected', 'selected');
                    }
                }
            },
            
            showConsignorSelector: function(isDisabled) {
            	if(!isDisabled) {
            		var regionName = 'consignorIdContainer';
                	if(!this[regionName]) {
            			this.addRegion(regionName, '#' + regionName);
                	}
                	var consignorCombobox = new ConsignorSelector({
        				el : this.$('#consignorId'),
        				id : this.procureLineModel.id,
        				disabled : this.isDisabled,
        				select2Options: {
        					width: 'off'
        				},
        				bindTextElement: {
        					id: 'consignorName',
        					name: 'procureLine[supplierName]'
        				}
        			});
        			this[regionName].show(consignorCombobox);
            	}
            },

			onShow : function() {
				var that = this;
				this.companyCodeTypeaheadView();
				this.glAccountTypeaheadView(this.procureLineModel.get('companyCode'), true);
				this.wbsTypeaheadView(this.procureLineModel.get('companyCode'), this.procureLineModel.get('projectId'), true);
				this.costCenterTypeaheadView(this.procureLineModel.get('companyCode'), true);
				this.sapTypeaheadView(true);
				this.showPurchaseOrg();
				this.showBuyerCodes(this.procureLineModel.get('purchaseOrganisationCode'), this.isDisabled);
				// Disable Buyer Code Dropdown if purchaseOrganisationCode is not selected yet
				if(!this.procureLineModel.get('purchaseOrganisationCode')) {
					this.$('#buyerCode').select2('enable', false);
				}
				this.showShipTo();
				this.showConsignorSelector(that.isDisabled);
				var internalFields = '.js-internalFields';
				var externalFields = '.js-externalFields';
				var fromStockNotFields = '.js-fromStockNotFields';
                var procureTypeValue = this.procureLineModel.get('procureType');
                this.showFieldsBasedOnSource(procureTypeValue, internalFields, externalFields, fromStockNotFields);
                this.disableSourceOptions();      
               
                if(!that.isDisabled) {
                	var today = DateHelper.getNextLocalizedDate();
                    this.$('.requiredStaDateDiv').datepicker('setStartDate',today);
                    this.$('.orderStaDateDiv').datepicker();
                }
                
			},
			
		   onDestroy: function() {
                this.$('.date').datepicker('remove');
            }
		});
	});
	
	return Gloria.ProcurementApp.View.ProcurementProcureInfoView;
});
