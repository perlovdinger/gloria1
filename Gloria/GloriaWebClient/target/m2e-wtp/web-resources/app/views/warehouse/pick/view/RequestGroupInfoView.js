define(['app',
        'backbone',
        'marionette',
        'handlebars',
		'hbs!views/warehouse/pick/view/requestgroup-info' 
], function(Gloria, Backbone, Marionette, Handlebars, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.RequestGroupInfoView = Marionette.View.extend({			
			 
			 initialize : function(options) {
				 this.model = options.model;
			 },
			 
			 render : function() {		
			 	this.$el.html(compiledTemplate({			 		
			 		'requestGroupModel' : this.model.toJSON(),
	 		    }));
			    return this;
			}
			
		});
		
	});

	return Gloria.WarehouseApp.View.RequestGroupInfoView;
});
