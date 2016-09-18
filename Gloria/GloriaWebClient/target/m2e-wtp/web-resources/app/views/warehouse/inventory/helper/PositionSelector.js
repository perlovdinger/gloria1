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

	var PositionSelector = Marionette.LayoutView.extend({
	
		initialize : function(options) {
			this.element = options.element;
			this.warehouseId = options.warehouseId;
			this.storageRoomId = options.storageRoomId;
			this.zoneId = options.zoneId;
			this.aisleId = options.aisleId;
			this.bayId = options.bayId;
			this.constructPositionList();
		},

		constructPositionList : function() {
			var that = this;
			if (this.storageRoomId && this.zoneId && this.aisleId && this.bayId) {
				var positionModel = new Model();
				positionModel.url = '/warehouse/v1/warehouses/' + this.warehouseId + '/storagerooms/' + this.storageRoomId
									+ '/zones/' + this.zoneId +'/aislerackrows/' + this.aisleId +'/baysettings/' + this.bayId;
				positionModel.fetch({
	                async : false,
	                success : function(data) {
	                	that.render(data.get('numberOfPositions'));
	                }
	            });
			} else {
				that.render();
			}            
        },
		
        render : function(numberOfPositions) {
        	$(this.element).empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.warehouse.selectPosition'));
			$(this.element).append(option);
			for(var i = 1; i <= numberOfPositions; i++) {
				var option = $('<option></option>');
				option.attr('value', i).text(i);
				$(this.element).append(option);
			};
			return this;
		}
	});

	return PositionSelector;
});
