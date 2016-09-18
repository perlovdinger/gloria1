define([ 
         'utils/backbone/GloriaModel',
         'utils/UserHelper'
         ], function(Model, UserHelper) {

	var TransportLabelModel = Model.extend({

		urlRoot : function() {
		    return '/material/v1/transportlabels?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse() //OK
		}

	});

	return TransportLabelModel;
});