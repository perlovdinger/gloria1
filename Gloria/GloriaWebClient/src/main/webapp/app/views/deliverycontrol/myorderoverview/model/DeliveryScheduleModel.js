define([ 
         'utils/backbone/GloriaModel',
         'utils/UserHelper'
         ], function(Model, UserHelper) {

	var DeliveryScheduleModel = Model.extend({
		urlRoot : function() {
		    return '/procurement/v1/materiallines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
		}
	});

	return DeliveryScheduleModel;
});