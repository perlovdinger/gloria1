define([ 
    'utils/backbone/GloriaCollection', 
    'models/TransportLabelModel',
    'utils/UserHelper'
    ], function(Collection, TransportLabelModel, UserHelper) {

	var TransportLabelCollection = Collection.extend({

		model : TransportLabelModel,

		url : function() {
		    return '/material/v1/transportlabels?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
		}
          		    
	});

	return TransportLabelCollection;
});