define(['app',
        'jquery',
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
	    'bootstrap',
	    'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator,
		StringHeaderCell, ClickableRow, BackgridSelectAll, Bootstrap, GridUtil) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		var InspectionCell = Backgrid.Cell.extend({
			render : function() {
				this.$el.html(i18n.t('Gloria.i18n.warehouse.qisettings.inspectionOptions.' + this.model.get('mandatory')));
				return this;
			}
		});
		
		View.QISettingsGridView = Marionette.View.extend({
	
			initialize : function(options) {
				this.module = options.module;
				this.collection = options.collection;
	            this.listenTo(this.collection, 'add remove backgrid:selected', this.handleSelectRow);
	            this.listenTo(Gloria.WarehouseApp, 'QISettingsGrid:clearSelectedModels', this.clearSelectedModels);
	            this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
	        },	        

	        handleSelectRow : _.debounce(function(e) {
	        	var selectedModels = this.gridView.getSelectedModels();
	            Gloria.WarehouseApp.trigger('QISettings:select', selectedModels);
	        }, 200),
	
	        clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
	        render : function() {
	            this.gridView = new Backgrid.Grid({
	            	id : 'QISettings' + this.module,
	                row : ClickableRow,
	                collection : this.collection,
	                emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
		                    name: "",
		                    cell: "select-row",
		                    headerCell: "select-all"
		                }, {
		                    name : 'partNumber',
		                    label : i18n.t('Gloria.i18n.warehouse.qisettings.header.partNumber'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    renderable : this.module == 'part',
		                    headerCell : function(options) {
		                        options.tooltip = {
		                            'tooltipText': i18n.t('Gloria.i18n.warehouse.qisettings.header.partNumberTooltip'),
		                            'displayText': i18n.t('Gloria.i18n.warehouse.qisettings.header.partNumber')
		                        };    
		                        options.column.isSearchable = false;
		                        return new StringHeaderCell(options);
		                    }
		                }, {
		                    name : 'partName',
		                    label : i18n.t('Gloria.i18n.warehouse.qisettings.header.partName'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    renderable : this.module == 'part'
		                }, {
		                    name : 'project',
		                    label : i18n.t('Gloria.i18n.warehouse.qisettings.header.project'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    renderable : this.module == 'project'
		                }, {
		                    name : 'supplier',
		                    label : i18n.t('Gloria.i18n.warehouse.qisettings.header.supplier'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    renderable : this.module == 'supplier'
		                }, {
		                    name : 'mandatory',
		                    label : i18n.t('Gloria.i18n.warehouse.qisettings.header.mandatory'),
		                    cell : InspectionCell,
		                    sortable : false,
		                    editable : false
		                }
		             ]
	            });
	            // Initialize the paginator
				this.paginator = new GloriaPaginator({
					collection : this.collection,
					grid : this.gridView,
					postbackSafe : true
				});
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
	        
	        onDestroy: function() {
	            this.gridView.remove();
	            this.paginator.remove();
	        }
	    });
	});

    return Gloria.WarehouseApp.View.QISettingsGridView;
});
