define(['app',
        'jquery',
        'backgrid',
        'bootstrap',
        'i18next',        
        'utils/backgrid/DateCellEditor'        
], function(Gloria, $, Backgrid, Bootstrap, i18n, DateCellEditor) {       
    
    Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.ExpectedArrivalCell = Backgrid.Cell.extend({
            
            editor: DateCellEditor,
            
            render: function() {
                Backgrid.Cell.prototype.render.apply(this, arguments);
                if(this.model.get('markPassedDate')) {             
                    this.$el.addClass('markPassedDate');
                } else {
                    this.$el.removeClass('markPassedDate');
                }
                return this;
            },
            /**
             * Override. Removed "this.stopListening(this.currentEditor)" 
             * which caused loosing of the current view listeners on the model.
             */
            exitEditMode: function() {
                this.$el.removeClass("error");
                this.currentEditor && this.currentEditor.remove();            
                delete this.currentEditor;
                this.$el.removeClass("editor");
                this.render();
            }
        });
    });
    
    return Gloria.DeliveryControlApp.View.ExpectedArrivalCell;
});