/**
 * Backgrid Row highlighter
 */
define([ 'app', 'backgrid', 'utils/backgrid/clickableRow' ], function(Gloria,
		BackgridPaginator, ClickableRow) {

	var HighlightableRow = ClickableRow.extend({

		highlightCssClass : 'background-orange',

		conditionCallback : function() {
			return false;
		},

		render : function(options) {
			var origRender = ClickableRow.prototype.render.apply(this, arguments);
			var className = this.conditionCallback.call(this);
			if (className.length > 0) {
				this.$el.addClass(className);
			}
			return origRender;
		}

	});

	return HighlightableRow;
});
