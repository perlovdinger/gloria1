define(['jquery',
        'backgrid',
        'bootstrap',
        'hbs!utils/backgrid/DeviationCell'
], function($, Backgrid, Bootstrap, DeviationCellTemplate) {
    
    var DEVIATION_TYPES = {
            DEFAULT: {
                className: ""
            },            
            HIGHER: {                
                keyTranslate: "Gloria.i18n.warehouse.inventory.deviation.HIGHER",
                className: "fa-arrow-up"
            },
            LOWER: {                
                keyTranslate: "Gloria.i18n.warehouse.inventory.deviation.LOWER",
                className: "fa-arrow-down"
            }
    };

    var DeviationCell = Backgrid.Cell.extend({
        
        'DEVIATION_TYPES': DEVIATION_TYPES,
        
        template: DeviationCellTemplate,
        
        render: function() {
            var flag = this.model.get('deviation') ? DEVIATION_TYPES[this.model.get('deviation')] : DEVIATION_TYPES['DEFAULT'];
            this.$el.html(this.template({
                className: flag['className'] || '',
                keyTranslate: flag['keyTranslate'] || '',
                id: this.model.id
            }));
            this.delegateEvents();
            return this;
        }
    });

    return DeviationCell;
});