define(['app',
        'backbone',
        'marionette',
        'handlebars',
		'hbs!views/warehouse/pick/view/requestgroup-materiallines-info' 
], function(Gloria, Backbone, Marionette, Handlebars, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.RequestGroupMaterialLinesInfoView = Marionette.LayoutView.extend({
			
			 
			 initialize : function(options) {
				 this.model = options.model;
			 },
			 
			 render : function() {		
			 	this.$el.html(compiledTemplate({			 		
			 		'requestGroupModel' : this.model.toJSON(),
	 		    }));		 	
			 
			    return this;
			},
			
			onShow: function() {
				Gloria.WarehouseApp.trigger('dispatchNote:showPartInformation', this.model);
			}
			
		});
		
	});

	return Gloria.WarehouseApp.View.RequestGroupMaterialLinesInfoView;
});
