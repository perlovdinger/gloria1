define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'i18next',
		'bootstrap',		
		'utils/backbone/GloriaCollection', 
		'views/warehouse/store/view/AdjustStoreItemView'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, BootStrap, Collection, AdjustStoreItemView) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AdjustStoreCollectionView = Marionette.CollectionView.extend({

			id : "store-list",
			
			initialize: function(options) {				
			    this.listenTo(Gloria.WarehouseApp, 'Store:AdjusttedMaterialLines:clicked', this.onSave);
			    this.currentSplitCount = options.currentSplitCount;
			},
			
			collectionEvents: {
			    'add remove change:storedQuantity': 'triggerModifiedCollection'
			},

			childView : AdjustStoreItemView,
			
			childViewOptions: function(model, index) {			    
			    return {
			      model: model,
			      index: index,
			      currentSplitCount : this.currentSplitCount
			    };
			},
			
			triggerModifiedCollection: function() {
			    Gloria.WarehouseApp.trigger('Store:update:split', this.collection);
			},
			
			onSave : function(model) {				
				Gloria.WarehouseApp.trigger('Store:split', model, this.collection);
			},
			
			showWarningMessage : function() {
				if(this.model.get('storedQuantity') != this.countTotalExpectedQty()) {
					$('#warningMessage').show();
				} else {
					$('#warningMessage').hide();
				}
			},
			
			countTotalExpectedQty : function() {
				var count = 0;
				this.collection.each(function(model) {
					count += parseInt(model.get('storedQuantity'), 10);
				});
				return count;
			},
			
			onShow: function() {
			    this.showWarningMessage();
			    this.triggerModifiedCollection();
			}
		});
	});

	return Gloria.WarehouseApp.View.AdjustStoreCollectionView;
});
