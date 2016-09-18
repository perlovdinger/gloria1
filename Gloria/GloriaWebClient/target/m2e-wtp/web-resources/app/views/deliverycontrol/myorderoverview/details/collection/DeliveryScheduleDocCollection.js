define(['utils/backbone/GloriaCollection',
        'views/deliverycontrol/myorderoverview/details/model/DeliveryScheduleDocModel'
], function(Collection, DeliveryScheduleDocModel) {

	var DeliveryScheduleDocCollection = Collection.extend({
		url : '/documents/v1/attacheddocs',
		model : DeliveryScheduleDocModel
	});

	return DeliveryScheduleDocCollection;
});
