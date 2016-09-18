define(['app',
		'jquery',
		'i18next',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'utils/dialog/dialog',
	    'hbs!views/warehouse/inventory/view/inventory'
], function(Gloria, $, i18n, _, Handlebars, Backbone, Marionette, Dialog, compiledTemplate) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.InventoryView = Marionette.LayoutView.extend({
    	
			initialize : function(options) {
				this.listenTo(Gloria.WarehouseApp, 'Inventory:select', this.gridClickHandler);
			},
			
			regions: {
                moduleInfo : '#moduleInfo',
                gridInfo : '#gridInfo'
            },
			
            events : {
            	'click #clear-filter' : 'clearFilter',
            	'click #adjustQty-button' : 'handleAdjustQtyClick',
                'click #move-button' : 'handleMovePartClick',
				'click #printPL' : 'printPartLabel',
				'click #printBL' : 'printBinLocation',
				'click #exportInventory-button' : 'exportInventory'
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
			
			gridClickHandler : function(selectedModels) {
				this.selectedModels = selectedModels;
				// Adjust Quantity
				if (selectedModels && selectedModels.length == 1) {
	            	this.$('#adjustQty-button').removeAttr('disabled');
	            } else {
	            	this.$('#adjustQty-button').attr('disabled', true);
	            }
				// Move
	            if (selectedModels && selectedModels.length == 1 && !selectedModels[0].get('deviation') && selectedModels[0].get('zoneType') == "STORAGE") {
	            	this.$('#move-button').removeAttr('disabled');
	            } else {
	            	this.$('#move-button').attr('disabled', true);
	            }
	            // Print
	            if (selectedModels && selectedModels.length >= 1) {
	            	this.$('#printPL').closest('li').removeClass('disabled');
	            } else {
	            	this.$('#printPL').closest('li').addClass('disabled');
	            }
	        },
	        
	        printPartLabel : function(e) {
	        	e.preventDefault();
	        	Gloria.trigger('hideAppMessageView');
	        	if(!this.$('#printPL').closest('li').hasClass('disabled')) {
					Gloria.WarehouseApp.trigger('Inventory:printPL', this.selectedModels);
	        	}
			},
			
			printBinLocation : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('Inventory:printBL');
			},
			
			handleMovePartClick : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('Inventory:MovePartModal:show', this.selectedModels[0]);
			},

			handleAdjustQtyClick: function(e){
				 e.preventDefault();	
				 Gloria.trigger('hideAppMessageView');
				 Gloria.WarehouseApp.trigger('Inventory:AdjustQtyModal:show', this.selectedModels[0]);
			},
			
			exportInventory : function(e) {
				Gloria.WarehouseApp.trigger('Inventory:export');
			},
			
	        render : function() {
	        	this.$el.html(compiledTemplate());
				return this;
			}
	    });
	 });
    
    return Gloria.WarehouseApp.View.InventoryView;
});
