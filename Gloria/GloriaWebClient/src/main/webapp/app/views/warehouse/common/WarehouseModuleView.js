define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
        'hbs!views/warehouse/common/warehouse-module' 
], function(Gloria, $, _, Handlebars, Backbone, Marionette, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
	    
		View.WarehouseModuleView = Marionette.LayoutView.extend({
					
			initialize : function(options) {
				this.module = options.module;
				this.control = options.control;
			},

			render : function() {				
				this.$el.html(compiledTemplate({						
					'control' : this.control
				}));
				Gloria.trigger('show:breadcrumb:moduleView');
				return this;
			}
		});
	});
	
	return Gloria.WarehouseApp.View.WarehouseModuleView;
});
