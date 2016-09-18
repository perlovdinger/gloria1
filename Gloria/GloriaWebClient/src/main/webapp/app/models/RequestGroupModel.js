define([
    'utils/backbone/GloriaModel',
    'utils/UserHelper'
], function(Model, UserHelper) {
    
    var RequestGroupModel = Model.extend({
        
        urlRoot : function() {
            return '/material/v1/requestgroups?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse()+ '&userId=' + UserHelper.getInstance().getUserId();  //OK
        }
    });
    
    return RequestGroupModel;
});