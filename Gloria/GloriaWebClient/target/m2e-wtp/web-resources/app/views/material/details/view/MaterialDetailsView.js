define(['app',
        'jquery',        
        'underscore',
        'i18next',
		'handlebars', 
		'marionette',
		'bootstrap',
		'backbone.syphon',
		'jquery-validation',
		'hbs!views/material/details/view/material-details'
], function(Gloria, $, _, i18n, Handlebars, Marionette, Bootstrap, Syphon, Validation, compiledTemplate) {
    
	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
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
	        	'material[partVersion]' : {
	                required : true
	            }
	        },
	        messages : {
	        	'material[partVersion]' : {
	                required : i18n.t('errormessages:errors.GLO_ERR_010')
	            }
	        },
	        showErrors: triggerErrors,
	        onfocusin: false,
	        onfocusout: false,
	        onkeyup: false,
	        onclick: false
	    };
		
		View.MaterialDetailsView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
	    		this.listenTo(Gloria.MaterialApp, 'MaterialDetails:testobject:showhide', this.showHideUpdateTOButton);
	    	},
			
			regions : {
				materialLineInfo : '#materialLineInfoContent',
				materialInfo : '#materialInfoContent',
				orderInfo : '#orderInfoContent',
				contactInfo : '#contactInfoContent',
				attachmentInfo : '#attachmentInfoContent',
	            historyInfo : '#historyInfoContent'
			},
			
			events : {
				'show.bs.collapse .accordion' : 'publishAccardionCollapseEvent',
				'click #edit' : 'handleEditClick',
				'click #save' : 'handleSaveClick',
	            'click #close' : 'handleCancelOrCloseClick',
	            'click #cancel' : 'handleCancelOrCloseClick',
	            'click #pull' : 'handlePullPartButtonClick',
	            'click #updateTO' : 'handleUpdateTOButtonClick'
	        },
	        
	        publishAccardionCollapseEvent : function(e) {
	        	if(e && e.target && e.target.getAttribute('id')) {  		
	        		var accardionID = e.target.getAttribute('id');        						   
	        		Gloria.MaterialApp.trigger('MaterialDetails:showaccordion', accardionID);
	        	}
	        },
	        
	        handleEditClick : function() {	        	
	        	Gloria.MaterialApp.trigger('editMode:materialLines');
	        	this.$('#edit').addClass('hidden');
	        	this.$('#close').addClass('hidden');
	        	this.$('#save').removeClass('hidden');
	        	this.$('#cancel').removeClass('hidden');
			},
			
			handleSaveClick : function(e) {
				e.preventDefault();
				if(this.isValidForm(saveValidatorOptions)) {
					this.listenTo(Gloria.MaterialApp, 'MaterialDetails:saved', this.handleCancelOrCloseClick);
					var formData = Backbone.Syphon.serialize(this);
					Gloria.MaterialApp.trigger('MaterialDetails:save', formData);
				}
			},
	        
			handleCancelOrCloseClick : function() {
				Backbone.history.navigate('material/linesoverview', {
					trigger : true
				});
	        },
	        
	        handlePullPartButtonClick : function(e) {
				e.preventDefault();
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:pulledPart', this.handleCancelOrCloseClick);
				Gloria.MaterialApp.trigger('MaterialRequestList:pullPart:show');
			},
			
			showHideUpdateTOButton : function(flag) {
				if(flag) {
					this.$('#updateTO').removeClass('hidden');
				}
			},
			
			handleUpdateTOButtonClick : function(e) {
				this.listenTo(Gloria.MaterialApp, 'MaterialDetails:saved', this.handleCancelOrCloseClick);
				var formData = Backbone.Syphon.serialize(this);
				Gloria.MaterialApp.trigger('MaterialDetails:save:testobject', formData);
			},
			
	        validator : function(options) {
                var validator = this.$('form').validate(options);
                validator.settings.rules = options.rules;
                validator.settings.messages = options.messages;
                return validator;
            },

            isValidForm : function(options) {
                return this.validator(options).form();
            },            

			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			},
			
			onDestroy : function() {
				Gloria.MaterialApp.off(null, null, this);
			}
		});
	});
	
	return Gloria.MaterialApp.View.MaterialDetailsView;
});
