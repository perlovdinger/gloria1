define(['app',
    'jquery',
    'underscore',
    'handlebars',
    'backbone',
    'marionette',
    'i18next',
    'utils/UserHelper',
    'utils/backbone/GloriaCollection',
    'utils/backbone/GloriaModel',
    'collections/MaterialLineCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, Collection, Model, MaterialLineCollection) {

    Gloria.module('MaterialApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

        var module;
        var materialLineCollection;
        var materialLineView;
        var borrowPartDialogView;
        var sm;
        /**
         * Prepare Material Line Overview.
         * Initialize Data Source Objects which are going to be used as data transfer objects
         * and set page layout.
         */
        var prepareMaterialLineOverview = function() {
            // Initialize Data Source Objects
            initializeDataSourceObjects();
            // Set Page Layout
            setPageLayout();
        };

        /**
         * Initialize Data Source Objects.
         */
        var initializeDataSourceObjects = function() {
            materialLineCollection = new MaterialLineCollection([], {
                queryParams: {
                    type: function() {
                        if (UserHelper.getInstance().hasUserRole('PROCURE')) {
                            return 'MATERIAL_CONTROL';
                        }
                        if (UserHelper.getInstance().hasUserRole('DELIVERY')) {
                            return 'DELIVERY_CONTROL';
                        }
                        return null; // WH_DEFAULT and other roles who have access to this module.
                    }()
                },
                state: {
                    pageSize: function() { // Check if any pageSize is already stored
                        var object = JSON.parse(window.localStorage.getItem('Gloria.material.MaterialLineGrid' + '.' + UserHelper.getInstance().getUserId()));
                        return (object && object['pageSize']) || 10; // Default 10
                    }(),
                    currentPage: 1,
                    sortKey: 'orderNo,pPartNumber,pPartVersion',
                    order: 1
                },
                filterKey: 'MaterialLineGrid'
            });
        };

        /**
         * Set Page Layout.
         */
        var setPageLayout = function() {
            Gloria.basicModalLayout.closeAndReset();
            Gloria.basicLayout.content.reset();
            Gloria.trigger('showBreadcrumbView', null);
            require(['views/material/overview/view/MaterialLineView'], function(MaterialLineView) {
                materialLineView = new MaterialLineView();
                materialLineView.on('show', function() {
                    showMaterialModuleView(); // Module Info
                    showMaterialGridView(); // Grid Info
                }, this);
                Gloria.basicLayout.content.show(materialLineView);
            });
        };

        /**
         * Show/Render Material Module & Control Button View.
         */
        var showMaterialModuleView = function() {
            require(['views/material/common/MaterialModuleView', 'views/material/overview/view/MaterialButtonView',
                'views/material/overview/view/ProcurerFilter'
            ], function(MaterialModuleView, MaterialButtonView, ProcurerFilter) {
                var materialModuleView = new MaterialModuleView({
                    module: module
                });
                // Attach to moduleInfo region
                materialLineView.moduleInfo.show(materialModuleView);
                var materialButtonView = new MaterialButtonView();
                var procurerFilter = new ProcurerFilter({
                    collection: materialLineCollection
                });
                // Attach to moduleInfo region
                materialLineView.moduleInfo.show(materialButtonView);
                try {
                    materialButtonView.filter.show(procurerFilter);
                } catch (e) {
                    // Filter DOM is not exist, maybe not have access to this,
                    // here Warehouse personnel has no access to filter 
                };
            });
        };

        /**
         * Show/Render Material Grid View.
         */
        var showMaterialGridView = function() {
            require(['views/material/overview/view/MaterialLineGridView'], function(MaterialLineGridView) {
                materialLineGridView = new MaterialLineGridView({
                    collection: materialLineCollection
                });
                var currentUser = UserHelper.getInstance().getUserId();
                materialLineGridView.on('show', function() {
                    var materialFilters = window.localStorage.getItem('Gloria.material.MaterialLineGrid.ProcurerFilter.' + currentUser);
                    var parsedParams = JSON.parse(materialFilters || '{"filter": "procureUserId", "value": "' + currentUser + '"}');
                    if (parsedParams && parsedParams.filter == 'procureTeam') { // List for the selected team
                        filterMaterialRequestList('procureTeam', parsedParams.value);
                    } else if (parsedParams && parsedParams.filter == 'procureUserId') { // List for the current user
                        filterMaterialRequestList('procureUserId', parsedParams.value);
                    } else {
                        filterMaterialRequestList(); // List all
                    }
                }, this);
                materialLineView.gridInfo.show(materialLineGridView);
            });
        };

        /**
         * Process Material Line Information
         */
        var processMaterialLineInfo = function() {
            if (!UserHelper.getInstance().hasPermission('filter', ['MaterialOverview'])) {
                delete materialLineCollection.queryParams.procureUserId;
            }
            materialLineCollection.trigger('QueryParams:reset', ['userId']);
            materialLineCollection.queryParams.userId = UserHelper.getInstance().getUserId();
            materialLineCollection.fetch({
                reset: true
            });
        };

        var filterMaterialRequestList = function(filterType, value) {
            Gloria.MaterialApp.trigger('MaterialLineGrid:clearSelectedModels');
            Gloria.MaterialApp.trigger('MaterialLineGrid:ResetButtons');
            var currentUser = UserHelper.getInstance().getUserId();
            var possibleQueryParams = ['procureUserId', 'procureTeam'];
            var queryParam = null;
            _.each(possibleQueryParams, function(queryParameter, index) {
                materialLineCollection.trigger('QueryParams:reset', [queryParameter]);
                delete materialLineCollection.queryParams[queryParameter];
            });
            switch (filterType) {
                case 'procureUserId':
                    queryParam = possibleQueryParams[0];
                    materialLineCollection.queryParams[queryParam] = UserHelper.getInstance().getUserId();
                    break;
                case 'procureTeam':
                    queryParam = possibleQueryParams[1];
                    materialLineCollection.queryParams[queryParam] = value;
                    break;
            }
            window.localStorage.setItem('Gloria.material.MaterialLineGrid.ProcurerFilter.' + currentUser, JSON.stringify({
                filter: queryParam,
                value: value
            }));
            materialLineCollection.state.currentPage = 1;
            processMaterialLineInfo();

        };

        // This method will construct and show the Release material information modal window.
        var showReleasePartDialog = function(releaseMaterialLines) {
            require(['views/material/overview/view/ReleasePartDialogView'], function(ReleasePartDialogView) {
                var releasePartDialogView = new ReleasePartDialogView({
                    releaseMaterialLines: releaseMaterialLines
                });
                Gloria.basicModalLayout.content.show(releasePartDialogView);
                releasePartDialogView.on('hide', function() {
                    Gloria.basicModalLayout.content.reset();
                });
            });
        };

        /**
         * Release Material RequestList
         */
        var releaseMaterialRequestList = function(releaseMaterialLines, confirmationText) {
            var that = this;
            var filteredCollection = new Collection(releaseMaterialLines);
            Backbone.sync('update', filteredCollection, {
                url: '/procurement/v1/materiallines?action=release&confirmationText=' + encodeURIComponent(confirmationText), //OK
                success: function(response) {
                    materialLineCollection.reset();
                    prepareMaterialLineOverview(); // Refetch!
                },
                validationError: function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type: 'error',
                        message: errorMessage //TODO : need to display the error message in the Modal.
                    });
                }
            });
        };

        /**
         * Show Borrow Part Dialog
         */
        var showBorrowPartDialog = function(model) {
            require(['views/material/overview/view/BorrowPartDialogView'], function(BorrowPartDialogView) {
                borrowPartDialogView = new BorrowPartDialogView({
                    model: model
                });
                borrowPartDialogView.on('show', function() {
                    processBorrowMaterialGrid(model.get('id'));
                }, this);
                Gloria.basicModalLayout.content.show(borrowPartDialogView);
                borrowPartDialogView.on('hide', function() {
                    Gloria.basicModalLayout.content.reset();
                });
            });
        };

        var processBorrowMaterialGrid = function(id) {
            var collection = new MaterialLineCollection([], {
                state: {
                    pageSize: function() { // Check if any pageSize is already stored
                        var object = JSON.parse(window.localStorage.getItem('Gloria.material.BorrowPartGrid' + '.' + UserHelper.getInstance().getUserId()));
                        return (object && object['pageSize']) || 10; // Default 10
                    }(),
                    currentPage: 1
                }
            });
            collection.url = '/procurement/v1/materiallines/borrow?forMaterialLineId=' + id;
            collection.fetch({
                success: function(response) {
                    if (response.length) {
                        showBorrowMaterialGrid(response);
                    }
                }
            });
        };

        var showBorrowMaterialGrid = function(collection) {
            require(['views/material/overview/view/BorrowPartGridView'], function(BorrowPartGridView) {
                var borrowPartGridView = new BorrowPartGridView({
                    collection: collection
                });
                borrowPartDialogView.gridInfo.show(borrowPartGridView);
            });
        };

        /**
         * Borrow Material RequestList
         */
        var borrowMaterialRequestList = function(model, ids, noReturn) {
            var that = this;
            model.save({}, {
                url: '/procurement/v1/materiallines/' + model.get('id') + '?action=borrow&fromMaterialLineId=' + ids + '&noReturn=' + noReturn,
                success: function() {
                    Gloria.MaterialApp.trigger('MaterialRequestList:partBorrowed', true);
                    prepareMaterialLineOverview(); // Refetch!
                },
                validationError: function(errorMessage, errors) {
                    //TODO Currently there is no error code associated with this error coming in the response.
                    // When the error code was provided by the server to in the response, then the below error message should be replaced by that.
                    Gloria.trigger('showExceptionHandler', i18n.t('Gloria.i18n.material.validation.state'));
                }
            });
        };

        /**
         * Show Scrap Part Dialog
         */
        var showScrapPartDialog = function(models) {
            require(['views/material/overview/view/ScrapPartDialogView'], function(ScrapPartDialogView) {
                var scrapPartDialogView = new ScrapPartDialogView({
                    models: models
                });
                Gloria.basicModalLayout.content.show(scrapPartDialogView);
            });
        };

        /**
         * Scrap Material RequestList
         */
        var scrapMaterialRequestList = function(models, scrapQty, comments) {
            var collection = new Collection(models);
            Backbone.sync('update', collection, {
                url: '/procurement/v1/materiallines?action=scrap' + (models.length == 1 ? '&quantity=' + scrapQty : '') + '&confirmationText=' + encodeURIComponent(comments),
                success: function(response) {
                    materialLineCollection.reset();
                    prepareMaterialLineOverview(); // Refetch!
                },
                validationError: function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type: 'error',
                        message: new Array({
                            message: i18n.t('Gloria.i18n.processFailed')
                        })
                    });
                }
            });
        };

        /**
         * Show Pull Part Dialog
         */
        var showPullPartDialog = function(model) {
            require(['views/material/overview/view/PullPartDialogView'], function(PullPartDialogView) {
                var pullPartDialogView = new PullPartDialogView({
                    model: model
                });
                Gloria.basicModalLayout.content.show(pullPartDialogView);
            });
        };

        /**
         * Add to Existing Request List
         */

        var showAddToReqListDialog = function(selectedRows) {
            sm = selectedRows;
            var matchingRequestListCollection = new Collection();
            matchingRequestListCollection.fetch({
                url: '/material/v1/requestlists?status=CREATED&requesterId=' + UserHelper.getInstance().getUserId() +
                    '&whSiteId=' + _.first(selectedRows).attributes.whSiteId +
                    '&outBoundLocationId=' + _.first(selectedRows).attributes.outBoundLocationId,
                success: function(response) {
                	if(response.models.length > 0){
                	      require(['views/material/overview/view/AddtoReqListDialogView'], function(AddtoReqListDialogView) {
                              var addtoReqListDialogView = new AddtoReqListDialogView({
                                  selectedRows: selectedRows,
                                  matchingRequestListCollection: matchingRequestListCollection
                              });
                              Gloria.basicModalLayout.content.show(addtoReqListDialogView);
                              addtoReqListDialogView.on('hide', function() {
                                  Gloria.basicModalLayout.content.reset();
                              });
                          });
                	} else {
                		 require(['views/material/overview/view/EmptyRequestListDialog'], function(EmptyRequestListDialog) {
                             var emptyRequestListDialog = new EmptyRequestListDialog();
                             Gloria.basicModalLayout.content.show(emptyRequestListDialog);
                             emptyRequestListDialog.on('hide', function() {
                                 Gloria.basicModalLayout.content.reset();
                             });
                         });
                	}
                }
            });
        };


        var saveMaterialLinesToReqList = function(selectedRows, matchingRequestListObject, action) {
            var addMaterialLines = new MaterialLineCollection(selectedRows);
            var url = '/material/v1/requestlists/materiallines/' + _.pluck(addMaterialLines.models, 'id').join(',') +
                '?action=' + action +
                '&requiredDeliveryDate=' + matchingRequestListObject.requiredDeliveryDate +
                '&deliveryAddressType=' + matchingRequestListObject.deliveryAddressType +
                '&deliveryAddressId=' + matchingRequestListObject.deliveryAddressId +
                '&deliveryAddressName=' + matchingRequestListObject.deliveryAddressName +
                '&requestlistoid=' + matchingRequestListObject.id;
            if (!_.isNull(matchingRequestListObject.priority)) {
                url = url + '&priority=' + matchingRequestListObject.priority;
            }
            Backbone.sync('update', addMaterialLines, {
                url: url,
                success: function(response) {
                    //showMaterialGridView(); // Refetch!
                	 materialLineCollection.fetch({
                         reset: true
                     });
                    Gloria.MaterialApp.trigger('MaterialRequestList:AddToReqList:saved', response);
                },
                validationError: function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type: 'error',
                        message: errorMessage
                    });
                },
                error: function() {
                    //Gloria.MaterialApp.trigger('MaterialRequestList:sent', false);
                }
            });
        };


        showMaterialLinesToReqListConfirmation = function(RequestListModel) {
            Gloria.trigger('showAppMessageView', {
                type: 'success',
            	title : i18n.t('Gloria.i18n.material.overview.header.confirmMessageHeader'),
                message: new Array({
                    message: i18n.t('Gloria.i18n.material.overview.header.confirmMessage') + ' ' + i18n.t('Gloria.i18n.material.overview.header.requestListId') + ':' + RequestListModel.id + ' and ' + i18n.t('Gloria.i18n.material.overview.header.requestListDeliveryAddress') + ':' + RequestListModel.deliveryAddressName
                })
            });
        };

        /**
         * Pull
         */
        var pullPart = function(model, data) {
            var dataAsQueryString = $.param(data);
            model.url = '/material/v1/materiallines/' + model.id + '/pull?' + dataAsQueryString;
            model.save(null, {
                success: function(response) {
                    materialLineCollection.reset();
                    prepareMaterialLineOverview(); // Refetch!
                    Gloria.trigger('reset:modellayout');
                },
                validationError: function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type: 'error',
                        modal: true,
                        message: errorMessage
                    });
                }
            });
        };

        /* Open Material Request List
         */
        var openMaterialRequestList = function(requestListId) {
            var requestListHeaderCollection = new Collection();
            var materialLinesCollection = new Collection();
            var ids = '';

            requestListHeaderCollection.fetch({
                url: '/material/v1/requestlists/' + requestListId,
                success: function(response) {},
                async: false
            });

            materialLinesCollection.fetch({
                url: '/material/v1/requestlists/' + requestListId + '/materiallines',
                success: function(response) {
                    materialLinesCollection.each(function(model) {
                        ids = (ids ? ids + ',' : '') + model.id;
                    });
                },
                async: false
            });

            Backbone.history.navigate('material/linesoverview/requests/' + ids, {
                trigger: true
            });

        };

        Controller.MaterialLineController = Marionette.Controller.extend({

            initialize: function() {
                this.initializeListeners();
            },

            initializeListeners: function() {
                this.listenTo(Gloria.MaterialApp, 'filter:materialRequestList', filterMaterialRequestList);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:releasePart', showReleasePartDialog);
                this.listenTo(Gloria.MaterialApp, 'MaterialLines:released', releaseMaterialRequestList);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:borrowPart:show', showBorrowPartDialog);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:borrowPart', borrowMaterialRequestList);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:scrapPart:show', showScrapPartDialog);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:scrapPart', scrapMaterialRequestList);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:pullPart:show', showPullPartDialog);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:pullPart', pullPart);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:open', openMaterialRequestList);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:AddToReqList:show', showAddToReqListDialog);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:AddToReqList:save', saveMaterialLinesToReqList);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:AddToReqList:saved', showMaterialLinesToReqListConfirmation);
            },

            control: function(subView) {
                module = subView;
                prepareMaterialLineOverview.call(this);
            },

            onDestroy: function() {
                module = null;
                materialLineCollection = null;
                materialLineView = null;
                borrowPartDialogView = null;
                sm = null;
            }
        });
    });

    return Gloria.MaterialApp.Controller.MaterialLineController;
});