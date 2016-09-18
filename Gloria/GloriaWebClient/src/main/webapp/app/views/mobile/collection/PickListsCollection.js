define(['utils/backbone/GloriaCollection', 
        'models/PickListsModel'
        ], function(Collection, PickListsModel) {

	var PickListsCollection = Collection.extend({
		model : PickListsModel,
		url : '/procurement/v1/picklists'
	});

	return PickListsCollection;
});