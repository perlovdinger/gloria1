define(['app',
        'jquery',
        'backgrid',
        'bootstrap',
        'i18next',
        'utils/backgrid/NestedGridCell',        
        'views/deliverycontrol/myorderoverview/view/ExpectedArrivalCell',
        'utils/backgrid/FlagCell',
        'utils/backgrid/IntegerCell',
        'utils/backgrid/dateFormatter',
        'utils/UserHelper'
], function(Gloria, $, Backgrid, Bootstrap, i18n, NestedGridCell, ExpectedArrivalCell, FlagCell, IntegerCell, DateFormatter, UserHelper) {
    

    Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.DeliveryScheduleCell = NestedGridCell.extend({
            
            className: (NestedGridCell.prototype.className || '') + ' no-padding', 
            
            attributes: {
                'colspan': '3'
            },
            
            columnMap: {
                'deliverySchedule': 'expectedQuantity',
                'expectedDate': 'expectedDate',
                'statusFlag': 'statusFlag'
            },
            
            initialize: function(option) {
                this.makeNestedGridOptions();
                NestedGridCell.prototype.initialize.apply(this, arguments);
                this.listenTo(this.model.collection, 'backgrid:refresh', function() {
                	this.markExpectedQuantity();
                    Gloria.DeliveryControlApp.trigger('MyOrderOverview:deliverySchedule:fetch', {                        
                        model: this.model                     
                    });
                });
                this.listenTo(this.model.deliverySchedules, 'change:expectedQuantity backgrid:refresh', this.markExpectedQuantity);
                this.listenTo(this.model, 'change', this.render);               
            },
            
            makeNestedGridOptions: function() {
            	var that = this;
                return this.nestedGridOptions = { 
                    className: 'table no-margin nested-table',
                    collection: this.model.deliverySchedules,
                    emptyText: i18n.t('Gloria.i18n.general.noRow'),
                    columns:  [{
                        name : 'expectedQuantity',
                        label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.expectedQuantity'),
                        cell : function(options) {                        	
                            options.className = that.model.get('deliveryDeviation') ? 'antiquewhite integer-cell expectedqty' : 'integer-cell expectedqty';
                            return new Backgrid.IntegerCell(options);
                        },
                        editable : false,
                        sortable: false
                    }, {
                        name : 'expectedDate',
                        label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.expectedDate'),        
                        cell : function(options) {                        	
                            options.className = that.model.get('deliveryDeviation')? 'string-cell xs-fixedWidth antiquewhite'  :'string-cell xs-fixedWidth';
                            return new ExpectedArrivalCell(options);
                        }, 
                        formatter : DateFormatter,
                        editable : UserHelper.getInstance().hasPermission('edit', ['DeliveryControlPartDetail']),
                        sortable: false
                    }, {
                        name : 'statusFlag',
                        label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.statusFlag'),
                        cell : function(options) {
                            options.className = 'xs-fixedWidth';
                            return new FlagCell(options);
                        },
                        editable : UserHelper.getInstance().hasPermission('edit', ['DeliveryControlPartDetail']),
                        sortable: false
                    }]
                };
            },
            
            render: function() {
                NestedGridCell.prototype.render.apply(this, arguments);
                this.grid.header.remove();
                return this;
            },
            
            markExpectedQuantity: function() {
                var total = this.model.deliverySchedules.getTotalNumber('expectedQuantity'); 
                var actualExpectedQty = (this.model.attributes.allowedQuantity - this.model.attributes.receivedQuantity) ;               
                if(total != actualExpectedQty ) {
                	this.$el.find('td[id^="expectedQuantity"]').each(function() {
                		$(this).addClass('markPassedDate');
                	});
                }
            },
            
            remove: function() {                
                NestedGridCell.prototype.remove.apply(this, arguments);
            }
        });
    });
    return Gloria.DeliveryControlApp.View.DeliveryScheduleCell;
});