define(['utils/backbone/GloriaCollection',
        'models/StorageRoomModel'
], function(Collection, StorageRoomModel) {

	var StorageRoomCollection = Collection.extend({
	    model : StorageRoomModel
	});
	
	return StorageRoomCollection;
});