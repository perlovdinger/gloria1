define(
            ['app', 'jquery', 'underscore', 'handlebars', 'marionette',
                    'hbs!views/warehouse/pick/view/picklist-detail-button'], function(Gloria, $, _, Handlebars,
                Marionette, compiledTemplate) {

            Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

                View.PickListDetailsButtonView = Marionette.View.extend({

                    initialize : function(options) {
                        this.model = options.model;
                        this.lockFailed = options.lockFailed;
                    },

                    events : {
                        'click #pick-button' : 'handlePickButtonClick',
                        'click #cancel-button' : 'handleCancelClick',
                        'click #cancel-picklist-button' : 'handleCancelPicklistClick',
                        'click #printPickList' : 'printPickList',
                        'click #printPullLabelList' : 'printPullLabelList',
                        'click #printPullLabelPart' : 'printPullLabelPart',
                        'click #print-pick-list-button' : 'handlePrintPickListClick',
                        'click #print-pull-button' : 'handlePrintPullListClick',
                        'click #pickAndMarkAsShip-button' : 'handlePickAndMarkAsShipClick'
                    },

                    handlePickButtonClick : function(e) {
                        e.preventDefault();
                        Gloria.WarehouseApp.trigger('Pick:Grid:validate', 'pick');
                    },

                    printPickList : function(e) {
                        e.preventDefault();
                        Gloria.WarehouseApp.trigger('Pick:print:pickList');
                    },

                    printPullLabelList : function(e) {
                        e.preventDefault();
                        Gloria.WarehouseApp.trigger('Pick:print:pickList:printPullLabelList');
                    },

                    printPullLabelPart : function(e) {
                        e.preventDefault();
                        Gloria.WarehouseApp.trigger('Pick:print:pickList:printPullLabelPart');
                    },

                    handlePickAndMarkAsShipClick : function(e) {
                        e.preventDefault();
                        Gloria.WarehouseApp.trigger('Pick:Grid:validate', 'pickAndMarkAsShip');
                    },

                    handleCancelClick : function(e) {
                        e.preventDefault();
                        var that = this;
                        if (that.model.get('status') == 'PICKED') {
                            // Redirect to ShipGrid
                            window.history.back();
                        } else {
                            // Redirect to PickModule
                            Backbone.history.navigate('warehouse/pick', {
                                trigger : true
                            });
                        }
                    },

                    handleCancelPicklistClick : function(e) {
                        e.preventDefault();
                        var that = this;
                        Gloria.WarehouseApp.trigger('CancelPickListDetail:show');
                    },

                    enableOrDiableCancelPicklistButton : function() {
                        if (this.model.get('status') == 'CREATED' || this.model.get('status') == 'IN_WORK') {
                            return true;
                        } else {
                            return false;
                        }
                    },

                    render : function() {
                        this.$el.html(compiledTemplate({
                            isDisabled : this.model.get('status') == 'PICKED' || this.lockFailed,
                            isEditable : this.enableOrDiableCancelPicklistButton(),
                            shipSkippable : this.model.get('shipSkippable') && this.model.get('status') != 'PICKED'
                                    && !this.lockFailed
                        }));
                        return this;
                    }
                });
            });

            return Gloria.WarehouseApp.View.PickListDetailsButtonView;
        });
