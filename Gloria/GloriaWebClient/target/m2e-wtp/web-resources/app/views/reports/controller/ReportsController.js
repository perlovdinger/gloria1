define(['app',
        'jquery',
        'underscore',
        'backbone',
        'handlebars',
        'marionette',
		'i18next',
		'utils/DateHelper',
		'utils/UserHelper',
		'utils/AjaxHelper',
		'utils/backbone/GloriaCollection',
		'utils/backbone/GloriaModel'
], function(Gloria, $, _, Backbone, Handlebars, Marionette, i18n, DateHelper, UserHelper, AjaxHelper, GloriaCollection, GloriaModel) {

	Gloria.module('ReportsApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var module;
		var reportsView;
		var favoriteFilterCollection;
		var favoriteFilterModel;
		
		/**
		 * Prepare Report View
		 */
		var prepareReport = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			favoriteFilterCollection = new GloriaCollection();
			favoriteFilterModel = new GloriaModel();
		};

		/**
		 * Set Page Layout
		 */
		var setPageLayout = function() {
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', null);
			require([ 'views/reports/view/ReportsView' ], function(ReportsView) {
				reportsView = new ReportsView({
					module : module
				});
				reportsView.on('show', function() {
					showReportModuleView();	// Module Info
					if(module == 'order' || module == 'material') {
						prepareBasicFilterView();
					} else {
						showBasicFilterView();
					}
				});
				Gloria.basicLayout.content.show(reportsView);
			});
		};
		
		/**
		 * Show/Render Report Module.
		 */
		var showReportModuleView = function() {
			require(['views/reports/common/ReportModuleView'], function(ReportModuleView) {
				var reportModuleView = new ReportModuleView({
					module : 'report'
				});
				// Attach to moduleInfo region
				reportsView.moduleInfo.show(reportModuleView);
			});
		};

		
		/**
		 * Prepare Basic Filter View
		 */
		var prepareBasicFilterView = function() {
			favoriteFilterCollection.fetch({
				url : '/report/v1/reportFilters',
				asyc : false,
				data : {
					reportType : module.toUpperCase(),
					userId : UserHelper.getInstance().getUserId()
				},
				success : function() {
					showBasicFilterView();
				}
			});
		};
		
		/**
		 * Show Basic Filter View
		 */
		var showBasicFilterView = function() {
			require([ 'views/reports/view/BasicFilterView' ], function(BasicFilterView) {
				var basicFilterView = new BasicFilterView({
					module : module,
					favFilters : favoriteFilterCollection
				});
				basicFilterView.on('show', function() {
					showPossibleInfoView();
				}, this);
				reportsView.basicInfo.show(basicFilterView);
			});
		};
		
		/**
		 * Show Possible Info View
		 */
		var showPossibleInfoView = function() {
			switch (module) {
			case 'order':
				showOrderInfoView();
				break;
			case 'material':
				showMaterialInfoView();
				break;
			case 'performance':
				showPerformanceInfoView();
				break;
			case 'warehouse':
				showWarehouseGeneralInfoView();
				break;
			case 'warehouseCost':
				showWarehouseCostInfoView();
				break;
			case 'warehouseAction':
				showWarehouseActionInfoView();
				break;
			case 'warehouseTransaction':
				showWarehouseTransactionInfoView();
				break;
			case 'partDeliveryPrecision':
				showPartDeliveryPrecisionInfoView();
				break;
			default:
				break;
			}
		};
		
		/**
		 * Show Order Info View
		 */
		var showOrderInfoView = function() {
			require([ 'views/reports/view/OrderReportView' ], function(OrderReportView) {
				var orderReportView = new OrderReportView({
					model : favoriteFilterModel
				});
				reportsView.possibleInfo.show(orderReportView);
			});
		};
		
		/**
		 * Show Material Info View
		 */
		var showMaterialInfoView = function() {
			require([ 'views/reports/view/MaterialReportView' ], function(MaterialReportView) {
				var materialReportView = new MaterialReportView({
					model : favoriteFilterModel
				});
				reportsView.possibleInfo.show(materialReportView);
			});
		};
		
		/**
		 * Show Warehouse General Info View
		 */
		var showWarehouseGeneralInfoView = function() {
			require([ 'views/reports/view/WarehouseGeneralReportView' ], function(WarehouseGeneralReportView) {
				var warehouseGeneralReportView = new WarehouseGeneralReportView();
				reportsView.possibleInfo.show(warehouseGeneralReportView);
			});
		};
		
		/**
		 * Show Warehouse Cost Info View
		 */
		var showWarehouseCostInfoView = function() {
			require([ 'views/reports/view/WarehouseCostReportView' ], function(WarehouseCostReportView) {
				var warehouseCostReportView = new WarehouseCostReportView();
				reportsView.possibleInfo.show(warehouseCostReportView);
			});
		};
		
		/**
		 * Show Performance Info View
		 */
		var showPerformanceInfoView = function() {
			require([ 'views/reports/view/PerformanceReportView' ], function(PerformanceReportView) {
				var performanceReportView = new PerformanceReportView();
				reportsView.possibleInfo.show(performanceReportView);
			});
		};
		
		/**
		 * Show Warehouse Action Info View
		 */
		var showWarehouseActionInfoView = function() {
			require([ 'views/reports/view/WarehouseActionReportView' ], function(WarehouseActionReportView) {
				var warehouseActionReportView = new WarehouseActionReportView();
				reportsView.possibleInfo.show(warehouseActionReportView);
			});
		};
		
		/**
		 * Show Warehouse Transaction Info View
		 */
		var showWarehouseTransactionInfoView = function() {
			require([ 'views/reports/view/WarehouseTransactionReportView' ], function(WarehouseTransactionReportView) {
				var warehouseTransactionReportView = new WarehouseTransactionReportView();
				reportsView.possibleInfo.show(warehouseTransactionReportView);
			});
		};
		
		/**
		 * Show Part Delivery Precision Info View
		 */
		var showPartDeliveryPrecisionInfoView = function() {
			require([ 'views/reports/view/PartDeliveryPrecisionReportView' ], function(PartDeliveryPrecisionReportView) {
				var partDeliveryPrecisionReportView = new PartDeliveryPrecisionReportView();
				reportsView.possibleInfo.show(partDeliveryPrecisionReportView);
			});
		};
		
		/**
		 * Show Favorite Modal
		 */
		var showFavoriteModal = function() {
			require(['views/reports/view/FavoriteModalView'], function(FavoriteModalView) {
				var favoriteModalView = new FavoriteModalView({
					module : module
				});
				Gloria.basicModalLayout.content.show(favoriteModalView);
			});
		};
		
		/**
		 * Fetch Favorite Filter
		 */
		var fetchFavoriteFilter = function(id) {
			if(id) {
				favoriteFilterModel.fetch({
					url : '/report/v1/reportFilters/' + id,
					success : function() {
						showPossibleInfoView();
					}
				});
			} else {
				favoriteFilterModel.clear();
				showPossibleInfoView();
			}
		};
		
		/**
		 * Save Favorite Filter
		 */
		var saveFavoriteFilter = function(name, info) {
			var getRestUrl = function(module) {
				var url = null;
				switch (module) {
				case 'order':
					url = '/report/v1/reportFilterOrders?userId=' + UserHelper.getInstance().getUserId();
					break;
				case 'material':
					url = '/report/v1/reportFilterMaterials?userId=' + UserHelper.getInstance().getUserId();
					break;
				default:
					break;
				}
				return url;
			};
			info = _.extend(info, {name : name, type : module.toUpperCase()});
			// Only CREATE new!!!
			favoriteFilterModel.unset('id');
			favoriteFilterModel.save(info, {
				url : getRestUrl(module),
				success : function(resp) {
					fetchFavoriteFilterCollection();
					Gloria.ReportsApp.trigger('Report:Favorite:saved', resp.id);
					showPossibleInfoView();
				}
			});
		};
		
		/**
		 * Delete Favorite Filter
		 */
		var deleteFavoriteFilter = function(model) {
			favoriteFilterModel.destroy({
				url : '/report/v1/reportFilters/' + favoriteFilterModel.id,
				success : function(resp) {
					favoriteFilterModel.clear();
					prepareBasicFilterView();
				}
			});
		};
		
		/**
		 * Fetch Favorite Filter Collection
		 */
		var fetchFavoriteFilterCollection = function() {
			favoriteFilterCollection.fetch({
				url : '/report/v1/reportFilters',
				data : {
					reportType : module.toUpperCase(),
					userId : UserHelper.getInstance().getUserId()
				}
			});
		};
		
		/**
		 * Export Excel
		 */
		var exportExcel = function(info, basicInfo) {
			AjaxHelper.downloadBinary(getRestUrl(module, basicInfo), info);
		};
		
		/**
		 * Get REST Url
		 */
		var getRestUrl = function(module, basicInfo) {
			var url = null;
			switch (module) {
			case 'order':
				url = '/report/v1/reportFilterOrders/excel?userId=' + UserHelper.getInstance().getUserId()
						+ '&fromDate=' + basicInfo.fromDate+ '&toDate=' + basicInfo.toDate;
				break;
			case 'material':
				url = '/report/v1/reportFilterMaterials/excel?userId=' + UserHelper.getInstance().getUserId();
				break;
			case 'performance':
				url = '/report/v1/reportPerformance/excel?userId=' + UserHelper.getInstance().getUserId()
				+ '&dateType=' + basicInfo.dateType + '&fromDate=' + basicInfo.fromDate + '&toDate=' + basicInfo.toDate;
				break;
			case 'warehouse':
				url = '/report/v1/reportGeneralWarehouse/excel?userId=' + UserHelper.getInstance().getUserId();
				break;
			case 'warehouseCost':
				url = '/report/v1/reportWarehouseCost/excel?userId=' + UserHelper.getInstance().getUserId()
					+ '&fromDate=' + basicInfo.fromDate + '&toDate=' + basicInfo.toDate;
				break;
			case 'warehouseAction':
				url = '/report/v1/reportWarehouseAction/excel?userId=' + UserHelper.getInstance().getUserId()
					+ '&fromDate=' + basicInfo.fromDate + '&toDate=' + basicInfo.toDate;
				break;
			case 'warehouseTransaction':
				url = '/report/v1/reportWarehouseTransaction/excel?userId=' + UserHelper.getInstance().getUserId()
					+ '&fromDate=' + basicInfo.fromDate + '&toDate=' + basicInfo.toDate;
				break;
			case 'partDeliveryPrecision':
				url = '/report/v1/reportPartDeliveryPrecision/excel?userId=' + UserHelper.getInstance().getUserId()
					+ '&fromDate=' + basicInfo.fromDate + '&toDate=' + basicInfo.toDate;
				break;
			default:
				break;
			}
			return url;
		};
		
		Controller.ReportsController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.ReportsApp, 'Report:Favorite:show', showFavoriteModal);
				this.listenTo(Gloria.ReportsApp, 'Report:Favorite:fetch', fetchFavoriteFilter);
				this.listenTo(Gloria.ReportsApp, 'Report:Favorite:save', saveFavoriteFilter);
				this.listenTo(Gloria.ReportsApp, 'Report:Favorite:delete', deleteFavoriteFilter);
				this.listenTo(Gloria.ReportsApp, 'Report:Excel:export', exportExcel);
			},

			control : function(page) {
				module = page;
				prepareReport.call(this);
			},

			disposeVariables : function() {
				module = null;
				reportsView = null;
				favoriteFilterCollection = null;
				favoriteFilterModel = null;
			},

			onDestroy : function() {
				this.disposeVariables();
			}
		});
	});

	return Gloria.ReportsApp.Controller.ReportsController;
});
