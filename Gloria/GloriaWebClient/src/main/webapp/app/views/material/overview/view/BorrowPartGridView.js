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
		'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator,
        StringHeaderCell, DropdownHeaderCell, ClickableRow, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.BorrowPartGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.setGrid();
			},
			
			events : {
				'change .select-row-cell input' : 'handleSelectRow',
				'change .select-all-header-cell input' : 'handleSelectRow'
			},
			
			handleSelectRow : _.debounce(function(e) {
	            var selectedModels = this.gridView.getAllSelectedModels();
	            Gloria.MaterialApp.trigger('BorrowPartGrid:select', selectedModels);
	        }, 250),

	        clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			setGrid : function() {
				this.gridView = new Backgrid.Grid({
					className : 'backgrid',
					id : 'BorrowPartGrid',
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
                            name : 'changeRequestIds',
                            label : i18n.t('Gloria.i18n.material.overview.header.changeRequestId'),
                            cell :  'string',
                            sortable: false,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'requisitionReference',
                            label : i18n.t('Gloria.i18n.material.overview.header.requisitionReference'),
                            cell :  'string',
                            sortable: false,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'materialMailFormId',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialMailFormId'),
                            cell :  'string',
                            sortable: false,
                            editable : false,
                            headerCell : StringHeaderCell
                        },
                        {
                            name : 'quantity',
                            label : i18n.t('Gloria.i18n.material.overview.header.quantity'),
                            cell : 'integer',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'status',
                            label : i18n.t('Gloria.i18n.material.overview.header.status'),
                            cell : Backgrid.StringCell.extend({
								formatter : {
									fromRaw : function(rawValue) {
									    return i18n.t('Gloria.i18n.materialLineStatus.' + rawValue);
									}
								}
							}),
                            sortable: false,
                            editable : false,
                            headerCell : function(options) {
                            	options.column.type = 'select';
            					options.column.defaultData = [{
            						id : 'RECEIVED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.RECEIVED')
            					},{
            						id : 'QI_READY',
            						text : i18n.t('Gloria.i18n.materialLineStatus.QI_READY')
            					},{
            						id : 'QI_OK',
            						text : i18n.t('Gloria.i18n.materialLineStatus.QI_OK')
            					},{
            						id : 'BLOCKED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.BLOCKED')
            					},{
            						id : 'READY_TO_STORE',
            						text : i18n.t('Gloria.i18n.materialLineStatus.READY_TO_STORE')
            					},{
            						id : 'STORED',
            						text : i18n.t('Gloria.i18n.materialLineStatus.STORED')
            					}];
            					return new DropdownHeaderCell(options);
            				}
                        },
                        {
                            name : 'assignedMaterialControllerId',
                            label : i18n.t('Gloria.i18n.material.overview.header.materialController'),
                            headerCell : StringHeaderCell,
                            cell : Backgrid.StringCell.extend({
            					formatter : {
            						fromRaw : function(rawValue, model) {
            							return model.get('assignedMaterialControllerId') + ' - ' + model.get('assignedMaterialControllerName'); 
            						}
            					}
            				}),
                            sortable: false,
                            editable : false
                        }
					]
				});
				
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

	return Gloria.MaterialApp.View.BorrowPartGridView;
});
