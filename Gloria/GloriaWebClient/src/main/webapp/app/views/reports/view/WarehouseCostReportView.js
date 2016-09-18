/**
 * Warehouse Cost Report View
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
        'backbone.syphon',
		'hbs!views/reports/view/warehouse-cost-report',
		'views/reports/components/WarehouseSelector',
		'views/reports/components/ProjectSelector'
], function(Gloria, $, _, Handlebars, Marionette, Syphon, compiledTemplate, WarehouseSelector, ProjectSelector) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.WarehouseCostReportView = Backbone.Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
			},
			
			regions : {
				warehouseSelectorContainer : 'div#warehouseSelectorContainer',
				projectSelectorContainer : 'div#projectSelectorContainer'
			},
			
			// Warehouse Code Selector
			warehouseSelector : function() {
				this.warehouseSelectorContainer.show(new WarehouseSelector({
					el : this.$('#warehouse')
				}));
			},

			// Project Selector
			projectSelector : function() {
				var formData = Backbone.Syphon.serialize(this);
				this.projectSelectorContainer.show(new ProjectSelector({
					el : this.$('#project')
				}));
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module
				}));
				return this;
			},
			
			populate : function() {
				this.warehouseSelector();
				this.projectSelector();
			},
			
			onShow : function() {
				this.populate();
			},
			
			onDestroy : function() {

			}
		});
	});

	return Gloria.ReportsApp.View.WarehouseCostReportView;
});
