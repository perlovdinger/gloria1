define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var OrderModel = Model.extend({
		urlRoot : '/material/v1/orders'
	});

	return OrderModel;
});