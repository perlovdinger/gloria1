define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'bootstrap',
		'utils/backgrid/clickableRow',
		'utils/DateHelper',
		'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, Bootstrap, ClickableRow, DateHelper, GridUtil) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.TraceabilityGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.setGrid();
			},
			
			setGrid : function() {
				this.gridView = new Backgrid.Grid({
					id : 'OrderTraceabilityGrid',
					className : 'backgrid',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
						{
                            name : 'loggedTime',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.traceability.loggedTime'),
                            cell : Backgrid.DatetimeCell.extend({
            					formatter : {
            						fromRaw : function(rawValue) {
            							return DateHelper.formatDateTimewithUTC(rawValue);
            						}
            					}
            				}),
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'action',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.traceability.action'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'actionDetail',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.traceability.actionDetail'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'userId',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.traceability.userId'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'userName',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.traceability.userName'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        }
					]
				});
			},

			render : function() {				
				var $gridView = this.gridView.render().$el;
	            this.$el.html($gridView);
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
			
			onDestroy : function() {
				this.gridView.remove();
			}
		});
	});

	return Gloria.MaterialApp.View.TraceabilityGridView;
});
