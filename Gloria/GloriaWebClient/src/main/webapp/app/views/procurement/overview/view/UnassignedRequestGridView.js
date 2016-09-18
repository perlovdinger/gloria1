define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'backgrid',
        'i18next',
        'views/common/paginator/PaginatorView',
        'utils/backgrid/stringHeaderCell',
		'utils/backgrid/dateHeaderCell',
		'utils/backgrid/dateFormatter',
		'backgrid-select-all',
		'bootstrap',
		'grid-util',
		'utils/DateHelper',
		'utils/backgrid/clickableRow',
		'utils/backgrid/dropdownHeaderCell'
],function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell, DateHeaderCell,
		BackgridDateFormatter, BackgridSelectAll, Bootstrap, GridUtil, DateHelper, ClickableRow, DropdownHeaderCell) {
	
	var columnModel = [
						{
							// Checkbox column
							name : "",
							cell : "select-row",
							headerCell : "select-all",
						},
						{
                            name : 'projectId',
                            label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.projectId'),
                            cell : 'string',
                            editable : false,
                            headerCell : StringHeaderCell
                        },
						{
							name : 'referenceGroup',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.buildSeriesName'),
							cell : 'string',
							editable : false,
							headerCell : StringHeaderCell
						},											
						{
							name : 'referenceId',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.referenceIds'),
							cell : 'string',
							editable : false,
							headerCell : StringHeaderCell
						},
						{
							name : 'buildName',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.phase'),
							cell : 'string',
							editable : false,
							sortable: false
						},
						{
							name : 'mtrlRequestVersion',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.changeRequestId'),
							cell : 'string',
							editable : false,
							headerCell : function(options) {                 
			                    options.tooltip = {
			                            'tooltipText': i18n.t('Gloria.i18n.procurement.unassignedRequest.header.changeRequestIdTooltip'),
			                            'displayText': i18n.t('Gloria.i18n.procurement.unassignedRequest.header.changeRequestId')
			                    };                       
			                    return new StringHeaderCell(options);
			                }
						},						
						{
							name : 'outboundLocationId',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.buildSiteId'),
							cell : 'string',
							editable : false,
							headerCell : StringHeaderCell
						},
						{
							name : 'requesterUserId',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.requesterName'),
							cell : Backgrid.StringCell.extend({
			                    render: function() {
			                        var requesterUserId = this.model.get('requesterUserId') || '';
			                        var requesterName = this.model.get('requesterName') || '';
			                        var value = requesterUserId + " " + requesterName;
			                        this.$el.html(value);                           
			                        return this;
			                    }
			                }),
							editable : false,
							headerCell : StringHeaderCell
						},
						{
							name : 'receivedDateTime',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.receivedDateTime'),
							cell : Backgrid.DatetimeCell.extend({
	            					formatter : {
	            						fromRaw : function(rawValue) {
	            							return DateHelper.formatDate(rawValue);
	            						}
	            					}
	            				}),
							editable : false,
							headerCell : DateHeaderCell						
						},
						{
							name : 'outboundStartDate',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.buildSeriesStartDate'),
							cell : 'date',
							editable : false,
							formatter : BackgridDateFormatter,
							headerCell : DateHeaderCell
						},	
						{
							name : 'assignedMaterialControllerId',
							label : i18n.t('Gloria.i18n.procurement.unassignedRequest.header.assignedMaterialController'),
							cell : Backgrid.StringCell.extend({
			                    render: function() {
			                        var assignedMaterialControllerId = this.model.get('assignedMaterialControllerId') || '';
			                        var assignedMaterialControllerName = this.model.get('assignedMaterialControllerName') || '';
			                        var assignedMaterialControllerTeam = this.model.get('assignedMaterialControllerTeam') ? " (" + this.model.get('assignedMaterialControllerTeam') + ")" : '';
			                        var value = assignedMaterialControllerId + " " + assignedMaterialControllerName + assignedMaterialControllerTeam;
			                        this.$el.html(value);                           
			                        return this;
			                    }
			                }),
							editable : false,
							headerCell : StringHeaderCell
						}
				];
	
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.UnassignedRequestGridView = Marionette.LayoutView.extend({
			
			initialize : function(options) {	
				this.collection = options.collection;
				this.listenTo(Gloria.ProcurementApp, 'UnassignedRequestGrid:clearSelectedModels', this.clearSelectedModels);
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.setGrid();
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},
			
			setGrid : function() {
	            // Initialize the grid
	            this.gridView = new Backgrid.Grid({
	            	id : 'inbox',
	                collection : this.collection,
					emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : columnModel
	            });
	            
				// Initialize the paginator
				this.paginator = new GloriaPaginator({
					collection : this.collection,
					grid : this.gridView,
					postbackSafe : true
				});
			},
			
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			collectionEvents: {
				'add remove backgrid:selected': 'handleSelectRow'
			},
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
            handleSelectRow : _.debounce(function(model, selected) {
                var selectedModelsIds = this.gridView.getAllSelectedModelIds();
                var selectedModels = this.gridView.getSelectedModels();
                Gloria.ProcurementApp.trigger('procureRequestLineGrid:select', selectedModelsIds, selectedModels);
            }, 200),

			render : function() {
				if (this.collection.length == 0) {
					Gloria.ProcurementApp.trigger('disableAssign');
				}				
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

	return Gloria.ProcurementApp.View.UnassignedRequestGridView;
});