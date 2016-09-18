define(['utils/backbone/GloriaPageableCollection', 
        'models/ChangeIdModel'
        ], function(PageableCollection, ChangeIdModel) {

    var ChangeIdCollection = PageableCollection.extend({
        model : ChangeIdModel,
        url : '/procurement/v1/changeids'
    });

    return ChangeIdCollection;
});