define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',       
        'hbs!views/material/overview/view/borrow-part-dialog'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, compiledTemplate) {
    
    Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.BorrowPartDialogView = Marionette.LayoutView.extend({
            
            initialize: function(options) {    			
    			this.materialLine = options.model;
	        	this.listenTo(Gloria.MaterialApp, 'BorrowPartGrid:select', this.gridClickHandler, this);
    		},
    		
    		regions : {
    			gridInfo : '#gridInfo'
			},
    		
            className: 'modal',
            
            id: 'borrowMaterialLinesModal',
            
            events: {
                'click #borrow-button' : 'borrowMaterialInfo',
                'click #cancel-button' : 'cancelMaterialInfo'
            },
            
            gridClickHandler : function(selectedModels) {
            	if(!selectedModels.length) {
            		this.resetBorrow();
            		return;
            	}
            	
				this.selectedModelIds = _.map(selectedModels, function(model) {
					return model.id;
				});
				
				var invalidBorrowStatus = ['REQUISITION_SENT', 'ORDER_PLACED_INTERNAL', 'ORDER_PLACED_EXTERNAL'];
				var hasInvalidBorrowStatus = _.any(selectedModels, function(model) {
					return _.contains(invalidBorrowStatus, model.get('status'));
				});
				
				if(hasInvalidBorrowStatus) {
					this.disableBorrow();					
				} else {
					this.enableBorrow();			
				}				
	        },
	        
	        resetBorrow: function() {
	        	this.$('#borrow-button').attr('disabled', true);
	        	this.$('div#borrowWarning').addClass('hidden');
	        },
	        
	        disableBorrow: function() {
	        	this.$('#borrow-button').attr('disabled', true);
				this.$('div#borrowWarning').removeClass('hidden');	
	        },
	        
	        enableBorrow: function() {
	        	this.$('#borrow-button').removeAttr('disabled');
				this.$('div#borrowWarning').addClass('hidden');
	        },
            
            borrowMaterialInfo: function(e) {
            	e.preventDefault();
				this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:partBorrowed', function(flag) {
					this.$el.modal('hide');
				}, this);
				var noReturn = $('#noReturn').prop('checked');
            	Gloria.MaterialApp.trigger('MaterialRequestList:borrowPart', this.materialLine, this.selectedModelIds.join(','), noReturn);
            },
          
            cancelMaterialInfo: function() {
                this.$el.modal('hide');
            },

    		render : function() {
  				var that = this;
  				this.$el.html(compiledTemplate({
  					data : that.materialLine ? that.materialLine.toJSON() : {},
  					status : that.materialLine ? i18n.t('Gloria.i18n.materialLineStatus.' + that.materialLine.get('status')) : ''
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
    
    return  Gloria.MaterialApp.View.BorrowPartDialogView;
});