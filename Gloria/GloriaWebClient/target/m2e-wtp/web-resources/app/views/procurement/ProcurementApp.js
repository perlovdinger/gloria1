define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'utils/marionette/GloriaRouter',
        'controllers/controllerManager'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaRouter, ControllerManager) {

	Gloria.module('ProcurementApp', function(ProcurementApp, Gloria, Backbone, Marionette, $, _) {
		
	    ProcurementApp.title = "Gloria.i18n.procurement.procurement";
	    
	    ProcurementApp.routes =  {
	    		'procurement/overview(/:page)' : 'showProcurementOverview',
				'procurement/overview/toProcure/procureLineDetail(/:id)' : 'showProcurementDetail',
				'procurement/overview/procured/procureLineDetail(/:id)' : 'showProcurementDetail',				
				'procurement/overview/procureLineDetail(/:id)' : 'showProcurementDetail',
				'procurement/overview/modification/modifyDetails(/:id)?:params' : 'showModifyDetails',
				'procurement/overview/modification/viewDetails(/:id)?:params' : 'viewModifyDetails',
				'procurement/overview/change/changeDetails/:id' : 'showChangeDetails'				
		},
	    
		ProcurementApp.Router = GloriaRouter.extend({
			appRoutes: GloriaRouter.prototype.transformAppRoutes(ProcurementApp.routes)
		});

		var API = {
	        
			showProcurementOverview : function(page, procurementOverviewController) {
	        	if (procurementOverviewController) {
	                procurementOverviewController.control(page);
	            } else {
	            	Gloria.controllerManager.getController('procurementOverviewController', this.showProcurementOverview, page);
	            }
			},
			   
			showProcurementDetail : function(id, procurementDetailController) {
				var procureLineId = id;
	        	if (procurementDetailController) {	        		
	                procurementDetailController.control(procureLineId);
	            } else {
	            	Gloria.controllerManager.getController('procurementDetailController', this.showProcurementDetail, procureLineId);
	            }
			},
			
			showModifyDetails : function(ids, params, modifyDetailsController) {
				if (modifyDetailsController) {					
					modifyDetailsController.control(ids, params);
				} else {
					Gloria.controllerManager.getController('modifyDetailsController', this.showModifyDetails, ids, params);
				}
			},
			
			viewModifyDetails : function(ids, params, modifyDetailsController) {
				if (modifyDetailsController) {					
					modifyDetailsController.control(ids, params);
				} else {
					Gloria.controllerManager.getController('modifyDetailsController', this.showModifyDetails, ids, params);
				}
			},
			
			showChangeDetails: function(id, changeDetailsController) {
			    if (changeDetailsController) {                   
			        changeDetailsController.control(id);
                } else {
                    Gloria.controllerManager.getController('changeDetailsController', this.showChangeDetails, id);
                }
			}
		};

		Gloria.addInitializer(function() {
			this.controllerManager = ControllerManager.getInstance();
			new ProcurementApp.Router({
				controller : API
			});			
		});
	});

	return Gloria.ProcurementApp;
});
