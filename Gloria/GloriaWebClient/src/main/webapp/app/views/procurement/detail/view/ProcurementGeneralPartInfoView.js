/** 
 * ProcurementGeneralPartInfoView is a Subview in ProcureLineDetail Page in Gloria 
 */
define([ 'app',
         'jquery',
         'underscore',
         'backbone',
         'marionette',
         'hbs!views/procurement/detail/view/procurement-general-part-info' 
], function(Gloria, $, _, Backbone, Marionette, compiledTemplate) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementGeneralPartInfoView = Marionette.View.extend({
	
			initialize : function(options) {
				this.procureLineModel = options.procureLineModel;
			},
	
			render : function() {
				if (this.procureLineModel) {
					this.$el.html(compiledTemplate({
						'data' : this.procureLineModel.toJSON()
					}));
				}
				return this;
			},
			
			onShow : function() {
				if(this.procureLineModel.get('status') === 'PROCURED'
				    || this.procureLineModel.get('status') === 'PLACED'
				    || this.procureLineModel.get('status') === 'RECEIVED'
				    || this.procureLineModel.get('status') === 'RECEIVED_PARTLY') {
					$('#procuredDiv').show();
				} else {
					$('#procureDiv').show();
				}
				if(this.procureLineModel.get('status') === 'RECEIVED' || this.procureLineModel.get('status') === 'RECEIVED_PARTLY') {
				    $('#edit').css('display', 'none');
				}
			}
		});
	});
	
	return Gloria.ProcurementApp.View.ProcurementGeneralPartInfoView;
});
