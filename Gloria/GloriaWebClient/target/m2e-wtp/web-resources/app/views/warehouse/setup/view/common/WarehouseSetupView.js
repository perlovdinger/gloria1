define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'backbone.syphon',
		'hbs!views/warehouse/setup/view/common/warehouse-setup'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Syphon, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.WarehouseSetupView = Marionette.LayoutView.extend({

			regions : {
				warehouseBasicInfo : '#warehouseBasicInfo',
				storageInfoContent : '#storageInfoContent',
				zoneInfoContent : '#zoneInfoContent',
				aisleInfoContent : '#aisleInfoContent',
				levelInfoContent : '#levelInfoContent',
				rackInfoContent : '#rackInfoContent'
			},

			initialize : function(options) {
				this.warehouseId = options.warehouseId;
				Gloria.WarehouseApp.on('warehouseModel:fetched', this.handleRackRowSetup, this);
			},

			events : {			
			    'click #save': 'save',
				'click #cancel' : 'goToPreviousRoute',				
				'show.bs.collapse .accordion' : 'publishAccardionCollapseEvent'
			},
			
			save: function(e) {
			    var printBarcode = this.$('#printBarcodes').is(':checked');
			    Gloria.WarehouseApp.trigger('warehouse:save', printBarcode);
			},

			goToPreviousRoute : function() {
				Backbone.history.navigate('#', true);
			},

			handleRackRowSetup : function(warehouseModel) {				
			    if(warehouseModel && warehouseModel.get('setUp') == 'ROW') {
			        var title = i18n.t('Gloria.i18n.warehouse.setup.text.aisleInfo_ROW');
			        this.$('#aisleInfo').find('.accordion-toggle').find('strong').text(title);			        
			    }
			},
			
			publishAccardionCollapseEvent : function(e) {
	        	if(e && e.target && e.target.getAttribute('id')) {        		
	        		var accardionID = e.target.getAttribute('id');        						   
	        		Gloria.WarehouseApp.trigger(accardionID + ':showaccordion');        		      		     		
	        	}       	                 
	        },
			
			render : function() {
				this.$el.html(compiledTemplate({
					warehouseId : this.warehouseId
				}));
				return this;
			},			
			
			onShow : function() {
				this.$el.find('#rackInfo').hide();
				// Show Storage Room View as default
				Gloria.WarehouseApp.trigger('storageInformation:showaccordion');
			},
			
			onDestroy: function() {
			    Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.WarehouseSetupView;
});
