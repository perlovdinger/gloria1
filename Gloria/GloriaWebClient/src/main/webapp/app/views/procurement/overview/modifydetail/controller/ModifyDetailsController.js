define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
        'i18next',
        'utils/backbone/GloriaModel',
        'utils/backbone/GloriaCollection',
		'collections/ProcureRequestLineCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, GloriaModel, GloriaCollection, ProcureRequestLineCollection) {

	Gloria.module('ProcurementApp.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {

		var inputIds;
		var actionType;
		var modificationId;
		var modifyDetailsView;
		var selectedInfo;
		var hasReferer;

		/**
		 * Prepare Modify Details Part Info
		 */
		var prepareModifyDetailsPartInfo = function() {
			var that = this;
			Gloria.basicModalLayout.closeAndReset();
			Gloria.basicLayout.content.reset();
			Gloria.trigger('showBreadcrumbView', {
				itemId : inputIds
			});
			var idList = inputIds.split(',');
			selectedInfo = new ProcureRequestLineCollection();
			
			if(actionType == 'material') {				
				doFetchMaterialInfo.call(this, idList);				
			} else if(actionType == 'procureLine') {
				var procLineCollection = new GloriaCollection();
				procLineCollection.url = '/procurement/v1/materials?procureLineId=' + inputIds;
				procLineCollection.fetch({
					success : function(collection) {						
						doFetchMaterialInfo.call(that, collection.pluck('id'));						
					}
				});
			} else if(actionType == 'ASSEMBLE' || actionType == 'REPLACE') {
			    selectedInfo.url = '/procurement/v1/procurelines/modification/' + modificationId + '/materials';
			    selectedInfo.fetch({
			        success : function(response) {
			            processModifyDetailsView();
                    }
			    });
			}
		};
		
		/**
		 * Fetch Material Info
		 */
		var doFetchMaterialInfo = function(idsArray) {	
			var that = this;
			idsArray || (idsArray = []);				
			var promises = [];
			require([ 'models/ProcureRequestLineModel' ], function(ProcureRequestLineModel) {
				_.each(idsArray, function(element, index) {
					var tempModel = new ProcureRequestLineModel();
					tempModel.url = '/procurement/v1/materials/' + element;
					promises.push(tempModel.fetch({wait: true}));					
				});
				
				Backbone.$.when.apply($, promises).then(function() {
					var args = _.toArray(arguments);
					var modelsData = [];
					if(args && _.isObject(args[0]) && !_.isArray(args[0])) {
						modelsData.push(args[0]);
					} else {
						modelsData = _.map(args, function(response) {
							if(response && _.isArray(response)) {
								return response[0];	
							} 						
						});						
					}
					
					selectedInfo.reset(modelsData);
					if (idsArray.length == selectedInfo.length) {
						that.trigger('ProcureRequestLineCollection:fetched');
					}
				});
			});
		};
		
		/**
		 * Process Modify Details View
		 */
		var processModifyDetailsView = function() {
			if(actionType == 'ASSEMBLE' || actionType == 'REPLACE') {
				 var model = new GloriaModel();
		         model.fetch({
		        	 url: '/procurement/v1/procurelines/' + inputIds, // inputIds will be only ONE in this case!
		        	 success: function(response) {
		        		 showModifyDetailsView(response);
					}
		         });
			} else {
				showModifyDetailsView();
			}
		};
		
		/**
		 * Show Modify Details View
		 */
		var showModifyDetailsView = function(model) {
            require([ 'views/procurement/overview/modifydetail/view/ModifyDetailsView' ], function(ModifyDetailsView) {
                modifyDetailsView = new ModifyDetailsView({
                    collection : selectedInfo,
                    model : model,
                    hasReferer : hasReferer
                });
                Gloria.basicLayout.content.show(modifyDetailsView);
            });
		};

		/**
		 * Save Modify Details
		 */
		var saveModifyDetails = function(procureLineData, testObjectData) {			
			var materialHeaderGroupingCollection = new GloriaCollection();
			_.each(testObjectData.grouped, function(value, key) {
				var ref = value.referenceId || null;
				var models = selectedInfo.where({referenceId : ref});
				var ids = [];
				_.each(models, function(model) {
					if(model.notGrouped !== true) {
						ids.push(model.id);
					}
				});
				var modelTemp = new GloriaModel();
				modelTemp.set('materialIds', ids.join(','));
				modelTemp.set('procurementQty', value.procurementQty);
				materialHeaderGroupingCollection.add(modelTemp, {silent: true});
			});			
			
			_.each(testObjectData.notGrouped, function(value, key) {				
				var model = selectedInfo.get(key);				
				var modelTemp = new GloriaModel();
				modelTemp.set('materialIds', model.id + '');
				modelTemp.set('procurementQty', value.procurementQty);
				materialHeaderGroupingCollection.add(modelTemp, {silent: true});
			});	
			
			materialHeaderGroupingCollection.url = '/procurement/v1/materialheaders/grouping?pPartNo=' + procureLineData.pPartNo + '&pVersion='
				+ procureLineData.pVersion + '&pPartName=' + procureLineData.pPartName + '&pPartModification=' + procureLineData.pPartModification;
			materialHeaderGroupingCollection.save({
				success : function() {
					Gloria.trigger('goToPreviousRoute');
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			title : i18n.t('errormessages:general.title'),
		    			message : errorMessage
		    		});
				},
				error: function(error) {
                    if(error.status == 201) {
                        Gloria.trigger('goToPreviousRoute');
                    }
                }
			});
		};

		/**
		 * Cancel Modify Details
		 */
		var cancelModifyDetails = function(thisModel) {
			thisModel.url = '/procurement/v1/materialheaders/grouping/' + thisModel.get('modificationId') + '?action=cancel';
			thisModel.save({}, {
				success : function() {
					Gloria.trigger('goToPreviousRoute');
				},
				validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : errorMessage
		    		});
				},
				error: function(resp, error) {
                    if(error.status == 200) {
                        Gloria.trigger('goToPreviousRoute');
                    }
                }
			});
		};
		
		Controller.ModifyDetailsController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			getURLParameter : function(name) {
				return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)') .exec(location.hash) || [, "" ])[1].replace(/\+/g, '%20')) || null;
			},
			
			initializeListeners : function() {
				this.listenTo(Gloria.ProcurementApp, 'procureLine:ModifyDetails:save', saveModifyDetails);
	            this.listenTo(this, 'ProcureRequestLineCollection:fetched', processModifyDetailsView);
	            this.listenTo(Gloria.ProcurementApp, 'procureLine:ModifyDetails:cancel', cancelModifyDetails);
			},

			control : function(ids) {
				inputIds = ids;
				actionType = this.getURLParameter('type');
				hasReferer = !!this.getURLParameter('from');
				modificationId = this.getURLParameter('modificationId');
				prepareModifyDetailsPartInfo.call(this);
			},
			
			onDestroy: function() {
			    inputIds = null;
			    actionType = null;
			    modifyDetailsView.destroy();
		        modifyDetailsView = null;
		        selectedInfo = null;
		        hasReferer = null;
			}
		});
	});

	return Gloria.ProcurementApp.Controller.ModifyDetailsController;
});
