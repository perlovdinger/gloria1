define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
		'collections/ZoneTypeCollection'
], function(Backbone, Marionette, Handlebars, i18n, ZoneTypeCollection) {

	var viewAttrs = ['id', 'className', 'selected', 'name', 'multiple'];
	
	var ZoneTypeSelector = Marionette.ItemView.extend({

		tagName : 'select',

		className : 'input-block-level form-control',

		initialize : function(options) {
		    options || (options = {});
			_.extend(this, _.pick(options, viewAttrs));
			this.$el.attr('id', (this.id ? this.id : ''));
			this.$el.attr('name', (this.name ? this.name : ''));
			this.constructZoneTypeList();
		},

		constructZoneTypeList : function() {
			var that = this;
			var zoneTypeListInfoOptions;		
			var zoneTypeCollection = new ZoneTypeCollection();
			zoneTypeCollection.fetch({
				async : false,
				success : function(data) {					
					zoneTypeListInfoOptions = data.toJSON();
					that.render(zoneTypeListInfoOptions);
				}
			});		
			
			return zoneTypeListInfoOptions;
		},

		render : function(jsonData) {
			this.$el.empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
			this.$el.append(option);
			_.each(jsonData, function(item, index) {
				var option = $("<option></option>");
				option.attr("value", item.code).text(item.code);
				if (this.selected && this.selected == item.code) {
					option.attr("selected", "selected");
				}
				this.$el.append(option);
			}, this);
			return this;
		}
	});

	return ZoneTypeSelector;
});