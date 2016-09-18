define(['app',
        'jquery',
        'underscore',
        'i18next',
        'handlebars',
        'backbone',
        'marionette',
        'collections/MaterialLineQICollection',
        'utils/backbone/GloriaModel',
        'utils/UserHelper',
        'utils/backbone/GloriaCollection'
],function(Gloria, $, _, i18n, Handlebars, Backbone, Marionette, MaterialLineQICollection, GloriaModel, UserHelper, Collection) {

    Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
        
        var materialLineQICollection;
        var module;
        var whSiteId;
        var localStorage = window.localStorage;
        
        /**
         * Prepare Overview Request. Initialize Data Source Objects which are going to be used as
         * data transfer objects and also set the page layout.
         */
        var prepareOverview = function() {
            require([ 'collections/WarehouseCollection' ], function(WarehouseCollection) {
                var isSiteWarehouseMapped = false;
                var warehouseCollection = new WarehouseCollection();
                warehouseCollection.url = '/warehouse/v1/users/' + UserHelper.getInstance().getUserId() + '/warehouses';
                warehouseCollection.fetch({
                    async : false,
                    success : function(collection) {
                        collection.each(function(model) {
                            if (model.get('siteId') == UserHelper.getInstance().getDefaultWarehouse()) {
                                isSiteWarehouseMapped = true;
                                localStorage.setItem('Gloria.User.DefaultWarehouse.QISupport', (model.get('qiSupported')));
                            }
                        });
                        
                        if(UserHelper.getInstance().hasUserRole('IT_SUPPORT')) { //TODO : Check what should be status when qi support is false
                            localStorage.setItem('Gloria.User.DefaultWarehouse.QISupport', true);
                        } else {
                            if (!isSiteWarehouseMapped) {
                                localStorage.setItem('Gloria.User.DefaultWarehouse.QISupport', false);
                            }
                        }
                        
                        var isWHQISupport =  localStorage.getItem('Gloria.User.DefaultWarehouse.QISupport');
                        if(isWHQISupport != "true") {
                            module = 'blockedPart';
                        } else if(!module) {
                            module = 'inStock';
                        }
                        Backbone.history.navigate('warehouse/qualityinspection/' + module, {
                            replace: true
                        });
                        // Initialize Data Source Objects
                        initializeDataSourceObjects();
                        // Set Page Layout
                        setPageLayout();
                    }
                });
            });
        };

        /**
         * Initialize Data Source Objects. These objects are going to be used by the page/components.
         */
        var initializeDataSourceObjects = function() {  
            var whichGrid = '';
            materialLineQICollection = new MaterialLineQICollection([], {
                state : {
                    pageSize : function() { // Check if any pageSize is already stored
                        switch (module) {
                        case 'inStock':
                             whichGrid = 'InStockGrid';
                             break;    
                        case 'mandatory':
                            whichGrid = 'MandatoryGrid';
                            break;
                        case 'optional':
                            whichGrid = 'OptionalGrid';
                            break;
                        case 'blockedPart':
                            whichGrid = 'BlockedPartGrid';
                            break;
                        case 'setMarking':
                            whichGrid = 'SetMarkingGrid';
                            break;
                        default:
                            break;
                        }
                        var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.' + whichGrid
                                 + '.' + UserHelper.getInstance().getUserId()));
                        return (object && object['pageSize']) || 10; // Default 10
                    }(),
                    currentPage : 1
                },
                filterKey : whichGrid
            });
            whSiteId = UserHelper.getInstance().getDefaultWarehouse();
        };

        /**
         * Set Page Layout. QualityInspectionOverview is the main Layout which adds two regions: moduleInfo & gridInfo to the page,
         * so that the respective views can be attached later on!
         */
        var setPageLayout = function() {
            var that = this;
            Gloria.basicModalLayout.closeAndReset();
            Gloria.trigger('showBreadcrumbView');
            Gloria.basicLayout.content.reset();
            require(['views/warehouse/qualityinspection/overview/view/QualityInspectionOverview'], function(QualityInspectionOverview) {
                var isWHQISupport =  localStorage.getItem('Gloria.User.DefaultWarehouse.QISupport');
                qualityInspectionOverview = new QualityInspectionOverview({
                    module: module,
                    isWHQISupport : isWHQISupport == "true" ? true : false
                });
                qualityInspectionOverview.on('show', function() {
                    showModuleView();
                    showOverviewButtonView.call(that, module);
                    showOverviewTabs.call(that, module);
                });
                Gloria.basicLayout.content.show(qualityInspectionOverview);
            });
        };
        
        /**
         * Show Module View.
         */
        var showModuleView = function() {
            require(['views/warehouse/common/WarehouseModuleView'], function(WarehouseModuleView) {
                var warehouseModuleView = new WarehouseModuleView({
                    module : 'qualityinspection'
                });
                // Attach to moduleInfo region
                try {
                    qualityInspectionOverview.moduleInfo.show(warehouseModuleView);
                } catch (e) {}
            });
        };

        /**
         * Show/Render OverviewButtonView
         */
        var showOverviewButtonView = function(subModule) {
            require(['views/warehouse/qualityinspection/overview/view/OverviewButtonView'], function(OverviewButtonView) {
                overviewButtonView = new OverviewButtonView({
                    module : subModule,
                    collection: materialLineQICollection
                });
                if (qualityInspectionOverview.buttonDiv) {
                    qualityInspectionOverview.buttonDiv.empty();
                }
                try {
                    var buttonId = '#' + subModule + 'Button';
                    qualityInspectionOverview.addRegion('buttonDiv', buttonId);
                    qualityInspectionOverview.buttonDiv.show(overviewButtonView);
                } catch (e) {}
            });
        };

        /**
         * Show/Render Overview Grid View depending on the subModule/tab clicked!
         */
        var showOverviewTabs = function(subModule) {
            switch (subModule) {
            case 'inStock':
                 showInstockGridView(subModule);
                 break;
            case 'mandatory':
                showMandatoryGridView(subModule);
                break;
            case 'optional':
                showOptionalGridView(subModule);
                break;
            case 'blockedPart':
                showBlockedGridView(subModule);
                break;
            case 'setMarking':
                showSetMarkingGridView(subModule);
                break;
            default:
                break;
            }
        };

        /**
         * Show/Render Grid View
         */
        var showQualityInspectionOverview = function(options) {
            if(qualityInspectionOverview.gridDiv) {
                qualityInspectionOverview.gridDiv.empty();
            }
            try {
                var gridId = '#' + options.subModule + 'Grid';
                qualityInspectionOverview.addRegion('gridDiv', gridId);
                qualityInspectionOverview.gridDiv.show(options.grid);
            } catch (e) {}
        };
        
        // This method will be called for fetching the materialLineQICollection, also after Approve functions.  
        var fetchGridData = function(options) {
            options || (options = {});
            var opts = _.extend({
                reset: true,
                parse: true
            }, options);
            materialLineQICollection.url ='/material/v1/deliverynotelines?needSublines=true&status=QI_READY&qiMarking=MANDATORY&suggestBinLocation=true&whSiteId=' + whSiteId;
            materialLineQICollection.fetch(opts);
        };
        
         var renderApproveQtyFieldEmpty = function(){
            var approveQtyCells = $('td.integer-cell');
            approveQtyCells.each(function(){
                var curreApproveQtyCell = $(this);
                if(curreApproveQtyCell[0].innerHTML= "0"){
                    curreApproveQtyCell[0].innerHTML= "";
                }
            });
        };
        
        /**
         * Show/Render InstockGridView using same OptionalGridView as we can reuse Columns from OptionalGridView
         * No separate showInstockGridView is Created
         */
        var showInstockGridView = function(subModule) {
            require(['views/warehouse/qualityinspection/overview/view/OptionalGridView'], function(OptionalGridView) {
                var optionalGridView = new OptionalGridView({
                    module : subModule,
                    collection : materialLineQICollection
                });
                optionalGridView.on('show', function() {
                    processinStockGridInfo();
                }, this);
                showQualityInspectionOverview({
                    subModule : subModule,
                    grid : optionalGridView
                });
            });
        };
        
        var processinStockGridInfo = function() {
            materialLineQICollection.url ='/procurement/v1/materiallines/qi?status=READY_TO_STORE,STORED,MARKED_INSPECTION&whSiteId=' + whSiteId;
            materialLineQICollection.fetch();
        };
        
        /**
         * Show/Render MandatoryGridView
         */
        var showMandatoryGridView = function(subModule) {
            require(['views/warehouse/qualityinspection/overview/view/MandatoryGridView'], function(MandatoryGridView) {
                var transportLabels = new Collection();
                var mandatoryGridView = new MandatoryGridView({
                    collection : materialLineQICollection,
                    transportLabels : transportLabels                   
                });
                mandatoryGridView.on('show', function() {
                    fetchGridData();
                    processTransPortLabelInfo(transportLabels, true);                   
                }, this);
                showQualityInspectionOverview({
                    subModule : subModule,
                    grid : mandatoryGridView
                });
            });
        };
        
        processTransPortLabelInfo = function(transportLabels, forceFetch) {
            if(forceFetch || !transportLabels.isFetched) { // Fetch; if it's not fetched before
                transportLabels.url = '/material/v1/transportlabels?whSiteId=' + whSiteId;
                transportLabels.fetch({
                    async : false,
                    success : function() {
                        transportLabels.isFetched = true;
                        Gloria.WarehouseApp.trigger('TransportLabel:fetched', transportLabels);
                    }
                });
            } else {
                Gloria.WarehouseApp.trigger('TransportLabel:fetched', transportLabels);
            }
        };
        
        /**
         * @param {array[models/MaterialLineQIModel]} materialLineQIs
         */
        var showMultipleUpdateModal = function(materialLineQIModels) {
            if(!materialLineQIModels || !materialLineQIModels.length) return;
            require(['collections/TransportLabelCollection', 'views/warehouse/qualityinspection/overview/view/EditPartInfoModalView'], 
                    function(TransportLabelCollection, EditPartInfoModalView) {
                
                var transportLabelCollection = new TransportLabelCollection();
                transportLabelCollection.fetch({
                    success: function(collection) {
                        var editPartInfoModalView = new EditPartInfoModalView({
                            selectedModels: materialLineQIModels,
                            collection: collection
                        });
                        Gloria.basicModalLayout.content.show(editPartInfoModalView);
                    }
                });
            });
        };
        
        /**
         * @param {collections/MaterialLineQICollection} materialLineQIs
         */
        var validateMaterialLineQI = function(materialLineQICollection) {
            if(!materialLineQICollection || !materialLineQICollection.length) return false;
            if(!materialLineQICollection.isValid({action: 'approve'})) {                                
                Gloria.trigger('showAppMessageView', { 
                    type : 'error',
                    title : i18n.t('errormessages:general.title'),
                    singleMessage: i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.validation.invalidApprovedQty')
                });
                return false;
            }
            return true;
        };
        
        /**
         * @param {collections/MaterialLineQICollection} materialLineQIs
         */
        var approveMaterialLineQI = function(materialLineQIs) {
            if(!validateMaterialLineQI(materialLineQIs)) return;            
            var ids = materialLineQIs.pluck('id').join(',');
            materialLineQIs.save({
                url : '/material/v1/deliverynotelines/qiapprove?deliveryNoteLineIds=' + ids + '&userId=' + UserHelper.getInstance().getUserId(),
                success: function(response) {
                    Gloria.WarehouseApp.trigger('OverviewButton:refresh');
                    Gloria.WarehouseApp.trigger('MandatoryGrid:clearselection');
                    if(module == "blockedPart"){
                        processBlockedGridInfo({silent: false});
                    } else {
                        fetchGridData({silent: false});
                    }
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
        
        //=-=-=-=-=-=-=-=-=-=-= Optional Module [START] =-=-=-=-=-=-=-=-=-=-=//
        /**
         * Show/Render Optional Module
         */
        var showOptionalGridView = function(subModule) {
            require(['views/warehouse/qualityinspection/overview/view/OptionalGridView'], function(OptionalGridView) {
                var optionalGridView = new OptionalGridView({
                    module : subModule,
                    collection : materialLineQICollection
                });
                optionalGridView.on('show', function() {
                    processOptionalGridInfo();
                }, this);
                showQualityInspectionOverview({
                    subModule : subModule,
                    grid : optionalGridView
                });
            });
        };
        
        var processOptionalGridInfo = function() {
            materialLineQICollection.url ='/procurement/v1/materiallines/qi?status=STORED,MARKED_INSPECTION&qiMarking=OPTIONAL&whSiteId=' + whSiteId;
            materialLineQICollection.fetch();
        };
        
        var markForInspection = function(models) {
            var collection = new Collection();
            _.each(models, function(model) {
                if(!model.get('markedForInspection')) {     // Add models which has sendToQI as false
                    model.set('markedForInspection', true, {silent: true});
                    collection.add(model);
                }
            });
            if(collection.length) {
                Backbone.sync('update', collection, {
                    url : '/procurement/v1/materiallines/qi',
                    success : function(response) {
                        Gloria.WarehouseApp.trigger('OverviewButton:refresh');
                        Gloria.WarehouseApp.trigger('OptionalGrid:clearselection');
                        if (module == 'optional') {
                            processOptionalGridInfo();
                        } else {
                            processinStockGridInfo();
                        }
                    },
                    error : function() {
                        Gloria.trigger('showAppMessageView', {
                            type : 'error',
                            message : new Array({
                                message : i18n.t('Gloria.i18n.processFailed')
                            })
                        });
                    }
                });
            }
        };
        
        var showUnmarkModalView = function(models) {
            require(['views/warehouse/qualityinspection/overview/view/UnmarkModalView'], function(UnmarkModalView) {
                var unmarkModalView = new UnmarkModalView({
                    model : _.first(models)
                });
                Gloria.basicModalLayout.content.show(unmarkModalView);
            });
        };
        
        var unMark = function(model, unmarkQty) {
            model.save({}, {
                url : '/procurement/v1/materiallines/qi/' + model.id + '/unmark?quantity=' + unmarkQty,
                success : function(response) {
                    Gloria.WarehouseApp.trigger('OverviewButton:refresh');
                    Gloria.WarehouseApp.trigger('OptionalGrid:clearselection');
                    if (module == 'optional') {
                        processOptionalGridInfo();
                    } else {
                        processinStockGridInfo();
                    }
                },
                error : function() {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        message : new Array({
                            message : i18n.t('Gloria.i18n.processFailed')
                        })
                    });
                }
            });
        };

        //=-=-=-=-=-=-=-=-=-=-= Optional Module [END] =-=-=-=-=-=-=-=-=-=-=//
        
        //=-=-=-=-=-=-=-=-=-=-= Blocked Module [START] =-=-=-=-=-=-=-=-=-=-=//
        /**
         * Show/Render Blocked Module
         */
        var showBlockedGridView = function(subModule) {
            require(['views/warehouse/qualityinspection/overview/view/MandatoryGridView'], function(MandatoryGridView) {
                var transportLabels = new Collection();
                var blockedGridView = new MandatoryGridView({
                    module : 'blockedPart',
                    collection : materialLineQICollection,
                    transportLabels : transportLabels                   
                });
                blockedGridView.on('show', function() {
                    processBlockedGridInfo();
                    processTransPortLabelInfo(transportLabels, true);
                }, this);
                showQualityInspectionOverview({
                    subModule : subModule,
                    grid : blockedGridView
                });
            });
        };
        
        var processBlockedGridInfo = function(options) {
            options || (options = {});
            var opts = _.extend({
                reset: true,
                parse: true
            }, options);
            materialLineQICollection.url ='/material/v1/deliverynotelines?needSublines=true&status=BLOCKED&suggestBinLocation=true&whSiteId=' + whSiteId;
            materialLineQICollection.fetch(opts);
        };
        
        //=-=-=-=-=-=-=-=-=-=-= Blocked Module [END] =-=-=-=-=-=-=-=-=-=-=//
        
        //=-=-=-=-=-=-=-=-=-=-= Set Marking Module [START] =-=-=-=-=-=-=-=-=-=-=//
        /**
         * Show/Render Blocked Module
         */
        var showSetMarkingGridView = function(subModule) {
            require(['views/warehouse/qualityinspection/overview/view/SetMarkingGridView'], function(SetMarkingGridView) {
                var setMarkingGridView = new SetMarkingGridView({
                    collection : materialLineQICollection
                });
                setMarkingGridView.on('show', function() {
                    processSetMarkingGridInfo();
                }, this);
                showQualityInspectionOverview({
                    subModule : subModule,
                    grid : setMarkingGridView
                });
            });
        };
        
        var processSetMarkingGridInfo = function() { 
            materialLineQICollection.url ='/warehouse/v1/orderlines/current?' +
                'status=PLACED&calculateInStock=true&internalExternal=EXTERNAL&whSiteId=' + whSiteId;
            materialLineQICollection.fetch();
        };
        
        var markAsMandatorySetMarking = function(models) {
            var collection = new Collection();
            _.each(models, function(model) {
                if(model.get('qiMarking') != 'MANDATORY') { // Add models which has qiMarking not true
                    model.set('qiMarking', 'MANDATORY'); 
                    collection.add(model);
                }
            });
            if(collection.length) {
                Backbone.sync('update', collection, {
                    url : '/warehouse/v1/orderlines/qimarking',
                    success : function(response) {
                        materialLineQICollection.add(response, {merge : true});
                        Gloria.WarehouseApp.trigger('OverviewButton:refresh');
                        Gloria.WarehouseApp.trigger('SetMarkingGrid:clearselection');
                        materialLineQICollection.fetch();
                    },
                    error : function() {
                        Gloria.trigger('showAppMessageView', {
                            type : 'error',
                            message : new Array({
                                message : i18n.t('Gloria.i18n.processFailed')
                            })
                        });
                    }
                });
            }
        };
        
        var markAsOptionalSetMarking = function(models) {
            // Check if any orderlines DirectSend is set to true!
            // Those lines cannot be set optional, show an error block!
            var isValid = true;
            _.each(models, function(model) {
                if(model.get('directSend')) {
                    Gloria.WarehouseApp.trigger('QIOverview:setMarking:markAsOptional:invalid');
                    isValid = false;
                };
            });
            if(!isValid) return;
            var collection = new Collection();
            _.each(models, function(model) {
                if(model.get('qiMarking') != 'OPTIONAL') {  // Add models which has qiMarking not false
                    model.set('qiMarking', 'OPTIONAL');
                    collection.add(model);
                }
            });
            if(collection.length) {
                Backbone.sync('update', collection, {
                    url : '/warehouse/v1/orderlines/qimarking',
                    success : function(response) {
                        materialLineQICollection.add(response, {merge : true});
                        Gloria.WarehouseApp.trigger('OverviewButton:refresh');
                        Gloria.WarehouseApp.trigger('SetMarkingGrid:clearselection');
                        materialLineQICollection.fetch();    
                    },
                    error : function() {
                        Gloria.trigger('showAppMessageView', {
                            type : 'error',
                            message : new Array({
                                message : i18n.t('Gloria.i18n.processFailed')
                            })
                        });
                    }
                });
            }
        };
        
        var unmarkSetMarking = function(models) {
            var collection = new Collection();
            _.each(models, function(model) {
                if(model.get('qiMarking') != null) {    // Add models which has a qiMarking
                    model.set('qiMarking', null);
                    collection.add(model);
                }
            });
            if(collection.length) {
                Backbone.sync('update', collection, {
                    url : '/warehouse/v1/orderlines/qimarking',
                    success : function(response) {
                        materialLineQICollection.add(response, {merge : true});
                        Gloria.WarehouseApp.trigger('OverviewButton:refresh');
                        Gloria.WarehouseApp.trigger('SetMarkingGrid:clearselection');
                        materialLineQICollection.fetch();               
                    },
                    error : function() {
                        Gloria.trigger('showAppMessageView', {
                            type : 'error',
                            message : new Array({
                                message : i18n.t('Gloria.i18n.processFailed')
                            })
                        });
                    }
                });
            } else {
                Gloria.WarehouseApp.trigger('QIOverview:setMarking:unmark:invalid');
            }
        };
        
        //=-=-=-=-=-=-=-=-=-=-= Set Marking Module [END] =-=-=-=-=-=-=-=-=-=-=//
        var showCreateTransportLabelPopup = function(){
            require(['views/warehouse/common/CreateTransportLabelView'], function(CreateTransportLabelView) {
                var createTransportLabelView = new CreateTransportLabelView();
                Gloria.basicModalLayout.content.show(createTransportLabelView);
            });
        };     
        
        var createTransportLabel = function(number) {
            require(['collections/TransportLabelCollection'], function(TransportLabelCollection) {
                var transportLabelCollection = new TransportLabelCollection();
                transportLabelCollection.url = '/material/v1/transportlabels/?action=create&whSiteId=' + whSiteId + '&transportLabelCopies=' + number;
                transportLabelCollection.fetch({
                    success : function(collection) {
                        var transportLabels = new Collection();
                        processTransPortLabelInfo(transportLabels, true);
                        printCreatedTransportLabels(collection);
                    }
                });
            });         
        };
        
        var prepareTransportLabel = function() {
            var transportlabels = new Collection();
            require(['views/warehouse/qualityinspection/overview/view/PrintTLModalView'], function(PrintTLModalView) {
                var printTLModalView = new PrintTLModalView({
                    collection : transportlabels
                });
                printTLModalView.on('show', function() {
                    processTransPortLabelInfo(transportlabels, true);
                });
                Gloria.basicModalLayout.content.show(printTLModalView);
            });
        };
        
        var printCreatedTransportLabels = function(transportlabels){
            transportlabels.each(function(model){
                printTransportLabel(model.id);
            });
        };
        
        var printTransportLabel = function(transportlabelID) {
            var model = new GloriaModel();
            model.url = '/material/v1/transportlabels/' + transportlabelID + '?action=printLabel&whSiteId=' + whSiteId;
            model.fetch({
                success : function() {
                    Gloria.WarehouseApp.trigger('qualityInspection:printTL:printed', true);
                },
                error : function() {
                    Gloria.WarehouseApp.trigger('qualityInspection:printTL:printed', false);
                }
            });
        };
        
        var loadQIDetailsPage = function(selectedId) {
            var details;
            switch(module) {
                case 'mandatory':
                    details = 'mandatory/mandatory'; break; 
                case 'optional':
                    details = 'optional/optional'; break;
                case 'blockedPart':
                    details = 'blockedPart/blocked'; break;  
                case 'inStock':
                    details = 'inStock/inStock'; break;     
            }
            Backbone.history.navigate('warehouse/qualityinspection/' + details + 'Details/' + selectedId, {
                trigger : true
            });
        };
        
        var fetchDeliveryNoteSublines = function(options) {
            var model = options.model;
            var thisStatus = '';
            if(module == 'mandatory') {
                thisStatus = 'QI_READY';
            } else if(module == 'blockedPart') {
                thisStatus = 'BLOCKED';
            }
            
            model.directsends.on('backgrid:edit', function(thisModel) {
                Gloria.WarehouseApp.trigger('QI:DeliveryNoteLine:start:update');
            }, this);
            
            this.listenTo(model.directsends, 'backgrid:edited', function(directsend, column, command) { 
                if(command.keyCode !== 27) {
                    saveDeliveryNoteSubLine(directsend);
                }
            });
            return model.directsends;
        };
        
        var saveDeliveryNoteSubLine = _.debounce(function(directsend) {
            if(_.isEqual(directsend.previousAttributes(), directsend.attributes)) return;
            directsend.url = '/material/v1/deliverynotelines/' + directsend.id + '/deliverynotesublines/' + directsend.id;
            directsend.save(null, {
                silent : false,
                type : 'PUT',
                beforeSend : function() {
                    Gloria.WarehouseApp.trigger('QI:DeliveryNoteLine:start:update', directsend);
                },
                complete : function() {
                    Gloria.WarehouseApp.trigger('QI:DeliveryNoteLine:complete:update', directsend);
                },
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('errormessages:general.title'),
                        message : errorMessage
                    });
                }
            });
        }, 200);
        
          var collectDirectSendandUpdate = function(selectedModels,transportLabelId) {
                _.each(selectedModels, function(model) {
                    _.each(model.directsends.models, function(directsend) {
                        directsend.url = '/material/v1/deliverynotelines/' + model.id + '/deliverynotesublines/' + directsend.id;
                        directsend.save({transportLabelId: transportLabelId});
                    });
                });
            };
        
        Controller.QualityInspectionOverviewController = Marionette.Controller.extend({
            
            initialize : function() {
                this.initializeListeners();
            },

            initializeListeners : function() {
                this.listenTo(Gloria.WarehouseApp, 'approve:materialLineQI', approveMaterialLineQI);
                this.listenTo(Gloria.WarehouseApp, 'show:multipleUpdate', showMultipleUpdateModal);
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:addInformation', loadQIDetailsPage);             
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:optional:markForInspection', markForInspection);
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:optional:unmark', showUnmarkModalView);
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:optional:unmarkqty', unMark);                
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:inStock:markForInspection', markForInspection);
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:inStock:unmark', showUnmarkModalView);
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:inStock:unmarkqty', unMark);
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:setMarking:markAsMandatory', markAsMandatorySetMarking);
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:setMarking:markAsOptional', markAsOptionalSetMarking);
                this.listenTo(Gloria.WarehouseApp, 'QIOverview:setMarking:unmark', unmarkSetMarking);
                this.listenTo(Gloria.WarehouseApp, 'qualityInspection:createTransLabel', showCreateTransportLabelPopup);
                this.listenTo(Gloria.WarehouseApp, 'CreateNumberOfTransportLabel', createTransportLabel);
                this.listenTo(Gloria.WarehouseApp, 'qualityInspection:printTL', prepareTransportLabel);
                this.listenTo(Gloria.WarehouseApp, 'qualityInspection:printTL:print', printTransportLabel);
                this.listenTo(Gloria.WarehouseApp, 'QI:DeliveryNoteLine:sublines:fetch', fetchDeliveryNoteSublines);
                this.listenTo(Gloria.WarehouseApp, 'TransportLabel:change', saveDeliveryNoteSubLine);
                this.listenTo(Gloria.WarehouseApp, 'BinLocation:change', saveDeliveryNoteSubLine);
                this.listenTo(Gloria.WarehouseApp, 'TransportLabel:multiupdate', collectDirectSendandUpdate);
            },

            control : function(tab) {
                module = tab;
                prepareOverview.call(this);
            },
            
            onDestroy: function() {
                materialLineQICollection = null;
                module = null;
                whSiteId = null;
            }
        });
    });

    return Gloria.WarehouseApp.Controller.QualityInspectionOverviewController;
});