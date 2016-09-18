define([
    'utils/backbone/GloriaModel'
], function(Model) {
    
    var RequestListModel = Model.extend({
        
        urlRoot : '/material/v1/requestlists'
    });
    
    return RequestListModel;
});