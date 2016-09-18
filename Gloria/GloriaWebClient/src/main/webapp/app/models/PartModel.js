define([
    'utils/backbone/GloriaModel'
], function(Model) {
    
	var PartModel = Model.extend({
	    
        urlRoot : '/warehouse/v1/parts',
        
        defaults : {
            'partNo' : undefined
        }
        
	});
	return PartModel;
});