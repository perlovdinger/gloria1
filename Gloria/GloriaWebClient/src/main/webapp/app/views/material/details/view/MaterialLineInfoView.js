define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
	    'bootstrap',
	    'datepicker',
        'moment',
	    'i18next',
	    'hbs!views/material/details/view/material-line-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, Datepicker, moment, i18n, compiledTemplate) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.MaterialLineInfoView = Marionette.View.extend({
	        
	    	initialize : function(options) {
	    		this.model = options.model;
	    		this.listenTo(Gloria.MaterialApp, 'editMode:materialLines', this.enterEditMode);
	    	},
	        
	        render : function() {
	        	this.$el.html(compiledTemplate({
	        		data: this.model ? this.model.toJSON() : {}
	        	}));
	        	 this.$('.date').datepicker();
	            return this;
	        },
	        
	        enterEditMode: function() {
	        	var notAllowedStatus = ['CREATED', 'WAIT_TO_PROCURE', 'ORDER_PLACED_INTERNAL', 'REQUISITION_SENT', 'ORDER_PLACED_EXTERNAL'];
				if(!_.contains(notAllowedStatus, this.model.get('status'))) {
					this.$('#expirationDateSpan').addClass('hidden');
					this.$('#expirationDateDiv').removeClass('hidden');
				}
	        },
	        
	        showHideButtons : function() {	        	
				var allowedStatusForPull = [ 'READY_TO_STORE', 'STORED' ];
				if(_.contains(allowedStatusForPull, this.model.get('status'))) {
					$('#pull').removeClass('hidden');
				}
			},
	        
			onShow : function() {				
				this.showHideButtons();
			},
			
	        onDestroy: function() {
	        	this.$('.date').datepicker('remove');
            }
	    });
	});
    
	return Gloria.MaterialApp.View.MaterialLineInfoView;
 });
