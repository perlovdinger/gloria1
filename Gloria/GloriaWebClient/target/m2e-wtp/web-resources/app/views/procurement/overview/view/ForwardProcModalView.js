define([ 'app', 'jquery', 'i18next', 'handlebars', 'underscore', 'marionette',
		'bootstrap', 'jquery-validation', 'hbs!views/procurement/overview/view/forward-proc',
		'views/procurement/helper/ProcureTeamMembersSelectorHelper',
		'utils/UserHelper' ], function(Gloria, $, i18n, Handlebars, _,
		Marionette, Bootstrap, Validation, compiledTemplate,
		ProcureTeamMembersSelectorHelper, UserHelper) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone,
			Marionette, $, _) {

		View.ForwardProcModalView = Marionette.ItemView.extend({

			className : 'modal',

			id : 'forwardProcForm',

			events : {
				'change #forwardProc' : 'handleForwardProcChange',
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.models = options.models;
			},
			
			handleForwardProcChange : function(e) {
				var teamName = e.currentTarget.value;
				var userList = [];
				if(teamName) {
					userList = ProcureTeamMembersSelectorHelper.constructInternalProcureUserList(teamName);
				}
				var $forwardProcUser = $('#forwardProcUser');
				$forwardProcUser.empty();
				var ret = '<option value="">' + i18n.t('Gloria.i18n.any') + '</option>';
				_.each(userList, function(user) {
					ret += '<option value="' + user.id + '">' + user.firstName + ' ' + user.lastName + '</option>';
				});
				$forwardProcUser.append(ret);
			},

			handleSaveClick : function(e) {
				if (this.isValidForm()) {
					Gloria.ProcurementApp.trigger('procureline:forward', this.models, this.collectData());
					this.$el.modal('hide');
				}
			},
			
			validator : function() {
				var that = this;
				return this.$el.find('form').validate({
					rules: {	
						'forwardProc': {
							required: true
						}
					},
					messages: {
						'forwardProc': {
							required: i18n.t('errormessages:errors.GLO_ERR_061')
						}
					},
					showErrors: function (errorMap, errorList) {
						that.showErrors(errorList);
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},

			showErrors : function(errorList) {
				Gloria.trigger('showAppMessageView', {
					modal : true,
					type : 'error',
					title : i18n.t('errormessages:general.title'),
					message : errorList
				});
			},

			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},

			collectData : function() {
				return {
					forwardProc : $('#forwardProc option:selected').text(),
					forwardProcUser : $('#forwardProcUser').val()
				};
			},

			render : function() {
				var that = this;
				Handlebars.registerHelper('renderProcureInternalTeamDropDown', function(options) {
					var ret = '';
					$.each(options, function(i, item) {
						ret += '<option value="' + item.name + '">' + item.name + '</option>';
					});
					return ret;
				});
				this.$el.html(compiledTemplate({
					teamList : ProcureTeamMembersSelectorHelper.constructInternalProcureTeamListFlat()
				}));
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
				});
				return this;
			},

			onShow : function() {
				this.$el.modal('show');
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.ProcurementApp.off(null, null, this);
			}
		});
	});

	return Gloria.ProcurementApp.View.ForwardProcModalView;
});
