define(['jquery',
        'underscore',
        'backgrid',
        'bootstrap',
        'i18next',
        'utils/backgrid/FlagCell',
        'utils/backgrid/dropdownHeaderCell',
        'hbs!utils/backgrid/FlagHeaderCell'
], function($, _, Backgrid, Bootstrap, i18n, FlagCell, DropdownHeaderCell, FlagHeaderCellTemplate) {

	
    var FlagHeaderCell = DropdownHeaderCell.extend({
    	
        className: (Backgrid.HeaderCell.prototype.className || '') + ' xs-fixedWidth',

        initialize: function(options) {            
            this.makeOptions(options);
            DropdownHeaderCell.prototype.initialize.apply(this, arguments);
        },

        
        makeOptions: function(options) {
        	var flagSelect;
        	
        	if(options.column.attributes.moduleName == 'TOPROCURE' && (options.isInternalProcurer == null)) {
        		flagSelect = FlagCell.prototype.TOPROCURE_FLAG_TYPES;
        	} else if (options.column.attributes.moduleName == 'TOPROCURE' && (options.isInternalProcurer)) {
        		flagSelect = FlagCell.prototype.TOPROCURE_IP_FLAG_TYPES;
        	} else {
        		flagSelect =  FlagCell.prototype.FLAG_TYPES;
        	}
            options.column.select2Options = {
                escapeMarkup: function(m) { 
                    return m; 
                }
            };
            
            options.column.defaultData = [];            
            _.each(flagSelect, function(value, key){
                if(key.toUpperCase() != 'DEFAULT' && value) {
                    options.column.defaultData.push({
                        id : key,                                            
                        text : FlagHeaderCellTemplate({value: value})
                    });
                }
            });
            
            return options;
        },
        
        // Filter event handler
		onStringFilter : function(e) {
			DropdownHeaderCell.prototype.onStringFilter.apply(this, arguments);
		},
    });
    
    return FlagHeaderCell;    
});