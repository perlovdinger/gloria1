define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
		'bootstrap',
		'i18next',
		'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Handlebars, Marionette, Bootstrap, i18n, Collection) {

    var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'isDisabled', 'namePrefix', 'modelName', 'isReadOnly'];

	var QualityDocumentSelector = Marionette.ItemView.extend({
	
        tagName : 'select',
        className : 'input-block-level form-control',

		initialize : function(options) {
            _.extend(this, _.pick(options, viewAttrs));
			this.selected = options.selected;
			this.isReadOnly = options.isReadOnly;
            if (this.isReadOnly) {
                // It should be a span instead of a select...
                this.$el.unwrap();
                this.setElement($('<span></span>'));
                this.$el.addClass('controls');
            } else {
	            this.$el.attr('name', this.modelName + (this.id ? ('[' + this.id + ']') : '') + '[' + this.namePrefix + ']');
			}
			this.$el.attr('id', this.idPrefix + (this.id ? this.id : ''));
            this.constructQualityDocumentList();
		},
		
		constructQualityDocumentList : function() {
	          // Store in LocalStorage for later use!
            var qualityDocumentOptions = sessionStorage.getItem('QualityDocumentListInfo');
            if (!qualityDocumentOptions) {
                var collection = new Collection();
                collection.url = '/common/v1/qualitydocuments';
                collection.fetch({
                    cache : false,
                    async : false, // Must be false - rendered through Handlebars
                    success : function(data) {
                    	data.comparator = function(model) {
                            return model.get('name');
                        };
                        qualityDocumentOptions = JSON.stringify(data.sort().toJSON());
                        sessionStorage.setItem('QualityDocumentListInfo', qualityDocumentOptions);
                    }
                });
            }
            this.render(JSON.parse(qualityDocumentOptions));
        },
		
		render : function(jsonData) {
			$(this.element).empty();
			if (this.isReadOnly) {
                _.each(jsonData, function(item, index) {
                    if (this.selected && this.selected == item.id) {
                        this.$el.append(i18n.t('Gloria.i18n.qualityDocumentType.' + item.code));
                        return false;
                    }
                }, this);
			} else {
    			var option = $('<option></option>');	
    			option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
    			this.$el.append(option);
    			_.each(jsonData, function(item, index) {
    				var option = $('<option></option>');
    				option.attr('value', item.id).text(i18n.t('Gloria.i18n.qualityDocumentType.' + item.code));
    				if (this.selected && this.selected == item.id) {
    					option.attr('selected', 'selected');
    				}
    				this.$el.append(option);
    			}, this);
			}
			return this;
		}
	});

    var qualityDocumentSelector = function(options) {
        var csv = new QualityDocumentSelector(options.hash);
        var returnString = new Handlebars.SafeString(csv.el.outerHTML);
        csv.destroy();
        csv = null;
        return returnString;
    };

	return qualityDocumentSelector;
});
