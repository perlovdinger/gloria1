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
	    'backgrid-select-all',
	    'backgrid-select2-cell',
	    'bootstrap',   
	    'utils/UserHelper',
	    'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator,
		StringHeaderCell, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, UserHelper, GridUtil) {
	
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
	
		View.SupplierProjectGridView = Marionette.View.extend({
	
	        initialize : function(options) {
	        	this.module = options.module;
	        	this.collection = options.collection;
	        	this.team = options.team;
	        	this.followUpType = options.filter;
	            this.setGrid();
	            this.dcList = UserHelper.getInstance().getDCList(); // This restricts fetching dc list by grid more than once!
	            this.listenTo(Gloria.DeliveryControlApp, 'SupplierProjectGrid:clearselection', this.clearSelectedModels);
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
				Gloria.DeliveryControlApp.trigger('AssignDCGrid:select', selectedModels);
			}, 200),
            
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
	       
			setGrid : function() {
				var that = this;
	            this.gridView = new Backgrid.Grid({
	            	id : that.module + 'Grid',
	                collection : that.collection,
					emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
	        	    	// Checkbox column
	        	        name: "",
	        	        cell: "select-row",
	        	        headerCell: "select-all",
	        	    }, {
	        	    	name : 'supplierId',
						label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.supplier.supplierId'),
						cell : 'string',
						editable : false,
						renderable : that.followUpType == 'supplier',
						headerCell : StringHeaderCell
	        	    }, {
	        	    	name : 'projectId',
						label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.project.projectId'),
						cell : 'string',
						editable : false,
						renderable : function() {
							return that.followUpType == 'project';
						},
						headerCell : StringHeaderCell
	        	    }, {
	        	    	name : 'suffix',
						label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.supplier.suffix'),
						cell : 'string',
						editable : false,
						renderable : that.followUpType == 'supplier',
						headerCell : StringHeaderCell
	        	    }, {
						name : 'name',
						label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.supplier.name'),
						cell : Backgrid.Cell.extend({
					        render: function() {
					            this.$el.text(that.team.get('name'));
					            return this;
					        }
					    }),
						editable : false,
						headerCell : StringHeaderCell
					}, {
						name : 'deliveryControllerUserId',
						label : i18n.t('Gloria.i18n.deliverycontrol.assignOrReassign.header.supplier.deliveryControllerUserId'),
						cell : Backgrid.StringCell.extend({
						formatter : {
                                    fromRaw : function(rawValue) {
                                    return that.getDCIdName(rawValue); }
                                    }
						}),
						editable : false,
						headerCell : StringHeaderCell
					}]
	            });
	            
				// Initialize the paginator
	            this.paginator = new GloriaPaginator({
					collection : that.collection,
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
	
    return Gloria.DeliveryControlApp.View.SupplierProjectGridView;
}); 
