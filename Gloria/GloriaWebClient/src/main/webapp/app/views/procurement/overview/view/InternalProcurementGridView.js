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
		'utils/backgrid/clickableRow',
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'grid-util',
		'views/procurement/overview/view/MarkGridCell',
		'views/procurement/helper/ProcureTeamMembersSelectorHelper'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell,
		DropdownHeaderCell, IntegerHeaderCell, NumberHeaderCell, IntegerCell, ClickableRow, 
		BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil, MarkGridCell, ProcureTeamMembersSelectorHelper) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		var EmptyCell = Backgrid.Cell.extend({
			render : function() {
				this.$el.html('');
				return this;
			}
		});
		
		View.InternalProcurementGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.module = options.module;
				this.listenTo(Gloria.ProcurementApp, 'procurelineGrid:clearSelectedModels', this.clearSelectedModels);
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.setGrid();
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},

			collectionEvents: {
                'add remove backgrid:selected': 'handleSelectRow',
                'backgrid:selected': 'sendSelectedRow'
            },
            
            clearFilter : function() {
            	this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},

            handleSelectRow : _.debounce(function(model, selected) {
                var selectedModelsIds = this.gridView.getAllSelectedModelIds();
                var selectedModels = this.gridView.getSelectedModels();
                Gloria.ProcurementApp.trigger('procureRequestLineGrid:select', selectedModelsIds, selectedModels);
            }, 200),
            
            sendSelectedRow: function(model, selected) {
                Gloria.ProcurementApp.trigger('select:model', model, selected);
            },

			setGrid : function() {
				var that = this;
				// Initialize the grid
				this.gridView = new Backgrid.Grid({
					id : that.module,
					className : 'backgrid procurementOverview-grid-main',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
								{
									// Checkbox column
									name : '',
									cell : 'select-row',
									headerCell : 'select-all',
								},
								{
									name : 'needIsChanged',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.needIsChanged'),
									cell : function(options) {
										if(options.model.get('needIsChanged')) {
											options.isInternalProcurer = true;
						                	return new MarkGridCell(options);
										} else {
											return new EmptyCell(options);
										}
									},
									editable : false,
									headerCell : function(options) {
										options.column.isSearchable = false;
										return new StringHeaderCell(options);
									}
								},
								{
									name : 'projectId',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.projectId'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'referenceGroups',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.reference'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'referenceIds',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.referenceIds'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'pPartNumber',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartNo'),
									cell : 'string',
									editable : false,
									headerCell : function(options) {                 
		                                options.tooltip = {
		                                        'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartNoTooltip'),
		                                        'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartNo')
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
                                        options.column.isSearchable = false;
                                        return new StringHeaderCell(options);
                                    }
								},
								{
									name : 'pPartName',
									label :i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartName'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'pPartModification',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartModification'),
									cell : 'string',
									editable : false,
									headerCell : function(options) {                 
                                        options.tooltip = {
                                                'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartModificationTooltip'),
                                                'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartModification')
                                        };
                                        return new StringHeaderCell(options);
                                    }
								},		
								{
									name : 'quantity',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.procQty'),
									cell : IntegerCell,
									editable : false,
					                sortable: false,
					                headerCell : function(options) {                 
                                        options.tooltip = {
                                                'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procQtyTooltip'),
                                                'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procQty')
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
									name : 'changeRequestIds',
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
									name : 'mailFormIds',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.mailFromId'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'procureType',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.source'),
									cell : Backgrid.StringCell.extend({
										formatter : {
											fromRaw : function(rawValue) {
											    return i18n.t('Gloria.i18n.procurement.sourceOptions.' + rawValue);
											}
										}
									}),
									editable : false,
									sortable: false,
									headerCell : function(options) {
										options.column.type = 'select';
										options.column.defaultData = [{
											id : 'INTERNAL',
											text : i18n.t('Gloria.i18n.procurement.sourceOptions.INTERNAL')
										},{
											id : 'INTERNAL_FROM_STOCK',
											text : i18n.t('Gloria.i18n.procurement.sourceOptions.INTERNAL_FROM_STOCK')
										}];
										return new DropdownHeaderCell(options);
									}
								},
								{
									name : 'procureForwardedId',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.internalProcurer'),
									cell : Backgrid.StringCell.extend({
		                                render: function() {
		                                    var procureForwardedId = this.model.get('procureForwardedId') || '';
		                                    var procureForwardedName = this.model.get('procureForwardedName') || '';
		                                    var procureForwardedTeam = this.model.get('procureForwardedTeam') ? " (" + this.model.get('procureForwardedTeam') + ")" : '';
		                                    var value = ((procureForwardedId) ? (procureForwardedId + " " + procureForwardedName + procureForwardedTeam) : "");		                                   
		                                    this.$el.html(value);                           
		                                    return this;
		                                }
		                            }),									    
									editable : false,
									headerCell : function(options) {
										var userList = ProcureTeamMembersSelectorHelper.constructInternalProcureUserList();
										userList.sort(function(first, second) {
										    return (first.firstName + first.lastName).localeCompare(second.firstName + second.lastName);
										});
										options.column.type = 'select';
										var finalList = new Array();
										_.each(userList, function(user) {
											finalList.push({
												id : user.id,
												text : user.firstName + ' ' + user.lastName
											});
										});
										options.column.defaultData = finalList;
										return new DropdownHeaderCell(options);
									}
								}]
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
			
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
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

	return Gloria.ProcurementApp.View.InternalProcurementGridView;
});