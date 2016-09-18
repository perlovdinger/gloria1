define([ 'utils/backbone/GloriaModel' 
         ], function(Model) {

	var WarehouseModel = Model.extend({
		urlRoot : '/warehouse/v1/warehouses'
	});

	return WarehouseModel;
});