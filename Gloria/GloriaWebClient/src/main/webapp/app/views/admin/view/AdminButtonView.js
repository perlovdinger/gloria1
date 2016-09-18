define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
        'i18next',
		'hbs!views/admin/view/admin-button'
], function(Gloria, $, _, Handlebars, Marionette, i18n, compiledTemplate) {
    
	Gloria.module('AdminTeamApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.AdminButtonView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
				this.module = options.module;
				this.permittedActions = {}; 
	            this.listenTo(Gloria.AdminTeamApp, 'AdminGrid:select', this.gridSelectHandler);
				this.listenTo(Gloria.AdminTeamApp, 'AdminButton:refresh', this.refreshThis);
			},
			
			events : {
				'click #clear-filter' : 'clearFilter',
				'click #edit-button' : 'handleEditButtonClick'
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},

			gridSelectHandler : function(selectedRows) {
				this.selectedModels = selectedRows;
				this.permittedActions = _.clone(this.defalutPermittedActions);
				if (this.selectedModels.length == 1) {
					this.permittedActions['edit'] = true;
				} else {
					delete this.permittedActions['edit'];
				}
				this.render();
			},
			
			refreshThis : function() {
                this.permittedActions = {};
                this.render();
			},
			
			defalutPermittedActions: {
                edit: false
            },
            
	        resetButtons : function() {
	        	this.permittedActions = {};
	        	this.render();
			},
			
			handleEditButtonClick : function(e) {
				Gloria.trigger('hideAppMessageView');
				Gloria.AdminTeamApp.trigger('AdminGrid:AssignUserModal:show', _.first(this.selectedModels));
			},
	        
			render : function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.permittedActions || this.defalutPermittedActions
				}));
				return this;
			},
			
			onDestroy : function() {
				Gloria.AdminTeamApp.off(null, null, this);
			}
		});
	});
	
	return Gloria.AdminTeamApp.View.AdminButtonView;
});
