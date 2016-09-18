define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'bootstrap',
		'i18next',
		'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Collection) {

	var BaySelector = Marionette.LayoutView.extend({
	
		initialize : function(options) {
			this.element = options.element;
			// If url is passed then this view can fetch the information and render the view
			// in this case input args [warehouseId, storageRoomId...] are not required.
			this.url = options.url;
			this.warehouseId = options.warehouseId;
			this.storageRoomId = options.storageRoomId;
			this.zoneId = options.zoneId;
			this.aisleId = options.aisleId;
			this.constructBayList();
		},

		constructBayList : function() {
			var that = this;
            var bayCollection = new Collection();
            if ((this.storageRoomId && this.zoneId && this.aisleId) || this.url) {
            	bayCollection.url = this.url || '/warehouse/v1/warehouses/' + this.warehouseId + '/storagerooms/' + this.storageRoomId
					+ '/zones/' + this.zoneId +'/aislerackrows/' + this.aisleId +'/baysettings';
				bayCollection.fetch({
					async : false,
					success : function(data) {
					    that.render(data.toJSON());
					}
				});
            } else {
            	that.render();
            }            
        },
		
        render : function(jsonData) {
        	$(this.element).empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.warehouse.selectBay'));
			$(this.element).append(option);
			_.each(jsonData, function(item, index) {
				var option = $('<option></option>');
				option.attr('value', item.id).text(item.bayCode);
				$(this.element).append(option);
			}, this);
			return this;
		}
	});

	return BaySelector;
});
