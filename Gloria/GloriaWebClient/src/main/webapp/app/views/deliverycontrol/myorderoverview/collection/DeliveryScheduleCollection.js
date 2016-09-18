define(['utils/backbone/GloriaCollection',
        'views/deliverycontrol/myorderoverview/model/DeliveryScheduleModel',
        'utils/UserHelper'
], function(Collection, DeliveryScheduleModel, UserHelper) {

	var DeliveryScheduleCollection = Collection.extend({
		url : function() {
		    return '/procurement/v1/materiallines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
		},
		model : DeliveryScheduleModel,
		getTotalNumber: function(attr) {
            return this.reduce(function(memo, value) { 
                return memo + (Number(value.get(attr)) || 0);
            }, 0);
        }
	});

	return DeliveryScheduleCollection;
});
