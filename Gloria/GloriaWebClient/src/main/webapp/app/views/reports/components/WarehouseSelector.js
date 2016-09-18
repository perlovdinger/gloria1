/**
 * Warehouse Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var WarehouseSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.module = 'warehouse';
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		cachePrefix : 'gloria.selectors.warehouses.',

		url : function() {
			return '/report/v1/warehouses?userId=' + UserHelper.getInstance().getUserId();
		},
		
		mapData: function(data) {
			return _.map(data, function(object) {
				var id = object[this.resultMap.id] || object.id;
				var text = (object[this.resultMap.text] || object.id) + ' (' + id + ')';
				return {
					id : id,
					text : text
				};
			}, this);
		}
	});

	return WarehouseSelector;
});