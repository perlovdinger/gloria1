define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var OrderLogModel = Model.extend({
		urlRoot : '/material/v1/orderlines'
	});

	return OrderLogModel;
});