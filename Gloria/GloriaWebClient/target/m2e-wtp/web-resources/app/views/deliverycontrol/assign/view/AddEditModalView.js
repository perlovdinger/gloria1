define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'jquery-validation',
        'backbone.syphon',
        'utils/UserHelper',
        'views/deliverycontrol/helper/ProjectTypeaheadView',
		'hbs!views/deliverycontrol/assign/view/add-edit-modal'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, UserHelper, ProjectTypeaheadView, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AddEditModalView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'addEditModal',
			
			regions: {
            	projectSelectorContainer: 'div#projectSelectorContainer'
            },

			events : {
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.module = options.module;
				this.model = options.model;
				this.collection = options.collection;
				this.team = options.team;
				this.deliveryFollowUpTeamModel = options.deliveryFollowUpTeamModel;
				this.listenTo(Gloria.DeliveryControlApp, 'DeliveryControllerInfo:saved', this.handleSaveResponse);
			},

			handleSaveClick : function(e) {
				e.preventDefault();
				var that = this;
				if (this.isValidForm()) {
					var formData = Backbone.Syphon.serialize(this);
					Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:save', that.module, this.model, formData.delivery);
				}
			},
			
			handleSaveResponse : function(flag, errors) {
				if(flag) {
					this.$el.modal('hide');
					Gloria.DeliveryControlApp.trigger('ControlButton:refresh');
					Gloria.DeliveryControlApp.trigger('SupplierProjectGrid:clearselection');
				} else {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						modal : true,
						message : errors
					});
				}				
			},
			
			validator : function() {
				var that = this;
				
				//Check if SupplierId Or Suffix exist
				$.validator.addMethod('checkIfSupplierIdOrsuffixExist', function(supplierId, element) {
					var suffix =  $('#ppSuffix');
					var isEitherSupplierIdOrSuffix = false;
					if ($(element).val() != '' || $(suffix).val() != ''){
						isEitherSupplierIdOrSuffix = true;
					}
					return  isEitherSupplierIdOrSuffix;
				});
				
				//checkIfSupplierIdSuffixComboUnique
				$.validator.addMethod('checkIfSupplierIdSuffixComboUnique', function(supplierId , element) {
					var enteredSuffix =  $('#ppSuffix').val();	
					var isSupplierIdSuffixComboUnique = true;
					
					that.collection.each(function(model){
						var currentSupplierId = model.attributes.supplierId || '';
						var currentSuffix = model.attributes.suffix || '';
						
						if(enteredSuffix == currentSuffix && supplierId == currentSupplierId){
							isSupplierIdSuffixComboUnique = false;
							if(that.model && that.model.get('id') == model.get('id')){
								isSupplierIdSuffixComboUnique = true;
							}
						};
					});
					return  isSupplierIdSuffixComboUnique;
				});
				
				//checkIfProjectIdUnique
				$.validator.addMethod('checkIfProjectIdUnique', function(projectId, element) {
					var isProjectIdUnique = true;					
					that.collection.each(function(model){
						var currentProjectId = model.attributes.projectId || '';
						if(projectId == currentProjectId) {
							isProjectIdUnique = false;
							if(that.model && that.model.get('id') == model.get('id')){
								isProjectIdUnique = true;
							}
						};
					});
					return  isProjectIdUnique;
				});
				
				return this.$el.find('form').validate({
					rules: {						
						'delivery[supplierId]': {
							checkIfSupplierIdOrsuffixExist: true,
							checkIfSupplierIdSuffixComboUnique: true
						},						
						'delivery[deliveryControllerUserId]': {
							required: true
						},
						'delivery[projectId]': {
							required : true,
							checkIfProjectIdUnique: true
						}
					},
					messages: {						
						'delivery[supplierId]': {
							checkIfSupplierIdOrsuffixExist: i18n.t('errormessages:errors.GLO_ERR_036'),
							checkIfSupplierIdSuffixComboUnique: i18n.t('errormessages:errors.GLO_ERR_037')
						},
						'delivery[deliveryControllerUserId]': {
							required: i18n.t('errormessages:errors.GLO_ERR_039')
						},
						'delivery[projectId]': {
							required : i18n.t('errormessages:errors.GLO_ERR_003'),
							checkIfProjectIdUnique: i18n.t('errormessages:errors.GLO_ERR_038')
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

			showErrors : function(errorList) {
				Gloria.trigger('showAppMessageView', {
					modal : true,
					type : 'error',
					title : i18n.t('errormessages:general.title'),
					message : errorList
				});
			},
			
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},
			
			 projectTypeaheadView: function() {
				var currentUser = UserHelper.getInstance().getUserId();
				var savedTeamInfo = window.localStorage.getItem('Gloria.User.DefaultTeam.' + currentUser);
				var savedTeamInfoObj = JSON.parse(savedTeamInfo);
        		this.projectSelectorContainer.show(new ProjectTypeaheadView({ 
        			el: this.$('#projectId'),
        			teamName : savedTeamInfoObj ? savedTeamInfoObj.name : null,
        			teamType : 'DELIVERY_CONTROL'
        		}));
            },
			
			showDCSelector : function() {
				var that = this;
				var currentUser = UserHelper.getInstance().getUserId();
				var savedTeamInfo = window.localStorage.getItem('Gloria.User.DefaultTeam.' + currentUser);
				var savedTeamInfoObj = JSON.parse(savedTeamInfo);
            	require(['views/deliverycontrol/common/DeliveryControllerSelector'], function(DeliveryControllerSelector) {
					that.deliveryControllerSelector = new DeliveryControllerSelector({
						element : '#dcUserId',
						name : 'delivery[deliveryControllerUserId]',
						suggestedDC : that.model ? that.model.get('deliveryControllerUserId') : '',
						defaultDC : that.team ? that.team.defaultDcUserid : null,
						teamId : savedTeamInfoObj ? savedTeamInfoObj.name : null
					});
				});
			},
			
			showSuffixSelector : function() {
				var that = this;
				require(['views/deliverycontrol/helper/SuffixSelectorView'], function(SuffixSelectorView) {
					that.suffixSelector = new SuffixSelectorView({
						element : '#ppSuffix',
						deliveryFollowUpTeamId : that.deliveryFollowUpTeamModel.id,
						suggestedOption : that.model ? that.model.get('suffix') : ''
					});
				});
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					module : that.module,
					data : this.model ? this.model.toJSON() : {},
					team : this.team,
				}));
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
			},
			
			onShow : function() {
				this.$el.modal('show');
				if(this.module == 'project') {
					this.projectTypeaheadView();
				} else if(this.module == 'supplier') {
					this.showSuffixSelector();
				}
				this.showDCSelector();				
				this.$("[data-toggle='tooltip']").tooltip({
				    container: this.$('.modal-body')
				});
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				this.deliveryControllerSelector && this.deliveryControllerSelector.remove();				
				this.suffixSelector && this.suffixSelector.remove();				
				Gloria.DeliveryControlApp.off(null, null, this);
			}
		});
	});

	return Gloria.DeliveryControlApp.View.AddEditModalView;
});
