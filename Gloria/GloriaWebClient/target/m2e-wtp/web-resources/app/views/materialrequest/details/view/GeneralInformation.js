define(['app',
        'backbone',
        'marionette',
        'handlebars',
        'datepicker',
        'moment',
        'i18next',
		'select2',    
		'utils/DateHelper',
        'utils/typeahead/CompanyCodeTypeaheadView',
        'utils/typeahead/ProjectTypeaheadView',
        'utils/typeahead/GLAccountTypeaheadView',
        'utils/typeahead/WBSTypeaheadView',
        'utils/typeahead/CostCenterTypeaheadView',
        'utils/typeahead/SAPTypeaheadView',
        'utils/typeahead/BuildSitesTypeaheadView',
        'views/materialrequest/details/view/ContactPersonUserIDView',
        'views/materialrequest/details/view/MaterialControllerUserIDView',
        'hbs!views/materialrequest/details/view/general-information'
], function(Gloria, Backbone, Marionette, Handlebars, Datepicker, moment, i18n, select2,DateHelper,
		CompanyCodeTypeaheadView, ProjectTypeaheadView, GLAccountTypeaheadView, WBSTypeaheadView,
		CostCenterTypeaheadView, SAPTypeaheadView, BuildSitesTypeaheadView, ContactPersonUserIDView, MaterialControllerUserIDView, compiledTemplate) {

	Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.GeneralInformation = Backbone.Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
				this.listenTo(Gloria.MaterialRequestApp, 'load:LDAPUserDTOModel', this.updateContactPersonName);
				this.listenTo(Gloria.MaterialRequestApp, 'load:LDAPUserDTOModel:MaterialController', this.updateMaterialControllerName);
			},
			
			updateContactPersonName: function(isValid, LDAPUserDTOModel) {
			    if(isValid) {
			        this.$('input#contactPersonName').val(LDAPUserDTOModel.get('userName'));
			    } else {
			        this.$('input#contactPersonName').val('');
			    }
			},
			
			updateMaterialControllerName: function(isValid, LDAPUserDTOModel) {
			    if(isValid) {
			        this.$('input#materialControllerName').val(LDAPUserDTOModel.get('userName'));
			    } else {
			        this.$('input#materialControllerName').val('');
			    }
			},
	
			className : 'control-label',
			
			regions : {
				companyCodeSelectorContainer : 'div#companyCodeSelectorContainer',
				projectSelectorContainer : 'div#projectSelectorContainer',
				glAccountSelectorContainer : 'div#glAccountSelectorContainer',
				wbsCodeSelectorContainer : 'div#wbsCodeSelectorContainer',
				costCenterSelectorContainer : 'div#costCenterSelectorContainer',
				internalOrderNoSAPSelectorContainer : 'div#internalOrderNoSAPSelectorContainer',
				outboundLocationIdSelectorContainer : 'div#outboundLocationIdSelectorContainer',
				contactPersonUserIdView: 'div#contactPersonUserIdViewContainer',
				materialControllerUserIdView: 'div#materialControllerUserIdViewContainer'
			},
	
			events : {
				'show .date' : 'stopPropagation',
				'change #type' : 'handleTypeChange',
				'change #companyCode' : 'handleCompanyCodeChange',
				'change #projectId' : 'handleProjectIdChange'
			},

			typeOptions : [{
					'SINGLE' : 'Gloria.i18n.materialrequest.details.generalInformation.sdtype.SINGLE'
				}, {
					'MULTIPLE' : 'Gloria.i18n.materialrequest.details.generalInformation.sdtype.MULTIPLE'
				}, {
					'FOR_STOCK' : 'Gloria.i18n.materialrequest.details.generalInformation.sdtype.FOR_STOCK'
				}],

			stopPropagation : function(e) {
				e.stopPropagation();
				e.stopImmediatePropagation();
			},
			
			handleTypeChange : function(e) {
				var type = e.currentTarget.value;
				this.hideShowDivsOnType(type);
				Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:typeChange', this.model, type);
			},
			
			// Company Code Change Handler
			handleCompanyCodeChange : function(e) {
				this.companyCode = e.currentTarget.value;
				var defaultSelectItem = {
					id : '', 
					text : i18n.t('Gloria.i18n.selectBoxDefaultValue')
				};
				this.$('#projectId').select2('data', defaultSelectItem);
				this.$('#glAccount').select2('data', defaultSelectItem);
				this.$('#wbsCode').select2('data', defaultSelectItem);
				this.$('#costCenter').select2('data', defaultSelectItem);
				this.projectTypeaheadView(this.companyCode);
				this.glAccountTypeaheadView(this.companyCode);
				this.wbsTypeaheadView(this.companyCode, null);
				this.costCenterTypeaheadView(this.companyCode);
			},
			
			// seperte method for "Copy and create new " to set the values to dropdown. 
			performCompanyCodeChange : function(code) {
				this.companyCode = code;
				this.projectTypeaheadView(this.companyCode);
				this.glAccountTypeaheadView(this.companyCode);
				this.wbsTypeaheadView(this.companyCode, null);
				this.costCenterTypeaheadView(this.companyCode);
			},
			
			// Project Change Handler
			handleProjectIdChange: function(e) {
				this.projectId = e.currentTarget.value;
				var defaultSelectItem = {
					id : '', 
					text : i18n.t('Gloria.i18n.selectBoxDefaultValue')
				};
				this.$('#wbsCode').select2('data', defaultSelectItem);
				this.wbsTypeaheadView((this.companyCode || this.model.get('companyCode')), this.projectId);
			},
			
			// CompanyCode Typeahead View
			companyCodeTypeaheadView : function() {
				var that = this;
				var companyCodeTypeaheadView = new CompanyCodeTypeaheadView({
					type : 'MATERIAL_CONTROL',
					el : this.$('#companyCode'),
					disabled : !that.model.isEditable(),
					select2Options: {
					    width: 'off'
					}
				});
				this.listenTo(companyCodeTypeaheadView, companyCodeTypeaheadView.cid, that.performCompanyCodeChange);
				this.companyCodeSelectorContainer.show(companyCodeTypeaheadView);
			},
			
			// Project Typeahead View
			projectTypeaheadView : function(companyCode) {
				if (this.model.isEditable()) {
					this.projectSelectorContainer.show(new ProjectTypeaheadView({
						el : this.$('#projectId'),
						companyCode : companyCode,
	                    select2Options: {
	                        width: 'off'
	                    }
					}));
				}
			},
			
			// GLAccount Typeahead View
			glAccountTypeaheadView : function(companyCode) {
				var that = this;
				this.glAccountSelectorContainer.show(new GLAccountTypeaheadView({
					el : this.$('#glAccount'),
					companyCode : companyCode,
					disabled : !that.model.isEditable(),
                    select2Options: {
                        width: 'off'
                    }
				}));
			},
			
			// WBS Typeahead View
			wbsTypeaheadView : function(companyCode, projectId) {
				if (this.model.isEditable()) {
					this.wbsCodeSelectorContainer.show(new WBSTypeaheadView({
						el : this.$('#wbsCode'),
						companyCode : companyCode,
						projectId : projectId,
	                    select2Options: {
	                        width: 'off'
	                    }
					}));
				}
			},
			
			// CostCenter Typeahead View
			costCenterTypeaheadView : function(companyCode) {
				if (this.model.isEditable()) {
					this.costCenterSelectorContainer.show(new CostCenterTypeaheadView({
						el : this.$('#costCenter'),
						companyCode : companyCode,
	                    select2Options: {
	                        width: 'off'
	                    }
					}));
				}
			},
			
			// SAP Typeahead View
			sapTypeaheadView : function() {
				if (this.model.isEditable()) {
					this.internalOrderNoSAPSelectorContainer.show(new SAPTypeaheadView({
						el : this.$('#internalOrderNoSAP'),
	                    select2Options: {
	                        width: 'off'
	                    }
					}));
				}
			},
			
			// BuildSites Typeahead View
			buildSitesTypeaheadView : function() {
				var that = this;
				this.outboundLocationIdSelectorContainer.show(new BuildSitesTypeaheadView({
					el : this.$('#outboundLocationId'),
					disabled : !that.model.isEditableOrUpdated(),
                    select2Options: {
                        width: 'off'
                    }
				}));
			},
			
			// ContactPersonUserId
			contactPersonUserId : function() {
			    if(this.model.isEditableOrUpdated()) {
			        this.contactPersonUserIdView.show(new ContactPersonUserIDView({
			            contactPersonUserId: this.model.get('contactPersonUserId'),
			            htmlName: "materialRequest[contactPersonUserId]"
			        }));
			    }
			},
			
			materialControllerUserId : function() {
			    if(this.model.isEditable()) {
			        this.materialControllerUserIdView.show(new MaterialControllerUserIDView({
			        	materialControllerUserId: this.model.get('materialControllerUserId'),
			            htmlName: "materialRequest[materialControllerUserId]"
			        }));
			    }
			},
			
			hideShowDivsOnType : function(typeVal) {
				switch (typeVal) {
				case 'SINGLE':
					this.$el.find('#testObjectDiv').show();
					break;
				case 'MULTIPLE':
					this.$el.find('#testObjectDiv').hide();
					break;
				case 'FOR_STOCK':
					this.$el.find('#testObjectDiv').hide();
					break;
				default:
					break;
				}
			},
			
			render : function() {
				var that = this;	
				this.$el.html(compiledTemplate({
					isNew: this.model.isNew(),
					isEditableOrUpdated: this.model.isEditableOrUpdated(),
					isNotEditableOrUpdated: !this.model.isEditableOrUpdated(),
					data : this.model.toJSON(),
					editable : this.model.isEditable(),
					readOnly : !this.model.isEditable(),
					status : 'Gloria.i18n.materialrequest.overview.status.' + this.model.hasStatus(),				
					'typeOptions' : this.typeOptions,
					type : 'Gloria.i18n.materialrequest.details.generalInformation.sdtype.' + this.model.toJSON().type,
					isNew : this.model.isNew()
				}));
				this.$('.date').datepicker();
				return this;
			},
			
			onShow : function() {
				this.hideShowDivsOnType(this.model.get('type'));
				this.companyCodeTypeaheadView();
				this.projectTypeaheadView(this.model.get('companyCode'));
				this.glAccountTypeaheadView(this.model.get('companyCode'));
				this.wbsTypeaheadView(this.model.get('companyCode'), this.model.get('projectId'));
				this.costCenterTypeaheadView(this.model.get('companyCode'));
				this.sapTypeaheadView();
				this.buildSitesTypeaheadView();
				this.contactPersonUserId();
				this.materialControllerUserId();
				var today = DateHelper.getNextLocalizedDate();
				this.$('.requiredStaDateDiv').datepicker('setStartDate',today);
			},
	
			onDestroy : function() {
				this.$('.date').datepicker('remove');
			}
		});
	});

	return Gloria.MaterialRequestApp.View.GeneralInformation;
});
