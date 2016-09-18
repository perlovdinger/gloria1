define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'collections/SupplierCounterPartCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, SupplierCounterPartCollection) {

	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'namePrefix', 'modelName', 'isDisabled', 'companyCode', 'multiple'];
	
    var SupplierCounterPartSelectorView = Marionette.View.extend({

		tagName : 'select',

		className : 'input-block-level form-control',
    
        initialize : function(options) {
        	_.extend(this, _.pick(options, viewAttrs));
			this.$el.attr('id', this.idPrefix + (this.id ? this.id : ''));
            this.deliveryFollowUpTeamId = options.deliveryFollowUpTeamId;
            this.suggestedOption = options.suggestedOption;
            this.$el.attr('name', this.multiple ? this.modelName + '[' + (this.id ? (this.id + '][') : '')
                    + this.namePrefix + ']' : this.modelName + '[' + this.namePrefix + ']');
            this.renderSupplierCounterPartList();
        },

        renderSupplierCounterPartList : function() {             
            var ccpCollection = this.companyCode ? constructSupplierCounterPartSelectorByCompanyCode(this.companyCode) : constructSupplierCounterPartSelector(this.deliveryFollowUpTeamId);                
            this.render(ccpCollection);            
        },
        
        render : function(supplierCounterPartInfoObj) {
			this.$el.empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
			this.$el.append(option);
			_.each(supplierCounterPartInfoObj, function(item, index) {
				option = $('<option></option>');
				option.attr('value', item.id).text(item.shipToId + " - " + item.ppSuffix);
				if (this.selected && this.selected == item.id) {
					option.attr('selected', 'selected');
				}
				this.$el.append(option);
			}, this);
			if(this.isDisabled) {
			    this.$el.attr('disabled', true);
			}
			return this;
		}
        
	});

    var constructSupplierCounterPartSelector = function(deliveryFollowUpTeamId) {
        var dfuTeam = deliveryFollowUpTeamId || "0";
        var dropdownOptions = null;      
        var collection = new SupplierCounterPartCollection();
        collection.url = '/common/v1/deliveryfollowupteams/' + dfuTeam + '/suppliercounterparts';
        collection.fetch({
            async : false,
            success : function(data) {
                dropdownOptions = data.toJSON();               
            }
        });       
        
        return dropdownOptions;
    };
    
    var constructSupplierCounterPartSelectorByCompanyCode = function(companyCode) {
        if(!companyCode) return;
        var dropdownOptions = null;   
        var collection = new SupplierCounterPartCollection();
        collection.url = '/common/v1/suppliercounterparts?companyCode=' + companyCode;
        collection.fetch({
            async : false,
            success : function(data) {
                dropdownOptions = data.toJSON();                
            }
        });
   
        return dropdownOptions;
    };
    
    var supplierCounterPartSelector = function(options) {
        var csv = new SupplierCounterPartSelectorView(options.hash);
        var returnString = new Handlebars.SafeString(csv.el.outerHTML);
        csv.destroy();
        csv = null;
        return returnString;
    };
    
    return {
        'handlebarsHelper' : supplierCounterPartSelector,
        'constructSupplierCounterPartList' : constructSupplierCounterPartSelector,
        'constructSupplierCounterPartSelectorByCompanyCode': constructSupplierCounterPartSelectorByCompanyCode
    };
});
