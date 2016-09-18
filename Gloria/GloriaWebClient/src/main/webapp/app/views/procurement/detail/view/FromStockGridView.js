define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'views/common/paginator/PaginatorView',
		'utils/backgrid/stringHeaderCell',
		'utils/backgrid/clickableRow',		
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'grid-util',
		'views/procurement/detail/view/highlightRow'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell,
		ClickableRow, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil, HighlightableRow) {

	var columnModel = [
			{
				// Checkbox column
				name : "",
				cell : "select-row",
				headerCell : "select-all",
			},
		/*	{
				name : 'countryCode',
				label : i18n.t('Gloria.i18n.procurement.procureDetail.header.countryCode'),
				cell : 'string',
				editable : false,				
				headerCell : StringHeaderCell,
				sortable: false
			},*/	
			 {
                name : 'materialType',
                label : i18n.t('Gloria.i18n.procurement.procureDetail.header.materialType'),
                cell : Backgrid.StringCell.extend({
                    formatter : {
                        fromRaw : function(rawValue, model) {
                            if(model.get('materialType') == 'ADDITIONAL_USAGE') {
                                return "ADDITIONAL";
                            } else {
                                return model.get('materialType');
                            }
                        }
                    }
                }),
                editable : false,               
                headerCell : StringHeaderCell,
                sortable: true
            },
            {
                name : 'projectId',
                label : i18n.t('Gloria.i18n.procurement.procureDetail.header.projectId'),
                cell : 'string',
                editable : false,               
                headerCell : StringHeaderCell,
                sortable: false
            },
            {
                name : 'referenceId',
                label : i18n.t('Gloria.i18n.procurement.procureDetail.header.testObject'),
                cell : 'string',
                editable : false,               
                headerCell : StringHeaderCell,
                sortable: false
            },
            {
                name : 'mtrlRequestVersion',
                label : i18n.t('Gloria.i18n.procurement.procureDetail.header.mtrId'),
                cell : 'string',
                editable : false,               
                headerCell : StringHeaderCell,
                sortable: false
            },
            {
                name : 'quantity',
                label : i18n.t('Gloria.i18n.procurement.procureDetail.header.quantity'),
                cell : 'number',
                editable : false,               
                sortable: false
            },
			{
    	    	name : 'siteId',
				label : i18n.t('Gloria.i18n.procurement.procureDetail.header.siteId'),
				cell : 'string',
				editable : false,				
				headerCell : StringHeaderCell,
				sortable: false
    	    },
			{
				name : 'assignedMaterialController',
				label : i18n.t('Gloria.i18n.procurement.procureDetail.header.assignedMaterialController'),
				cell : 'string',
				editable : false,				
				headerCell : StringHeaderCell,
				sortable: false
			}];

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.FromStockGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
			    this.procureLineModel = options.procureLineModel;
				this.collection = options.collection;
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.setGrid(this.procureLineModel); 
			},

			events : {
				'change .select-row-cell input' : 'handleSelectRow',
				'change .select-all-header-cell input' : 'handleSelectRow'
			},

			handleSelectRow : function(e, model) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.ProcurementApp.trigger('FromStockGrid:select', selectedModels, this.procureLineModel);
			},
			
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},

			setGrid : function(procureLineModel) {
            var procureLineProject = procureLineModel.get('projectId'); 
				// Initialize the grid
				this.gridView = new Backgrid.Grid({
					className : 'backgrid',
					id : 'FromStockGrid',
                    row : HighlightableRow.extend({ /* for highlighting on condition */
                                highlightCssClass : 'background-orange',
                                conditionCallback : function() {
                                    if (this.model.get('materialType') == 'USAGE'
                                            || ((this.model.get('materialType') == 'ADDITIONAL' || this.model.get('materialType') == 'ADDITIONAL_USAGE') 
                                                        && this.model.get('projectId') != procureLineProject)) {
                                        return "background-orange";
                                    } else {
                                        return "background-white";
                                    }
                                },
                                
                            }), 
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

	return Gloria.ProcurementApp.View.FromStockGridView;
});