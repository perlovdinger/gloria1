define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'utils/UserHelper',
		'utils/backbone/GloriaPageableCollection',
		'utils/backbone/GloriaModel'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, UserHelper, GloriaPageableCollection, GloriaModel) {

	Gloria.module('MaterialRequestApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var module;
		var materialRequestCollection;

		/**
		 * Prepare Material Request Overview. Initialize Data Source Objects
		 * which are going to be used as data transfer objects and set page layout.
		 */
		var prepareMaterialRequestOverview = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};

		/**
		 * Initialize Data Source Objects.
		 * These objects are going to be used by the page/components.
		 */
		var initializeDataSourceObjects = function() {
			materialRequestCollection = new GloriaPageableCollection([], {
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.materialrequest.' + module
								+ '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1,
					sortKey : 'materialRequestVersionStatusDate', 	// Sort by date
					order : 1										// By Descending Order, latest first
				},
				filterKey : module
			});
			materialRequestCollection.url = '/materialrequest/v1/materialrequests/current';
			

            if (!UserHelper.getInstance().hasUserRole('IT_SUPPORT')) {
                if (!materialRequestCollection.getFilterStorageValue('requesterId')) { // Set default requester asn logged in user
                    materialRequestCollection.queryParams.requesterId = UserHelper.getInstance().getUserId();
                }
            }
	
		};
		
		/**
		 * Set Page Layout.
		 * OverviewLayout is the main Layout which adds three regions: controlPane, buttonPane & gridPane
		 * to the page, so that the respective views can be attached later on!
		 */
		var setPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView');
			require(['views/materialrequest/overview/view/OverviewLayout'], function(OverviewLayout) {
				var overviewLayout = new OverviewLayout();
				overviewLayout.on('show', function() {
					showOverviewContent(overviewLayout);
				}, this);
				Gloria.basicLayout.content.show(overviewLayout);
			});
		};
		
		/**
		 * Show/Render Material Request Overview Module, Button and Grid View.
		 * MaterialRequestModuleView, OverviewButtonView and OverviewGridView can be/should be loaded async way as:
		 * these views are going to be attached in three different regions! and then process information/load page data.
		 */
		var showOverviewContent = function(parentView) {
			require(['views/materialrequest/common/MaterialRequestModuleView',
			         'views/materialrequest/overview/view/OverviewButtonView',
					 'views/materialrequest/overview/view/OverviewGridView'],
			function(MaterialRequestModuleView, OverviewButtonView, OverviewGridView) {
				// Module View
				var materialRequestModuleView = new MaterialRequestModuleView({
					module : 'materialrequest'
				});
				parentView.controlPane.show(materialRequestModuleView);
				
				// Button View
				var overviewButtonView = new OverviewButtonView();
				parentView.buttonPane.show(overviewButtonView);

				// Grid View
				var overviewGridView = new OverviewGridView({
					module : module,
					collection : materialRequestCollection
				});
				overviewGridView.on('show', function() {
					processOverviewInfo();
				}, this);
				parentView.gridPane.show(overviewGridView);
			});
		};
		
		/**
		 * Process/Load Material Request Overview Information
		 */
		var processOverviewInfo = function() {
			materialRequestCollection.queryParams.userId = UserHelper.getInstance().getUserId();
			materialRequestCollection.fetch();
		};

		/**
		 * Navigate to Material Request Create Page
		 */
		var navigateToCreateNewMaterialRequest = function() {
			var location = 'materialrequest/overview/createrequest';
			Backbone.history.navigate(location, {
				trigger : true
			});
		};

		/**
		 * Navigate to Material Request Details Page
		 */
		var navigateToMaterialRequestDetails = function(id) {
			var location = 'materialrequest/overview/openrequest/' + id;
			Backbone.history.navigate(location, {
				trigger : true
			});
		};
		
		/**
		 * Copy & Create Material Request
		 */
		var copyAndCreateNewMaterialRequest = function(id) {
			var modelTemp = new GloriaModel();
			modelTemp.fetch({
				url : '/materialrequest/v1/materialrequests/' + id + '/current',
				data : {action : 'copy'},
				success : function(response) {
					var location = 'materialrequest/overview/openrequest/' + response.get('id');
					Backbone.history.navigate(location, {
						trigger : true
					});
				}
			});
		};
		
		Controller.OverviewController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestOverview:show', navigateToMaterialRequestDetails);
			    this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestOverview:copy', copyAndCreateNewMaterialRequest);
			    this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestOverview:create', navigateToCreateNewMaterialRequest);
			},

			control : function(subView) {
				module = subView;
				prepareMaterialRequestOverview.call(this);
			},

			onDestroy : function() {
				module = null;
		        materialRequestCollection = null;
			}
		});
	});

	return Gloria.MaterialRequestApp.Controller.OverviewController;
});
