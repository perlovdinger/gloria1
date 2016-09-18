define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'hbs!views/deliverycontrol/assign/view/delete-modal',
	    'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, compiledTemplate, UserHelper) {
    
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.DeleteModalView = Marionette.LayoutView.extend({
	    	
			className : 'modal',

			id : 'deleteModal',
			
	    	events : {
	            'click #yes' : 'handleYesClick',
	            'click #no' : 'handleNoClick'
	        },
	        
	        initialize : function(options) {
	        	this.module = options.module;
	        	this.model = options.model;
	        	this.team = options.team;
	        	Gloria.DeliveryControlApp.on('DeliveryControllerInfo:deleted', function(resp) {
                    this.$el.modal('hide');
                    Gloria.DeliveryControlApp.trigger('ControlButton:refresh');
                    Gloria.DeliveryControlApp.trigger('SupplierProjectGrid:clearselection');
                }, this);
	        },
	        
	        handleYesClick : function(e) {
				Gloria.DeliveryControlApp.trigger('DeliveryControllerInfo:delete', this.module, this.model);
	        },
	        
	        handleNoClick : function(e) {
	        	this.$el.modal('hide');
	        },
	        
	        render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					module : that.module,
					team : this.team ? this.team.toJSON() : {},
					dcIdName : this.team.get('defaultDcUserid') ? '\'' + UserHelper.getInstance().getDCIdName(this.team.get('defaultDcUserid')) + '\'' : '\'NA\''
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
	
    return Gloria.DeliveryControlApp.View.DeleteModalView;
});
