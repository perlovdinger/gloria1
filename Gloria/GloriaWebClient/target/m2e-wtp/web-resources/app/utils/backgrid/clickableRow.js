/**
 * A Backgrid.Row class, which handles marking of selected rows and firing double click events for rows
 */
define(['app', 
        'backgrid'
], function (Gloria, BackgridPaginator) {

	var ClickableRow = Backgrid.Row.extend({
		events : {
			"click" : "onClick",
			"dblclick" : "onDoubleClick"
		},
		onDoubleClick : function(e) {
			$(e.currentTarget).trigger("rowdoubleclicked", this.model);
		},
		onClick: function (e) {
		    $(e.currentTarget).trigger("rowclicked", this.model);
			$(e.currentTarget).siblings('tr').removeClass('selected');
			$(e.currentTarget).addClass('selected');
		}
	});
	
	return ClickableRow;
});
