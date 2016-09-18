define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var OrderLineLogModel = Model.extend({
		urlRoot : '/material/v1/orderlines'
	});

	return OrderLineLogModel;
});