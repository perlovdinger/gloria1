define(['app',
        'jquery',
        'i18next',
        'underscore',
		'handlebars', 
		'marionette',
		'utils/dialog/dialog',
		'hbs!views/warehouse/qualityinspection/overview/view/overview-button'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Dialog, compiledTemplate) {
    
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.OverviewButtonView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
	        	this.module = options.module;
	            this.permittedActions = {}; 
	            this.listenTo(Gloria.WarehouseApp, 'qualityInspection:mandatoryGrid:select', this.update);
	            // Optional Module also used in 'inStock' module
	            this.listenTo(Gloria.WarehouseApp, 'qualityInspection:optionalGrid:select', this.handleOptionalGridSelect);
	            
	            // Set Marking Module
	            this.listenTo(Gloria.WarehouseApp, 'qualityInspection:setMarkingGrid:select', this.handleSetMarkingGridSelect);
	            this.listenTo(Gloria.WarehouseApp, 'QIOverview:setMarking:markAsOptional:invalid', this.inValidSelectionMarkAsOptional);
	            this.listenTo(Gloria.WarehouseApp, 'QIOverview:setMarking:unmark:invalid', this.inValidSelectionUnmark);
	            
	            this.listenTo(Gloria.WarehouseApp, 'OverviewButton:refresh', this.refreshThis);
	            
	            this.listenTo(Gloria.WarehouseApp, 'QI:DeliveryNoteLine:start:update', this.deliveryNoteLinesStartUpdate);
	            this.listenTo(Gloria.WarehouseApp, 'QI:DeliveryNoteLine:complete:update', this.deliveryNoteLinesCompleteUpdate);
	        },
	        
	        refreshThis : function() {
				this.permittedActions = {};
				this.render();
			},
			
			deliveryNoteLinesStartUpdate: function() {			    
			    this.$('#approve-button').attr('disabled', true);
			},
			
			deliveryNoteLinesCompleteUpdate: function(model) {			    
			    this.$('#approve-button').removeAttr('disabled');
            },
			
			events : {
				'click #clear-filter' : 'clearFilter',
				'click #approve-button' : 'approve',
				'click #edit-button' : 'multipleUpdate',
				'click #addInformation-button' : 'addInformation',
				'click #markForInspection-button' : 'markForInspection',
				'click #unmark-button' : 'unmark',
				'click #markAsMandatory-button' : 'markAsMandatory',
				'click #markAsOptional-button' : 'markAsOptional',
				'click #createTransportLabel-button' : 'handleCreateTransportLabelClick',
				'click #print-menu li a' : 'handlePrintClick'
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
			
			handleCreateTransportLabelClick: function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('qualityInspection:createTransLabel');
			},
			
			handlePrintClick: function(e) {
				e.preventDefault();
				switch (e.currentTarget.id) {
				case 'printTL':
					Gloria.WarehouseApp.trigger('qualityInspection:printTL');
					break;
				default:
					break;
				};
			},
			
			approve: function(e) {
				Gloria.WarehouseApp.trigger('approve:materialLineQI', this.collection); // Can it be done the way addInformation/unmark event is handled?
																						// by adding module name to differentiate event name?
			},
			
			multipleUpdate: function(e) {
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('show:multipleUpdate', this.selectedModels); // Here also ?			
			},
			
			addInformation : function(e) {
			    var thisId;
			    if((this.module == 'optional') || (this.module == 'inStock')) {
			        thisId = _.first(this.selectedModels).get('deliveryNoteLineId');
			    } else {
			        thisId = _.first(this.selectedModels).get('id');
			    }
				Gloria.WarehouseApp.trigger('QIOverview:addInformation', thisId);
			},

			markForInspection : function(e) {
				Gloria.WarehouseApp.trigger('QIOverview:' + this.module + ':markForInspection', this.selectedModels);
			},

			unmark : function(e) {
				Gloria.WarehouseApp.trigger('QIOverview:' + this.module + ':unmark', this.selectedModels);
			},
			
			markAsMandatory : function(e) {
				Gloria.WarehouseApp.trigger('QIOverview:' + this.module + ':markAsMandatory', this.selectedModels);
			},
			
			markAsOptional : function(e) {
				Gloria.WarehouseApp.trigger('QIOverview:' + this.module + ':markAsOptional', this.selectedModels);
			},
			
			inValidSelectionMarkAsOptional : function() {
				Gloria.trigger('showAppMessageView', {
	    			type : 'error',
	    			message : new Array({
	    				message : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.validation.inValidSelectionMarkAsOptional')
	    			})
	    		});
			},
			
			inValidSelectionUnmark : function() {
				Gloria.trigger('showAppMessageView', {
	    			type : 'error',
	    			message : new Array({
	    				message : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.validation.inValidSelectionUnmark')
	    			})
	    		});
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					permittedActions : this.permittedActions
				}));
				return this;
			},
			
			handleOptionalGridSelect : function(selectedModels) {
				this.selectedModels = selectedModels;
				if(!selectedModels) return;
				
				if(selectedModels.length == 1) {
					this.permittedActions['addInformation'] = true;
				} else {
					delete this.permittedActions['addInformation'];
				}
				
				if(selectedModels.length >= 1) {
					this.permittedActions['markForInspection'] = true;
					this.permittedActions['unmark'] = true;
				} else {
					delete this.permittedActions['markForInspection'];
					delete this.permittedActions['unmark'];
				}
				
				// For Optional unmark can be done partially!
				if(this.module === 'optional' || this.module === 'inStock') {
					if(selectedModels.length == 1 && _.first(selectedModels).get('markedForInspection')) {
						this.permittedActions['unmark'] = true;
					} else {
						delete this.permittedActions['unmark'];
					}
				}
				this.render();
			},
	
			handleSetMarkingGridSelect : function(selectedModels) {
				this.selectedModels = selectedModels;
				if(!selectedModels) return;
				if(selectedModels.length >= 1) {
					this.permittedActions['markAsMandatory'] = true;
					this.permittedActions['markAsOptional'] = true;
					this.permittedActions['unmark'] = true;
				} else {
					delete this.permittedActions['markAsMandatory'];
					delete this.permittedActions['markAsOptional'];
					delete this.permittedActions['unmark'];
				}
				this.render();
			},
			
			update: function(selectedModels) {
				this.selectedModels = selectedModels;
				if(selectedModels && selectedModels.length) {
					this.permittedActions['multipleUpdate'] = true;					
				} else {
					delete this.permittedActions['multipleUpdate'];					
				}	
				
				if(selectedModels.length == 1) {
					this.permittedActions['addInformation'] = true;
				} else {
					delete this.permittedActions['addInformation'];
				}
				this.render();
			},
			
			onDestroy : function() {
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});
	
	return Gloria.WarehouseApp.View.OverviewButtonView;
});
