define(['utils/backbone/GloriaPageableCollection', 
        'models/MaterialRequestLineModel' 
        ], function(PageableCollection, MaterialRequestLineModel) {

    var MaterialRequestLineCollection = PageableCollection.extend({
        model : MaterialRequestLineModel
    });

    return MaterialRequestLineCollection;
});