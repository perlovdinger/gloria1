define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'i18next',
		'backbone.syphon',
		'jquery-validation',
		'utils/dialog/dialog',
		'utils/DateHelper',
		'hbs!views/material/requests/view/material-requests'
], function(Gloria, $, _, Handlebars, Marionette, i18n, Syphon, Validator, Dialog, DateHelper, compiledTemplate) {
    
	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.MaterialRequestsView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
				this.currentRoute = Backbone.history.getFragment();
				this.initializeListeners();
				this.model = options.model;
			},
			
			initializeListeners : function() {
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:select', this.gridClickHandler, this);
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:sent', this.handleResponse, this);
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:removed', this.handleMaterialRemove, this);
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:disableSend', this.disableSendButton, this);
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:disableSave', this.disableSaveButton, this);
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:disableCancelRequestList', this.disableCancelRequestListButton, this);
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:disabledisableRequestLater', this.disableRequestLaterButton, this);
			},
			
			regions : {
				generalInfo : '#generalInfo',
				gridInfo : '#gridInfo'
			},
	        
			events : {
				'click #save-button' : 'handleRequestListOperation',
				'click #send-button' : 'handleRequestListOperation',
				'click #cancelrequestlist-button' : 'handleRequestListOperation',
				'click #remove-button' : 'handleRemoveClick',
				'click #cancel-button' : 'handleCancelClick',
			},
			
			gridClickHandler : function(selectedModels) {
				this.selectedModels = selectedModels;
	            if (selectedModels && selectedModels.length > 0 && this.model.attributes.requestListIdStatus!="SENT" ) {
	            	this.$('#remove-button').removeAttr('disabled');
	            } else {
	            	this.$('#remove-button').attr('disabled', true);
	            }
			},
			
			handleRemoveClick : function(e) {
				e.preventDefault();
				var formData = this.getFormData();
				var action = e.target.name;
				var that = this;
				Dialog.show({
			    	title : i18n.t('Gloria.i18n.material.requests.remove.messageTitle') + ' | ' + i18n.t('Gloria.i18n.gloriaHeader'),
			    	message: i18n.t('Gloria.i18n.material.requests.remove.messageBody'),
                    buttons: {
		                yes: {
		                    label: i18n.t('Gloria.i18n.buttons.yes'),
		                    className: "btn btn-primary",
		                    callback: function(e) {
		                        e.preventDefault();
		                        Gloria.MaterialApp.trigger('MaterialRequestList:remove', that.selectedModels,that.model,formData.request,action);
		                        return true;
		                    }
		                },
		                no: {
		                    label: i18n.t('Gloria.i18n.buttons.no'),
		                    className: "btn btn-default",
		                    callback: function(e) {
		                        e.preventDefault();
		                        return true;
		                    }
		                }
                    }
                });
			},

			cancelrequestlistClick : function(e){
				e.preventDefault();
				var that = this;
				Dialog.show({
			    	title : i18n.t('Gloria.i18n.material.requests.requestlistremove.messageTitle') + ' | ' + i18n.t('Gloria.i18n.gloriaHeader'),
			    	message: i18n.t('Gloria.i18n.material.requests.requestlistremove.messageBody'),
                    buttons: {
		                yes: {
		                    label: i18n.t('Gloria.i18n.buttons.yes'),
		                    className: "btn btn-primary",
		                    callback: function(e) {
		                        e.preventDefault();
		                        //Gloria.MaterialApp.trigger('MaterialRequestList:remove', that.selectedModels);
		                        return true;
		                    }
		                },
		                no: {
		                    label: i18n.t('Gloria.i18n.buttons.no'),
		                    className: "btn btn-default",
		                    callback: function(e) {
		                        e.preventDefault();
		                        return true;
		                    }
		                }
                    }
                });
			
			},
			
			handleRequestListOperation : function(e) {
				
				if(($('.backgrid').find('.error').length == 0)) {
					e.preventDefault();
					if (this.isValidForm()) {
						var formData = this.getFormData();
						Gloria.MaterialApp.trigger('MaterialRequestList:manageRequestListOperation', formData.request,e.target.name);
					}
				}				
			},
			
			getFormData : function(){
				var formData = Backbone.Syphon.serialize(this);
					if(formData.request.deliveryAddressType == 'WH_SITE') {
						try {
							formData.request.deliveryAddressName = $('#tranferToWarehouseDropDown').select2('data').text.split(' - ')[1].trim();
						} catch(e){};
					} else if(!formData.request.deliveryAddressType) {
						formData.request.deliveryAddressId = $('#buildLocation').text().split(' - ')[0];
						formData.request.deliveryAddressName = $('#buildLocation').text().split(' - ')[1];
					}
					return formData;
			},
			
			validator : function() {
				return this.$el.find('#requestForm').validate({
					ignore: [],
					rules: {
						'request[requiredDeliveryDate]': {
							required: true
						},
						'request[deliveryAddressType]': {
							required: {
								depends: function(element) {
									return $('#buildLocation').text() == ' - ';
								}
							}
						},
						'request[deliveryAddressId]': {
							required: {
								depends: function(element) {
									return $('#transferToWarehouse').is(':checked');
								}
							}
						},
						'request[deliveryAddressName]': {
							required: {
								depends: function(element) {
									return $('#newDeliveryAddress').is(':checked');
								}
							}
						}
					},
					messages: {
						'request[requiredDeliveryDate]': {
							required: i18n.t('errormessages:errors.GLO_ERR_032')
						},
						'request[deliveryAddressType]': {
							required: i18n.t('errormessages:errors.GLO_ERR_068')
						},
						'request[deliveryAddressId]': {
							required: i18n.t('errormessages:errors.GLO_ERR_034')
						},
						'request[deliveryAddressName]': {
							required: i18n.t('errormessages:errors.GLO_ERR_033')
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

			handleCancelClick : function(e) {
				e.preventDefault();
				Gloria.MaterialApp.trigger('MaterialRequestList:cancel');
			},
			
			handleResponse : function(flag) {
				if(flag) {
					Backbone.history.navigate('material/linesoverview', {
						trigger : true
					});
				} else {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : new Array({
		    				message : i18n.t('Gloria.i18n.processFailed')
		    			})
		    		});
				}
			},
			
			handleMaterialRemove : function() {
				this.$('#remove-button').attr('disabled', true);
			},
			
			disableSendButton : function(){
			    this.$('#send-button').attr('disabled', true);
			},
			
			disableSaveButton : function(){
			    this.$('#save-button').attr('disabled', true);
			},
			
			disableCancelRequestListButton : function() {
				this.$('#cancelrequestlist-button').attr('disabled', true);
			},
			
			disableRequestLaterButton : function() {
				this.$('#remove-button').attr('disabled', true);
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.currentRoute.indexOf("requestList") >= 0 ? false : true
				}));
				return this;
			},
			
			onShow: function() {
				this.validator();
			},
			
			onDestroy : function() {
				Gloria.MaterialApp.off(null, null, this);
			}
		});
	});
	
	return Gloria.MaterialApp.View.MaterialRequestsView;
});
