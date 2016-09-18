define(['utils/backbone/GloriaPageableCollection',
        'models/MaterialLineInventoryModel',
        'utils/UserHelper'
         ], function(GloriaPageableCollection, MaterialLineInventoryModel, UserHelper) {

    var MaterialLineInventoryCollection = GloriaPageableCollection.extend({
        url : function() {
            return '/procurement/v1/materiallines/inventory?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
        },
        
        model: MaterialLineInventoryModel
    });

    return MaterialLineInventoryCollection;
});