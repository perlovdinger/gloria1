define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
        'i18next',
		'hbs!views/procurement/overview/view/overview-button',
		'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Marionette, i18n, compiledTemplate, UserHelper) {
    
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.OverviewButtonView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
	        	this.module = options.module;
	        	this.hasRolePI = options.hasRolePI;
	        	this.isIPRole = options.hasRolePI?"yes":"no";
	            this.permittedActions = {}; 
	            this.listenTo(Gloria.ProcurementApp, 'procurelineGrid:select procureRequestLineGrid:select', this.procurelineGridSelectHandler);	            
	            this.listenTo(Gloria.ProcurementApp, 'changelineGrid:select', this.changelineGridSelectHandler);
	            this.listenTo(Gloria.ProcurementApp, 'procurelineGrid:ResetButtons', this.resetButtons);
	        },
			
			events : {
				'click #clear-filter' : 'clearFilter',
				'click #open-button' : 'openProcurelineDetail',
				'click #change-open-button' : 'openChangeDetail',
				'click #modify-button' : 'groupProcureRequestLines',
				'click #returnProcurement-button' : 'returnProcurement',
				'click #forwardProcurement-button' : 'forwardProcurement',
				'click #tomodify-button' : 'handleToModify',
				'click #onbuildsite-button' : 'handleOnBuildSite',
				'click #buildSiteAssignToProcure-button' : 'assignToProcureFromBuildSite',				
				'click #assignToProcure-button' : 'assignToProcure',
				'click #delegateOnBuildSite-button' : 'delegateOnBuildSite',
				'click #assign-button' : 'handleAssignButtonClick',
				'click #unassign-button' : 'handleUnAssignButtonClick',
				'click #markAsMine-button' : 'handleMarkAsMineButtonClick',
				'click #multipleUpdate-button' : 'handlemultipleUpdateClick',
				'click #procure-button' : 'handleProcureClick',
				'click #exportExcel-button' : 'handleExportExcelButtonClick',
				'click #EDITED' : 'handleMaterialLineTypeClick',
				'click #NOT_EDITED' : 'handleMaterialLineTypeClick',
				'click #ALL' : 'handleMaterialLineTypeClick'/*,
				'click #FROM_STOCK' : 'handleMaterialLineTypeClick',
				'click #RETURNED' : 'handleMaterialLineTypeClick'*/
			},
			
			
			handleMaterialLineTypeClick: function(e) {
				$('#materialLineCategory-button').html(e.currentTarget.text + ' <span class="caret"></span>');
				$('#materialLineCategory-button').val(e.currentTarget.text);
				e.preventDefault();
				Gloria.ProcurementApp.trigger('ToProcure:MaterialLineType:show', e.currentTarget.id);
			},
			
			 materialLineCategory : 
				 [{
						'ALL' : 'Gloria.i18n.procurement.materialLineCategory.ALL'
					},
					{
						'EDITED' : 'Gloria.i18n.procurement.materialLineCategory.EDITED'
					},
					{
						'NOT_EDITED' : 'Gloria.i18n.procurement.materialLineCategory.NOTEDITED'
					}/*,
					{
						'FROM_STOCK' : 'Gloria.i18n.procurement.materialLineCategory.FROM_STOCK'
					},
					{
						'RETURNED' : 'Gloria.i18n.procurement.materialLineCategory.RETURNED'
					}*/],
				
			handleExportExcelButtonClick : function(){
			    Gloria.trigger('hideAppMessageView');
			    Gloria.ProcurementApp.trigger('ToProcure:ExportExcel:show', this.selectedRows);
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
			
			handleAssignButtonClick : function() {
				Gloria.trigger('hideAppMessageView');
		        Gloria.ProcurementApp.trigger('showProcureRequestAssignView', this.selectedModels, _.first(this.selectedModels).get('companyCode'));
	        },
	        
	        areAllSameCompanyCodes : function (array) { // array -> list of models
			    var first = _.first(array).get('companyCode');
			    return array.every(function(element) {
			        return element && element.get('companyCode') === first;
			    });
			},
	        
	        handleUnAssignButtonClick: function(){
	        	Gloria.ProcurementApp.trigger('unAssignMaterialRequest', this.selectedModels);
	        },
	        
	        returnProcurement : function(e) {
	        	Gloria.ProcurementApp.trigger('procureline:return', this.selectedRows);
			},
	        
			forwardProcurement : function(e) {
				Gloria.trigger('hideAppMessageView');
				Gloria.ProcurementApp.trigger('procureline:forward:show', this.selectedRows);
			},
			
			handleMarkAsMineButtonClick : function() {
				Gloria.ProcurementApp.trigger('procureline:markAsMine', this.selectedRows);
			},
			
			handlemultipleUpdateClick : function() {
				Gloria.trigger('hideAppMessageView');
				Gloria.ProcurementApp.trigger('procureline:multipleupdate:show', this.selectedRows);
			},
			
			handleProcureClick : function() {
				Gloria.ProcurementApp.trigger('procureline:procure', this.selectedRows);
			},
			
			openProcurelineDetail : function() {				
				if(this.selectedRows) {
					var selectedModel = _.first(this.selectedRows);
					var procureId;
					if(selectedModel.id){
						//Navigating from Procure Tab
						procureId = selectedModel.id;
					}else{
						//Navigate from ToProcure Tab
						procureId = selectedModel;
					}
					Gloria.ProcurementApp.trigger('procurelineDetails:show', procureId);
				}
			},
			
			openChangeDetail : function(){
				if(this.selectedRows) {
					var changeId = _.first(this.selectedRows).id;
					Gloria.ProcurementApp.trigger('changelineDetails:show', changeId);
				}
			},
			
			groupProcureRequestLines : function(){
				if(this.selectedRows) {					
					Gloria.ProcurementApp.trigger('modifyDetails:modify', this.selectedRows, 'material');
				}
			},
			
			handleToModify: function() {
				if(this.selectedRows) {
					Gloria.ProcurementApp.trigger('procureline:tomodify', this.selectedRows, 'procureLine');
				}
			},
			
			handleOnBuildSite: function() {
				if(this.selectedRows) {
					Gloria.ProcurementApp.trigger('procureline:onbuildsite', this.selectedRows);
				}
			},
			
			assignToProcureFromBuildSite : function() {
				if(this.selectedRows && this.selectedRows.length > 0) {					
					Gloria.ProcurementApp.trigger('procureline:assignToProcure', this.selectedRows, 'ONBUILDSITE');
				}
			},
			
			assignToProcure : function() {
				if(this.selectedRows && this.selectedRows.length > 0) {					
					Gloria.ProcurementApp.trigger('procureline:assignToProcure', this.selectedRows, 'PROCURED');
				}
			},
			
			delegateOnBuildSite : function() {				
				Gloria.ProcurementApp.trigger('procureline:delegateOnBuildSite', this.selectedRows);
			},
	        
	        resetButtons : function() {
	        	this.permittedActions = {};
	        	this.render();
			},
	        
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					isIP : this.hasRolePI,
					isIPRole : this.isIPRole,
					permittedActions : this.permittedActions,
					materialLineCategory: this.materialLineCategory,
					procurelinesIDs: (this.selectedRows || []).join(',') 
				}));
				
  		        var tooltipText= i18n.t('Gloria.i18n.procurement.unassignedRequest.header.unassignHoverText');
  		        var btnName = $(this.$el.find('#unassign-button'));
  		        btnName.attr("title", tooltipText);
		        btnName.attr("data-toggle","tooltip");	        
		        btnName.tooltip();

				return this;
			},
			
			changelineGridSelectHandler : function(selectedRows) {	
				if(selectedRows) {					
					this.selectedRows = selectedRows;
					if(this.module.toUpperCase() == 'CHANGE' && selectedRows.length == 1){
						this.permittedActions['openChangeDetails'] = true;
					} else {
						delete this.permittedActions['openChangeDetails'];	
					}
				} else {
					delete this.selectedRows;
				} 
				this.render();
			},
			
			isAllowedProcureType : function(array) {
			    return array.every(function(element) {
			    	var procureType = element.get('procureType');
			        return  procureType == 'INTERNAL' || procureType == 'INTERNAL_FROM_STOCK';
			    });
			},
			
			isValidSelectionToUnAssign : function(array) {
				return array.every(function(model) {
			        return (model.get('assignedMaterialControllerId') || model.get('procureForwardedId')) == UserHelper.getInstance().getUserId() && model.get('unAssignable') !== false;
			    });
			},
			
			isMultipleUpdateAllowed : function(models) {
			    var isAllowed = true;
                _.each(models, function(model) {
                    if(model.get('procureType') == 'FROM_STOCK') {
                        isAllowed = false;
                    }
                });
                return isAllowed;
            },
			
			procurelineGridSelectHandler : function(selectedRows, selectedModels) {
				this.selectedModels = selectedModels;
				if (selectedRows) {
					this.selectedRows = selectedRows;
					switch (this.module.toUpperCase()) {
					case 'TOPROCURE':
						if (selectedRows.length == 1) {
							this.permittedActions['openProcureDetails'] = true;
							this.permittedActions['tomodify'] = true;
							this.permittedActions['returnProcurement'] = true;
							this.permittedActions['onbuildsite'] = true;
							if(this.isAllowedProcureType(selectedRows)) {
								this.permittedActions['forwardProcurement'] = true;
							} else {
								delete this.permittedActions['forwardProcurement'];
							}
							if(this.isMultipleUpdateAllowed(this.selectedRows)) {
							    this.permittedActions['multipleUpdate'] = true;
							} else {
							    delete this.permittedActions['multipleUpdate'];
							}
							this.permittedActions['procure'] = true;
						} else if (selectedRows.length > 1) {
							delete this.permittedActions['openProcureDetails'];
							this.permittedActions['tomodify'] = true;
							this.permittedActions['returnProcurement'] = true;
							this.permittedActions['onbuildsite'] = true;
							if(this.isAllowedProcureType(selectedRows)) {
								this.permittedActions['forwardProcurement'] = true;
							} else {
								delete this.permittedActions['forwardProcurement'];
							}
							if(this.isMultipleUpdateAllowed(this.selectedRows)) {
                                this.permittedActions['multipleUpdate'] = true;
                            } else {
                                delete this.permittedActions['multipleUpdate'];
                            }
							this.permittedActions['procure'] = true;
						} else {
							delete this.permittedActions['openProcureDetails'];
							delete this.permittedActions['tomodify'];
							delete this.permittedActions['returnProcurement'];
							delete this.permittedActions['forwardProcurement'];
							delete this.permittedActions['multipleUpdate'];
							delete this.permittedActions['procure'];
							delete this.permittedActions['onbuildsite'];
						}
					break;
					case 'MODIFICATION':
						if (selectedRows.length > 0) {
							var allStatusAreUSAGE = function(array) {
								return array.every(function(element) {
									return element.get('materialType') == 'USAGE';
								});
							};
							if (allStatusAreUSAGE(selectedRows)) {
								this.permittedActions['import'] = true;
							} else {
								delete this.permittedActions['import'];
							}
						} else {
							delete this.permittedActions['import'];
						}
					break;
					case 'ONBUILDSITE':
						if (selectedRows.length > 0) {
							this.permittedActions['buildSiteAssignToProcure'] = true;
							this.permittedActions['delegateOnBuildSite'] = true;
						} else {
							delete this.permittedActions['buildSiteAssignToProcure'];
							delete this.permittedActions['delegateOnBuildSite'];
						}
					break;
					case 'PROCURED':
						if (selectedRows.length == 1) {
							this.permittedActions['openProcureDetails'] = true;
							this.permittedActions['assignToProcure'] = true;
						} else {
							delete this.permittedActions['openProcureDetails'];
							delete this.permittedActions['assignToProcure'];
						}
						if (selectedRows.length >= 1) {
							this.permittedActions['assignToProcure'] = true;
						} else {
							delete this.permittedActions['assignToProcure'];
						}
						var isToProcurePossible = function() {
						    var flag = true;
						    _.each(selectedRows, function(thisRow) {
						    	if((thisRow.get('status') == 'PLACED' && (thisRow.get('procureType') == 'EXTERNAL' || thisRow.get('procureType') == 'EXTERNAL_FROM_STOCK'))
						    			|| thisRow.get('status') == 'RECEIVED' || thisRow.get('status') == 'RECEIVED_PARTLY') {
						    		flag = false;
						    		return false;
						    	}
                            });
						    return flag;
                        };
                        if(selectedRows.length && isToProcurePossible()) {
                            this.permittedActions['assignToProcure'] = true;
                        } else {
                            delete this.permittedActions['assignToProcure'];
                        }
					break;
					case 'CHANGE':
						if (selectedRows.length == 1) {
							this.permittedActions['openChangeDetails'] = true;
						} else {
							delete this.permittedActions['openChangeDetails'];
						}
					break;
					case 'INBOX':
						if (selectedRows.length > 0) {
							if(this.areAllSameCompanyCodes(this.selectedModels)) {
								this.permittedActions['assign'] = true;
							} else {
								delete this.permittedActions['assign'];
							}
							if(this.isValidSelectionToUnAssign(this.selectedModels)) {
								 this.permittedActions['unAssign'] = true;
							} else {
								delete this.permittedActions['unAssign'];
							}
						} else {
							delete this.permittedActions['assign'];
							delete this.permittedActions['unAssign'];
						}
					break;
					case 'INTERNALPROCUREMENT':
					    if (selectedRows.length > 0) {
					    	if(this.areAllSameCompanyCodes(this.selectedModels)) {
								this.permittedActions['assign'] = true;
							} else {
								delete this.permittedActions['assign'];
							}
							if(this.isValidSelectionToUnAssign(this.selectedModels)) {
								 this.permittedActions['unAssign'] = true;
							} else {
								delete this.permittedActions['unAssign'];
							}
                            this.permittedActions['assign'] = true;
                        } else {
                            delete this.permittedActions['assign'];
                            delete this.permittedActions['unAssign'];
                        }
					    /*
						if (selectedRows.length >= 1) {
							this.permittedActions['markAsMine'] = true;
						} else {
							delete this.permittedActions['markAsMine'];
						}
						*/
					break;
					default:
						break;
					}
				} else {
					delete this.selectedRows;
				}
				this.render();
			},
			
			onDestroy : function() {
				Gloria.ProcurementApp.off(null, null, this);
			}
		});
	});
	
	return Gloria.ProcurementApp.View.OverviewButtonView;
});
