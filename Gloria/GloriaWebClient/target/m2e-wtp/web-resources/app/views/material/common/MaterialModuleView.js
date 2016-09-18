define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
        'hbs!views/material/common/material-module' 
], function(Gloria, $, _, Handlebars, Backbone, Marionette, compiledTemplate) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MaterialModuleView = Marionette.LayoutView.extend({
					
			initialize : function(options) {
				this.module = options.module;
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					'state' : this.module
				}));
				Gloria.trigger('show:breadcrumb:moduleView');                    				
				return this;
			}
		});
	});
	
	return Gloria.MaterialApp.View.MaterialModuleView;
});
