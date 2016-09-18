define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',       
        'hbs!views/material/overview/view/release-part-dialog'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, compiledTemplate) {
    
    Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.ReleasePartDialogView = Marionette.View.extend({
            
            className: 'modal',
            
            id: 'releaseMaterialLinesModal',
            
            events: {
                'click #release-button' : 'releaseMaterialInfo',
                'click #cancel-button' : 'cancelMaterialInfo'
            },
            
    		releaseMaterialInfo: function(e) {
            	e.preventDefault();            
        	    this.$('#release-button').attr('disabled', true);
        		var confirmationText = this.$('#confirmationText').val();
                Gloria.MaterialApp.trigger('MaterialLines:released', this.releaseMaterialLines, confirmationText);
                this.$el.modal('hide');  
            },
          
            cancelMaterialInfo: function() {
                this.$el.modal('hide');
            },
            
            initialize: function(options) {    			
    			this.releaseMaterialLines = options.releaseMaterialLines;
    		},            

    		render : function() {
  				var that = this;
  				this.$el.html(compiledTemplate({  					
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
            
            onShow: function() {
                this.$el.modal('show');                
            },
            
            onDestroy: function() {
                this.$el.modal('hide');
                this.$el.off('.modal');                
            }
        });
    });
    
    return  Gloria.MaterialApp.View.ReleasePartDialogView;
});