define(['jquery',
        'backgrid',
        'bootstrap',
        'i18next',
        'hbs!views/warehouse/pick/view/MarkedGridCell'
], function($, Backgrid, Bootstrap, i18n, MarkedCellTemplate) {

    var MarkedGridCell = Backgrid.Cell.extend({
        
        template: MarkedCellTemplate,
        
        initialize: function(options) {
            this.tooltip = true;
            Backgrid.Cell.prototype.initialize.apply(this, arguments);
            this.render();
        },
        
        render: function() {
            var statusIcon;
            var statusColor;
            var statusText;
            
            if(this.model.get('status') === 'PICKED') {
                statusIcon = 'fa-check';
                statusColor = 'color-green';
                statusText = this.model.get('status');
            } 
            
            if(this.model.get('status') == null && this.model.get('items') == 0) {
                statusIcon = 'fa-exclamation-triangle';
                statusColor = 'color-orange';
                statusText = i18n.t('Gloria.i18n.warehouse.ship.yettoRecieve'); 
            }
           
            
            this.$el.html(this.template({
                statusIcon: statusIcon,
                statusColor: statusColor,
                statusText: statusText
            }));
                        
            this.$('i').tooltip();            
            
            return this;
        },
        
        remove: function() {
            this.$('i').tooltip('destroy');
            return Backgrid.Cell.prototype.remove.apply(this, arguments);
        }
    });

    return MarkedGridCell;
});