define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'utils/backbone/GloriaModel',
        'utils/backbone/GloriaCollection',
        'collections/DeliveryNoteLineCollection',
        'models/DeliveryNoteLineModel',
        'utils/UserHelper',
        'i18next'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Model, Collection, DeliveryNoteLineCollection, DeliveryNoteLineModel, UserHelper, i18n) {

	Gloria.module('WarehouseApp.Receive.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var deliveryNoteLineOverViewLayout = undefined;
		var deliveryNoteLineCollection = undefined;
		var module;
		var deliveryNoteModel;
		var deliveryNoteId;
		var parentRegion;
		var receiveType;
		var transportLabels = new Collection();

		var preparePartInformation = function() {
			var that = this;
			require(['views/warehouse/receive/deliverynotelines/view/DeliveryNoteLineOverViewLayout',
			         'views/warehouse/receive/deliverynotelines/view/ControlButtonView',
			         'views/warehouse/receive/deliverynotelines/view/DeliveryNoteLineGridView'],
			function(DeliveryNoteLineOverViewLayout, ControlButtonView, DeliveryNoteLineGridView) {
				// Instantiate Layout
				deliveryNoteLineOverViewLayout = new DeliveryNoteLineOverViewLayout();
				// Add Layout to the main content region.
				parentRegion.show(deliveryNoteLineOverViewLayout);
				
				var controlButtonView = new ControlButtonView();
                deliveryNoteLineOverViewLayout.buttonPane.show(controlButtonView);
				deliveryNoteLineCollection = new DeliveryNoteLineCollection([], {
					state : {
    					pageSize : 100,
    					currentPage : 1
    				}
                });
				showDeliveryNoteLineGrid();
			});
		};
		
		var showDeliveryNoteLineGrid = function() {
		    require(['views/warehouse/receive/deliverynotelines/view/DeliveryNoteLineGridView'], function(DeliveryNoteLineGridView) {
		        var deliveryNoteLineGridView = new DeliveryNoteLineGridView({
		        	deliveryNoteModel : deliveryNoteModel,
		        	collection : deliveryNoteLineCollection,
                    transportLabels : transportLabels
                });
		        receiveType = deliveryNoteModel.get('receiveType');
		        deliveryNoteLineGridView.on('show', function() {
		        	processGridInfo();
				}, this);
		        deliveryNoteLineOverViewLayout.gridPane.show(deliveryNoteLineGridView);
		    });
		};

		var processGridInfo = function() {
		    deliveryNoteLineCollection.url = '/material/v1/deliverynotes/' + deliveryNoteId + '/deliverynotelines?whSiteId=' 
    		+ UserHelper.getInstance().getDefaultWarehouse()+'&receiveType='+receiveType;
			if (!deliveryNoteModel.isNew()) {
			    deliveryNoteLineCollection.fetch({
					success : function(collection) {
					    Gloria.WarehouseApp.trigger('loaded:deliveryNoteLine', collection);
					}
				});
			}
		};
		
		var fetchDeliveryNoteSublines = function(options) {
			var model = options.model;
			var recieveType = model.receiveType;
		    model.directsends.fetch({
	            url: '/material/v1/deliverynotelines/' + model.id + '/deliverynotesublines?whSiteId=' 
        		+ UserHelper.getInstance().getDefaultWarehouse(),
        		async : false,
	            success: function() {
	    	    	processTransPortLabelInfo();
				}
	        });
		    
		    model.directsends.on('backgrid:edit', function(thisModel) {
				Gloria.WarehouseApp.trigger('deliveryNoteLine:start:update');
			}, this);
		    
	    	this.listenTo(model.directsends, 'backgrid:edited', function(directsend, column, command) {
	    		if(command.keyCode !== 27) {
	    			saveDeliveryNoteLine(directsend);
	    		} else {
	    			Gloria.WarehouseApp.trigger('deliveryNoteLine:complete:update', directsend);
	    		}
	        });
			return model.directsends;
		};
				
		var processTransPortLabelInfo = function() {
			transportLabels.url = '/material/v1/transportlabels?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
			transportLabels.fetch({
				success : function() {
					Gloria.WarehouseApp.trigger('TransportLabel:fetched', transportLabels);
				}
			});
		};
		
		var saveDeliveryNoteLine = function(model) {
			var errors = [];
			var isValidate = false;
			if ((receiveType === "REGULAR")) {
				validateDeliveryNoteLineModel(model, errors);
				isValidate = true;
			}
			if (errors.length) {
				errors = _.map(_.uniq(errors), function(err) {
					return {
						message : err
					};
				});
				showError(null, errors, true);
			} else {
				model.save(null, {
					url: '/material/v1/deliverynotelines/' + model.id + '/deliverynotesublines/' + model.id,
					silent : false,
					type : 'PUT',
					validate : isValidate,
					beforeSend : function() {
						Gloria.WarehouseApp.trigger('deliveryNoteLine:start:update', model);
					},
					complete : function() {
						Gloria.WarehouseApp.trigger('deliveryNoteLine:complete:update', model);
						Gloria.WarehouseApp.trigger('deliveryNoteLine:directsend:updated', model);
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
		
		var validateDeliveryNoteLineModel = function(model, errors) {
            if(!model.isValid()) {
                model.trigger('backgrid:column:error', model, model.validationError.attr, {highlight: true});
                errors.push(model.validationError.message);                 
            }
		};
		
		var showError = function(errorMessage, errors, localize) {
            Gloria.trigger('showAppMessageView', {
                type : 'error',
                title : i18n.t('errormessages:general.title'),
                message : errors,
                localize: localize
            });
		};
		
		var receiveDeliveryNoteLine = function(data) {
		    var errors = [];
		    if((receiveType === "REGULAR")){
			    deliveryNoteLineCollection.forEach(function(model){
			        validateDeliveryNoteLineModel(model, errors);
			    });
		    }
		    
		    if(errors.length) {		        
		        errors = _.map(_.uniq(errors), function(err){
		            return {message: err};
		        });
		        showError(null, errors, true);
		    } else {		        
		        deliveryNoteLineCollection.save({
		            type: 'PUT',
		            url: '/material/v1/deliverynotelines?receive=true',
                    success : function(collection) {
                        Backbone.history.navigate('warehouse/receive/toReceive', {trigger: true});
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
		
		var cancelDeliveryNoteLine = function() {
			Backbone.sync('delete', deliveryNoteLineCollection, {
				url : '/material/v1/deliverynotes/' + deliveryNoteId + '/deliverynotelines/'
						+ _.pluck(deliveryNoteLineCollection.models, 'id').join(','),
				success : function() {
                    window.history.back();
				}
			});
		};
		
        var showAddInformationForm = function(selectedId) {
        	Backbone.history.navigate('warehouse/receive/details/' + selectedId, {
                trigger : true
            });
        };
        
        var showCreateTransportLabelPopup = function(){
        	require(['views/warehouse/common/CreateTransportLabelView'], function(CreateTransportLabelView) {
				var createTransportLabelView = new CreateTransportLabelView();
				Gloria.basicModalLayout.content.show(createTransportLabelView);
			});
        };        
        
        var createTransportLabel = function(number) {
        	transportLabels.url = '/material/v1/transportlabels/?action=create&whSiteId=' 
        		+ UserHelper.getInstance().getDefaultWarehouse() + '&transportLabelCopies=' + number;
        	transportLabels.fetch({
        		success : function(response) {        			
        			processTransPortLabelInfo();
        			printCreatedTransportLabels(response);
				}
        	});
		};
        
        var prepareTransportLabel = function() {
        	var transportlabels = new Collection();
        	require(['views/warehouse/receive/deliverynotelines/view/PrintTLModalView'], function(PrintTLModalView) {
				var printTLModalView = new PrintTLModalView({
					collection : transportlabels
				});
				printTLModalView.on('show', function() {
					processTransportLabel(transportlabels);
				});
				Gloria.basicModalLayout.content.show(printTLModalView);
			});
        };
        
        var processTransportLabel = function(transportlabels) {
        	transportlabels.url = '/material/v1/transportlabels?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
        	transportlabels.fetch();
		};
		
		var printCreatedTransportLabels = function(transportlabels){
			transportlabels.each(function(model){
				printTransportLabel(model.id);
			});
		};
		
		var printTransportLabel = function(transportlabelID) {
			var model = new Model();
			model.url = '/material/v1/transportlabels/' + transportlabelID + '?action=printLabel&whSiteId=' + 
			    UserHelper.getInstance().getDefaultWarehouse(); //OK
        	model.fetch({
        		success : function() {
        			Gloria.WarehouseApp.trigger('DeliveryNoteLine:printTL:printed', true);
				},
				validationError : function(errorMessage, errors) {
					var errorMessage = new Array();
					var item = {
							message : i18n.t('Gloria.i18n.errors.GLO_ERR_69')
						};
					errorMessage.push(item);
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : errorMessage
		    		});
                },
				error : function() {
					Gloria.WarehouseApp.trigger('DeliveryNoteLine:printTL:printed', false);
				}
        	});
		};
		
		var prepareEditPartInfo = function(selectedModels) {
        	var col = new Collection();
        	require(['views/warehouse/receive/deliverynotelines/view/EditPartInfoModalView'], function(EditPartInfoModalView) {
				var editPartInfoModalView = new EditPartInfoModalView({
					selectedModels : selectedModels,
					collection : col
				});
				editPartInfoModalView.on('show', function() {
					processTransportLabel(col);
				});
				Gloria.basicModalLayout.content.show(editPartInfoModalView);
			});
        };
		
		var savePartInfo = function(selectedModels, transportLabelId) {
//			var col = new Collection();
//			_.each(selectedModels, function(model) {
//				col.add(deliveryNoteLineCollection.where({orderLineId: model.get('orderLineId')}));
//			});
//			col.invoke('set', {'transportLabelId': transportLabelId});
//			Backbone.sync('update', col, {
//				url : '/material/v1/deliverynotelines',
//				success : function(response) {
//					processGridInfo();
//				}
//			});
			_.each(selectedModels, function(model) {
				_.each(model.directsends.models, function(directsend) {
					directsend.url = '/material/v1/deliverynotelines/' + model.id + '/deliverynotesublines/' + directsend.id;
	            	directsend.save({transportLabelId: transportLabelId});
				});
			});
		};
		
		var preparePartLabel = function(models) {
			require(['views/warehouse/receive/deliverynotelines/view/PrintPLModalView'], function(PrintPLModalView) {
				var printPLModalView = new PrintPLModalView({
					models : models
				});
				Gloria.basicModalLayout.content.show(printPLModalView);
			});
		};
		
		var printPartLabel = function(models, printQty) {
		    var collection = new Collection();
		    collection.add(models);
			collection.save({
				url : '/common/v1/deliverynotelines/partlabels?whSiteId='
					+ UserHelper.getInstance().getDefaultWarehouse() + (printQty ? ('&quantity=' + printQty) : ''),
        		success : function(response) {
        			console.log('Printed!');
        		},
        		validationError : function(errorMessage, errors) {
					var errorMessage = new Array();
					var item = {
							message : i18n.t('Gloria.i18n.errors.GLO_ERR_69')
						};
					errorMessage.push(item);
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : errorMessage
		    		});
                }
        	});
		};
		
		Controller.OverviewController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:addInformation', showAddInformationForm);
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:editInformation', prepareEditPartInfo);
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:PartInfo:save', savePartInfo);
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:createTransLabel', showCreateTransportLabelPopup);
				this.listenTo(Gloria.WarehouseApp, 'CreateNumberOfTransportLabel', createTransportLabel);
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:printTL', prepareTransportLabel);
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:printTL:print', printTransportLabel);
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:printPL', preparePartLabel);
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:printPL:print', printPartLabel);
				this.listenTo(Gloria.WarehouseApp, 'TransportLabel:change', saveDeliveryNoteLine);
				this.listenTo(Gloria.WarehouseApp, 'receive:deliveryNoteLine', receiveDeliveryNoteLine);
				this.listenTo(Gloria.WarehouseApp, 'cancel:deliveryNoteLine', cancelDeliveryNoteLine);
				this.listenTo(Gloria.WarehouseApp, 'ZoneCell:change', saveDeliveryNoteLine);
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:sublines:fetch', fetchDeliveryNoteSublines);
				this.listenTo(Gloria.WarehouseApp, 'BinLocation:change', saveDeliveryNoteLine);
			},

			control : function(options) {
				options || (options = {});
				deliveryNoteModel = options.deliveryNoteModel;
				deliveryNoteId = deliveryNoteModel.id;
				parentRegion = options.parentRegion;				
				preparePartInformation.call(this);
			},
			
			onDestroy: function() {
				deliveryNoteLineOverViewLayout = null;
			    deliveryNoteLineCollection = null;
		        module = null;
		        deliveryNoteModel = null;
		        deliveryNoteId = null;
		        parentRegion = null;
			}
		});
	});

	return Gloria.WarehouseApp.Receive.Controller.OverviewController;
});
