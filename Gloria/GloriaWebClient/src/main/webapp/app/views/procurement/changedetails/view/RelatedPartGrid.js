define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'backgrid',
        'i18next',
        'utils/backgrid/IntegerCell',
        'views/procurement/changedetails/view/MarkedGridCell',
        'views/procurement/changedetails/view/ActionCell',
        'bootstrap',
        'select2',
        'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, IntegerCell, MarkedGridCell, ActionCell, Bootstrap, select2, GridUtil) {

    Gloria.module('ProcurementApp.ChangeDetails.View', function(View, Gloria, Backbone, Marionette, $, _) {
    	
    	var CustomActionCell = Backgrid.Cell.extend({
			className : 'string-cell show-full-text',
			formatter : Backgrid.StringFormatter,
			render : function(option) {
				if(this.model.get('changeAction')) {
					this.$el.html(i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.actions.' + this.model.get('changeAction')));
				} else {
					this.model.set({changeAction: ''}, {silent: true});
					this.$el.html('');
				}
				return this;
			}
		});

        View.RelatedPartGridView = Marionette.LayoutView.extend({

            initialize : function(options) {
            	this.changeIdModel = options.changeIdModel;
            	this.listenTo(Gloria.ProcurementApp, 'Action:change', this.updateActions);
                this.setGrid();     
            },
			
			updateActions : function(model) {
				var allLinesWithSameProcureLineId = this.collection.where({procureLineId: model.get('procureLineId')});
				_.each(allLinesWithSameProcureLineId, function(line) {
					if(line.id != model.id) { // Except the current dropdown disable rest
						line.set('changeAction', model.get('changeAction'), {silent : true});
						$('td#changeAction_' + line.id + ' div.select2-container').select2('val', model.get('changeAction')).select2('enable', false);
					}
				});
				
				var allLinesWithSameOrderId = this.collection.where({orderId: model.get('orderId')});
				_.each(allLinesWithSameOrderId, function(line) {
					if(line.id != model.id) { // Except the current dropdown disable rest
						line.set('changeAction', model.get('changeAction'), {silent : true});
						$('td#changeAction_' + line.id + ' div.select2-container').select2('val', model.get('changeAction')).select2('enable', false);
					}
				});
			},

            setGrid : function() {
            	var that = this;
                // Initialize the grid
                this.gridView = new Backgrid.Grid({
                	id : 'RelatedPartGrid',
                    collection : this.collection,
                    emptyText : i18n.t('Gloria.i18n.general.noRow'),
                    columns : [           
                               {
                                   name : 'mark',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.mark'),
                                   cell : MarkedGridCell,
                                   editable : false,
                                   sortable: false                
                               },
                               {
                                   name : 'referenceId',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.referenceId'),
                                   cell : 'string',
                                   editable : false,
                                   sortable: false             
                               },
                               {
                                   name : 'partAffiliation',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.partAffiliation'),
                                   cell : 'string',
                                   editable : false,
                                   sortable: false             
                               },
                               {
                                   name : 'partNumber',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.partNumber'),
                                   cell : 'string',
                                   editable : false,
                                   sortable: false             
                               },
                               {
                                   name : 'partVersion',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.version'),
                                   cell : 'string',
                                   editable : false,
                                   sortable: false             
                               },
                               {
                                   name : 'partName',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.partName'),
                                   cell : 'string',
                                   editable : false,
                                   sortable: false             
                               },
                               {
                                   name : 'partModification',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.partModification'),
                                   cell : 'string',
                                   editable : false,
                                   sortable: false             
                               },
                               {
                                   name : 'quantity',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.quantity'),              
                                   cell : IntegerCell,
                                   editable : false,
                                   sortable: false                                            
                               },
                               {
                                   name : 'functionGroup',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.functionGroup'),              
                                   cell : 'string',
                                   editable : false,
                                   sortable: false                                            
                               },
                               {
                                   name : 'procureLineStatus',             
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.procureStatus'), 
                                   cell : 'string',
                                   formatter: {                    
                                       fromRaw: function (rawData, model) {
                                       	if(!rawData) return "";
                                           return i18n.t('Gloria.i18n.procurement.procureLineStatus.' + rawData.toUpperCase());
                                       }
                                   },
                                   editable : false,
                                   sortable: false             
                               },
                               {
                                   name : 'procureLineId',             
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.link'), 
                                   cell : Backgrid.UriCell.extend({
                                       className: Backgrid.UriCell.prototype.className + ' text-center',
                                       target: '_self',
                                       title: i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.link'),
                                       render: function() {
                                           if(!this.model.get('procureLineId')) return this;
                                           Backgrid.UriCell.prototype.render.apply(this, arguments);
                                           this.$('a').attr({
                                               href: '#procurement/overview/procureLineDetail/' + this.model.get('procureLineId')
                                           }).text(i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.link'));
                                           return this;
                                       }
                                   }),                
                                   editable : false,
                                   sortable: false             
                               }, {
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.linkToModifyDetails'), 
                                   cell : Backgrid.UriCell.extend({
                                       className: Backgrid.UriCell.prototype.className + ' text-center',
                                       target: '_self',
                                       title: i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.linkToModifyDetails'),
                                       render: function() {
                                           Backgrid.UriCell.prototype.render.apply(this, arguments);
                                           if(this.model.get('materialType') == 'USAGE_REPLACED') {
                                        	   this.$('a').attr({
                                                   href: '#procurement/overview/modification/viewDetails/'
                                                	   + this.model.get('procureLineId') + '?type=' + this.model.get('modificationType')
                                                	   + '&modificationId=' + this.model.get('modificationId')
                                                	   + '&from=changeDetailsPage'
                                               }).text(i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.link'));
                                           }
                                           return this;
                                       }
                                   }),                
                                   editable : false,
                                   sortable: false             
                               },{
                                   name : 'changeAction',
                                   label : i18n.t('Gloria.i18n.procurement.changeDetails.detail.grid.header.changeAction'),
                                   cell : function(options) {
										if(!options.model.get('changeAction') 
											&& (options.model.get('materialType') == 'USAGE' || options.model.get('materialType') == 'RELEASED')
											&& that.changeIdModel.get('status') != 'ACCEPTED'
											&& ((options.model.get('procureLineStatus') == 'PROCURED' 
											&& (options.model.get('procureType') == 'EXTERNAL' || options.model.get('procureType') == 'EXTERNAL_FROM_STOCK')) 
											|| (options.model.get('procureLineStatus') == 'PLACED' 
											&& (options.model.get('procureType') == 'INTERNAL' || options.model.get('procureType') == 'INTERNAL_FROM_STOCK')))) {
						                	return new ActionCell(options);
										} else {
											return new CustomActionCell(options);
										}
									},
                                   editable : false,
                                   sortable: false             
                               },]
                });             
            },

            render : function() {               
                // Render the grid
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

    return Gloria.ProcurementApp.ChangeDetails.View.RelatedPartGridView;
});