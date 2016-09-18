define(['app', 
        'backgrid'
], function (Gloria, Backgrid) {

    var spannedCell = Backgrid.Cell.extend({
        el: function() {
            return document.createTextNode('');
        }
    });
    
    return spannedCell;
});
