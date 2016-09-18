define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'hbs!views/mobile/warehouse/view/pick/pickpull-labelprintlist'
], function(Gloria,$, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

    Gloria.module('WarehouseApp.Pick.View', function(View, Gloria, Backbone, Marionette, $, _) {
        var pickListId;
        var module;
        View.PickPullLabelprintListView = Marionette.ItemView.extend({

            initialize : function(options) {
                this.pickListId = options.pickListId;
                this.model = options.model;
                this.module = options.module;
            },
            
            events : {
                'click #print' : 'handlePrintClick',
                'click #cancel' : 'handleCancelClick',
                'change #printSelect' : 'handlePrintSelectChange'
            },
            
            handlePrintSelectChange : function(e) {
                e.preventDefault();
                if(e.currentTarget.value == '2') {
                    $('#printQty').removeAttr('disabled');
                } else {
                    $('#printQty').val('');
                    $('#printQty').attr('disabled', 'disabled');
                };
            },
            
            handleCancelClick : function(e) {
                this.$el.modal('hide');
            },
            
            handlePrintClick : function(e) {
                e.preventDefault();
                var qty; 
                var check_value = $('#printSelect:checked').val();
                if (check_value === '2') {
                    qty = $('#printQty').val();
                } /*else {
                    qty = null;
                }*/
                if (this.module === 'list') {
                    Gloria.WarehouseApp.trigger('Pick:PullLabel:print:List:qty', this.pickListId, qty);
                } else {
                    Gloria.WarehouseApp.trigger('Pick:PullLabel:print:part:qty', this.pickListId, qty);
                }
                this.$el.modal('hide');
            },

            className : 'modal',
            
            id : 'PickPullLabelprintListView',
         
            getTemplate : function() {
            	var that = this;
                return function(data) {                	
                    return _.partial(compiledTemplate, _.extend({module: that.module}, data));
                };
            },

            isValidForm : function() {},
            
            onRender : function() {             
                var that = this;
                this.$el.modal({
                    show : false
                });
                this.$el.on('hidden.bs.modal', function() {
                    that.trigger('hide');
                });                
            },

            onShow : function() {
                this.$el.modal('show');
            },

            onDestroy : function() {
                this.$el.modal('hide');
                this.$el.off('.modal');
                pickListId = null;
                module = null;
            }
        });
    });

    return Gloria.WarehouseApp.Pick.View.PickPullLabelprintListView;
});