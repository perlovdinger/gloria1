define(['app',
        'backbone',
        'marionette',
        'handlebars',
		'views/procurement/detail/view/WarehouseSelectorHelper',
		'hbs!views/procurement/detail/view/test-object' 
], function(Gloria, Backbone, Marionette, Handlebars, warehouseSelectorHelper, compiledTemplate) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.TestObjectView = Backbone.Marionette.ItemView.extend({
			
			className : 'control-label',
			
			initialize : function(options) {
				this.model = options.model;
				this.status = options.status;
				this.procureLineModel = options.procureLineModel;
				this.listenTo(Gloria.ProcurementApp, 'procurelineDetails:ShipToAndPPSuffix:change', this.updateFinalWarehouse);
				this.listenTo(Gloria.ProcurementApp, 'procureLineform:finalwarehouse:hide', this.hideShowFinalWarehouse);
			},
			
			events : {
	    		'change input[type=checkbox][id^="testObject"]' : 'testObjectSelectHandler',
	        },
	        
			testObjectSelectHandler : function(event) {
				if(event.target.checked) {
					this.selected = true;
				} else {
					this.selected = false;
				}
				this.trigger('select');
			},
			
			updateFinalWarehouse : function(selectedValue) {
				this.$el.find('select[id^="warehouseId_"]').each(function(i, el) {
					$(el).val(selectedValue);
				});
			},

            hideShowFinalWarehouse : function(flag) {
                this.$el.find('[id^="warehouseDiv_"]').each(function(i, el) {
                    if(flag) {
                        $(el).hide();
                    } else {
                        $(el).show();
                    }
                });
            },
            
            formattedFinalWhSiteNames : function(finalWhSiteNames) {
            	var formattedWhSiteNames = '';
            	if(finalWhSiteNames) {            		
            		_.each(finalWhSiteNames, function(whSiteName) {
            			formattedWhSiteNames = formattedWhSiteNames + (formattedWhSiteNames ?  ', ' : '') + whSiteName;
					});
            	}
            	return formattedWhSiteNames;
			},
            
			render : function() {				
			 	this.$el.html(compiledTemplate({
			 		data : this.model.toJSON(),
			 		requestTypeKey: 'Gloria.i18n.materialrequest.details.generalInformation.requestType.' + this.model.get('requestType'),
			 		isDisabled : (this.status === 'PROCURED' || this.status === 'PLACED' || this.status === 'RECEIVED'
			 						|| this.status === 'RECEIVED_PARTLY') ? true : false,
					warehouseSelector : warehouseSelectorHelper,
			 		mtrlRequestType : 'Gloria.i18n.materialrequest.details.generalInformation.sdtype.' + this.model.get('mtrlRequestType'),
			 		showWarehouse : this.procureLineModel.get('procureType') != 'FROM_STOCK',
			 		warehouseSiteNames : this.formattedFinalWhSiteNames(this.model.get('finalWhSiteNames'))
	 		    }));		 	
			    return this;
			},
			
			onShow : function() {
				var selectedSupplierCounterPart = $('select[id^="supplierCounterPartId"] :selected').text().split(' - ')[0];
				this.updateFinalWarehouse(this.model.get('finalWhSiteId') || selectedSupplierCounterPart || '');
			}
		});
	});

	return Gloria.ProcurementApp.View.TestObjectView;
});
