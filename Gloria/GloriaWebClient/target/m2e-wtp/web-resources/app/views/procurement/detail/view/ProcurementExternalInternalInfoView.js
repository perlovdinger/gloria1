/** 
 * ProcurementExternalInternalInfoView is a Subview in ProcureLineDetail Page in Gloria 
 */
define([ 'app',
         'jquery',
         'underscore',
         'backbone',
         'marionette',
         'utils/handlebars/CurrencySelectorHelper',
         'hbs!views/procurement/detail/view/procurement-external-internal-info' 
], function(Gloria, $, _, Backbone, Marionette, currencySelectorHelper, compiledTemplate) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ProcurementExternalInternalInfoView = Marionette.View.extend({
			
			initialize : function(options) {
				this.procureLineModel = options.procureLineModel;
				this.isDisabled = (this.procureLineModel.get('status') === 'PROCURED' 
				                || this.procureLineModel.get('status') === 'PLACED' 
				                || this.procureLineModel.get('status') === 'RECEIVED'
				                || this.procureLineModel.get('status') === 'RECEIVED_PARTLY') ? true : false;
			},

			render : function() {
			    var that = this;
				if (this.procureLineModel) {
					this.$el.html(compiledTemplate({
						'data' : this.procureLineModel.toJSON(),
						'isDisabled' : that.isDisabled,
						'currencySelector' : currencySelectorHelper
					}));
				}
				return this;
			}
		});
	});
	
	return Gloria.ProcurementApp.View.ProcurementExternalInternalInfoView;
});
