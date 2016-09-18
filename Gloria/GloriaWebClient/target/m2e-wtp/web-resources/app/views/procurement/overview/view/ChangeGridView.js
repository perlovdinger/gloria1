/**
 * Change Grid
 * Supports Multi-page selection/operations
 */
define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'views/common/paginator/PaginatorView',
		'utils/backgrid/stringHeaderCell',
		'utils/backgrid/dropdownHeaderCell',
		'utils/backgrid/integerHeaderCell',
		'utils/backgrid/numberHeaderCell',
		'utils/backgrid/IntegerCell',
		'utils/backgrid/dateHeaderCell',
		'utils/backgrid/dateFormatter',
		'utils/backgrid/clickableRow',		
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell,
		DropdownHeaderCell, IntegerHeaderCell, NumberHeaderCell, IntegerCell, DateHeaderCell, BackgridDateFormatter, ClickableRow, 
		 BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil) {

	var columnModel = [
			{
				// Checkbox column
				name : "",
				cell : "select-row",
				headerCell : "select-all",
			},
			{
				name : 'changeId',
				label : i18n.t('Gloria.i18n.procurement.overviewModule.header.changeId'),
				cell : 'string',
				editable : false,				
				headerCell : function(options) {                 
                    options.tooltip = {
                            'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.changeIdTooltip'),
                            'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.changeId')
                    };                       
                    return new StringHeaderCell(options);
                },
				sortable: false
			},	
			{
    	    	name : 'receivedDate',
				label : i18n.t('Gloria.i18n.procurement.overviewModule.header.receivedDate'),
				cell : 'string',
				editable : false,						
				formatter : BackgridDateFormatter,
				headerCell : DateHeaderCell,
                sortable: false
    	    },
			{
				name : 'status',
				label : i18n.t('Gloria.i18n.procurement.overviewModule.header.status'),
				cell : Backgrid.StringCell.extend({
                    render: function() {
                        var status = this.model.get('status');
                        this.$el.html(i18n.t('Gloria.i18n.procurement.changeIDStatus.' + status));                           
                        return this;
                    }
                }),
				editable : false,
				headerCell : function(options) {
					options.column.type = 'select';
					options.column.defaultData = [{
						id : 'WAIT_CONFIRM,CANCEL_WAIT',
						text : i18n.t('Gloria.i18n.procurement.changeIDStatus.WAIT_CONFIRM') + ' / ' 
								+ i18n.t('Gloria.i18n.procurement.changeIDStatus.CANCEL_WAIT')
					},{
						id : 'WAIT_CONFIRM',
						text : i18n.t('Gloria.i18n.procurement.changeIDStatus.WAIT_CONFIRM')
					},{
						id : 'ACCEPTED',
						text : i18n.t('Gloria.i18n.procurement.changeIDStatus.ACCEPTED')
					},{
						id : 'REJECTED',
						text : i18n.t('Gloria.i18n.procurement.changeIDStatus.REJECTED')
					},{
						id : 'CANCEL_WAIT',
						text : i18n.t('Gloria.i18n.procurement.changeIDStatus.CANCEL_WAIT')
					},{
						id : 'CANCELLED',
						text : i18n.t('Gloria.i18n.procurement.changeIDStatus.CANCELLED')
					},{
						id : 'CANCEL_REJECTED',
						text : i18n.t('Gloria.i18n.procurement.changeIDStatus.CANCEL_REJECTED')
					}];
					
					return new DropdownHeaderCell(options);
				},
				sortable: false
			}];

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ChangeGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;			
				this.setGrid();
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},

			events : {
				'change .select-row-cell input' : 'handleSelectRow',
				'change .select-all-header-cell input' : 'handleSelectRow',
				'rowdoubleclicked table.backgrid tr' : 'rowDoubleClick'
			},
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
			rowDoubleClick : function(e, model) {
				Gloria.ProcurementApp.trigger('changelineDetails:show', model.id);
			},
			
			handleSelectRow : function(e, model) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.ProcurementApp.trigger('changelineGrid:select', selectedModels);
			},

			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			setGrid : function() {
				// Initialize the grid
				this.gridView = new Backgrid.Grid({
					id : 'change',
					className : 'backgrid procurementOverview-grid-main',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : columnModel
				});
				
				// Initialize the paginator
				this.paginator = new GloriaPaginator({
					collection : this.collection,
					grid : this.gridView,
					postbackSafe : true
				});
			},

			render : function() {				
				// Render the grid
	            var $gridView = this.gridView.render().$el;
				this.$el.html($gridView);

				// Render the paginator
				$gridView.after(this.paginator.render().$el);
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
				this.paginator.remove();
			}
		});
	});

	return Gloria.ProcurementApp.View.ChangeGridView;
});