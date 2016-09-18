/** 
 * ProcurementWarehouseInfoView is a Subview in ProcureLineDetail Page in Gloria 
 */

define([ 'app',
         'jquery',
         'underscore',
         'backbone',
         'marionette',
         'handlebars',
         'datepicker',
         'moment',
         'views/common/dangerousgoods/DangerousGoodsSelectorView',
         'hbs!views/procurement/detail/view/procurement-warehouse-info' 
], function(Gloria, $, _, Backbone, Marionette, Handlebars, Datepicker, moment, DangerousGoodsSelectorView, compiledTemplate) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
	
		View.ProcurementWarehouseInfoView = Marionette.View.extend({
			
			initialize : function(options) {
				this.procureLineModel = options.procureLineModel;
				this.isDisabled = (this.procureLineModel.get('status') === 'PROCURED' 
                    || this.procureLineModel.get('status') === 'PLACED' 
                    || this.procureLineModel.get('status') === 'RECEIVED'
                    || this.procureLineModel.get('status') === 'RECEIVED_PARTLY') ? true : false;
				this.listenTo(Gloria.ProcurementApp, 'procurement:editmode', this.enableEditMode);				
			},
			
			enableEditMode: function(editable) {
			    var id = this.procureLineModel.id;
			    this.$('div#warehouseComment').addClass('hidden');
			    this.$('textarea#warehouseComment').removeClass('hidden');
			},
	
			events : {
                'show .date' : 'stopPropagation',
            },
            
            stopPropagation : function(e) {
                e.stopPropagation();
                e.stopImmediatePropagation();
            },
            
           dangerousGoodsSelector : function(options) {
                var csv = new DangerousGoodsSelectorView(options.hash);
                var returnString = new Handlebars.SafeString(csv.el.outerHTML);
                csv.destroy();
                delete csv;
                return returnString;
            },
			render : function() {				
				this.$el.html(compiledTemplate({
					'procureLineModel' : this.procureLineModel.toJSON(),
					'dangerousGoodsSelector' : this.dangerousGoodsSelector,
					'readOnly': this.isDisabled
				}));
				if(this.isDisabled) {
					this.$('.date').datepicker('remove');
				} else {
					this.$('.date').datepicker();
				}
				return this;
			},
			onDestroy: function() {
                this.$('.date').datepicker('remove');
            }
		});
	});
	
	return Gloria.ProcurementApp.View.ProcurementWarehouseInfoView;
});
