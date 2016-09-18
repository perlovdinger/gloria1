define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'hbs!views/warehouse/receive/deliverynotelines/view/edit-part-info-modal'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, compiledTemplate) {
    
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.EditPartInfoModalView = Marionette.LayoutView.extend({
	    	
			className : 'modal',

			id : 'edit-part-modal',
			
	    	events : {
	    		'click #save' : 'handleSaveClick',
	    		'click #cancel' : 'handleCancelClick'
	        },
	        
	        initialize : function(options) {
	        	this.selectedModels = options.selectedModels;
	        	this.collection = options.collection;
	        	this.collection.off('add remove change');
	        },
			
	        getTransportLabelId : function() {
                return this.$el.find('#view-select').val();
            },
            
	        handleSaveClick : function(e) {
	    		this.$el.modal('hide');
		    	Gloria.WarehouseApp.trigger('DeliveryNoteLine:PartInfo:save', this.selectedModels, this.getTransportLabelId());
			},
	        
	        handleCancelClick : function(e) {
	        	this.$el.modal('hide');
	        },
	        
	        renderTLSelector : function(options) {
	        	var ret = "<option value='' " + ">" + Handlebars.helpers.t('Gloria.i18n.warehouse.receive.editInfo.selectTL') + "</option>";
        		$.each(options, function(i, item) {
        			ret += "<option value='" + item.id + "'>" + item.code + "</option>";
				});
                return ret;
            },
	        
	        render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					items : that.collection ? that.collection.toJSON() : [],
					'renderTLSelector' : that.renderTLSelector
				}));
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
			},
			
			onShow : function() {
				this.$el.modal('show');
				this.collection.on('add remove change', this.render);
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.WarehouseApp.off(null, null, this);
			}
	    });
	});
	
    return Gloria.WarehouseApp.View.EditPartInfoModalView;
});
