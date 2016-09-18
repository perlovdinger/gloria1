define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'views/deliverycontrol/collections/OrderLineCollection',
		'utils/UserHelper',
		'utils/DateHelper',
		'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, OrderLineCollection, UserHelper, DateHelper, GloriaCollection) {

	Gloria.module('DeliveryControlApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var module;
		var orderLineCollection;
		var adjustDeliveryModalView;
		var overviewLayout;
		
		/**
		 * Prepare My Order Overview
		 */ 
		var prepareMyOrderOverview = function() {
			// Initialize Data Source Objects
			initializeDataSourceObjects();
			// Set Page Layout
			setPageLayout();
		};
		
		/**
		 * Initialize Data Source Objects
		 */
		var initializeDataSourceObjects = function() {
			var whichGrid = 'MyOrderOverview' + module + 'Grid';
			orderLineCollection = new OrderLineCollection([], {
			     state : {
					pageSize : function() { // Check if any pageSize is already stored
					var object = JSON.parse(window.localStorage.getItem('Gloria.deliverycontrol.' + whichGrid
							+ '.' + UserHelper.getInstance().getUserId()));
					return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : whichGrid
			 });
			orderLineCollection.on('sync', function() {
				(window.setImmediate || window.setTimeout)(function() {
					module != 'completed' && highlightLastUpdatedOrderLine(orderLineCollection);
				}, 0);				 
			});
		};
		
		/**
		 * Set Page Layout
		 */ 
		var setPageLayout = function() {
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', {itemId : module}); // No breadcrumb for External/Internal/Completed
			require(['views/deliverycontrol/myorderoverview/view/OverviewLayout'], function(OverviewLayout) {
				overviewLayout = new OverviewLayout({
					module : module
				});
				overviewLayout.on('show', function() {
					showModuleView();
					prepareUserSelectorView(showMyOrderOverviewContent);					
				}, this);
				Gloria.basicLayout.content.show(overviewLayout);
			});
		};
		
		/**
		 * Show Module View.
		 */
		var showModuleView = function() {
			require(['views/deliverycontrol/common/DeliveryControlModuleView'], function(DeliveryControlModuleView) {
				var deliveryControlModuleView = new DeliveryControlModuleView({
					module : 'myOrderOverview'
				});
				overviewLayout.moduleInfo.show(deliveryControlModuleView);
			});
		};
		
		/**
		 * Prepare Filter View.
		 */
		var prepareUserSelectorView = function(callback) {
			require(['views/deliverycontrol/common/DeliveryControllerTeamHelper'], function(DeliveryControllerTeamHelper) {
				DeliveryControllerTeamHelper.constructDCTeamMembersList().then(function(transformedJSON) {
					showUserSelectorView(transformedJSON);
					callback();
		        });
			});
		};
		
		/**
		 * Show Filter View.
		 */
		var showUserSelectorView = function(transformedJSON) {
			require(['views/deliverycontrol/common/UserSelector'], function(UserSelector) {
				var selected, userId, userTeam;
				var currentUser = UserHelper.getInstance().getUserId();
				try {
	                userId = window.localStorage.getItem('Gloria.User.DefaultDCId.' + currentUser) || orderLineCollection.getFilterStorageValue('deliveryControllerId') || '';
				    userTeam = window.localStorage.getItem('Gloria.User.DefaultDCTeam.' + currentUser) || orderLineCollection.getFilterStorageValue('deliveryControllerTeam') || '';
			        selected =  userId + ';' + userTeam;
			    } catch(e) {
			        selected = '';
			    }
				var userSelector = new UserSelector({
					module : module,
					user : transformedJSON,
					selected: selected
				});
				overviewLayout.userSelector.show(userSelector);
			});
		};
	
		/**
		 * Show/Render My Order Overview Module and Grid View.
		 */
		var showMyOrderOverviewContent = function() {
			require(['views/deliverycontrol/myorderoverview/view/OverviewButtonView',
			         'views/deliverycontrol/myorderoverview/view/OverviewGridView'],
			function(OverviewButtonView, OverviewGridView) {
				var overviewButtonView = new OverviewButtonView({
					module : module
				});
				var overviewGridView = new OverviewGridView({
					module : module,
					collection : orderLineCollection,
					showDCInfo : window.localStorage.getItem('Gloria.User.DefaultDCId.' + UserHelper.getInstance().getUserId()) === 'open'
				});
				overviewGridView.on('show', function() {
					processMyOrderOverviewInfo();
				}, this);
				if(module == 'internal') {
					overviewLayout.internalButton.show(overviewButtonView);
					overviewLayout.internalGrid.show(overviewGridView);
				} else if(module == 'external') {
					overviewLayout.externalButton.show(overviewButtonView);
					overviewLayout.externalGrid.show(overviewGridView);
				} else if(module == 'completed') {
					overviewLayout.completedButton.show(overviewButtonView);
					overviewLayout.completedGrid.show(overviewGridView);
				}
			});
		};
		
		/**
		 * Process My Order Overview Information
		 */
		var processMyOrderOverviewInfo = function() {
			delete orderLineCollection.queryParams.deliveryControllerId;
			delete orderLineCollection.queryParams.deliveryControllerTeam;
			delete orderLineCollection.queryParams.unassigned;
			delete orderLineCollection.queryParams.status;
			orderLineCollection.trigger('QueryParams:reset', ['deliveryControllerId', 'deliveryControllerTeam',
			                                                  'unassigned', 'internalExternal', 'status', 'completeType']);
			orderLineCollection.queryParams.internalExternal = module.toUpperCase();
			orderLineCollection.queryParams.needDeliverySchedules = true;
			var currentUser = UserHelper.getInstance().getUserId();
			var userTeam = window.localStorage.getItem('Gloria.User.DefaultDCTeam.' + currentUser);
			var userId = window.localStorage.getItem('Gloria.User.DefaultDCId.' + currentUser);
			if(userTeam) {
				orderLineCollection.queryParams.deliveryControllerTeam = userTeam;
			}
			if(userId) {
				orderLineCollection.queryParams.status = 'PLACED,RECEIVED_PARTLY';
				if(userId == 'unassigned') {
					orderLineCollection.queryParams.unassigned = true;
				} else if(userId != 'open') {
					orderLineCollection.queryParams.deliveryControllerId = userId;
				}
			}
			if(module == 'completed') {
				orderLineCollection.queryParams.internalExternal = 'INTERNAL,EXTERNAL';
				orderLineCollection.queryParams.completeType = 'COMPLETE,RECEIVED,CANCELLED';
				orderLineCollection.queryParams.status = 'COMPLETED';
			}
			orderLineCollection.fetch();
		};
		
		/**
		 * Highlight Last Updated OrderLine
		 */
		var highlightLastUpdatedOrderLine = function(response) {
			var lastModifiedModelId = undefined;
        	var tempOrderLineCollection = response;
			var lastModifiedDateList = tempOrderLineCollection.pluck('lastModifiedDate');
			var maxValueInArray = Math.max.apply(Math, lastModifiedDateList);
			tempOrderLineCollection.each(function(model) {
        		var currOrderLineDate = model.attributes.lastModifiedDate;
        		var currOrderLineId = model.attributes.id;
        		//var contentEdited = model.attributes.markedAsComplete;
        		$('td#orderNo_' + currOrderLineId).closest('tr').removeClass('border-yellow');
        		if(currOrderLineDate == maxValueInArray) {
        			lastModifiedModelId = currOrderLineId;
        		}
        		/*if(contentEdited == false){
                    $('td#orderNo_' + contentEdited).closest('tr').addClass('border-yellow'); 
                }*/
    		});
        	if(lastModifiedModelId) {
        		$('td#orderNo_' + lastModifiedModelId).closest('tr').addClass('border-yellow');
        	}	
		};
		

		/**
		 * Show My Order Overview Edit View
		 */
		var showMyOrderOverviewEditView = function(selectedRows) {
			require(['views/deliverycontrol/myorderoverview/view/MyOrderOverviewEditView'], function(MyOrderOverviewEditView) {
				var myOrderOverviewEditView = new MyOrderOverviewEditView({
					module : module,
					models : selectedRows
				});
				Gloria.basicModalLayout.content.show(myOrderOverviewEditView);
				myOrderOverviewEditView.on('hide', function() {
					Gloria.basicModalLayout.content.reset();
				});
			});
		};
		
		/**
		 *  Multiple Update for Internal & External modules
		 */
		var updateMyOrderOverviewInfo = function(module, models, dsInfo, orderInfo) {
			var saveDeliverySchedules = function(models, dsInfo) {
				_.each(models, function(model, count) {
					_.each(model.deliverySchedules.models, function(thisModel, dsCount) {
						var thisInfo = {
							expectedDate : dsInfo.expectedDate ? dsInfo.expectedDate : thisModel.get('expectedDate'),
							plannedDispatchDate : dsInfo.plannedDispatchDate ? dsInfo.plannedDispatchDate : thisModel.get('plannedDispatchDate'),
							statusFlag : dsInfo.statusFlag ? dsInfo.statusFlag : thisModel.get('statusFlag'),
						};
						thisModel.fetch({
							url: '/material/v1/orderlines/' + model.get('id') 
							+ '/deliveryschedules/' + thisModel.get('id'),
							success : function(response) {
								thisModel.save(thisInfo, {
									async : false,
									silent : true,
									url: '/material/v1/orderlines/' + model.get('id') 
											+ '/deliveryschedules/' + thisModel.get('id'),
									success: function() {
										if(count == models.length - 1 && dsCount == model.deliverySchedules.models.length - 1) {
											Gloria.DeliveryControlApp.trigger('MyOrderOverviewEditView:updatedInfo', true);
											Gloria.DeliveryControlApp.trigger('OverviewGrid:clearselection');
											Gloria.DeliveryControlApp.trigger('OverviewButton:refresh');
											processMyOrderOverviewInfo(); // Refetch
											Gloria.trigger('showAppMessageView', {
								    			type : 'success',
								    			message : new Array({
								    				message : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.validation.processSucceeded')
								    			})
								    		});
										}
									},
									error: function() {
										Gloria.DeliveryControlApp.trigger('MyOrderOverviewEditView:updatedInfo', false);
										return false;
									}
								});
							}
						});
					});
				});
			};
			switch (module) {
			case 'internal':
				saveDeliverySchedules(models, dsInfo);
				break;
			case 'external':
				_.each(models, function(model, count) {
					var thisInfo = {
						staAcceptedDate : orderInfo.staAcceptedDate ? orderInfo.staAcceptedDate : model.get('staAcceptedDate'),
						staAgreedDate : orderInfo.staAgreedDate ? orderInfo.staAgreedDate : model.get('staAgreedDate')
					};
					model.save(thisInfo, {
						validate : false,
						parse : false,
						success : function(response) {
							if(count == models.length - 1) {
								saveDeliverySchedules(models, dsInfo);
							}
						},
						error : function() {
							Gloria.DeliveryControlApp.trigger('MyOrderOverviewEditView:updatedInfo', false);
							return false;
						},
						validationError: function(errorMessage) {
							Gloria.trigger('showAppMessageView', { 
					            type : 'error',
					            title : i18n.t('errormessages:general.title'),
					            message: errorMessage
					        });
						}
					});
				});
				break;
			}
		};
		
		/**
		 * Show Delivery Adjust Modal View
		 */
		var showDeliveryAdjustModalView = function(model) {
		    var that = this;
			require(['views/deliverycontrol/myorderoverview/view/AdjustDeliveryModalView'], function(AdjustDeliveryModalView) {
			    adjustDeliveryModalView = new AdjustDeliveryModalView({
					model : model
				});
			    adjustDeliveryModalView.on('show', function() {
			    	showDeliveryInfo.call(this, {
				    	model : model,
				    	collection : model.deliverySchedules
				    });
				}, that);
				Gloria.basicModalLayout.content.show(adjustDeliveryModalView);
			});
		};
		
		/**
		 * Show Delivery Info
		 */
		var showDeliveryInfo = function(options) {
			require(['views/deliverycontrol/myorderoverview/view/AdjustDeliveryView'], function(AdjustDeliveryView) {
				var adjustDeliveryView = new AdjustDeliveryView({
					model : options.model,
					collection : options.collection.deepClone()
				});
				adjustDeliveryModalView.adjustDelivery.show(adjustDeliveryView);
			});
		};
		
		/**
		 * Save Adjusted Deliveries
		 */
		var saveAdjustedDeliveries = function(options) {
		    var orderLine = options.orderLine;
		    var deliveries = options.deliveries;
		    deliveries.url = '/material/v1/orderlines/' + orderLine.id + '/deliveryschedules';
		    
		    deliveries.save({			
				success : function(collection) {
					if(collection) {
						// Sorting this collection by expectedDate
						collection.sort(function(ob1, ob2) {
							return new Date(ob1.expectedDate) - new Date(ob2.expectedDate);
						});
					};
					Gloria.DeliveryControlApp.trigger('MyOrderOverview:savedAdjustedDeliveries', true, {orderLineId: orderLine.id, collection: collection});
					Gloria.DeliveryControlApp.trigger('OverviewGrid:clearselection');
					Gloria.DeliveryControlApp.trigger('OverviewButton:refresh');
					processMyOrderOverviewInfo(); // Refetch
				},
				error : function() {
					Gloria.DeliveryControlApp.trigger('MyOrderOverview:savedAdjustedDeliveries', false);
				},
				validationError: function(errorMessage) {
					Gloria.trigger('showAppMessageView', { 
			            type : 'error',
			            title : i18n.t('errormessages:general.title'),
			            message: errorMessage
			        });
				}
			});
		};
		
		/**
		 * Ignore Deviation
		 */
		var ignoreDeviation = function(options) {
			var orderLine = options.orderLine;
			var ignoreDeliveryDeviationCollection = new  GloriaCollection();
			ignoreDeliveryDeviationCollection = orderLine;
			ignoreDeliveryDeviationCollection.set('deliveryDeviation', false,  {silent : true});
			ignoreDeliveryDeviationCollection.url  = '/procurement/v1/orderlines/'+ orderLine.id +'/ignore';
			ignoreDeliveryDeviationCollection.save({}, {
			    validate : false,
				success : function(response) {				
					orderLineCollection.fetch();
				},
				validationError: function(errorMessage) {
					Gloria.trigger('showAppMessageView', { 
			            type : 'error',
			            title : i18n.t('errormessages:general.title'),
			            message: errorMessage
			        });
				}
			});
		};
		
		/**
		 * Fetch Delivery Schedules
		 */
		var fetchDeliverySchedule = function(options) {		    
		    var model = options.model;
		    var deliverySchedules = model.deliverySchedules;
	        //Remove previous change listener 
	        this.stopListening(deliverySchedules);	      
	        this.listenTo(deliverySchedules, 'backgrid:edited', function(deliverySchedule, column, command) {    	        	
	            deliverySchedule.url = '/material/v1/orderlines/' + model.id + '/deliveryschedules/' + deliverySchedule.id;
	            if(command.keyCode === 13) {
	                deliverySchedule.save({}, {
	                	success : function() {
	                		orderLineCollection.fetch(); // highlight Last Updated Order Line after save
						}
	                });
	            }
	        });
		    return deliverySchedules;
		};
		
		/**
		 * Update Delivery Schedules
		 */
		var updateDeliverySchedules = function(status, options) {		    		    
		    if(status) {		        
		        var orderLineId = options.orderLineId;
		        var collection = options.collection;
		        orderLineCollection.get(orderLineId).deliverySchedules.reset(collection);
		    }
		};
		
		/**
		 * Show Export Excel Modal View
		 */
		var showExportExcelModalView = function(models) {
			require(['views/deliverycontrol/myorderoverview/view/ExportExcelModalView'], function(ExportExcelModalView) {
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
			var whichGrid = 'MyOrderOverview' + module + 'Grid';
			// Find all the filter values from session storage
			var filterStorageKey = 'Gloria.deliverycontrol.' + whichGrid + '.filters';
			var qryParams = window.sessionStorage.getItem(filterStorageKey);
			var dataAsQueryString;
			if(qryParams) {
				var parsedParams = JSON.parse(qryParams);
				var dataAsQueryString = $.param(parsedParams);
			}
			window.location.href = '/GloriaUIServices/api/material/v1/orderlines/current/excel?ids=' + ids + '&' + options + '&' + dataAsQueryString;
		};
		
		/**
		 * Refresh Data On User Change
		 */
		var refreshDataOnUserChange = function(userId, userTeam) {
			var currentUser = UserHelper.getInstance().getUserId();
			window.localStorage.setItem('Gloria.User.DefaultDCTeam.' + currentUser, userTeam);
            window.localStorage.setItem('Gloria.User.DefaultDCId.' + currentUser, userId);
			Gloria.DeliveryControlApp.trigger('MyOrderOverview:grid:rerender', {showDCInfo: userId === 'open'});
			processMyOrderOverviewInfo();
		};
		
		var saveAndRedirect = function(model, callback) {
			if(module == 'internal' || module == 'external') {
				model.save({}, {
					url : '/procurement/v1/orderlines/' + model.id,
				    validate : false,
					success : function() {
						callback && callback();
					}
				});
			} else {
				callback && callback();
			}
		};
		
		Controller.MyOrderOverviewController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {			
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverviewEditView:show', showMyOrderOverviewEditView);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverviewEditView:updateInfo', updateMyOrderOverviewInfo);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:showDeliveryAdjustModalView', showDeliveryAdjustModalView);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:ignore', ignoreDeviation);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:saveAdjustedDeliveries', saveAdjustedDeliveries);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:deliverySchedule:fetch', fetchDeliverySchedule);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:savedAdjustedDeliveries', updateDeliverySchedules);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:ExportExcel:show', showExportExcelModalView);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:ExportExcel', exportToExcel);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:user:change', refreshDataOnUserChange);
				this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:SaveAndRedirect', saveAndRedirect);
			},

			control : function(page) {
				module = page || 'external';
				prepareMyOrderOverview.call(this);
			},
			
			onDestroy: function() {
			    module = null;
		        orderLineCollection = null;		        
		        adjustDeliveryModalView = null;
		        overviewLayout= null;
			}
		});
	});

	return Gloria.DeliveryControlApp.Controller.MyOrderOverviewController;
});
