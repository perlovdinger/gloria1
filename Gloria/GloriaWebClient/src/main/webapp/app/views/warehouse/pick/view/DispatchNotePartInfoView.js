define([ 'app',
         'jquery',
         'i18next',
         'handlebars',
         'backbone',
         'marionette',
         'bootstrap',         
         'views/warehouse/pick/view/RequestGroupMaterialLinesInfoView',      
         'hbs!views/warehouse/pick/view/dispatchNote-part-info'
], function(Gloria, $, i18n, Handlebars, Backbone, Marionette, BootStrap, RequestGroupMaterialLinesInfoView,  compiledTemplate) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.DispatchNotePartInfoView = Marionette.CollectionView.extend({
			
			id: "requestGroupMaterialLineList",
			
			childView: RequestGroupMaterialLinesInfoView,		
			
			initialize : function(options) {
				this.collection = options.collection;
			}
			
		});
	});
    
    return Gloria.WarehouseApp.View.DispatchNotePartInfoView;
});
