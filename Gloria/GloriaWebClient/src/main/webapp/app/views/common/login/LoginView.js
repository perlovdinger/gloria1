define([ 'app', 
         'jquery', 
         'handlebars', 
         'backbone', 
         'bootstrap',
         'hbs!views/common/login/LoginTemplate' 
], function(Gloria, $, Handlebars, Backbone, Bootstrap, compiledTemplate) {

	Gloria.module('LoginApp.Login.View', function(View, Gloria, Backbone, Marionette, $, _) {
		View.Login = Marionette.ItemView.extend({
			
					template: compiledTemplate,
					
					className : 'modal',
					
					id: 'loginModal',
					
					render : function() {
						var that = this;
						this.$el.html(this.template({
							showPassword: Gloria.Attributes.loginMethod === 'USERID_PASSWORD'
						}));
						this.$el.modal({                                        
		                    show: false                    
		                });
						
						return this;
					},

					events : {
						'click #loginButton' : 'loginClicked'
					},

					loginClicked : function(e) {
						e.preventDefault();
						this.clearSession();
						var username = this.$('#username').val();
						var password = this.$('#password').val();
						this.trigger('form:submit', username, password);
					},
					
					/**
					 * To be called before running the login procedure.
					 * Will clear session variables stored in browser.
					 */
					clearSession : function() {
						sessionStorage.clear();
					},

					onLoginInvalidcredentials : function(e) {
						this.$('#login-wrong-credentials').show();
					},
					
					onShow: function() {						
						this.$el.modal('show');
					},
					
					onDestroy : function() {
		                this.$el.modal('hide');
		                this.$el.off('.modal');
		            }
				});
	});

	return Gloria.LoginApp.Login.View;
});