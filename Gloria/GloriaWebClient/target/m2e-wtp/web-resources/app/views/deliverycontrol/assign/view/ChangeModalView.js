define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'backbone.syphon',
	    'utils/UserHelper',
	    'hbs!views/deliverycontrol/assign/view/change-modal',
	    'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Syphon, UserHelper, compiledTemplate, UserHelper) {
    
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ChangeModalView = Marionette.LayoutView.extend({
	    	
			className : 'modal',

			id : 'changeModal',
			
	    	events : {
	    		'click #save' : 'handleSaveClick',
	            'click #cancel' : 'handleCancelClick'
	        },
	        
	        initialize : function(options) {
	        	this.module = options.module;
	        	this.team = options.team;
	        	this.model = options.model;
	        	Gloria.DeliveryControlApp.on('DeliveryControllerInfo:changed', function(resp) {
                    this.$el.modal('hide');
                }, this);
	        },
	        
	        handleSaveClick : function(e) {
	        	e.preventDefault();
				var formData = Backbone.Syphon.serialize(this);
				Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:change', this.module, formData.delivery);
	        },
	        
	        handleCancelClick : function(e) {
	        	this.$el.modal('hide');
	        },
	        
	        render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					module : that.module,
					data : that.model ? that.model.toJSON() : {},
					team : that.team,
					dcIdName : that.model.get('defaultDcUserid') ? '\'' + UserHelper.getInstance().getDCIdName(that.model.get('defaultDcUserid')) + '\'' : '\'NA\''
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

			showDCSelector : function() {
				var that = this;
				var currentUser = UserHelper.getInstance().getUserId();
				var savedTeamInfo = window.localStorage.getItem('Gloria.User.DefaultTeam.' + currentUser);
				var savedTeamInfoObj = JSON.parse(savedTeamInfo);
				require(['views/deliverycontrol/common/DeliveryControllerSelector'], function(DeliveryControllerSelector) {
					that.deliveryControllerSelector = new DeliveryControllerSelector({
						element : '#defaultDcUserid',
						name : 'delivery[defaultDcUserid]',
						suggestedDC : that.model ? that.model.get('defaultDcUserid') : '',
						defaultDC : that.model ? that.model.get('defaultDcUserid') : '',
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
	
    return Gloria.DeliveryControlApp.View.ChangeModalView;
});
