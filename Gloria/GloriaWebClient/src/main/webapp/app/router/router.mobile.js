/**
 * router.js is mainly used to route the startup page only. Please do not add
 * any more route url's in this file. Those URLs can be configured in
 * corresponding module level routers
 */
define([ 'app', 'marionette', 'utils/marionette/GloriaRouter',
		'controllers/controllerManager', 'controllers/globalController.mobile',
		'utils/UserHelper' ], function(Gloria, Marionette, GloriaRouter,
		ControllerManager, GlobalController, UserHelper) {

	var MainAppRouter = GloriaRouter.extend({

		publicRouter : true,

		routes : {
			// Login application
			'login(/:successUrl)' : 'showLoginPage'
		},

		/**
		 * @param successUrl the url that the user will be rerouted to on successful login
		 */
		showLoginPage : function(successUrl, loginController) {
			if (loginController) {
				if (UserHelper.getInstance().isUserLoggedIn()) {
					Gloria.trigger('showHomePage');
				} else {
					loginController.control(successUrl);
				}
			} else {
				this.controllerManager.getController('loginController', this.showLoginPage, successUrl);
			}
		},

		getGlobalController : function(args, globalController) {
			if (!globalController) {
				this.controllerManager.getController('mobileController', this.getGlobalController, args);
			} else {
				this.globalController = globalController;
			}
		},

		initialize : function() {
			// initialize controllerManager
			this.controllerManager = ControllerManager.getInstance();
			// initialize the mobileController
			this.getGlobalController(null, null);
			_.bindAll(this, 'showLoginPage');
			Gloria.on('401', this.showLoginPage);
		}
	});

	return MainAppRouter;
});
