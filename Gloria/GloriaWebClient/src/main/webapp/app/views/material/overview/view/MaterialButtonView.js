define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'utils/backbone/GloriaCollection',
        'utils/UserHelper',
		'hbs!views/material/overview/view/material-button'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, GloriaCollection, UserHelper, compiledTemplate) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MaterialButtonView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.listenTo(Gloria.MaterialApp, 'MaterialOverview:selectGridLine', this.gridClickHandler, this);
	            this.listenTo(Gloria.MaterialApp, 'MaterialLineGrid:ResetButtons', this.resetButtons);
			},
			
			regions : {
				filter : '#filter'
			},
			
			events : {
				'click #clear-filter' : 'clearFilter',
				'click #open-button' : 'handleOpenButtonClick',
				'click #create-button' : 'handleCreateButtonClick',
				'click #release-part-button' : 'handleReleasePartButtonClick',
				'click #borrow-part-button' : 'handleBorrowPartButtonClick',
				'click #scrap-part-button' : 'handleScrapPartButtonClick',
				'click #pull-part-button' : 'handlePullPartButtonClick',
				'click #addtoRL-button' : 'handleAddtoRLButtonClick'
			},

			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},

			resetButtons : function() {
				this.$('#open-button').attr('disabled', true);
				this.$('#create-button').attr('disabled', true);
				this.$('#release-part-button').attr('disabled', true);
				this.$('#borrow-part-button').attr('disabled', true);
				this.$('#scrap-part-button').attr('disabled', true);
				this.$('#pull-part-button').attr('disabled', true);
			},
			
			gridClickHandler : function(selectedModels) {
				var that = this;
				this.selectedModels = selectedModels;
				
				// Open
				if (selectedModels && selectedModels.length == 1) {
					this.$('#open-button').removeAttr('disabled');
				} else {
					this.$('#open-button').attr('disabled', true);
				}
				
				// Create Request List
				if (selectedModels && selectedModels.length != 0 && this.isValidSelection(selectedModels)) {
					this.$('#create-button').removeAttr('disabled');
				} else {
					this.$('#create-button').attr('disabled', true);
				}
				
				// Add to Request List
				if (selectedModels && selectedModels.length != 0 && this.isValidSelection(selectedModels)) {
					this.$('#addtoRL-button').removeAttr('disabled');
				} else {
					this.$('#addtoRL-button').attr('disabled', true);
				}
			

				// Release Part 
				//GLO-6891
                var allowedStatusForRelease = [ 'ORDER_PLACED_INTERNAL', 'ORDER_PLACED_EXTERNAL', 'READY_TO_STORE', 'STORED', 'QI_OK' ];
                var isRelease = false;
				if (selectedModels && selectedModels.length != 0) {
					$.each(selectedModels, function(i, model) {
						 if(_.contains(allowedStatusForRelease, model.get('status')) && (model.get('materialType') != 'RELEASED')) {
							 isRelease = true;				        						       		            
				        } else {
				        	isRelease = false;
				            return false;
				        }
					});
				}
				
				if (isRelease) {
					this.$('#release-part-button').removeAttr('disabled');
				} else {
					this.$('#release-part-button').attr('disabled', true);
				}
				           
				// Borrow Part 
				var allowedStatusForBorrow = [ 'ORDER_PLACED_INTERNAL', 'ORDER_PLACED_EXTERNAL', 'BLOCKED', 'MARKED_INSPECTION', ' QTY_DECREASED', 'MISSING', 'DEVIATED' ];
				if (selectedModels && selectedModels.length == 1 && _.contains(allowedStatusForBorrow, _.first(selectedModels).get('status'))) {
					this.$('#borrow-part-button').removeAttr('disabled');
				} else {
					this.$('#borrow-part-button').attr('disabled', true);
				}
				
				/***
				 * Scrap -- NOT SUPPORTED UNLESS SCRAP IMPLEMENTED 
				 * 
				 * 
				var allowedMaterialTypeForScrap = [ 'USAGE', 'MODIFIED', 'ADDITIONAL', 'RELEASED'];
				var allowedMaterialStatusForScrap = [ 'RECEIVED', 'QI_READY', 'BLOCKED', 'QI_OK', 'READY_TO_STORE', 'STORED', 'MARKED_INSPECTION'];
				if (selectedModels && selectedModels.length > 0
						&& this.checkIfModelsContainValueFromList(selectedModels, 'materialType', allowedMaterialTypeForScrap)
						&& this.checkIfModelsContainValueFromList(selectedModels, 'status', allowedMaterialStatusForScrap)) {
					this.$('#scrap-part-button').removeAttr('disabled');
				} else {
					this.$('#scrap-part-button').attr('disabled', true);
				}*/
				
				// Pull
				var allowedStatusForPull = [ 'READY_TO_STORE', 'STORED' ];
				if (selectedModels && selectedModels.length == 1 && _.contains(allowedStatusForPull, _.first(selectedModels).get('status'))) {
					this.$('#pull-part-button').removeAttr('disabled');
				} else {
					this.$('#pull-part-button').attr('disabled', true);
				}
			},
			
			checkIfModelsContainValueFromList : function(models, attr, list) {
				var isThere = true;
				_.some(models, function(model) {
					isThere = _.contains(list, model.get(attr));
					return !isThere;
				});
				return isThere;
			},

			isValidSelection : function(selectedModels) {
				var tempCollection = new GloriaCollection(selectedModels);
				var outboundLocationIdList = tempCollection.pluck('outBoundLocationId');
				var whSiteIdList = tempCollection.pluck('whSiteId');
				var statusList = tempCollection.pluck('status');
				var reqlistIdList = tempCollection.pluck('requestListID');
 				if (this.hasRequestListId(reqlistIdList) && this.allAreSame(outboundLocationIdList) && this.allAreSame(whSiteIdList)) {
        			// REQUESTER_FOR_PULL can also do Request for Pull even if the status is MARKED_INSPECTION
			        var checkStatus = function(array) {
				        return array.every(function(element) {
		                    return element === 'READY_TO_STORE' || element === 'STORED'
		                        || element === 'ORDER_PLACED_INTERNAL' || element ===  'ORDER_PLACED_EXTERNAL'
		                        || (UserHelper.getInstance().hasUserRole('REQUESTER_FOR_PULL') && UserHelper.getInstance().hasUserRole('WH_QI') && element === 'MARKED_INSPECTION'); // GLO-6625 : only PSQA(Combination)
		                });
			        };
			        return checkStatus(statusList);
				}
				return false;
			},
			
			hasRequestListId :function(array) {
				return (_.contains(array,0) || _.contains(array,null) || _.contains(array,undefined)) ;
			},
			
			allAreSame : function(array) {
				var first = array[0];
				return array.every(function(element) {
					return element == first;
				});
			},

			handleCreateButtonClick : function(e) {
				e.preventDefault();
				var ids = '';
				_.each(this.selectedModels, function(model) {
					ids = (ids ? ids + ',' : '') + model.id;
				});
				Backbone.history.navigate('material/linesoverview/requests/' + ids, {
					trigger : true
				});
			},

			handleOpenButtonClick : function(e) {
				e.preventDefault();
				Backbone.history.navigate('material/linesoverview/linedetails/' + _.first(this.selectedModels).id, {
					trigger : true
				});
			},
			
			handleReleasePartButtonClick : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.MaterialApp.trigger('MaterialRequestList:releasePart', this.selectedModels);
			},

			handleBorrowPartButtonClick : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.MaterialApp.trigger('MaterialRequestList:borrowPart:show', _.first(this.selectedModels));
			},

			handleScrapPartButtonClick : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.MaterialApp.trigger('MaterialRequestList:scrapPart:show', this.selectedModels);
			},
			
			handlePullPartButtonClick : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.MaterialApp.trigger('MaterialRequestList:pullPart:show', _.first(this.selectedModels));
			},
			
			handleAddtoRLButtonClick :function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.MaterialApp.trigger('MaterialRequestList:AddToReqList:show', this.selectedModels);
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.permittedActions
				}));
				return this;
			},

			onDestroy : function() {
				Gloria.MaterialApp.off(null, null, this);
			}
		});
	});

	return Gloria.MaterialApp.View.MaterialButtonView;
});
