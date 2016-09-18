define(['utils/backbone/GloriaPageableCollection', 
        'models/DispatchNoteModel'
        ], function(PageableCollection, DispatchNoteModel) {

	var DispatchNoteCollection = PageableCollection.extend({
		model : DispatchNoteModel,
		url : '/material/v1/dispatchnotes'
	});

	return DispatchNoteCollection;
});