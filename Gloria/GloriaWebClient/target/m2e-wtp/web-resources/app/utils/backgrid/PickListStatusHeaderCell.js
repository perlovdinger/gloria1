define(['jquery',
        'underscore',
        'backgrid',
        'bootstrap',
        'i18next',
        'utils/backgrid/PickListStatusCell',
        'utils/backgrid/dropdownHeaderCell',
        'hbs!utils/backgrid/PickListStatusHeaderCell'
], function($, _, Backgrid, Bootstrap, i18n, PickListStatusCell, DropdownHeaderCell, PickListStatusHeaderCellTemplate) {

    var PickListStatusHeaderCell = DropdownHeaderCell.extend({
        
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
            _.each(PickListStatusCell.prototype.PICKLISTSTATUS_TYPES, function(value, key){
                if(key.toUpperCase() != 'DEFAULT' && value) {
                    options.column.defaultData.push({
                        id : key,
                        text : PickListStatusHeaderCellTemplate({value: value})
                    });
                }
            });
            
            return options;
        }
    });
    
    return PickListStatusHeaderCell;    
});