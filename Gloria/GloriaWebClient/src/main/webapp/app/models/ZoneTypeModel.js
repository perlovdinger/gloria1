define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var ZoneTypeModel = Model.extend({

		urlRoot : '/warehouse/v1/zonetype'

	});

	return ZoneTypeModel;
});