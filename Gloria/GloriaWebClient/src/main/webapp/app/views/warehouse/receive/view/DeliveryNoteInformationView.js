define(['app',
        'i18next',
        'backbone',
        'marionette',
        'handlebars',
        'datepicker',
        'moment',
        'jquery-validation',
        'backbone.syphon',
        'utils/DateHelper',
        'hbs!views/warehouse/receive/view/deliverynote-information' 
], function(Gloria, i18n, Backbone, Marionette, Handlebars, Datepicker, moment, Validation, Syphon, DateHelper, compiledTemplate) {

	var receiveType = undefined;
    // This method will be called whenever the validation is false. 
    var triggerErrors = function (errorMap, errorList) {
        Gloria.trigger('showAppMessageView', {
            type : 'error',
            title : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.validation.title'),
            message : errorList
        });
    };
    
    // The validation rules and messages for saving
    var saveValidatorOptions = {
        rules : {
            'deliveryNote[deliveryNoteNo]' : {
                required : true
            },
            'deliveryNote[deliveryNoteDate]' : {
                required : true,
                gloriaDate: true
            },
            'deliveryNote[orderNo]' : {
                required : true
            }
        },
        messages : {
            'deliveryNote[deliveryNoteNo]' : {
                required : i18n.t('Gloria.i18n.warehouse.receive.validation.delivNoteNoRequired'),
            },
            'deliveryNote[deliveryNoteDate]' : {
                required : i18n.t('Gloria.i18n.warehouse.receive.validation.delivNoteDateRequired'),
            },
            'deliveryNote[orderNo]' : {
                required : i18n.t('Gloria.i18n.warehouse.receive.validation.delivNoteOrderNumberRequired'),
            }
        },
        showErrors: triggerErrors,
        onfocusin: false,
        onfocusout: false,
        onkeyup: false,
        onclick: false
    };
    
    Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.DeliveryNoteInformation = Marionette.View.extend({
            
            initialize: function(options) {
                options || (options = {editable: true});                
                this.orderModel = options.orderModel;
                this.deliveryNoteTransferReturnModel = options.deliveryNoteTransferReturnModel;
                this.selectedReceiveType = options.selectedReceiveType ? options.selectedReceiveType : receiveType ;
                this.listenTo(Gloria.WarehouseApp, 'NoRecordFound', this.handleNoRecordFound);
            },
            
            className : 'control-label',
            
            orderModelBindings: {
                '#orderNo': 'orderNo',
                '#supplierParmaID': 'supplierId',
                '#supplierName': 'supplierName',
                '#transportationNo': 'transportationNo',
                '#carrier': 'carrier',
                '#save': {
                    observe: 'id',
                    update: 'disableSave'
                 }
            },
            
            deliveryNoteTransferReturnModelBindings: {
                '#deliveryNoteNoTransferReturn': 'dispatchNoteNo',
                '#supplierParmaID': 'parmaID',
                '#supplierName': 'parmaName',
                '#transportationNo': 'transportationNo',
                '#carrier': 'carrier',
                '#save': {
                    observe: 'id',
                    update: 'disabledeliveryNoteTransferReturnModelSave'
                 }
            },
            
            bindings: {
            	'#deliveryNoteNoTransferReturn': 'dispatchNoteNo',
                '#deliveryNoteNo': 'deliveryNoteNo',
                '#deliveryNoteDate': {
                    observe: 'deliveryNoteDate',
                    onGet: function(value) {
                        return DateHelper.formatDate(value);
                    }
                },
                '#orderNo': 'orderNo',
                '#supplierParmaID': 'supplierId',
                '#supplierName': 'supplierName',
                '#transportationNo': 'transportationNo',
                '#carrier': 'carrier',
                '#receiveType': {
                    observe: 'receiveType',
                    onGet: function(value) {
                        return i18n.t('Gloria.i18n.warehouse.receive.receiveType.' + value);
                    }
                 }
            },
                        
            disableSave : function($el, val, model, options) {
                if (model.isNew()) {
                    $el.prop('disabled', true);
                } else {
                    $el.prop('disabled', false);
                }
            },
               
            disabledeliveryNoteTransferReturnModelSave : function($el, val, model, options) {
                if (model.isNew()) {
                    $el.prop('disabled', true);
                } else {
                    $el.prop('disabled', false);
                }
            },
            
            events : {
                'show .date' : 'stopPropagation',
                'focusin #orderNo': 'record',
                'focusout #orderNo': 'search',
                'keyup #orderNo': 'searchOnEnter',
                'click #save': 'save',
                'click #cancel': 'reset',
                'click #orderLookup': 'openLookupPopup',
                'focusout #deliveryNoteNoTransferReturn': 'searchTransferReturn',
                'click #deliveryNoteTransferReturnLookup': 'openDeliveryNoteTransferReturnDialog',
                'change #receiveType': 'handleReceiveTypeChange'
            },
            
            receiveTypeOptions : [{
	            'REGULAR' : 'Gloria.i18n.warehouse.receive.receiveType.REGULAR'
	        }, {
	            'TRANSFER' : 'Gloria.i18n.warehouse.receive.receiveType.TRANSFER'
	        }, {
	            'RETURN' : 'Gloria.i18n.warehouse.receive.receiveType.RETURN'
	        }, {
                'RETURN_TRANSFER' : 'Gloria.i18n.warehouse.receive.receiveType.RETURN_TRANSFER'
            }],
	        
	        hideShowBasedOnReceiveType: function(){
        		 var that = this;
	        	 if(that.selectedReceiveType == 'REGULAR') {
	        		 this.$el.find('.orderNo').show(); 
	        		 this.$el.find('.deliveryNoteTransferReturn').hide(); 
	        		 this.$el.find('.deliveryNoteRegular').show(); 
	        	 } else {
	        		 this.$el.find('.orderNo').hide();
	        		 this.$el.find('.deliveryNoteRegular').hide(); 
	        		 this.$el.find('.deliveryNoteTransferReturn').show(); 
	        	 }
	        },
	        
	        handleReceiveTypeChange : function(e) {
    			 var that = this;
	        	 e.preventDefault();
	        	 that.selectedReceiveType = e.currentTarget.value;
	        	 receiveType =  that.selectedReceiveType;
	        	 that.hideShowBasedOnReceiveType();
	        	 this.$('#deliveryNoteNo').val('');
	        	 this.$('#deliveryNoteNoTransferReturn').val('');
	        	 this.$('#orderNo').val('');
	        	 this.$('#supplierParmaID').empty();
	        	 this.$('#supplierName').empty();
	        	 this.$('#transportationNo').val('');
	        	 this.$('#carrier').val('');
	        	 // Clear Model Information
	        	 this.orderModel && this.orderModel.clear();
	        	 this.deliveryNoteTransferReturnModel && this.deliveryNoteTransferReturnModel.clear();
	        	 that.bindSelectedModelValues();
			},
            
            openLookupPopup: function(e) {
                e.preventDefault();
                Gloria.trigger('hideAppMessageView');
                this.hideError();
                if(!this.$('#orderNo').val()) {
                    Gloria.WarehouseApp.trigger('popup:order', this.$('#orderNo').val());
                } else {
                	this.search(e);
                }
            },
            
            openDeliveryNoteTransferReturnDialog: function(e) {
            	var that = this;
                e.preventDefault();
                Gloria.trigger('hideAppMessageView');
                this.hideError();
                Gloria.WarehouseApp.trigger('popup:deliveryNoteTransferReturn', this.$('#deliveryNoteNo').val(), that.selectedReceiveType);
            },
            
            reset: function(e) {
                e.preventDefault();
                this.$('form')[0].reset();
                this.$('#orderNo').focusout();
                var today = DateHelper.getCurrentLocalizedDate();
                this.$('.js-date').val(today);
                this.$('.deliveryNoteDateDiv').datepicker('setEndDate',today);
             
                // Clear Model Information
                this.orderModel && this.orderModel.clear();
                this.deliveryNoteTransferReturnModel && this.deliveryNoteTransferReturnModel.clear();
                this.validator(saveValidatorOptions).reset();
                this.validator(saveValidatorOptions).resetForm();
                this.validator(saveValidatorOptions).showErrors();
                this.selectedReceiveType = 'REGULAR';
                this.hideShowBasedOnReceiveType();
                this.bindSelectedModelValues();
            },
            
            record: function(e){
                this.orderNumber = this.$('#orderNo').val().trim();
            },
            
            hideError : function() {
            	$('.form-group').removeClass('has-error');
			},
            
            search: function(e) {       
            	this.hideError();
                if(this.$('#orderNo').val().trim().length == 0) {
                    this.orderModel.clear();
                    return;
                }
                if(this.orderNumber == this.$('#orderNo').val().trim()) return;
                e.preventDefault();
                Gloria.WarehouseApp.trigger('search:order', this.$('#orderNo').val());
            },
            
            searchOnEnter : function(e) {
				if(e.which == 13) {
					this.search(e);
				}
			},
            
            recordTransferReturn: function(e){
                this.dispatchNumber = this.$('#deliveryNoteNoTransferReturn').val().trim();
            },
            
            searchTransferReturn: function(e) {
            	var enteredVal = this.$('#deliveryNoteNoTransferReturn').val().trim();
                if(enteredVal.length == 0) {
                    this.deliveryNoteTransferReturnModel.clear();
                    return;
                }
                Gloria.WarehouseApp.trigger('search:dispatchNumber', enteredVal, this.selectedReceiveType);
            },
            
            handleNoRecordFound : function() {
            	Gloria.trigger('showAppMessageView', {
                    type : 'error',
                    message : [{
                        element: this.$('#orderNo'),
                        message: i18n.t('errormessages:errors.GLO_ERR_067')
                    }]
                });
			},
            
			 futureDateCheck : function() {
	            	Gloria.trigger('showAppMessageView', {
	                    type : 'error',
	                    message : [{
	                        element: this.$('#deliveryNoteDate'),
	                        message: i18n.t('Gloria.i18n.errors.GLO_ERR_110')
	                    }]
	                });
				},
			
            save: function(e) {
                e.preventDefault();
              //glo-6966. should not apply Locale and Check as syphon gives timestamp as 00:00:0000  
                var check = DateHelper.isDateInTheFuture(DateHelper.formatDate(Backbone.Syphon.serialize(this).deliveryNote.deliveryNoteDate));
                if(check) {
                	this.futureDateCheck();
                	return;
                }
                
                if(this.isValidForm(saveValidatorOptions)) {
                    var formData = Backbone.Syphon.serialize(this).deliveryNote;  
                    if(this.selectedReceiveType === "REGULAR"){
                    	_.extend(formData, this.orderModel.pick(['supplierId', 'supplierName']));
                    }else{
                        this.deliveryNoteTransferReturnModel.attributes.deliveryNoteNo = this.deliveryNoteTransferReturnModel.attributes.dispatchNoteNo;                        
                        formData['supplierId']= this.deliveryNoteTransferReturnModel.attributes.parmaID;
                        formData['supplierName'] = this.deliveryNoteTransferReturnModel.attributes.parmaName;
                        formData['carrier']  = this.deliveryNoteTransferReturnModel.attributes.carrier;
                        formData['transportationNo']  = this.deliveryNoteTransferReturnModel.attributes.transportationNo;
                    	_.extend(formData, this.deliveryNoteTransferReturnModel.pick(['deliveryNoteNo']));
                    }
                    Gloria.WarehouseApp.trigger('save:deliveryNote', formData);
                }
            },
            
            stopPropagation : function(e) {
                e.stopPropagation();
                e.stopImmediatePropagation();            
            }, 
            
            render : function() {                
                this.$el.html(compiledTemplate({
                    editable: this.model.isEditable(),
                    receiveTypeOptions: this.receiveTypeOptions
                }));
                receiveType = this.selectedReceiveType;
                this.bindSelectedModelValues();
                this.hideShowBasedOnReceiveType();
                var today = DateHelper.getCurrentLocalizedDate();
                this.$('.js-date').val(today);
                this.$('.deliveryNoteDateDiv').datepicker('setEndDate',today);
                return this;
            },

            bindSelectedModelValues : function() {
            	if (this.selectedReceiveType === "REGULAR") {
					if (this.model.isEditable() && this.orderModel) {
						this.unstickit();
						this.stickit(this.orderModel, this.orderModelBindings);
					} else {
						this.unstickit(this.orderModel);
						this.stickit();
					}
				} else {
					if (this.model.isEditable() && this.deliveryNoteTransferReturnModel) {
						this.unstickit();
						this.stickit(this.deliveryNoteTransferReturnModel, this.deliveryNoteTransferReturnModelBindings);
					} else {
						this.unstickit(this.deliveryNoteTransferReturnModel);
						this.stickit();
					}
				}			
			},
            
            validator : function(options) {
                return this.$('form').validate(options);
            },

            isValidForm : function(options) {
                return this.validator(options).form();
            },
            
            onDestroy: function() {
                this.$('.date').datepicker('remove');
            }
        });
        
    });

    return Gloria.WarehouseApp.Receive.View.DeliveryNoteInformation;
});