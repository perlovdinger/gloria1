/**
 * Base Selector
 */
define(['app',
        'jquery',
        'underscore',
        'backbone',
        'bootstrap',
        'i18next',
        'select2',
        'utils/UserHelper'
], function(Gloria, $, _, Backbone, Bootstrap, i18n, select2, UserHelper) {

	var BaseSelector = Backbone.View.extend({

		tagName : 'input',

		className : 'form-control',
		
		key : 'id',

		resultMap : {
			id : 'id',
			text : 'text'
		},

		events : {
			'change' : 'handleChangeEvent'
		},

		initialize : function(options) {
			options || (options = {});
			this.url = options.url || this.url;
			this.key = options.key || this.key;
			this.resultMap = options.resultMap || this.resultMap;
			this.select2Options = options.select2Options || this.select2Options || {};
			this.defaultDataSet = options.defaultDataSet;
			this.module = options.module;
			this.disabled = options.disabled;
			this.noDefaultData = options.noDefaultData;
			this.filter = options.filter;
		},

		defaultData : function() {
			var res = [];
			if(!this.noDefaultData) {
				res = [{
					id : 'all',
					text : i18n.t('Gloria.i18n.reports.all'),
					locked: true
				}];
			}
			return res;
		},

		handleChangeEvent : function(e) {
			if (e.added) { // Item added
				// Id "all" is already selected remove it and add current item!
				if(e.val.indexOf('all') > -1) {
					e.val = [ e.added.id ];
					e.currentTarget.value = e.added.id;
					this.$el.data().select2.updateSelection(e.added);
				}
				// If current item is "all" then remove previous selected items!
				if(e.currentTarget.value == 'all') {
					e.val = [ 'all' ];
					this.$el.data().select2.updateSelection(this.defaultData());
				}
			}
			
			// if every selected item is removed , should be updated with 'ALL'
			if(e.removed && this.$el.data().select2.val().length == 0) {
				this.$el.data().select2.updateSelection(this.defaultData());			
			}
			// Trigger an event select2-selected-<module> so that other module can perform anything specific
			// This gets triggered for both added & removed events triggered by select2!
			if(this.module) {
				Gloria.ReportsApp.trigger('select2-selected-' + this.module, {
					type : e.added ? 'added' : (e.removed ? 'removed' : null),
					value : e.currentTarget.value,
					item : e.added || e.removed,
					items : this.$el.select2('data'),
					data : this.data
				});
			}
		},

		generateResult : function(data, page, query) {
			if (!data) {
				return {
					results : []
				};
			}
			if (!this.resultMap) {
				return data;
			}			
			var results = this.mapData(data);
			// Adding -All- as default option!
			results = this.filter ? this.filter(this.defaultData().concat(results), this.$el.select2('val')) : this.defaultData().concat(results);
			return {
				results : results
			};
		},
		
		mapData: function(data) {
			return _.map(data, function(object) {				
				return {
					id : object[this.resultMap.id] || object.id,
					text : object[this.resultMap.text] || object.id
				};
			}, this);
		},

		// search Term
		searchTerm : function(term) {
			var data = {};
			if (this.key) {
				data[this.key] = '*' + term;
			}
			return data;
		},

		// select2 Query
		select2Query : _.debounce(function(query) {
			var term = query.term;
			var cachedData = null;
			if (this.cachePrefix) {
				cachedData = sessionStorage.getItem(_.result(this, 'cachePrefix') + UserHelper.getInstance().getUserId() + (term ? '.' + term : ''));
			}
			if (cachedData) {
				try {
					query.callback(this.generateResult(JSON.parse(cachedData)));
				} catch(e) {
					query.callback(this.generateResult(JSON.parse('{}')));
				}
				return;
			} else {
				this.loadData({
					query : query,
					data : this.searchTerm(term)
				});
			}
		}, 500),

		// making select2 options
		select2DefaultOptions : function() {
			return _.extend({
				query : _.bind(this.select2Query, this),
				multiple : true,
				allowClear : true,
				closeOnSelect : true
			}, this.select2Options);
		},

		// Load Data
		loadData : function(options) {
			options || (options = {});
			if (this.defaultDataSet) {
				var searchString = options.data.id.replace('*', '');
				if(searchString) {
					var filteredDataSet = [];
					_.each(this.defaultDataSet, function(thisData) {
						if(thisData.id.toUpperCase().indexOf(searchString.toUpperCase()) >= 0
								|| thisData.text.toUpperCase().indexOf(searchString.toUpperCase()) >= 0) {
							filteredDataSet.push(thisData);
						}
					});
					this.onLoadDataSuccess(options, filteredDataSet);
				} else {
					this.onLoadDataSuccess(options, this.defaultDataSet);
				}
			} else {
				return Backbone.$.ajax({
					type : 'GET',
					url : _.result(this, 'url'),
					dataType : 'json',
					data : options.data,
					success : _.bind(_.partial(this.onLoadDataSuccess, options), this),
					error : _.bind(_.partial(this.onLoadDataError, options), this)
				});
			}
		},

		onLoadDataSuccess : function(options, data) {
			this.data = data;
			if (options.query) {
				if (this.cachePrefix) {
					sessionStorage.setItem(_.result(this, 'cachePrefix') + UserHelper.getInstance().getUserId()
							+ (options.query.term ? '.' + options.query.term : ''), JSON.stringify(data));
				}
				options.query.callback(this.generateResult(data));
			}
		},

		onLoadDataError : function(options) {
			this.$el.select2('close');
		},
		
		// Refresh selector
		refresh : function() {
			this.$el.data().select2.updateSelection(this.defaultData());
		},

		onShow : function() {
			this.$el.is(':hidden') && this.$el.show();
			this.$el.select2(this.select2DefaultOptions());
			this.$el.data().select2.updateSelection(this.defaultData());
			if(this.disabled) {
				this.$el.select2('disable');
			}
		},
		
		remove : function() {
			this.$el.select2('destroy');
			Backbone.View.prototype.remove.apply(this, arguments);
		}
	});

	return BaseSelector;
});
