define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
        'bootstrap',
        'jquery.fileupload',
        'datepicker',
        'moment',
        'i18next',
        'hbs!views/deliverycontrol/myorderoverview/details/view/delivery-line-detail'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, fileupload, Datepicker, moment, i18n, compiledTemplate) {
	
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
	    View.DeliveryLineDetailView = Marionette.LayoutView.extend({
	
	    	initialize : function(options){
	    		 this.deliveryLineOverviewModel = options.model;
	    	},
	    	
	    	/**
	    	 *TODO Refactoring Handlebars Helper, Move it to somewhere else more general 
	    	 */
	    	/**
	    	 * Handlebars helper to render agreedSta as editable/non editable
	    	 */
	    	agreedStaEditableHelper : function(positiveRender, context) {
	    		var stmt = !this.data.agreedStaDate || this.data.orderStaChanged;
	    		if (!positiveRender) stmt = !stmt;
	    		if (stmt) {
	    			return context.fn(this);
	    		};
	    	},
	
	    	className: "panel panel-default",
	
	    	events : {
	    		'click .flagClass' : 'handleFlagChange',
	    		'show .js-date' : 'stopPropagation'
	        },
	        
	        handleFlagChange : function(e) {
				this.refreshFlagDropdown(e);
			},
			
			refreshFlagDropdown : function(e) {
				var currentFlagDropdown = $(e.currentTarget.parentElement).closest('div.dropdown');
				var currentDeliveryId = currentFlagDropdown[0].id.match(/\d+/)[0];
	    		var hiddenInputName = "deliveryschedules[" + currentDeliveryId +"][statusFlag]" ;
	    		
				this.$el.find('.flagClass').unbind('click');	
				this.$el.find('.dropdown.open .dropdown-toggle').dropdown('toggle');
				var attributes = this.$el.find('#' + e.currentTarget.id + ' a i').prop("attributes");
				currentFlagDropdown.find('#flagSelect').empty();
	    		$.each(attributes, function() {
	    			currentFlagDropdown.find('#flagSelect').attr(this.name, this.value);
	    		}); 
	    		var selectedText = this.$el.find('#' + e.currentTarget.id + ' a').text();
	    		currentFlagDropdown.find('#flagSelectText').text(selectedText);    	
	    		this.selectedFlag = e.currentTarget.id;
	    		this.$el.find('input:hidden[name="'+ hiddenInputName+'"]').val( this.selectedFlag );    		
			},
	    	
	    	stopPropagation : function(e) {
				e.stopPropagation();
				e.stopImmediatePropagation();			 
	    	},
	    	    	
			render : function() {      	
			 	this.$el.html(compiledTemplate({ 
	 		 		'cid' : this.cid,
	 				'data' : this.deliveryLineOverviewModel.toJSON(),
	 				'agreedStaEditable' : this.agreedStaEditableHelper
	 		    }));
			 	// Attaching datepicker to text fields with class js-date
			 	this.$('.js-date').datepicker();
			    return this;
			},
            
            onDestroy: function() {
                this.$('.js-date').datepicker('remove');
            }		
	    });
	});
    
    return Gloria.DeliveryControlApp.View.DeliveryLineDetailView;
});
