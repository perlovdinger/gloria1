define([ 'utils/backbone/GloriaPageableCollection',
		'views/deliverycontrol/models/OrderLineModel'
], function(PageableCollection, OrderLineModel) {

	var OrderLineCollection = PageableCollection.extend({
		model : OrderLineModel,
		url : function() {
			return '/procurement/v1/orderlines/current';
		}
	});
	
	return OrderLineCollection;
});