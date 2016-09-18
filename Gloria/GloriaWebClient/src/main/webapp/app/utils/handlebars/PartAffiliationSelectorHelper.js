define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
		'collections/PartAffiliationCollection'
], function(Backbone, Marionette, Handlebars, i18n, PartAffiliationCollection) {

	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'namePrefix', 'modelName', 'multiple'];
	
	var PartAffiliationSelectorView = Marionette.ItemView.extend({

		tagName : 'select',

		className : 'input-block-level form-control',

		initialize : function(options) {
		    options || (options = {});
			_.extend(this, _.pick(options, viewAttrs));
			this.$el.attr('id', this.idPrefix + (this.id ? this.id : ''));
			this.$el.attr('name', this.multiple ? this.modelName + '[' + (this.id ? (this.id + '][') : '') + this.namePrefix + ']'
					: this.modelName + '[' + this.namePrefix + ']');
			this.render(constructPartAffiliationList());
		},

		render : function(jsonData) {
			this.$el.empty();
			_.each(jsonData, function(item, index) {
				var option = $("<option></option>");				
				option.attr("value", item.code).text(item.code);
				if(!this.selected) this.selected = 'V'; // Default to V Bug#GLO-2988
				if (this.selected == item.code) {
					option.attr("selected", "selected");
				}
				this.$el.append(option);
			}, this);
			return this;
		}
	});

    var constructPartAffiliationList = function() {
        var partAffiliationListInfoOptions = sessionStorage.getItem('PartAffiliationListInfo');
        if (partAffiliationListInfoOptions) {
            partAffiliationListInfoOptions = JSON.parse(partAffiliationListInfoOptions);
        } else {
            var partAffiliationCollection = new PartAffiliationCollection();
            partAffiliationCollection.fetch({
                async : false,
                success : function(data) {
                    sessionStorage.setItem('PartAffiliationListInfo', JSON.stringify(data.toJSON()));
                    partAffiliationListInfoOptions = data.toJSON();
                }
            });
        }
        
        return partAffiliationListInfoOptions;
    };

    var partAffiliationSelector = function(options) {
        var csv = new PartAffiliationSelectorView(options.hash);
        var returnString = new Handlebars.SafeString(csv.el.outerHTML);
        csv.destroy();
        csv = null;
        return returnString;
    };
    
	return {
	    'handlebarsHelper' : partAffiliationSelector,
	    'constructPartAffiliationList' : constructPartAffiliationList
	};
});