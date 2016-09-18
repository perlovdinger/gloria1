define(['utils/typeahead/TypeaheadView', 'utils/UserHelper'], function(TypeaheadView, UserHelper) {
	
	var BinLocationTypeaheadView = TypeaheadView.extend({
	    
	    initialize: function(options) {
	        this.defaultSelect = options.defaultSelect;
	        this.disable = options.disable;
	        this.modelAttrName = options.modelAttrName || 'binlocation';
	        this.callback = options.callback;
	        this.triggerInitially = options.triggerInitially === false ? false : true; 
	        return TypeaheadView.prototype.initialize.call(this, options);
	    },
	    
		key: 'code',
		url: function() {
			return '/warehouse/v1/binlocations';
		},
		cachePrefix: function() {
			return 'tpah.binlocation.' + UserHelper.getInstance().getDefaultWarehouse() + '.';
		},
		resultMap: {
			id: 'id',
			text: 'code'
		},
		
		events: {
            'change': 'onChange'
        },
        
        onChange: function(e) {
            if(!this.model) return;
            e.stopImmediatePropagation();
            this.model.set(this.modelAttrName, e.val, {silent : true});
            this.callback && this.callback();
        },		
		
		// Override
		searchTerm: function(term) {
			var data = {};
			if(this.key) {
				data[this.key] = '*' + term;// search term
			}
			return data;
		},
		
		// Override to put the default
		generateResult: function(data, page, query) {
		    //data = data.sort(this.sortItemsCallback);
	        return TypeaheadView.prototype.generateResult.call(this, data, page, query);
		},	
		
		//Override to put required parameters into the data hash.
		loadData: function(options) {
		    try {
		        var userSite;
		        if((userSite = UserHelper.getInstance().getDefaultWarehouse())) {
		            options.data.whSiteId = userSite;
		        }
		    } catch (e) {}
		    options.data.zoneType = 'STORAGE';
		    return TypeaheadView.prototype.loadData.call(this, options);
		},
		/*
		sortItemsCallback: function(obj1, obj2) {	
		    if(!obj1.text || !obj2.text) return 0;
		    return obj1.text - obj2.text;		    
		},
		*/
		// Override
		onShow: function() {		    
		    TypeaheadView.prototype.onShow.call(this);		    
            if(this.defaultSelect) {                
                this.$el.select2('val', this.defaultSelect, this.triggerInitially);                
            }    
            
            if(this.disable){
            	 this.$el.select2('disable', true);  
            }
            
            return this;
		}
	});
	
	return BinLocationTypeaheadView;
});