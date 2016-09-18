define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'backgrid',
        'select2'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Backgrid, select2) {

    var ActionCell = Backgrid.Cell.extend({

        initialize : function(options) {  
            Backgrid.Cell.prototype.initialize.call(this, options);
        },

        render : function() {
        	var that = this;			
			this.$el.html('<span></span>');
			var select2Data = [];
			if((that.model.get('materialType') == 'USAGE' || that.model.get('materialType') == 'RELEASED') && that.model.get('procureLineStatus') == 'PROCURED'
				&& (that.model.get('procureType') == 'EXTERNAL' || that.model.get('procureType') == 'EXTERNAL_FROM_STOCK')) {
				select2Data.push({
					id : 'CANCEL_PP_REQ',
					text : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.actions.CANCEL_PP_REQ')
				});
			} else if((that.model.get('materialType') == 'USAGE' || that.model.get('materialType') == 'RELEASED') && that.model.get('procureLineStatus') == 'PLACED' 
				&& (that.model.get('procureType') == 'INTERNAL' || that.model.get('procureType') == 'INTERNAL_FROM_STOCK')) {
				select2Data.push({
					id : 'CANCEL_IO',
					text : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.actions.CANCEL_IO')
				});
			}
			select2Data.push({
				id : 'ADDITIONAL',
				text : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.actions.ADDITIONAL')
			});

        	var selectOptions = {
				data : select2Data,
				placeholder: i18n.t('Gloria.i18n.selectBoxDefaultValue'),
				width: 'off',
				containerCss: {
				    minWidth: '100%',
				    width: '0px',
				    maxWidth: '100%'
				}
			};
        	selectOptions.dropdownAutoWidth = true;
			this.$('span').select2(selectOptions).on('change', function(e) {	
				that.model.set(that.column.attributes.name, e.val, {silent : true});
				Gloria.ProcurementApp.trigger('Action:change', that.model);
			});
			if(that.model.get(that.column.attributes.name)) {
				this.$('span').select2('val', that.model.get(that.column.attributes.name));
			} else { // Reset
				this.$('span').prop('selectedIndex', 0);
			}
            return this;
        },
        
        remove: function() {
            this.$('span').select2('destroy');
            return Backgrid.Cell.prototype.remove.apply(this, arguments);
        }
    });

    return ActionCell;
});
