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
		'utils/backgrid/FlagHeaderCell',
		'utils/backgrid/FlagCell',
		'utils/backgrid/SpannedCell',
		'utils/backgrid/dateFormatter',
		'utils/backgrid/clickableRow',
		'utils/handlebars/PartAffiliationSelectorHelper',
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'grid-util',
		'views/procurement/overview/view/MarkGridCell',
		'utils/UserHelper'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell,
		DropdownHeaderCell, IntegerHeaderCell, NumberHeaderCell, IntegerCell, DateHeaderCell, FlagHeaderCell, FlagCell, SpannedCell, BackgridDateFormatter, ClickableRow, 
		partAffiliationSelectorHelper, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil, MarkGridCell, UserHelper) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		var EmptyCell = Backgrid.Cell.extend({
			render : function() {
				this.$el.html('');
				return this;
			}
		});
		
		View.OverviewGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.module = options.module;
				this.hasRolePI = options.hasRolePI;
				this.filter = (options.filter && options.filter.toUpperCase());
				this.isSourceColumnHidden = options.isSourceColumnHidden;
				this.listenTo(Gloria.ProcurementApp, 'procurelineGrid:clearSelectedModels', this.clearSelectedModels);
				this.listenTo(Gloria.ProcurementApp, 'procureline:rows:highlight', this.highlightRows);
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
			
			rowDoubleClick : function(e, model) {
				if (this.filter.toUpperCase() != 'ONBUILDSITE' && model && model.id) {
					var procureId = model.id;
					Gloria.ProcurementApp.trigger('procurelineDetails:show', procureId);
				} else {
					return;
				}
			},

			/**
			 * Called when the user clicks the checkbox on one line
			 */
			handleSelectRow : function(e, model) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.ProcurementApp.trigger('procurelineGrid:select', selectedModels);
			},
			
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			highlightRows: function(collection) {
				(window.setImmediate || window.setTimeout)(_.bind(function() {
					collection.each(function(model) {
						//this.$('td#projectId_' + model.id).closest('tr').addClass('border-yellow');
					});
				}, this), 0);
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
											options.isInternalProcurer = that.hasRolePI;
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
									cell : Backgrid.StringCell.extend({
										render: function() {
											if(this.model && this.model.get('procureCommmentExist')) {
												this.$el.addClass('envelope-icon color-blue-envelope-icon top-right');
											}
											return Backgrid.StringCell.prototype.render.apply(this, arguments);
										}
									}),
									editable : false,
									headerCell : StringHeaderCell
								},						
								{
									name : 'pPartAffiliation',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partAffiliation'),
									cell : 'string',
									editable : false,
									columnChooser: {                            	
		                            	choosable: that.module != 'toProcure'
		                            },
									renderable : that.filter.toUpperCase() != 'TOPROCURE',
									headerCell : function(options) {
									    var data = partAffiliationSelectorHelper.constructPartAffiliationList();
									    options.column.type = 'select';
					                    options.column.defaultData = _.map(data, function(value, key){
					                        if(value && value.code) {
					                            return {
					                                id : value.code,
					                                text : value.code
					                            };
					                        } else {
					                            return;
					                        }
					                    });
					                                   
					                    options.tooltip = {
					                    		'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.partAffiliationTooltip'),
					                    		'displayText' : i18n.t('Gloria.i18n.procurement.overviewModule.header.partAffiliation')
					                    };   
					                    return new DropdownHeaderCell(options);
					                }
								},
								{
									name : 'pPartNumber',
									label : that.filter.toUpperCase() == 'TOPROCURE' ? i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartNo') : 
											i18n.t('Gloria.i18n.procurement.overviewModule.header.partNo'),
									cell : 'string',
									editable : false,
									headerCell : function(options) {	
									    if(that.filter.toUpperCase() == 'TOPROCURE') {
									        options.tooltip = {
					                    		'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartNoTooltip'),
					                    		'displayText' : i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartNo')
									        }; 
									    } else {
									        options.tooltip = {
                                                'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.partNoTooltip'),
                                                'displayText' : i18n.t('Gloria.i18n.procurement.overviewModule.header.partNo')
	                                        };
									    }
					                    return new StringHeaderCell(options);
					                }
								},
								{
									name : 'pPartVersion',
									label : that.filter.toUpperCase() == 'TOPROCURE' ? i18n.t('Gloria.i18n.procurement.overviewModule.header.procVersion') : 
											i18n.t('Gloria.i18n.procurement.overviewModule.header.version'),
									cell : 'string',
									editable : false,
									headerCell : function(options) {                 
                                        options.tooltip = {
                                                'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procVersionTooltip'),
                                                'displayText' :  that.filter.toUpperCase() == 'TOPROCURE' ? i18n.t('Gloria.i18n.procurement.overviewModule.header.procVersion') : 
                                                    i18n.t('Gloria.i18n.procurement.overviewModule.header.version')
                                        };                       
                                        return new StringHeaderCell(options);
                                    }
								},
								{
									name : 'pPartName',
									label : that.filter.toUpperCase() == 'TOPROCURE' ? i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartName') : 
											i18n.t('Gloria.i18n.procurement.overviewModule.header.partName'),
									cell : 'string',
									editable : false,
									headerCell : function(options) {
									    if(that.filter.toUpperCase() == 'TOPROCURE') {
									        options.tooltip = {
                                                'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartNameTooltip'),
                                                'displayText' : i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartName')
									        }; 
									    }
                                        return new StringHeaderCell(options);
                                    }
								},
								{
									name : 'pPartModification',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartModification'),
									cell : 'string',
									editable : false,
									renderable : that.filter.toUpperCase() == 'TOPROCURE',
									headerCell : function(options) {                 
                                        options.tooltip = {
                                                'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartModificationTooltip'),
                                                'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.procPartModification')
                                        };                       
                                        return new StringHeaderCell(options);
                                    }
								},	
								{
									name : 'procureQty',
									label : that.filter.toUpperCase() == 'TOPROCURE' ? i18n.t('Gloria.i18n.procurement.overviewModule.header.procQty') : 
											i18n.t('Gloria.i18n.procurement.overviewModule.header.qty'),
									cell : IntegerCell.extend({
										initialize: function(options) {											
											IntegerCell.prototype.initialize.apply(this, arguments);
								        	this.model.get('hasUnread') && this.$el.addClass('arrow-up-icon'); 
								        },
								        className: IntegerCell.prototype.className + ' text-left' 
									}),																
									editable : false,
					                sortable: false,
					                headerCell : function(options) {				  
					                    options.tooltip = {
					                    		'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.qtyTooltip'),
					                    		'displayText' : that.filter.toUpperCase() == 'TOPROCURE' ? i18n.t('Gloria.i18n.procurement.overviewModule.header.procQty') : 
																i18n.t('Gloria.i18n.procurement.overviewModule.header.qty'),
					                    		'noSearchAndSort' : true
					                    };
					                    return new NumberHeaderCell(options);
					                }	
								},
								{
									name : 'modularHarness',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.modularHarness'),
									cell : 'string',
									editable : false,
									renderable : that.filter.toUpperCase() == 'TOPROCURE',
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
                                                'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.changeRequestIds'),
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
									name : 'requiredStaDate',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.requiredStaDate'),
									cell : 'date',
									editable : false,
									formatter : BackgridDateFormatter,
									headerCell : DateHeaderCell
								},
								{
									name : 'outboundLocationId',
									label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.buildSiteId'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell,
									columnChooser: {                            	
		                            	choosable: that.module != 'toProcure'
		                            },
									renderable : that.module == 'onBuildSite'
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
									renderable : that.module.toUpperCase() == 'TOPROCURE',
									headerCell : function(options) {
										options.column.type = 'select';										
										options.column.defaultData = [{
											id : 'INTERNAL',
											text : i18n.t('Gloria.i18n.procurement.sourceOptions.INTERNAL')
										}];
										
										if(UserHelper.getInstance().checkPermission('ProcuredOverview', 'viewExternalSourceFilter')) {
											options.column.defaultData.push({
												id : 'EXTERNAL',
												text : i18n.t('Gloria.i18n.procurement.sourceOptions.EXTERNAL')
											});
										}
										
										options.column.defaultData.push({
											id : 'FROM_STOCK',
											text : i18n.t('Gloria.i18n.procurement.sourceOptions.FROM_STOCK')
										});
										options.column.defaultData.push({
											id : 'INTERNAL_FROM_STOCK',
											text : i18n.t('Gloria.i18n.procurement.sourceOptions.INTERNAL_FROM_STOCK')
										});
										
										if(UserHelper.getInstance().checkPermission('ProcuredOverview', 'viewExternalSourceFilter')) {
											options.column.defaultData.push({
												id : 'EXTERNAL_FROM_STOCK',
												text : i18n.t('Gloria.i18n.procurement.sourceOptions.EXTERNAL_FROM_STOCK')
											});											
										}
										
										return new DropdownHeaderCell(options);
									}
								},
								{  
		                            name: 'statusFlag',
		                            label : i18n.t('Gloria.i18n.procurement.overviewModule.header.toprocureStatusFlag'),                            
		                            cell : function(options) {
		                                options.className = 'xs-fixedWidth';
		                                return new FlagCell(options);
		                            },
		                            sortable: false,
		                            renderable : that.filter.toUpperCase() == 'TOPROCURE',
		                            headerCell : function(options) {
		                            	options.isInternalProcurer = that.hasRolePI;
		                                return new FlagHeaderCell(options);
		                            },
		                            moduleName : 'TOPROCURE',
		                            isInternalProcurer : that.hasRolePI,
		                            columnChooser: {                            	
		                            	choosable: true
		                            }
								 },
								{
									name : 'supplierName',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.internalSupplier'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell,
									columnChooser: {                            	
		                            	choosable: !!that.hasRolePI
		                            },
		                            renderable : false
								}]
				});
				
				// Initialize the paginator
				this.paginator = new GloriaPaginator({
					collection : this.collection,
					grid : this.gridView,
	                showColumnSettings: this.module == 'toProcure',
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

	return Gloria.ProcurementApp.View.OverviewGridView;
});