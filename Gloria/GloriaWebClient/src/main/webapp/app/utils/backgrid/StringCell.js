define(['jquery',
        'backgrid',
        'utils/backgrid/StringCellEditor'
], function($, Backgrid, StringCellEditor) {

    var StringCell = Backgrid.StringCell.extend({
        editor: StringCellEditor
    });

    return StringCell;
});