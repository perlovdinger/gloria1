define([ 
	'utils/backbone/GloriaPageableCollection',
	'models/RequestListModel' 
], function(PageableCollection, RequestListModel) {
	
	var RequestListCollection = PageableCollection.extend({
	   
		model : RequestListModel,
	    
		url : '/material/v1/requestlists'

	});
	
	return RequestListCollection;
});