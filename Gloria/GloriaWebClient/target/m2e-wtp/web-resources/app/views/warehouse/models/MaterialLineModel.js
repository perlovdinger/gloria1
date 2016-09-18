define([ 
         'utils/backbone/GloriaModel',
         'utils/UserHelper'
         ], function(Model, UserHelper) {

	var MaterialLineModel = Model.extend({
		urlRoot : function() {
		    return '/warehouse/v1/materiallines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
		}
	});

	return MaterialLineModel;
});