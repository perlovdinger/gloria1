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
        'hbs!views/deliverycontrol/assign/view/reassign-modal'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, UserHelper, compiledTemplate) {
    
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ReassignModalView = Marionette.LayoutView.extend({
	    	
			className : 'modal',

			id : 'reassignModal',
			
	    	events : {
	            'click #save' : 'handleSaveClick',
	            'click #cancel' : 'handleCancelClick'
	        },
	        
	        initialize : function(options) {
	        	this.models = options.models;
	        	this.team = options.team;
	        },
	        
	        handleSaveClick : function(e) {
	        	e.preventDefault();
				var that = this;
				if (this.isValidForm()) {
					var formData = Backbone.Syphon.serialize(this);
					_.extend(formData.delivery, {deliveryControllerUserName : $('#dcUserId').select2('data').text});
					this.$el.modal('hide');
					Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:reassign', this.models, formData.delivery);
				}
	        },
	        
	        validator : function() {
				var that = this;
				return this.$el.find('form').validate({
					rules : {
						'dcUserId' : {
							required : true
						}
					},
					messages : {
						'dcUserId' : {
							required: i18n.t('errormessages:errors.GLO_ERR_035')
						}
					},
					showErrors : function(errorMap, errorList) {
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
	        
			render : function() {
				var that = this;
				this.$el.html(compiledTemplate());
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
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
						defaultDC : that.team ? that.team.get('defaultDcUserid') : '',
						teamId : savedTeamInfoObj ? savedTeamInfoObj.name : null
					});					
				});
			},

			onShow : function() {
				this.$el.modal('show');
				this.showDCSelector();
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				this.deliveryControllerSelector && this.deliveryControllerSelector.remove();
				Gloria.DeliveryControlApp.off(null, null, this);
			}
	    });
	});
	
    return Gloria.DeliveryControlApp.View.ReassignModalView;
});
