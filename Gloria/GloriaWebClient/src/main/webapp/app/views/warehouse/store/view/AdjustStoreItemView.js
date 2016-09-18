define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'bootstrap',
		'i18next',		
		'utils/typeahead/BinLocationTypeaheadView',	
		'hbs!views/warehouse/store/view/adjust-store-item'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, i18n,  BinLocationTypeaheadView, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AdjustStoreItemView = Marionette.LayoutView.extend({
		    
		    initialize: function(options) {
		        options || (options = {});
		        this.index = options.index;
		        this.currentSplitCount = options.currentSplitCount;
		    },
		    
		    modelEvents: {
		        'change': "modelChanged"
		    },    
		    
		    events: {
		        'change input[id^="storedQuantity"]': 'updateExpectedQuantity',	
		        'change input[id^="binLocationContainer"]': 'updateBinLocation',
		        'click a[id^="adjust"]' : 'handleAdjustClick',
                'click a[id^="remove"]' : 'handleRemoveClick',
		    },
		    
		    modelChanged: function(model) {		        
		        this.$('input[id^="storedQuantity"]').val(model.get('storedQuantity'));		        
		    },
		    
		    updateExpectedQuantity: function(e) {
		        var value = e.target.value;
		        this.model.set('quantity', parseInt(value));
		        this.model.set('storedQuantity', value);		        		        
		    },
		    
		    updateBinLocation: function(e) {
		        var value = e.target.value || (e.added && e.added.text);	        
		        this.model.set('binlocation', value);
		        this.model.set('binLocationCode', e.added.text);
		    },
		    
		   
		    handleAdjustClick : function(e) {
                e.preventDefault();
                var clonedModel = this.model.clone();
                //clonedModel.unset('id', {silent:true}); // Unsetting id creates issue in grid!
                this.currentSplitCount = this.currentSplitCount + 1;
                this.model.collection.currentSplitCount = this.currentSplitCount;
                clonedModel.set('id', this.model.get('id') + '_' + this.currentSplitCount, {silent:true});
                clonedModel.unset('storedQuantity', {silent:true});
                clonedModel.unset('binlocation', {silent:true});
                this.model.collection.add(clonedModel);
            },
            
            handleRemoveClick : function(e) {
                e.preventDefault();
                this.model.collection.remove(this.model);
            },
	    	
	    	id : function() {
				return 'store' + this.model.cid;
			},
	    	
	    	render : function() {
			 	this.$el.html(compiledTemplate({ 
	 				'data': this.model ? this.model.toJSON() : {},
	 				'cid': this.model.cid,
	 				first: (this.index == 0)
	 		    }));			 		 	
			    return this;
			},
			
			onShow : function() {	
				var regionName = 'binLocationSelectorContainer' + this.model.cid;
            	if(!this[regionName]) {
        			this.addRegion(regionName, '#' + regionName);
            	}
    			this[regionName].show(new BinLocationTypeaheadView({
					el : this.$('#binLocationContainer'+this.model.cid),					
					select2Options: {
					    dropdownAutoWidth: true
					}
    			}));
			}
		});
	});

	return Gloria.WarehouseApp.View.AdjustStoreItemView;
});
