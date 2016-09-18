define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',        
        'hbs!views/warehouse/receive/view/order-popup'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, compiledTemplate) {
    
    Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.OrderPopup = Marionette.LayoutView.extend({
            
            className : 'modal',
            
            id: 'orderLookupModal',            
            
            regions: {
                grid: '#gridPane'
            },
            
            events : {
                'click #orderSearch' : 'orderLookup',
                'click #ok' : 'orderSelect',
                'click #cancel' : 'cancel'
            },
            
            initialize : function(options) {
                this.listenTo(Gloria.WarehouseApp, 'select:orderlinegrid', this.selectOrderLineGrid);
            },
            
            selectOrderLineGrid: function(selectedRows) {
                if(selectedRows && selectedRows.length == 1){
                    this.selectedOrderLineID = _.first(selectedRows);
                    this.$('#ok').prop('disabled', false);
                } else {
                    this.selectedOrderLineID = null;
                    this.$('#ok').prop('disabled', true);
                }
            },
            
            orderLookup : function(e) {
                e.preventDefault();
                var partNumberOrAlias = this.$('#partNumber').val();
                if(partNumberOrAlias.trim().length == 0) return;
                
                Gloria.WarehouseApp.trigger('search:orderline', partNumberOrAlias);
            },
            
            orderSelect: function(e) {
                e.preventDefault();
                Gloria.WarehouseApp.trigger('select:orderline', this.selectedOrderLineID);
                Gloria.trigger('reset:modellayout');
            },
            
            cancel : function(e) {
                e.preventDefault();
                Gloria.trigger('reset:modellayout');
            },

            render : function() {
                var that = this;
                
                this.$el.html(compiledTemplate());
                
                this.$el.modal({                                        
                    show: false                    
                });
                
                this.$el.on('hidden.bs.modal', function(){                    
                    that.trigger('hide');
                    Gloria.trigger('reset:modellayout');
                });
                
                return this;
            },
            
            onShow: function() {
                this.$el.modal('show');
                this.$('#partNumber').focus();
            },
            
            onDestroy : function() {
                this.$el.modal('hide');
                this.$el.off('.modal');                
            }
        });
    });
    
    return Gloria.WarehouseApp.Receive.View.OrderPopup;
});