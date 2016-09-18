define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'views/admin/collection/UserCollection',
		'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserCollection, UserHelper) {

	Gloria.module('AdminTeamApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
		
		var module;
		var userCollection;
		var adminTeamView;
		
		/**
		 * Prepare Admin Team View.
		 */ 
		var prepareAdminTeamView = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects.
		 */
		var initializeDataSourceObjects = function() {
			userCollection = new UserCollection([], {
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.admin.' + module + 'Grid'
								+ '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : module + 'Grid'
			});
		};

		/**
		 * Set Page Layout.
		 */ 
		var setPageLayout = function() {
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', {itemId : module}); // Do not show tab names in breadcrumb
			var that = this;
			require([ 'views/admin/view/AdminTeamView' ], function(AdminTeamView) {
				adminTeamView = new AdminTeamView({
					module : module
				});
				adminTeamView.on('show', function() {
					showAdminButtonView.call(that, module);
                    showAdminGridView.call(that, module);
				});
				Gloria.basicLayout.content.show(adminTeamView);
			});
		};
		
		/**
		 * Show/Render Admin Button View.
		 */
		var showAdminButtonView = function(module) {
			require(['views/admin/view/AdminButtonView'], function(AdminButtonView) {
				var adminButtonView = new AdminButtonView({
					module : module
				});
				if (adminTeamView.buttonDiv) {
					adminTeamView.buttonDiv.empty();
				}
				
				// Create Button Region Dynamically & show the Button View
				if(adminTeamView.buttonDiv) {
					adminTeamView.buttonDiv.empty();
				}
				var buttonId = '#' + module + 'Button';
				adminTeamView.addRegion('buttonDiv', buttonId);
				adminTeamView.buttonDiv.show(adminButtonView);
			});
		};
		
		/**
		 * Show/Render Admin Grid View depending on the module/tab clicked.
		 */
		var showAdminGridView = function(module) {
			require(['views/admin/view/AdminGridView'], function(AdminGridView) {
				var adminGridView = new AdminGridView({
					module : module,
					collection : userCollection
				});
				adminGridView.on('show', function() {
					processAdminGridInfo(module);
				}, this);
				
				// Create Grid Region Dynamically & show the Grid View
				if(adminTeamView.gridDiv) {
					adminTeamView.gridDiv.empty();
				}
				var gridId = '#' + module + 'Grid';
				adminTeamView.addRegion('gridDiv', gridId);
				adminTeamView.gridDiv.show(adminGridView);
			});
		};
		
		/**
		 * Process Admin Grid Information.
		 */
		var processAdminGridInfo = function(module) {
			switch (module) {
			case 'mc':
				userCollection.queryParams.teamType = 'MATERIAL_CONTROL';
				userCollection.queryParams.userCategoryID = 'MATERIAL_CONTROLLER';
				break;
			case 'dc':
				userCollection.queryParams.teamType = 'DELIVERY_CONTROL';
				userCollection.queryParams.userCategoryID = 'DELIVERY_CONTROLLER';
				break;
			case 'ip':
				userCollection.queryParams.teamType = 'INTERNAL_PROCURE';
				userCollection.queryParams.userCategoryID = 'INTERNAL_PROCURER';
					break;
			default:
				break;
			}
			userCollection.state.currentPage = 1;
			userCollection.fetch({
				reset : true
			});
		};
		
		/**
		 * Show Assign User Pop-up
		 */
		var showAssignUserModalView = function(model) {
			require(['views/admin/view/AssignTeamModalView'], function(AssignTeamModalView) {
				var assignTeamModalView = new AssignTeamModalView({
					module : module,
					model : model
				});
				Gloria.basicModalLayout.content.show(assignTeamModalView);
			});
		};
		
		/**
		 * Save Assign Team
		 */
		var saveAssignTeam = function(collection, userId, teamType) {
			Backbone.sync('update', collection, {
				url: '/user/v1/users/' + userId + '/teams?type=' + teamType,
				type: 'PUT',
				success: function(response) {
					// Refetch the team if the current/logged-in user information has been updated!
					if(userId == UserHelper.getInstance().getUserId()) {
						UserHelper.getInstance().getUserTeams(teamType);
					}
					// Admin:User:Setup:changed event triggered to notify Gloria that the user set up has been changed!
					Gloria.trigger('Admin:User:Setup:changed');
					Gloria.AdminTeamApp.trigger('AdminGrid:clearselection');
					Gloria.AdminTeamApp.trigger('AdminButton:refresh');
					processAdminGridInfo(module);
				}
			});
		};
		
		Controller.AdminTeamController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.AdminTeamApp, 'AdminGrid:AssignUserModal:show', showAssignUserModalView);
				this.listenTo(Gloria.AdminTeamApp, 'AdminGrid:AssignUserModal:save', saveAssignTeam);
			},

			control : function(page) {
				module = page || 'mc';
				prepareAdminTeamView.call(this);
			},

			onDestroy : function() {
				module = null;
				userCollection = null;
				adminTeamView = null;
			}
		});
	});

	return Gloria.AdminTeamApp.Controller.AdminTeamController;
});
