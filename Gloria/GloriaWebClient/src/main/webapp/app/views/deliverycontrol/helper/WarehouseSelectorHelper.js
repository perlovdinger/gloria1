define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
		'collections/WarehouseCollection'
], function(Backbone, Marionette, Handlebars, i18n, WarehouseCollection) {

	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'isDisabled', 'namePrefix', 'modelName', 'isReadOnly'];
	
	var WarehouseSelectorView = Marionette.ItemView.extend({

		tagName : 'select',
		className : 'input-block-level form-control',

		initialize : function(options) {
			_.extend(this, _.pick(options, viewAttrs));
			this.warehouseListInfo = this.fetchWarehouseList();
			this.isReadOnly = options.isReadOnly;
			if (this.isReadOnly) {
			    // It should be a span instead of a select...
			    this.$el.unwrap();
			    this.setElement($('<span></span>'));
	            this.$el.addClass('controls');
			} else {
				if(this.modelName && this.id) {
					this.$el.attr('name', this.modelName + '[' + this.id + '][' + this.namePrefix + ']');
				} else {
					this.$el.attr('name', this.namePrefix);
				}
			}
			
			if(this.id) {
				this.$el.attr('id', this.idPrefix + this.id);
			} else {
				this.$el.attr('id', this.idPrefix);
			}
			
			this.render();
		},

		fetchWarehouseList : function() {		
			var warehouseCollection = new WarehouseCollection();
			var warehouseOptions;
			warehouseCollection.fetch({
				cache : false,
				async : false, // Must be false - rendered through Handlebars
				success : function(data) {
					warehouseOptions = JSON.stringify(data.toJSON());						
				}
			});		
			return warehouseOptions;
		},

		render : function() {
            this.$el.empty();
            var warehouseListInfoObj = JSON.parse(this.warehouseListInfo);
		    if (this.isReadOnly) {
                _.each(warehouseListInfoObj, function(item, index) {
                    if (this.selected && this.selected == item.siteId) {
                        this.$el.append(item.siteName + ' (' + item.siteId + ')');
                        return false;
                    }
                }, this);
            } else {
    			var option = $('<option></option>');
    			option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
    			this.$el.append(option);
    			_.each(warehouseListInfoObj, function(item, index) {
    				var option = $("<option></option>");
    				option.attr("value", item.siteId).text(i18n.t(item.siteName + ' (' + item.siteId + ')'));
    				if (this.selected && this.selected == item.siteId) {
    					option.attr("selected", "selected");
    				}
    				this.$el.append(option);
    			}, this);
    			if(this.isDisabled) {
    				this.$el.attr('disabled', true);
    			}
		    }
			return this;
		}
	});

    var warehouseSelector = function(options) {
        var csv = new WarehouseSelectorView(options.hash);
        var returnString = new Handlebars.SafeString(csv.el.outerHTML);
        csv.destroy();
        csv = null;
        return returnString;
    };

	return warehouseSelector;
});