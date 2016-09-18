define(['jquery',
        'backgrid',
        'bootstrap',
        'hbs!utils/backgrid/PickListStatusCell'
], function($, Backgrid, Bootstrap, PickListStatusCellTemplate) {
    
    var PICKLISTSTATUS_TYPES = {
            DEFAULT: {
                className: ""
            },            
            CREATED: {                
                keyTranslate: "Gloria.i18n.warehouse.pick.pickListStatus.CREATED",
                className: "fa-edit"
            },
            IN_WORK: {                
                keyTranslate: "Gloria.i18n.warehouse.pick.pickListStatus.IN_WORK",
                className: "fa-spinner"
            },
            PICKED: {                
                keyTranslate: "Gloria.i18n.warehouse.pick.pickListStatus.PICKED",
                className: "fa-check "
            },
            CANCELLED: {                
                keyTranslate: "Gloria.i18n.warehouse.pick.pickListStatus.CANCELLED",
                className: "fa-times"
            }
    };

    var PickListStatusCell = Backgrid.Cell.extend({
        
        'PICKLISTSTATUS_TYPES': PICKLISTSTATUS_TYPES,
        
        template: PickListStatusCellTemplate,
        
        render: function() {
            var flag = this.model.get('status') ? PICKLISTSTATUS_TYPES[this.model.get('status')] : PICKLISTSTATUS_TYPES['DEFAULT'];
            this.$el.html(this.template({
                className: flag && flag['className'] || '',
                keyTranslate: flag && flag['keyTranslate'] || '',
                id: this.model.id
            }));
            this.delegateEvents();
            return this;
        }
    });

    return PickListStatusCell;
});