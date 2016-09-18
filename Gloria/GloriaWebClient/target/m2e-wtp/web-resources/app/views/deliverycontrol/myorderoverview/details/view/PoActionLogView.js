define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
	    'bootstrap',
	    'i18next',
	    'hbs!views/deliverycontrol/myorderoverview/details/view/po-action-log'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, i18n, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.PoActionLogView = Marionette.View.extend({
	        
	    	initialize : function(options) {
	    		this.collection = options.collection;
	    		this.isEditable = options.isEditable;
	    		this.module = options.module;
				this.collection.off('add remove change');
                this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:addedOrderLog', function(status) {
                    if(!status) {
                        Gloria.trigger('showAppMessageView', {
                            type : 'error',
                            message : new Array({
                                message : i18n.t('Gloria.i18n.saveFailed')
                            })
                        });
                    }
                });
	        },
	        
	        events : {
	            'click #addorderlog' : 'handleAddButtonClick'
	        },
	        
	        handleAddButtonClick : function(e) {
				e.preventDefault();
				var logMessage = $('#orderlog').val();
				logMessage = logMessage ? logMessage.trim() : '';
				if(logMessage) {
					Gloria.DeliveryControlApp.trigger('OrderLineDetail:addOrderLog', logMessage);
				}
			},
	        
	        render : function() {
	        	this.$el.html(compiledTemplate({
	                'data' : this.collection ? this.collection.toJSON() : [],
	                isDisabled : !this.isEditable || this.module == 'completed'
	            }));
	            return this;
	        },
	        
	        onShow : function() {
				this.collection.on('add remove change', this.render);
			}
	    });
	});
    
	return Gloria.DeliveryControlApp.View.PoActionLogView;
 });
