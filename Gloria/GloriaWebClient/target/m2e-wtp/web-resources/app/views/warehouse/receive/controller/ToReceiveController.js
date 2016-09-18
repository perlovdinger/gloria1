define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',        
        'utils/UserHelper',
        'collections/WarehouseMaterialLineCollection',
        'models/WarehouseMaterialLineModel',
        'views/warehouse/receive/deliverynotelines/DeliveryNoteLinesModule'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, WarehouseMaterialLineCollection, WarehouseMaterialLineModel, DeliveryNoteLinesModule) {
    
    Gloria.module('WarehouseApp.Receive.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
    
        var deliveryNote;
        var orderModel;
        var deliveryNoteTransferReturnModel;
        var orderCollection;
        var orderLineCollection;
        var deliveryNoteTransferReturnCollection;
        var deliveryNoteModel;
        var layout;
        var toReceiveLayout;
        var deliveryNoteInformationView;
        var selectedReceiveType;
            
        var setupLayout = function() {            
            var that = this; 
            Gloria.basicModalLayout.closeAndReset();
            Gloria.trigger('showBreadcrumbView', deliveryNote ? {itemId: deliveryNote} : undefined);            
            require(['views/warehouse/receive/view/Layout', 'views/warehouse/receive/view/ToReceiveLayout'], function(Layout, ToReceiveLayout) {
                layout = new Layout({
                	module : 'toReceive'
                });
                toReceiveLayout = new ToReceiveLayout();
                layout.on('show', function() {
					showModuleView();
				}, this);
                
                Gloria.basicLayout.content.show(layout);
                layout.toReceivePane.show(toReceiveLayout);
                if(deliveryNote) {
                    showGeneralAndPartInformation.call(that);
                } else {
                    showGeneralInformation.call(that);
                }
            });        
        };
        
        /**
		 * Show Module View.
		 */
		var showModuleView = function() {
			require(['views/warehouse/common/WarehouseModuleView'], function(WarehouseModuleView) {
				var warehouseModuleView = new WarehouseModuleView({
					module : 'receive',
					control : 'toReceive' // Button control
				});
				// Attach to moduleInfo region
	            layout.moduleInfo.show(warehouseModuleView);
			});
		};
        
        var showGeneralInformation = function() {
            require(['models/DeliveryNoteModel', 'models/OrderModel', 'views/warehouse/collection/OrderCollection', 
                     'views/warehouse/receive/view/DeliveryNoteInformationView'], 
                    function(DeliveryNoteModel, OrderModel, OrderCollection, DeliveryNoteInformationView) {
                deliveryNoteModel = new DeliveryNoteModel();
                orderModel = new OrderModel();
                deliveryNoteTransferReturnModel = new WarehouseMaterialLineModel();
                orderCollection = new OrderCollection();
                selectedReceiveType = 'REGULAR';
                
                var viewOptions = {
                    model : deliveryNoteModel,
                    orderModel : orderModel,
                    deliveryNoteTransferReturnModel: deliveryNoteTransferReturnModel,
                    selectedReceiveType :selectedReceiveType
                };
                deliveryNoteInformationView = new DeliveryNoteInformationView(viewOptions);
                toReceiveLayout.general.show(deliveryNoteInformationView);
            });            
        };
        
        var showGeneralAndPartInformation = function() {
            require(['models/DeliveryNoteModel', 'views/warehouse/receive/view/DeliveryNoteInformationView'], 
                    function(DeliveryNoteModel, DeliveryNoteInformationView) {
                deliveryNoteModel = new DeliveryNoteModel({id: deliveryNote});                
                deliveryNoteModel.fetch({
                	data : {
						whSiteId : UserHelper.getInstance().getDefaultWarehouse()
					},
					success : function(model, res, settings) {
						if(settings.xhr.status == 204) { // Redirect to Page Not Found if resource is not available
							Gloria.trigger('Error:404');
							return;
						} else {
							var viewOptions = {
			                    model: deliveryNoteModel,
			                    selectedReceiveType: deliveryNoteModel.get('receiveType')
			                };
			                deliveryNoteInformationView = new DeliveryNoteInformationView(viewOptions);                
			                toReceiveLayout.general.show(deliveryNoteInformationView);
			                DeliveryNoteLinesModule.stop();
			                DeliveryNoteLinesModule.start({
			                    deliveryNoteModel: deliveryNoteModel,
			                    parentRegion: toReceiveLayout.grid
			                });
						}
					}
                });
            }); 
        };
        
        var openDeliveryNoteTransferReturn = function(defaultDeliveryNoteNumber, selectedDropdownValue){
        	require(['views/warehouse/receive/view/DeliveryNotePopupView', 
                     'views/warehouse/receive/view/DeliveryNoteTransferReturnGridView'], 
                    function(DeliveryNotePopupView, DeliveryNoteTransferReturnGridView){
        		selectedReceiveType = selectedDropdownValue;
                
            	deliveryNoteTransferReturnCollection = new WarehouseMaterialLineCollection([], {
		        		state : {
							pageSize : function() {	// Check if any pageSize is already stored
								var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.DeliveryNoteLineTransferReturngrid'
										 + '.' + UserHelper.getInstance().getUserId()));
								return (object && object['pageSize']) || 10; // Default 10
							}(),
							currentPage : 1
						},
                    	url: '/warehouse/v1/materiallines/transferReturn',                    	
                    	beforeFetch: function(collection) {
                    		if(!collection || !collection.queryParams) return false;
                    		var search = collection.queryParams.search;
                    		if(!search || search.length == 0) return false;
                    		return true;
                    	}
                });                    
                
                
                delete deliveryNoteTransferReturnCollection.queryParams.search;
                
                var deliveryNoteTransferReturnGridView = new DeliveryNoteTransferReturnGridView({
                    collection: deliveryNoteTransferReturnCollection,
                    selectedReceiveType: selectedReceiveType
                });
                
                var deliveryNotePopupView = new DeliveryNotePopupView();
                
                Gloria.basicModalLayout.content.show(deliveryNotePopupView);
                deliveryNotePopupView.grid.show(deliveryNoteTransferReturnGridView);                                
            });
        };
        
        var searchDeliveryNoteTransferReturn= function(search) {
            if(search && deliveryNoteTransferReturnCollection){
            	var user = UserHelper.getInstance().getUser();    		
    			var whSite = UserHelper.getInstance().getDefaultWarehouse();
    			if(selectedReceiveType === 'TRANSFER') {                       
                    deliveryNoteTransferReturnCollection.queryParams.deliveryAddressId = whSite;
                    deliveryNoteTransferReturnCollection.queryParams.shipmentType = 'TRANSFER';
                    deliveryNoteTransferReturnCollection.queryParams.status = 'IN_TRANSFER';
                    delete deliveryNoteTransferReturnCollection.queryParams.whSiteId;
    			} else if(selectedReceiveType ===  'RETURN'){                         
                    deliveryNoteTransferReturnCollection.queryParams.whSiteId = whSite;
                    deliveryNoteTransferReturnCollection.queryParams.shipmentType = 'SHIPMENT';
                    deliveryNoteTransferReturnCollection.queryParams.status = 'SHIPPED';
                    delete deliveryNoteTransferReturnCollection.queryParams.deliveryAddressId;
    			} else if(selectedReceiveType ===  'RETURN_TRANSFER'){                         
                    deliveryNoteTransferReturnCollection.queryParams.shipmentType = 'TRANSFER';
                    deliveryNoteTransferReturnCollection.queryParams.status = 'IN_TRANSFER';
                    deliveryNoteTransferReturnCollection.queryParams.whSiteId = whSite;
                    delete deliveryNoteTransferReturnCollection.queryParams.deliveryAddressId;
                } 
            	deliveryNoteTransferReturnCollection.queryParams.search = search;
            	deliveryNoteTransferReturnCollection.fetch({
    				reset : true,
    				wait : true
    			});	
            }
        };
        
        var selectDeliveryNoteTransferReturn = function(selectedLineId) {
            var tempModel;
            if(selectedLineId && deliveryNoteTransferReturnCollection && (tempModel = deliveryNoteTransferReturnCollection.get(selectedLineId))){
            	deliveryNoteTransferReturnModel.clear();
            	deliveryNoteTransferReturnModel.set(tempModel.pick(['id', 'dispatchNoteNo', 'parmaID', 'parmaName', 'projectId', 'transportationNo', 'carrier']));
            }
        };
        
        var openOrderLookupPopup = function(defaultOrderNumber) {
            require(['views/warehouse/collection/OrderLineCollection', 'views/warehouse/receive/view/OrderPopupView', 'views/warehouse/receive/view/OrderLinesGridView'], 
            function(OrderLineCollection, OrderPopupView, OrderLinesGridView) {
                if(!orderLineCollection){
                    orderLineCollection = new OrderLineCollection([], {
                    	state : {
        					pageSize : function() {	// Check if any pageSize is already stored
        						var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.OrderLinesGrid'
        								 + '.' + UserHelper.getInstance().getUserId()));
        						return (object && object['pageSize']) || 10; // Default 10
        					}(),
        					currentPage : 1
        				},
                    	url: '/warehouse/v1/orderlines/current?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(), //OK                   	
                    	beforeFetch: function(collection) {
                    		if(!collection || !collection.queryParams) return false;
                    		var orderQueryStr = collection.queryParams.orderQueryStr;
                    		if(!orderQueryStr || orderQueryStr.length == 0) return false;
                    		return true;
                    	}
                    });                    
                } else {
                    orderLineCollection.reset();
                }
                
                delete orderLineCollection.queryParams.orderQueryStr;
                
                var orderLinesGridView = new OrderLinesGridView({
                    collection: orderLineCollection
                });
                
                var orderPopupView = new OrderPopupView();
                
                Gloria.basicModalLayout.content.show(orderPopupView);
                orderPopupView.grid.show(orderLinesGridView);                                
            });
        };
        
        var searchOrderLine = function(orderQueryStr) {
            if(orderQueryStr && orderLineCollection){
            	orderLineCollection.state.currentPage = 1;
            	orderLineCollection.queryParams.status = 'PLACED,RECEIVED_PARTLY';
                orderLineCollection.queryParams.orderQueryStr = orderQueryStr;
    			orderLineCollection.fetch({
    				reset : true
    			});	
            }
        };
        
        var selectOrderLine = function(orderLineID) {
            var tempModel;
            if(orderLineID && orderLineCollection && (tempModel = orderLineCollection.get(orderLineID))){
                orderModel.clear();
                orderModel.set(tempModel.pick(['id', 'orderNo', 'supplierId', 'supplierName', 'projectId']));
            }
        };
        
        var loadOrder = function(orderNumber) {
            var tempModel;
            // check the order collection first to find the model
            if(orderCollection && (tempModel = orderCollection.findWhere({orderNo: orderNumber}))) {                
                 orderModel.set(tempModel.toJSON());
            } else {            
                orderCollection.fetch({
                    data: {orderNo: orderNumber},
                    success: function(collection) {
                        if(collection && collection.length > 0) {
                            orderModel.set(collection.at(0).toJSON());
                        } else {                            
                            orderModel.clear();
                            orderModel.set({orderNo: orderNumber});
                            Gloria.WarehouseApp.trigger('NoRecordFound');
                        }
                    }
                }); 
            }            
        };
        
        var showError = function(errorMessage, errors) {
            Gloria.trigger('showAppMessageView', {
                type : 'error',
                title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'),
                message : errorMessage
            });
        };
        
        var saveDeliveryNote = function(formData) {
        	deliveryNoteModel.set('whSiteId', UserHelper.getInstance().getDefaultWarehouse());
            deliveryNoteModel.save(formData, {
                type: 'PUT',
                success : function(model) {
                    Backbone.history.navigate('warehouse/receive/toReceive/'+model.get('id'), {trigger: true});                    
                },                           
                validationError : showError
            });
        };
        
        var loadOrderByDispatchNumber = function(dispatchNumber, receiveType) {
        	selectedReceiveType = receiveType;
    		if(!deliveryNoteTransferReturnCollection) {
    			deliveryNoteTransferReturnCollection = new WarehouseMaterialLineCollection([], {
                	url: '/warehouse/v1/materiallines/transferReturn'
        		});
    		}
    		if(dispatchNumber) {
            	var user = UserHelper.getInstance().getUser();    		
    			var whSite = UserHelper.getInstance().getDefaultWarehouse();
    			if(selectedReceiveType === 'TRANSFER') {                       
                    deliveryNoteTransferReturnCollection.queryParams.deliveryAddressId = whSite;
                    deliveryNoteTransferReturnCollection.queryParams.shipmentType = 'TRANSFER';
                    deliveryNoteTransferReturnCollection.queryParams.status = 'IN_TRANSFER';
                    delete deliveryNoteTransferReturnCollection.queryParams.whSiteId;
    			} else if(selectedReceiveType ===  'RETURN') {                         
                    deliveryNoteTransferReturnCollection.queryParams.whSiteId = whSite;
                    deliveryNoteTransferReturnCollection.queryParams.shipmentType = 'SHIPMENT';
                    deliveryNoteTransferReturnCollection.queryParams.status = 'SHIPPED';
                    delete deliveryNoteTransferReturnCollection.queryParams.deliveryAddressId;
    			} else if(selectedReceiveType ===  'RETURN_TRANSFER') {                         
                    deliveryNoteTransferReturnCollection.queryParams.shipmentType = 'TRANSFER';
                    deliveryNoteTransferReturnCollection.queryParams.status = 'IN_TRANSFER';
                    deliveryNoteTransferReturnCollection.queryParams.whSiteId = whSite;
                    delete deliveryNoteTransferReturnCollection.queryParams.deliveryAddressId;
                }
            	deliveryNoteTransferReturnCollection.queryParams.search = dispatchNumber;
            	deliveryNoteTransferReturnCollection.fetch({
    				reset : true,
    				wait : true,
    				success : function(response) {
    					if(response && response.length > 0) {
    						var exactModel = response.findWhere({dispatchNoteNo: dispatchNumber});
    						exactModel && deliveryNoteTransferReturnModel.set(exactModel.toJSON());
                        } else {                            
                        	deliveryNoteTransferReturnModel.clear();
                            orderModel.set({dispatchNoteNo: dispatchNumber});
                        }
					}
    			});
    		}
        	return deliveryNoteTransferReturnModel;
		};
        
        Controller.ToReceive = Marionette.Controller.extend({
            
            initialize : function() {
                this.initializeListeners();
            },
            
            initializeListeners : function() {              
                this.listenTo(Gloria.WarehouseApp, 'search:order', loadOrder);  
                this.listenTo(Gloria.WarehouseApp, 'save:deliveryNote', saveDeliveryNote);
                this.listenTo(Gloria.WarehouseApp, 'popup:order', openOrderLookupPopup);
                this.listenTo(Gloria.WarehouseApp, 'popup:deliveryNoteTransferReturn',  openDeliveryNoteTransferReturn);
                this.listenTo(Gloria.WarehouseApp, 'search:orderline', searchOrderLine);
                this.listenTo(Gloria.WarehouseApp, 'search:deliveryNoteTransferReturn', searchDeliveryNoteTransferReturn);
                this.listenTo(Gloria.WarehouseApp, 'select:orderline', selectOrderLine);
                this.listenTo(Gloria.WarehouseApp, 'select:deliveryNoteTransferReturn', selectDeliveryNoteTransferReturn);
                this.listenTo(Gloria.WarehouseApp, 'search:dispatchNumber', loadOrderByDispatchNumber);
            },
            
            control : function(deliveryNoteId) {
                deliveryNote = deliveryNoteId;
                setupLayout.call(this);                
            },
            
            onDestroy : function() {
                deliveryNote = null;
                orderModel = null;
                orderCollection = null;
                orderLineCollection = null;
                deliveryNoteModel = null;
            	DeliveryNoteLinesModule.stop();
            }
        });    
    });
    
    return Gloria.WarehouseApp.Receive.Controller.ToReceive;
});
