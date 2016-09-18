define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',
        'jquery-validation',
        'hbs!views/material/overview/view/scrap-part-dialog'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, Validation, compiledTemplate) {
    
    Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.ScrapPartDialogView = Marionette.LayoutView.extend({
            
            initialize: function(options) {    			
    			this.models = options.models;
    		},
    		
            className: 'modal',
            
            id: 'scrap-modal',
            
            events: {
                'click #scrap-button' : 'scrap',
                'click #cancel-button' : 'cancel'
            },
            
            scrap: function(e) {
            	if (this.isValidForm()) {
            		var scrapQty = this.$('#scrapQty').val();
                	var comments = this.$('#comments').val();
                	Gloria.MaterialApp.trigger('MaterialRequestList:scrapPart', this.models, scrapQty, comments);
                	this.$el.modal('hide');
            	}
            },
          
            cancel: function() {
                this.$el.modal('hide');
            },
            
            validator : function() {
				var that = this;
				return this.$el.find('form').validate({
					rules: {						
						'scrapQty': {
							required: true,
			    			min: 0,
			    			digits: true
						},
						'comments' : {
							required: true
						}
					},
					messages: {
						'scrapQty': {
							required: i18n.t('errormessages:errors.GLO_ERR_012'),
							min: i18n.t('errormessages:errors.GLO_ERR_012'),
							max: i18n.t('errormessages:errors.GLO_ERR_012'),
							digits: i18n.t('errormessages:errors.GLO_ERR_012')
						},
						'comments' : {
							required: i18n.t('errormessages:errors.GLO_ERR_053')
						}
					},
					showErrors: function (errorMap, errorList) {
			        	that.showErrors(errorList);
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},

			showErrors : function(errorList) {
	        	Gloria.trigger('showAppMessageView', {
	        		modal : true,
	        		type : 'error',
	        		message : errorList
	        	});
			},

    		render : function() {
  				var that = this;
  				this.$el.html(compiledTemplate({
  					isOne : this.models.length == 1,
  					data : this.models.length == 1 ? _.first(this.models).toJSON() : {}
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
    
    return  Gloria.MaterialApp.View.ScrapPartDialogView;
});