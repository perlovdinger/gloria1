define(['app',
        'jquery',
        'i18next',
        'handlebars',
        'underscore',
        'marionette',
		'bootstrap',
		'jquery-validation',
		'datepicker',
        'moment',
        'select2',
        'backbone.syphon',
        'utils/UserHelper',
        'utils/DateHelper',
        'views/procurement/helper/PurchaseOrgSelector',
        'views/procurement/common/buyercode/BuyerCodeSelector',
		'views/procurement/detail/view/SupplierCounterPartSelectorHelper',
        'views/procurement/common/qualitydocument/QualityDocumentSelector',
        'views/procurement/detail/view/WarehouseSelectorHelper',
        'views/procurement/common/consignor/ConsignorSelector',
		'hbs!views/procurement/overview/view/multiple-update'
], function(Gloria, $, i18n, Handlebars, _, Marionette, Bootstrap, Validation, Datepicker, moment, select2, Syphon, UserHelper, DateHelper, PurchaseOrgSelector,
		BuyerCodeSelector, SupplierCounterPartSelectorHelper, QualityDocumentSelectorhelper, WarehouseSelectorHelper, ConsignorSelector, compiledTemplate) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MultipleUpdateModalView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'forwardProcForm',
			
			purchaseOrganisationCodes : [{
	            'GTT' : 'GTT'
	        }],	

			events : {
				'click .flagClass' : 'handleFlagChange',
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick',
				'change input[id^="purchaseOrganisationCode"]' : 'handlePurchaseOrgChange',
				'change #procureType' : 'handleSourceChange'
			},
			  handleFlagChange : function(e) {
					this.refreshFlagDropdown(e);
				},
				
				refreshFlagDropdown : function(e) {
					var currentFlagDropdown = $(e.currentTarget.parentElement).closest('div.dropdown');
				//	var currentDeliveryId = currentFlagDropdown[0].id.match(/\d+/)[0];
		    		//var hiddenInputName = "procureLine[" + currentDeliveryId +"][statusFlag]" ;
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
            }],

			initialize : function(options) {
				this.models = options.models;
				this.companyCode = _.first(this.models).get('companyCode');
				this.hasRolePI = options.hasRolePI;
				this.hasRoleDELIVERY = options.hasRoleDELIVERY;
				this.listenTo(Gloria.ProcurementApp, 'procureline:multipleupdate:done', this.handleMultipleUpdate);
			},

			handleSaveClick : function(e) {
				var formData = Backbone.Syphon.serialize(this);
				Gloria.ProcurementApp.trigger('procureline:multipleupdate', this.models,
						formData.procureLine, formData.finalWhSiteId, formData.alsoProcure);
			},

			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},
			
			handleMultipleUpdate : function(flag) {
				if(flag) {
					this.$el.modal('hide');
				}
			},
			
			handlePurchaseOrgChange: function(e) {
            	this.$('#buyerCode').select2('enable', true);
                this.showBuyerCodes(e.target.value);
            },
            
            handleSourceChange : function(e) {
            	this.populate(e.currentTarget.value);
            },
             
            populate : function(type) {
            	this.showPurchaseOrg();
            	// Set Default to 1st; if there is only 1 "Ship to & PP Suffix" available in dropdown
				if($('#supplierCounterPartId > option').length == 2) { // Including "Please Select" option
					$('#supplierCounterPartId').prop('selectedIndex', 1); // pre-select 1st one
				}
	        	if(type == 'INTERNAL') {
	        		$('#consignorIdDiv').show();
					$('#orderNoDiv').show();
					$('#orderStaDateDiv').show();
	        		$('#purchaseOrganisationCodeDiv').hide();
					$('#buyerCodeDiv').hide();
					$('#qualityDocumentDiv').hide();
					$('#procureInfoDiv').hide();
	        	} else if(type == 'EXTERNAL') {
	        		$('#consignorIdDiv').hide();
					$('#orderNoDiv').hide();
					$('#orderStaDateDiv').hide();
	        		$('#purchaseOrganisationCodeDiv').show();
					$('#buyerCodeDiv').show();
					$('#qualityDocumentDiv').show();
					this.showBuyerCodes();
					this.$('#buyerCode').select2('enable', false);
					$('#procureInfoDiv').show();
	        	} else {
	        		$('#consignorIdDiv').hide();
					$('#orderNoDiv').hide();
					$('#orderStaDateDiv').hide();
	        		$('#purchaseOrganisationCodeDiv').hide();
					$('#buyerCodeDiv').hide();
					$('#qualityDocumentDiv').hide();
					$('#procureInfoDiv').hide();
	        	}
			},
			
			isInternalItemSelected : function() {
				var isInternalThere = false;
				_.each(this.models, function(model) {
					if(!isInternalThere) {
						var procureType = model.get('procureType');
						isInternalThere = procureType == 'INTERNAL' || procureType == 'INTERNAL_FROM_STOCK';
					}
				});
				return isInternalThere;
			},
			
			isExternalItemSelected : function() {
				var isExternalThere = false;
				_.each(this.models, function(model) {
					if(!isExternalThere) {
						var procureType = model.get('procureType');
						isExternalThere = procureType == 'EXTERNAL' || procureType == 'EXTERNAL_FROM_STOCK';
					}
				});
				return isExternalThere;
			},
			
			whichProcureType : function() {
				var isInternalType = this.isInternalItemSelected();
				var isExternalType = this.isExternalItemSelected();
				if(this.hasRolePI) {
					return 'INTERNAL';
				} else {
					return (isInternalType && isExternalType) ? 'INTERNAL_EXTERNAL' : (isInternalType ? 'INTERNAL' : 'EXTERNAL');
				}
			},
			
		    isIPstatus : function (flag) {
	            	var iPflags = ['PENDING_AGREEMENT','REVIEW_LATER']; //TODO : Pattern for accessing the variables from Flag Cell
	            	if(_.contains(this.ipflags,flag)) {
	            	return true; 
	            	} else { return false; 
	            	}
	            },

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					hasRolePI : this.hasRolePI,
					hasRoleDELIVERY : this.hasRoleDELIVERY,
					hasDeliveryTeam : UserHelper.getInstance().getUserAttribute('delFollowUpTeam') ? true : false,
					sourceOptions : this.sourceOptions,
					companyCode : that.companyCode,
					suggestedRequiredSTADate: this.getEarliestRequiredSTADate(),
					type : that.whichProcureType(),
					supplierCounterPartSelector : SupplierCounterPartSelectorHelper.handlebarsHelper,
					qualityDocumentSelector : QualityDocumentSelectorhelper,
					purchaseOrganisationCodes : that.purchaseOrganisationCodes,
					warehouseSelector : WarehouseSelectorHelper
				}));
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
				});
				var today = DateHelper.getNextLocalizedDate();
				this.$('.date').datepicker();
				this.$('.requiredStaDateDiv').datepicker('setStartDate',today);
				
				return this;
			},
			
			getEarliestRequiredSTADate: function() {
				return _.first(_.sortBy(this.models, function(model) {
					return model.get('requiredStaDate');
				})).get('requiredStaDate');
			},
			
			showPurchaseOrg : function() {
            	var regionName = 'purchaseOrganisationCodeContainer';
            	if(!this[regionName]) {
        			this.addRegion(regionName, '#' + regionName);
        			var purchaseOrgCombobox = new PurchaseOrgSelector({
        				el: this.$('#purchaseOrganisationCode')
        			});
        			this[regionName].show(purchaseOrgCombobox);
            	}
            },
            
            showBuyerCodes : function(purchaseOrgCode) {
            	var regionName = 'buyerCodeContainer';
            	if(!this[regionName]) {
        			this.addRegion(regionName, '#' + regionName);
            	}
            	var buyerCodeCombobox = new BuyerCodeSelector({
                    purchaseOrganisationCode : purchaseOrgCode,
                    el: this.$('#buyerCode')
                });
                this[regionName].show(buyerCodeCombobox);
            },

			onShow : function() {
				this.$el.modal('show');
				this.populate(this.whichProcureType());
			},

			onDestroy : function() {
				this.$('.date').datepicker('remove');
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.ProcurementApp.off(null, null, this);
			}
		});
	});

	return Gloria.ProcurementApp.View.MultipleUpdateModalView;
});
