define(
    ['app', 'jquery', 'underscore', 'handlebars', 'backbone', 'marionette', 'i18next',
            'utils/backbone/GloriaCollection', 'utils/backbone/GloriaModel', 'collections/RequestGroupCollection',
            'collections/MaterialLineCollection', 'utils/UserHelper'], function(Gloria, $, _, Handlebars, Backbone,
        Marionette, i18n, Collection, GloriaModel, RequestGroupCollection, MaterialLineCollection, UserHelper) {

    Gloria.module('WarehouseApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

        var requestGroupCollection;
        var pickListModel;
        var pickListId;
        var layout;
        var pickShipLayout;
        var detailLayout;
        var materialLineCollection;
        var materialLineModel;
        var lockFailed;
        var pickListDetailData;
        /**
         * Prepare Pick
         */
        var preparePick = function() {
            // Initialize Data Source Objects
            initializeDataSourceObjects();
            setPageLayout();
        };

        /**
         * preparePickListDetails
         */
        var preparePickListDetails = function(pickListId) {
            var setDetailsPageLayout = function() {
                initializeDataSourcePickListDetailsObjects();
                setPagePickListDetailsLayout(pickListId);
            };
            if (location.hash.indexOf('warehouse/pick/') != -1
                    || location.hash.indexOf('warehouse/ship/pickListDetails/') != -1) {
                var reserveMaterialLinesForAPickList = new Collection();
                reserveMaterialLinesForAPickList.url = '/procurement/v1/picklists/' + pickListId + '/lock?userId='
                        + UserHelper.getInstance().getUserId();
                return Backbone.sync('update', reserveMaterialLinesForAPickList, {
                    success : function() {
                        lockFailed = false;
                        setDetailsPageLayout();
                    },
                    validationError : function() {
                        lockFailed = true;
                        setDetailsPageLayout();
                    }
                });
            }
        };

        /**
         * Initialize Data Source Objects.
         */
        var initializeDataSourceObjects = function() {
            requestGroupCollection = new RequestGroupCollection([], {
                state : {
                    pageSize : function() { // Check if any pageSize is already stored
                        var object = JSON.parse(window.localStorage.getItem('Gloria.warehouse.PickGrid' + '.'
                                + UserHelper.getInstance().getUserId()));
                        return (object && object['pageSize']) || 10; // Default 10
                    }(),
                    currentPage : 1
                },
                filterKey : 'PickGrid'
            });
        };

        /**
         * Initialize Data Source PickListDetails Objects.
         */
        var initializeDataSourcePickListDetailsObjects = function() {
            // @TODO This should be fetched in 'client' mode; it will fail if there are more than 100 items!
            materialLineCollection = new MaterialLineCollection([], {
                state : {
                    sortKey : 'binLocationCode,pPartNumber',
                    order : -1,
                    pageSize : 100,
                    currentPage : 1
                }
            });
        };

        /**
         * Set Page Layout.
         */
        var setPageLayout = function() {
            Gloria.basicModalLayout.closeAndReset();
            Gloria.trigger('showBreadcrumbView');
            Gloria.basicLayout.content.reset();
            require(
                ['views/warehouse/pick/view/Layout', 'views/warehouse/pick/view/PickShipLayout'], function(Layout,
                    PickShipLayout) {
                layout = new Layout({
                    module : 'pick'
                });
                layout.on('show', function() {
                    showModuleView();
                }, this);
                pickShipLayout = new PickShipLayout();
                Gloria.basicLayout.content.show(layout);
                // Attach to pickPane
                layout.pickPane.show(pickShipLayout);
                showPickButtonView();
                showPickGridView(pickShipLayout);

            });
        };

        /**
         * Show Module View.
         */
        var showModuleView = function() {
            require(
                ['views/warehouse/common/WarehouseModuleView'], function(WarehouseModuleView) {
                var warehouseModuleView = new WarehouseModuleView({
                    module : 'pickship',
                    control : 'pick' // Button control
                });
                // Attach to moduleInfo region
                layout.moduleInfo.show(warehouseModuleView);
            });
        };

        /**
         * Set Page Layout PickListDetails.
         */
        var setPagePickListDetailsLayout = function(pickListId) {
            Gloria.basicModalLayout.closeAndReset();
            Gloria.trigger('showBreadcrumbView', {
                itemId : pickListId
            });
            Gloria.basicLayout.content.reset();
            require(
                ['views/warehouse/pick/view/DetailLayout'], function(DetailLayout) {
                detailLayout = new DetailLayout();
                detailLayout.on('show', function() {
                    preparePickListDetailsView();
                }, this);
                Gloria.basicLayout.content.show(detailLayout);
            });
        };

        /**
         * Show PickButton View
         */
        var showPickButtonView = function() {
            require(
                ['views/warehouse/pick/view/PickButtonView'], function(PickButtonView) {
                var pickButtonView = new PickButtonView();
                pickShipLayout.button.show(pickButtonView);
            });
        };

        /**
         * Prepare PickListDetails Button View
         */
        var preparePickListDetailsView = function() {
            pickListModel = new GloriaModel();
            pickListModel.url = '/procurement/v1/picklists/' + pickListId + '?whSiteId='
                    + UserHelper.getInstance().getDefaultWarehouse();
            pickListModel.fetch({
                success : function(response) {
                    pickListDetailData = response;
                    Gloria.trigger('showBreadcrumbView', {
                        code : response.get('code')
                    });
                    showPickListDetailsButtonView(response);
                    showPickListDetailsGridView(response.get('status'));
                }
            });
        };

        /**
         * Show PickListDetails Button View
         */
        var showPickListDetailsButtonView = function(model) {
            require(
                ['views/warehouse/pick/view/PickListDetailsButtonView'], function(PickListDetailsButtonView) {
                var pickListDetailsButtonView = new PickListDetailsButtonView({
                    model : model,
                    lockFailed : lockFailed
                });
                detailLayout.button.show(pickListDetailsButtonView);
            });
        };

        /**
         * Show Pick Grid View
         */
        var showPickGridView = function(parentView) {
            require(
                ['views/warehouse/pick/view/PickGridView'], function(PickGridView) {
                var pickGridView = new PickGridView({
                    collection : requestGroupCollection
                });
                pickGridView.on('show', function() {
                    processPickGridView();
                }, this);
                showGridView(parentView, pickGridView);
            });
        };

        /**
         * Show PickListDetailsGrid View
         */
        var showPickListDetailsGridView = function(status) {
            require(
                ['views/warehouse/pick/view/PickListDetailsGridView'], function(PickListDetailsGridView) {
                var pickListDetailsGridView = new PickListDetailsGridView({
                    status : status,
                    lockFailed : lockFailed,
                    collection : materialLineCollection
                });
                var parentView = detailLayout;
                pickListDetailsGridView.on('show', function() {
                    processPickListsDetailGridView();
                }, this);
                showGridView(parentView, pickListDetailsGridView);
            });
        };

        /**
         * Show Grid View
         */
        var showGridView = function(parentView, gridView) {
            parentView.grid.show(gridView);
        };

        /**
         * Process PickGrid Information
         */
        var processPickGridView = function() {
            requestGroupCollection.fetch({
                data : {
                    fetchLockedItems : true
                }
            });
        };

        /**
         * ProcessPickListsDetailGridView Information
         */
        var processPickListsDetailGridView = function() {
            materialLineCollection.url = '/procurement/v1/picklists/' + pickListId + '/materiallines?whSiteId='
                    + UserHelper.getInstance().getDefaultWarehouse();
            materialLineCollection.fetch({
                data : location.hash.indexOf('warehouse/pick/') != -1 ? {
                    status : 'REQUESTED'
                } : {},
                success : function(collection) {
                    materialLineCollection = collection;
                }
            });
        };

        var createPickList = function(models) {
            Backbone.sync('create', new Collection(models), {
                url : '/procurement/v1/picklists',
                success : function(response) {
                    Backbone.history.loadUrl(Backbone.history.fragment);
                }
            });
        };

        var cancelPickList = function(selectedModels) {
            Backbone.sync('update', new Collection(selectedModels), {
                url : '/procurement/v1/picklists/cancel',
                success : function(response) {
                    Backbone.history.loadUrl(Backbone.history.fragment);
                }
            });
        };

        var cancelPickListDetail = function() {
            var picklistid = pickListId;
            var picklistData = pickListDetailData.attributes;
            if (picklistid) {
                Backbone.sync('update', pickListModel, { // changed here
                    url : '/procurement/v1/picklists/' + picklistid + '/cancel',
                    success : function(response) {
                        // Redirect to PickModule
                        Backbone.history.navigate('warehouse/pick', {
                            trigger : true
                        });
                    },
                    validationError : function(errorMessage, errors) {
                        Gloria.trigger('showAppMessageView', {
                            type : 'error',
                            title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'),
                            message : errorMessage
                        });
                    }

                });
            }
        };

        var unlockMaterialLinesForAPickList = function(callback) {
            var reserveMaterialLinesForAPickList = new Collection();
            reserveMaterialLinesForAPickList.url = '/procurement/v1/picklists/' + pickListId + '/unlock?userId='
                    + UserHelper.getInstance().getUserId();
            Backbone.sync('update', reserveMaterialLinesForAPickList, {
                success : function(model, resp, xhr) {
                    callback && callback();
                },
                validationError : function(errorMessage, errors) {
                    if (!errorMessage.length) return;
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'),
                        message : errorMessage
                    });
                }
            });
        };

        var materialLinesPickListsPicked = function() {
            Backbone.sync('update', materialLineCollection, {
                url : '/procurement/v1/picklists/' + pickListId + '/materiallines?action=pick&whSiteId='
                        + UserHelper.getInstance().getDefaultWarehouse(),
                success : function(response) {
                    unlockMaterialLinesForAPickList(function() {
                        // Redirect to PickModule
                        Backbone.history.navigate('warehouse/pick', {
                            trigger : true
                        });
                    });
                },
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'),
                        message : errorMessage
                    });
                }

            });
        };

        var pickAndMarkAsShip = function() {
            Backbone.sync('update', materialLineCollection, {
                url : '/procurement/v1/picklists/' + pickListId + '/materiallines?action=pickShip&whSiteId='
                        + UserHelper.getInstance().getDefaultWarehouse(),
                success : function(response) {
                    unlockMaterialLinesForAPickList(function() {
                        // Redirect to PickModule
                        Backbone.history.navigate('warehouse/pick', {
                            trigger : true
                        });
                    });
                },
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'),
                        message : errorMessage
                    });
                }

            });
        };

        var printPickList = function() {
            require(
                ['hbs!views/warehouse/pick/view/pickPrintView'], function(PickPrintViewTemplate) {
                var content = PickPrintViewTemplate({
                    materiallines : materialLineCollection.toJSON(),
                    pickListId : pickListId
                });
                Gloria.trigger('print', {
                    content : content
                });
            });
        };

        var printPullLabel = function() {
            pickListModel.save({}, {
                url : '/common/v1/picklists/' + pickListId + '/pullabels?whSiteId='
                        + UserHelper.getInstance().getDefaultWarehouse(),
                success : function(response) {
                    console.log('Printed!');
                },
                validationError : function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type : 'error',
                        message : errorMessage
                    });
                }
            });
        };

        var printPullLabelList = function(search) {
            require(
                ['views/warehouse/pick/view/PickPullLabelprintView'], function(PickPullLabelprintView) {
                var pickPullLabelprintView = new PickPullLabelprintView({
                    pickListId : pickListId,
                    module : 'list'
                });
                Gloria.basicModalLayout.content.show(pickPullLabelprintView);
            });
        };

        var printPullLabelPart = function(search) {
            require(
                ['views/warehouse/pick/view/PickPullLabelprintView'], function(PickPullLabelprintView) {
                var pickPullLabelprintView = new PickPullLabelprintView({
                    pickListId : pickListId,
                    module : 'part'
                });
                Gloria.basicModalLayout.content.show(pickPullLabelprintView);
            });
        };

        var unlock = function(options) {
            if (options.previous.hash.indexOf('warehouse/pick/') != -1) {
                unlockMaterialLinesForAPickList();
            }
        };

        var printPullLabelprintListData = function(id, qty) {
            var url = '/common/v1/picklists/' + id + '/pullabels?whSiteId='
                    + UserHelper.getInstance().getDefaultWarehouse();
            if (qty) {
                url = url + '&quantity=' + qty;
            } else {
                url = url;
            }
            pickListModel.save({}, {
                url : url
            });
        };

        var pickPullLabelprintPartData = function(id, qty) {
            var url = '/common/v1/picklists/' + id + '/materiallines/' + this.materialLineModel.id
                    + '/pullabels?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
            if (qty) {
                url = url + '&quantity=' + qty;
            } else {
                url = url;
            }
            pickListModel.save({}, {
                url : url
            });
        };

        var materialLinePrint = function(selectedModel) {
            this.materialLineModel = selectedModel;
        };

        Controller.PickController = Marionette.Controller.extend({

            initialize : function() {
                this.initializeListeners();
            },

            initializeListeners : function() {
                this.listenTo(Gloria.WarehouseApp, 'CreatePickList:show', createPickList);
                this.listenTo(Gloria.WarehouseApp, 'CancelPickList:show', cancelPickList);
                this.listenTo(Gloria.WarehouseApp, 'CancelPickListDetail:show', cancelPickListDetail);
                this.listenTo(Gloria.WarehouseApp, 'PickButton:clicked', materialLinesPickListsPicked);
                this.listenTo(Gloria.WarehouseApp, 'Pick:print:pickList', printPickList);
                // this.listenTo(Gloria.WarehouseApp, 'Pick:pullLabel:print', printPullLabel);
                this.listenTo(Gloria.WarehouseApp, 'Pick:print:pickList:printPullLabelList', printPullLabelList);
                this.listenTo(Gloria.WarehouseApp, 'Pick:print:pickList:printPullLabelPart', printPullLabelPart);
                this.listenTo(Gloria.WarehouseApp, 'Pick:PickAndMarkAsShip', pickAndMarkAsShip);
                this.listenTo(Gloria, 'route:changed', unlock);
                this.listenTo(Gloria.WarehouseApp, 'Pick:PullLabel:print:List:qty', printPullLabelprintListData);
                this.listenTo(Gloria.WarehouseApp, 'Pick:PullLabel:print:part:qty', pickPullLabelprintPartData);
                this.listenTo(Gloria.WarehouseApp, 'Pick:MaterialLineModel:Print', materialLinePrint);
            },

            control : function(options) {
                options || (options = {});
                if (options.pickListId) {
                    pickListId = options.pickListId;
                    preparePickListDetails.call(this, options.pickListId);
                } else {
                    preparePick.call(this);
                }
            },

            onDestroy : function() {
                requestGroupCollection = null;
                pickListModel = null;
                pickListId = null;
                layout = null;
                pickShipLayout = null;
                detailLayout = null;
                materialLineCollection = null;
                materialLinemodel = null;
                lockFailed = null;
                pickListDetailData = null;
            }
        });
    });

    return Gloria.WarehouseApp.Controller.PickController;
});
