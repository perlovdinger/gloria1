define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'jquery-validation',
	    'hbs!views/warehouse/qualityinspection/overview/view/edit-part-info-modal'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, compiledTemplate) {
    
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.QIEditPartInfoModalView = Marionette.LayoutView.extend({
	    	
			className : 'modal',

			id : function() {
				return 'QIMultipleUpdateModal_' + this.cid;
			},
			
			template: compiledTemplate,
			
	    	events : {
	    		'click #save' : 'handleSaveClick',
	    		'click #cancel' : 'handleCancelClick',
	    		'hidden.bs.modal': 'onHide'
	        },
	        
	        initialize : function(options) {
	        	this.selectedModels = options.selectedModels;
	        },
			
	        getTransportLabelId : function() {
                return this.$el.find('#view-select').val();
            },
            
	        handleSaveClick : function(e) {
	    		e.preventDefault();
	    		if(this.selectedModels) {
	    			var transportLabelId = this.getTransportLabelId();
	    			 Gloria.WarehouseApp.trigger('TransportLabel:multiupdate',this.selectedModels,transportLabelId);
	    			_.each(this.selectedModels, function(model) {
	    				model.trigger('backgrid:select', model, false);
	    			});
	    			this.$el.modal('hide');
	    			Gloria.WarehouseApp.trigger('OverviewButton:refresh');
	    		};
			},

	        handleCancelClick : function(e) {
	        	this.$el.modal('hide');
	        },
	        
	        renderTransportLabelSelector : function(options) {
	        	var ret = "<option value='' " + ">" + Handlebars.helpers.t('Gloria.i18n.warehouse.qualityInspection.qioverview.editModal.selectTransportLabel') + "</option>";
        		$.each(options, function(i, item) {
        			ret += "<option value='" + item.id + "'>" + item.code + "</option>";
				});
                return ret;
            },
	        
	        render : function() {				
				this.$el.html(this.template({
					items : this.collection ? this.collection.toJSON() : [],
					'renderTransportLabelSelector' : this.renderTransportLabelSelector
				}));
				this.$el.modal({
					show : false
				});	
				return this;
			},
			
			onShow : function() {
				this.$el.modal('show');
				//this.collection.on('add remove change', this.render);
			},
			
			onHide: function() {
                Gloria.trigger('reset:modellayout');                
            },

			onDestroy : function() {
                this.$el.modal('hide');
                this.$el.off('.modal');
            }
	    });
	});
	
    return Gloria.WarehouseApp.View.QIEditPartInfoModalView;
});