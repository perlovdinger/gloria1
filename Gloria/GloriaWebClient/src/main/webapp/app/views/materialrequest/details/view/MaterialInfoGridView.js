define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'views/common/paginator/PaginatorView',
		'utils/backgrid/dropdownHeaderCell',
		'utils/backgrid/stringHeaderCell',
		'utils/backgrid/integerHeaderCell',
		'utils/backgrid/numberHeaderCell',
		'utils/backgrid/IntegerCell',
		'utils/backgrid/clickableRow',
		'utils/handlebars/PartAffiliationSelectorHelper',
	    'views/materialrequest/details/view/MarkedGridCell',
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, DropdownHeaderCell, StringHeaderCell,
		IntegerHeaderCell, NumberHeaderCell, IntegerCell, ClickableRow, partAffiliationSelectorHelper, 
		MarkedGridCell, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil) {

	Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MaterialInfoGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			    this.listenTo(this.collection, 'add remove change backgrid:selected', this.handleSelectRow);
			    this.listenTo(Gloria.MaterialRequestApp, 'MaterialInfo:clearSelectedModels', this.clearSelectedModels);
			    this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.setGrid();
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
			isRenderable: function() {
				var args = Array.prototype.slice.call(arguments);
				var renderable = false;
				_.each(args, function(item) {					
					if(this.model && this.model.get('type') && this.model.get('type').toUpperCase() == item.toUpperCase()) {
						renderable = true;
					}
				}, this);				
				return renderable;
			},

			/**
			 * Called when the user clicks the checkbox on one line
			 * Using debounce to prevent executing this method more than once when user select all.
			 */
			handleSelectRow : _.debounce(function(model, selected) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.MaterialRequestApp.trigger('materialInfoGrid:select', selectedModels);
			}, 200),

			setGrid : function() {
				var that = this;
				// Initialize the grid
				this.gridView = new Backgrid.Grid({
					id : 'MaterialInfo' + that.model.get('type').toUpperCase() + 'Grid',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
						{
							// Checkbox column
							name : "",
							cell : "select-row",
							headerCell : "select-all",
							renderable : this.model.get('materialRequestVersionStatus') != 'SENT_ACCEPTED'
											&& this.model.get('materialRequestVersionStatus') != 'SENT_WAIT'
											&& this.model.get('materialRequestVersionStatus') != 'SENT_REJECTED'
						},
						{
			                name : '',
			                label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.mark'),
			                cell : MarkedGridCell,
			                editable : false,
			                sortable: false,
			                renderable: this.model.get('mtrlRequestVersion') != undefined
			                				&& this.model.get('mtrlRequestVersion').split('V1')[1] != ''
			            },
			            {
							name : 'name',
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.testObject'),
							cell : 'string',
							editable : false,
							sortable: !that.model.isNew(),
							headerCell : that.model.isNew() ? null : StringHeaderCell,							
			                renderable: this.isRenderable('MULTIPLE')
						},
						{
							name : 'partAffiliation',
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.partAffiliation'),
							cell : 'string',
							editable : false,
							sortable: !that.model.isNew(),
							headerCell : that.model.isNew() ? null : StringHeaderCell,
							renderable: this.isRenderable('SINGLE', 'MULTIPLE', 'FOR_STOCK')
						},
						{
							name : 'partNumber',
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.partNumber'),
							cell : 'string',
							editable : false,
							sortable: !that.model.isNew(),
							headerCell : function(options) {                 
				                options.tooltip = {
				                        'tooltipText': i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.partNumberTooltip'),
				                        'displayText': i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.partNumber')
				                };
				                if(that.model.isNew()) options.column.isSearchable = false;
				                return new StringHeaderCell(options);
				            },
							renderable: this.isRenderable('SINGLE', 'MULTIPLE', 'FOR_STOCK')
						},
						{
							name : 'partVersion',
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.partVersion'),
							cell : 'string',
							editable : false,
							sortable: !that.model.isNew(),
							headerCell : that.model.isNew() ? null : StringHeaderCell,
							renderable: this.isRenderable('SINGLE', 'MULTIPLE', 'FOR_STOCK')
						},
						{
							name : 'partName',
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.partName'),
							cell : 'string',
							editable : false,
							sortable: !that.model.isNew(),
							headerCell : that.model.isNew() ? null : StringHeaderCell,
							renderable: this.isRenderable('SINGLE', 'MULTIPLE', 'FOR_STOCK')
						},						
						{
							name : 'quantity',
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.quantity'),				
							cell : IntegerCell,
							editable : false,
							sortable: !that.model.isNew(),
							renderable: this.isRenderable('SINGLE', 'MULTIPLE', 'FOR_STOCK')
						},
						{
							name : 'unitOfMeasure',				
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.unitOfMeasure'),	
							cell : 'string',
							editable : false,
							sortable: !that.model.isNew(),
							headerCell : that.model.isNew() ? null : StringHeaderCell,
							renderable: this.isRenderable('SINGLE', 'MULTIPLE', 'FOR_STOCK')
						},
						{
							name : 'partModification',
							cell : 'string',
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.partModification'),				
							editable : false,
							sortable: !that.model.isNew(),
							headerCell : that.model.isNew() ? null : StringHeaderCell,
							renderable: this.isRenderable('SINGLE', 'FOR_STOCK')
						},
						{
							name : 'functionGroup',
							label : i18n.t('Gloria.i18n.materialrequest.details.materialInformation.header.functionGroup'),
							cell : 'string',
							editable : false,	
							sortable: !that.model.isNew(),
							headerCell : that.model.isNew() ? null : StringHeaderCell,
							renderable: this.isRenderable('SINGLE', 'FOR_STOCK')
						}]
				});
				if(!that.model.isNew()) {
					// Initialize the paginator
					this.paginator = new GloriaPaginator({
						collection : that.collection,
						grid : this.gridView,
						postbackSafe : true
					});
				}				
			},

			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			render : function() {				
				// Render the grid
	            var $gridView = this.gridView.render().$el;
				this.$el.html($gridView);
				// Render the paginator
				this.paginator && $gridView.after(this.paginator.render().$el);
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
				this.paginator && this.paginator.remove();
			}
		});
	});

	return Gloria.MaterialRequestApp.View.MaterialInfoGridView;
});