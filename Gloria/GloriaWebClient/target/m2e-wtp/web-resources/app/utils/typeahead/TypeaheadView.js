define(['jquery', 'underscore', 'backbone', 'bootstrap', 'i18next' ], function($, _, Backbone, Bootstrap, i18n) {

var TypeaheadView = Backbone.View.extend({
	
		tagName: 'input',
	
		className: 'form-control',

		ajaxDataType : 'json',
		
		maxResults : 15,
		
		defaultOption : true,
		
		// cachePrefix : 'tpah.', 	// Respective Module has to send this if it has to cache,
									// or else it will not cache any data
									// It is more of a FLAG to ask whether Data Cache is required or NOT
		
		initialize : function(options) {
			options || (options = {});
			this.cacheDataSource = [];
			this.url = options.url || this.url;
			this.key = options.key || this.key;
			this.disabled = options.disabled || this.disabled;
			this.resultMap = options.resultMap || this.resultMap;
			this.select2Options = options.select2Options || this.select2Options || {};
		},

		// parse response and generate select2 compatible result
		generateResult : function(data, page, query) {
			if (!data) {
				return {
					results : []
				};	
			}
			if (!this.resultMap) {
				return data;
			}
			var that = this;
			var isTextAPattern = that.isTextAPattern(that.resultMap.text);
			var results = _.map(data, function(object) {
				return {
					id : object[that.resultMap.id] || object.id,
					text : function() {
						if (isTextAPattern) {
							return (that.pattern(object, that.resultMap.text) || object.id) + '';
						} else {
							return (object[that.resultMap.text] || object.id) + '';
						}
					}()
				};
			}, this);
			if (data.length == this.maxResults) {
				results.push({
					id : '__more__',
					text : i18n.t('Gloria.i18n.general.more'),
					disabled : true
				});
			}
			if(this.defaultOption) {
				results.unshift({
					id : '',
					text : i18n.t('Gloria.i18n.general.pleaseSelect')
				});
			}
			return {
				results : results
			};
		},
		
		// make an object using key as key and term as value
		// the main usage is to pass this object to be consumed in ajax as data
		searchTerm : function(term) {
			var data = {};
			if (this.key) {
				data[this.key] = term;// search term
			}
			return data;
		},
		
		// Function used to query results for the search term.
		// Using debounce to postpone its execution until after
		// 0.5 seconde to avoid sending too much request
		select2Query : _.debounce(function(query) {
			var term = query.term;
			var cachedData = null;
			if (this.cachePrefix) {
				cachedData = sessionStorage.getItem(_.result(this, 'cachePrefix') + this.key + '.' + term);
			}
			// return results from cachedData
			if (cachedData) {
				query.callback(this.generateResult(JSON.parse(cachedData))); //its select2 callback
				return;
			} else {
				this.loadData({
					query : query,
					data : this.searchTerm(term)
				});
			}
		}, 500),
		
		// Called when Select2 is created to allow the user to
		// initialize the selection based on the value of the element select2 is attached to.
		// Essentially this is an id->object mapping function
		select2InitSelect : function(element, callback) {
			var that = this;
			var id = element.val();
			if (id != '') {
				this.loadData({
					data : this.searchTerm(id)
				}).done(_.bind(function(resposneData) {
					var result = this.generateResult(resposneData);
					try {
						callback((that.defaultOption ? result.results[1] : result.results[0]) || null);
					} catch(e) {
						// Error
					}
				}, this));
			}
		},
		
		// making select2 options
		select2DefaultOptions : function() {
			return _.extend({
				placeholder : i18n.t('Gloria.i18n.general.pleaseSelect'),
				width : 'resolve',
				initSelection : _.bind(this.select2InitSelect, this),
				// apply css that makes the dropdown taller
				dropdownCssClass : 'bigdrop',
				// we do not want to escape markup since we are displaying html in results
				escapeMarkup : function(m) {
					return m;
				},
				query : _.bind(this.select2Query, this),
			}, this.select2Options);
		},
		
		// send a GET request to the specifed url and cache the result
		loadData : function(options) {
			if (!this.url)
				return;
			options || (options = {});
			var data = {
				page : 1,
				per_page : this.maxResults
			};
			data = _.extend(data, options.data);
			return Backbone.$.ajax({
				url : _.result(this, 'url'),
				data : data,
				dataType : this.ajaxDataType,
				type : 'GET',				
				success : _.bind(_.partial(this.onLoadDataSuccess, options), this),
				error : _.bind(_.partial(this.onLoadDataError, options),this)
			});
		},

		onLoadDataSuccess : function(options, data) {
			if (options.query) {
				if (this.cachePrefix) {
					sessionStorage.setItem(_.result(this, 'cachePrefix')	+ this.key + '.' + options.query.term, JSON.stringify(data));
				}
				options.query.callback(this.generateResult(data));
			}
		},

		onLoadDataError : function(options) {
			this.$el.select2('close');
		},

		isTextAPattern : function(str) { // Valid format check has been ignored!
			return str.indexOf('{{') >= 0 && str.indexOf('}}') >= 0;
		},

		pattern : function(data, patternStr) {
			var result = patternStr;
			// TODO: Find a better RegExp
			if (!this.replaces) { // Do not parse again if it is already parsed!
				this.replaces = new Array();
				while (patternStr.indexOf('{{') >= 0 && patternStr.indexOf('}}') >= 0) {
					this.replaces.push(patternStr.substring(patternStr.indexOf('{{') + 2, patternStr.indexOf('}}')));
					patternStr = patternStr.substring(patternStr.indexOf('}}') + 2, patternStr.length);
				}
			}
			_.each(this.replaces, function(replace) {
				var re = new RegExp('{{' + replace + '}}', 'g');
				result = result.replace(re, data[replace]);
			});
			return result;
		},
		
		showSpannedText: function() {
			var that = this;
			var currentText = that.$el.val();
			var parentEl = this.$el.parent();
			that.$el.hide(); // Keep input but in hidden mode, so that user can re-render this if required!
			if(currentText) {
				this.loadData({
					data : that.searchTerm(currentText)
				}).done(_.bind(function(resposneData) {
					var object = _.first(resposneData); // Should return only one
					var text = function() {
						if(object) {
							if (that.isTextAPattern(that.resultMap.text)) {
								return (that.pattern(object, that.resultMap.text) || object.id) + '';
							} else {
								return (object[that.resultMap.text] || object.id) + '';
							}
						} else {
							return '';
						}
					}();
					parentEl.append('<span id=' + this.key + '>' + text + '</span>'); // Set the Text
				}, this)).fail(_.bind(function(resposneData) {
					parentEl.append('<span id=' + this.key + '>' + currentText + '</span>'); // Set the ID
				}, this));
			} else {
				parentEl.append('<span id=' + this.key + '>' + currentText + '</span>'); // Set the blank
			}
		},

		onShow : function() {
			var that = this;
			if (this.disabled) {
				this.showSpannedText();
			} else {
				var options = this.select2DefaultOptions();
				// Check if the el is hidden, show it first
				this.$el.is(':hidden') && this.$el.show(); 
				this.$el.select2(options);
				// Select First Option! If there is ONLY one option available!
				if (this.selectFirstOption) {
					return Backbone.$.ajax({
						url : _.result(this, 'url'),
						data : {
							page : 1,
							per_page : this.maxResults
						},
						dataType : this.ajaxDataType,
						type : 'GET',				
						success : function(response) {
							if(response && response.length == 1) {
								var code = _.first(response)[that.resultMap.id];
								that.$el.select2('val', code);
								that.trigger(that.cid, code);
							}
						},
						error : _.bind(_.partial(this.onLoadDataError, options),this)
					});
				}
			}
		},

		remove : function() {
			this.$el.select2('destroy');
			Backbone.View.prototype.remove.apply(this, arguments);
		}

	});

	return TypeaheadView;
});