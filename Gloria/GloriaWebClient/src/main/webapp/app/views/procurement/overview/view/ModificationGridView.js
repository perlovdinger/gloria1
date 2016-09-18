define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'backgrid',
        'i18next',
        'views/common/paginator/PaginatorView',
        'utils/backgrid/stringHeaderCell',
        'utils/backgrid/integerHeaderCell',
        'utils/backgrid/numberHeaderCell',
		'utils/backgrid/IntegerCell',
        'utils/backgrid/dropdownHeaderCell',
        'utils/backgrid/dateHeaderCell',
        'utils/backgrid/dateFormatter',
        'utils/backgrid/clickableRow',
        'backgrid-select-all',
        'backgrid-select2-cell',
        'bootstrap',
        'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell, IntegerHeaderCell, 
        NumberHeaderCell, IntegerCell, DropdownHeaderCell,
        DateHeaderCell, BackgridDateFormatter, ClickableRow, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil) {

	var CustomStringCell  = Backgrid.Cell.extend({
		className : 'string-cell info-icon',
		formatter : Backgrid.StringFormatter
	});
	
    var columnModel = [
                       {
                           // Checkbox column
                           name : "",
                           cell : "select-row",
                           headerCell : "select-all",
                       },
                       {
                           name : 'functionGroup',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.functionGroup'),
                           cell : 'string',
                           editable : false,
                           headerCell : function(options) {                 
                               options.tooltip = {
                                       'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.functionGroupTooltip'),
                                       'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.functionGroup')
                               };                       
                               return new StringHeaderCell(options);
                           }
                       },
                       {
                           name : 'designResponsible',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.descGroup'),
                           cell : 'string',
                           editable : false,
                           headerCell : function(options) {                 
                               options.tooltip = {
                                       'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.descGroupTooltip'),
                                       'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.descGroup')
                               };                       
                               return new StringHeaderCell(options);
                           }
                       },
                       {
                           name : 'modularHarness',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.modularHarness'),
                           cell : 'string',
                           editable : false,
                           headerCell : function(options) {                 
                               options.tooltip = {
                                       'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.modularHarnessTooltip'),
                                       'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.modularHarness')
                               };                       
                               return new StringHeaderCell(options);
                           }
                       },
                       {
                           name : 'mtrlRequestVersion',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.changeRequestIds'),
                           cell : 'string',
                           editable : false,
                           headerCell : function(options) {                 
                               options.tooltip = {
                                       'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.changeRequestIdsTooltip'),
                                       'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.changeRequestIds')
                               };                       
                               return new StringHeaderCell(options);
                           }
                       },
                       {
                           name : 'referenceId',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.referenceId'),
                           cell : 'string',
                           editable : false,
                           headerCell : StringHeaderCell
                       }, 
                       {
                           name : 'partNumber',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partNo'),
                           cell : function(options) {
			                	if(options.model.get('materialType') == 'USAGE_REPLACED') {
			                		return new CustomStringCell(options);
			                	} else {
			                		return new Backgrid.StringCell(options);
			                	}
			        		},
                           editable : false,
                           headerCell : function(options) {				  
                               options.tooltip = {
                               		"tooltipText": i18n.t('Gloria.i18n.procurement.overviewModule.header.partNoTooltip'),
                               		"displayText" : i18n.t('Gloria.i18n.procurement.overviewModule.header.partNo')
                               };                       
                               return new StringHeaderCell(options);
                           }
                       },  
                       {
                           name : 'partVersion',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.ver'),
                           cell : 'string',
                           editable : false,
                           headerCell : StringHeaderCell
                       },
                       {
                           name : 'partName',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partName'),
                           cell : 'string',
                           editable : false,
                           headerCell : StringHeaderCell
                       }, 
                       {
                           name : 'partModification',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partModification'),
                           cell : 'string',
                           editable : false,
                           headerCell : StringHeaderCell
                       }, 
                       {
                           name : 'quantity',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.qty'),
		                   cell : IntegerCell,
		                   editable : false,
	                       sortable: false,
	                       headerCell : function(options) {				  
			                    options.tooltip = {
			                    		"tooltipText": i18n.t('Gloria.i18n.procurement.overviewModule.header.qtyTooltip'),
			                    		"displayText" : i18n.t('Gloria.i18n.procurement.overviewModule.header.qty'),
			                    		"noSearchAndSort" : true
			                    };                       
			                    return new NumberHeaderCell(options);
			                }	
                       },
                       {
                           name : 'pPartNumber',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.pPartNumber'),
                           cell : Backgrid.StringCell.extend({
								render : function() {
									var pPartNumber = this.model.get('pPartNumber') || '';
									if(this.model.get('materialType') == 'USAGE_REPLACED') {
										this.$el.html('<a id="pPartNumber_' + pPartNumber 
												+ '" href="#procurement/overview/modification/viewDetails/' + this.model.get('procureLineId') + '?type='
												+ this.model.get('modificationType') + '&modificationId=' + this.model.get('modificationId') + '">' + pPartNumber + '</a>'); 
									} else {
										this.$el.html(pPartNumber); 
									}
									return this;
								}
							}),
                           editable : false,
                           headerCell : function(options) {                 
                               options.tooltip = {
                                       'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.pPartNumberTooltip'),
                                       'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.pPartNumber')
                               };                       
                               return new StringHeaderCell(options);
                           }
                       },
                       {
                           name : 'pPartVersion',
                           label : i18n.t('Gloria.i18n.procurement.overviewModule.header.procVersion'),
                           cell : 'string',
                           editable : false,
                           headerCell : function(options) {                 
                               options.tooltip = {
                                       'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procVersionTooltip'),
                                       'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procVersion')
                               };                       
                               return new StringHeaderCell(options);
                           }
                       }];

    Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.ModificationGridView = Marionette.LayoutView.extend({

            initialize : function(options) {
            	this.listenTo(this.collection, 'QueryParams:changed', function() {
            		this.clearSelectedModels();
            		this.handleSelectRow();
				});
                this.setGrid();
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

            handleSelectRow : function(e, model) {                
            	var selectedModels = this.gridView.getSelectedModels(); 
                Gloria.ProcurementApp.trigger('procureRequestLineGrid:select', selectedModels);
            },
            
            clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},

            setGrid : function() {
                // Initialize the grid
                this.gridView = new Backgrid.Grid({
                	id : 'modification',
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

    return Gloria.ProcurementApp.View.ModificationGridView;
});