define(['app',
        'underscore',
        'handlebars',
        'marionette',
        'jquery-validation',
		'hbs!views/testingutility/view/message'
], function(Gloria, _, Handlebars, Marionette, Validation, compiledTemplate) {
	

	var messageTypeOtions = [ {
			'' : 'Please Select'
		}, {
			'userGatewayQueue' : 'User Queue'
		}, {
			'purchaseOrderGatewayQueue' : 'Purchase Order Queue'
		}, {
			'costCenterGatewayQueue' : 'Cost Center Queue'
		}, {
			'wbsElementGatewayQueue' : 'WBS Element Queue'
		}, {
			'carryOverGatewayQueue' : 'Carry-over Queue'
		}, {
            'requisitionSenderQueue' : 'Requisition Sender Queue'
        }, {
			'procureRequestTOGatewayQueue' : 'Procure Request Queue'
        }, {
            'goodsReceiptSenderQueue' : 'Goods Receipt Sender Queue'
        }, {
            'processPurchaseOrderQueue' : 'Process Purchase Order Sender Queue'
        }, {
            'materialProcureResponseSenderQueue' : 'Material Procure Response Sender Queue'
	} ];

	Gloria.module('TestingUtilityApp.View', function(View, Gloria, Backbone,
			Marionette, $, _) {

		View.MessageView = Marionette.LayoutView.extend({
		    
		    initialize: function(options) {
		        this.reader = new FileReader();
		        this.reader.onloadend = _.bind(this.onReaderLoadEnd, this);
		        this.listenTo(Gloria.TestingUtilityApp, 'Message:posted', this.onMessagePosted);
		    },
		    
		    onReaderLoadEnd: function(e) {		        
                if (e.target.readyState == FileReader.DONE) {
                    this.$('#message').val(e.target.result);
                }
		    },
		    
		    onMessagePosted: function(status) {
                Gloria.trigger('showAppMessageView', {
                    type : status,
                    message : new Array({
                        message : status == 'error' ? 'There is an issue in posting the XML data.' : 'The XML data has been posted successfully.'
                    })
                });
            },

			events : {
				'click #post' : 'postClickHandler',
				'click #cancel' : 'cancelClickHandler',
				'change #messageFormat' : 'changeFormat',
				'change #messageType' : 'changeType',
				'change #files': 'handleFileSelect',
				'click #files': 'handleFileClick'
			},
			
			handleFileClick: function(e) {
			    this.$('#files').value = null;
			},			
			
			format: 'xml',
			
			changeFormat: function(e) {
			    this.format = this.$('#messageFormat').val();
			    this.render();
			},
			
			changeType: function(e) {
                this.type = this.$('#messageType').val();                
            },
			
			postClickHandler : function(evt) {
				Gloria.trigger('hideAppMessageView');				
				
				if(this.format == 'csv') {
				    Gloria.TestingUtilityApp.trigger('Message:post', this.getFormData());
				} else {
				    if(this.isValidForm()) {
				        Gloria.TestingUtilityApp.trigger('Message:post', this.getFormData());
				    }
				}
			},
			
			getFormData : function() {
				return {
					jmsQueueId : $('#messageType').val(),
					format: this.format,
					messageContent : $('#message').val()
				};
			},
			
			cancelClickHandler : function() {
				Gloria.trigger('goToPreviousRoute');
			},
			
			checkFileCompatibility : function() {
				if (window.File && window.FileReader && window.FileList && window.Blob) {
					return true;
				} else {
					return false;
				}
			},

			handleFileSelect : function(evt) {
				var file = evt.target.files[0];				
				//var blob = file.slice(0, file.size);				
			    this.reader.readAsText(file);
			},
			
			validator : function() {
				//Validation to check valid XML
				$.validator.addMethod('isValidXMLData', function(num, element) {
					var flag = false;
					try {
						$.parseXML($('#message').val());
						flag = true;
					} catch(e) {
						flag = false;
					}
	                return flag;
	            });
				return this.$el.find('#messageForm').validate({
					rules: {
						messageType: {
							required: true
						},
						message: {
							required: true,
							isValidXMLData: true
						}
					},
					messages: {
						messageType: {
							required: 'Please select a message type from dropdown list.'
						},
						message: {
							required: 'Please select an XML document or enter some message to post.',
							isValidXMLData: 'Provided information is not a valid XML data, please correct it to proceed.'
						}
					},
			        showErrors: function (errorMap, errorList) {
			        	Gloria.trigger('showAppMessageView', {
		        			type : 'error',
		        			message : errorList
		        		});
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},
			
			render : function() {			
				this.$el.html(compiledTemplate({
					isCompatible : this.checkFileCompatibility(),
					messageTypeOtions: messageTypeOtions,
					format: this.format,
					type: this.type
				}));
				return this;
			},
			
			onShow : function() {}
		});
	});

	return Gloria.TestingUtilityApp.View.MessageView;
});
