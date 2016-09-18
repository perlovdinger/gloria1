define(['app',
	    'jquery',    
	    'underscore',
        'backbone',
        'marionette',
        'utils/typeahead/Select2View',
        'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Backbone, Marionette, Select2View, GloriaCollection) {
	
	var PurchaseOrgSelector = Select2View.extend({
		
		resultMap: {
			id: 'organisationCode',
			text: 'organisationName'
		},
		
		initialize: function(options) {
			this.disabled = options.disabled;
			Select2View.prototype.initialize.apply(this, arguments);
			this.resultMap = options.resultMap || this.resultMap;	
			this.select2Options = options.select2Options || this.select2Options || {};
			this.collection = new GloriaCollection([]);	
			this.loadData();
			this.data = this.generateResult(this.collection);
		},
		
		// parse response and generate select2 compatible result
		generateResult: function(collection) {
			if(!collection) return {results : []};
			if(!this.resultMap) return data;
			var results = collection.map(function(model) {
				return {
					id: model.get(this.resultMap.id),
					text: (model.get(this.resultMap.text) || model.get(this.resultMap.id)) + ''
				};
			}, this);
			return {results: results};
		},
		
		loadData: function(options) {			
			this.collection.fetch({
				url : '/procurement/v1/purchaseorganization',
				async: false				
			});
		},
		
		select2InitSelect: function(element, callback) {              
            var id = element.val();
            if (id !== "") {
                var currentText = this.getTextForId(id);
                callback({id: id, text: currentText});              
            }
        },
		
		getTextForId: function(id) {
			var that = this;
			var text = null;
			this.collection.each(function(model) {
				if(model.get(that.resultMap.id) == id) { // will be one match always!
					text = model.get(that.resultMap.text);
				}
			});
			return text;
		},
		
		// making select2 options
		select2DefaultOptions: function() {
			return _.extend(Select2View.prototype.select2DefaultOptions(), {
			    initSelection: _.bind(this.select2InitSelect, this),
		        multiple: false,
		        data: this.data,
		        disabled: this.disabled
			}, this.select2Options);
		}		
	});
	
	return PurchaseOrgSelector;
});