/** 
 * here we will bootstrap Gloria client
 * Require.js allows us to configure shortcut alias
 * We will use require-shim function to wrap in libraries that are not AMD compatible
 */

/**
 * Enable dynamic loading of css. Insert css file before first css file to not overload Gloria specific styling.
 */
require([], function() {
    
    if (window.applicationCache) {
        window.applicationCache.addEventListener('updateready', function(e) {           
            if (window.applicationCache.status == window.applicationCache.UPDATEREADY) {
                // Browser downloaded a new app cache
                window.location.reload();
            }
        }, false);
    }

    var isMobileScreenSize = function() {
        //Please do not remove the below line of code.
        //return window.screen.width <= 480 && window.screen.height <= 800;
       return window.innerWidth <= 480 && window.innerHeight <= 800;
    };
    
    /**
     * Helper to load css dynamically when needed by library
     */
    require.loadCss = function(url) {
        var link = document.createElement("link");
        link.type = "text/css";
        link.rel = "stylesheet";
        link.href = require.toUrl(url);
        link.media = "all";
        var head = document.getElementsByTagName("head")[0];
        head.insertBefore(link, head.getElementsByTagName("link")[0]);
    };
    
    // Start the application
    require.config({
        
        // Time to wait before load timeout. 
        waitSeconds : 0, 

        // Initialize the application with the main.js file!
        deps : [isMobileScreenSize() ? 'main.mobile' : 'main'],

        hbs : {
            templateExtension : ".html"
        },
        paths : {
        	  "backbone": "libs/backbone/backbone",
        	  "backbone-pageable": "libs/backbone.pageable/backbone-pageable",
        	  "backbone.babysitter": "libs/backbone.babysitter/backbone.babysitter",
        	  "backbone.stickit": "libs/backbone.stickit/backbone.stickit",
        	  "backbone.syphon": "libs/backbone.syphon/backbone.syphon",
        	  "backbone.wreqr": "libs/backbone.wreqr/backbone.wreqr",
        	  "backgrid": "libs/backgrid/backgrid",
        	  "backgrid-paginator": "libs/backgrid/extensions/paginator/backgrid-paginator",
        	  "backgrid-select-all": "libs/backgrid/extensions/select-all/backgrid-select-all",
        	  "backgrid-select2-cell": "libs/backgrid/extensions/select2-cell/backgrid-select2-cell",
        	  "bootstrap": "libs/bootstrap/js/bootstrap",
        	  "bootstrap-tagsinput": "libs/bootstrap-tagsinput/bootstrap-tagsinput",
        	  "datepicker": "utils/wrappers/datepicker",
        	  "grid-util": "utils/backgrid/grid-util/grid-util",
        	  "handlebars": "libs/handlebars/handlebars",
        	  "hbs": "libs/require/hbs",
        	  "promise": "libs/require/promise",        	  
        	  "i18next": "libs/i18next/i18next.min",
        	  "jquery": "libs/jquery/jquery",
        	  "jquery-validation": "utils/wrappers/jquery.validate",
        	  "jquery.fileupload": "libs/blueimp-file-upload/jquery.fileupload",
        	  "jquery.ui.widget": "libs/jquery-ui/widget",
        	  "jquery-jqpagination": "libs/jquery.jqpagination/js/jquery.jqpagination",
        	  "marionette": "libs/backbone.marionette/backbone.marionette",
        	  "moment": "libs/moment/moment",
        	  "pikaday": "libs/pikaday/pikaday",
        	  "pikaday.jquery": "libs/pikaday/pikaday.jquery",
        	  "select2": "libs/select2/select2",
        	  "text": "libs/require/text",
        	  "htmlSortable": "libs/html.sortable/html.sortable",
        	  "underscore": "libs/underscore/underscore", // Used for fetching an precompiling templates
        	  "barcodeReader": "utils/scannerhelper/BarcodeReader"
        },
        shim : {            
            'i18next' : ['jquery'],
            'backbone' : {
                exports : 'Backbone',
                deps : ['underscore', 'jquery']
            },
            'backbone-pageable' : {
                deps : ['backbone']
            },            
            'bootstrap' : {
                init : function() {
                    require.loadCss('libs/bootstrap/css/bootstrap.min.css');
                },
                deps : ['jquery']
            },
            'bootstrap-tagsinput' : {
                init : function() {
                    require.loadCss('libs/bootstrap-tagsinput/bootstrap-tagsinput.css');
                },
                deps : ['bootstrap']
            },            
            'backgrid' : {
                exports : 'backgrid',
                deps : ['jquery', 'underscore', 'backbone', 'utils/backgrid/backgridInitializer'],
                init : function($, _, Backbone, BackgridInitializer) {
                    require.loadCss('libs/backgrid/backgrid.min.css');
                    BackgridInitializer.initialize(Backgrid);
                    return Backgrid;
                }
            },
            'grid-util' : {
            	exports : 'grid-util',
                deps : ['backbone', 'backgrid'],
	            init : function() {
	                require.loadCss('utils/backgrid/grid-util/grid-util.css');
	            }
            },
            'backgrid-paginator' : {
                exports : 'backgrid-paginator',
                deps : ['backgrid'],
                init : function() {
                    require.loadCss('libs/backgrid/extensions/paginator/backgrid-paginator.min.css');
                }
            },
            'backgrid-select-all' : {
                exports : 'backgrid-select-all',
                deps : ['backgrid', 'utils/backgrid/gridExtension'],
                init : function(backgrid, GridExtension) {
                    require.loadCss('libs/backgrid/extensions/select-all/backgrid-select-all.min.css');
                    GridExtension.initialize();
                }
            },
            'select2' : {
            	exports : 'select2',
            	deps : ['jquery'],
                init : function() {
                    require.loadCss('libs/select2/select2.css');
                    var origInit = this.Select2.class.abstract.prototype.init;
                    this.Select2.class.abstract.prototype.init = function() {                    	
                    	origInit.apply(this, arguments);
                    	this.opts.element.on('select2-open.select2', function(e) {                    	
                    		$(document).mousedown();
                    		$(document).click();
                    	});
                    };
                }
            },
            'backgrid-select2-cell' : {
            	exports : 'backgrid-select2-all',
                deps : ['backgrid'],
            	init : function() {
                    require.loadCss('libs/backgrid/extensions/select2-cell/backgrid-select2-cell.min.css');
                }
            },
            'jquery.fileupload' : {
                exports : 'jquery.fileupload',
                deps : ['jquery', 'jquery.ui.widget']
            },            
            'backbone.stickit': ['backbone'],
            'marionette' : {
                deps : ['backbone', 'backbone.stickit']
            },            
            'jquery-jqpagination' : {
           	 	init : function() {
                    require.loadCss('libs/jquery.jqpagination/css/jqpagination.css');
                },
                deps : ['jquery']
           },
           'barcodeReader': {
        	   exports: 'BarcodeReader'
           }
        }
    });
});