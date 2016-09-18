define([ 
         'app',
         'jquery',
         'handlebars',
         'marionette',
         'hbs!views/materialrequest/common/material-request-module' 
], function(Gloria, $, Handlebars, Marionette, compiledTemplate) {
    
	var MaterialRequestModuleView = Marionette.LayoutView.extend({
				
		initialize : function(options) {
			this.module = options.module;
		},
        
		render : function() {
			this.$el.html(compiledTemplate());
			Gloria.trigger('show:breadcrumb:moduleView');			    				
			return this;
		}
	});
	
	return MaterialRequestModuleView;
});