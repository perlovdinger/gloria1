define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
		'collections/UnitsOfMeasureCollection'
], function(Backbone, Marionette, Handlebars, i18n, UnitsOfMeasureCollection) {

	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'namePrefix', 'modelName', 'multiple'];
	
	var UnitsOfMeasureSelectorView = Marionette.ItemView.extend({

		tagName : 'select',

		className : 'input-block-level form-control',

		initialize : function(options) {
		   options || (options = {});
			_.extend(this, _.pick(options, viewAttrs));
			this.$el.attr('id', this.idPrefix + (this.id ? this.id : ''));
			this.$el.attr('name', this.multiple ? this.modelName + '[' + (this.id ? (this.id + '][') : '') + this.namePrefix + ']'
					: this.modelName + '[' + this.namePrefix + ']');
			this.render(constructUnitsOfMeasureList());
		},

		render : function(jsonData) {
			this.$el.empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
			this.$el.append(option);
			_.each(jsonData, function(item, index) {
				if(item && item.code) {
					var option = $("<option></option>");
					option.attr("value", item.code).text(item.code);
					if (this.selected && this.selected == item.code) {
						option.attr("selected", "selected");
					}
					this.$el.append(option);
				}
			}, this);
			return this;
		}
	});

    var constructUnitsOfMeasureList = function() {
        var unitsOfMeasureListOptions = sessionStorage.getItem('UnitsOfMeasureListInfo');
        if (unitsOfMeasureListOptions) {
            unitsOfMeasureListOptions = JSON.parse(unitsOfMeasureListOptions);
        } else {
            var unitsOfMeasureCollection = new UnitsOfMeasureCollection();
            unitsOfMeasureCollection.fetch({
                async : false,
                success : function(data) {
                    sessionStorage.setItem('UnitsOfMeasureListInfo', JSON.stringify(data.toJSON()));
                    unitsOfMeasureListOptions = data.toJSON();
                }
            });
        }
        
        return unitsOfMeasureListOptions;
    };


    var unitsOfMeasureSelector = function(options) {
        var csv = new UnitsOfMeasureSelectorView(options.hash);
        var returnString = new Handlebars.SafeString(csv.el.outerHTML);
        csv.destroy();
        csv = null;
        return returnString;
    };

	return {
	    'handlebarsHelper' : unitsOfMeasureSelector,
	    'constructUnitsOfMeasureList' : constructUnitsOfMeasureList
	};
});