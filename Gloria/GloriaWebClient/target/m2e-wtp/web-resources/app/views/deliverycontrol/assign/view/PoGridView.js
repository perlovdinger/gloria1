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
		'utils/backgrid/clickableRow',
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'utils/UserHelper',
		'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell,
		DropdownHeaderCell, ClickableRow, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, UserHelper, GridUtil) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PoGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.filter = (options.filter && options.filter.toUpperCase());
				this.dcList = UserHelper.getInstance().getDCList(); // This restricts fetching dc list by grid more than once!
				this.setGrid();
				this.listenTo(Gloria.DeliveryControlApp, 'PoGrid:clearselection', this.clearSelectedModels);
				this.listenTo(this.collection, 'backgrid:selected', this.handleSelectRow);
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
			getDCIdName : function(dcId) {
				var formattedDC = '';
				_.each(this.dcList, function(item, index) {
					if (item.id === dcId) {
						formattedDC =  item.id + ' - ' + item.firstName + ' ' + item.lastName;
					}
				});
				return formattedDC;
			},
			
            handleSelectRow : _.debounce(function(model, selected) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.DeliveryControlApp.trigger('PoGrid:select', selectedModels);
			}, 200),
            
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			setGrid : function() {
				var that = this;
				this.gridView = new Backgrid.Grid({
					id : 'PoGrid',
					className : 'backgrid procurementOverview-grid-main',
					row : ClickableRow,
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
                                    name : 'internalExternal',
                                    label : i18n.t('Gloria.i18n.procurement.overviewModule.header.source'),
                                    cell : Backgrid.StringCell.extend({
                                        formatter : {
                                            fromRaw : function(rawValue) {
                                                if(rawValue) {
                                                    return i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.' + rawValue);
                                                }
                                                return rawValue;
                                            }
                                        }
                                    }),
                                    editable : false,                                                                     
                                    headerCell : function(options) {
                                        options.column.type = 'select';
                                        options.column.defaultData = [{
                                            id : 'INTERNAL',
                                            text : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.INTERNAL')
                                        },{
                                            id : 'EXTERNAL',
                                            text : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.EXTERNAL')
                                        }];
                                        return new DropdownHeaderCell(options);
                                    }
                                },
								{
									name : 'orderNo',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.orderNo'),
									cell : 'string',
									editable : false,
									headerCell : function(options) {
					                    options.tooltip = {
					                        'tooltipText': i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.orderNoTooltip'),
					                        'displayText': i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.orderNo')
					                    };                    
					                    return new StringHeaderCell(options);
					                }
								},
								{
									name : 'buyerId',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.buyerCode'),
									cell : Backgrid.StringCell.extend({
                                        formatter : {
                                            fromRaw : function(rawValue, model) {
                                            	var buyerId = model.get('buyerId') || '';
                                            	var buyerName = model.get('buyerName') || '';
                                                return buyerId ? buyerId + (buyerId && buyerName ? '/' : '') + buyerName : buyerName;
                                            }
                                        }
                                    }),
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'supplierId',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.supplierId'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},		
								{
									name : 'supplierName',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.supplierName'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'suffix',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.suffix'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'projectId',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.projectNumberFirst'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'reference',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.reference'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'partNumber',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.partNumber'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'partVersion',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.partVersion'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'partName',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.partName'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'partModification',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.partModification'),
									cell : 'string',
									editable : false,
									headerCell : StringHeaderCell
								},
								{
									name : 'deliveryControllerUserId',
									label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.order.deliveryControllerUserId'),
									cell : Backgrid.StringCell.extend({
										formatter : {
											fromRaw : function(rawValue) {
												return that.getDCIdName(rawValue);
											}
										}
									}),
									editable : false,
									headerCell : StringHeaderCell
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

	return Gloria.DeliveryControlApp.View.PoGridView;
});
