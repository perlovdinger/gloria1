define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'bootstrap',
		'i18next',
		'utils/backbone/GloriaModel'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Model) {

	var LevelSelector = Marionette.LayoutView.extend({
	
		initialize : function(options) {
			this.element = options.element;
			// If url is passed then this view can fetch the information and render the view
			// in this case input args [warehouseId, storageRoomId...] are not required.
			this.url = options.url;
			this.warehouseId = options.warehouseId;
			this.storageRoomId = options.storageRoomId;
			this.zoneId = options.zoneId;
			this.aisleId = options.aisleId;
			this.bayId = options.bayId;
			this.constructLevelList();
		},
		
		constructLevelList : function() {
			var that = this;
			if ((this.storageRoomId && this.zoneId && this.aisleId && this.bayId) || this.url) {
				var levelModel = new Model();
				levelModel.url = this.url || '/warehouse/v1/warehouses/' + this.warehouseId + '/storagerooms/' + this.storageRoomId
									+ '/zones/' + this.zoneId +'/aislerackrows/' + this.aisleId +'/baysettings/' + this.bayId;
				levelModel.fetch({
	                async : false,
	                success : function(data) {
	                    that.render(data.get('numberOfLevels'));
	                }
	            });
			} else {
				that.render();
			}
        },
		
        render : function(numberOfLevels) {
        	$(this.element).empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.warehouse.selectLevel'));
			$(this.element).append(option);
			for(var i = 1; i <= numberOfLevels; i++) {
				var option = $('<option></option>');
				option.attr('value', i).text(i);
				$(this.element).append(option);
			};
			return this;
		}
	});

	return LevelSelector;
});
