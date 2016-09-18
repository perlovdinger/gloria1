define(['app',
	    'jquery',
	    'handlebars',
	    'backbone',
	    'bootstrap',
	    'utils/UserHelper',
	    'views/common/login/LoginView'
], function(Gloria, $, Handlebars, Backbone, Bootstrap, UserHelper, View) {
	
	Gloria.module('LoginApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var setEnv = function(username, password) {
			var options = {};
			if(Gloria.Attributes.loginMethod === 'USERID_PASSWORD') {
				options.url = '/user/v1/login';
				options.data = {
	        		userId: username,
	        		password: password
	        	};
			} else {	
				options.url = '/usertest/v1/login';
				options.data = {userId: username};
			}
			return options;
		};
		
		var showLogin = function(loginUrl) {				
			var that = this;
			this.forwardTo = loginUrl;
			this.view = new View.Login();
			this.view.on('form:submit', function(username, password) {
				login.call(that, username, password);
			});
			Gloria.basicLayout.layoutReset();
			Gloria.basicModalLayout.content.show(this.view);
		};
		
		var login = function(username, password) {
			var that = this;
	        $.ajax(_.extend(setEnv(username, password), {
	        	type : 'GET',		        	
	         	global : false,
	         	cache: false,
	        	success : function (data) {
	        		var redirectUrl = Backbone.history.fragment.indexOf('login') > -1 ? '#' 
	        				: Backbone.history.fragment;
	        		Backbone.history.navigate(redirectUrl, {
	        			replace : true,
	        			trigger : false
	        		});
	        		Backbone.history.loadUrl(redirectUrl);
	            },
	            error : function () {
	            	that.view.triggerMethod('login:invalidcredentials');
	            	return false; // Prevent default
	            }
	        }));
	        return false;
		};

		Controller.LoginController = Marionette.Controller.extend({
	       
	    	initialize : function() {
	            this.initializeListeners();
	        },
	        
	        initializeListeners : function() {
	            
	        },
	
	        control: function(loginUrl) {
	        	showLogin(loginUrl);
	        },
	        
	        onDestroy : function() {

	        }
	    });
	});
	
	return Gloria.LoginApp.Controller.LoginController;	
});
