define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'backgrid',
	    'i18next',
	    'bootstrap',
	    'utils/backgrid/dateFormatter',
	    'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n, Bootstrap, BackgridDateFormatter, GridUtil) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.AmendmentGridView = Marionette.LayoutView.extend({
	
	        initialize : function(options) {
	            this.collection = options.collection;
	        },
	
	        render : function() {
	            // Initialize the grid
	            this.gridView = new Backgrid.Grid({
	            	id : 'AmendmentGrid',
	                collection : this.collection,
	                emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
		                    name : 'versionDate',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.versionDate'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    formatter : BackgridDateFormatter
		                }, {
		                    name : 'buyerId',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.buyerId'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false
		                }, {
		                    name : 'version',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.version'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false
		                }, {
		                    name : 'partVersion',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.partVersion'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false
		                }, {
		                    name : 'orderStaDate',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.orderStaDate'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    formatter : BackgridDateFormatter,
		                }, {  
                            name: 'quantity',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.quantity'),                            
                            cell : 'string',
                            sortable: false,
                            editable: false
                        }, {
		                    name : 'priceType',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.priceType'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false
		                }, {  
                            name: 'unitPrice',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.unitPrice'),                            
                            cell : 'string',
                            sortable: false,
                            editable: false
                        }, {  
                            name: 'currency',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.currency'),                            
                            cell : 'string',
                            sortable: false,
                            editable: false
                        }
		             ]
	            });
	
	            // Render the grid
	            var $gridView = this.gridView.render().$el;
	            this.$el.append($gridView);
	            
	            return this;
	        },
	        
	        onShow : function() {
                this.gridView.enableGridColumnManipulation({
                    grid : this.gridView,
                    resizable : {
                        postbackSafe : true
                    }
                });
			},
	        
	        onDestroy: function() {
	            this.gridView.remove();
	        }
	    });
	});

    return Gloria.DeliveryControlApp.View.AmendmentGridView;
});
