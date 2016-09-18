define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var MaterialModel = Model.extend({
		urlRoot : '/procurement/v1/orderlines'
	});

	return MaterialModel;
});