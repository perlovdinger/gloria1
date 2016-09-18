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
		'utils/backgrid/dateHeaderCell',
		'utils/backgrid/dateFormatter',
		'utils/backgrid/clickableRow',
		'backgrid-select-all',
		'backgrid-select2-cell',
		'bootstrap',
		'grid-util',
		'utils/DateHelper',
		'views/procurement/helper/ProcureTeamMembersSelectorHelper'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell, DropdownHeaderCell,
			DateHeaderCell, BackgridDateFormatter, ClickableRow, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil, DateHelper, ProcureTeamMembersSelectorHelper) {

	var columnModel = [{
			// Checkbox column
			name : '',
			cell : 'select-row',
			headerCell : 'select-all',
		},
		{
			name : 'projectId',
			label : i18n.t('Gloria.i18n.materialrequest.overview.header.project'),
			cell : 'string',
			editable : false,
			headerCell : StringHeaderCell
		},
		{
			name : 'name',
			label : i18n.t('Gloria.i18n.materialrequest.overview.header.testObject'),
			cell : 'string',
			editable : false,
			headerCell : StringHeaderCell
		},
		{
			name : 'title',
			label : i18n.t('Gloria.i18n.materialrequest.overview.header.title'),
			cell : 'string',
			editable : false,
			headerCell : StringHeaderCell
		},
		{
			name : 'mtrlRequestVersion',
			label : i18n.t('Gloria.i18n.materialrequest.overview.header.mtrID'),
			cell : 'string',
			editable : false,
			headerCell : function(options) {                 
                options.tooltip = {
                        'tooltipText': i18n.t('Gloria.i18n.materialrequest.overview.header.mtrIDTooltip'),
                        'displayText': i18n.t('Gloria.i18n.materialrequest.overview.header.mtrID')
                };                       
                return new StringHeaderCell(options);
            }
		},
		{
			name : 'materialRequestVersionStatus',
			label : i18n.t('Gloria.i18n.materialrequest.overview.header.status'),
			cell : Backgrid.StringCell.extend({
				render : function() {
					var status = this.model.get('materialRequestVersionStatus');
					this.$el.html(i18n.t('Gloria.i18n.materialrequest.overview.status.' + status));
					return this;
				}
			}),
			editable : false,
			headerCell : function(options) {
				options.column.type = 'select'; // Bootstrap select dropdown not select2 dropdown!!!
				options.column.defaultData = [
					{
						id : 'CREATED,UPDATED',
						text : i18n.t('Gloria.i18n.materialrequest.overview.status.IN_WORK')
					},
					{
						id : 'SENT_ACCEPTED',
						text : i18n.t('Gloria.i18n.materialrequest.overview.status.SENT_ACCEPTED')
					},
					{
						id : 'SENT_REJECTED',
						text : i18n.t('Gloria.i18n.materialrequest.overview.status.SENT_REJECTED')
					},
					{
						id : 'SENT_WAIT',
						text : i18n.t('Gloria.i18n.materialrequest.overview.status.SENT_WAIT')
					},
					{
						id : 'CANCEL_WAIT',
						text : i18n.t('Gloria.i18n.materialrequest.overview.status.CANCEL_WAIT')
					},
					{
						id : 'CANCEL_REJECTED',
						text : i18n.t('Gloria.i18n.materialrequest.overview.status.CANCEL_REJECTED')
					},
					{
						id : 'CANCELLED',
						text : i18n.t('Gloria.i18n.materialrequest.overview.status.CANCELLED')
					} ];
		
				return new DropdownHeaderCell(options);
			}
		},
		{
			name : 'materialRequestVersionStatusDate',
			label : i18n.t('Gloria.i18n.materialrequest.overview.header.statusDate'),
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
			name : 'contactPersonName',
			label : i18n.t('Gloria.i18n.materialrequest.overview.header.contactPerson'),
			cell : Backgrid.StringCell.extend({
				render : function() {
					var cid = this.model.get('contactPersonId') || '';
					var cname = this.model.get('contactPersonName') || '';
					var value = cid + ' ' + cname;
					this.$el.html(value);
					return this;
				}
			}),
			editable : false,
			headerCell : StringHeaderCell
		},
		{
			name : 'requesterId',
			label : i18n.t('Gloria.i18n.materialrequest.overview.header.requester'),
			cell : Backgrid.StringCell.extend({
				render : function() {
					var cid = this.model.get('requesterId') || '';
					var cname = this.model.get('requesterName')	|| '';
					var value = cid + ' - ' + cname;
					this.$el.html(value);
					return this;
				}
			}),
			editable : false,
			// headerCell : StringHeaderCell
			headerCell : function(options) {
				var data = ProcureTeamMembersSelectorHelper.constructProcureTeamMembersListFlat();				
				options.column.type = 'select'; // Bootstrap select dropdown not select2 dropdown!!!
				options.column.defaultData = _.map(data, function(value, key) {
					if (value && value.id) {
						return {
							id : value.id,
							text : value.id + ' - ' + value.firstName + ' ' + value.lastName
						};
					} else {
						return;
					}
				});
				return new DropdownHeaderCell(options);
			}
	} ];

	Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.OverviewGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
				this.listenTo(this.collection, 'backgrid:selected', this.handleSelectRow);
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

			events : {
				'rowdoubleclicked table.backgrid tr' : 'rowDoubleClick'
			},

			rowDoubleClick : function(e, model) {
				Gloria.MaterialRequestApp.trigger('MaterialRequestOverview:show', model.id);
			},
			
			handleSelectRow : _.debounce(function(model, selected) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.MaterialRequestApp.trigger('MaterialRequestOverview:select', selectedModels);
			}, 200),
			
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},

			setGrid : function() {
				var that = this;
				// Initialize the grid
				this.gridView = new Backgrid.Grid({
					id : that.module,
					row : ClickableRow,
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

	return Gloria.MaterialRequestApp.View.OverviewGridView;
});
