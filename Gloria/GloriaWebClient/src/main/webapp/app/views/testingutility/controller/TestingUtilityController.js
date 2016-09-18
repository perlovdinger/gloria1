define(['app',
        'jquery',
 		'underscore',
 		'handlebars',
 		'backbone',
 		'marionette'
], function(Gloria, $, _, Handlebars, Backbone, Marionette) {

	Gloria.module('TestingUtilityApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var messageView;

		/**
		 * This method is responsible for building and preparing the testing
		 * utility page layout.
		 */
		var setTestingUtilityPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', null);
			require([ 'views/testingutility/view/MessageView' ], function(MessageView) {
				messageView = new MessageView();
				Gloria.basicLayout.content.show(messageView);
			});
		};
		
		var postMessage = function(requestData) {
			require(['views/testingutility/model/MessageModel'], function(MessageModel) {
				var model = new MessageModel();
				model.set(requestData);
				model.save({}, {
					wait: true,
					success : function(response) {
						Gloria.TestingUtilityApp.trigger('Message:posted', 'success');
					},
					error : function(error) {
						Gloria.TestingUtilityApp.trigger('Message:posted', 'error');
					}
				});
	    	});
		};

		Controller.TestingUtilityController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.TestingUtilityApp, 'Message:post', postMessage);
			},

			control : function() {
				setTestingUtilityPageLayout.call(this);
			},
			
			onDestroy: function() {			    
			    messageView = null;
			}
		});
	});

	return Gloria.TestingUtilityApp.Controller.TestingUtilityController;
});
