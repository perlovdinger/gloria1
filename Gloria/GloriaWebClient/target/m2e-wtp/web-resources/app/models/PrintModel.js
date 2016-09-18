define([
    'utils/backbone/GloriaModel'
], function(Model) {
    
	var PrintModel = Model.extend({
	    
        urlRoot : '/warehouse/v1/print',
        
        defaults : {
            'copies' : undefined,
            'partNo': undefined,
            'partVersion': undefined
        }
		
	});
	return PrintModel;
});