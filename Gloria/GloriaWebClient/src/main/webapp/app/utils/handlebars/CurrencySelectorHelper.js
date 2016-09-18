define([ 
         'backbone', 
         'marionette', 
         'handlebars',
         'i18next'
], function(Backbone, Marionette, Handlebars, i18n) {

	var currencies = {
		"EUR" : "Gloria.i18n.currencies.EUR",
		"SEK" : "Gloria.i18n.currencies.SEK",
		"USD" : "Gloria.i18n.currencies.USD",
		"BRL" : "Gloria.i18n.currencies.BRL",
		"JPY" : "Gloria.i18n.currencies.JPY",
		"INR" : "Gloria.i18n.currencies.INR",
		"THB" : "Gloria.i18n.currencies.THB",
		"CNY" : "Gloria.i18n.currencies.CNY"
	};
		
	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'namePrefix', 'modelName', 'readOnly', 'isDisabled', 'multiple'];
	var CurrencySelectorView = Marionette.ItemView.extend({
		tagName : 'select',
		
		className: 'input-block-level form-control',
		
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
                        this.$el.text(i18n.t(value));
                    }                    
                }, this);
            } else {
	        	var option = $('<option></option>');
	            option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
	            this.$el.append(option);
				_.each(currencies, function(value, key) {
					var option = $("<option></option>");
					option.attr("value", key).text(i18n.t(value));
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