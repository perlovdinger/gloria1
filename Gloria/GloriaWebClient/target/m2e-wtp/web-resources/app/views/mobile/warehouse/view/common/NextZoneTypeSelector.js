define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
		'bootstrap',
		'i18next',
		'utils/backbone/GloriaCollection',
		'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Marionette, Bootstrap, i18n, Collection, UserHelper) {

	var NextZoneTypeSelector = Marionette.View.extend({
	
		initialize : function(options) {
			this.element = options.element;
			this.zoneType = options.zoneType;
			this.nextZoneCode = options.nextZoneCode;
			this.disabled = options.disabled;
			this.constructNextZoneTypeList();
		},
		
		constructNextZoneTypeList : function() {
			var that = this;
			var collection = new Collection();
			collection.url = '/warehouse/v1/zones?type=' + that.zoneType + '&whSiteId=' + 
			    UserHelper.getInstance().getDefaultWarehouse(); //OK
			collection.fetch({
				async : false,
				success : function(data) {
					that.render(data.toJSON());
				}
			});
		},
		
		render : function(jsonData) {
			$(this.element).empty();
			var that = this;
			var firstAvailableZone;
			var isZoneCodeMatched = false;
			var option = $('<option></option>');
			$(this.element).append(option);
			_.each(jsonData, function(item, index) {
				var option = $("<option></option>");
				option.attr("value", item.code).text(item.code);
				if (index == 0) {
					firstAvailableZone = item.code;
				};
				if (that.nextZoneCode == item.code) {
					isZoneCodeMatched = true;
				};
				$(this.element).append(option);
			}, this);

			if (isZoneCodeMatched) {
				$(this.element).val(that.nextZoneCode);
			} else {
				$(this.element).val(firstAvailableZone);
			};

			if (that.disabled) {
				$(this.element).attr("disabled", that.disabled);
			};
			return this;
		}
	});
	
	return NextZoneTypeSelector;
});
