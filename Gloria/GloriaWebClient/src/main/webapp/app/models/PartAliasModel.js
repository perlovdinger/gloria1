define([
        'utils/backbone/GloriaModel'
    ], function(Model) {
        
    	var PartAliasModel = Model.extend({
    		urlRoot : '/procurement/v1/partaliases'
    	});
    	return PartAliasModel;
    });