define(['utils/backbone/GloriaCollection',
		'views/deliverycontrol/myorderoverview/details/model/OrderLineLogModel'
],function(Collection, OrderLineLogModel) {

	var OrderLineLogCollection = Collection.extend({
		model : OrderLineLogModel,
		url : '/material/v1/orderlines'
	});

	return OrderLineLogCollection;
});