define([ 
    	'utils/backbone/GloriaCollection',
    	'models/PartAliasModel' 
    ], function(Collection, PartAliasModel) {
    	
    	var PartAliasCollection = Collection.extend({
    	    model : PartAliasModel,
    	    
    		url : '/procurement/v1/partaliases'
    		
    	});
    	return PartAliasCollection;
    });