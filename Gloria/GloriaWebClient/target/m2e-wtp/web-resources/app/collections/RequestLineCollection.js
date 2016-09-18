define(['utils/backbone/GloriaPageableCollection', 
        'models/RequestLineModel'
        ], function(PageableCollection, RequestLineModel) {

    var RequestLineCollection = PageableCollection.extend({
        model : RequestLineModel        
    });

    return RequestLineCollection;
});