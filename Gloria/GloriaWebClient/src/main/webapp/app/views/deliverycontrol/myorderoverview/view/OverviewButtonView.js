define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'hbs!views/deliverycontrol/myorderoverview/view/overview-button'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.OverviewButtonView = Marionette.ItemView.extend({
					
			initialize : function(options) {
				this.module = options.module;
				this.listenTo(Gloria.DeliveryControlApp, 'myOrderOverview:selectGridLine', this.gridClickHandler);
				this.listenTo(Gloria.DeliveryControlApp, 'OverviewButton:refresh', this.refreshThis);
			},
			
			events : {
				'click #clear-filter' : 'clearFilter',
	            'click #open-button' : 'handleOpenButtonClick',
	            'click #adjust-button' : 'handleAdjustButtonClick',	            
	            'click #edit-button' : 'handleEditButtonClick',
	            'click #exportExcel-button' : 'handleExportExcelButtonClick'
	        },
	        
	        clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
	        
	        defalutPermittedActions: {				
	        	open : false,
                edit : false,
                adjust : false
            },
            
	        gridClickHandler : function(selectedRows, orderLineIds, isExpectedArrivalEditable) {
	            this.selectedRows = selectedRows;
	            this.orderLineIds = orderLineIds;
	            this.isExpectedArrivalEditable = isExpectedArrivalEditable;
	            this.permittedActions = _.clone(this.defalutPermittedActions);
	            if (this.selectedRows && this.selectedRows.length == 1) {
					this.permittedActions['open'] = true;
					this.permittedActions['adjust'] = true;
				} else {
					delete this.permittedActions['open'];
					delete this.permittedActions['adjust'];
				}
	            if (this.selectedRows && this.selectedRows.length >= 1) {
	            	this.permittedActions['edit'] = true;
	            } else {
	            	delete this.permittedActions['edit'];
	            }
				this.render();
	        },
	        
	        refreshThis : function() {
				this.permittedActions = {};
				this.render();
			},
	        
	        handleOpenButtonClick : function(e) {	        	
	            Backbone.history.navigate('deliverycontrol/myOrderOverview/' + this.module + '/orderLineDetail/' + this.orderLineIds, {
	                trigger : true
	            });
	        },
	        
	        handleEditButtonClick : function(e) {
	        	Gloria.trigger('hideAppMessageView');
	        	Gloria.DeliveryControlApp.trigger('MyOrderOverviewEditView:show', this.selectedRows);
	        },
	
	        handleAdjustButtonClick : function(e) {
	        	Gloria.trigger('hideAppMessageView');
				Gloria.DeliveryControlApp.trigger('MyOrderOverview:showDeliveryAdjustModalView', _.first(this.selectedRows));
			},
			
			handleExportExcelButtonClick : function() {
				Gloria.trigger('hideAppMessageView');
				Gloria.DeliveryControlApp.trigger('MyOrderOverview:ExportExcel:show', this.selectedRows);
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					permittedActions : this.permittedActions || this.defalutPermittedActions
				}));
				return this;
			}
		});
	});
	
	return Gloria.DeliveryControlApp.View.OverviewButtonView;
});
