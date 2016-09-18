define([
    'utils/backbone/GloriaModel'
], function(Model) {
    
	var WarehouseMaterialLineModel = Model.extend({
	    
        urlRoot : '/warehouse/v1/materiallines/transferReturn'
        
	});
	return WarehouseMaterialLineModel;
});