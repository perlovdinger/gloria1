define(['jquery',
        'backgrid',
        'bootstrap',       
        'hbs!views/procurement/changedetails/view/MarkedGridCell'
], function($, Backgrid, Bootstrap, MarkedCellTemplate) {

    var MarkedGridCell = Backgrid.Cell.extend({
        
        template: MarkedCellTemplate,
        
        initialize: function(options) {
            var model = this.model;
            this.tooltip = true;
            Backgrid.Cell.prototype.initialize.apply(this, arguments);
            this.render();
        },
        
        render: function() {
            var changeIdModel = this.model.collection.changeIdModel;
            if(!changeIdModel) { return this;}
            var statusIcon;
            var statusColor;
            var statusText;
            
            if(this.model.get('mark') === 'REMOVED') {
                statusIcon = 'fa-minus';
            } else if(this.model.get('mark') === 'ADDED') {
                statusIcon = 'fa-plus';
            }
            
            if(changeIdModel.isRejected() || changeIdModel.isCancelRejected()) {
                statusColor = 'color-red';
            } else if(changeIdModel.isAccepted() || changeIdModel.isCancelled()) {
                statusColor = 'color-green';
            } else if(changeIdModel.isWaitingForConfirmation() || changeIdModel.isCancelWait()) {
                statusColor = 'color-orange';
            } else {
            	statusColor = 'color-black';
            }
            
            if(changeIdModel.hasStatus()) {
                statusText= 'Gloria.i18n.procurement.changeDetails.changeIDStates.' + changeIdModel.get('status').toUpperCase();
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