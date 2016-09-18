define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'backbone.syphon',
		'hbs!views/deliverycontrol/myorderoverview/view/export-excel'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Syphon, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ExportExcelModalView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'exportExcelModal',

			events : {
				'click #export' : 'handleExportClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.models = options.models;
			},

			handleExportClick : function(e) {
				var choice = this.$el.find('input[name="exportOption"]:checked').val();
				var ids = null;
				if(choice == 1) {
					// Ids list
					_.each(this.models, function(model) {
						ids = (ids ? ids + ',' : '') + model.id;
						model.trigger('backgrid:select', model, false);
					});
				}
				var formData = Backbone.Syphon.serialize(this);
				// Options list
				var options = '';
				_.each(formData.options, function(val, key) {
					options = (options ? options + '&' : '') + (key + '=' + val);
				});
				this.$el.modal('hide');
				Gloria.DeliveryControlApp.trigger('MyOrderOverview:ExportExcel', ids, options);
			},
			
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate());
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
			},

			onShow : function() {
				this.$el.modal('show');
				if(this.models && this.models.length > 0) {
					this.$el.find('input#exportOption1').prop('checked', true); 
				} else {
					this.$el.find('input#exportOption2').prop('checked', true);
					this.$el.find('input#exportOption1').prop('disabled', true);
					this.$el.find('input#exportOption1').prop('disabled', true);
				}
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');			
				Gloria.DeliveryControlApp.off(null, null, this);
			}
		});
	});

	return Gloria.DeliveryControlApp.View.ExportExcelModalView;
});
