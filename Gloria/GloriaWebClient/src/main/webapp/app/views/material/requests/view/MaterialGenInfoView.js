define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'i18next',
		'datepicker',
        'moment',
        'views/material/helper/AllWarehouseListSelectorHelper',
        'views/material/helper/AllWarehouseSelectorViewHelper',
		'hbs!views/material/requests/view/material-gen-info'
], function(Gloria, $, _, Handlebars, Marionette, i18n, Datepicker, moment,AllWarehouseListHelper,AllWarehouseSelectorViewHelper,compiledTemplate) {
    
	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.MaterialGenInfoView = Marionette.View.extend({

			initialize : function(options) {
				if(!options.model) {
					throw new Error('Model must be supplied!');
				}
				this.model = options.model;
			},
			
			events : {
			    'change input[name="deliveryAddressType"]': 'handleNewDeliveryAddressChange'
			},
	        
			priorityOptions : [{
	            '1' : '1'
	        }, {
	            '2' : '2'
	        }, {
	            '3' : '3'
	        }],	        
	        
			render : function() {
				this.$el.html(compiledTemplate({
					'data' : this.model ? this.model.toJSON() : {},
					'priorityOptions' : this.priorityOptions
				}));
				var nowDate = new Date();
				var today = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), 0, 0, 0, 0);
				this.$('.date').datepicker({
					startDate: today
				});
				
				if (this.model.attributes.deliveryAddressType == 'WH_SITE') {
					this.$('#newDeliveryAddress').attr('checked', false);
					this.$('#transferToWarehouse').attr('checked', true);
					var jsonStringData = AllWarehouseListHelper.constructAllWarehouseList();
                	// Remove the Ship To warehouse/site
                	jsonStringData = JSON.parse(jsonStringData);
                	var whSiteId = this.model.get('whSiteId');
                	jsonStringData = jsonStringData.filter(function(el) {
						return el.siteId != whSiteId;
					});
                    new AllWarehouseSelectorViewHelper({
                        element : '#tranferToWarehouseDropDown',
                        select2Data: this.constructSelect2Data(jsonStringData),
                        select2Options: {
                            minimumResultsForSearch : 1
                        }
                    });
					this.$('#warehouseId').show();
					this.$('#deliveryAddressTextArea').hide();
					this.$("input[name='request[deliveryAddressId]']" ).val('');
                	this.$('#deliveryAddressType').val('WH_SITE');
                	this.$('#deliveryAddressTextArea').prop("disabled", true);
                	this.$('#newDeliveryAddress').prop("disabled", true);
                	this.$('#transferToWarehouse').prop("disabled", true);
                	this.$('#tranferToWarehouseDropDown').prop("disabled", true);
					} else if (this.model.attributes.deliveryAddressType == 'NEW_DELIVERY_ADDRESS') {
						this.$('#newDeliveryAddress').attr('checked', true);
						this.$('#transferToWarehouse').attr('checked', false);
						this.$('#warehouseId').hide();
						this.$('#deliveryAddressTextArea').show();
						this.$("input[name='request[deliveryAddressId]']").val('');
	                	this.$('#deliveryAddressType').val('NEW_DELIVERY_ADDRESS');
	                	this.$('#deliveryAddressTextArea').prop("disabled", true);
	                	this.$('#newDeliveryAddress').prop("disabled", true);
	                	this.$('#transferToWarehouse').prop("disabled", true);
	                	this.$('#tranferToWarehouseDropDown').prop("disabled", true);
				} else if(this.model.attributes.deliveryAddressType == 'OUTBOUND_LOCATION'){
					var outBoundLoc = this.model.attributes.deliveryAddressId+'-'+this.model.attributes.deliveryAddressName;
					 $('#buildLocation').text(outBoundLoc);
					 this.$('#warehouseId').hide();
					 this.$('#deliveryAddressTextArea').hide();
					 this.$('#deliveryAddressTextArea').prop("disabled", true);
                	 this.$('#newDeliveryAddress').prop("disabled", true);
                	 this.$('#transferToWarehouse').prop("disabled", true);
                	 this.$('#tranferToWarehouseDropDown').prop("disabled", true);
				}
				
				this.checkRequestListStatus();
				return this;
			},
			
			checkRequestListStatus: function() {
			    if (this.model.get('status') == 'SENT') {
			        this.$('#requiredDeliveryDate').prop("disabled", true);
			        this.$('#priority').prop("disabled", true);
			    }
			},
			
			onShow : function() {
				this.listenTo(this.model, 'add remove change', this.render);
			},
			
			onDestroy: function() {
                this.$('.date').datepicker('remove');
            },
            
            handleNewDeliveryAddressChange : function(e){
                e.preventDefault();
                if(e.currentTarget.id == 'newDeliveryAddress') {
                	this.$('#transferToWarehouse').attr('checked', false);
                	this.$('#warehouseId').hide();
                	this.$("input[name='request[deliveryAddressId]']" ).val('');
                	this.$('#deliveryAddressType').val('NEW_DELIVERY_ADDRESS');
                	if(e.currentTarget.checked) {
                		this.$('#deliveryAddressTextArea').show();
                	} else {
                		this.$('#deliveryAddressTextArea').hide();
                		this.$('#deliveryAddressTextArea').val('');
                	}
                } else if(e.currentTarget.id == 'transferToWarehouse') {
                	this.$('#newDeliveryAddress').attr('checked', false);
                	this.$('#deliveryAddressTextArea').hide();
                    this.$('#deliveryAddressTextArea').val('');
                    this.$('#deliveryAddressType').val('WH_SITE');
                    if(e.currentTarget.checked) {
                    	this.$('#warehouseId').show();
                    	var jsonStringData = AllWarehouseListHelper.constructAllWarehouseList();
                    	// Remove the Ship To warehouse/site
                    	jsonStringData = JSON.parse(jsonStringData);
                    	var whSiteId = this.model.get('whSiteId');
                    	jsonStringData = jsonStringData.filter(function(el) {
							return el.siteId != whSiteId;
						});
                        new AllWarehouseSelectorViewHelper({
                            element : '#tranferToWarehouseDropDown',
                            select2Data: this.constructSelect2Data(jsonStringData),
                            select2Options: {
                                minimumResultsForSearch : 1
                            }
                        });
                	} else {
                		this.$('#warehouseId').hide();
                		this.$("input[name='request[deliveryAddressId]']" ).val('');
                	}
                }
                
                if(!e.currentTarget.checked) {
                	this.$('#deliveryAddressType').val('');
                }
            },
            
            constructSelect2Data : function(jsonData){
                var select2Data = [];
                _.each(jsonData, function(item, index) {
                    select2Data.push({
                        id : item.siteId,
                        text : item.siteId + ' - ' + item.siteName
                    });
                });
                return select2Data;
            }
            
		});
	});
	
	return Gloria.MaterialApp.View.MaterialGenInfoView;
});
