define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
		'collections/TransportLabelCollection'
], function(Backbone, Marionette, Handlebars, i18n, TransportLabelCollection) {

	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'namePrefix', 'modelName', 'multiple'];
	
	var TransportLabelSelectorView = Marionette.ItemView.extend({

		tagName : 'select',

		className : 'input-block-level form-control',

		initialize : function(options) {
		    options || (options = {});
			_.extend(this, _.pick(options, viewAttrs));
			this.$el.attr('id', this.idPrefix + (this.id ? this.id : ''));
			this.$el.attr('name', this.multiple ? this.modelName + '[' + (this.id ? (this.id + '][') : '') + this.namePrefix + ']'
					: this.modelName + '[' + this.namePrefix + ']');
			this.render(constructTransportLabelList());
		},

		render : function(jsonData) {
			this.$el.empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
			this.$el.append(option);
			_.each(jsonData, function(item, index) {
				var option = $("<option></option>");
				option.attr("value", item.id).text(item.code);
				if (this.selected && this.selected == item.code) {
					option.attr("selected", "selected");
				}
				this.$el.append(option);
			}, this);
			return this;
		}
	});

    var constructTransportLabelList = function() {
    	var  transportLabelListInfoOptions = '';
            var transportLabelCollection = new TransportLabelCollection();
            transportLabelCollection.fetch({
                async : false,
                success : function(data) {                   
                    transportLabelListInfoOptions = data.toJSON();
                }
            });
        
        return transportLabelListInfoOptions;
    };

    var transportLabelSelector = function(options) {
        var csv = new TransportLabelSelectorView(options.hash);
        var returnString = new Handlebars.SafeString(csv.el.outerHTML);
        csv.destroy();
        csv = null;
        return returnString;
    };
    
	return {
	    'handlebarsHelper' : transportLabelSelector,
	    'constructTransportLabelList' : constructTransportLabelList
	};
});