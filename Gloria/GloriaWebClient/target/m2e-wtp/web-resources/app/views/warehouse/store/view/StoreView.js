define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'hbs!views/warehouse/store/view/store'
], function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.StoreView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
				this.listenTo(Gloria.WarehouseApp, 'Store:setSelectedItems', this.setSelectedStoreItems);
				this.listenTo(Gloria.WarehouseApp, 'Store:StoreInfoSaved', this.handleStoreInfoSaveResponse);
	        },
			
			regions : {
				moduleInfo : '#moduleInfo',
				gridInfo : '#gridInfo'
			},

			events : {
				'click #clear-filter' : 'clearFilter',
				'click #toStore-button' : 'handleToStoreButtonClick',
				'click #store-button' : 'handleStoreButtonClick',
	            'click #split-button' : 'handleSplitButtonClick',	
	            'click #cancel-button' : 'handleCancelButtonClick',
				'click #print-store-list-button': 'handlePrintStoreListButton'
	        },
	        
	        clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
	        
	        handleToStoreButtonClick : function(models) {	        	
	        	Gloria.WarehouseApp.trigger('Store:clearSelectedModels');
		        Gloria.WarehouseApp.trigger('Store:ShowToStore', this.selectedModels);
	        },
	        
	        handleStoreButtonClick : function() {
	        	Gloria.WarehouseApp.trigger('Store:SaveStoreInfoIfValid');
	        },
	        
	        handleStoreInfoSaveResponse : function(status) {
				if(status) {
					Backbone.history.loadUrl(Backbone.history.fragment);
				} else {
					// Show error
				}
			},
	        
	        handleCancelButtonClick : function() {
	        	Gloria.WarehouseApp.trigger('Store:Store:Cancel');	        	
	        },
	        
	        handlePrintStoreListButton: function(e) {
	            e.preventDefault();
	            Gloria.WarehouseApp.trigger('Store:print:storeList');
	        },
	        
	        handleSplitButtonClick : function(models) {
	        	Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('Store:showStoreAdjustModalView', this.selectedModels);
			},

	        setSelectedStoreItems : function(selectedRows) {
	        	this.selectedModels = selectedRows;
				if (selectedRows && selectedRows.length >= 1) {
	            	$('#toStore-button').removeAttr('disabled');
	            } else {
	            	$('#toStore-button').attr('disabled', true);
	            }
				
				if (selectedRows && selectedRows.length == 1 && selectedRows[0].get('storedQuantity') > 0) {
					$('#split-button').removeAttr('disabled');
	            } else {
	            	$('#split-button').attr('disabled', true);
	            }
			},
	        
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});
	
	return Gloria.WarehouseApp.View.StoreView;
});
