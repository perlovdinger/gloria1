define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette'
], function(Gloria, $, _, Handlebars, Backbone, Marionette) {

	var defaultView = undefined;

	var showStartpageView = function() {
		Gloria.trigger('showHeader');
		require([ 'views/mobile/startpage/startpageView' ], function(DefaultView) {
			defaultView = new DefaultView();
			Gloria.basicLayout.content.show(defaultView);
		});
	};

	var StartpageController = Marionette.Controller.extend({

		initialize : function() {
			this.initializeListeners();
		},

		initializeListeners : function() {

		},

		control : function() {
			showStartpageView.call(this);
		},

		onDestroy : function() {
			defaultView = null;
		}
	});

	return StartpageController;
});
