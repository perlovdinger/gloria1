define(['jquery',
        'backgrid'
], function($, Backgrid) {
    
    var StringCellEditor = Backgrid.StringCell.prototype.editor.extend({

        postRender: function (model, column) {
            Backgrid.CellEditor.prototype.postRender.apply(this, model, column);
            this.$el.focus().select();
            return this;
        }
    });    

    return StringCellEditor;
});