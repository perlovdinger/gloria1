define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'hbs!views/warehouse/receive/view/receive-button'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ReceiveButtonView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
				this.permittedActions = {};
				this.listenTo(Gloria.WarehouseApp, 'ReceiveButton:refresh', this.refreshThis);
				this.listenTo(Gloria.WarehouseApp, 'ReceivedGrid:select', this.gridSelectHandler, this);
			},

			events : {
				'click #clear-filter' : 'clearFilter',
				'click #cancel-receive' : 'cancelGoodReceipt'
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
			
			refreshThis : function() {
				this.permittedActions = {};
				this.render();
			},

			gridSelectHandler : function(selectedModels) {
				this.selectedModels = selectedModels;
				if (selectedModels.length == 1) {
					this.permittedActions['cancelGoodReceipt'] = true;
				} else {
					delete this.permittedActions['cancelGoodReceipt'];
				}
				this.render();
			},

            cancelGoodReceipt: function(e) {
            	e.preventDefault();
            	Gloria.WarehouseApp.trigger('Received:GoodReceipt:show', _.first(this.selectedModels));
			},

			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					permittedActions : this.permittedActions
				}));
				return this;
			},

			onDestroy : function() {
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.ReceiveButtonView;
});
