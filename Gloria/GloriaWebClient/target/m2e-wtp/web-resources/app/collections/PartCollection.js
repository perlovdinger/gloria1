define([ 
	'utils/backbone/GloriaPageableCollection',
	'models/PartModel' 
], function(PageableCollection, PartModel) {
	
	var PartCollection = PageableCollection.extend({
	    model : PartModel,
	    
		url : '/warehouse/v1/parts'
		
	});
	return PartCollection;
});