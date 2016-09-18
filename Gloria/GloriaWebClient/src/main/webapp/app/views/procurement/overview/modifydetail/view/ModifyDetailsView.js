define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'i18next',
		'jquery-validation',
		'backbone.syphon',
		'utils/dialog/dialog',
        'views/procurement/overview/modifydetail/view/BasicInfoView',
		'views/procurement/overview/modifydetail/view/TestObjectListView',
		'hbs!views/procurement/overview/modifydetail/view/modify-details'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Validation, Syphon, Dialog, BasicInfoView, TestObjectListView, compiledTemplate) {
    
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ModifyDetailsView = Marionette.LayoutView.extend({
			
			regions : {
				basicInfo : '#basicInfo',
				partInfo : '#partInfo'
			},

			events : {
				'click #cancel' : 'goToPreviousRoute',
	        	'click #save' : 'saveClickHandler',
	        	'click #cancelModification' : 'cancelModification',
	        	'click #close' : 'goToPreviousRoute'
	        },
	        
	        initialize : function(options) {
	        	if(options.collection && options.collection.length < 1) {
					throw new Error('Collection must be supplied!');
				} 
	        	this.collection = options.collection;
	        	this.model = options.model;
	        	this.hasReferer = options.hasReferer;
	        },
	        
	        goToPreviousRoute : function() {
	        	Gloria.trigger('goToPreviousRoute');
	        },
	        
	        saveClickHandler : function(e) {
	            e.preventDefault();
	            if(this.isValidForm()) {
	                var formData = Backbone.Syphon.serialize(this);
	                _.extend(formData.procureLine, {pPartAffiliation: 'X'});
	                Gloria.ProcurementApp.trigger('procureLine:ModifyDetails:save', formData.procureLine, formData.testObject);
	            }
	        },
	        
	        cancelModification : function(e) {
				e.preventDefault();
				var that = this;
				Dialog.show({
			    	title : i18n.t('Gloria.i18n.procurement.modifyDetail.text.cancelModification') + ' | ' + i18n.t('Gloria.i18n.gloriaHeader'),
			    	message: '<b>' + i18n.t('Gloria.i18n.procurement.modifyDetail.text.cancelModification') + '</b>'
								+ '<br/><br/>' + i18n.t('Gloria.i18n.procurement.modifyDetail.text.cancelModificationText'),
                    buttons: {
		                yes: {
		                    label: i18n.t('Gloria.i18n.buttons.yes'),
		                    className: "btn btn-primary",
		                    callback: function(e) {
		                        e.preventDefault();
		                        Gloria.ProcurementApp.trigger('procureLine:ModifyDetails:cancel', that.collection.at(0));
		                        return true;
		                    }
		                },
		                no: {
		                    label: i18n.t('Gloria.i18n.buttons.no'),
		                    className: 'btn btn-default',
		                    callback: function(e) {
		                        e.preventDefault();
		                        return true;
		                    }
		                }
                    }
                });
	        },
	        
	        // Register Validation
	        registerValidator : function() {
	        	$('input[id^="procurementQty_"]').each(function(){
	        		var referenceId = this.id.split('procurementQty_')[1];
	    			$(this).rules('add', {
		    			required: true,
		    			greaterThanZero: true,
		    			messages: {
		    				required: i18n.t('errormessages:errors.GLO_ERR_031') + ': ' + referenceId,
		    				greaterThanZero: i18n.t('errormessages:errors.GLO_ERR_031') + ': ' + referenceId
		    			}
		        	});
	    		});
	        },
	        
	        validator : function() {
	        	$.validator.addMethod('greaterThanZero', function(value, element) {
					return Number(value) > 0;
				});
				return this.$el.find('#basicInfoForm').validate({
					rules: {

					},
					messages: {
						'procureLine[pPartAffiliation]': {
							required: i18n.t('errormessages:errors.GLO_ERR_026')
						},
						'procureLine[pPartNo]': {
							required: i18n.t('errormessages:errors.GLO_ERR_027')
						},
						'procureLine[pVersion]': {
							required: i18n.t('errormessages:errors.GLO_ERR_028')
						},
						'procureLine[pPartName]': {
							required: i18n.t('errormessages:errors.GLO_ERR_029')
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
				var validator = this.validator();
				this.registerValidator(); // Check if its registered more than once!
				return validator.form();
			},

	        renderBasicInfoView : function() {
	        	var basicInfoView = new BasicInfoView({
	        		collection : this.collection,
	        		procModel : this.model
	    		});
				this.basicInfo.show(basicInfoView);
			},
			
	        renderTestObjectListView : function() {
	        	var testObjectListView = new TestObjectListView({
	        		collection : this.collection,
	        		procModel : this.model
	        	});
				this.partInfo.show(testObjectListView);	
			},
			
			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					readOnly : !!that.model,
					hasReferer : that.hasReferer
				}));
				this.renderBasicInfoView();
				this.renderTestObjectListView();
				return this;
			}
		});
	});
	
	return Gloria.ProcurementApp.View.ModifyDetailsView;
});
