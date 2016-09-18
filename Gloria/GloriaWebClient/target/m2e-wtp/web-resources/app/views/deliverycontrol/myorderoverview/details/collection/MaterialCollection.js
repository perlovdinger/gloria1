define(['utils/backbone/GloriaCollection',
		'views/deliverycontrol/myorderoverview/details/model/MaterialModel'
],function(Collection, MaterialModel) {

	var MaterialCollection = Collection.extend({
		model : MaterialModel,
		url : '/procrement/v1/orderlines'
	});

	return MaterialCollection;
});