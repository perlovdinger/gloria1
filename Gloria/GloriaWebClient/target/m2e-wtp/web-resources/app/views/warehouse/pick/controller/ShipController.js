define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'i18next',
		'utils/backbone/GloriaCollection',
		'utils/backbone/GloriaModel',
		'collections/DispatchNoteCollection',
		'models/DispatchNoteModel',
		'collections/RequestListCollection',	
		'collections/RequestGroupCollection',	
		'models/RequestListModel',
		'models/RequestGroupModel',
		'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Collection, Model, DispatchNoteCollection, DispatchNoteModel, RequestListCollection, RequestGroupCollection, RequestListModel, RequestGroupModel, UserHelper) {
    
	Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var requestListCollection;
		var requestListId;
		var layout;
		var pickShipLayout;
		var dispatchNoteModel;
		var requestListModel;
		var dispatchNoteId;
		var isShipped;
		var createDispatchNoteView;
		var dispatchNotePartInfoView;
		var printDispatchNote;
		var subModule;
		
		/**
		 * Prepare Ship.
		 */
		var prepareShip = function() {
			// Initialize Data Source Objects
			initializeShipDataSourceObjects();
			setShipPageLayout();
		};	
	
		/**
		 * Initialize Ship Data Source Objects.
		 */
		var initializeShipDataSourceObjects = function() {	
			var whichGrid = '';
			requestListCollection= new RequestListCollection([], {
				state : {
					pageSize : function() {	
							switch (subModule) {
							case 'ship':
		                         whichGrid = 'ShipGrid';
		                         break;    
							case 'inpick':
								whichGrid = 'InPickGrid';
							default:
								break;
							}
						var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.'+ whichGrid
								 + '.' + UserHelper.getInstance().getUserId()));
						return (object && object['pageSize']) || 10; // Default 10
					}(),
					currentPage : 1
				},
				filterKey : whichGrid
			});		
		};		

		/**
		 * Set Page Layout.
		 */
		var setShipPageLayout = function() {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.trigger('showBreadcrumbView');
			Gloria.basicLayout.content.reset();
			require(['views/warehouse/pick/view/Layout', 'views/warehouse/pick/view/PickShipLayout'], 
					function(Layout, PickShipLayout) {
				layout = new Layout({
                	module : subModule
                });
				layout.on('show', function() {
					showModuleView();
				}, this);
				
				pickShipLayout = new PickShipLayout();
				Gloria.basicLayout.content.show(layout);
				
				//Attach to shipPane
				if(subModule == 'ship') {
                layout.shipPane.show(pickShipLayout);
                showShipButtonView();
	                if(printDispatchNote){
	            	   Gloria.WarehouseApp.trigger('PrintDispatchNote:clicked', dispatchNoteId);
	            	   printDispatchNote = false;
	                 }    
				} else {
				 layout.inpickPane.show(pickShipLayout);
			}
				showShipGridView(pickShipLayout);
			});
		};
		
		/**
		 * Show Module View.
		 */
		var showModuleView = function() {
			require(['views/warehouse/common/WarehouseModuleView'], function(WarehouseModuleView) {
				var warehouseModuleView = new WarehouseModuleView({
					module : 'pickship',
					control : 'ship' // Button control
				});
				// Attach to moduleInfo region
                layout.moduleInfo.show(warehouseModuleView);
			});
		};
	
		/**
		 * Show Ship Button View
		 */
		var showShipButtonView = function() {
			require(['views/warehouse/pick/view/ShipButtonView'], function(ShipButtonView) {
				var shipButtonView = new ShipButtonView();
				pickShipLayout.button.show(shipButtonView);
			});
		};
	
		/**
		 * Show Ship Grid View
		 */
		var showShipGridView = function(parentView) {
			require(['views/warehouse/pick/view/ShipGridView'], function(ShipGridView) {
				var shipGridView = new ShipGridView({
					collection : requestListCollection,
					subModule : subModule
				});
				shipGridView.on('show', function() {
					processShipGridView();
				}, this);
				showGridView(parentView, shipGridView);
			});
		};
		
		/**
		 * Show Grid View
		 */
		var showGridView = function(parentView, gridView) {
			parentView.grid.show(gridView);
		};
	
		/**
		 * Process ShipGrid Information
		 */
		var processShipGridView = function() {			
			var statuses = (subModule == "ship"?"PICK_COMPLETED,READY_TO_SHIP,SHIPPED":"SENT");
			requestListCollection.url = '/material/v1/requestlists?statuses='+statuses+ '&whSiteId=' 
			+ UserHelper.getInstance().getDefaultWarehouse() + '&userId=' + UserHelper.getInstance().getUserId();
			requestListCollection.fetch();
			requestListCollection.on('change', function(e) {
				// update the ONLY model changed!
				var changedModelId = e.id;
				var model = requestListCollection.get(changedModelId);
				var currentReqListId = model.get('id');						
				model.url = '/material/v1/requestlists/' + currentReqListId ;
				model.save({}, {
					wait: true,
					silent: true,
					validationError : function(errorMessage, errors) {
						Gloria.trigger('showAppMessageView', {
							type : 'error',
							title : i18n.t('errormessages:general.title'),
							message : errorMessage
						});
					}
				});
			}, this);
		};
		
		/**
		 * showOpenDispatchNote View
		 */
		var showOpenDispatchNote = function(dispatchNoteId){
			// Initialize Data Source Objects
			initializeDataSourceDispatchNoteObjects();
			setOpenDispatchNoteLayout(dispatchNoteId);
		};
		
		/**
		 * showCreateDispatchNote View
		 */
		var showCreateDispatchNote = function(requestListId){
			// Initialize Data Source Objects
			initializeDataSourceDispatchNoteObjects();
			setCreateDispatchNoteLayout(requestListId);
		};

		/**
		 * Initialize Data Source DispatchNote Objects.
		 */
		var initializeDataSourceDispatchNoteObjects = function() {
			dispatchNoteModel = new DispatchNoteModel();
			requestListModel = new RequestListModel();
		};

		/**
		 * Set Page Layout for OpenDispatchNote
		 */
		var setOpenDispatchNoteLayout = function(dispatchNoteId) {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.trigger('showBreadcrumbView', {dispatchNoteNumber: ''});
			Gloria.basicLayout.content.reset();
			require(['views/warehouse/pick/view/CreateDispatchNoteView'],
			function(CreateDispatchNoteView) {
				createDispatchNoteView = new CreateDispatchNoteView({					
					isShipped : isShipped,
					mode: 'open'
				});
				Gloria.basicLayout.content.show(createDispatchNoteView);
				processDispatchNoteGeneralInfoLayout(dispatchNoteId, createDispatchNoteView);
			});
		};
		
		/**
		 * Set Page Layout for OpenDispatchNote
		 */
		var setCreateDispatchNoteLayout = function(requestListId) {
		    Gloria.basicModalLayout.closeAndReset();
			Gloria.trigger('showBreadcrumbView', {dispatchNoteNumber: ''});
			Gloria.basicLayout.content.reset();
			require(['views/warehouse/pick/view/CreateDispatchNoteView'],
			function(CreateDispatchNoteView) {
				createDispatchNoteView = new CreateDispatchNoteView({					
					isShipped : isShipped,
					mode: 'create'
				});
				Gloria.basicLayout.content.show(createDispatchNoteView);
				processCreateDispatchNoteGeneralInfoLayout(requestListId, createDispatchNoteView);
			});
		};
		
		/**
		 * LoadDispatchNoteAccordions -Part Info 
		 */
		var loadDispatchNotePartAccordion = function(mode) {
			if(mode == 'create') {
				processCreateDispatchNotePartInfoLayout(requestListId, createDispatchNoteView);	
			} else {
				processDispatchNotePartInfoLayout(dispatchNoteId, createDispatchNoteView);	
			}
		};
		
		/**
		 * Get GeneralInfo-DispatchNote from DispatchNoteDTO
		 */
		var processDispatchNoteGeneralInfoLayout = function(dispatchNoteId, parentView) {
			dispatchNoteModel.url = '/material/v1/dispatchnotes/'+ dispatchNoteId;
			dispatchNoteModel.fetch({
				success : function(model, res, settings) {
					if(settings.xhr.status == 204) { // Redirect to Page Not Found if resource is not available
						Gloria.trigger('Error:404');
						return;
					} else {					
						renderCreateEditDispatchNoteView(model, parentView);
						requestListId = model.get('requestListOId');
					}
				}
			});
		};
		
		var renderCreateEditDispatchNoteView = function(model, parentView){
			require(['views/warehouse/pick/view/DispatchNoteGeneralInfoView'], function(DispatchNoteGeneralInfoView) {
				requestListModel.url = '/material/v1/requestlists/'+requestListId; 
				requestListModel.fetch({
					success : function(response, res, settings) {
						if(settings.xhr.status == 204) { // Redirect to Page Not Found if resource is not available
							Gloria.trigger('Error:404');
							return;
						} else {
							var dispatchNoteGeneralInfoView = new DispatchNoteGeneralInfoView({	
								model : model,
								requestListModel : response,
								isShipped : isShipped
							});			
							parentView.generalInfo.show(dispatchNoteGeneralInfoView);
						}
					}
				});
			});
		};
		
		/**
		 * Get GeneralInfo-DispatchNote from DispatchNoteDTO
		 */
		var processCreateDispatchNoteGeneralInfoLayout = function(requestListId, parentView) {
			var model = new Model();	
			renderCreateEditDispatchNoteView(model, parentView);
		};
		
		/**
		 * Get PartInfo-DispatchNote comprises of RequestGroups & MaterialLines for a DispatchNoteId(RequestList)
		 * In Create Mode- Fetch Request Groups based on RequestListId
		 */
		var processDispatchNotePartInfoLayout = function(dispatchNoteId, parentView) {
			require(['views/warehouse/pick/view/DispatchNotePartInfoView'], function(DispatchNotePartInfoView) {
				var dispatchNoteRequestGroupCollection = new RequestGroupCollection();
				dispatchNoteRequestGroupCollection.url = '/material/v1/dispatchnotes/'+dispatchNoteId+'/requestgroups';
				dispatchNoteRequestGroupCollection.fetch({
					success : function(collection) {						
						dispatchNotePartInfoView = new DispatchNotePartInfoView({	
							collection: collection,
							isShipped : isShipped
						});
						parentView.partInfo.show(dispatchNotePartInfoView);
					}
				});
			});
		};
		
		/**
		 * Get PartInfo-DispatchNote comprises of RequestGroups & MaterialLines for a DispatchNoteId(RequestList)
		 */
		var processCreateDispatchNotePartInfoLayout = function(requestListId, parentView) {
			require(['views/warehouse/pick/view/DispatchNotePartInfoView'], function(DispatchNotePartInfoView) {
				var dispatchNoteRequestGroupCollection = new RequestGroupCollection();
				dispatchNoteRequestGroupCollection.url = '/material/v1/requestlists/'+requestListId+'/requestgroups';
				dispatchNoteRequestGroupCollection.fetch({
					success : function(collection) {						
						dispatchNotePartInfoView = new DispatchNotePartInfoView({	
							collection: collection,
							isShipped : isShipped
						});
						parentView.partInfo.show(dispatchNotePartInfoView);
					}
				});
			});
		};
	
		var showPartInformation = function(fetchedRequestGroupModel){
			showRequestGroupView(fetchedRequestGroupModel);
			showMaterialLinesGridView(fetchedRequestGroupModel.id);			
		};
		
		/**
		 * Get PartInfo-RequestGroup
		 */
		var showRequestGroupView = function(fetchedRequestGroupModel){			
			require(['views/warehouse/pick/view/RequestGroupInfoView'], function(RequestGroupInfoView) {				
				var requestGroupInfoView =  new RequestGroupInfoView({
					model:fetchedRequestGroupModel
				});
				var requestGroupRegionId = '#requestGroup_'+ fetchedRequestGroupModel.id;
				createDispatchNoteView.addRegion('requestGroupDiv', requestGroupRegionId);				
				createDispatchNoteView.requestGroupDiv.show(requestGroupInfoView);				
			});
		};
		
		/**
		 * Get PartInfo-MaterialLineGrid
		 */
		var showMaterialLinesGridView= function(fetchedRequestGroupModelId){		
			require(['views/warehouse/pick/view/MaterialLinesGridView'], function(MaterialLinesGridView) {	
				var materialLineCollection = new Collection();
				materialLineCollection.url = '/material/v1/requestgroups/'+fetchedRequestGroupModelId+ '/materials';
				materialLineCollection.fetch({
					success : function(collection) {						
						var materialLinesGridView = new MaterialLinesGridView({	
							collection: collection
						});
						var materialLineRegionId = '#materialLine_'+ fetchedRequestGroupModelId;
						createDispatchNoteView.addRegion('materialLineDiv', materialLineRegionId);				
						createDispatchNoteView.materialLineDiv.show(materialLinesGridView);	
					}
				});
			});
		};
		
		var getDispatchNote = function(dispatchNoteId, data, markAsShipped, print) {
			dispatchNoteModel= new Model();			
			dispatchNoteModel.url = '/material/v1/dispatchnotes/'+ dispatchNoteId;
			dispatchNoteModel.fetch({
				success : function(model) {	
					dispatchNoteModel= model;
					updateDispatchNote(data, markAsShipped, print);
				}
			});
		};
	
		/**
		 * Update DispatchNote
		 * When the user checks MarkAsShipped: change RequestLine(corresponding to DispatchNote) status to Shipped
		 */
		var updateDispatchNote = function(data, markAsShipped, print, mode) {
			var that = this;
			dispatchNoteModel.set(data);
			if (mode.toUpperCase() == 'OPEN') {
				dispatchNoteModel.url = '/material/v1/requestlists/' + requestListId
										+ '/dispatchnotes/' + dispatchNoteId + '?action=' + markAsShipped;
			} else {
				dispatchNoteModel.url = '/material/v1/requestlists/' + requestListId
										+ '/dispatchnotes?action=' + markAsShipped;
			}
			dispatchNoteModel.save({}, {
				success : function(resp) {
					if (print) {
						printDispatchNote = true;
						if (!(mode.toUpperCase() == 'OPEN')){
							dispatchNoteId = resp.id;
						};
						Gloria.WarehouseApp.trigger('RedirectToShipOverview');
					} else {
						Gloria.WarehouseApp.trigger('RedirectToShipOverview');
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
		 * Fetch DispatchNoteModel, RequestGroupCollection & its respective MaterialLineCollection 
		 */
		var printDispatchNoteClicked = function(dispatchNoteId) {
			if(!dispatchNoteModel) {
				dispatchNoteModel = new DispatchNoteModel();
				dispatchNoteModel.url = '/material/v1/dispatchnotes/' + dispatchNoteId ;
			}
			dispatchNoteModel.fetch({
				url : '/material/v1/dispatchnotes/'+ dispatchNoteId,
				success: function(response) {
					fetchRequestListForPrint(response);
				}
			});
		};
		
		var fetchRequestListForPrint = function(dispatchNoteModel) {
			var thisModel = new RequestListModel();
			thisModel.url = '/material/v1/requestlists/' + dispatchNoteModel.get('requestListOId'); 
			thisModel.fetch({
				success : function(response, res, settings) {
					Gloria.WarehouseApp.trigger('fetchRequestGroupForPrint', dispatchNoteModel, response);
				}
			});
		};
		
		/**
		 * Fetch RequestGroupCollection for a given DispatchNoteId For Print from ShipOverview/DispatchNoteDetails
		 */	
		var fetchRequestGroupForPrint = function(dispatchNoteModel, requestListModel) {
			var requestGroupCollection = new RequestGroupCollection();
			requestGroupCollection.url = '/material/v1/dispatchnotes/' + dispatchNoteModel.id + '/requestgroups';
			requestGroupCollection.fetch({			
				success : function(rgcollection) {
					Gloria.WarehouseApp.trigger('fetchMaterialLinesForPrint', dispatchNoteModel, requestListModel, rgcollection);
				}
			});
		};
		
		/**
         * Fetch MaterialLineCollection foreach RequestGroup For Print
         */
         var fetchMaterialLinesForPrint = function(dispatchNoteModel, requestListModel, requestGroupCollection){   
            var totalModelsToFetch = requestGroupCollection.length;
            var modelsFetched = 0;
            requestGroupCollection.each(function(model) {
              var materialLineCollection = new Collection();
              materialLineCollection.url = '/material/v1/requestgroups/'+ model.id + '/materials';
              materialLineCollection.fetch({
                 success : function(mlcollection) { 
                    modelsFetched++;
                    model.set('materialLineCollection', mlcollection.toJSON());
                    if(totalModelsToFetch == modelsFetched){
                           Gloria.WarehouseApp.trigger('printDispatchNote', dispatchNoteModel, requestListModel, requestGroupCollection);
                    }
                 }
              });
            });
         };

		
		/**
		 * Delete DispatchNote 
		 */
		var deleteDispatchNote = function(){
			dispatchNoteModel.destroy({
				url : '/material/v1/dispatchnotes/'+ dispatchNoteId,
				success : function() {
					Gloria.WarehouseApp.trigger('RedirectToShipOverview');
				}
			});
		};
	
		var redirectToShipOverview = function() {		
			Backbone.history.navigate('warehouse/ship', {
				trigger : true
			}); 
		};
		
		/**
		 * TODO Can we have collection save?
		 * RequestList status set to SHIPPED.
		 */
		var requestListsShipped = function(models) {
			var successCount = 0;
			_.each(models, function(model) {
				Backbone.sync('update', model, {
                    url : '/material/v1/requestlists/' + model.id + '?action=ship',
                    success : function(model) {  
                    	successCount ++;
                    	requestListCollection.add(model, {merge:true});
                    	if(successCount == models.length) {
                    		Gloria.WarehouseApp.trigger('ShipButton:refresh');
                    		Gloria.WarehouseApp.trigger('ShipGrid:clearselection');
                    		requestListCollection.fetch();
                    	}
                    }
		    	});
			});
		};
		
		var createDispatchNoteClicked = function(requestListModel) {
			var status =  requestListModel.attributes.status;
			if (requestListModel.get('status') == 'SHIPPED') {
				isShipped = true;
			} else {
				isShipped = false;
			}			
			Backbone.history.navigate('warehouse/ship/createDispatchNote/' + requestListModel.id, {
				trigger : true
			});
		};
		
		var OpenDispatchNoteButtonClicked = function(requestListModel) {
			if (requestListModel.get('status') == 'SHIPPED') {
				isShipped = true;
			} else {
				isShipped = false;
			}			
			Backbone.history.navigate('warehouse/ship/openDispatchNote/' + requestListModel.get('dispatchNoteId'), {
				trigger : true
			});	   
		};
		
		/**
		 * This method helps to fetch subgrid information for ship
		 * the input model should trigger 'subgrid:fetched' event once it is fetched
		 * which actually then listened by gloria-subgrid plug-in
		 */
		var fetchSubgridInfo = function(model) {
			model.subcollection = new Collection();
			model.subcollection.url = '/material/v1/requestlists/' + model.get('id') + '/requestgroups';
			model.subcollection.fetch({
				cache : false,
				success : function(collection) {
					model.trigger('subgrid:fetched', collection);
				}
			});
		};
		
		var exportProForma = function(id) {
            if (id) {
                window.location.href = '/GloriaUIServices/api/material/v1/requestlists/excel/' + id +"?whSiteId=" +  UserHelper.getInstance().getDefaultWarehouse();
            }
        };		
		
		Controller.ShipController = Marionette.Controller.extend({
	       
	    	initialize : function() {
	            this.initializeListeners();
	        },
	        
	        initializeListeners : function() {	        	
	        	this.listenTo(Gloria.WarehouseApp, 'DispatchNote:update', updateDispatchNote);	
	        	this.listenTo(Gloria.WarehouseApp, 'DispatchNote:delete', deleteDispatchNote);	
	        	this.listenTo(Gloria.WarehouseApp, 'RedirectToShipOverview', redirectToShipOverview);	
	        	this.listenTo(Gloria.WarehouseApp, 'MarkAsShipped:clicked', requestListsShipped);	
	        	this.listenTo(Gloria.WarehouseApp, 'DispatchButton:clicked', createDispatchNoteClicked);
	        	this.listenTo(Gloria.WarehouseApp, 'OpenDispatchNoteButton:clicked', OpenDispatchNoteButtonClicked);
	        	this.listenTo(Gloria.WarehouseApp, 'ShipGrid:Subgrid:fetch', fetchSubgridInfo);
	        	this.listenTo(Gloria.WarehouseApp, 'dispatchNote:loadDispatchNotePartAccordion', loadDispatchNotePartAccordion);
	        	this.listenTo(Gloria.WarehouseApp, 'dispatchNote:showPartInformation', showPartInformation);
	        	this.listenTo(Gloria.WarehouseApp, 'PrintDispatchNote:clicked', printDispatchNoteClicked);	      
	         	this.listenTo(Gloria.WarehouseApp, 'fetchRequestGroupForPrint', fetchRequestGroupForPrint);	
	        	this.listenTo(Gloria.WarehouseApp, 'fetchMaterialLinesForPrint', fetchMaterialLinesForPrint);
	        	this.listenTo(Gloria.WarehouseApp, 'Ship:ExportProForma', exportProForma);
	        },
	        
	        control : function(options) {
	        	 options || (options = {});
	        	 if (options.requestListId) {
					showCreateDispatchNote.call(this, options.requestListId);
					requestListId = options.requestListId;
				} else if (options.dispatchNoteId) {
					showOpenDispatchNote.call(this, options.dispatchNoteId);
					dispatchNoteId = options.dispatchNoteId;
				} else {
					subModule = options.subModule;
					prepareShip.call(this);
				}
            },
            
            onDestroy: function() {
        		requestListCollection = null;
        		requestListId = null;
        		layout = null;
        		pickShipLayout = null;
        		dispatchNoteModel = null;
        		requestListModel = null;
        		dispatchNoteId = null;
        		isShipped = null;
        		createDispatchNoteView = null;
        		dispatchNotePartInfoView = null;
        		printDispatchNote = null;
        		subModule = null;
            }
	    });
	});
	
	return Gloria.WarehouseApp.Controller.ShipController;
});
