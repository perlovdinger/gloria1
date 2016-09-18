define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'datepicker',
        'moment',
	    'jquery-validation',
	    'backbone.syphon',
	    'hbs!views/deliverycontrol/myorderoverview/view/adjust-delivery-modal'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Datepicker, moment, Validation, Syphon, compiledTemplate) {
    
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.AdjustDeliveryModalView = Marionette.LayoutView.extend({
	    	
			regions : {
			    adjustDelivery : '#adjustDelivery'
			},
			
			className : 'modal',

			id : 'adjust-modal',
			
	    	events : {
	    		'click #save' : 'handleSaveClick',
	    		'click #cancel' : 'handleCancelClick',
	    		'click #ignore' : 'handleIgnoreClick',
	    		'hide.bs.modal' : 'onHide'	    		
	        },
	        
	        initialize : function(options) {
	        	this.model = options.model;
	        	this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:update:split', this.update);
	        	this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:savedAdjustedDeliveries', function(flag) {
                    if(flag) {
                        Gloria.trigger('reset:modellayout');                       
                    } else {
                    	Gloria.trigger('showAppMessageView', {
			    			type : 'error',
			    			modal : true,
			    			message : new Array({
			    				message : i18n.t('errormessages:general.processFailed')
			    			})
			    		});
                    }
                });
	        	this.listenToOnce(Gloria.DeliveryControlApp, 'AdjustDelivery:warningmessage:remove', this.removeWarningMessage);
	        },
	        
	        update: function(splitModifiedCollection) {            
                var total = splitModifiedCollection.getTotalNumber('expectedQuantity'); 
                var actualExpectedQty = this.model.get('allowedQuantity') - this.model.get('receivedQuantity');
                if(total !== 0 && total === actualExpectedQty) {
                    this.redMark(false);
                } else {
                    this.redMark(true);
                }
                this.$('#quantitySum').text(total);
	        },
	        
	        removeWarningMessage: function() {
				this.$el.find('#warningMessage').remove();
			},
	        
	        redMark: function(flag) {
	            var expectedQuantities = this.$('input[id^="expectedQuantity"]');
	            var mappedExpectedQuantitiesToError = _.map(expectedQuantities, function(elem) {
                    return {
                        element: elem
                    };
                });
	            var allowedQuantity = this.$('#allowedQuantity');
	            if(flag) {
	                this.showErrors(mappedExpectedQuantitiesToError);
	                allowedQuantity.addClass('text-danger');	                
	            } else {
	                this.showErrors();
	                allowedQuantity.removeClass('text-danger');                    
	            }	            
	            this.$('#save').attr('disabled', flag);
	        },

	        handleIgnoreClick : function(e){
	        	e.preventDefault();	        	
	        	$('#deviationMessage').hide();
	        	Gloria.DeliveryControlApp.trigger('MyOrderOverview:ignore', {orderLine: this.model});
	        },
	        
			handleSaveClick : function(e) {
	    		e.preventDefault();
	    		if(this.isValidForm()) {	    		    
	    		    Gloria.DeliveryControlApp.trigger('MyOrderOverview:AdjusttedDeliveries:clicked', {orderLine: this.model});
	    		}
			},
			
			// Register Validation
	        registerValidator : function() {	        	
	            this.$('#adjust-form input[id^="expectedQuantity"]').each(function() {
	    			$(this).rules('add', {
		    			required: true,
		    			positiveNumber: true,
		    			digits: true,
		    			messages: {
		    				required: i18n.t('errormessages:errors.GLO_ERR_041'),
		    				positiveNumber: i18n.t('errormessages:errors.GLO_ERR_041'),
		    				digits: i18n.t('errormessages:errors.GLO_ERR_041')
		    			}
		        	});
	    		});
	        	
	        	this.$('#adjust-form input[id^="expectedDate"]').each(function() {
	    			$(this).rules('add', {
		    			required: true,
		    			messages: {
		    				required: i18n.t('errormessages:errors.GLO_ERR_042')
		    			}
		        	});
	    		});
			},
	        
	        validator : function() {
	        	var that = this;
	        	Backbone.$.validator.addMethod('positiveNumber', function(value, element) {
                    return Number(value) > 0;
                });
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
				this.registerValidator();
				return validator.form();
			},
			
			showErrors : function(errorList) {
				Gloria.trigger('showAppMessageView', {
					modal : true,
					type : 'error',
					title : i18n.t('errormessages:general.title'),
					message : errorList,
					duplicate : false
				});
			},
	        
			onHide: function(e) {
			    // To prevent conflict between hide events of datepickers inside the modal 
			    // and hide effect of modal itself.
			    if(e.target == this.el) {
			        this.$('.date').datepicker('remove');
			    }
			},
			
	        handleCancelClick : function(e) {	            
	        	this.$el.modal('hide');
	        },
	        
	        render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					id : that.model.get('id'),
					data: this.model.toJSON(),
					markChanged: this.model.get('previousQuantity') != -1 && this.model.get('previousQuantity') != this.model.get('quantity')
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
				Gloria.DeliveryControlApp.off(null, null, this);
			}
	    });
	});
	
    return Gloria.DeliveryControlApp.View.AdjustDeliveryModalView;
});
