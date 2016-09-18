define(['jquery',
        'backgrid',
        'bootstrap'
], function($, Backgrid, Bootstrap) {
    
    var IntegerCellEditor = Backgrid.InputCellEditor.extend({

        render : function() {
            Backgrid.InputCellEditor.prototype.render.apply(this);
            this.$el.attr('type', 'number');
            this.$el.attr('min', '0');
            this.$el.attr('step', '1');
            this.$el.attr('pattern', '\d+');
        },
        
        postRender: function (model, column) {
            Backgrid.InputCellEditor.prototype.postRender.apply(this, model, column);
            this.$el.focus().select();
            return this;
        }
    });    

    return IntegerCellEditor;
});