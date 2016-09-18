/**
 * Order Report View
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
        'backbone.syphon',
        'datepicker',
        'moment',
        'i18next',
		'hbs!views/reports/view/performance-report',
		'views/reports/components/WarehouseSelector',
		'views/reports/components/SuffixSelector',
		'views/reports/components/ProjectSelector',
		'views/reports/components/BuildSeriesSelector',
		'views/reports/components/BuyerIdSelector',
		'views/reports/components/PartNoSelector',
		'views/reports/components/DateTypeSelector'
], function(Gloria, $, _, Handlebars, Marionette, Syphon, Datepicker, moment, i18n, compiledTemplate,
		WarehouseSelector, SuffixSelector, ProjectSelector, BuildSeriesSelector, BuyerIdSelector, PartNoSelector, DateTypeSelector) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PerformanceReportView = Backbone.Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
			},
			
			regions : {
				warehouseSelectorContainer : 'div#warehouseSelectorContainer',
				suffixSelectorContainer : 'div#suffixSelectorContainer',
				projectSelectorContainer : 'div#projectSelectorContainer',
				buildSeriesSelectorContainer : 'div#buildSeriesSelectorContainer',
				buyerIdSelectorContainer : 'div#buyerIdSelectorContainer',
				partNoSelectorContainer : 'div#partNumberSelectorContainer',
				dateTypeSelectorContainer : 'div#dateTypeSelectorContainer'
			},
			
			events : {
				'change #dateType' : 'handleDateTypeChange'
			},
			
			// Warehouse Code Selector
			warehouseSelector : function() {
				this.warehouseSelectorContainer.show(new WarehouseSelector({
					el : this.$('#warehouse')
				}));
			},
			
			// Suffix Selector
			suffixSelector : function() {
				this.suffixSelectorContainer.show(new SuffixSelector({
					el : this.$('#suffix')
				}));
			},
			
			// Project Selector
			projectSelector : function() {
				var formData = Backbone.Syphon.serialize(this);
				this.projectSelectorContainer.show(new ProjectSelector({
					el : this.$('#project'),
					companyCode : formData.dropdown.companyCode
				}));
			},
			
			// Build Series Selector
			buildSeriesSelector : function() {
				var formData = Backbone.Syphon.serialize(this);
				this.buildSeriesSelectorContainer.show(new BuildSeriesSelector({
					el : this.$('#buildSeries'),
					companyCode : formData.dropdown.companyCode
				}));
			},
			
			// Buyer Id Selector
			buyerIdSelector : function() {
				this.buyerIdSelectorContainer.show(new BuyerIdSelector({
					el : this.$('#buyerId')
				}));
			},
			
			// Part Number Selector
			partNoSelector : function() {
				this.partNoSelectorContainer.show(new PartNoSelector({
					el : this.$('#partNumber')
				}));
			},
			
			// Time Between Selector
			dateTypeSelector : function() {
				this.dateTypeSelectorContainer.show(new DateTypeSelector({
					el : this.$('#dateType'),
					select2Options: {
						placeholder: i18n.t('Gloria.i18n.general.pleaseSelect')
    				}
				}));
			},
			
			handleDateTypeChange : function(e) {
				var selectedOption = e.currentTarget.value;
				if(selectedOption) {
					this.$el.find('#fromDateHints').text(i18n.t('Gloria.i18n.reports.text.' + selectedOption + '_FromDateText'));
					this.$el.find('#toDateHints').text(i18n.t('Gloria.i18n.reports.text.' + selectedOption + '_ToDateText'));
				} else {
					this.$el.find('#fromDateHints').text('');
					this.$el.find('#toDateHints').text('');
				}
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					actions : this.actions
				}));
				this.$('.date').datepicker();
				return this;
			},
			
			populate : function() {
				this.warehouseSelector();
				this.suffixSelector();
				this.projectSelector();
				this.buildSeriesSelector();
				this.buyerIdSelector();
				this.partNoSelector();
				this.dateTypeSelector();
			},
			
			onShow : function() {
				this.populate();
			},
			
			onDestroy : function() {
				this.$('.date').datepicker('remove');
			}
		});
	});

	return Gloria.ReportsApp.View.PerformanceReportView;
});
