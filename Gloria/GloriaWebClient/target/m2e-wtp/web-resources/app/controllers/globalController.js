define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'localstorage/breadcrumbStorage',
        'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, LocalStorageStore, UserHelper) {

	Gloria.module('MainApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
	
		var showBreadcrumbView = function(options) {
			require([ 'views/common/breadcrumb/BreadcrumbView' ], function(BreadcrumbView) {
			    var breadcrumbView = new BreadcrumbView();			    
			    if(Gloria.basicLayout.breadcrumb.currentView) {
			        Gloria.basicLayout.breadcrumb.currentView.stopListening();
			    }
			    var pageIds = Backbone.history.fragment.split('/');
				// remove id because it does not map to a breadcrumb
				if (options && pageIds && pageIds.length > 0) {
					pageIds.splice(pageIds.length - 1, 1);
				}
				LocalStorageStore.getInstance().getBreadCrumbsForPageIds(pageIds, function(breadCrumbs) {
					var viewOptions = {
						breadcrumbs : breadCrumbs,
						i18nOptions : options
					};
					breadcrumbView.setOptions(viewOptions);
					Gloria.basicLayout.breadcrumb.show(breadcrumbView);
					Gloria.trigger('showHeader');
				});
			});
		};

		var showAppMessageView = function(options) {
			require([ 'views/common/message/AppMessageView' ], function(AppMessageView) {
				var appMessageView = new AppMessageView(options);
				Gloria.basicLayout.appMessage.show(appMessageView);
			});
		};

		var showAbout = function() {
			require([ 'views/common/startpage/AboutView' ], function(AboutView) {
				var aboutView = new AboutView({id: 'aboutDialog'});
				Gloria.basicModalLayout.content.show(aboutView);
			});
		};
		
		var showPrintView = function(partId) {
			require([ 'views/common/print/PrintView' ], function(PrintView) {
				new PrintView().render(partId);
			});
		};

		var showExceptionHandler = function(exceptionMessage, exceptionTimeStamp) {
			require(['views/common/exception/ExceptionHandlerView'], function(ExceptionHandlerView) {
				var exceptionHandlerView = new ExceptionHandlerView({
					exceptionMessage : exceptionMessage,
					exceptionTimeStamp : exceptionTimeStamp
				});
				Gloria.basicModalLayout.closeAndReset();
				Gloria.basicModalLayout.content.show(exceptionHandlerView);
			});
		};

		var goToPreviousRoute = function() {		    
		    window.history.back();		    
		};
		
		var closeModalLayout = function() {
		    if(Gloria && Gloria.basicModalLayout) {
		        Gloria.basicModalLayout.closeAndReset();
		    }
		};

		var showHeader = function() {
		    var moduleName;
			if (Backbone.history.fragment.indexOf('material/') === 0) {				
				moduleName = 'MaterialApp';
			} else if (Backbone.history.fragment.indexOf('deliverycontrol/') === 0) {
			    moduleName = 'DeliveryControlApp';			
			} else if (Backbone.history.fragment.indexOf('warehouse/') === 0) {				
				moduleName = 'WarehouseApp';  
			} else if (Backbone.history.fragment.indexOf('procurement/') === 0) {				
				moduleName = 'ProcurementApp'; 
			} else if (Backbone.history.fragment.indexOf('materialrequest') === 0) {				
				moduleName = 'MaterialRequestApp'; 
			} else if (Backbone.history.fragment.indexOf('testingutility/') === 0) {				
				moduleName = 'TestingUtilityApp';
			}else if (Backbone.history.fragment.indexOf('admin/') === 0) {				
				moduleName = 'AdminTeamApp';
			} else {
			    moduleName = 'StartpageApp';
			}
			require([ 'views/common/pageHeader/PageHeaderView' ], function(PageHeaderView) {
			    // Check to initialize and show the header only when switching between modules.
			    if(!Gloria.basicLayout.header.currentView ||
			            Gloria.basicLayout.header.currentView.moduleName != moduleName) {
			        var pageHeaderView = new PageHeaderView({
			            'moduleName': moduleName
			        });
			        pageHeaderView.on('show', function() {
			        	setSettingsPageLayout.call(this);
					});
			        Gloria.basicLayout.header.show(pageHeaderView);
			    }
            });
		};

		var showHomePage = function() {
			Backbone.history.navigate("#", true);
		};
		
		var pageNotFound = function() {
			Gloria.trigger('showHeader');
			Gloria.trigger('showBreadcrumbView', null);
			require([ 'views/common/pagenotfound/PageNotFoundView' ], function(PageNotFoundView) {
		        var pageNotFoundView = new PageNotFoundView();
		        Gloria.basicLayout.content.show(pageNotFoundView);
            });
		};
		
		var print = function(options) {
		    require(['views/common/printerView/PrinterView'], function(PrinterView) {
		        var printerView = new PrinterView(options)
		        Gloria.basicLayout.printer.show(printerView); 
		    });		    
		};
		
		/**
         * This method should not be used in normal cases.
         * It reloads the current route.
         */
        var reloadPage = function(options) {
            options || (options = {});
            options.trigger = true;
            options.replace = options.replace || true;
            var fragment = Backbone.history.fragment || '';
            Backbone.history.fragment = null;
            Backbone.history.navigate(fragment, options);
        };
        
        var setSettingsPageLayout = function() {
            if(!UserHelper.getInstance().getUser()) return;        
            require([ 'views/common/settings/SettingsDropdownView'], function(SettingsDropdownView) {
                var settingsView = new SettingsDropdownView();
                if(settingsView.settings) {
                    settingsView.settings.empty();
                } 
                Gloria.basicLayout.addRegion("settings", "#header-settings");
                Gloria.basicLayout.settings.show(settingsView);
                showWarehouseSetting();
            });
    	};
    	
    	var showWarehouseSetting = function() {
    	    if(!UserHelper.getInstance().getUser()) return;
    	    if(UserHelper.getInstance().hasPermission('view', ['WarehouseSettings'])) {
    	    var whSite = UserHelper.getInstance().getDefaultWarehouse();
            require(['collections/WarehouseCollection'], function(WarehouseCollection) {
                var warehouseCollection = new WarehouseCollection();
                warehouseCollection.url = '/warehouse/v1/users/'+ UserHelper.getInstance().getUserId() +'/warehouses';
                warehouseCollection.fetch({                
                    success : function(event) {                    
                        Gloria.basicLayout.settings.currentView.setWarehouses(warehouseCollection.toJSON());                    
                    }
                });
            });
    	    }
    	};
    	
    	var onLogout = function() {
    	    UserHelper.getInstance().logout().then(function() {   
    	    	// Clean Storage : @TODO need to decide which are the thing should be moved to session storage / local storage!
            	sessionStorage.clear(); // Clear all session storage information!            	
            	localStorage.removeItem('Gloria.User.DefaultUserTeam');
    	        Backbone.history.navigate('login/gloria', true);
    	    }, function(xhr, error, errorMsg) {
    	    	if(xhr && xhr.status === 401) {
    	    		Backbone.history.navigate('login/gloria', true);
    	    	} else {
    	    		Gloria.trigger('showExceptionHandler', errorMsg, new Date());
    	    	}
    	    });
    	};
    	
    	var handleUserConfigChange = function() {
			// Cleaning local/session storage related to user config.
    		var currentUser = UserHelper.getInstance().getUserId();
    		window.localStorage.removeItem('Gloria.User.DefaultMCTeam.' + currentUser);
    		window.localStorage.removeItem('Gloria.User.DefaultMC.' + currentUser);
    		window.localStorage.removeItem('Gloria.User.DefaultIPTeam.' + currentUser);
    		window.localStorage.removeItem('Gloria.User.DefaultIP.' + currentUser);
    		window.localStorage.removeItem('Gloria.User.DefaultTeam.' + currentUser);
    		// Add Others...
		};

		Controller.GlobalController = Marionette.Controller.extend({

			initialize : function() {
				// add listeners
				this.initializeListeners();				
			},

			initializeListeners : function() {
				this.listenTo(Gloria, 'showBreadcrumbView',	showBreadcrumbView);
				this.listenTo(Gloria, 'showAppMessageView',	showAppMessageView);
				this.listenTo(Gloria, 'showHomePage', showHomePage);
				this.listenTo(Gloria, 'showAbout', showAbout);
				this.listenTo(Gloria, 'showExceptionHandler', showExceptionHandler);
				this.listenTo(Gloria, 'showPrintView', showPrintView);
				this.listenTo(Gloria, 'goToPreviousRoute', goToPreviousRoute);
				this.listenTo(Gloria, 'reset:modellayout', closeModalLayout);
				this.listenTo(Gloria, 'showHeader', showHeader);
				this.listenTo(Gloria, 'reloadPage', reloadPage);
				this.listenTo(Gloria, 'print', print);
				this.listenTo(Gloria, 'Error:404', pageNotFound);
				this.listenTo(Gloria, 'logout', onLogout);
				this.listenTo(Gloria, 'Admin:User:Setup:changed', handleUserConfigChange);
			},
			
			onDestroy: function() {}
		});
	});

	return Gloria.MainApp.Controller.GlobalController;
});
