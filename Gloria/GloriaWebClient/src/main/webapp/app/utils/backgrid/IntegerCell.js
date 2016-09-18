define(['jquery',
        'backgrid',
        'bootstrap',
        'utils/backgrid/IntegerCellEditor'
], function($, Backgrid, Bootstrap, IntegerCellEditor) {

    var IntegerCell = Backgrid.IntegerCell.extend({
    	
    	initialize : function(options) {  
        	if(options.className) {
				this.$el.addClass(options.className);
			}
            Backgrid.Cell.prototype.initialize.call(this, options);
        },
        
        editor: IntegerCellEditor
    
    });

    return IntegerCell;
});