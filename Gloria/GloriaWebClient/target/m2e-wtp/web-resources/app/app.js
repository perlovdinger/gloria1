//this is the main module of Gloria client
//applications main module should always remain light weight
define(['jquery',
        'i18next',
        'underscore',
        'backbone',
        'marionette',        
        'bootstrap',        
        'utils/backbone/BackboneOverrider',
        'promise!utils/appConfiguration',
        'utils/SyphonHelper'
], function($, i18n, _, Backbone, Marionette, Bootstrap, BackboneOverrider, AppConfiguration, SyphonHelper){
      
	BackboneOverrider.initialize(Backbone);
    Backbone.$.ajaxSetup({ cache: false });    
    
    var Gloria = new Marionette.Application();
    
    // Add Attributes hashlist
    Gloria.Attributes = Gloria.Attributes || {};
    
    Gloria.Attributes.apiBaseURL = '/GloriaUIServices/api';
    
    // Set Application verion
    Gloria.Attributes.version = '${buildNumber}';
    Gloria.Attributes.loginMethod = AppConfiguration.loginMethod;    
    
    // Set default-locale
    Gloria.Attributes.defaultLang = 'en';
    window.sessionStorage.setItem('Gloria.Attributes.locale', 'en');
    
    Gloria.addRegions({
        appMainRegion : '#appMainRegion',
        appModalRegion : '#appModalRegion'
    });
    
    Gloria.Attributes.emulateHTTP = true;
    
    // Add support for Page Not Found / routeNotFound
	(function() {
		var oldLoadUrl = Backbone.History.prototype.loadUrl;
		_.extend(Backbone.History.prototype, {
			loadUrl : function() {
				var matched = oldLoadUrl.apply(this, arguments);
				if (!matched) {
					Gloria.trigger('Error:404', arguments);
				}
				return matched;
			}
		});
	}).call(this);
    
	Gloria.on('start', function(options) {	
	    Backbone.$.ajaxPrefilter(function( options, originalOptions, jqXHR) { 	        
	        if(options && options.prefilter === false) return;
	    	options.url = Gloria.Attributes.apiBaseURL + ( options.url || '');
	    });
	});
	
	// Replace "" with null before sending the ajax request to the server.
	Gloria.on('start', function(options) {	
	    Backbone.$.ajaxPrefilter(function( options, originalOptions, jqXHR) {
	    	options || (options = {});
	    	var data = options.data || {};	
	    	if(_.isString(options.data) && options.type !== "GET") {
	    		data = JSON.parse(options.data || "{}");
	    		_.each(data, function(value, key) {
	    			if(_.isString(value) && value.trim().length == 0) {
	    				data[key] = null;
	    			}
	    		});
	    		options.data = JSON.stringify(data);
	    	}   		
	    });
	});
	
    Gloria.on('start', function(options) {        
	    /**
	     * The mobile device and proxy server does not support HTTP PUT and DELETE method for requests.
	     * This is a fix for solving that problem. Normally it is enough to set Backbone.emulateHTTP = true
	     * but since in Gloria some Rest resources use PUT method to be created and updated, 
	     * the Backbone.sync needs to be overridden like the below.
	     */    
	        if(Gloria.Attributes.emulateHTTP) {
	            Backbone.emulateHTTP = true; 
	            var origBackboneSync = Backbone.sync;
	            Backbone.sync = function(method, model, options) {
	                options || (options = {});
	                var type = options.type && options.type.toUpperCase();
	                if(type == 'PUT') {
	                    method = 'update';
	                    delete options.type;
	                }            
	                if(type == 'DELETE') {
	                    method = 'delete';
	                    delete options.type;
	                }                            
	                return origBackboneSync.call(this, method, model, options);
	            };
	        }        
	});
    
    Gloria.on("before:start", function(){
    	/**
         * Always start with a fresh session storage
         */
    	window.sessionStorage.clear();
    	
        SyphonHelper.initialize();        
    });
    
    Gloria.on("initialized:i18n", function(){
    	require(['utils/handlebars-helper'], function(HandlebarsHelper) {
    		HandlebarsHelper.initialize(); 
    	});    	
    });
        
    Gloria.on('start', function(options){
        // add custom defaults to bootstrap modal
        if($ && $.fn.modal) {
            $.extend( $.fn.modal.Constructor.DEFAULTS, {
                keyboard : false,
                backdrop: 'static'
            });
        }
    });
    
    Gloria.on('start', function(options){   
        //prevent backspace acts as Browser Back button        
        $(document).on("keydown", function (e) {
            if (e.which === 8 && ($(e.target).is("input:checkbox, input:radio") || !$(e.target).is("input, textarea"))) {                
                e.preventDefault();
            }
        });
    });
    
    Gloria.on('initialized:i18n', function(options) {
	    require(['views/common/ajaxoverlay/AjaxOverlayView'], function(AjaxOverlayView) {
	        var spinnerView = new AjaxOverlayView();
	        spinnerView.render();
	        $('body').append(spinnerView.el);
	        $(document).ajaxStart(function(e) {
	        	// Clear error message (if any) on new ajax call
	        	Gloria.trigger('hideAppMessageView');
	        	spinnerView.showLoading();
            });
	        $(document).ajaxStop(function(e) {
	        	spinnerView.hideLoading();
	        });
	    });    
    });
        
    return Gloria;
});
