define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'hbs!views/procurement/overview/modifydetail/view/part-info' 
], function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PartInfoView = Backbone.Marionette.ItemView.extend({
			
			className : 'control-label',
			
			initialize : function(options) {
				this.model = options.model;
			},
			
			render : function() {
			 	this.$el.html(compiledTemplate({
			 		'data' : this.model.toJSON()
	 		    }));
			    return this;
			}
		});
	});

	return Gloria.ProcurementApp.View.PartInfoView;
});
