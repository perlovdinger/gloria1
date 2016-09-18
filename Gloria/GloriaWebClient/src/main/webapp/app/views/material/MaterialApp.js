define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'utils/marionette/GloriaRouter',
        'controllers/controllerManager'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaRouter, ControllerManager) {

	Gloria.module('MaterialApp', function(MaterialApp, Gloria, Backbone, Marionette, $, _) {
		
	    MaterialApp.title = "Gloria.i18n.material.material";
	    
		MaterialApp.Router = GloriaRouter.extend({
			appRoutes : {				
				'material/:page' : 'showMaterialView',
				'material/linesoverview/requests/:ids' : 'showMaterialRequestsView',
				'material/linesoverview/linedetails/:ids' : 'showMaterialDetailsView',
				'material/linesoverview/requestlist/:id':'showMaterialLinesforReqList'
			}
		});

		var API = {
				showMaterialView : function(page) {
					page = page || 'linesoverview';
					switch (page) {
					case 'linesoverview':
						this.showMaterialOverview(page);
						break;
				}
	        },
	        
	        showMaterialLinesforReqList : function(id) {
				this.materialLinesForReqList({
					'input' : 'fromRequestListId',
					'id' : id
				});
			},
			
			materialLinesForReqList : function(options, materialRequestsController) {
				if (materialRequestsController) {
					materialRequestsController.control(options);
				} else {
					Gloria.controllerManager.getController('materialRequestsController', this.materialLinesForReqList, options);
				}
			},
	        
	        showMaterialOverview : function(page, materialLineController) {
	            if (materialLineController) {
	            	Backbone.history.navigate('material/' + page, {
	                    replace : true
	                });
	            	materialLineController.control(page);
	            } else {
	            	Gloria.controllerManager.getController('materialLineController', this.showMaterialOverview, page);
	            }
	        },
	        
	        showMaterialRequestsView : function(ids) {
				this.showMaterialRequests({
					'input' : 'createNewRequestList',
					'ids' : ids
				});
			},
	        
			showMaterialRequests : function(options, materialRequestsController) {
	            if (materialRequestsController) {
	            	materialRequestsController.control(options);
	            } else {
	            	Gloria.controllerManager.getController('materialRequestsController', this.showMaterialRequests, options);
	            }
	        },
	        
	        
	        showMaterialDetailsView : function(id, materialDetailsController) {
	        	if (materialDetailsController) {
					materialDetailsController.control(id);
				} else {
					Gloria.controllerManager.getController('materialDetailsController', this.showMaterialDetailsView, id);
				}
	        }
		};

		Gloria.addInitializer(function() {
			this.controllerManager = ControllerManager.getInstance();
			new MaterialApp.Router({
				controller : API
			});
		});
	});

	return Gloria.MaterialApp;
});
