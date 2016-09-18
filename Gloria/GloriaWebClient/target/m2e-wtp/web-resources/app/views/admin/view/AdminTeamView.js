define(['app',
		'jquery',
		'i18next',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'hbs!views/admin/view/admin-team'
], function(Gloria, $, i18n, _, Handlebars, Backbone, Marionette, compiledTemplate) {
	
	Gloria.module('AdminTeamApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.AdminTeamView = Marionette.LayoutView.extend({
    	
			initialize : function(options) {
				this.module = options.module;
			},
			
            events : {
            	'click #adminTab a' : 'handleAdminTabClick'
			},
			
			handleAdminTabClick : function(e) {
				e.preventDefault();
				Backbone.history.navigate('admin/' + e.currentTarget.hash.split("#")[1], {
				    trigger: true
				});
			},
			
	        render : function() {
	        	this.$el.html(compiledTemplate());
				return this;
			},
			
			onShow : function() {							
			    var tabId = this.module ? '#adminTab a[href="#' + this.module + '"]' : '#adminTab a:first';
			    this.$(tabId).tab('show');
			}
	    });
	 });
    
    return Gloria.AdminTeamApp.View.AdminTeamView;
});
