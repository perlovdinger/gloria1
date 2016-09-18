define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'backgrid',
	    'i18next',	
	    'backgrid-select-all',
	    'backgrid-select2-cell',
	    'bootstrap',   
	    'utils/UserHelper',
	    'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, UserHelper, GridUtil) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
	
		View.MaterialLinesGridView = Marionette.View.extend({
	
	        initialize : function(options) {
	        	this.collection = options.collection; 
	            this.setGrid();
	        }, 
	       
			setGrid : function() {
				var that = this;
	            this.gridView = new Backgrid.Grid({
	            	className: 'backgrid',
	            	id : 'MaterialLinesGrid',
	                collection : that.collection,
					emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
	        	    	name : 'pPartNumber',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.partNo'),
						cell : 'string',
						editable : false,	
		                sortable: false
	        	    },{
	        	    	name : 'pPartVersion',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.version'),
						cell : 'string',
						editable : false,
		                sortable: false
	        	    }, {
	        	    	name : 'pPartName',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.partName'),
						cell : 'string',
						editable : false,	
		                sortable: false
	        	    },{
						name : 'pPartModification',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.partModification'),
						cell : 'string',
						editable : false,
		                sortable: false
					},		
					{
						name : 'unitOfMeasure',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.UoM'),
						cell : 'string',
						editable : false,
		                sortable: false
					},{
	        	    	name : 'quantity',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.pullQty'),
						cell : 'string',
						editable : false,
		                sortable: false
	        	    }]
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
	        
	        onDestroy: function() {
	            this.gridView.remove();
	        }
	    });
	});
	
    return Gloria.WarehouseApp.View.MaterialLinesGridView;
}); 
