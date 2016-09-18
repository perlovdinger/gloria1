define(['utils/backbone/GloriaCollection',
        'models/DeliveryNoteLineModel'
],function(Collection, DeliveryNoteLineModel) {

	var DeliveryNoteLineCollection = Collection.extend({
		model : DeliveryNoteLineModel,
		url : '/material/v1/orderlines'
	});

	return DeliveryNoteLineCollection;
});