define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, UserHelper) {

	Gloria.module('MainApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var defaultView = undefined;
		var defaultTabModule = undefined;
		
		var showStartpageView = function() {
			// remove header and breadcrumb if any
			Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.header.empty();
			Gloria.basicLayout.breadcrumb.empty();
			Gloria.trigger('showHeader');
			require([ 'views/common/startpage/startpageView' ], function(DefaultView) {
				var hasRolePROCURE_INTERNAL = false;
				var userRoles = UserHelper.getInstance().getUserRoles();
	            _.each(userRoles, function(ur) {
					if(ur.roleName === 'PROCURE-INTERNAL') { // Or PROCURE_INTERNAL ?
						hasRolePROCURE_INTERNAL = true;
					}
				});

				defaultView = new DefaultView({
					module : defaultTabModule,
					isIP: hasRolePROCURE_INTERNAL
				});
				Gloria.basicLayout.content.show(defaultView);
			});
		};
		
		Controller.StartpageController = Marionette.Controller.extend({
		
			initialize : function() {
				this.initializeListeners();
			},
		
			initializeListeners : function() {
				
			},
		
			control : function(module) {
				defaultTabModule = module;
				showStartpageView.call(this);
			},
		
			onDestroy : function() {
				defaultView = null;
				defaultTabModule = null;
			}
		});

	});
	
	return Gloria.MainApp.Controller.StartpageController;
});
