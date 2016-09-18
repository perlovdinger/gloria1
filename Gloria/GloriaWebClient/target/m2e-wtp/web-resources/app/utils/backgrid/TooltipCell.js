define(['jquery',
        'backgrid',
        'bootstrap'
], function($, BackgridPaginator, Bootstrap) {

	var TooltipCell = Backgrid.Cell.extend({

		initialize : function(options) {
			Backgrid.Cell.prototype.initialize.call(this, options);
			this.tooltipOptions = options.column.tooltip;
		},

		render : function() {
			var that = this;
			Backgrid.Cell.prototype.render.call(this, arguments);
			this.$el.tooltip({
				title: that.tooltipOptions.title,
				placement: 'top',
				container: 'body',
				trigger: 'hover'
            });
			return this;
		},
		
		remove : function() {
			this.$el.tooltip('destroy');
			Backgrid.Cell.prototype.remove.call(this, arguments);
		}
	});

	return TooltipCell;
});
