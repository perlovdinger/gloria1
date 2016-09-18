/**
 * Change Details Page Layout View in Gloria.
 */
define([
    'app',
    'i18next',
    'underscore',
    'handlebars',
    'marionette',
    'utils/dialog/dialog',
    'hbs!views/procurement/changedetails/view/page-layout'
], function(Gloria, i18n, _, Handlebars, Marionette, Dialog, compiledTemplate) {
    
    Gloria.module('ProcurementApp.ChangeDetails.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.ChangeDetailsView = Marionette.LayoutView.extend({
            
            template: compiledTemplate,
            
            initialize: function(options) {
            	this.collection = options.collection;
                this.listenTo(this.model, 'change:status', this.update);
                this.listenTo(this.collection, 'sync', this.showHideComponents);
            },
            
            regions : {
                generalInformation : '#generalInformation',
                gridPane : '#relatedPartGrid'            
            },
    
            events : {               
               'click #cancel' : 'goToPreviousRoute',
               'click #close' : 'goToPreviousRoute',
               'click #accept' : 'acceptChangeId',
               'click #reject' : 'rejectChangeId'
               
            },    
            
            acceptChangeId : function(e){
        		e.preventDefault();		
        		Gloria.ProcurementApp.trigger('changeID:accept');
            },
            
            rejectChangeId : function(e){
            	e.preventDefault();		
            	Gloria.ProcurementApp.trigger('changeID:reject');
            },
            
            goToPreviousRoute : function() {
                Gloria.trigger('goToPreviousRoute');
            },        
            
            render : function() {            
                this.$el.html(this.template());  
                return this;
            },
            
            update: function(status) {
                if(this.model.isWaitingForConfirmation()) {
                    this.$('#cancel').removeClass('hidden');
                    this.$('#accept').removeClass('hidden');
                    this.$('#reject').removeClass('hidden');
                    this.$('#close').addClass('hidden');
                } else if(this.model.isAccepted() || this.model.isRejected()) {
                    this.$('#cancel').removeClass('hidden');
                    this.$('#accept').addClass('hidden');
                    this.$('#reject').addClass('hidden');
                    this.$('#close').addClass('hidden');
                } else if(this.model.isCancelWait()) {
                    this.$('#cancel').addClass('hidden');
                    this.$('#accept').removeClass('hidden');
                    this.$('#reject').removeClass('hidden');
                    this.$('#close').removeClass('hidden');
                } else if(this.model.isCancelled() || this.model.isCancelRejected()){
                    this.$('#cancel').addClass('hidden');
                    this.$('#accept').addClass('hidden');
                    this.$('#reject').addClass('hidden');
                    this.$('#close').removeClass('hidden');
                }
            },
            
            showHideComponents : function() {
				this.hideAcceptButton();
				this.showMessage();
			},
            
			// Hide Accept button
            hideAcceptButton : function() {
            	var isAccepAllowed = false;
	        	this.collection.each(function(model) {
					if(!isAccepAllowed && (model.get('procureLineStatus') == 'RECEIVED'
						|| model.get('procureLineStatus') == 'RECEIVED_PARTLY') && model.get('materialType') == 'USAGE_REPLACED'
						&& (model.get('procureType') == 'INTERNAL' || model.get('procureType') == 'EXTERNAL')) {
						isAccepAllowed = true;
						this.$('#accept').hide();
					}
				});
			},
			
			// Show info message if changeAction is CANCEL_MOD_WAITING
			showMessage : function() {
				var isMessageShown = false;
				this.collection.each(function(model) {
					if(!isMessageShown && (model.get('changeAction') == 'CANCEL_MOD_WAITING')) {
						isMessageShown = true;
						this.$('#message').show();
					}
				});
			}
        });    
    });

    return Gloria.ProcurementApp.ChangeDetails.View.ChangeDetailsView;
});