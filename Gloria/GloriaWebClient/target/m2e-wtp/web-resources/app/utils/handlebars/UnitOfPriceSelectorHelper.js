define([ 
         'backbone', 
         'marionette', 
         'handlebars',
         'i18next'
], function(Backbone, Marionette, Handlebars, i18n) {

	var currencies = {
		"1" : "Gloria.i18n.unitOfPrice.per",
		"100" : "Gloria.i18n.unitOfPrice.per",
		"1000" : "Gloria.i18n.unitOfPrice.per"		
	};
		
	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'namePrefix', 'modelName', 'readOnly', 'isDisabled', 'multiple'];
	var CurrencySelectorView = Marionette.ItemView.extend({
		tagName : 'select',
		
		className: 'input-block-level form-control js-number',
		
		initialize : function(options) {
			_.extend(this, _.pick(options, viewAttrs));	
			if(this.readOnly) this.setElement(Backbone.$('<span/>'), false);
			this.$el.attr('id', this.idPrefix + (this.id ? this.id : ''));
			this.$el.attr('name', this.multiple ? this.modelName + '[' + (this.id ? (this.id + '][') : '')
                    + this.namePrefix + ']' : this.modelName + '[' + this.namePrefix + ']');
			this.render();
		},
		
		render : function() {
		    if (this.readOnly) {                
                _.each(currencies, function(value, key) {                   
                    if (this.selected && this.selected == key) {
                        this.$el.text(i18n.t(value, {amount: key}));
                    }                    
                }, this);
            } else {
	        	var option = $('<option></option>');
	          	_.each(currencies, function(value, key) {
					var option = $("<option></option>");
					option.attr("value", key).text(i18n.t(value, {amount: key}));
					if(this.selected && this.selected == key) {
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

    var currencySelector = function(options) {
        var csv = new CurrencySelectorView(options.hash);
        var returnString = new Handlebars.SafeString(csv.el.outerHTML);
        csv.destroy();
        csv = null;
        return returnString;        
    };
    
	return currencySelector;
});