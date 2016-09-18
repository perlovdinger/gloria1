define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'i18next',
		'bootstrap',		
		'utils/backbone/GloriaCollection', 
		'views/mobile/warehouse/view/receive/QuantityItemView'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, BootStrap, Collection, QuantityItemView) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.QuantityCollectionView = Marionette.CollectionView.extend({

			id : "qtyColl-list",
			
			initialize: function(options) {
				this.collection = options.collection;
				this.dnlModel = options.model;
				this.module = options.module;
				this.listenTo(this.collection, 'edit:started', this.onCollectionEditStarted);
				this.listenTo(this.collection, 'edit:ended', this.onCollectionEdited);
				this.listenTo(this.collection, 'sync', this.onCollectionSync);
				this.listenTo(Gloria.WarehouseApp, 'Receive:dnsl:saved', this.enableReceive);
				this.currentTarget = null;
			},
			
			onCollectionEditStarted: function(model, currentTarget) {
				this.editing = true;
				this.currentTarget = currentTarget;
				this.disableReceive();
			},
			
			onCollectionEdited: function(model, currentTarget) {
				if(this.currentTarget == currentTarget) {
					this.editing = false;
				}
			},
			
			onCollectionSync: function(model) {
				Gloria.WarehouseApp.trigger('Receive:dnsl:saved');
			},
			
			disableReceive : function() {
				$('#receive').attr('disabled', 'disabled');
			},
			
			enableReceive : function() {
				if(!this.editing) {
					$('#receive').removeAttr('disabled');
				}	
			},

			childView : QuantityItemView,
			
			childViewOptions: function(model, index) {			    
			    return {
			      model: model,
			      index: index,
			      module: this.module,
			      dnlModel : this.dnlModel
			    };
			}
		});
	});

	return Gloria.WarehouseApp.Receive.View.QuantityCollectionView;
});
