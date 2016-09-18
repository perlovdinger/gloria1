require( [ 'utils/core/core.mobile' ], function () {
	require(['utils/base/base.mobile'], function () {
	require(['jquery',         
	         'i18next',
	         'app',
	         'backbone',
	         'router/router.mobile',
	         'errorhandler',
	         'utils/i18nInitializer',	         
	         'utils/scannerhelper/ScannerHelper',	         
	         'views/common/layout/mobile/BasicLayout',
	         'views/common/layout/mobile/BasicModalLayout'
	], function($, i18n, Gloria, Backbone, MainAppRouter, ErrorHandler, i18nInitializer, 
	        ScannerHelper, BasicLayout, BasicModalLayout) {
	
	    Gloria.Attributes.mobile = true;
	    
	    // Adding Application initializers
	    Gloria.on('start', function(options) {
	        // Basic layout-regions
	        var basicLayout = new BasicLayout();
	        Gloria.appMainRegion.show(basicLayout);
	        Gloria.basicLayout = Gloria.appMainRegion.currentView;
	        
	        // Basic Modal layout-regions
	        var basicModalLayout = new BasicModalLayout();
	        Gloria.appModalRegion.show(basicModalLayout);
	        Gloria.basicModalLayout = Gloria.appModalRegion.currentView;
	    });
	    
	    // Set up datepicker
        Gloria.on('before:start', function(options) {
	        require.loadCss('libs/pikaday/pikaday.css');	        
	        require(['utils/pikaday/extension'], function(PikadayExtension) {
	        	PikadayExtension.initialize();
	        });	        
	    });
	    
	    var subModuleLoader = function() {
	        // Initiate or Load sub-app modules/routers
	    	require(['views/mobile/startpage/StartpageApp', 'views/mobile/warehouse/WarehouseApp'], function(StartpageApp, WarehouseApp) {
			    Backbone.history.start();
	        });
	    };
	    
	    Gloria.on('initialized:i18n', function() {
	        // Initiate MainAppRouter
            Gloria.router = new MainAppRouter();
            // Initiate sub-routers
            subModuleLoader();
            //attach Global Error Handlers
            ErrorHandler.handleGlobalErrors();
	    });
	    
		Gloria.on('start', function(options) {		        
		        // Making button disabled and showing "Please wait..." so that user will not be able to click them twice!
		        // This is applicable to all the buttons which are having AJAX calls / REST calls with sever!
		        $(document).ajaxStart(function(e) {
		        	var target = $('button.touchstart');
		        	if(target && target.hasClass('btn')) {
		        		this.target = target;
		        		this.originalText = this.target.html();
		        		this.target.html(i18n.t('Gloria.i18n.general.pleaseWait'));
		        		this.target.addClass('disabled');
		        	}
	            });
		        $(document).ajaxStop(function(e) {
		        	if(this.target) {
		        		this.target.removeClass('touchstart');
		        		this.target.removeClass('disabled');
		        		this.target.html(this.originalText);
		        		this.target = null;
		        	}
		        });
		});
	    
	    Gloria.on('before:start', function() {
	    	// First double check if we really have mobile attributes in LocalStorage
	        try {  // Clean up if required
	            var localVersion = localStorage.getItem('Gloria.Attributes.version');
	            var deviceType = window.localStorage.getItem("Gloria.Attributes.mobile");
	            if(localVersion != Gloria.Attributes.version || deviceType != 1) { // If this version is not same as user's local version
	            	window.localStorage.clear();
	            }
	            window.localStorage.setItem('Gloria.Attributes.version', Gloria.Attributes.version);
	            window.localStorage.setItem('Gloria.Attributes.mobile', 1);
	        } catch(e) {
	            console.log('Unable to clear the cache.');
	        };
	        
			i18nInitializer.initialize('mobile', {
				fallbackLng: Gloria.Attributes.defaultLang
			}, function(err, t) {
		        Gloria.trigger('initialized:i18n');
		    });
		});
	    
	    Gloria.on('before:start', function() {	        
	        // Hide app-messages (if available) from app level
	        Backbone.history.bind("all", function (route, router) {
	        	Gloria.trigger('hideAppMessageView', {unstick: true});
	           Gloria.basicLayout.footer.empty();
	        });
	    });    
	    
	    // initialize scanner for the mobile     
	    Gloria.on('start', function(options) {
	        ScannerHelper.getInstance({autoUpdateClassName: 'scannable'});        
	    });	       
	    
	    $(document).ready(function() {        
	        Gloria.start();     
	    });
	});
	});
});