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

	var AisleSelector = Marionette.LayoutView.extend({
	
		initialize : function(options) {
			this.element = options.element;
			// If url is passed then this view can fetch the information and render the view
			// in this case input args [warehouseId, storageRoomId...] are not required.
			this.url = options.url;
			this.warehouseModel = options.warehouseModel;
			this.warehouseId = options.warehouseId;
			this.storageRoomId = options.storageRoomId;
			this.zoneId = options.zoneId;
			this.suggestedAisle = options.suggestedAisle;
			this.constructAisleList();
		},

		constructAisleList : function() {
            var that = this;          
            var aisleCollection = new Collection();
            if ((this.storageRoomId && this.zoneId) || this.url) {
	            aisleCollection.url = this.url || '/warehouse/v1/warehouses/' + this.warehouseId 
	                                + '/storagerooms/' + this.storageRoomId
	                                + '/zones/' + this.zoneId +'/aislerackrows';
	            aisleCollection.fetch({
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
            var context = {context: this.warehouseModel && this.warehouseModel.get('setUp').toUpperCase()};
			$(this.element).empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.warehouse.selectAisle', context));
			$(this.element).append(option);
			_.each(jsonData, function(item, index) {
				var option = $("<option></option>");
				option.attr("value", item.id).text(item.code);
				if (this.selected && this.selected == item.id) {
					option.attr("selected", "selected");
				}
				$(this.element).append(option);
			}, this);
			return this;
		}
	});

	return AisleSelector;
});
