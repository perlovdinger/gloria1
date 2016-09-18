define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
		'hbs!views/warehouse/pick/view/pick-button'
],function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PickButtonView = Marionette.View.extend({

			initialize : function(options) {				
				this.listenTo(Gloria.WarehouseApp,'PickGrid:select', this.pickGridSelectHandler);	
				this.listenTo(Gloria.WarehouseApp,'CreatePickListButton:disable', this.disableCreatePickListButton);
				this.listenTo(Gloria.WarehouseApp,'CancelPickListButton:enable', this.enableCancelPickListButton);
				this.listenTo(Gloria.WarehouseApp,'CancelPickListButton:disable', this.disableCancelPickListButton);
			},
			
			events : {
				'click #clear-filter' : 'clearFilter',
				'click #create-button' : 'handleCreateButtonClick',
				'click #cancel-button' : 'handleCancelButtonClick'
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
			
			disableCreatePickListButton : function(){
				this.$('#create-button').attr('disabled', true);
			},
			
			enableCreatePickListButton : function(){
                this.$('#create-button').removeAttr('disabled');
            },
			
			disableCancelPickListButton : function(){
                this.$('#cancel-button').attr('disabled', true);
            },
            
            enableCancelPickListButton : function(){
                this.$('#cancel-button').removeAttr('disabled');
            },
			
			pickGridSelectHandler : function(selectedRows) {
				this.selectedRows = selectedRows;
				if (this.selectedRows && this.selectedRows.length >= 1) {
					this.enableCreatePickListButton();
				} else {
					this.disableCreatePickListButton();
					this.disableCancelPickListButton();
				}
			},
			
			handleCreateButtonClick : function(e) {
				var that = this;
				e.preventDefault();	
				Gloria.WarehouseApp.trigger('CreatePickList:show', that.selectedRows);
			},
			
			handleCancelButtonClick : function(e) {
			    var that = this;
                e.preventDefault(); 
                Gloria.WarehouseApp.trigger('CancelPickList:show', that.selectedRows);
			},
			
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			},
			
			onDestroy: function() {
			    Gloria.WarehouseApp.off(null, null, this);                
			}
		});
	});

	return Gloria.WarehouseApp.View.PickButtonView;
});
