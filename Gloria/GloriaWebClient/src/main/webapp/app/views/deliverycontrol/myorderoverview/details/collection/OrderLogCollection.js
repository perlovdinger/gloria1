define(['utils/backbone/GloriaCollection',
		'views/deliverycontrol/myorderoverview/details/model/OrderLogModel'
],function(Collection, OrderLogModel) {

	var OrderLogCollection = Collection.extend({
		model : OrderLogModel,
		url : '/material/v1/orderlines'
	});

	return OrderLogCollection;
});