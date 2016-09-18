define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
		'hbs!views/testingutility/view/migration'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	Gloria.module('TestingUtilityApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MigrationView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
			},
			
			regions : {
				loaddata : '#loaddata',
				initdata : '#initdata'
			},

			events : {
				'click #migrationTab a' : 'handleTabClick'
	        },
	        
	        handleTabClick : function(e) {
				e.preventDefault();				
				Backbone.history.navigate('testingutility/migration/' + e.currentTarget.hash.split("#")[1], {
				    trigger: true
				});
			},
			
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			},
			
			onShow : function() {							
			    var tabId = this.module ? '#migrationTab a[href="#' + this.module + '"]' : '#migrationTab a:first';
			    this.$(tabId).tab('show');
			}
		});
	});

	return Gloria.TestingUtilityApp.View.MigrationView;
});
