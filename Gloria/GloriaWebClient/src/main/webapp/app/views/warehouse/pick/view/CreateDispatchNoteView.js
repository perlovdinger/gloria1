define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'backbone.syphon',
        'jquery-validation',
        'datepicker',
        'moment',
        'hbs!views/warehouse/pick/view/createDispatchNote'
],function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Syphon, Validation, Datepicker, moment, compiledTemplate) {
    

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.CreateDispatchNoteView = Marionette.LayoutView.extend({
	
	    	regions : {
	    		generalInfo : '#generalInfo',
	    		partInfo : '#partInfo'
			},

			initialize : function(options) {			    
			    this.isShipped = options.isShipped;		
			    this.mode = options.mode;		

			},			
			
            events : {          
            	'click .accordion-toggle.collapsed' : 'publishAccordionCollapseEvent',
                'click #save' : 'handleSaveClick',               
                'click #cancel' : 'handleCancelClick',
                'click #close' : 'handleCancelClick'
            },
            
            handleSaveClick : function() {
            	var that = this;
        		var formData = Backbone.Syphon.serialize(that);	
        		var markAsShipped = '';
        		var printDispatchNote = false;
        		
        		if($('#markAsShipped').prop('checked')){
        			markAsShipped = 'markAsShipped';
        		}
        		if($('#printDispatchNote').prop('checked')){
        			printDispatchNote = true;
        		}

    			Gloria.WarehouseApp.trigger('DispatchNote:update', formData, markAsShipped, printDispatchNote, that.mode);
	        	
	        },
	        
//	        handleDeleteClick : function() {
//	        	Gloria.WarehouseApp.trigger('DispatchNote:delete');
//	        },
	        
	        handleCancelClick : function() {	        	
	        	Gloria.WarehouseApp.trigger('RedirectToShipOverview');	        	
	        },

            hideButtons : function(){
            	this.$el.find('#notShipped').hide();
            	this.$el.find('#shipped').show();
            },
            
            showButtons: function(){
            	this.$el.find('#notShipped').show();
            	this.$el.find('#shipped').hide();
            },
            
            publishAccordionCollapseEvent : function(e) {	        	
        		Gloria.WarehouseApp.trigger('dispatchNote:loadDispatchNotePartAccordion', this.mode); 
	        },
           
			render : function() {
				this.$el.html(compiledTemplate({					
					'isShipped' : this.isShipped,
					
				}));
				this.$('.date').datepicker();
				
				if(this.isShipped){
					this.hideButtons();
				}else{
					this.showButtons();
				}
				return this;
			},
            
            onDestroy: function() {
                this.$('.date').datepicker('remove');
            }
		});
	});

	return Gloria.WarehouseApp.View.CreateDispatchNoteView;
});
