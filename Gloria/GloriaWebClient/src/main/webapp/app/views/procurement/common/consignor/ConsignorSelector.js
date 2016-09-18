define(['app',
	    'jquery',    
	    'underscore',
        'backbone',
        'marionette',
        'utils/typeahead/Select2View',
        'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Backbone, Marionette, Select2View, GloriaCollection) {
	
	var ConsignorSelector = Select2View.extend({
		
		resultMap: {
			id: 'id',
			text: 'supplierName'
		},
		
		events: {
			"select2-selecting": "onSelecting"
		},
		
		onSelecting: function(e) {
			if(e && e.choice) {
				var criteria = {};
				criteria[this.resultMap.id] = e.choice.id;
				if(!this.collection.findWhere(criteria)) {
					e.choice.id = -1;					
				}				
			};
			
			if(this.bindTextElement && !this.disabled) {				
				this.makeBindTextElement();
				this.boundEl.val(e.choice.text);
			}
		},
		
		initialize: function(options) {
			this.id = options.id;
			this.url = options.url;
			this.disabled = options.disabled;
			Select2View.prototype.initialize.apply(this, arguments);
			this.resultMap = options.resultMap || this.resultMap;	
			this.bindTextElement = options.bindTextElement;
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
					id: model.get(this.resultMap.id) || model.id,
					text: (model.get(this.resultMap.text) || model.id) + ''
				};
			}, this);
			return {results: results};
		},
		
		loadData: function(options) {
		    var that = this;
			this.collection.fetch({
				url : that.url || '/procurement/v1/procurelines/' + this.id + '/carryovers',
				async: false				
			});
		},		
		
		createSearchChoice: function(term, data) {
			var results = _.filter(data, function(item) {				
				return (item && item.toString().localeCompare(term)) === 0; 
			});
			if (results.length === 0) {
                return {id:term, text:term};
            }			
		},
		
		select2InitSelect: function(element, callback) {				
			var id = element.val();
			if (id != '') {
				id = Number(id);
				var currentText = this.getTextForId(id);
				var data = {id: id, text: (currentText || id)};
				callback(data);
				element.trigger({
					type: 'select2-selecting',
					choice: data,
					val: data.id
				});				
			}
		},
		
		getTextForId: function(id) {
			var text = null;
			this.collection.find(function(model) {
				if(model.get(this.resultMap.id) == id) { // will be one match always!
					text = model.get(this.resultMap.text);
				}
			}, this);
			return text;
		},
		
		// making select2 options
		select2DefaultOptions: function() {
			return _.extend(Select2View.prototype.select2DefaultOptions(), {				
				createSearchChoice: this.createSearchChoice,
				initSelection: _.bind(this.select2InitSelect, this),
		        multiple: false,
		        data: this.data,
		        disabled: this.disabled
			}, this.select2Options);
		},
		
		onShow: function() {		
			var options = this.select2DefaultOptions();
			Select2View.prototype.onShow.apply(this, arguments);
			if(this.bindTextElement && !options.disabled) {				
				this.makeBindTextElement();
			}
		},
		
		makeBindTextElement: function() {
			if(!this.boundEl) {
				this.boundEl = Backbone.$('<input class="hidden"/>').attr({
					id: this.bindTextElement.id,
					name: this.bindTextElement.name
				});
				this.$el.after(this.boundEl);
			}
		}
	});
	
	return ConsignorSelector;
});