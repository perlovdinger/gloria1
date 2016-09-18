define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'moment',
	    'i18next',
	    'backbone.syphon',
	    'jquery-validation',
	    'hbs!views/deliverycontrol/myorderoverview/details/view/orderline-detail'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, moment, i18n, Syphon, validator, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.OrderLineDetailView = Marionette.LayoutView.extend({
	
			 initialize : function(options) {
		         this.isEditable = options.isEditable;
		         this.module = options.module;
		         this.orderline = options.orderline;
                 this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:saved', this.handleSaveFinished);
                 this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:showHidePOAmdInfo', this.showPOAmdInfoAccordion);
			 },
		        
	        regions : {
	            partGeneralInfo : '#partGeneralInfoContent',
	            poInfoLog : '#poInfoLog',
	            poDelInfo : '#poDelInfoContent',
	            poAmdInfo : '#poAmdInfoContent',
	            procInfo : '#procInfoContent',
	            historyInfo : '#historyInfoContent'
	        },
	
	        events : {
	            'show.bs.collapse .accordion' : 'publishAccardionCollapseEvent',
	            'click #save' : 'handleSaveClick',
	            'click #cancel' : 'handleCancelClick',
	    		'click #ignore' : 'handleIgnoreClick'
	        },
	        
	        showPOAmdInfoAccordion : function(isExternal) {
	        	isExternal && $('#poAmdInfo').show();
			},
			

	        handleIgnoreClick : function(e){
	        	e.preventDefault();
	        	$('#deviationMessage').hide();
	        	Gloria.DeliveryControlApp.trigger('OrderLineDetail:ignore');
	        },
	        
	        handleSaveClick : function(e) {
	            e.preventDefault();
	            if (this.isValidForm()) {
		            var formData = Backbone.Syphon.serialize(this);
		            Gloria.DeliveryControlApp.trigger('OrderLineDetail:save', formData.orderLine, _.toArray(formData.deliveryschedules),formData.orderStaDate);
	            }
	        },
	        
	        /**
	         * Called after save was performed, whether it succeeded or not 
	         */
	        handleSaveFinished : function(flag) {
                if(flag) {
                	Gloria.DeliveryControlApp.trigger('orderlinedetail:back');
                } else {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        message : new Array({
                            message : i18n.t('Gloria.i18n.saveFailed')
                        })
                    });
                }
            },
	        
	        validator : function() {
	        	var that = this;
	        	$.validator.addMethod('positiveNumber', function(value, element) {
					return Number(value) >= 0;
				});
	        	
	        	// Validation for allowedQuantity GreatherThan Or EqualTo OrderQuantity
				$.validator.addMethod('allowedQuantityGreatherThanOrEqualToOrderQuantity', function(num, element) {
				    num = num.replace(/\s+/g, '');
				    var allowedQuantity = (parseInt(num));
				    //var possibleToReciveQuantity = parseInt(that.$el.find('#quantity')[0].innerHTML);
				    var possibleToReciveQuantity = that.orderline.get('allowedQuantity');
				    if(that.module == 'completed') {
				    	possibleToReciveQuantity = that.orderline.get('receivedQuantity');
				    }				    
					return this.optional(element) || (allowedQuantity >= possibleToReciveQuantity) ;					
	            });
			
				return this.$el.find('form').validate({
					rules: {
						'orderLine[allowedQuantity]': {
							 digits: true,
							 allowedQuantityGreatherThanOrEqualToOrderQuantity: true

						}
					},
					messages: {
						'orderLine[allowedQuantity]': {
							 digits: i18n.t('errormessages:errors.GLO_ERR_040'),
							 allowedQuantityGreatherThanOrEqualToOrderQuantity: i18n.t('errormessages:errors.GLO_ERR_040'),
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
				return this.validator().form();
			},
	
	        handleCancelClick : function() {
	            //e.preventDefault();
                if (this.isValidForm()) {
                    var formData = Backbone.Syphon.serialize(this);
	        	Gloria.DeliveryControlApp.trigger('orderlinedetail:back', formData.orderLine);
                }
	        	//Gloria.trigger('goToPreviousRoute');
	        },
	                  
	        publishAccardionCollapseEvent : function(e) {
	        	if(e && e.target && e.target.getAttribute('id')) {        		
	        		var accardionID = e.target.getAttribute('id');        						   
	        		Gloria.DeliveryControlApp.trigger('orderlinedetail:showaccordion:' + accardionID);        		      		     		
	        	}       	                 
	        },
	
	        render : function() {
	            this.$el.html(compiledTemplate({
	            	isDisabled : !this.isEditable && this.module != 'completed',
	            	module : this.module
	            }));  
	            return this;
	        }
	        
	    });
	});

    return Gloria.DeliveryControlApp.View.OrderLineDetailView;
});
