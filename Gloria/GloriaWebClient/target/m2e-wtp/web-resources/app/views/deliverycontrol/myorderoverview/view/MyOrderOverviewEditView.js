define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'bootstrap',
	    'datepicker',
	    'utils/DateHelper',
	    'i18next',
	    'hbs!views/deliverycontrol/myorderoverview/view/my-order-overview-edit'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, Datepicker, DateHelper, i18n, compiledTemplate) {
    
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.MyOrderOverviewEditView = Marionette.ItemView.extend({
			
			className : 'modal',
	            
			id: 'myOrderOverviewEditForm',
	
	        initialize : function(options) {
				this.module = options.module;
				this.models = options.models;
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverviewEditView:updatedInfo', this.handleResponse);
	        },
	
	        events : {
	    		'click .flagClass' : 'refreshFlagDropdown',
	            'click #save' : 'handleSaveClick',
	            'click #cancel' : 'handleCancelClick'
	        },
	        
			refreshFlagDropdown : function(e) {
				var that = this;
				this.$el.find('.flagClass').unbind('click');	
				this.$el.find('.dropdown.open .dropdown-toggle').dropdown('toggle');
				var attributes = this.$el.find('#' + e.currentTarget.id + ' a i').prop("attributes");
				this.$el.find('#flagSelect').empty();
	    		$.each(attributes, function() {
	    			that.$el.find('#flagSelect').attr(this.name, this.value);
	    		}); 
	    		this.$el.find('#flagSelectText').text(this.$el.find('#' + e.currentTarget.id + ' a').text());
	    		this.selectedFlag = e.currentTarget.id;
			},
			
			handleSaveClick : function(e) {
				var that = this;
				Gloria.DeliveryControlApp.trigger('MyOrderOverviewEditView:updateInfo', this.module, this.models, 
				{
					'expectedDate' : DateHelper.parseDate(that.$el.find('#expectedDate').val()),
					'plannedDispatchDate' : DateHelper.parseDate(that.$el.find('#plannedDispatchDate').val()),
					'statusFlag' : that.selectedFlag ? that.selectedFlag : null
				},{
					'staAcceptedDate' : DateHelper.parseDate(that.$el.find('#staAcceptedDate').val()),
					'staAgreedDate' : DateHelper.parseDate(that.$el.find('#staAgreedDate').val())
				});
	        },
	        
	        handleResponse : function(flag) {
				if(flag) {
					this.$el.modal('hide');
				} else {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						modal : true,
						message : new Array({
		    				message : i18n.t('Gloria.i18n.processFailed')
		    			})
					});
				}
			},
			
	        handleCancelClick : function(e) {
	        	 this.$el.modal('hide');
	        },
	        
	        render : function() {
	        	var that = this;
			    this.$el.html(compiledTemplate({
			    	module : that.module
				}));
                this.$el.modal({                                        
                    show: false
                });
                this.$el.on('hidden.bs.modal', function(){                    
                    that.trigger('hide');
                });	                
		        this.$el.find('.date').datepicker();
		        return this;
			},
		    
			onShow: function() {
                this.$el.modal('show');
            },
            
            onDestroy: function() {
                this.$('.js-date').datepicker('remove');               
                this.$el.modal('hide');
                this.$el.off('.modal');
            }
	    });
	});
    
    return Gloria.DeliveryControlApp.View.MyOrderOverviewEditView;
});
