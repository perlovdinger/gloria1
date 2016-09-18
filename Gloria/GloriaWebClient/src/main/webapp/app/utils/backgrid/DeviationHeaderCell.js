define(['jquery',
        'underscore',
        'backgrid',
        'bootstrap',
        'i18next',
        'utils/backgrid/DeviationCell',
        'utils/backgrid/dropdownHeaderCell',
        'hbs!utils/backgrid/DeviationHeaderCell'
], function($, _, Backgrid, Bootstrap, i18n, DeviationCell, DropdownHeaderCell, DeviationHeaderCellTemplate) {

    var DeviationHeaderCell = DropdownHeaderCell.extend({
        
        initialize: function(options) {            
            this.makeOptions(options);
            DropdownHeaderCell.prototype.initialize.apply(this, arguments);
        },
        
        makeOptions: function(options) {  
            options.className = 'xxs-fixedWidth';
            options.column.select2Options = {
                escapeMarkup: function(m) { 
                    return m; 
                }
            };
            options.column.defaultData = [];            
            _.each(DeviationCell.prototype.DEVIATION_TYPES, function(value, key){
                if(key.toUpperCase() != 'DEFAULT' && value) {
                    options.column.defaultData.push({
                        id : key,
                        text : DeviationHeaderCellTemplate({value: value})
                    });
                }
            });
            return options;
        }
    });
    
    return DeviationHeaderCell;    
});