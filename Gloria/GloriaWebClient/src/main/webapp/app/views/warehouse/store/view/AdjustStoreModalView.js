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
	    'hbs!views/warehouse/store/view/adjust-store-modal'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, compiledTemplate) {
    
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.AdjustStoreModalView = Marionette.LayoutView.extend({
	    	
			regions : {
				adjustStore : '#adjustStore'
			},
			
			className : 'modal',

			id : 'adjust-modal',
			
	    	events : {
	    		'click #splitStore' : 'handleSplitStoreClick',
	    		'click #cancel' : 'handleCancelClick',
	    		'hide.bs.modal' : 'onHide'	    		
	        },
	        
	        initialize : function(options) {	        	
	        	this.listenTo(Gloria.WarehouseApp, 'Store:update:split', this.update);
	        	this.listenTo(Gloria.WarehouseApp, 'Store:savedAdjustedMaterialLines', function(flag) {
                    if(flag) {
                    	Gloria.WarehouseApp.trigger('Store:clearSelectedModels');
                    	$('#split-button').attr('disabled', true);
                        Gloria.trigger('reset:modellayout');                       
                    }
                });
	        },
	        
	        update: function(splitModifiedCollection) {
	        	if(splitModifiedCollection){	        		   
	        		 var total = splitModifiedCollection.getTotalNumber('storedQuantity'); 	                 
	                 if(total !== 0 && total === parseInt(this.model.get('storedQuantity'))) {
	                     this.redMark(false);
	                 } else {
	                     this.redMark(true);
	                 }	                 
	                 this.$('#quantitySum').text(total);
	        	}               
	        },
	        	        
	        redMark: function(flag) {
	            var expectedQuantities = this.$('input[id^="storedQuantity"]');
	            var mappedExpectedQuantitiesToError = _.map(expectedQuantities, function(elem) {
                    return {
                    	message: i18n.t('errormessages:errors.GLO_ERR_050'),
                        element: elem
                    };
                });
	            var allowedStoreQuantity = this.$('#allowedStoredQty');
	            if(flag) {
	                this.showErrors(mappedExpectedQuantitiesToError);
	                allowedStoreQuantity.addClass('text-danger');	                
	            } else {
	                this.showErrors();
	                allowedStoreQuantity.removeClass('text-danger');                    
	            }	            
	            this.$('#splitStore').attr('disabled', flag);
	        },
			
	        handleSplitStoreClick : function(e) {
	    		e.preventDefault();
	    		if(this.isValidForm()) {	    		    
	    		    Gloria.WarehouseApp.trigger('Store:AdjusttedMaterialLines:clicked', this.model);
	    		}
			},
			
			// Register Validation
	        registerValidator : function() {	        	
	            this.$('#adjust-form input[id^="storedQuantity"]').each(function() {
	    			$(this).rules('add', {
		    			required: true,
		    			min: 1,
		    			digits: true,
		    			messages: {
		    				required: i18n.t('errormessages:errors.GLO_ERR_050'),
		    				min: i18n.t('errormessages:errors.GLO_ERR_050'),
		    				digits: i18n.t('errormessages:errors.GLO_ERR_050')
		    			}
		        	});
	    		});
	            this.$('#adjust-form input[id^="binLocationContainer"]').each(function() {
	    			$(this).rules('add', {
		    			required: true,
		    			messages: {
		    				required: i18n.t('errormessages:errors.GLO_ERR_051')
		    			}
		        	});
	    		});
			},
	        
	        validator : function() {
	        	var that = this;
				return this.$('#adjust-form').validate({
				    ignore: null,		            
		            onfocusin: false,
		            onfocusout: false,
		            onkeyup: false,
		            onclick: false,
			        showErrors: function (errorMap, errorList) {
			        	that.showErrors(errorList);
			        }
				});
			},
			
			isValidForm : function() {
				var validator = this.validator();
				this.registerValidator(); // Check if its registered more than once!
				return validator.form();
			},
			
			showErrors : function(errorList) {
	        	this.hideErrors();
	        	Gloria.trigger('showAppMessageView', {
	        		modal : true,
	        		type : 'error',
	        		title : i18n.t('errormessages:general.title'),
	        		message : errorList
	        	});
			},
			
			hideErrors : function() {
				Gloria.trigger('hideAppMessageView');
				this.$('.form-group').removeClass('has-error');
			},
	        
			onHide: function(e) {
			    // To prevent conflict between hide events of datepickers inside the modal 
			    // and hide effect of modal itself.		   
			},
			
	        handleCancelClick : function(e) {	            
	        	this.$el.modal('hide');
	        },
	        
	        render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					id : that.model.get('id'),
					data: this.model.toJSON()
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
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.WarehouseApp.off(null, null, this);
			}
	    });
	});
	
	
    return Gloria.WarehouseApp.View.AdjustStoreModalView;
});
