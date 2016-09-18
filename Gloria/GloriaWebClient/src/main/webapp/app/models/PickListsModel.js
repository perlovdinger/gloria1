define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var PickListsModel = Model.extend({
		urlRoot : '/procurement/v1/picklists'
	});

	return PickListsModel;
});