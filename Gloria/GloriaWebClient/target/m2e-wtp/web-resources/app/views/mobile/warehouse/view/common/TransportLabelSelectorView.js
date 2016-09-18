define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
		'collections/TransportLabelCollection'
], function(Backbone, Marionette, Handlebars, i18n, TransportLabelCollection) {

	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'namePrefix', 'modelName', 'multiple', 'readOnly'];
	
	var TransportLabelSelectorView = Marionette.ItemView.extend({

		tagName : 'select',

		className : 'input-block-level form-control scannable',

		initialize : function(options) {
			_.extend(this, _.pick(options, viewAttrs));
			if(this.readOnly) this.setElement(Backbone.$('<span/>'), false);
			this.$el.attr('id', this.idPrefix + (this.id ? this.id : ''));
			this.$el.attr('name', this.namePrefix );
			this.constructTransportLabelList();
		},

		constructTransportLabelList : function() {
			var that = this;
			var transportLabelCollection = new TransportLabelCollection();
			transportLabelCollection.fetch({
				async : false,
				success : function(data) {					
					that.render(data.toJSON());
				}
			});
			
		},

		render : function(jsonData) {
			this.$el.empty();
			if (this.readOnly) {                
                _.each(jsonData, function(item, index) {                   
                    if (this.selected && this.selected == item.id) {
                        this.$el.text(item.code);
                    }                    
                }, this);
            } else {
                var option = $('<option></option>');
                option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
                this.$el.append(option);
                _.each(jsonData, function(item, index) {
                    var option = $("<option></option>");
                    option.attr("value", item.id).text(item.code);
                    if (this.selected && this.selected == item.id) {
                        option.attr("selected", "selected");
                    }
                    this.$el.append(option);
                }, this);
            }
			return this;
		}
	});

	return TransportLabelSelectorView;
});