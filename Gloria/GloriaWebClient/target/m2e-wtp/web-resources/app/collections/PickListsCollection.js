define(['utils/backbone/GloriaPageableCollection', 
        'models/PickListsModel'
        ], function(PageableCollection, PickListsModel) {

	var PickListsCollection = PageableCollection.extend({
		model : PickListsModel,
		url : '/procurement/v1/picklists'
	});

	return PickListsCollection;
});