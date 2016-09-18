define([ 'app', 'jquery', 'underscore', 'handlebars', 'backbone', 'marionette',
		'hbs!views/materialrequest/overview/view/overview-button' ], function(
		Gloria, $, _, Handlebars, Backbone, Marionette, compiledTemplate) {

	Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.OverviewButtonView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.permittedActions = {};
				Gloria.MaterialRequestApp.on('MaterialRequestOverview:select', this.gridSelectHandler, this);
			},
			
			events : {
				'click #clear-filter' : 'clearFilter',
				'click #open-button' : 'openMaterialRequest',
				'click #copy-button' : 'copyAndCreateMaterialRequest',
				'click #create-button' : 'createMaterialRequest'
			},

			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
			
			openMaterialRequest : function() {
				if (this.selectedRows) {
					Gloria.MaterialRequestApp.trigger('MaterialRequestOverview:show', _.first(this.selectedRows).id);
				}
			},

			copyAndCreateMaterialRequest : function() {
				if (this.selectedRows) {
					Gloria.MaterialRequestApp.trigger('MaterialRequestOverview:copy', _.first(this.selectedRows).id);
				}
			},

			createMaterialRequest : function() {
				Gloria.MaterialRequestApp.trigger('MaterialRequestOverview:create');
			},

			render : function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.permittedActions
				}));
				return this;
			},

			gridSelectHandler : function(selectedRows) {
				if (selectedRows) {
					this.selectedRows = selectedRows;
					if (selectedRows.length == 1) {
						this.permittedActions['open'] = true;
						this.permittedActions['copy'] = true;
					} else {
						delete this.permittedActions['open'];
						delete this.permittedActions['copy'];
					}
				} else {
					delete this.selectedRows;
				}
				this.render();
			},

			onDestroy : function() {
				Gloria.MaterialRequestApp.off(null, null, this);
			}
		});
	});

	return Gloria.MaterialRequestApp.View.OverviewButtonView;
});
