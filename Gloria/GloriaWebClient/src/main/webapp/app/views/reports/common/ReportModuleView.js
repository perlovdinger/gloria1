define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
        'hbs!views/reports/common/report-module' 
], function(Gloria, $, _, Handlebars, Backbone, Marionette, compiledTemplate) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ReportModuleView = Marionette.LayoutView.extend({
					
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
	
	return Gloria.ReportsApp.View.ReportModuleView;
});
