define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',        
        'hbs!views/warehouse/receive/view/deliverynote-popup'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, compiledTemplate) {
    
    Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.DeliveryNotePopup = Marionette.LayoutView.extend({
            
            className : 'modal',
            
            id: 'deliveryNoteLookupModal',            
            
            regions: {
                grid: '#gridPane'
            },
            
            events : {
                'click #orderSearch' : 'deliveryNoteLookUp',
                'click #ok' : 'deliveryNoteSelect',
                'click #cancel' : 'cancel'
            },
            
            initialize : function(options) {
                this.listenTo(Gloria.WarehouseApp, 'TransferReturnGrid:select', this.handleTransferReturnGridSelect);
            },
            
            handleTransferReturnGridSelect: function(selectedRows) {
                if(selectedRows && selectedRows.length == 1){
                    this.selecteLineId = _.first(selectedRows);
                    this.$('#ok').prop('disabled', false);
                } else {
                    this.selecteLineId = null;
                    this.$('#ok').prop('disabled', true);
                }
            },
            
            deliveryNoteLookUp : function(e) {
                e.preventDefault();
                var partNumberOrAlias = this.$('#partNumber').val();
                if(partNumberOrAlias.trim().length == 0) return;
                Gloria.WarehouseApp.trigger('search:deliveryNoteTransferReturn', partNumberOrAlias);
            },
            
            deliveryNoteSelect: function(e) {
                e.preventDefault();
                Gloria.WarehouseApp.trigger('select:deliveryNoteTransferReturn', this.selecteLineId);
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
    
    return Gloria.WarehouseApp.Receive.View.DeliveryNotePopup;
});