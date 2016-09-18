define(['jquery',
        'underscore',
        'backgrid',
        'bootstrap',
        'i18next',
        'utils/backgrid/dropdownHeaderCell',
        'hbs!views/warehouse/qualityinspection/overview/view/mark-header-cell'
], function($, _, Backgrid, Bootstrap, i18n, DropdownHeaderCell, Template) {

    var MarkHeaderCell = DropdownHeaderCell.extend({
    	
        initialize: function(options) {
        	this.MARK_TYPES = {          
                    'MANDATORY' : {                
                        keyTranslate: "Gloria.i18n.warehouse.qualityInspection.qioverview.mark.true",
                        className: "fa fa-star"
                    },
                    'OPTIONAL' : {                
                        keyTranslate: "Gloria.i18n.warehouse.qualityInspection.qioverview.mark.false",
                        className: "fa fa-star-half-empty"
                    }
            };
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
            _.each(this.MARK_TYPES, function(value, key){
                if(value) {
                    options.column.defaultData.push({
                        id : key,                                            
                        text : Template({value: value})
                    });
                }
            });
            return options;
        },
        
        // Filter event handler
		onStringFilter : function(e) {
			DropdownHeaderCell.prototype.onStringFilter.apply(this, arguments);
		}
    });
    
    return MarkHeaderCell;    
});