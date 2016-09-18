define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
		'hbs!views/deliverycontrol/assign/view/assign-dc-button'
],function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AssignDCButtonView = Marionette.View.extend({

			initialize : function(options) {
				this.module = options.module;
				this.permittedActions = {};
				this.listenTo(Gloria.DeliveryControlApp, 'AssignDCGrid:select', this.assignDCGridSelectHandler);
				this.listenTo(Gloria.DeliveryControlApp, 'PoGrid:select', this.poGridSelectHandler);
				this.listenTo(Gloria.DeliveryControlApp, 'ControlButton:refresh', this.refreshThis);
			},
			
			refreshThis : function() {
                this.permittedActions = {};
                this.render();
			},
			
			events : {
				'click #clear-filter' : 'clearFilter',
				'click #add-button' : 'handleAddButtonClick',
				'click #edit-button' : 'handleEditButtonClick',
				'click #change-button' : 'handleChangeButtonClick',
				'click #delete-button' : 'handleDeleteButtonClick',
				'click #reassign-button' : 'handleReassignButtonClick'
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},

			assignDCGridSelectHandler : function(selectedRows) {
				this.selectedRows = selectedRows;
				if(selectedRows.length != 1) {
				    delete this.permittedActions['edit'];
				    delete this.permittedActions['delete'];
				    delete this.permittedActions['reassign'];
				} else {
				    this.permittedActions['edit'] = true;
				    this.permittedActions['delete'] = true;
				    this.permittedActions['reassign'] = true;
				}
				this.render();
			},
			
			poGridSelectHandler : function(selectedRows) {
				this.selectedRows = selectedRows;
				if(selectedRows.length < 1) {
				    delete this.permittedActions['reassign'];
				} else {
				    this.permittedActions['reassign'] = true;
				}
				this.render();
			},
			
			handleAddButtonClick : function(e) {
				Gloria.trigger('hideAppMessageView');
				Gloria.DeliveryControlApp.trigger('AddEditModal:show', this.module);
			},
			
			handleEditButtonClick : function(e) {
				Gloria.trigger('hideAppMessageView');
				Gloria.DeliveryControlApp.trigger('AddEditModal:show', this.module, _.first(this.selectedRows));
			},
			
			handleChangeButtonClick : function(e) {
				Gloria.trigger('hideAppMessageView');
				Gloria.DeliveryControlApp.trigger('ChangeModal:show', this.module);
			},
			
			handleDeleteButtonClick : function(e) {
				Gloria.trigger('hideAppMessageView');
				Gloria.DeliveryControlApp.trigger('DeleteModal:show', this.module, _.first(this.selectedRows));
			},
			
			handleReassignButtonClick : function(e) {
				Gloria.trigger('hideAppMessageView');
				Gloria.DeliveryControlApp.trigger('ReassignModal:show', this.selectedRows);
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					permittedActions : this.permittedActions
				}));
				return this;
			}
		});
	});

	return Gloria.DeliveryControlApp.View.AssignDCButtonView;
});
