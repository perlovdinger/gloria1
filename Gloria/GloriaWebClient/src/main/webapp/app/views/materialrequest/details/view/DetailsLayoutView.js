define([
    'app',
    'i18next',
    'underscore',
    'handlebars',
    'marionette',
    'jquery-validation',
    'backbone.syphon',
    'utils/dialog/dialog',
    'hbs!views/materialrequest/details/view/details-layout'
], function(Gloria, i18n, _, Handlebars, Marionette, Validation, Syphon, Dialog, compiledTemplate) {
      
    Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
    	// This method will be called whenever the validation is false. 
	    var triggerErrors = function (errorMap, errorList) {
	        Gloria.trigger('showAppMessageView', {
	            type : 'error',
	            title : i18n.t('errormessages:general.title'),
	            message : errorList
	        });
	    };
	    
	    // The validation rules and messages for saving
	    var saveValidatorOptions = {
	        rules : {
	        	'materialRequest[type]' : {
	                required : true
	            },
	            'materialRequest[companyCode]' : {
	                required : true
	            },
	            'materialRequest[contactPersonUserId]' : {
	                minlength: 7,
	                maxlength: 7,
	                contactPersonId: true
	            }
	        },
	        messages : {
	        	'materialRequest[type]' : {
	                required : i18n.t('errormessages:errors.GLO_ERR_001')
	            },
	            'materialRequest[companyCode]' : {
	            	required : i18n.t('errormessages:errors.GLO_ERR_002')
	            },
	            'materialRequest[contactPersonUserId]' : {
                    minlength: i18n.t('errormessages:errors.GLO_ERR_063'),
                    maxlength: i18n.t('errormessages:errors.GLO_ERR_063'),
                    contactPersonId: i18n.t('errormessages:errors.GLO_ERR_064')
                }
	        }
	    };
	    
	    // The validation rules and messages for sending
	    var sendValidatorOptions = {
	        rules : {
	            'materialRequest[companyCode]' : {
	                required : true
	            },
	        	'materialRequest[projectId]' : {
	                required : true
	            },            
	            'materialRequest[glAccount]' : {
	                required : true
	            },
	            'materialRequest[wbsCode]' : {
	                required : true
	            },
	            'materialRequest[costCenter]' : {
	                required : true
	            },
	            'materialRequest[requiredStaDate]' : {
	                required : true
	            }
	        },
	        messages : {
	        	'materialRequest[companyCode]' : {
	            	required : i18n.t('errormessages:errors.GLO_ERR_002')
	            },
	        	'materialRequest[projectId]' : {
	                required : i18n.t('errormessages:errors.GLO_ERR_003')
	            },
	            'materialRequest[glAccount]' : {
	                required : i18n.t('errormessages:errors.GLO_ERR_004')
	            },
	            'materialRequest[wbsCode]' : {
	                required : i18n.t('errormessages:errors.GLO_ERR_005')
	            },
	            'materialRequest[costCenter]' : {
	                required : i18n.t('errormessages:errors.GLO_ERR_006')
	            },
	            'materialRequest[requiredStaDate]' : {
	                required : i18n.t('errormessages:errors.GLO_ERR_007')
	            }
	        }
	    };
	    
	    // The common options between saving and sending validation options.
	    var commonSettings = {
	    	ignore: null,
	        showErrors: triggerErrors,
	        onfocusin: false,
	        onfocusout: false,
	        onkeyup: false,
	        onclick: false
	    };
	    
	    _.defaults(sendValidatorOptions.rules, saveValidatorOptions.rules);
	    _.defaults(sendValidatorOptions.messages, saveValidatorOptions.messages);
	    // Extends both the saving and sending validation options with the common options.
	    _.each([saveValidatorOptions, sendValidatorOptions], function(item) {
	        _.extend(item, commonSettings);
	    });
	    
        View.DetailsLayoutView = Marionette.LayoutView.extend({
        	
        	initialize: function(options) {
				$.validator.addMethod('positiveNumber', function(value, element) {
					return Number(value) >= 0;
				});
				
				$.validator.addMethod('contactPersonId', function(value, element) {
                    return $(element).data('isvalid');
                });
				
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:headerInfo:collect', this.getHeaderInfo);
            },
            
            regions : {
                generalInformation : '#generalInformation',
                gridPane : '#materialInformation',
                controlButtons : '#detailsControlButtons'
            },
            
            events : {
				'click #save' : 'saveClicked',
				'click #send' : 'sendClicked',
				'click #cancel' : 'goToPreviousRoute',
				'click #revert' : 'showRevertDialog',
				'click #delete' : 'showRemoveDialog',
				'click #newVersion' : 'newVersionClicked',
				'click #cancelMaterialRequest' : 'showCancelMaterialRequestDialog',
				'click #copyAndCreateNew' : 'copyAndCreateNew'
			},
			
			saveClicked : function(e) {
                e.preventDefault();
                if(this.isValidForm(saveValidatorOptions)) {
                    var formData = Backbone.Syphon.serialize(this);          
                    Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:save', formData.materialRequest, formData.exportExcel);
                }
            },
            
            getHeaderInfo : function() {
            	if(this.isValidForm(saveValidatorOptions)) {
                    var formData = Backbone.Syphon.serialize(this);
                    Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:headerInfo:collected', formData.materialRequest);
                } else {
                	Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:headerInfo:collected', null);
                }
			}, 
			
            sendClicked : function(e) {
                e.preventDefault();
                if(this.isValidForm(sendValidatorOptions)) {
                    var formData = Backbone.Syphon.serialize(this);          
                    Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:send', formData.materialRequest, formData.exportExcel);
                }
            },
            
            showRevertDialog: function(e) {
                e.preventDefault(); 
                Dialog.show({ 
                    title: i18n.t('Gloria.i18n.materialrequest.revertMaterialRequest'),
                    message: i18n.t('Gloria.i18n.materialrequest.revertConfirmation')                    
                }).dialog.on('ok', this.revertClicked, this);
            },
            
            revertClicked: function() {                               
                Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:revert', this.model);
            },
            
            showRemoveDialog: function(e) {
                e.preventDefault(); 
                Dialog.show({ 
                    title: i18n.t('Gloria.i18n.materialrequest.deleteMaterialRequest'),
                    message: i18n.t('Gloria.i18n.materialrequest.deleteConfirmation')                    
                }).dialog.on('ok', this.deleteClicked, this);
            },
            
            showCancelMaterialRequestDialog: function(e) {
            	e.preventDefault(); 
                Dialog.show({ 
                    title: i18n.t('Gloria.i18n.materialrequest.cancelMaterialRequest'),
                    message: i18n.t('Gloria.i18n.materialrequest.cancelMaterialRequestConfirmation')                    
                }).dialog.on('ok', this.cancelMaterialRequestClicked, this);
            },
            
            copyAndCreateNew: function(e) {
            	e.preventDefault();
            	Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:copyAndCreate');
			},
            
            deleteClicked: function() {                               
                Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:delete');
            },
            
            cancelMaterialRequestClicked: function() {
            	Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:cancelMaterialRequest');
            },
            
            newVersionClicked: function(e) {
                e.preventDefault();
                Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:newVersion');
            },

            validator : function(options) {
                var validator = $('form').validate(options);
                validator.settings.rules = options.rules;
                validator.settings.messages = options.messages;
                return validator;
            },

            isValidForm : function(options) {
                return this.validator(options).form();
            },            
            
            goToPreviousRoute : function() {
            	Backbone.history.navigate('materialrequest/overview', {
					trigger : true
				});
            },
            
             render : function() {
                this.$el.html(compiledTemplate());  
                return this;
            }
        });
    
    });

    return Gloria.MaterialRequestApp.View.DetailsLayoutView;
});