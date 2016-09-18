define(['app',
        'jquery',
 		'underscore',
 		'handlebars',
 		'backbone',
 		'marionette',
 		'utils/backbone/GloriaModel'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaModel) {

	Gloria.module('TestingUtilityApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var module;
		var migrationView;

		/** Data Migration **/
		var setDataMigrationPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', null);
			require([ 'views/testingutility/view/MigrationView' ], function(MigrationView) {
				migrationView = new MigrationView({
					module : module
				});
				migrationView.on('show', function() {
					showDataTabs(module);
				}, this);
				Gloria.basicLayout.content.show(migrationView);
			});
		};
		
		var showDataTabs = function(module) {
			switch (module) {
			case 'initdata':
				showInitDataTab();
				break;
			default:
				showLoadDataTab(); // loaddata
				break;
			}
		};
		
		var showInitDataTab = function() {
			require([ 'views/testingutility/view/InitDataView' ], function(InitDataView) {
				var initDataView = new InitDataView();
				migrationView.initdata.show(initDataView);
			});
		}
		
		var showLoadDataTab = function() {
			require([ 'views/testingutility/view/LoadDataView' ], function(LoadDataView) {
				var loadDataView = new LoadDataView();
				migrationView.loaddata.show(loadDataView);
			});
		}
		
		var postData = function() {
			
		};
		
		var initiateData = function(dataType) {
			var thisUrl = '';
			switch (dataType) {
			case 'CleanDB':
				thisUrl = '/testingutility/v1/cleandb';
				break;
			case 'CostCenter':
				thisUrl = '/testingutility/v1/initcostcenters';
				break;
			case 'WBSElements':
				thisUrl = '/testingutility/v1/initwbselements';
				break;
			case 'CarryOvers1':
				thisUrl = '/testingutility/v1/initcarryovers1';
				break;
			case 'CarryOvers2':
				thisUrl = '/testingutility/v1/initcarryovers2';
				break;
			case 'CarryOvers3':
				thisUrl = '/testingutility/v1/initcarryovers3';
				break;
			case 'CarryOvers4':
				thisUrl = '/testingutility/v1/initcarryovers4';
				break;
			case 'CarryOvers5':
				thisUrl = '/testingutility/v1/initcarryovers5';
				break;
			case 'Warehouses':
				thisUrl = '/testingutility/v1/initwarehouses';
				break;
			}
			var thisModel = new GloriaModel();
			thisModel.save({}, {
				url : thisUrl,
				success : function(response) {
					Gloria.TestingUtilityApp.trigger('DataMigration:initiated', 'success');
				},
				error : function(error) {
					Gloria.TestingUtilityApp.trigger('DataMigration:initiated', 'error');
				}
			});
		};
		
		var loadInitiateData = function(URL, ids) {
		    if(URL && ids && ids.length > 0) {
		        URL = URL + '?ids=' + ids.toString();
		        $.ajax({
		            type: 'post',
		            url: URL,
		            global: false,
		            success : function(e, data) {
		                hideError();
		            },
		            fail : function(e, data) {
		                console.log('Failed!');
		            },
		            validationError : showError
            });
		    }
		};
		
		var refreshInitiateDataState = function() {            
                $.ajax({
                    type: 'get',
                    async: false,
                    url: '/testingutility/v1/migrationstatus',
                    success : function(data) {
                        Gloria.TestingUtilityApp.trigger('DataMigration:load:refresh:data', data); 
                    },
                    fail : function(e, data) {
                        console.log('Failed!');
                    	Gloria.TestingUtilityApp.trigger('DataMigration:load:refresh:data', null);
                    },
                    validationError : showError
            });
            
        };		
		
		var hideError = function() {
            Gloria.trigger('hideAppMessageView');
        };

        var showError = function(errorMessage, errors) {
            Gloria.trigger('showAppMessageView', {
                type : 'error',
                message : errorMessage
            });
        };

		Controller.DataMigrationController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.TestingUtilityApp, 'DataMigration:post', postData);
				this.listenTo(Gloria.TestingUtilityApp, 'DataMigration:initiate', initiateData);
				this.listenTo(Gloria.TestingUtilityApp, 'DataMigration:load:initiate', loadInitiateData);
				this.listenTo(Gloria.TestingUtilityApp, 'DataMigration:load:refresh', refreshInitiateDataState);
			},

			control : function(subView) {
				module = subView || 'loaddata'; // Default page loaddata
				setDataMigrationPageLayout.call(this);
			},
			
			onDestroy: function() {
				module = null;
				migrationView = null;
			}
		});
	});

	return Gloria.TestingUtilityApp.Controller.DataMigrationController;
});
