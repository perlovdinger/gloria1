define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'bootstrap',
		'utils/backgrid/clickableRow',
		'utils/DateHelper',
		'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, Bootstrap, ClickableRow, DateHelper, GridUtil) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.TraceabilityGridView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.setGrid();
			},
			
			setGrid : function() {
				this.gridView = new Backgrid.Grid({
					id : 'TraceabilityGrid',
					className : 'backgrid',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
						{
                            name : 'loggedTime',
                            label : i18n.t('Gloria.i18n.material.details.traceability.loggedTime'),
                            cell : Backgrid.DatetimeCell.extend({
            					formatter : {
            						fromRaw : function(rawValue) {
            							return DateHelper.formatDateTimewithUTC(rawValue); //utc
            						}
            					}
            				}),
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'action',
                            label : i18n.t('Gloria.i18n.material.details.traceability.action'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'actionDetail',
                            label : i18n.t('Gloria.i18n.material.details.traceability.actionDetail'),
                            cell : Backgrid.StringCell.extend({
								render : function() {
									if(this.model.get('action') == 'Modified') {
										var linkJSON = JSON.parse(this.model.get('actionDetail'));
										this.$el.html('<a href="#procurement/overview/modification/viewDetails/' + linkJSON.procureLineId + '?type='
												+ linkJSON.modificationType + '&modificationId=' + linkJSON.modificationId
												+ '&from=materialDetailsPage">' 
												+ i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.linkToModifyDetails') + '</a>'); 
									} else {
										this.$el.html(this.model.get('actionDetail')); 
									}
									return this;
								}
							}),
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'mlQuantity',
                            label : i18n.t('Gloria.i18n.material.details.traceability.mlQuantity'),
                            cell : 'integer',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'mlStatus',
                            label : i18n.t('Gloria.i18n.material.details.traceability.mlStatus'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'orderNo',
                            label : i18n.t('Gloria.i18n.material.details.traceability.orderNo'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'userId',
                            label : i18n.t('Gloria.i18n.material.details.traceability.userId'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'userName',
                            label : i18n.t('Gloria.i18n.material.details.traceability.userName'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        }
					]
				});
			},

			render : function() {				
				var $gridView = this.gridView.render().$el;
	            this.$el.html($gridView);
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
			}
		});
	});

	return Gloria.MaterialApp.View.TraceabilityGridView;
});
