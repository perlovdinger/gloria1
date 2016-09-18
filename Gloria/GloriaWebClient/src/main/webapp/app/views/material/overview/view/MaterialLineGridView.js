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
		'views/material/helper/BuildLocationSelectorHelper',
		'views/material/helper/WarehouseSiteSelectorHelper',
		'utils/backgrid/dateHeaderCell',
		'utils/backgrid/dateFormatter',
		'utils/backgrid/TooltipCell',
		'utils/backgrid/clickableRow',
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'utils/UserHelper',
		'utils/backgrid/SameLevelGroupedGridRow',
		'grid-util',
		'utils/backgrid/DateFilterHeader/DateHeaderFilterCell',
		'utils/handlebars/PartAffiliationSelectorHelper'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, 
        StringHeaderCell, DropdownHeaderCell, BuildLocationSelectorHelper, WarehouseSiteSelectorHelper, 
        DateHeaderCell, BackgridDateFormatter, TooltipCell, ClickableRow, BackgridSelectAll, 
        BackgridSelect2Cell, Bootstrap, UserHelper, SameLevelGroupedGridRow, GridUtil, DateHeaderFilterCell, PartAffiliationSelectorHelper) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		var CustomStringCell = Backgrid.Cell.extend({
			className : 'integer-cell additionalQty',
			formatter : Backgrid.StringFormatter
		});
		
		var VersionCell  = Backgrid.Cell.extend({
			className : 'string-cell info-icon',
			formatter : Backgrid.StringFormatter
		});
		
		var ReleasedStringCell = Backgrid.Cell.extend({
			className : 'integer-cell',
			formatter : Backgrid.StringFormatter,
			render: function(options) {
	            this.$el.html('<label class="label label-default releasedQty">' + this.model.get('quantity') + '</label>');
	            return this;
	        }
		});
		
		var AdditionalUsageStringCell = Backgrid.Cell.extend({
			className : 'string-cell',
			formatter : Backgrid.StringFormatter,
			render: function(options) {
	            this.$el.html('ADDITIONAL');
	            return this;
	        }
		});

		View.MaterialLineGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.listenTo(Gloria.MaterialApp, 'MaterialLineGrid:clearSelectedModels', this.clearSelectedModels);
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.setGrid();
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},
			
			events : {
				'click a[id^="requestListCode_"]' : 'handleRequestListIdClick',
				'change .select-row-cell input' : 'handleSelectRow',
				'change .select-all-header-cell input' : 'handleSelectRow',
				'rowdoubleclicked table.backgrid tr' : 'rowDoubleClick'
			},
			
			handleRequestListIdClick : function(e) {
	        	e.preventDefault();
	        	e.stopPropagation();
                var requestListId = $(e.currentTarget).attr('data-requestListid');
				Backbone.history.navigate('material/linesoverview/requestlist/' + requestListId, {
					trigger : true,
					replace :true
				});
	        },
	       
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
			handleSelectRow : _.debounce(function(e) {
	            var selectedModels = this.gridView.getSelectedModels();
	            Gloria.MaterialApp.trigger('MaterialOverview:selectGridLine', selectedModels);
	        }, 200),
	        
	        rowDoubleClick : function(e, model) {
	        	Backbone.history.navigate('material/linesoverview/linedetails/' + model.id, {
					trigger : true
				});
			},

			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			setGrid : function() {
				var binlocations = BuildLocationSelectorHelper.constructBuildLocationList();
				this.gridView = new Backgrid.Grid({
					id : 'MaterialLineGrid',
					className : 'backgrid MaterialLine-grid-main',
					row : function(options) {
					    options.groupBy = 'materialId';
					    options.classNames = ['groupedRow-group-1', 'groupedRow-group-2'];
					    return new SameLevelGroupedGridRow(options);
					},
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
						{
							// Checkbox column
							name : "",
							cell : "select-row",
							headerCell : "select-all",
						},
						{
                            name : 'orderNo',
                            label : i18n.t('Gloria.i18n.material.overview.header.orderNo'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            headerCell : function(options) {                                
                                options.tooltip = {
                                    'tooltipText': i18n.t('Gloria.i18n.material.overview.header.orderNoTooltip'),
                                    'displayText' : i18n.t('Gloria.i18n.material.overview.header.orderNo')
                                };
                                return new StringHeaderCell(options);
                            }
                        },
                        {
                            name : 'projectId',
                            label : i18n.t('Gloria.i18n.material.overview.header.projectId'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'referenceGroup',
                            label : i18n.t('Gloria.i18n.material.overview.header.referenceGroup'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'referenceId',
                            label : i18n.t('Gloria.i18n.material.overview.header.referenceId'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'buildName',
                            label : i18n.t('Gloria.i18n.material.overview.header.phase'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'mtrlRequestVersion',
                            label : i18n.t('Gloria.i18n.material.overview.header.changeRequestId'),
                            cell :  function(options) {
                            	options.column.tooltip = {
                            		title : function() {
                            		    var txt = i18n.t('Gloria.i18n.material.overview.text.changeRequestType') + ': ';
                            		    var type = options.model.get('mtrlRequestType');
                            		    if (type) {
                                            txt += i18n.t('Gloria.i18n.materialrequest.details.generalInformation.sdtype.' + type);
                            		    }
                            		    return txt;
                            		}()
								};
								return new TooltipCell(options);
							},
                            sortable: false,
                            editable : false,
                            headerCell : function(options) {                 
                                options.tooltip = {
                                        'tooltipText': i18n.t('Gloria.i18n.material.overview.header.changeRequestIdTooltip'),
                                        'displayText': i18n.t('Gloria.i18n.material.overview.header.changeRequestId')
                                };                       
                                return new StringHeaderCell(options);
                            },
                        },
                        {
                            name : 'pPartNumber',
                            label : i18n.t('Gloria.i18n.material.overview.header.pPartNumber'),
                            cell : 'string',
                            sortable: true,
                            editable : false,
                            headerCell : function(options) {                                
                                options.tooltip = {
                                    'tooltipText': i18n.t('Gloria.i18n.material.overview.header.pPartNumberTooltip'),
                                    'displayText' : i18n.t('Gloria.i18n.material.overview.header.pPartNumber')
                                };
                                return new StringHeaderCell(options);
                            }
                        },
                        {
                            name : 'pPartVersion',
                            label : i18n.t('Gloria.i18n.material.overview.header.pPartVersion'),
                            cell : function(options) {
                            	if(options.model.get('alertPartVersion')) {
                            		return new VersionCell(options);
                            	} else {
                            		return new Backgrid.StringCell(options);
                            	}
                    		},
                            sortable: true,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'pPartName',
                            label : i18n.t('Gloria.i18n.material.overview.header.pPartName'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'outboundStartDate',
                            label : i18n.t('Gloria.i18n.material.overview.header.outBoundStartDate'),
                            cell : 'date',
                            editable : false,
                            formatter : BackgridDateFormatter,
							headerCell : DateHeaderCell
                        }, 
                        {
                            name : 'expirationDate',
                            label : i18n.t('Gloria.i18n.material.overview.header.expirationDate'),
                            cell : Backgrid.StringCell.extend({
			                    render: function() {
			                    	 Backgrid.Cell.prototype.render.apply(this, arguments);
			                         if(this.model.get('markPassedDate')) {             
			                             this.$el.addClass('markPassedDate');
			                         } else {
			                             this.$el.removeClass('markPassedDate');
			                         }
			                         return this;
			                    }
			                }),
                            editable : false,
                            formatter : BackgridDateFormatter,
                            customFilters: ['expirationDate', 'expirationDateFrom', 'expirationDateTo', 'allExpirationDate'],
                            headerCell :  function(options) {
                                options.dateFilterBindings = {
                                        on: 'expirationDate',
                                        from: 'expirationDateFrom',
                                        to: 'expirationDateTo',
                                        all: 'allExpirationDate'
                                };
                                options.headerTitle = i18n.t('components:dateFilter.defaults.titleExpiredDate');
                                return new DateHeaderFilterCell(options);
                            }
                        },
                        {
                            name : 'outBoundLocationId',
                            label : i18n.t('Gloria.i18n.material.overview.header.outBoundLocationId'),
                            cell : Backgrid.StringCell.extend({
            					formatter : {
            						fromRaw : function(rawValue, model) {
            						    if(!model.get('outBoundLocationId')) return '';
            							return model.get('outBoundLocationId') + ' - ' + model.get('outBoundLocationName'); 
            						}
            					}
            				}),
                            sortable: false,
                            editable : false,
                            headerCell : function(options) {
			                    options.column.defaultData = _.map(binlocations, function(value, key) {
			                        if(value) {
			                            return {
			                                id : value.siteId,
			                                text : value.siteId + '-' + value.siteName
			                            };
			                        } else {
			                            return;
			                        }
			                    });
			                    options.column.select2Options = {
			                    	minimumResultsForSearch : 1,
			                    	dropdownAutoWidth: true
			                    };
			                    return new DropdownHeaderCell(options);
			                }
                        },
                        {
                            name : 'whSiteId',
                            label : i18n.t('Gloria.i18n.material.overview.header.whSiteId'),
                            cell : Backgrid.StringCell.extend({
            					formatter : {
            						fromRaw : function(rawValue, model) {
            						    if(!model.get('whSiteId')) return '';
            							return model.get('whSiteId'); 
            						}
            					}
            				}),
                            sortable: false,
                            editable : false,
                            headerCell : function(options) {
							    var data = WarehouseSiteSelectorHelper.constructWarehouseSiteList();
							    //options.column.type = 'select';
			                    options.column.defaultData = _.map(data, function(value, key){
			                        if(value) {
			                            return {
			                                id : value.siteId,
			                                text : value.siteId + ' - ' + value.siteName
			                            };
			                        } else {
			                            return;
			                        }
			                    });
			                    options.column.select2Options = {
			                    	minimumResultsForSearch : 1,
			                    	dropdownAutoWidth: true
			                    };
			                    return new DropdownHeaderCell(options);
			                }
                        },
                        {
                            name : 'finalWhSiteId',
                            label : i18n.t('Gloria.i18n.material.overview.header.finalWhSiteId'),
                            cell : Backgrid.StringCell.extend({
                                formatter : {
                                    fromRaw : function(rawValue, model) {
                                        if(!model.get('finalWhSiteId')) return '';
                                        return model.get('finalWhSiteId'); 
                                    }
                                }
                            }),
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : function(options) {
                                var data = WarehouseSiteSelectorHelper.constructWarehouseSiteList();
                                //options.column.type = 'select';
                                options.column.defaultData = _.map(data, function(value, key){
                                    if(value) {
                                        return {
                                            id : value.siteId,
                                            text : value.siteId + ' - ' + value.siteName
                                        };
                                    } else {
                                        return;
                                    }
                                });
                                options.column.select2Options = {
                                    minimumResultsForSearch : 1,
                                    dropdownAutoWidth: true
                                };
                                return new DropdownHeaderCell(options);
                            }
                        },
                        {
                            name : 'quantity',
                            label : i18n.t('Gloria.i18n.material.overview.header.quantity'),
                            cell : function(options) {
                            	if(options.model.get('materialType') == 'ADDITIONAL' || options.model.get('materialType') == 'ADDITIONAL_USAGE') {
                            		return new CustomStringCell(options);
                            	} else if(!options.model.get('projectId') && options.model.get('materialType') == 'RELEASED') {
                            		return new ReleasedStringCell(options);
                            	} else {
                            		return new Backgrid.IntegerCell(options);
                            	}
                    		},
                            sortable: false,
                            editable : false,
                            headerCell : function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
                        },{
    	        	    	name : 'requestListID',
    						label : i18n.t('Gloria.i18n.material.overview.header.requestListId'),
    						cell : Backgrid.StringCell.extend({
			                    render: function() {
			                        var val = this.model.get('requestListID') || '';
			                        this.$el.html('<a id="requestListCode_' + val + '" data-requestListid="' + val + '">' + val + '</a>');
			                        return this;
			                    }
			                }),
    						editable : false,
    		                sortable: false,
    		                renderable : true,
    		                headerCell : StringHeaderCell.extend({    		                	
    		                	actAsNumber: true
    		                })
    	        	    },
                        {
                            name : 'status',
                            label : i18n.t('Gloria.i18n.material.overview.header.status'),                            
							cell : Backgrid.StringCell.extend({
			                    render: function() {
			                        var status = this.model.get('status');
			                        this.$el.html('<span' + (status == 'MISSING' ? ' class="color-red"' : '') + '>' 
			                        		+ i18n.t('Gloria.i18n.materialLineStatus.' + status) + '</span>');                           
			                        return this;
			                    }
			                }),
                            sortable: false,
                            editable : false,
                            headerCell : function(options) {
                            	options.column.type = 'select';
            					options.column.defaultData = [{
            						id : 'CREATED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.CREATED')
            					},{
            						id : 'WAIT_TO_PROCURE',
            						text : i18n.t('Gloria.i18n.materialLineStatus.WAIT_TO_PROCURE')
            					},{
            						id : 'REQUISITION_SENT',
            						text : i18n.t('Gloria.i18n.materialLineStatus.REQUISITION_SENT')
            					},{
            						id : 'ORDER_PLACED_INTERNAL',
            						text : i18n.t('Gloria.i18n.materialLineStatus.ORDER_PLACED_INTERNAL')
            					},{
            						id : 'ORDER_PLACED_EXTERNAL',
            						text : i18n.t('Gloria.i18n.materialLineStatus.ORDER_PLACED_EXTERNAL')
            					},{
            						id : 'QI_READY',
            						text : i18n.t('Gloria.i18n.materialLineStatus.QI_READY')
            					},{
            						id : 'BLOCKED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.BLOCKED')
            					},{
                                    id : 'MARKED_INSPECTION',
                                    text : i18n.t('Gloria.i18n.materialLineStatus.MARKED_INSPECTION')
            					},{
            						id : 'READY_TO_STORE',
            						text : i18n.t('Gloria.i18n.materialLineStatus.READY_TO_STORE')
            					},{
            						id : 'STORED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.STORED')
            					},{
            						id : 'REQUESTED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.REQUESTED')
            					},{
                                    id : 'DEVIATED',
                                    text : i18n.t('Gloria.i18n.materialLineStatus.DEVIATED')
                                },{
            						id : 'MISSING',
            						text : i18n.t('Gloria.i18n.materialLineStatus.MISSING')
            					},{
            						id : 'READY_TO_SHIP',
            						text : i18n.t('Gloria.i18n.materialLineStatus.READY_TO_SHIP')
            					},{
            						id : 'SHIPPED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.SHIPPED')
            					},{
                                    id : 'IN_TRANSFER',
                                    text : i18n.t('Gloria.i18n.materialLineStatus.IN_TRANSFER')
                                },{
            						id : 'SCRAPPED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.SCRAPPED')
            					},{
            						id : 'REMOVED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.REMOVED')
            					},{
            						id : 'QTY_DECREASED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.QTY_DECREASED')
            					}];
            					return new DropdownHeaderCell(options);
            				}
                        },
                        {
                            name : 'materialPartModification',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialPartModification'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialPartAffiliation',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialPartAffiliation'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : function(options) {
							    var data = PartAffiliationSelectorHelper.constructPartAffiliationList();
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
			                    return new DropdownHeaderCell(options);
			                }
                        },
                        {
                            name : 'financeHeaderCompanyCode',
                            label : i18n.t('Gloria.i18n.material.overview.header.financeHeaderCompanyCode'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'orderSuffix',
                            label : i18n.t('Gloria.i18n.material.overview.header.orderSuffix'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'procureLineMaterialControllerTeam',
                            label : i18n.t('Gloria.i18n.material.overview.header.procureLineMaterialControllerTeam'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false
                        },
                        {
                            name : 'financeHeaderWbsCode',
                            label : i18n.t('Gloria.i18n.material.overview.header.financeHeaderWbsCode'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'financeHeaderCostCenter',
                            label : i18n.t('Gloria.i18n.material.overview.header.financeHeaderCostCenter'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'financeHeaderGlAccount',
                            label : i18n.t('Gloria.i18n.material.overview.header.financeHeaderGlAccount'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'financeHeaderInternalOrderNoSAP',
                            label : i18n.t('Gloria.i18n.material.overview.header.internalOrderNo'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialMailFormId',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialMailFormId'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialFunctionGroup',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialFunctionGroup'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialDesignResponsible',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialDesignResponsible'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialModularHarness',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialModularHarness'),
                            cell : 'string',
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'procureLineRequisitionId',
                            label : i18n.t('Gloria.i18n.material.overview.header.procureLineRequisitionId'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'procureLineProcureDate',
                            label : i18n.t('Gloria.i18n.material.overview.header.procureLineProcureDate'),
                            cell : 'date',
                            editable : false,
                            renderable : false,
                            formatter : BackgridDateFormatter,
							headerCell : DateHeaderCell
                        },
                        {
                            name : 'procureLineReferenceGps',
                            label : i18n.t('Gloria.i18n.material.overview.header.procureLineReferenceGps'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'procureLineRequiredStaDate',
                            label : i18n.t('Gloria.i18n.material.overview.header.procureLineRequiredStaDate'),
                            cell : 'date',
                            editable : false,
                            renderable : false,
                            formatter : BackgridDateFormatter,
							headerCell : DateHeaderCell
                        },
                        {
                            name : 'procureLineMaterialControllerId',
                            label : i18n.t('Gloria.i18n.material.overview.header.procureLinematerialControllerId'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'procureLineMaterialControllerName',
                            label : i18n.t('Gloria.i18n.material.overview.header.procureLineMaterialControllerName'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialHeaderVersionRequesterUserId',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialHeaderVersionRequesterUserId'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialHeaderVersionRequesterName',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialHeaderVersionRequesterName'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialHeaderVersionContactPersonId',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialHeaderVersionContactPersonId'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialHeaderVersionContactPersonName',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialHeaderVersionContactPersonName'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'storageRoomName',
                            label : i18n.t('Gloria.i18n.material.overview.header.storageRoomName'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'binLocationCode',
                            label : i18n.t('Gloria.i18n.material.overview.header.binlocationCode'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'unitOfMeasure',
                            label : i18n.t('Gloria.i18n.material.overview.header.unitOfMeasure'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'deliveryNoteDate',
                            label : i18n.t('Gloria.i18n.material.overview.header.deliveryNoteDate'),
                            cell : 'date',
                            editable : false,
                            renderable : false,
                            formatter : BackgridDateFormatter,
							headerCell : DateHeaderCell
                        },
                        {
                            name : 'deliveryNoteNo',
                            label : i18n.t('Gloria.i18n.material.overview.header.deliveryNoteNo'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'dispatchNoteNo',
                            label : i18n.t('Gloria.i18n.material.overview.header.dispatchNoteNo'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'requestListDeliveryAddressId',
                            label : i18n.t('Gloria.i18n.material.overview.header.requestListDeliveryAddressId'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'requestListDeliveryAddressName',
                            label : i18n.t('Gloria.i18n.material.overview.header.requestListDeliveryAddressName'),
                            cell : 'string',
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'expectedDate',
                            label : i18n.t('Gloria.i18n.material.overview.header.expectedDate'),
                            cell : 'date',
                            editable : false,
                            renderable : false,
                            formatter : BackgridDateFormatter,
							headerCell : DateHeaderCell
                        },
                        {
                            name : 'materialType',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialType'),
                            cell : function(options) {
                            	if(options.model.get('materialType') == 'ADDITIONAL_USAGE') {
                            		return new AdditionalUsageStringCell(options);
                            	} else {
                            		return new Backgrid.StringCell(options);
                            	}
                    		},
                            sortable: false,
                            editable : false,
                            renderable : false,
                            headerCell : function(options){
                                options.column.type = 'select';
                                options.column.defaultData = [{
                                    id : 'ADDITIONAL,ADDITIONAL_USAGE',
                                    text : i18n.t('Gloria.i18n.reports.materialType.ADDITIONAL')
                                },{
                                    id : 'RELEASED',
                                    text : i18n.t('Gloria.i18n.reports.materialType.RELEASED')
                                },{
                                    id : 'USAGE',
                                    text : i18n.t('Gloria.i18n.reports.materialType.USAGE')
                                }];
                                return new DropdownHeaderCell(options);
                            }
                        },
                        {
                            name : 'deliveryControllerId',
                            label : i18n.t('Gloria.i18n.material.overview.header.deliveryController'),
                            cell : Backgrid.StringCell.extend({
                                formatter : {
                                    fromRaw : function(rawValue, model) {
                                        return model.get('deliveryControllerId') ? model.get('deliveryControllerId')
                                        		+ ' - ' + model.get('deliveryControllerName') : ''; 
                                    }
                                }
                            }),
                            editable : false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        }
					]
				});
				
				this.paginator = new GloriaPaginator({
					collection : this.collection,
					grid : this.gridView,
					showColumnSettings: true,
					postbackSafe : true
				});
			},

			render : function() {				
	            var $gridView = this.gridView.render().$el;
				this.$el.html($gridView);
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

	return Gloria.MaterialApp.View.MaterialLineGridView;
});
