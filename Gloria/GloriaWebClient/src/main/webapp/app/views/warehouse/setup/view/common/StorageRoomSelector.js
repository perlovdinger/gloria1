define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'bootstrap',
		'i18next',
		'collections/StorageRoomCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, StorageRoomCollection) {

	var StorageRoomSelector = Marionette.LayoutView.extend({
	
		initialize : function(options) {
			this.element = options.element;
			this.warehouseId = options.warehouseId;
			this.suggestedStorageRoom = options.suggestedStorageRoom;
			this.constructStorageRoomList();
		},
		
		constructStorageRoomList : function() {
            var that = this;
            var storageRoomCollection = new StorageRoomCollection();
            storageRoomCollection.url = '/warehouse/v1/warehouses/' + this.warehouseId + '/storagerooms';
            storageRoomCollection.fetch({
                async : false,
                success : function(data) {
                    that.render(data.toJSON());
                }
            });
        },
		
		render : function(jsonData) {
			$(this.element).empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.warehouse.selectStorageRoom'));
			$(this.element).append(option);
			_.each(jsonData, function(item, index) {
				var option = $("<option></option>");
				option.attr("value", item.id).text(item.code);
				if (this.suggestedStorageRoom && this.suggestedStorageRoom == item.id) {
					option.attr("selected", "selected");
				}
				$(this.element).append(option);
			}, this);
			return this;
		}
	});

	return StorageRoomSelector;
});
