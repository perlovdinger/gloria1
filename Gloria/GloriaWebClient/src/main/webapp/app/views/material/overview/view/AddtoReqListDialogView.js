define(['app',
    'jquery',
    'underscore',
    'handlebars',
    'backbone',
    'marionette',
    'bootstrap',
    'datepicker',
    'utils/DateHelper',
    'utils/UserHelper',
    'i18next',
    'hbs!views/material/overview/view/addto-reqlist-dialog',
    'utils/typeahead/RequestListTypeaheadView'
], function(
    Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, Datepicker,
    DateHelper, UserHelper, i18n, compiledTemplate, RequestListTypeaheadView) {

    Gloria.module('MaterialApp.View', function(View, Gloria, Backbone,
        Marionette, $, _) {

        View.AddtoReqListDialogView = Marionette.LayoutView.extend({

            className: 'modal',

            id: 'addToRequestListModal',

            initialize: function(options) {
                this.selectedRows = options.selectedRows;
                this.matchingRequestListCollection = options.matchingRequestListCollection;
                this.requestListOptions;
                
            },

            events: {
                'click #save': 'save',
                'click #cancel': 'cancel',
                'change #requestList': 'handleRequestListChange',
            },

            handleRequestListChange: function(e) {
            	if( !_.isEmpty(e.currentTarget.value)){
            		this.matchingRequestListObject = _.find(this.matchingRequestListCollection.models, function(item) {
                        return item.attributes.id == e.currentTarget.value;
                    });
                    this.$('#deliveryAddressDiv').text("");
                    this.$('#deliveryAddressDiv').text(this.matchingRequestListObject.attributes.deliveryAddressName);
            	} else {
            		this.$('#deliveryAddressDiv').text("");
            	}
            },

            cancel: function() {
                this.$el.modal('hide');
            },

            save: function(e) {
                this.$el.modal('hide');
                Gloria.MaterialApp.trigger('MaterialRequestList:AddToReqList:save', this.selectedRows, this.matchingRequestListObject.attributes, e.target.name);
            },

            showRequestList: function() {
            	var that = this;
            	require(['utils/typeahead/RequestListTypeaheadView'], function(RequestListTypeaheadView) {
					that.requestListTypeaheadView = new RequestListTypeaheadView({
						element : '#requestList',
						matchingRequestListCollection:that.matchingRequestListCollection
					});
				});
            },

            render: function() {
                var that = this;
                var obj = _.object(_.map(this.matchingRequestListCollection.models, function(item) {
              	   return [item.id, item.id];
                }));
                
                this.$el.html(compiledTemplate({
                	requestListOptions :_.toArray(obj)
                }));
                this.$el.modal({
                    show: false
                });
                this.$el.on('hidden.bs.modal', function() {
                    that.trigger('hide');
                    Gloria.trigger('reset:modellayout');
                });
                return this;
            },

            onShow: function() {
                this.$el.modal('show');
                this.showRequestList();
            },

            onDestroy: function() {
                this.$el.modal('hide');
                this.$el.off('.modal');
                this.requestListTypeaheadView && this.requestListTypeaheadView.remove();
            }
        });
    });

    return Gloria.MaterialApp.View.AddtoReqListDialogView;
});