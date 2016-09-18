define(['utils/backbone/GloriaPageableCollection', 
        'models/ProcureRequestLineModel' 
        ], function(PageableCollection, ProcureRequestLineModel) {

	var ProcureRequestLineCollection = PageableCollection.extend({		

		model : ProcureRequestLineModel,
		
		url: '/procurement/v1/materials'
	});

	return ProcureRequestLineCollection;
});