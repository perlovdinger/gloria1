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
		'utils/handlebars/PartAffiliationSelectorHelper',
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'grid-util',
		'views/procurement/overview/view/MarkGridCell',
		'utils/UserHelper'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell,
		DropdownHeaderCell, IntegerHeaderCell, NumberHeaderCell, IntegerCell, ClickableRow, 
		partAffiliationSelectorHelper, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil, MarkGridCell, UserHelper) {

	var EmptyCell = Backgrid.Cell.extend({
		render : function() {
			this.$el.html('');
			return this;
		}
	});

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ProcuredGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.hasRolePI = options.hasRolePI;
				this.setGrid();
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},

			events : {
				'change .select-row-cell input' : 'handleSelectRow',
				'change .select-all-header-cell input' : 'handleSelectRow',
				'rowdoubleclicked table.backgrid tr' : 'rowDoubleClick'
			},
			
			clearFilter : function() {
				var mandatoryAttributes = {status : 'PROCURED,PLACED,RECEIVED_PARTLY,RECEIVED'};
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView, mandatoryAttributes);
			},
			
			rowDoubleClick : function(e, model) {
				if(UserHelper.getInstance().hasPermission('edit', ['ProcureDetails'])) {
					var procureId = model.id;
					Gloria.ProcurementApp.trigger('procurelineDetails:show', procureId);
				}
			},

			handleSelectRow : function(e, model) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.ProcurementApp.trigger('procurelineGrid:select', selectedModels);
			},
			
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},

			setGrid : function() {
				// Initialize the grid
				var that = this;
				this.gridView = new Backgrid.Grid({
					id : 'procured',
					className : 'backgrid procurementOverview-grid-main',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns :  [
								{
									// Checkbox column
									name : "",
									cell : "select-row",
									headerCell : "select-all",
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
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'pPartAffiliation',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partAffiliation'),
									cell : 'string',
									editable : false,
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
					                    		"tooltipText": i18n.t('Gloria.i18n.procurement.overviewModule.header.partAffiliationTooltip'),
					                    		"displayText" : i18n.t('Gloria.i18n.procurement.overviewModule.header.partAffiliation')
					                    };   
					                    return new DropdownHeaderCell(options);
					                }
								},
								{
									name : 'pPartNumber',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partNo'),
									cell : 'string',
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
									name : 'pPartVersion',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.version'),
									cell : 'string',
									editable : false
								},
								{
									name : 'pPartName',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partName'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'pPartModification',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partModification'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'requisitionId',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.requisitionId'),									
									cell : Backgrid.StringCell.extend({
										formatter : {
											fromRaw : function(rawValue, model) {
												if( model.get('procureType') == 'EXTERNAL' || model.get('procureType') == 'EXTERNAL_FROM_STOCK') {
													return rawValue;
												} else {
													return '';
												}		    
											}
										}
									}),
									editable : false,
									headerCell : StringHeaderCell.extend({
										actAsNumber: false
									})
								},	
								{
									name : 'orderNo',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.orderNo'),
									cell : 'string',
									editable : false,
									headerCell : function(options) {
					                    options.tooltip = {
					                        'tooltipText': i18n.t('Gloria.i18n.procurement.overviewModule.header.orderNoTooltip'),
					                        'displayText': i18n.t('Gloria.i18n.procurement.overviewModule.header.orderNo')
					                    };                    
					                    return new StringHeaderCell(options);
					                }
								},
								{
									name : 'buyerCode',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.buyerCode'),
									cell : Backgrid.StringCell.extend({
                                        formatter : {
                                            fromRaw : function(rawValue, model) {
                                            	var buyerCode = model.get('buyerCode') || '';
                                            	var buyerName = model.get('buyerName') || '';
                                                return buyerCode ? buyerCode + '/' + buyerName : buyerName;
                                            }
                                        }
                                    }),
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'partQualifier',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.partQualifier'),
									cell : 'string',
									editable : false,
									renderable: false,
									headerCell : StringHeaderCell
								},
								{
									name : 'procureQty',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.qty'),				
									cell : 'integer',
									formatter : {
										fromRaw : function(rawData, model) {
											return parseInt(model.get('additionalQuantity'), 10) + parseInt(model.get('usageQty'), 10);
										}
									},
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
									name : 'status',
									label : i18n.t('Gloria.i18n.procurement.overviewModule.header.status'),
									cell : Backgrid.StringCell.extend({
										formatter : {
											fromRaw : function(rawValue) {
											    return i18n.t('Gloria.i18n.procurement.procureLineStatus.' + rawValue);
											}
										}
									}),
									editable : false,
									headerCell : function(options) {
										options.column.type = 'select';
										options.column.noAll = true;
										options.column.defaultData = [{
											id : 'PROCURED,PLACED,RECEIVED_PARTLY,RECEIVED',
											text : i18n.t('Gloria.i18n.all')
										},{
											id : 'PROCURED',
											text : i18n.t('Gloria.i18n.procurement.procureLineStatus.PROCURED')
										},{
					                        id : 'PLACED',
					                        text : i18n.t('Gloria.i18n.procurement.procureLineStatus.PLACED')
					                    },{
					                        id : 'RECEIVED_PARTLY',
					                        text : i18n.t('Gloria.i18n.procurement.procureLineStatus.RECEIVED_PARTLY')
					                    },{
					                        id : 'RECEIVED',
					                        text : i18n.t('Gloria.i18n.procurement.procureLineStatus.RECEIVED')
					                    }];
										
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

	return Gloria.ProcurementApp.View.ProcuredGridView;
});