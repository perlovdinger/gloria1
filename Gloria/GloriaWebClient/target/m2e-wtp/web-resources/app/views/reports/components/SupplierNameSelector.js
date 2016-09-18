/**
 * Supplier Name Selector
 */
define([ 'app', 'views/reports/components/BaseSelector' ], function(Gloria,
		BaseSelector) {

	var SupplierNameSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.disabled = true;
			this.listenTo(Gloria.ReportsApp, 'select2-selected-supplierparma', this.handleSupplierParmaChange);
			this.listenTo(Gloria.ReportsApp, 'select2-selected-supplierparma:refresh', this.refresh);
			BaseSelector.prototype.initialize.call(this, options);
		},

		handleSupplierParmaChange : function(options) {
			var finalData = this.$el.select2('data');
			if (options.type == 'added') {
				if (options.item.id == 'all') {
					this.$el.data().select2.updateSelection(this.defaultData());
				} else {
					_.any(finalData, function(info, i) {
						if (info.id == 'all') {
							finalData.splice(i, 1);
							return true;
						}
					});
					_.any(options.data, function(item) {
						if (item.id == options.item.id) {
							finalData.push({
								id : item.id,
								text : item.supplierName
							});
							return true;
						}
					});
					this.$el.select2('data', finalData);
				}
			} else if (options.type == 'removed') {
				_.any(finalData, function(info, i) {
					if (info.id == options.item.id) {
						finalData.splice(i, 1);
						return true;
					}
				});
				this.$el.select2('data', finalData, true);
			}
		}
	});

	return SupplierNameSelector;
});