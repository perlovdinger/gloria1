define(['app',
        'jquery',
        'underscore',
        'backbone',
        'handlebars',
		'marionette',
		'i18next',
		'utils/UserHelper',
		'utils/backbone/GloriaCollection',
		'utils/backbone/GloriaPageableCollection',
		'collections/ProcureLineCollection', 
		'collections/ProcureRequestLineCollection',
		'collections/ProcureRequestHeaderCollection',
		'collections/ChangeIdCollection'
], function(Gloria, $, _, Backbone, Handlebars, Marionette, i18n, UserHelper, Collection, GloriaPageableCollection,
        ProcureLineCollection, ProcureRequestLineCollection, ProcureRequestHeaderCollection, ChangeIdCollection) {

	Gloria.module('ProcurementApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var module;
		var procureLineCollection;
		var procureRequestLineCollection;
		var changeIdCollection;
		var procureRequestCollection;
		var procurementOverviewView;
		var overviewButtonView;
		var overviewGridView;
		var modificationGridView;
		var unassignedRequestGridView;
		var internalProcurementGridView;
		var filteredRequestHeaderCollection;
		var hasRolePROCURE_INTERNAL = false;
		var hasRoleDELIVERY = false;

		/**
		 * Prepare Overview Request. Initialize Data Source Objects which are going to be used as
		 * data transfer objects and also set the page layout.
		 */
		var prepareOverviewRequest = function() {
			var userRoles = UserHelper.getInstance().getUserRoles();
            _.each(userRoles, function(ur) {
				if(ur.roleName === 'PROCURE-INTERNAL') {
					hasRolePROCURE_INTERNAL = true;
				}
				if(ur.roleName === 'DELIVERY') {
					hasRoleDELIVERY = true;
				}
			});
			initializeDataSourceObjects();
			setPageLayout();
		};

		/**
		 * Initialize Data Source Objects. These objects are going to be used by the page/components.
		 */
		var initializeDataSourceObjects = function() {
			procureLineCollection = new ProcureLineCollection([], {
			    queryParams: {
                    type: hasRolePROCURE_INTERNAL ? 'INTERNAL_PROCURE' : 'MATERIAL_CONTROL'
                },
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.procurement.' + module
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : module
			});
			procureRequestLineCollection = new ProcureRequestLineCollection([], {
			    queryParams: {
			        type: hasRolePROCURE_INTERNAL ? 'INTERNAL_PROCURE' : 'MATERIAL_CONTROL'
			    },
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.procurement.' + module
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : module
			});
			changeIdCollection = new ChangeIdCollection([], {
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.procurement.' + module
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},				
				filterKey : module
			});
			changeIdCollection.queryParams.status = changeIdCollection.getFilterStorageValue('status') || 'WAIT_CONFIRM,CANCEL_WAIT'; // Default
		};

		/**
		 * Set Page Layout. ProcurementOverviewView is the main Layout which adds two regions: moduleInfo & gridInfo to the page,
		 * so that the respective views can be attached later on!
		 */
		var setPageLayout = function() {
	        var that = this;
	        Gloria.basicModalLayout.closeAndReset();
	        Gloria.trigger('showBreadcrumbView', null);			
		    Gloria.basicLayout.content.reset();	            
            require(['views/procurement/overview/view/ProcurementOverviewView'], function(ProcurementOverviewView) {
	            procurementOverviewView = new ProcurementOverviewView({
	            	module: module,
	            	hasRolePI: hasRolePROCURE_INTERNAL
	            });
		        procurementOverviewView.on('show', function() {
		        	showModuleView();
		        	showOverviewButtonView.call(that, module, hasRolePROCURE_INTERNAL);
                    // Do not show User selector for inbox & internalProcurement
                    if(module != 'inbox' && module != 'internalProcurement') {
                        processProcureTeamMembersView({}, function() {
                        	showOverviewTabs.call(that, module);
						});
                    } else {
               /* this was added to set the team names in the local Storage for displaying the Changetab mark when the control comes to 
                * Inbox Module*/     	
            			require(['views/procurement/helper/ProcureTeamMembersSelectorHelper'], 
            					function(ProcureTeamMembersSelectorHelper) {
            				var currentUser = UserHelper.getInstance().getUserId();
            				if(hasRolePROCURE_INTERNAL) {
            					userId = window.localStorage.getItem('Gloria.User.DefaultIP.' + currentUser);
        				    	userTeam = window.localStorage.getItem('Gloria.User.DefaultIPTeam.' + currentUser);
        				    	if(userId == null || userTeam == null ) {
            			        ProcureTeamMembersSelectorHelper.constructInternalProcureTeamList({}); }
            			    } else {
            			    	userId = window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser);
        				    	userTeam = window.localStorage.getItem('Gloria.User.DefaultMCTeam.' + currentUser);	
        				    	if(userId == null || userTeam == null ) {
            			    	ProcureTeamMembersSelectorHelper.constructProcureTeamMembersList({});}
            			    }
            			});
                    	showOverviewTabs.call(that, module);
                    }
				});
		        Gloria.basicLayout.content.show(procurementOverviewView);
            });
		};
		
		/**
		 * Show Module View.
		 */
		var showModuleView = function() {
			require(['views/procurement/common/ProcurementModuleView'], function(ProcurementModuleView) {
				var procurementModuleView = new ProcurementModuleView({
                    module : 'overview'
                });
                procurementOverviewView.moduleInfo.show(procurementModuleView);
			});
		};
		
		/**
		 * Process Procure Team Members View
		 */
		var processProcureTeamMembersView = function(options, callback) {
			require(['views/procurement/helper/ProcureTeamMembersSelectorHelper'], function(ProcureTeamMembersSelectorHelper) {
				if(hasRolePROCURE_INTERNAL) {
			        ProcureTeamMembersSelectorHelper.constructInternalProcureTeamList(options).then(function(transformedJSON) {
			        	showProcureTeamMembersView(transformedJSON);
			        	callback && callback();
			        });
			    } else {
			    	ProcureTeamMembersSelectorHelper.constructProcureTeamMembersList(options).then(function(transformedJSON) {
			        	showProcureTeamMembersView(transformedJSON);
			        	callback && callback();
			        });
			    }
			});
		};
		
		/**
		 * Show Procure Team Members View
		 */
		var showProcureTeamMembersView = function(usersJSON) {
			require([ 'views/procurement/common/user/UserSelector' ], function(UserSelector) {
			    var selected, userId, userTeam;		
			    var currentUser = UserHelper.getInstance().getUserId();
			    try {			    
			        if(hasRolePROCURE_INTERNAL) {
			        	userId = window.localStorage.getItem('Gloria.User.DefaultIP.' + currentUser);
				    	userTeam = window.localStorage.getItem('Gloria.User.DefaultIPTeam.' + currentUser);
	                } else{
	                	userId = window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser);
				    	userTeam = window.localStorage.getItem('Gloria.User.DefaultMCTeam.' + currentUser);				        
	                }
			        selected =  userId + ';' + userTeam;
			    } catch(e) {
			        selected = '';
			    }			    
        		var userSelector = new UserSelector({	        		    
					user : usersJSON,
					selected: selected
				});
        		procurementOverviewView.userInfo.show(userSelector);
			});
		};

		/**
		 * Show/Render OverviewButtonView
		 */
		var showOverviewButtonView = function(subModule, hasRolePI) {
			require(['views/procurement/overview/view/OverviewButtonView'], function(OverviewButtonView) {
				overviewButtonView = new OverviewButtonView({
					module : subModule,
					hasRolePI : hasRolePI
				});
				if (procurementOverviewView.buttonDiv) {
					procurementOverviewView.buttonDiv.empty();
				}
				var buttonId = '#' + subModule + 'Button';
				procurementOverviewView.addRegion('buttonDiv', buttonId);
				procurementOverviewView.buttonDiv.show(overviewButtonView);
			});
		};
		
		var markChangeTab = function() {
		    var checkChangeTabItemCollection = new GloriaPageableCollection();
            checkChangeTabItemCollection.queryParams.status = 'WAIT_CONFIRM,CANCEL_WAIT'; // New Items
            var currentUser = UserHelper.getInstance().getUserId();
            checkChangeTabItemCollection.queryParams.assignedMaterialControllerId =  window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser);
            checkChangeTabItemCollection.queryParams.assignedMaterialControllerTeam =  window.localStorage.getItem('Gloria.User.DefaultMCTeam.' + currentUser);               
            checkChangeTabItemCollection.fetch({
                url : '/procurement/v1/changeids',
                success : function(response) {
                    if(response.length > 0) {
                        Gloria.ProcurementApp.trigger('Procurement:ChangeTab:showChangeMark');
                    }
                }
            });
        };

		/**
		 * Show/Render Overview Grid View depending on the subModule/tab clicked!
		 */
		var showOverviewTabs = function(subModule) {
		    markChangeTab();
			switch (subModule) {
			case 'inbox':
				showUnassignedRequestGridView(subModule);
				break;
			case 'procured':
				showProcuredGridView(subModule);
				break;
			case 'modification':
				showModificationGridView(subModule);
				break;
			case 'change':
				showChangeGridView(subModule);
				break;
			case 'internalProcurement':
				showInternalProcurementGridView(subModule);
				break;
			default:
				showOverviewGridView(subModule);
				break;
			}
		};

		/**
		 * Show/Render Grid View
		 */
		var showProcureOverview = function(options) {
			if(procurementOverviewView.gridDiv) {
				procurementOverviewView.gridDiv.empty();
			}
			var gridId = '#' + options.subModule + 'Grid';
			procurementOverviewView.addRegion('gridDiv', gridId);
			procurementOverviewView.gridDiv.show(options.grid);
		};
		
		/**
		 * Show/Render ProcuredGridView
		 */
		var showProcuredGridView = function(subModule) {
			require(['views/procurement/overview/view/ProcuredGridView'], function(ProcuredGridView) {
				procuredGridView = new ProcuredGridView({
					collection : procureLineCollection,
	            	hasRolePI: hasRolePROCURE_INTERNAL
				});
				var procureResponsibility = 'PROCURER,BUILDSITE';
				var status = procureLineCollection.getFilterStorageValue('status') || 'PROCURED,PLACED,RECEIVED_PARTLY,RECEIVED';
				procuredGridView.on('show', function() {
					processOverviewGridInfo(procureResponsibility, status);
				}, this);
				showProcureOverview({
					subModule : subModule,
					grid : procuredGridView
				});
			});
		};
		
		/**
		 * Show/Render ChangeGridView
		 */
		var showChangeGridView = function(subModule) {
			require(['views/procurement/overview/view/ChangeGridView'], function(ChangeGridView) {
				changeGridView = new ChangeGridView({
					collection : changeIdCollection
				});
				changeGridView.on('show', function() {
					processChangeIdGridInfo();
				}, this);
				showProcureOverview({
					subModule : subModule,
					grid : changeGridView
				});
			});
		};
		
		/**
		 * Show/Render Internal Procurement
		 */
		var showInternalProcurementGridView = function(subModule) {
			require(['views/procurement/overview/view/InternalProcurementGridView'], function(InternalProcurementGridView) {
				var procureResponsibility = 'PROCURER';
				var status = 'WAIT_TO_PROCURE';
				var procureForwardedId = UserHelper.getInstance().getUserId();
				internalProcurementGridView = new InternalProcurementGridView({
					collection : procureLineCollection
				});
				internalProcurementGridView.on('show', function() {
					processInternalProcurement(procureResponsibility, status, procureForwardedId);
				}, this);
				showProcureOverview({
					subModule : subModule,
					grid : internalProcurementGridView
				});
			});
		};
		
		/**
		 * Process Internal Procurement
		 */
		var processInternalProcurement = function(procureResponsibility, status, procureForwardedId) {
			procureLineCollection.queryParams.procureResponsibility = procureResponsibility;
			procureLineCollection.queryParams.status = status;
			procureLineCollection.queryParams.procureForwardedTeam = UserHelper.getInstance().getUserAttribute('internalProcureTeam');
			procureLineCollection.queryParams.procureType = 'INTERNAL,INTERNAL_FROM_STOCK';
			procureLineCollection.state.currentPage = 1;
			processProcureLineCollection({
				cache : false
			});
		};
		
		/**
		 * Show/Render OverviewGridView
		 */
		var showOverviewGridView = function(subModule) {
			subModule || (subModule = 'TOPROCURE');
			require(['views/procurement/overview/view/OverviewGridView'], function(OverviewGridView) {
				var isHidden = false;
				var procureResponsibility = "";
				var status = "";
				if (subModule.toUpperCase() == 'ONBUILDSITE') {
					isHidden = true;
					procureResponsibility = 'BUILDSITE';
					status = 'WAIT_TO_PROCURE';
				} else if (subModule.toUpperCase() == 'TOPROCURE') {
					procureResponsibility = 'PROCURER';
					status = 'WAIT_TO_PROCURE';
				} else if (subModule.toUpperCase() == 'PROCURED') {
					procureResponsibility = 'PROCURER';
					status = 'PROCURED,PLACED,RECEIVED,RECEIVED_PARTLY';
				}

				overviewGridView = new OverviewGridView({
					collection : procureLineCollection,
					module : module,
					filter : subModule,
					isSourceColumnHidden : isHidden,
	            	hasRolePI: hasRolePROCURE_INTERNAL
				});
				overviewGridView.on('show', function() {
					processOverviewGridInfo(procureResponsibility, status);
				}, this);

				showProcureOverview({
					subModule : subModule,
					grid : overviewGridView
				});
			});
		};

		/**
		 * Process Overview Grid Information. This will be ONLY responsible to create/update data
		 * objects (e.g. procureLineCollection) required for page.
		 */
		var processOverviewGridInfo = function(procureResponsibility, status, userId, userTeam) {
			procureLineCollection.trigger('QueryParams:reset', ['procureResponsibility', 'status', 'procureForwardedId',
			                                                    'procureForwardedTeam', 'assignedMaterialControllerId',
			                                                    'assignedMaterialControllerTeam']);
			procureLineCollection.queryParams.procureResponsibility = procureResponsibility;
			procureLineCollection.queryParams.status = status;
			var currentUser = UserHelper.getInstance().getUserId();
			if(hasRolePROCURE_INTERNAL) {
				procureLineCollection.queryParams.procureForwardedId = window.localStorage.getItem('Gloria.User.DefaultIP.' + currentUser) ||userId;
				procureLineCollection.queryParams.procureForwardedTeam = window.localStorage.getItem('Gloria.User.DefaultIPTeam.' + currentUser) || userTeam;                
			} else {
				procureLineCollection.queryParams.assignedMaterialControllerId =   window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser) || userId;				
				procureLineCollection.queryParams.assignedMaterialControllerTeam =  window.localStorage.getItem('Gloria.User.DefaultMCTeam.' + currentUser) || userTeam;				
			}
			procureLineCollection.state.currentPage = 1;
			procureLineCollection.state.sortKey = 'procureFailureDate';
			procureLineCollection.state.order = 1;
			processProcureLineCollection({
				cache : false,
				highlight : true
			});
		};

		/**
		 * Process ChangeId Grid Info
		 */ 
		var processChangeIdGridInfo = function(userId, userTeam) {		
		    changeIdCollection.trigger('QueryParams:reset', ['assignedMaterialControllerId', 'assignedMaterialControllerTeam']);
			var currentUser = UserHelper.getInstance().getUserId();
			changeIdCollection.queryParams.assignedMaterialControllerId =  window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser) || userId;
			changeIdCollection.queryParams.assignedMaterialControllerTeam =  window.localStorage.getItem('Gloria.User.DefaultMCTeam.' + currentUser) || userTeam;			    
			changeIdCollection.fetch({
				cache : false
			});
		};
		
		/**
		 * Show/Render Modification Grid View
		 */
		var showModificationGridView = function(subModule) {
			require(['views/procurement/overview/view/ModificationGridView'], function(ModificationGridView) {
				modificationGridView = new ModificationGridView({
					collection : procureRequestLineCollection
				});
				modificationGridView.on('show', function() {
					processModificationGridInfo();
				}, this);
				showProcureOverview({
					subModule : subModule,
					grid : modificationGridView
				});
			});
		};
		
		/**
		 * Show Unassigned Request Grid View
		 */
		var showUnassignedRequestGridView = function(subModule){
			prepareUnassignedRequest(subModule);
		};
		
		/**
		 * Prepare Unassigned Request.
		 * Initialize Data Source Objects which are going to be used as data transfer objects
		 * and set page layout.
		 */ 
		var prepareUnassignedRequest = function(subModule) {
			// Initialize Data Source Objects
			initializeUnassignedRequestDataSourceObjects();				
			showUnassignedProcurementContent(subModule);
		};
		
		/**
		 * Initialize Data Source Objects.
		 * These objects are going to be used by the page/components.
		 */
		var initializeUnassignedRequestDataSourceObjects = function() {
			procureRequestCollection = new ProcureRequestHeaderCollection([], {
				state : {
					pageSize : function() {
						var object = JSON.parse(window.localStorage.getItem('Gloria.procurement.' + module
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : module
			});
			procureRequestCollection.defaultSort = {
				attributeName: 'assignedMaterialControllerId',
				order: '1'
			};
		};
		
		/**
		 * Show/Render Procurement Module and Grid View.
		 * UnassignedRequestGridView can be/should be loaded async way as:
		 * these views are going to be attached in two different regions! and then process information/page data.
		 */
		var showUnassignedProcurementContent = function(subModule) {
			require(['views/procurement/overview/view/UnassignedRequestGridView'],
			function( UnassignedRequestGridView) {
				unassignedRequestGridView = new UnassignedRequestGridView({
					collection : procureRequestCollection
				});
				unassignedRequestGridView.on('show', function() {
					processUnassignedProcurementInfo();
				}, this);
				// Attach to gridInfo region
				showProcureOverview({
					subModule : subModule,
					grid : unassignedRequestGridView
				});
			});
		};

		/**
		 * Process Unassigned Procurement Information.
		 * This will be ONLY responsible to create/update data objects (e.g. procureRequestCollection) required for page.
		 */
		var processUnassignedProcurementInfo = function(options) {
			options || (options = {});
			options.success || (options.success = function() {});
			options.success = _.wrap(options.success, function(success) {
				var args = _.rest(_.toArray(arguments), 1);
				var collection = args[0];
				collection.trigger('fetched');
				success.apply(this, args);
			});	
			procureRequestCollection.fetch(options);
		};
		
		/**
		 * Process Procure Line Collection
		 */
		var processProcureLineCollection = function(options) {
			options || (options = {});
			if(options.highlight) { // Highlight the rows which were failed!
				procureLineCollection.on('fetched', function() {
					var thisCollection = new Collection();
					procureLineCollection.each(function(model) {
						if(model.get('procureFailureDate')) {
							thisCollection.add(model);
						}
					});
					Gloria.ProcurementApp.trigger('procureline:rows:highlight', thisCollection);
					
				});
			} else {
				procureLineCollection.off('fetched');
			}
			options.success || (options.success = function() {});
			options.success = _.wrap(options.success, function(success) {
				var args = _.rest(_.toArray(arguments), 1);
				var collection = args[0];
				collection.trigger('fetched');
				success.apply(this, args);
			});
			procureLineCollection.fetch(options);
			
			
			procureLineStatusFlagUpdate(procureLineCollection);
			
			
		};
		
		var procureLineStatusFlagUpdate = function(procureLineCollection) {
			var pLCollection = procureLineCollection;
			pLCollection.stopListening(pLCollection,'backgrid:edited');	//should not close all events on collection , only mentioned. else clearfilter(event bind) fails     
			pLCollection.listenTo(pLCollection, 'backgrid:edited', function(pLCollection, column, command) {
		        	 	    
				pLCollection.set('edited', 'true');
		        	pLCollection.url = '/procurement/v1/procurelines/'+pLCollection.id+'?&action=save';
		            if(command.keyCode === 13) {
		            	pLCollection.save();
		            }
		        });
			
		};
		/**
		 * Prepare Procure Request Assign View
		 */
		var prepareProcureRequestAssignView = function(inputIds, companyCode) {
			require(['views/procurement/helper/ProcureTeamMembersSelectorHelper'], function(ProcureTeamMembersSelectorHelper) {
				if(hasRolePROCURE_INTERNAL) {
			        ProcureTeamMembersSelectorHelper.constructInternalProcureTeamList({companyCode : companyCode}).then(function(transformedJSON) {
			        	showProcureRequestAssignView(inputIds, transformedJSON);
			        });
			    } else {
			    	ProcureTeamMembersSelectorHelper.constructProcureTeamMembersList({companyCode : companyCode}).then(function(transformedJSON) {
			    		showProcureRequestAssignView(inputIds, transformedJSON);
			        });
			    }
			});
		};

		/**
		 * Show Procure Request Assign Pop-up View
		 */ 
		var showProcureRequestAssignView = function(models, teamCollection) {		    
			require(['views/procurement/overview/view/AssignRequestView', 'views/procurement/common/user/UserSelector'], 
			        function(AssignRequestView, UserSelector) {
			    
				var assignRequestView = new AssignRequestView({
					models : models,
					teamCollection : teamCollection
				});			    
				  
			    var userSelector = new UserSelector({
                    user : teamCollection
                });
			    
				Gloria.basicModalLayout.content.show(assignRequestView);				
				assignRequestView.userInfo.show(userSelector);
				
				assignRequestView.on('hide', function() {
                    Gloria.basicModalLayout.content.reset();
                });
			});
		};
		
		/**
		 * Assign Procurement Information
		 */
		var assignProcureRequests = function(models, info) {
			var that = this;
			var userId = UserHelper.getInstance().getUserId();
			var thisCollection = new Collection(models);
			var thisUrl = '/procurement/v1/materialheaders/current?action=assign&materialControllerId=' + info.assignedMaterialControllerId
							+ '&materialControllerTeam=' + info.assignedMaterialControllerTeam + '&type=MATERIAL_CONTROL&userId=' + userId;
			if (hasRolePROCURE_INTERNAL) { // If user is Internal Procurer
				thisUrl = '/procurement/v1/procurelines?action=assign&userId=' + info.assignedMaterialControllerId
							+ '&teamId=' + info.assignedMaterialControllerTeam + '&type=INTERNAL_PROCURE&currentUserId=' + userId;
			}
			Backbone.sync('update', thisCollection, {
				url : thisUrl,
				type : 'PUT',
				success : function(collection) {
					if (hasRolePROCURE_INTERNAL) { // If user is Internal Procurer
						Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
						Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
						procureLineCollection.reset();
						processProcureLineCollection(); // Refetch!
					} else {
						Gloria.ProcurementApp.trigger('UnassignedRequestGrid:clearSelectedModels');
						Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
						procureRequestCollection.reset();
						processUnassignedProcurementInfo.call(that); // Refetch!
					}
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						title : i18n.t('errormessages:general.title'),
						message : errorMessage
					});
				}
			});
		};
		
		/**
		 * UnAssign Material Request
		 */
		var unAssignMaterialRequest = function(models) {
			var that = this;
			var userId = UserHelper.getInstance().getUserId();
			var thisCollection = new Collection(models);
			var thisUrl = '/procurement/v1/materialheaders/current?action=unassign&userId=' + userId;
			if (hasRolePROCURE_INTERNAL) { // If user is Internal Procurer
				thisUrl = '/procurement/v1/procurelines?action=unassign&userId=' + userId;
			}
			Backbone.sync('update', thisCollection, {
				url : thisUrl,
				type : 'PUT',
				success : function(collection) {
					if (hasRolePROCURE_INTERNAL) { // If user is Internal Procurer
						Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
						Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
						procureLineCollection.reset();
						processProcureLineCollection(); // Refetch!
					} else {
							Gloria.ProcurementApp.trigger('UnassignedRequestGrid:clearSelectedModels');
						Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
						procureRequestCollection.reset();
						processUnassignedProcurementInfo.call(that); // Refetch!
					}
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						title : i18n.t('errormessages:general.title'),
						message : errorMessage
					});
				}
			});
		};

		/**
		 * Process Modification Grid Information. This will be ONLY responsible to create/update
		 * data objects (e.g. procureRequestLineCollection) required for page.
		 */
		var processModificationGridInfo = function(userId, userTeam) {
			procureRequestLineCollection.trigger('QueryParams:reset', ['materialType', 'procureLineStatus', 'procureForwardedId',
			                                                           'procureForwardedTeam', 'assignedMaterialControllerId',
			                                                           'assignedMaterialControllerTeam']);
			var currentUser = UserHelper.getInstance().getUserId();
			if(hasRolePROCURE_INTERNAL) {
				procureRequestLineCollection.queryParams.procureForwardedId = window.localStorage.getItem('Gloria.User.DefaultIP.' + currentUser) || userId;
				procureRequestLineCollection.queryParams.procureForwardedTeam = window.localStorage.getItem('Gloria.User.DefaultIPTeam.' + currentUser) || userTeam;
			} else {
				procureRequestLineCollection.queryParams.assignedMaterialControllerId = window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser) || userId;
				procureRequestLineCollection.queryParams.assignedMaterialControllerTeam = window.localStorage.getItem('Gloria.User.DefaultMCTeam.' + currentUser) || userTeam;
			}
			procureRequestLineCollection.queryParams.materialType = 'USAGE,USAGE_REPLACED';
			procureRequestLineCollection.queryParams.procureLineStatus = 'WAIT_TO_PROCURE';
			procureRequestLineCollection.state.currentPage = 1;
			procureRequestLineCollection.fetch();
		};

		/**
		 * Navigate To Procure line Details
		 */
		var navigateToProcurelineDetails = function(procureId) {
			var location = 'procurement/overview/'+ module +'/procureLineDetail/' + procureId;
			Backbone.history.navigate(location, {
				trigger : true
			});
		};
		
		/**
		 * Navigate To Change line Details
		 */
		var navigateToChangelineDetails = function(changeId) {
			var location = 'procurement/overview/change/changeDetails/' + changeId;
			Backbone.history.navigate(location, {
				trigger : true
			});
		};

		/**
		 * Navigate To Modify Details
		 */
		var navigateToModifyDetails = function(selectedProcureRequestLines, type) {
			var selectedProcureRequestLineIds = _.pluck(selectedProcureRequestLines, 'id').join(',');
			var model = new Backbone.Model();
			model.url = '/procurement/v1/procurelines/modification?requestLineIds=' + selectedProcureRequestLineIds  + '&type=' + type;
			model.fetch({
				success : function(model) {
					if(model.get('success')) {
						var location = 'procurement/overview/modification/modifyDetails/' + selectedProcureRequestLineIds + '?type=' + type;
						Backbone.history.navigate(location, {
							trigger : true
						});
					} else {
						var errorMessage = new Array();
						var item = {
								message : i18n.t('Gloria.i18n.errors.GLO_ERR_17')
							};
						errorMessage.push(item);
						Gloria.trigger('showAppMessageView', {
			    			type : 'error',
			    			message : errorMessage
			    		});
					}
				}
			});
		};

		/**
		 * Assign Procure line To Procure
		 */
		var assignProcurelineToProcure = function(procureLineModels, subModule) {
			var that = this;
		    if(!procureLineModels || procureLineModels.length == 0) return;
		    var thisCollection;
		    if(_.isNumber(procureLineModels[0])) {
		        thisCollection = new Collection();
		        _.each(procureLineModels, function(procureLineId) {
		            var model = procureLineCollection.get(procureLineId);
		            if(procureLineId && model) {
		                thisCollection.add(model);
		            }
		        });		        
		    } else {
		        thisCollection = new Collection(procureLineModels);   
		    }	
			Backbone.sync('update', thisCollection, {
				url: '/procurement/v1/procurelines' + (subModule == 'PROCURED' ? '?action=revert' : '?action=toprocure'),
				type: 'PUT',
				success: function(collection) {
					Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
					Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
					showOverviewTabs.call(that, module);
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						title : i18n.t('errormessages:general.title'),
						message : errorMessage
					});
				}
			});
		};

		/**
		 * Delegate On Build Site
		 */
		var delegateOnBuildSite = function(selectedModels) {
            var thisCollection = new Collection(selectedModels);
            Backbone.sync('update', thisCollection, {
                url: '/procurement/v1/procurelines?action=delegate',
                type: 'PUT',
                success: function(collection) {
                    Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
                    Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
                    // Refetch procureLineCollection and refresh the Grid
                    procureLineCollection.state.currentPage = 1;
                    processProcureLineCollection();
                    downloadExcel(function() {
                        var ids = new Array();
                        _.each(selectedModels, function(model) {
                            ids.push(model.id);
                        });
                        return ids;
                    }(), true);
                }
            });
        };
		
        /**
         * Download Excel
         */
		var downloadExcel = function(procurelinesIDs, shouldExport) {
        	if(!procurelinesIDs) return;
        	if(shouldExport) {
        		window.location.href = "/GloriaUIServices/api/procurement/v1/procurelines/excel?id=" + procurelinesIDs.join(',');
        	}
        };
        
        /**
         * Refresh Data On User Change
         */
        var refreshDataOnUserChange = function(userId, userTeam) {
        	var currentUser = UserHelper.getInstance().getUserId();
            procureLineCollection.trigger('QueryParams:reset', ['procureResponsibility', 'status', 'procureForwardedId',
                                                                'procureForwardedTeam', 'assignedMaterialControllerId',
                                                                'assignedMaterialControllerTeam']);
            Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
	        if (hasRolePROCURE_INTERNAL) {
	        	window.localStorage.setItem('Gloria.User.DefaultIPTeam.' + currentUser, userTeam);
	        	window.localStorage.setItem('Gloria.User.DefaultIP.' + currentUser, userId);
			} else {
				window.localStorage.setItem('Gloria.User.DefaultMCTeam.' + currentUser, userTeam);
				window.localStorage.setItem('Gloria.User.DefaultMC.' + currentUser, userId);
			}    	
         
			switch (module) {
			case 'toProcure':
				procureResponsibility = 'PROCURER';
				status = 'WAIT_TO_PROCURE';
				processOverviewGridInfo(procureResponsibility, status, userId, userTeam);
				break;
			case 'modification':
				processModificationGridInfo(userId, userTeam);
				break;
			case 'onBuildSite':
				procureResponsibility = 'BUILDSITE';
				status = 'WAIT_TO_PROCURE';
				processOverviewGridInfo(procureResponsibility, status, userId, userTeam);
				break;
			case 'procured':
				procureResponsibility = 'PROCURER';
				status = 'PROCURED,PLACED,RECEIVED,RECEIVED_PARTLY';
				processOverviewGridInfo(procureResponsibility, status, userId, userTeam);
				break;
			case 'change':
				processChangeIdGridInfo(userId, userTeam);
				break;
			default:
				break;
			}
		};
		
		/**
		 * Return Procurement
		 */
		var returnProcurement = function(selectedModels) {
			var thisCollection = new Collection(selectedModels);
			Backbone.sync('update', thisCollection, {
				url: '/procurement/v1/procurelines?action=return',
				type: 'PUT',
				success: function(collection) {
					Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
					Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
					// Refetch procureLineCollection and refresh the Grid
					procureLineCollection.state.currentPage = 1;
					processProcureLineCollection();
				}
			});
		};
		
		/**
		 * Show Forward Procurement Pop-up
		 */
		var showForwardProcurement = function(selectedModels) {
			require(['views/procurement/overview/view/ForwardProcModalView'], function(ForwardProcModalView) {
				var forwardProcModalView = new ForwardProcModalView({
					models : selectedModels
				});
				Gloria.basicModalLayout.content.show(forwardProcModalView);
				forwardProcModalView.on('hide', function() {
                    Gloria.basicModalLayout.content.reset();
                });
			});
		};
		
		/**
		 * Forward Procurement
		 */
		var forwardProcurement = function(models, data) {
		    models[0].attributes.hasUnread='false';
			var thisCollection = new Collection(models);
			Backbone.sync('update', thisCollection, {
				url: '/procurement/v1/procurelines?action=forward&teamId=' + data.forwardProc + '&userId=' + data.forwardProcUser,
				type: 'PUT',
				success: function(collection) {
					Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
					Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
					// Refetch procureLineCollection and refresh the Grid
					procureLineCollection.state.currentPage = 1;
					processProcureLineCollection();
				}
			});
		};
		
		/**
		 * Mark As Mine
		 */
		var markAsMine = function(models) {
			var currentUser = UserHelper.getInstance().getUserId();
			var thisCollection = new Collection(models);
			var userId;
			if(hasRolePROCURE_INTERNAL) {
				userId = window.localStorage.getItem('Gloria.User.DefaultIP.' + currentUser) || UserHelper.getInstance().getUserId();				
			} else {
				userId = window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser) || UserHelper.getInstance().getUserId();
			}
			Backbone.sync('update', thisCollection, {
				url: '/procurement/v1/procurelines?action=markAsMine&userId=' + userId,
				type: 'PUT',
				success: function(collection) {
					Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
					Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
					// Refetch procureLineCollection and refresh the Grid
					processProcureLineCollection();
				}
			});
		};
		
		/**
		 * Show Multiple Update
		 */
		var showMultipleUpdate = function(selectedModels) {
			require(['views/procurement/overview/view/MultipleUpdateModalView'], function(MultipleUpdateModalView) {
				var multipleUpdateModalView = new MultipleUpdateModalView({
					models : selectedModels,
					hasRolePI: hasRolePROCURE_INTERNAL,
					hasRoleDELIVERY : hasRoleDELIVERY
				});
				Gloria.basicModalLayout.content.show(multipleUpdateModalView);
				multipleUpdateModalView.on('hide', function() {
                    Gloria.basicModalLayout.content.reset();
                });
			});
		};
		
		/**
		 * Multiple Update
		 */
		var multipleUpdate = function(selectedModels, data, finalWhSiteId, alsoProcure) {
			_.each(selectedModels, function(thisModel) {
				data.procureType && thisModel.set('procureType', data.procureType);
				data.supplierCounterPartID && thisModel.set('supplierCounterPartID', data.supplierCounterPartID);
				data.requiredStaDate && thisModel.set('requiredStaDate', data.requiredStaDate);
				data.supplierName && thisModel.set({
					supplierId: -1,
					supplierName: data.supplierName
				});
				data.orderNo && thisModel.set('orderNo', data.orderNo);
				data.orderStaDate && thisModel.set('orderStaDate', data.orderStaDate);
				data.purchaseOrganisationCode && thisModel.set('purchaseOrganisationCode', data.purchaseOrganisationCode);
				data.buyerCode && thisModel.set('buyerCode', data.buyerCode);
				data.qualityDocumentId && thisModel.set('qualityDocumentId', data.qualityDocumentId);
				data.referenceGps && thisModel.set('referenceGps', data.referenceGps);
				data.forwardedForDC && thisModel.set('forwardedForDC', data.forwardedForDC);
				data.procureInfo && thisModel.set('procureInfo', data.procureInfo);
				data.warehouseComment && thisModel.set('warehouseComment', data.warehouseComment);
				data.statusFlag && thisModel.set('statusFlag', data.statusFlag);
				thisModel.set('edited', 'true');
			});
			var thisCollection = new Collection(selectedModels);
			Backbone.sync('update', thisCollection, {
				url: '/procurement/v1/procurelines?finalWarehouse=' + finalWhSiteId + (alsoProcure ? '&action=procure' : ''),
				type: 'PUT',
				success: function(collection) {
					Gloria.ProcurementApp.trigger('procureline:multipleupdate:done', true);
					Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
					Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
					if(alsoProcure) {
						procureLineCollection.state.currentPage = 1;
					}
					processProcureLineCollection({
						highlight : true
					});
					if(alsoProcure) {
						showProcureMessage(thisCollection, collection);
					}
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						modal : true,
						message : errorMessage
					});
				}
			});
		};
		
		/**
		 * Procure
		 */
		var procure = function(models) {
			var currentUser = UserHelper.getInstance().getUserId();
			var userId, teamId;			
			if(hasRolePROCURE_INTERNAL) {
				userId = window.localStorage.getItem('Gloria.User.DefaultIP.' + currentUser) || UserHelper.getInstance().getUserId();
				teamId = window.localStorage.getItem('Gloria.User.DefaultIPTeam.' + currentUser);
			} else {
				userId = window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser) || UserHelper.getInstance().getUserId();
				teamId = window.localStorage.getItem('Gloria.User.DefaultMCTeam.' + currentUser);
			}
			var thisCollection = new Collection(models);
			Backbone.sync('update', thisCollection, {
				url: '/procurement/v1/procurelines?action=procure&userId=' + userId	+ '&teamId=' + teamId,
				type: 'PUT',
				success: function(collection) {
					Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
					Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
					procureLineCollection.state.currentPage = 1;
					delete procureLineCollection.state.sortKey;
					delete procureLineCollection.state.order;
					processProcureLineCollection();
					showProcureMessage(thisCollection, collection);
				}
			});
		};
		
		/**
		 * Show Procure Message
		 */
		var showProcureMessage = function(originalCollection, responseCollection) {
			if(responseCollection && responseCollection.length == originalCollection.length) {
				Gloria.trigger('showAppMessageView', {
					type : 'success',
					title : i18n.t('Gloria.i18n.procurement.overviewModule.text.procureSuccessHeader'),
					message : new Array({
						message : i18n.t('Gloria.i18n.procurement.overviewModule.text.procureSuccessText')
					})
				});
			} else {
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					title : i18n.t('Gloria.i18n.procurement.overviewModule.text.procureFailureHeader'),
					message : new Array({
						message : i18n.t('Gloria.i18n.procurement.overviewModule.text.procureFailureText1') + responseCollection.length
								  + i18n.t('Gloria.i18n.procurement.overviewModule.text.procureFailureText2') + (originalCollection.length - responseCollection.length)
								  + i18n.t('Gloria.i18n.procurement.overviewModule.text.procureFailureText3')
					}),
					actions : {
						showDetails : {
							label : i18n.t('Gloria.i18n.procurement.overviewModule.text.showDetails'),
							callback : _.bind(function() {
								procureLineCollection.state.sortKey = 'procureFailureDate';
								procureLineCollection.state.order = 1;
								processProcureLineCollection({
									highlight: true
								});
							}, this)
						},
						ignore : {
							label : i18n.t('Gloria.i18n.procurement.overviewModule.text.ignore'),
							callback : _.bind(function() {
								var unsuccessCollection = new Collection(originalCollection.toJSON());
								if(responseCollection) {
									_.each(responseCollection, function(model) {
										unsuccessCollection.remove(model, {silent: true});
									});
								}
								var currentUser = UserHelper.getInstance().getUserId();
								var userId, teamId;			
								if(hasRolePROCURE_INTERNAL) {
									userId = window.localStorage.getItem('Gloria.User.DefaultIP.' + currentUser) || UserHelper.getInstance().getUserId();
									teamId = window.localStorage.getItem('Gloria.User.DefaultIPTeam.' + currentUser);
								} else {
									userId = window.localStorage.getItem('Gloria.User.DefaultMC.' + currentUser) || UserHelper.getInstance().getUserId();
									teamId = window.localStorage.getItem('Gloria.User.DefaultMCTeam.' + currentUser);
								}
								Backbone.sync('update', unsuccessCollection, {
									url: '/procurement/v1/procurelines?action=ignoreUnsuccessful&userId=' + userId	+ '&teamId=' + teamId,
									type: 'PUT',
									success: function() {
										processProcureLineCollection();
									}
								});
							}, this)
						}
					}
				});
			}
		};
		
		var onbuildsite = function(selectedProcureLineArray) {
			if(selectedProcureLineArray && selectedProcureLineArray.length) {
				var thisCollection = new Collection(selectedProcureLineArray);
				_.each(selectedProcureLineArray, function(procureLine) {
					procureLine.set({procureResponsibility: 'BUILDSITE'});
				});
				Backbone.sync('update', thisCollection, {
					url : '/procurement/v1/procurelines?action=onbuildsite',
					type : 'PUT',
					success : function(collection) {
						Gloria.ProcurementApp.trigger('procurelineGrid:ResetButtons');
						Gloria.ProcurementApp.trigger('procurelineGrid:clearSelectedModels');
						// Refetch procureLineCollection and refresh the Grid
						procureLineCollection.state.currentPage = 1;
						processProcureLineCollection();
					},
					validationError : function(errorMessage, errors) {
						Gloria.trigger('showAppMessageView', {
							type : 'error',
							title : i18n.t('errormessages:general.title'),
							message : errorMessage
						});
					}
				});
			}
		};
		
	      /**
         * Show Export Excel Modal View
         */
        var showExportExcelModalView = function(models) {
            require(['views/procurement/overview/view/ExportExcelModalView'], function(ExportExcelModalView) {
                var exportExcelModalView = new ExportExcelModalView({
                    models : models
                });
                Gloria.basicModalLayout.content.show(exportExcelModalView);
            });
        };
        
        /**
         * Export Excel
         */
        var exportToExcel = function(ids, options) {
            var filterStorageKey = 'Gloria.procurement.' + module + '.filters';
            var qryParams = window.sessionStorage.getItem(filterStorageKey);
            var dataAsQueryString;
            if(qryParams) {
                var parsedParams = JSON.parse(qryParams);
                var dataAsQueryString = $.param(parsedParams);
            } 
            var url = '/GloriaUIServices/api/procurement/v1/procurelines/toprocure/excel?'+dataAsQueryString;
                if(ids) {
                url = url+'&ids=' +ids;
                    }
            window.location.href = url;
        };
        
        var showMaterialLineType = function(type) {
        	var qryParams = window.sessionStorage.getItem("Gloria.procurement.toProcure.filters");
        	
        	if (qryParams) {
				var parsedParams = JSON.parse(qryParams);
				delete parsedParams['showFilter'];
				delete procureLineCollection.queryParams.showFilter;
        	}
        	window.sessionStorage.setItem("Gloria.procurement.toProcure.filters", JSON.stringify(parsedParams));
        	
        	procureLineCollection.queryParams.showFilter = type;
        	procureLineCollection.fetch({});
        }
        

		Controller.ProcurementOverviewController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();				
			},

			initializeListeners : function() {				
			    this.listenTo(Gloria.ProcurementApp, 'procurelineDetails:show', navigateToProcurelineDetails);
			    this.listenTo(Gloria.ProcurementApp, 'changelineDetails:show', navigateToChangelineDetails);
			    this.listenTo(Gloria.ProcurementApp, 'modifyDetails:modify', navigateToModifyDetails);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:tomodify', navigateToModifyDetails);			    
			    this.listenTo(Gloria.ProcurementApp, 'procureline:onbuildsite', onbuildsite);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:assignToProcure', assignProcurelineToProcure);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:delegateOnBuildSite', delegateOnBuildSite);
			    this.listenTo(Gloria.ProcurementApp, 'showProcureRequestAssignView', prepareProcureRequestAssignView);			    
			    this.listenTo(Gloria.ProcurementApp, 'unAssignMaterialRequest', unAssignMaterialRequest);			    
			    this.listenTo(Gloria.ProcurementApp, 'procureRequestAssign:done', assignProcureRequests);
			    this.listenTo(Gloria.ProcurementApp, 'Procurement:user:change', refreshDataOnUserChange);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:return', returnProcurement);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:forward:show', showForwardProcurement);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:forward', forwardProcurement);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:markAsMine', markAsMine);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:multipleupdate:show', showMultipleUpdate);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:multipleupdate', multipleUpdate);
			    this.listenTo(Gloria.ProcurementApp, 'procureline:procure', procure);
			    this.listenTo(Gloria.ProcurementApp, 'ToProcure:ExportExcel:show', showExportExcelModalView);
			    this.listenTo(Gloria.ProcurementApp, 'ToProcure:ExportExcel', exportToExcel);
			    this.listenTo(Gloria.ProcurementApp, 'ToProcure:MaterialLineType:show', showMaterialLineType);
			},

			control : function(subView) {
				this.disposeVariables();
				if(subView) {
					module = subView;
				} else {
					module = 'inbox';
					Backbone.history.navigate('procurement/overview/inbox', {
		                replace : true
		            });
				}
				prepareOverviewRequest.call(this);
			},
			
			disposeVariables: function() {
				module = null;
		        procureLineCollection = null;
		        procureRequestLineCollection = null;
		        procureRequestCollection = null;		        
		        procurementOverviewView = null;
		        overviewButtonView = null;
		        overviewGridView = null;
		        modificationGridView = null;
		        unassignedRequestGridView = null;
		        internalProcurementGridView = null;
		        filteredRequestHeaderCollection = null;
		        hasRolePROCURE_INTERNAL = null;
		        hasRoleDELIVERY = null;
			},
			
			onDestroy: function() {
			    this.disposeVariables();
			}
		});
	});

	return Gloria.ProcurementApp.Controller.ProcurementOverviewController;
});
