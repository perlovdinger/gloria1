define(['app',
        'jquery',
        'handlebars',
        'marionette',
        'hbs!views/procurement/common/procurement-module' 
], function(Gloria, $, Handlebars, Marionette, compiledTemplate) {

	var ProcurementModuleView = Marionette.LayoutView.extend({
				
		initialize : function(options) {
			this.module = options.module;
		},
		
		render : function() {
			//this.$el.html(compiledTemplate());
			Gloria.trigger('show:breadcrumb:moduleView');			    				
			return this;
		}
	});
	
	return ProcurementModuleView;
});