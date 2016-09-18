define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
		'hbs!views/warehouse/pick/view/ship-button'
], function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ShipButtonView = Marionette.View.extend({

			initialize : function(options) {
				this.listenTo(Gloria.WarehouseApp, 'ShipGrid:select', this.gridSelectHandler);
				this.listenTo(Gloria.WarehouseApp, 'ShipButton:refresh', this.refreshThis);
			},

			events : {
				'click #clear-filter' : 'clearFilter',
				'click #mark-button' : 'handleMarkAsShippedButtonClick',
				'click #create-button' : 'handleCreateDispatchNoteButtonClick',
				'click #open-button' : 'handleOpenDispatchNoteButtonClick',
				'click #print-button' : 'handlePrintDispatchNoteButtonClick',
				'click #export-button' : 'handleExportProFormaButtonClick'
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},

			defalutPermittedActions : {
				openDispatchNote : false,
				createDispatchNote : false,
				markAsShipped : false,
				printdispatchNote : false,
				exportProForma : false
			},

			gridSelectHandler : function(selectedRows) {
				this.selectedRows = selectedRows;
				this.permittedActions = _.clone(this.defalutPermittedActions);
				var isMarkShipped = false;
				$.each(selectedRows, function(i, model) {
					if (model.get('status') == 'READY_TO_SHIP') {
						isMarkShipped = true;
					} else {
						isMarkShipped = false;
						return false;
					}
				});

				if (isMarkShipped) {
					this.permittedActions['markAsShipped'] = true;
				} else {
					delete this.permittedActions['markAsShipped'];
				}

				if (selectedRows && selectedRows.length == 1) {
				    this.requestListId = _.first(selectedRows).get('id');
					this.dispatchNoteId = _.first(selectedRows).get('dispatchNoteId');
					if (this.dispatchNoteId) {
						this.permittedActions['openDispatchNote'] = true;
						delete this.permittedActions['createDispatchNote'];
						this.permittedActions['printdispatchNote'] = true;
						this.permittedActions['exportProForma'] = true;
					} else {
						delete this.permittedActions['openDispatchNote'];
						this.permittedActions['createDispatchNote'] = true;
						delete this.permittedActions['printdispatchNote'];
						delete this.permittedActions['exportProForma'];
					}
				} else {
					delete this.permittedActions['openDispatchNote'];
					delete this.permittedActions['createDispatchNote'];
					delete this.permittedActions['printdispatchNote'];
					delete this.permittedActions['exportProForma'];
				}
				this.render();
			},

			refreshThis : function() {
				this.permittedActions = {};
				this.render();
			},

			handleOpenDispatchNoteButtonClick : function(e) {
				Gloria.WarehouseApp.trigger('OpenDispatchNoteButton:clicked', _.first(this.selectedRows));
			},

			handleCreateDispatchNoteButtonClick : function(e) {
				Gloria.WarehouseApp.trigger('DispatchButton:clicked', _.first(this.selectedRows));
			},

			handleMarkAsShippedButtonClick : function(e) {
				Gloria.WarehouseApp.trigger('MarkAsShipped:clicked', this.selectedRows);
			},

			handlePrintDispatchNoteButtonClick : function(e) {
				Gloria.WarehouseApp.trigger('PrintDispatchNote:clicked', this.dispatchNoteId);
			},

			handleExportProFormaButtonClick : function(e) {
				Gloria.WarehouseApp.trigger('Ship:ExportProForma', this.requestListId);
			},

			render : function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.permittedActions || this.defalutPermittedActions
				}));
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.ShipButtonView;
});
