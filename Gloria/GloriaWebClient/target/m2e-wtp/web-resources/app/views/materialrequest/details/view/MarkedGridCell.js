define(['jquery',
        'backgrid',
        'bootstrap',       
        'hbs!views/materialrequest/details/view/MarkedGridCell'
], function($, Backgrid, Bootstrap, MarkedCellTemplate) {

    var MarkedGridCell = Backgrid.Cell.extend({
        
        template: MarkedCellTemplate,
        
        initialize: function(options) {
            var model = this.model;
            this.tooltip = true;
            Backgrid.Cell.prototype.initialize.apply(this, arguments);
            this.listenTo(model, "change:removeMarked change:isNew change:" + model.get(model.idAttribute) , function () {
                if (!this.$el.hasClass("editor")) this.render();
            });
        },
        
        render: function() {
            var materialRequest = this.model.collection.materialRequest;
            if(!materialRequest) { return this;}
            //var status = materialRequest.get('status');
            var statusIcon;
            var statusColor;
            var statusText;
            
            if(this.model.get('removeMarked')) {
                statusIcon = 'fa-minus';
            }
            if(this.model.get('isNew')) {
                statusIcon = 'fa-plus';
            }            
            
            if(materialRequest.isInWork()) {
                statusColor = 'color-black';                
            } else if(materialRequest.isRejected() || materialRequest.isCancelRejected()) {
                statusColor = 'color-red';
            } else if(materialRequest.isAccepted() || materialRequest.isCancelled()) {
                statusColor = 'color-green';
            } else if(materialRequest.isWaitingForConfirmation() || materialRequest.isCancelWait) {
                statusColor = 'color-orange';
            }
            
            if(materialRequest.hasStatus()) {
                statusText= 'Gloria.i18n.materialrequest.overview.status.' + materialRequest.get('materialRequestVersionStatus').toUpperCase();
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