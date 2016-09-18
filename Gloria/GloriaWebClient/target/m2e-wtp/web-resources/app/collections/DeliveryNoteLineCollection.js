define([ 
    'utils/backbone/GloriaPageableCollection',
    'models/DeliveryNoteLineModel' 
], function(PageableCollection, DeliveryNoteLineModel) {
    
    var DeliveryNoteLineCollection = PageableCollection.extend({
       
        model : DeliveryNoteLineModel
    });
    
    return DeliveryNoteLineCollection;
});