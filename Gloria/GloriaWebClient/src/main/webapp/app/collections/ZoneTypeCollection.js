define([ 
    'utils/backbone/GloriaCollection', 
    'models/ZoneTypeModel' 
    ], function(Collection, ZoneTypeModel) {

	var ZoneTypeCollection = Collection.extend({

		model : ZoneTypeModel,

		url : '/warehouse/v1/zonetype'

	});

	return ZoneTypeCollection;
});