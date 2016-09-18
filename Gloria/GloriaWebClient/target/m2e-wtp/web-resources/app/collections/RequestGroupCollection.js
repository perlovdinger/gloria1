define([ 
	'utils/backbone/GloriaPageableCollection',
	'models/RequestGroupModel',
	'utils/UserHelper'
], function(PageableCollection, RequestGroupModel, UserHelper) {
	
	var RequestGroupCollection = PageableCollection.extend({
	   
		model : RequestGroupModel,
	    
		url : function() {
		    return '/material/v1/requestgroups?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse()+ '&userId=' + UserHelper.getInstance().getUserId();
		}

	});
	
	return RequestGroupCollection;
});