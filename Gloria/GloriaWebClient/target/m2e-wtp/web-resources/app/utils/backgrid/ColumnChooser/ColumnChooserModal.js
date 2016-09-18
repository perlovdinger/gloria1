define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'utils/backgrid/ColumnChooser/ColumnHeaderList',
        'hbs!utils/backgrid/ColumnChooser/ColumnChooserModal'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, ColumnHeaderList, compiledTemplate) {
		
		var ColumnChooserModal = Marionette.LayoutView.extend({
	    	
			className : 'modal',

			id : 'columnChooserModal',
			
			regions: {
				visibleColumnList: 'div#visibleColumnList',
				hiddenColumnList: 'div#hiddenColumnList'
			},
			
	    	events : {
	            'click #save' : 'handleSaveClick',
	            'click #cancel' : 'handleCancelClick',
	            'hidden.bs.modal': 'onHide'
	        },
	        
	        initialize : function(options) {
	        	this.grid = options.grid;
	        	this.gridId = options.gridId;
	        	return Marionette.LayoutView.prototype.initialize.apply(this, arguments);
	        },
	        
	        handleSaveClick : function(e) {
	        	e.preventDefault();
	        	Gloria.trigger('columnChooser:sync', this.gridId, this.collection);
	        },
	        
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},
			
			onHide: function() {
				this.trigger('hide');
				Gloria.trigger('reset:modellayout');
			},
			
			getTemplate: function() {
				return compiledTemplate;
			},
	        
			onRender : function() {				
				this.visibleColumnList.show(new ColumnHeaderList({
					grid: this.grid,
					collection: this.collection,
					filter: function (child, index, collection) {
						var choosable = child.get('columnChooser') ? child.get('columnChooser').choosable !== false : true;
					    return child.get('renderable') && choosable;
					},
					sortable: true
				}));
				this.hiddenColumnList.show(new ColumnHeaderList({
					grid: this.grid,
					collection: this.collection,
					filter: function (child, index, collection) {
						var choosable = child.get('columnChooser') ? child.get('columnChooser').choosable !== false : true;
				        return !child.get('renderable') && choosable;
					}
				}));
				
				this.$el.modal({
					show : false
				});
			},
			
			onShow : function() {
				this.$el.modal('show');
			},
			
			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
			}
	    });
	
    return ColumnChooserModal;
});
