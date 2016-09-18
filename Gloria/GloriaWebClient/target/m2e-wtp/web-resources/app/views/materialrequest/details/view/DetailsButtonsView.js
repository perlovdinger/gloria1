define(['app',
        'jquery',
        'i18next',
        'underscore',
		'handlebars',
		'marionette',
		'hbs!views/materialrequest/details/view/details-buttons'
], function(Gloria, $, i18n, _, Handlebars, Marionette, compiledTemplate) {
    
	Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.DetailsButtonsView = Marionette.LayoutView.extend({
			
			initialize: function(options) {
				this.model = options.model;
				this.collection = options.collection;
				this.listenTo(this.collection, 'add remove', this.updateButtons);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:buttons:enable', this.enableButtons);
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:buttons:disable', this.disbaleButtons);
            },
            
            enableButtons: function(buttons) {
            	var that = this;
				_.each(buttons, function(button) {
					that.$el.find('#' + button).removeAttr('disabled');
				});
			},
			
			disbaleButtons : function(buttons) {
				var that = this;
				_.each(buttons, function(button) {
					that.$el.find('#' + button).attr('disabled', true);
				});
			},
			
			updateButtons : function() {
				if(this.collection.length > 0) {
					this.enableButtons(['send']);
				} else {
					this.disbaleButtons(['send']);
				}
			},
			
			buttonPermissions: function() {               
                return {
                    save: this.model.isSavable(), 
                    send: this.model.isSendable(),
                    cancel: this.model.isCancelable(),
                    'delete': this.model.isDeletable(),
                    revert: this.model.isRevertable(),
                    close: this.model.isClosable(),
                    newVersion: this.model.isNewVersionAvailable(),
                    cancelMaterialRequest: this.model.isMaterialRequestCancelable(),
                    copyAndCreateNew: this.model.isCopyAndCreateNew(),
                    cancelRejected: this.model.isCancelRejected()
                };
            },
	        
			render: function() {
				 this.$el.html(compiledTemplate(this.buttonPermissions()));  
	             return this;
			}
		});
	});
	
	return Gloria.MaterialRequestApp.View.DetailsButtonsView;
});
