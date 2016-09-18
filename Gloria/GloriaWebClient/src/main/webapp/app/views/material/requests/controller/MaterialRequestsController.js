define(['app',
    'jquery',
    'underscore',
    'handlebars',
    'backbone',
    'marionette',
    'i18next',
    'utils/backbone/GloriaModel',
    'utils/backbone/GloriaCollection',
    'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, GloriaModel, GloriaCollection, UserHelper) {

    Gloria.module('MaterialApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

        var materialLineIds;
        var materialRequestModel;
        var materialLineCollection;
        var materialRequestsView;
        var action;
        var requestListId;
        var materialHeaderStatus;
        /**
         * Prepare Material Requests
         * Initialize Data Source Objects and set page layout
         */
        var prepareMaterialRequests = function() {
            // Initialize Data Source Objects
            initializeDataSourceObjects();
            // Set Page Layout
            setPageLayout();
        };

        /**
         * Initialize Data Source Objects
         */
        var initializeDataSourceObjects = function() {
            materialRequestModel = new GloriaModel();
            materialLineCollection = new GloriaCollection();
        };

        /**
         * Set Page Layout
         */
        var setPageLayout = function() {
            Gloria.basicModalLayout.closeAndReset();
            Gloria.basicLayout.content.reset();
            if(action == "fromRequestListId" && requestListId){
                Gloria.trigger('showBreadcrumbView', { requestListId: requestListId
                });
            } else {
                Gloria.trigger('showBreadcrumbView', {
                    itemId: materialLineIds
                });
            }
            require(['views/material/requests/view/MaterialRequestsView'], function(MaterialRequestsView) {
                materialRequestsView = new MaterialRequestsView({
                    model: materialRequestModel,
                });
                materialRequestsView.on('show', function() {
                    showMaterialGeneralInfo();
                    showMaterialGridInfo();
                }, this);
                Gloria.basicLayout.content.show(materialRequestsView);
            });
        };

        /**
         * Show Material General Information
         */
        var showMaterialGeneralInfo = function() {
            require(['views/material/requests/view/MaterialGenInfoView'], function(MaterialGenInfoView) {
                var materialGenInfoView = new MaterialGenInfoView({
                    model: materialRequestModel,
                });
                materialRequestsView.generalInfo.show(materialGenInfoView);
            });
        };

        /**
         * Process Material General Information if the input is from Create New.
         */
        var processMaterialGeneralInfo = function(collection) {
            if (collection && collection.length > 0) {
                materialRequestModel.set(collection.at(0).attributes);
                materialRequestModel.set({
                    requestListIdStatus: false,
                    requestListIdVal: false
                }, {
                    silent: true
                });
            } else {
                Gloria.MaterialApp.trigger('MaterialRequestList:disableSend');
                Gloria.MaterialApp.trigger('MaterialRequestList:disableSave');
            }
            prepGridLayout();
            Gloria.MaterialApp.trigger('MaterialRequestList:disableCancelRequestList');
        };

        var processMaterialGeneralInfoFromRequestList = function() {
            materialRequestModel.fetch({
                url: '/material/v1/requestlists/' + requestListId,
                success: function(response) {
                    if (!requestListId || (!response.attributes.cancelAllowed) || response.attributes.requestUserId != UserHelper.getInstance().getUserId()) {
                        Gloria.MaterialApp.trigger('MaterialRequestList:disableCancelRequestList');
                    }

                    if (response.attributes.status != "CREATED" || response.attributes.requestUserId != UserHelper.getInstance().getUserId()) {
                        Gloria.MaterialApp.trigger('MaterialRequestList:disableSend');
                        Gloria.MaterialApp.trigger('MaterialRequestList:disableSave');
                    }
                    materialHeaderStatus = response.attributes.status;
                    
                    materialRequestModel.set({
                        requestListIdStatus: response.attributes.status,
                        requestListIdVal: requestListId
                    }, {
                        silent: true
                    });
                    prepGridLayout();
                }
            

            });
        };

        /**
         * Show Material Grid Information
         */
        var showMaterialGridInfo = function() {
            processMaterialRequestsInfo();
        };
        
        var prepGridLayout = function(){
            require(['views/material/requests/view/MaterialRequestsGridView'], function(MaterialRequestsGridView) {
                     materialRequestsGridView = new MaterialRequestsGridView({
                         collection: materialLineCollection,
                         materialHeaderStatus:materialHeaderStatus
                     });                  
                     materialRequestsView.gridInfo.show(materialRequestsGridView);
             });
        };

        /**
         * Process Material Requests Information depending on the input
         */
        var processMaterialRequestsInfo = function() {
            if (action == 'fromRequestListId') {
                var url = '/material/v1/requestlists/' + requestListId + '/materiallines';
                materialLineCollection.fetch({
                    url: url,
                    success: function(response) {
                        processMaterialGeneralInfoFromRequestList(response);
                    }
                });
            } else {
                var url = '/procurement/v1/requestlists/materiallines/' + materialLineIds;
                materialLineCollection.fetch({
                    url: url,
                    success: function(response) {
                        processMaterialGeneralInfo(response);
                    }
                });
            }
        };

        /**
         * Save Material Requests Information
         */
        var saveMaterialLine = function(model) {
            model.url = '/procurement/v1/materiallines/' + model.get('id') + '?action=request';
            model.save(null, {
                type: 'PUT',
                validate: true,
                silent: true,
                validationError: function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type: 'error',
                        message: errorMessage
                    });
                }
            });
        };

        /**
         * Remove Material Line(s) from Material Request List
         */
        var removeMaterialRequestList = function(selectedModels, requestListModel, data, action) {
            var dataAsQueryString = $.param(data);
            var models = new GloriaCollection(selectedModels);
            var requestListId = requestListModel.attributes.requestListIdVal;
            if (requestListId) {
                dataAsQueryString = dataAsQueryString + '&requestlistoid=' + requestListId;
            }
            materialLineCollection.remove(selectedModels);
            Gloria.MaterialApp.trigger('MaterialRequestList:removed', true);
            if (materialLineCollection.length == 0) {
                Gloria.MaterialApp.trigger('MaterialRequestList:disableSend');
                Gloria.MaterialApp.trigger('MaterialRequestList:disableSave');
                Gloria.MaterialApp.trigger('MaterialRequestList:disableCancelRequestList');
            }
            if (requestListId) {
                Backbone.sync('update', models, {
                    url: '/material/v1/requestlists/materiallines/' + _.pluck(selectedModels,'id').join(',') + '?action=' + action + '&' + dataAsQueryString,
                    success: function() {
                        
                    },
                    validationError: function(errorMessage, errors) {
                        Gloria.trigger('showAppMessageView', {
                            type: 'error',
                            message: errorMessage
                        });
                    },
                    error: function() {}
                });
            }
        };

        var manageRequestListOperation = function(data, action) {
            var dataAsQueryString = $.param(data);
            if (requestListId) {
                dataAsQueryString = dataAsQueryString + '&requestlistoid=' + requestListId;
            }
            Backbone.sync('update', materialLineCollection, {
                url: '/material/v1/requestlists/materiallines/' + _.pluck(materialLineCollection.models, 'id').join(',') + '?action=' + action + '&' + dataAsQueryString,
                success: function() {
                    Gloria.MaterialApp.trigger('MaterialRequestList:sent', true);
                },
                validationError: function(errorMessage, errors) {
                    Gloria.trigger('showAppMessageView', {
                        type: 'error',
                        message: errorMessage
                    });
                },
                error: function() {
                    Gloria.MaterialApp.trigger('MaterialRequestList:sent', false);
                }
            });
        };

        /**
         * Cancel Material Request List
         */
        var cancelMaterialRequestList = function() {
            Backbone.history.navigate('material/linesoverview', {
                trigger: true
            });
        };


        Controller.MaterialRequestsController = Marionette.Controller.extend({

            initialize: function() {
                this.initializeListeners();
            },

            initializeListeners: function() {
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:remove', removeMaterialRequestList);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:cancel', cancelMaterialRequestList);
                this.listenTo(Gloria.MaterialApp, 'MaterialRequestList:manageRequestListOperation', manageRequestListOperation);
            },

            control: function(options) {
                materialLineIds = options.ids;
                requestListId = options.id;
                action = options.input;
                prepareMaterialRequests.call(this);
            },

            onDestroy: function() {
                materialLineIds = null;
                materialRequestModel = null;
                materialLineCollection = null;
                materialRequestsView = null;
                action = null;
                requestListId = null;
            }
        });
    });

    return Gloria.MaterialApp.Controller.MaterialRequestsController;
});