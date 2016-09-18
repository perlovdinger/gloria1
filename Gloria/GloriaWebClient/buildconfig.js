module.exports = function(options) {
	return {
		baseUrl : options.sourceDir,

		// Destination <dir> should be stored inside target folder, so that
		// everytime maven can handle cleaning resources
		dir : options.destDir,

		optimize : 'uglify2',

		uglify2 : {
			output : {
				beautify : false,
				ascii_only : true,
				quote_keys : true
			},
			compress : {
				drop_console : true
			}
		},

		removeCombined : true,
		preserveLicenseComments : false,
		generateSourceMaps : true,

		//A function that will be called for every write to an optimized bundle
		//of modules. This allows transforms of the content before serialization.
		onBuildWrite : function(moduleName, path, contents) {
			//Always return a value.
			//Add isProduction attribute to config object for production mode.
			//Then it is possible for plugins to determine whether it is production runtime or dev runtime. 
			var regex = new RegExp(/.*config\.js$/);
			if (regex.test(path)) {
				return contents
						.concat('\n\rrequire.config({isProduction:true});');
			} else {
				return contents;
			}
		},

		// r.js and buildconfig.js are part of build process, should be excluded
		// copying to destination <dir>
		// fileExclusionRegExp : /^(libs|(r|buildconfig|min)\.js)$/,
		fileExclusionRegExp : /^(templates|(r|buildconfig)\.js)$/,

		// CSS optimization -> standard
		optimizeCss : 'standard',

		findNestedDependencies : true,

		paths : {
			"backbone" : "libs/backbone/backbone",
			"backbone-pageable" : "libs/backbone.pageable/backbone-pageable",
			"backbone.babysitter" : "libs/backbone.babysitter/backbone.babysitter",
			"backbone.stickit" : "libs/backbone.stickit/backbone.stickit",
			"backbone.syphon" : "libs/backbone.syphon/backbone.syphon",
			"backbone.wreqr" : "libs/backbone.wreqr/backbone.wreqr",
			"backgrid" : "libs/backgrid/backgrid",
			"backgrid-paginator" : "libs/backgrid/extensions/paginator/backgrid-paginator",
			"backgrid-select-all" : "libs/backgrid/extensions/select-all/backgrid-select-all",
			"backgrid-select2-cell" : "libs/backgrid/extensions/select2-cell/backgrid-select2-cell",
			"bootstrap" : "libs/bootstrap/js/bootstrap",
			"bootstrap-tagsinput" : "libs/bootstrap-tagsinput/bootstrap-tagsinput",
			"datepicker" : "utils/wrappers/datepicker",
			"handlebars" : "libs/handlebars/handlebars",
			"hbs" : "libs/require/hbs",
			"promise": "libs/require/promise",
			"i18next" : "libs/i18next/i18next.min",
			"jquery" : "libs/jquery/jquery",
			"jquery-validation": "utils/wrappers/jquery.validate",
			"jquery.fileupload" : "libs/blueimp-file-upload/jquery.fileupload",
			"jquery.ui.widget" : "libs/jquery-ui/widget",
			"jquery-jqpagination": "libs/jquery.jqpagination/js/jquery.jqpagination",
			"marionette" : "libs/backbone.marionette/backbone.marionette",
			"moment" : "libs/moment/moment",
			"pikaday" : "libs/pikaday/pikaday",
			"pikaday.jquery" : "libs/pikaday/pikaday.jquery",
			"select2" : "libs/select2/select2",
			"text" : "libs/require/text",
			"htmlSortable": "libs/html.sortable/html.sortable",
			"underscore" : "libs/underscore/underscore", // Used for fetching an precompiling templates
			"barcodeReader": "utils/scannerhelper/BarcodeReader"
		},

		shim : {
			'jquery' : {
                'exports' : 'jQuery'
            },
            'backbone' : {
                exports : 'Backbone',
                deps : [ 'jquery', 'underscore' ]
            },
            'underscore' : {
                exports : '_'
            },
            'marionette' : {
                deps : [ 'jquery', 'underscore', 'backbone' ],
                exports : 'Marionette'
            },
            'backbone.stickit' : {
                deps : [ 'backbone' ]
            },
            'backbone.wreqr' : {
                deps : [ 'backbone' ]
            },
            'backbone.babysitter' : {
                deps : [ 'backbone' ]
            },        
            'backbone-pageable' : {
                deps : [ 'backbone' ]
            },
            'bootstrap' : {
                exports : 'bootstrap',
                deps : [ 'jquery' ]
            },
            'bootstrap-tagsinput' : {
                init : function() {
                    require.loadCss('libs/bootstrap-tagsinput/bootstrap-tagsinput.css');
                },
                deps : ['bootstrap']
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
		},

		modules : [

				{
					name : 'app',
					include : [ 'app' ],
					exclude : [ 'jquery', 'i18next', 'underscore', 'backbone',
							'marionette', 'bootstrap' ]
				},

				{
					// Module -> Mobile Core
					name : 'utils/core/core.mobile'
				},
				{
					// Module -> Mobile Base
					name : 'utils/base/base.mobile',
					include : [ 'backbone-pageable',
							'utils/backbone/GloriaPageableCollection',
							'backbone.syphon', 'jquery-validation' ],
					exclude : [ 'utils/core/core.mobile' ]
				},
				{
					// Module -> Mobile globalController.mobile
					name : 'controllers/globalController.mobile',
					include : [],
					exclude : [ 'utils/core/core.mobile',
							'utils/base/base.mobile' ]
				},
				{
					// Module -> Mobile StartpageController
					name : 'views/mobile/startpage/StartpageController',
					include : [],
					exclude : [ 'utils/core/core.mobile',
							'utils/base/base.mobile' ]
				},
				{
					// Module -> Mobile ReceiveController
					name : 'views/mobile/warehouse/controller/ReceiveController',
					include : [],
					exclude : [ 'utils/core/core.mobile',
							'utils/base/base.mobile', 'backgrid',
							'text!utils/backgrid/stringHeaderCell.html',
							'utils/backgrid/stringHeaderCell',
							'utils/backgrid/clickableRow' ]
				},
				{
					// Module -> Mobile StoreController
					name : 'views/mobile/warehouse/controller/StoreController',
					include : [],
					exclude : [ 'utils/core/core.mobile',
							'utils/base/base.mobile', 'backgrid',
							'text!utils/backgrid/stringHeaderCell.html',
							'utils/backgrid/stringHeaderCell',
							'utils/backgrid/clickableRow' ]
				},
				{
					// Module -> Mobile PickController
					name : 'views/mobile/warehouse/controller/PickController',
					include : [],
					exclude : [ 'utils/core/core.mobile',
							'utils/base/base.mobile', 'backgrid',
							'text!utils/backgrid/stringHeaderCell.html',
							'utils/backgrid/stringHeaderCell',
							'utils/backgrid/clickableRow' ]
				},
				{
					// Module -> Mobile MoveController
					name : 'views/mobile/warehouse/controller/MoveController',
					include : [],
					exclude : [ 'utils/core/core.mobile',
							'utils/base/base.mobile' ]
				} ]
	};
};