define(['utils/backbone/GloriaPageableCollection', 
        'views/deliverycontrol/assign/model/OrderModel',
        'utils/UserHelper'
        ], function(PageableCollection, OrderModel, UserHelper) {

	var OrderCollection = PageableCollection.extend({
		model : OrderModel,
		url : function() {
		    return '/material/v1/orders/deliverycontrol'; //OK
		}
	});

	return OrderCollection;
});