require(['jquery',
         'i18next',
         'app',
         'backbone',
         'utils/i18nInitializer'         
], function($, i18n, Gloria, Backbone, i18nInitializer) {

    Gloria.Attributes.mobile = false;

    // Adding Application initializers
    Gloria.on('initialized:i18n', function() {
        require(['views/common/layout/desktop/BasicLayout', 'views/common/layout/desktop/BasicModalLayout'
                 ], function(BasicLayout, BasicModalLayout) {
            // Basic layout-regions
            var basicLayout = new BasicLayout();
            Gloria.appMainRegion.show(basicLayout);
            Gloria.basicLayout = Gloria.appMainRegion.currentView;
            
            // Basic Modal layout-regions
            var basicModalLayout = new BasicModalLayout();
            Gloria.appModalRegion.show(basicModalLayout);
            Gloria.basicModalLayout = Gloria.appModalRegion.currentView;
        });
    });
	
	var subModuleLoader = function() {
		// Initiate or Load sub-app modules/routers
		require(['views/common/startpage/StartpageApp','views/warehouse/WarehouseApp', 'views/material/MaterialApp',
                 'views/deliverycontrol/DeliveryControlApp', 'views/procurement/ProcurementApp',
                 'views/materialrequest/MaterialRequestApp', 'views/admin/AdminTeamApp', 'views/reports/ReportsApp',
                 'views/testingutility/TestingUtilityApp','views/applicationstatus/ApplicationStatusApp'],
                 function(StartpageApp, WarehouseApp, MaterialApp, DeliveryControlApp,
                		 ProcurementApp, MaterialRequestApp, AdminTeamApp, ReportsApp, TestingUtilityApp, ApplicationStatusApp) {
		    Backbone.history.start();
        });
	};

    Gloria.on('initialized:i18n', function() {
        require(['router/router', 'errorhandler'], function(MainAppRouter, ErrorHandler) {
    		// Initiate MainAppRouter
    		Gloria.router = new MainAppRouter();
    		// Initiate sub-routers
    		subModuleLoader();
    		//attach Global Error Handlers
            ErrorHandler.handleGlobalErrors();
        });
	});
	
	Gloria.on('start', function(options) {	        
	        // Making button disabled and showing "Please wait..." so that user will not be able to click them twice!
	        // This is applicable to all the buttons which are having AJAX calls / REST calls with sever!
	        $(document).ajaxStart(function(e) {
	        	var target = e.currentTarget.activeElement && e.currentTarget.activeElement.type == 'button' ?
	        			$(e.currentTarget.activeElement) : $('a:hover');
	        	if(target && target.hasClass('btn')) {
	        		this.target = target;
	        		this.originalText = this.target.html();
	        		this.target.html(i18n.t('Gloria.i18n.general.pleaseWait'));
	        		this.target.addClass('disabled');
	        	}
            });
	        $(document).ajaxStop(function(e) {
	        	if(this.target) {
	        		this.target.removeClass('disabled');
	        		this.target.html(this.originalText);
	        		this.target = null;
	        	}
	        });
	});

	/**
	 * Load styling unique for the desktop application
	 */
	Gloria.on('before:start', function() {
	    var head = document.getElementsByTagName("head")[0];
        
	    var link = document.createElement("link");
        link.type = "text/css";
        link.rel = "stylesheet";
        link.href = require.toUrl("styles/gloriaDesktop.css");
        link.media = "all and (min-width: 481px)";
        head.appendChild(link);
        
        var link2 = link.cloneNode(false);
        link2.href = require.toUrl("styles/print.css");
        link2.media = "print";
        head.appendChild(link2);
	});

	/**
	 * i18n must always (always) be initialized first
	 */
	Gloria.on('before:start', function() {
		// First double check if we really have desktop attributes in LocalStorage
        try {  // Clean up if required
            var localVersion = localStorage.getItem('Gloria.Attributes.version');
            var deviceType = window.localStorage.getItem("Gloria.Attributes.mobile");
            if(localVersion != Gloria.Attributes.version || deviceType != 0) { // If this version is not same as user's local version
            	window.localStorage.clear();
            }
            window.localStorage.setItem('Gloria.Attributes.version', Gloria.Attributes.version);
            window.localStorage.setItem('Gloria.Attributes.mobile', 0);
        } catch(e) {
            console.log('Unable to clear the cache.');
        };
        
		i18nInitializer.initialize('desktop', {
			fallbackLng: Gloria.Attributes.defaultLang
		}, function(err, t) {
    		Gloria.trigger('initialized:i18n');
    	});
	});
	
	Gloria.on('before:start', function() {        
        // Hide app-messages (if available) from app level
        Backbone.history.bind("all", function (route, router) {
           Gloria.trigger('hideAppMessageView', {unstick: true});
           Gloria.trigger('reset:modellayout');
        });
    });
	
	 Gloria.on('initialized:locale', function(options) {
        require(['datepicker'], function(datepicker) {
            /**
             * Called when datepicker is loaded - load localization and default values
             */        
            var locale = window.sessionStorage.getItem('Gloria.Attributes.locale');
            var language = window.sessionStorage.getItem('Gloria.Attributes.language');
            $.fn.datepicker.defaults.autoclose = true;
            $.fn.datepicker.defaults.format = i18n.t('Gloria.i18n.datepickerformat');
            $.fn.datepicker.defaults.weekStart = i18n.t('Gloria.i18n.weekStart');
            // Note that the localization is not the same in desktop and in mobile
            $.fn.datepicker.dates[locale || language] = i18n.t('Gloria.i18n.bootstrapdatepicker', {returnObjects : true });
            $.fn.datepicker.defaults.language = locale || language;      
            
            $.fn.datepicker.defaults.orientation = 'bottom';
        });	            
    });

	$(document).ready(function() {
		Gloria.start();		
	});

});
