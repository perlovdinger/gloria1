define(['collections/PartAliasCollection', 'utils/typeahead/Select2View', 'app'], function(PartAliasCollection, Select2View, Gloria) {
	
	var AliasComboboxView = Select2View.extend({
		
		resultMap: {
			id: 'partNumber',
			text: 'partNumber'
		},
		
		initialize: function(options) {
			Select2View.prototype.initialize.apply(this, arguments);
			this.resultMap = options.resultMap || this.resultMap;	
			this.select2Options = options.select2Options || this.select2Options || {};
			this.volvoPartNumber = options.volvoPartNumber;
			this.collection = new PartAliasCollection([]);	
			this.loadData();
			this.data = this.generateResult(this.collection);
			//this.listenTo(Gloria.ProcurementApp, 'procurelineDetails:consignorChanged', this.onConsignorChange);
		},		
		
		onConsignorChange: function(e, model) {
			//var options = {};
			/*
			if(model && model.get('supplierId')) {
				options.data = {
					'supplierId': model.get('supplierId')
				};
			}
			*/
			//this.loadData(options);
			//this.data = this.generateResult(this.collection);			
			//this.onShow();
		},
		
		// parse response and generate select2 compatible result 
		generateResult: function(collection) {					
			if(!collection) return {results : []};
			if(!this.resultMap) return data;
			var results = collection.map(function(model) {
				return {
					id: model.get(this.resultMap.id) || model.id,
					text: (model.get(this.resultMap.text) || model.id)+ ''
				};
			}, this);
			return {results: results};
		},
		
		loadData: function(options) {	
			options || (options = {});
			this.collection.fetch({
				data: _.defaults({'volvoPartNumber' : this.volvoPartNumber}, options.data),
				async: false				
			});						
		},		
		
		createSearchChoice: function(term, data) {
			var results = _.filter(data, function(item) {
				return item.localeCompare(term) === 0; 
			});
			
			if (results.length === 0) {
                return {id:term, text:term};
            }			
		}, 
		// making select2 options
		select2DefaultOptions: function() {					
			return _.extend(Select2View.prototype.select2DefaultOptions(), {				
				createSearchChoice: this.createSearchChoice,
		        multiple: false,
		        data: this.data
			}, this.select2Options);
		}		
	});
	
	return AliasComboboxView;
});