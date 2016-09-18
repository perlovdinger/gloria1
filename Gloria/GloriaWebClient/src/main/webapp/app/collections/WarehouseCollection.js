define([ 
    'utils/backbone/GloriaCollection', 
    'models/WarehouseModel' 
    ], function(Collection, WarehouseModel) {

	var WarehouseCollection = Collection.extend({
		model : WarehouseModel,
		url : '/warehouse/v1/warehouses'
	});

	return WarehouseCollection;
});