define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'backgrid',
        'i18next',
        'utils/backgrid/stringHeaderCell',
        'utils/backgrid/clickableRow',
        'utils/backgrid/dateFormatter',
        'utils/backgrid/IntegerCell',
        'backgrid-select-all',
        'backgrid-select2-cell',
        'bootstrap',
        'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, StringHeaderCell,
        ClickableRow, BackgridDateFormatter, IntegerCell, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil) {

    Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

        var CustomStringCell = Backgrid.Cell.extend({
            className : 'integer-cell additionalQty',
            formatter : Backgrid.StringFormatter
        });
        
        View.MaterialRequestsGridView = Marionette.LayoutView.extend({

            initialize : function(options) {
                this.collection = options.collection;
                this.materialHeaderResponse=options.materialHeaderStatus;
                this.setGrid();
                this.listenTo(this.collection, 'backgrid:error', this.showErrors);
            },
            
            showErrors: function(model, column, val) {
                Gloria.trigger('showAppMessageView', {
                    type : 'error',
                    title : i18n.t('errormessages:general.title'),
                    message : new Array({
                        message : i18n.t('errormessages:errors.GLO_ERR_062')
                    })
                });
            },
            
            events : {
                'change .select-row-cell input' : 'handleSelectRow',
                'change .select-all-header-cell input' : 'handleSelectRow'
            },
            
            handleSelectRow : function(e) {
                var selectedModels = this.gridView.getSelectedModels(); 
                if($('.backgrid').find('.error').length == 0){
                    Gloria.MaterialApp.trigger('MaterialRequestList:select', selectedModels);
                }
            },

            setGrid : function() {
                that=this;
                this.gridView = new Backgrid.Grid({
                    className : 'backgrid MaterialRequests-grid-main',
                    id : 'MaterialRequestsGrid',
                    row : ClickableRow,
                    collection : this.collection,
                    emptyText : i18n.t('Gloria.i18n.general.noRow'),
                    columns : [
                        {
                            name : "",
                            cell : "select-row",
                            headerCell : "select-all",
                        },
                        {
                            name : 'projectId',
                            label : i18n.t('Gloria.i18n.material.overview.header.projectId'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'referenceGroup',
                            label : i18n.t('Gloria.i18n.material.requests.header.referenceGroup'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'referenceId',
                            label : i18n.t('Gloria.i18n.material.overview.header.referenceId'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'pPartNumber',
                            label : i18n.t('Gloria.i18n.material.overview.header.pPartNumber'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'pPartVersion',
                            label : i18n.t('Gloria.i18n.material.overview.header.pPartVersion'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'pPartName',
                            label : i18n.t('Gloria.i18n.material.overview.header.pPartName'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'buildId',
                            label : i18n.t('Gloria.i18n.material.overview.header.phase'),
                            cell : 'string',
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'changeRequestIds',
                            label : i18n.t('Gloria.i18n.material.overview.header.changeRequestId'),
                            cell :  'string',
                            sortable: false,
                            editable : false,
                            headerCell : function(options) {                 
                                options.tooltip = {
                                        'tooltipText': i18n.t('Gloria.i18n.material.overview.header.changeRequestIdTooltip'),
                                        'displayText': i18n.t('Gloria.i18n.material.overview.header.changeRequestId')
                                };                                 
                                options.column.isSearchable = false;
                                return new StringHeaderCell(options);
                            }
                        },{
                            name : 'outboundStartDate',
                            label : i18n.t('Gloria.i18n.material.overview.header.outBoundStartDate'),
                            cell : 'date',
                            sortable: false,
                            editable : false,
                            formatter : BackgridDateFormatter
                        },
                        {
                            name : 'quantity',
                            label : i18n.t('Gloria.i18n.material.overview.header.quantity'),
                            cell : function(options) {
                                if(options.model.get('materialType') == 'ADDITIONAL') {
                                    return new CustomStringCell(options);
                                } else {
                                    return new Backgrid.IntegerCell(options);
                                }
                            },
                            sortable: false,
                            editable : false
                        },
                        {
                            name : 'possiblePickQuantity',
                            label : i18n.t('Gloria.i18n.material.requests.header.quantity'),
                            cell : IntegerCell,
                            formatter : {
                                fromRaw: function (rawData, model) {
                                    return rawData;
                                },
                                toRaw : function(formattedData, model) {                                    
                                    var precision = (formattedData + "").split(".")[1] && (formattedData + "").split(".")[1].length ?  true : false;                                    
                                    return (Number(formattedData) > 0) && !precision && (Number(formattedData) <= Number(model.get('quantity'))) ? formattedData : undefined;
                                }
                            },
                            sortable: false,
                            editable: (that.materialHeaderResponse == 'SENT') ? false:true
                        }
                    ]
                });
            },

            render : function() {               
                var $gridView = this.gridView.render().$el;
                this.$el.append($gridView);
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

    return Gloria.MaterialApp.View.MaterialRequestsGridView;
});
