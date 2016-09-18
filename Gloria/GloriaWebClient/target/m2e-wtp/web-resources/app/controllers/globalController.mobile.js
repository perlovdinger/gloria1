define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, UserHelper) {

    Gloria.module('MainApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
        
    	var DEFAULT_WAREHOUSE_PREFIX = 'Gloria.User.DefaultWarehouse';
    	
        var showAppHeaderView = function(options) {
        	Gloria.basicLayout.header.empty();      
            require(['views/mobile/common/header/HeaderView'], function(HeaderView) {                
                var headerView = new HeaderView();
                Gloria.basicLayout.header.show(headerView);
                showHeader();
            });
        };
								        
        var showAppControlButtonView = function(buttonOptions) {
			Gloria.basicLayout.footer.empty();
			if(buttonOptions) {
			    require(['views/mobile/common/footer/ButtonBarView'], function(ButtonBarView) {
			        var footerView = new ButtonBarView({
			        	buttons : buttonOptions
			        });
			        Gloria.basicLayout.footer.show(footerView);
			    });
			}
        };

        var showAppMessageView = function(options) {
            require([ 'views/common/message/AppMessageView' ], function(AppMessageView) {
                var appMessageView = new AppMessageView(options);
                Gloria.basicLayout.appMessage.show(appMessageView);
            });
        };
        
        var showExceptionHandler = function(exceptionMessage, exceptionTimeStamp) {
			require(['views/common/exception/ExceptionHandlerView'], function(ExceptionHandlerView) {
				var exceptionHandlerView = new ExceptionHandlerView({
					exceptionMessage : exceptionMessage,
					exceptionTimeStamp : exceptionTimeStamp
				});
				Gloria.basicModalLayout.content.show(exceptionHandlerView);
			});
		};

        var goToPreviousRoute = function() {
            window.history.back();
        };

        var showHeader = function() {
        	if(Backbone.history.fragment) {
        		if (Backbone.history.fragment.indexOf('receive') === 0) {
                	Gloria.trigger('change:title', 'Gloria.i18n.warehouse.receive');
                } else if (Backbone.history.fragment.indexOf('store') === 0) {
                	Gloria.trigger('change:title', 'Gloria.i18n.warehouse.store');
                } else if (Backbone.history.fragment.indexOf('pick') === 0) {
                	Gloria.trigger('change:title', 'Gloria.i18n.warehouse.pick');
                } else if (Backbone.history.fragment.indexOf('ship') === 0) {
                	Gloria.trigger('change:title', 'Gloria.i18n.warehouse.ship');
                } else {
                	Gloria.trigger('change:title', 'Gloria.i18n.gloria');
                }
        	}
        	require([ 'views/mobile/common/header/HeaderView' ], function(HeaderView) {
				var user = UserHelper.getInstance().getUser();
				var headerView = new HeaderView({
					user : user
				});
				headerView.on('show', function() {
					showWarehouseSetting.call(this);
				});
				Gloria.basicLayout.header.show(headerView);
			});
        };
        
    	var showWarehouseSetting = function() {
    	    if(!UserHelper.getInstance().getUser()) return;
	        require(['collections/WarehouseCollection'], function(WarehouseCollection) {
	            var warehouseCollection = new WarehouseCollection();
	            warehouseCollection.url = '/warehouse/v1/users/'+ UserHelper.getInstance().getUserId() +'/warehouses';
	            warehouseCollection.fetch({
	                success : function(event) {
	                	// Set Default Warehouse if nothing is set yet!
	                	var selectedWarehouse = localStorage.getItem(DEFAULT_WAREHOUSE_PREFIX + '.' + UserHelper.getInstance().getUserId());
	                	if(!selectedWarehouse) {
	                		localStorage.setItem(DEFAULT_WAREHOUSE_PREFIX + '.' + UserHelper.getInstance().getUserId(),
	                				(warehouseCollection.size() > 0 ? warehouseCollection.at(0).get('siteId') : null));
	                	}
	                	require(['views/mobile/common/settings/SettingsDropdownView'], function(SettingsDropdownView) {
	                        var settingsView = new SettingsDropdownView();
	                        if(settingsView.settings) {
	                            settingsView.settings.empty();
	                        } 
	                        Gloria.basicLayout.addRegion("settings", "#header-settings");
	                        Gloria.basicLayout.settings.show(settingsView);
	                        Gloria.basicLayout.settings.currentView.setWarehouses(warehouseCollection.toJSON());
	                    });
	                	Gloria.trigger('Warehouse:select', localStorage.getItem(DEFAULT_WAREHOUSE_PREFIX + '.' + UserHelper.getInstance().getUserId()));
	                }
	            });
	        });
    	};

        var showHomePage = function() {
            Backbone.history.navigate("#", true);
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
        
        var onLogout = function() {
            UserHelper.getInstance().logout().then(function() {
            	// Clean Storage : @TODO need to decide which are the thing should be moved to session storage / local storage!
            	sessionStorage.clear(); // Clear all session storage information!            	
                Backbone.history.navigate('login/gloria', true);              
            }, function(xhr, error, errorMsg) {
            	if(xhr && xhr.status === 401) {
    	    		Backbone.history.navigate('login/gloria', true);
    	    	} else {
    	    		Gloria.trigger('showExceptionHandler', errorMsg, new Date());
    	    	}
            });
        };

        Controller.MobileController = Marionette.Controller.extend({

            initialize : function() {
                // add listeners
                this.initializeListeners();                

                /**
                 * hide modal inside ModalLayout Region and remove corresponding event handlers, reset ModalLayout Region.
                 */
                Backbone.history.on('all', function() {
                    if (Gloria.basicModalLayout.content && Gloria.basicModalLayout.content.$el) {
                        Gloria.basicModalLayout.content.$el.find('.modal').modal('hide').off('.modal');
                        Gloria.basicModalLayout.content.reset();
                    }
                }, this);
                
                showAppHeaderView();                
            },

            initializeListeners : function() {                
                this.listenTo(Gloria, 'showAppMessageView', showAppMessageView);
                this.listenTo(Gloria, 'showHomePage', showHomePage);
                this.listenTo(Gloria, 'showHeader', showHeader);
                this.listenTo(Gloria, 'showExceptionHandler', showExceptionHandler);
                this.listenTo(Gloria, 'goToPreviousRoute', goToPreviousRoute);
                this.listenTo(Gloria, 'showAppControlButtonView', showAppControlButtonView);
                this.listenTo(Gloria, 'reloadPage', reloadPage);
                this.listenTo(Gloria, 'logout', onLogout);
            }
        });
    });

    return Gloria.MainApp.Controller.MobileController;
});