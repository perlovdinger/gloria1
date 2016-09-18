define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'jquery-validation',
        'hbs!views/reports/view/favorite-modal'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, compiledTemplate) {
    
	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.FavoriteModalView = Marionette.LayoutView.extend({
	    	
			className : 'modal',

			id : 'favorite',
			
	    	events : {
	            'click #save' : 'handleSaveClick',
	            'click #cancel' : 'handleCancelClick'
	        },
	        
	        initialize : function(options) {
	        	this.module = options.module;
	        },
	        
	        handleSaveClick : function(e) {
	        	e.preventDefault();
	        	if (this.isValidForm()) {
	        		Gloria.ReportsApp.trigger('Report:Favorite:add', $('#name').val());
		        	this.$el.modal('hide');
	        	}
	        },
	        
	        validator : function() {
				return this.$el.find('form').validate({
					rules: {						
						'name': {
							required : true
						}
					},
					messages: {						
						'name': {
							required: i18n.t('errormessages:errors.GLO_ERR_070')
						}
					},
					showErrors: function (errorMap, errorList) {
						Gloria.trigger('showAppMessageView', {
							modal : true,
							type : 'error',
							message : errorList
						});
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},

			handleCancelClick : function(e) {
				e.preventDefault();
				this.$el.modal('hide');
			},
	        
			render : function() {
				var that = this;
				this.$el.html(compiledTemplate());
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
			},
			
			onShow : function() {
				this.$el.modal('show');
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
			}
	    });
	});
	
    return Gloria.ReportsApp.View.FavoriteModalView;
});
