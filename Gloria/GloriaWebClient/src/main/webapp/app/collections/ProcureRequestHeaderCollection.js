define(['utils/backbone/GloriaPageableCollection', 
        'models/ProcureRequestHeaderModel',
        'utils/UserHelper'
        ], function(PageableCollection, ProcureRequestHeaderModel, UserHelper) {

	var ProcureRequestHeaderCollection = PageableCollection.extend({

		url : function() {
			return '/procurement/v1/materialheaders/current?userId=' + UserHelper.getInstance().getUserId();
		},

		model : ProcureRequestHeaderModel
	});

	return ProcureRequestHeaderCollection;
});