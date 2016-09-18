/**
 * This file contains Gloria's additions to backbone-pageable. This should
 * always be referenced instead of backbone-pageable in pageable collections.
 */
define(['app', 'backbone-pageable' ], function(Gloria, BackbonePageable) {

	var GloriaPageableCollection = Backbone.PageableCollection.extend({
		
		defaultSort: {
			attributeName: 'id',
			order: '-1'
		},

		initialize : function(args, options) {
			this.app = 'Gloria';
			this.subapp = Backbone.history.fragment.split('/')[0];
			this.filterKey = options && options.filterKey;
			this.listenTo(this, 'QueryParams:reset', this.resetQueryParams);
			this.listenTo(this, 'Grid:Filter:reset', this.resetFilter);
			return Backbone.PageableCollection.prototype.initialize.call(this, args);
		},
		
		resetFilter : function(grid, mandatoryAttributes) {
			var that = this;
			that.fetchNeeded = false;
			// Find all the filter values from session storage
			var filterStorageKey = this.app + '.' + this.subapp + '.' + this.filterKey + '.filters';
			var qryParams = window.sessionStorage.getItem(filterStorageKey);
			if (qryParams) {
				var parsedParams = JSON.parse(qryParams);
				_.each(grid.columns.models, function(model) {
					var customFilters = model.get('customFilters');
					if(customFilters) { // For Custom Header Cells
						var isTriggerRequired = false;
						_.each(customFilters, function(value) {
							_.each(parsedParams, function(val, key) {
								if(value == key && val) {
									isTriggerRequired = true;
									that.fetchNeeded = true;
									delete grid.collection.queryParams[key];	// Remove from collection
									delete parsedParams[key];					// Remove from filter
								}
							});
						});
						if(isTriggerRequired) { // Trigger Once for all fields of the header cell
							model.trigger('ControlPanel:filter:remove');
						}
					} else { // For General Header Cells
						_.each(parsedParams, function(val, key) {
							if(model.get('name') == key) {
								that.fetchNeeded = true;
								delete grid.collection.queryParams[key];	// Remove from collection
								delete parsedParams[key];					// Remove from filter
								model.trigger('ControlPanel:filter:remove');
							}
						});
					}
				});
				// If there is any mandatory fields
				if(mandatoryAttributes) {
					_.extend(grid.collection.queryParams, mandatoryAttributes);	// Add to collection
					_.extend(parsedParams, mandatoryAttributes);				// Add to filter
				}
				window.sessionStorage.setItem(filterStorageKey, JSON.stringify(parsedParams));
				if(that.fetchNeeded) { // Fetch if needed
					grid.collection.fetch({
						reset : true
					});
				}
			}
		},

		fetch : function(options, a, b) {
			this.normalizeQueryParams();
			var myOptions = options || {};
			myOptions.data = myOptions.data || {};
			myOptions.reset = myOptions.reset === false ? false : true;
			this.setDefaultSorting(myOptions);
			return Backbone.PageableCollection.prototype.fetch.call(this, myOptions);
		},
		
		setDefaultSorting: function(options) {
			options || (options = {data:{}});
			if(this.mode != 'client' && !this.state.sortKey && !options.data[this.queryParams.sortKey]) {
				this.setSorting(this.defaultSort.attributeName, this.defaultSort.order);
			}
		},

		resetQueryParams : function(params) {
			var filterStorageKey = this.app + '.' + this.subapp + '.' + this.filterKey + '.filters';
			var qryParams = window.sessionStorage.getItem(filterStorageKey);
			if (qryParams) {
				var parsedParams = JSON.parse(qryParams);
				_.each(params, function(param) {
					delete parsedParams[param];
				});
				window.sessionStorage.setItem(filterStorageKey, JSON.stringify(parsedParams));
			}
		},
		
		getStorageKey: function() {
		    return this.app + '.' + this.subapp + '.' + this.filterKey + '.filters';
		},
		
		getFilterStorageValue: function(key) {
		    var val = {};
		    try {
		    	val = JSON.parse(sessionStorage.getItem(this.getStorageKey()) || '');
		    } catch(e){}
		    return val[key];
		},

		// Parse server response to handle total number of items
		parseState : function(resp, queryParams, state, options) {
			var totalRecords = options && options.xhr && options.xhr.getResponseHeader("X-Result-Counter");
			return {
				totalRecords : Number(totalRecords) || 0
			};
		},

		save : function(options) {
			this.sync("update", this, options);
		},

		deepClone : function() {
			return new this.constructor(this.invoke('toJSON'));
		},

		normalizeQueryParams : function() {
			if (this.filterKey) {
				var filterStorageKey = this.app + '.' + this.subapp + '.' + this.filterKey + '.filters';
				var qryParams = window.sessionStorage.getItem(filterStorageKey);
				if (qryParams) {
					_.extend(this.queryParams, JSON.parse(qryParams));
				}
				var filteredQueryParam = this.removePageParamsFromQueryParam(_.clone(this.queryParams));
				var stringToBeStored = JSON.stringify(this.removeNullValuesFromJSON(filteredQueryParam));
				if(stringToBeStored != '{}') {
					window.sessionStorage.setItem(filterStorageKey, stringToBeStored);
				} else {
					window.sessionStorage.removeItem(filterStorageKey);
				}
			}
			if (this.queryParams && this.queryParams.projectId) {
				this.queryParams.projectId = '*' + this.queryParams.projectId;
			}
		},
		
		/**
		 * Remove Page Parameters added by PageableCollection
		 */
		removePageParamsFromQueryParam : function(queryParams) {
			// PageableCollection page params: currentPage, pageSize, totalPages, totalRecords, sortKey, order, directions
			delete queryParams['currentPage'];
			delete queryParams['pageSize'];
			delete queryParams['totalPages'];
			delete queryParams['totalRecords'];
			delete queryParams['sortKey'];
			delete queryParams['order'];
			delete queryParams['directions'];
			return queryParams;
		},
		
		removeNullValuesFromJSON : function(obj) {
			for(var key in obj) {
			    if(!obj[key]) {
			        delete obj[key];
			    }
			}
			return obj;
		}
	});

	return GloriaPageableCollection;
});