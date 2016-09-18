/**
 * Basic Filter View
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
        'datepicker',
        'moment',
		'hbs!views/reports/view/basic-filter'
], function(Gloria, $, _, Handlebars, Marionette, Datepicker, moment, compiledTemplate) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.BasicFilterView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
				this.favFilters = options.favFilters;
				this.listenTo(this.favFilters, 'sync', this.render);
				this.listenTo(Gloria.ReportsApp, 'Report:Favorite:saved', this.favFilterSelection);
			},
			
			events : {
				'change #reportType' : 'handleReportTypeChange',
				'change #filterType' : 'handleFilterTypeChange',
				'change #fromDate' : 'handleFromDateChange',
				'change #toDate' : 'handleToDateChange'
			},
			
			reportTypes : [{
				'order' : 'Gloria.i18n.reports.reportTypes.order'
			}, {
				'material' : 'Gloria.i18n.reports.reportTypes.material'
			}, {
				'performance' : 'Gloria.i18n.reports.reportTypes.performance'
			}, {
				'warehouse' : 'Gloria.i18n.reports.reportTypes.warehouse'
			}, {
				'warehouseCost' : 'Gloria.i18n.reports.reportTypes.warehouseCost'
			}, {
				'warehouseAction' : 'Gloria.i18n.reports.reportTypes.warehouseAction'
			}, {
				'warehouseTransaction' : 'Gloria.i18n.reports.reportTypes.warehouseTransaction'
			}, {
				'partDeliveryPrecision' : 'Gloria.i18n.reports.reportTypes.partDeliveryPrecision'
			}/*, {
				'partWithoutMovement' : 'Gloria.i18n.reports.reportTypes.partWithoutMovement'
			}*/],
			
			handleReportTypeChange : function(e) {
				this.selectedType = e.currentTarget.value;
				var thisLoc = this.selectedType ? 'reports/report/' + this.selectedType : 'reports/report';
				Backbone.history.navigate(thisLoc, {
					trigger : true
				});
			},
			
			handleFilterTypeChange : function(e) {
				Gloria.ReportsApp.trigger('Report:Favorite:fetch', e.currentTarget.value);
			},
			
			handleFromDateChange : function(e) {
				this.fromDate = e.currentTarget.value;
			},
			
			handleToDateChange : function(e) {
				this.toDate = e.currentTarget.value;
			},
			
			favFilterSelection : function(selectedVal) {
				this.currentFavFilter = selectedVal;
			},
			
			isFavFiltersApplicable : function() {
				return this.module == 'order' || this.module == 'material';
			},
			
			isDatesFiltersApplicable : function() {
				return this.module == 'order' || this.module == 'partDeliveryPrecision'
					|| this.module == 'warehouseAction' || this.module == 'warehouseCost';
			},
			
			// Format Filter
			getFavFilters : function(favFilters) {
				var formatterdFilters = new Array();
				favFilters.each(function(filter) {
					var obj = new Object();
					obj[filter.id] = filter.get('name');
					formatterdFilters.push(obj);
				});
				return formatterdFilters;
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					reportTypes : this.reportTypes,
					showFavFilters : this.isFavFiltersApplicable(),
					showDatesFilters : this.isDatesFiltersApplicable(),
					favFilters : this.favFilters ? this.getFavFilters(this.favFilters) : [],
					currentFavFilter : this.currentFavFilter,
					fromDate : this.fromDate,
					toDate : this.toDate
				}));
				this.$('.date').datepicker();
				return this;
			},

			onDestroy : function() {
				this.$('.date').datepicker('remove');
			}
		});
	});

	return Gloria.ReportsApp.View.BasicFilterView;
});
