define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'utils/UserHelper',
        'utils/dialog/dialog',
        'hbs!views/common/warehouseSelector/WarehouseSelector'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, Dialog, compiledTemplate) {

    var WarehouseSelectorView = Marionette.View.extend({
        
        className: 'form-inline',
        
        events: {
            'change select[id^="warehouseSelector_"]': 'setDefaultWarehouse'
        },
        
        setDefaultWarehouse: _.debounce(function(e) {
        	var target = e.target;
        	var $target = $(e.target);
        	var that = this;
        	require(['views/common/startpage/StartpageApp','views/warehouse/WarehouseApp', 'views/material/MaterialApp',
                     'views/deliverycontrol/DeliveryControlApp', 'views/procurement/ProcurementApp',
                     'views/materialrequest/MaterialRequestApp', 'views/admin/AdminTeamApp', 'views/testingutility/TestingUtilityApp'],
                     function(StartpageApp, WarehouseApp, MaterialApp, DeliveryControlApp,
                    		 ProcurementApp, MaterialRequestApp, AdminTeamApp, TestingUtilityApp) {
    		    	
        		var showed = false; 
        			// get the routes object of defined modules 
    		    	_.any([StartpageApp.routes, WarehouseApp.routes, MaterialApp.routes, DeliveryControlApp.routes,
                    		 ProcurementApp.routes, MaterialRequestApp.routes, AdminTeamApp.routes, TestingUtilityApp.routes], function(routes) {
    		    		if(routes) {
    		    		_.any(routes, function(value, key) {    	    		    	
        		    		// change the route to RegExp
    		    			var routeRegex = Backbone.Router.prototype._routeToRegExp(key);
        		    		// test the route RegExp against the current route
    		    			if(key && value.confirmLeave && routeRegex.test(Backbone.history.fragment)) {
        		    			// extract the parameters of the route to pass them to confirmLeave function 
        		    			var routeParams = Backbone.Router.prototype._extractParameters(routeRegex, Backbone.history.fragment);
        		    			var confirmLeave =  _.isFunction(value.confirmLeave) ? value.confirmLeave.apply(this, routeParams) : value.confirmLeave; 
        		    			if(confirmLeave) {
        		    				that.showConfirmation(target.value, value.leaveURL || '#');
        		    				return showed = true;        		    			
        		    			}
        		    		}
        		    	});
    		    		}
    		    		return showed;
    		    	});    		    	
    		    	
    		    	if(!showed) that.changeWarehouse(target.value);
            });
        	        	
        }, 500),
        
        showConfirmation: function(warehouseId, leaveURL) {
        	var that = this;
        	Dialog.show({
				title: i18n.t("Gloria.i18n.confirmDiscardHeader"),
				message: i18n.t("Gloria.i18n.confirmLeave"),
				options: {
					unclosable: true
				},
				buttons: {
	                yes: {
	                    label: i18n.t('Gloria.i18n.buttons.yes'),
	                    className: "btn btn-primary",
	                    callback: function(e) {
	                    	e.preventDefault();	                    	
	                        UserHelper.getInstance().setDefaultWarehouse(warehouseId);
	                    	Backbone.history.navigate(leaveURL, false);
	                    	Gloria.trigger('reloadPage');
	                        return true;
	                    }
	                },
	                no: {
	                    label: i18n.t('Gloria.i18n.buttons.no'),
	                    className: "btn btn-default",
	                    callback: function(e) {
	                        e.preventDefault();
	                        that.$('select[id^="warehouseSelector_"]').val(UserHelper.getInstance().getDefaultWarehouse());
	                        return false;
	                    }
	                }
	            }
			});
        },
        
        changeWarehouse: function(selectedValue) {
        	var warehouseId = selectedValue;
            UserHelper.getInstance().setDefaultWarehouse(warehouseId);
            Gloria.trigger('reloadPage');
        },
        
        render : function() {            
            this.$el.html(compiledTemplate({
                cid: this.cid,
                items : this.collection.toJSON(),
                defaultItem: UserHelper.getInstance().getDefaultWarehouse()
            }));
            return this;
        }
    });

    return WarehouseSelectorView;
});