/**
 * An extended header cell for filtering Strings
 */
define(['backgrid',
        'hbs!utils/backgrid/stringHeaderCell',
        'app',
        'bootstrap',
        'bootstrap-tagsinput'
        ], function (BackgridPaginator, StringHeaderCellTemplate, Gloria, BootstrapRef, TagsInputRef) {
	
    var StringHeaderCell = Backgrid.HeaderCell.extend({
        
        template : StringHeaderCellTemplate,
        
		initialize : function(options) {
			this.tooltip = options.tooltip;
			this.addAsterisk = this.actAsNumber ? false : this.addAsterisk;
			this.listenTo(options.column, 'ControlPanel:filter:remove', this.removeFilter);
			Backgrid.HeaderCell.prototype.initialize.call(this, options);			
		},
		
		className: 'string-header',

		events: {
    		'click a': 'onClick',
    		'itemAdded input': 'onItemAddRemove',
    		'itemRemoved input': 'onItemAddRemove',
    		'keypress input': 'validateInput',
    		'beforeItemAdd input': 'onBeforeItemAdd'
    	},
    	
    	removeFilter : function() {
    		this.$el.find('input').tagsinput('removeAll', {preventPost: true});
    		this.$el.find('input').tagsinput('destroy');
    		this.$el.find('input').tagsinput({
    			tagClass : 'tagspan'
    		});
		},
    	
    	onItemAddRemove: function(event) {    		   
    		event.target.value = this.normalizeSearchTerms(event.target.value);
			if(!this.blockSearch) {
    			this.performSearch(event);
			}
		},
		
		onBeforeItemAdd: function(e) {
			if(this.actAsNumber && e.item && isNaN(e.item)) {
				e.cancel = true;
			}			
		},
		
		validateInput: function(e) {
			if(this.actAsNumber && this.isNotNumber(e)) {
				e.preventDefault();
			}
		},
		
		isNotNumber: function(event) {
			return (event.which != 8 && isNaN(String.fromCharCode(event.which)));
		},
		
		addAsterisk: true,
		
		actAsNumber: false,
		
		normalizeSearchTerms: function(searchTerm) {
			var searchTerms = (searchTerm || '').split(',');
    		_.each(searchTerms, function(item, index) {
    			if(this.addAsterisk && item && item.indexOf('*') != 0) {
    				searchTerms[index] = '*' + item;
        		}
    		}, this);
    		return searchTerms;
		},
    	
    	/**
    	 * A state to keep track on if a search is running. If a search is currently running, idle gets set to false.
    	 */
    	idle : true,
    	
    	/**
    	 * Run a search.
    	 * @param target Search input box
    	 */
    	performSearch : function(target) {
    		var that = this;
    	   // var contentString = target.value;
    		var contentString = that.$('#' + that.column.get('name') + '_search').val();
    	    
    	    // Valid Search String?
    		if(contentString && contentString.replace(/[*]/g, '').length == 0) {
    			this.idle = true;
    			return;
    		}
    	    
    	    //Trim the string
    	    contentString = contentString ? contentString.replace(/^\s+|\s+$/g, '') : '' ;
    	    
    	    var stringRegex = /^.+$/;
    	    
    	    // Check if the value is a string
    	    if(stringRegex.test(contentString)) {
    	    	this.collection.queryParams[this.column.attributes.name] = contentString;
    	    	 // Update local storage query param value
        	    this.updateSessionStorageQueryParam(this.collection, this.column.get('name'), contentString);
    	    } else {
    	    	this.collection.queryParams[this.column.attributes.name] = null;
    	    	 // Update local storage query param value
        	    this.updateSessionStorageQueryParam(this.collection, this.column.get('name'), null);
    	    }
    	    
    	    this.collection.state.currentPage = 1;
    	    
    	    // Client mode filter!
    	    if(that.collection.mode == 'client') { // @TODO should check all attributes not only the recent one!!!
    	    	var models = this.collection.fullCollection.filter(function(model) {
    	    		return !contentString
    	    				|| (model.get(that.column.attributes.name) ? 
    	    				model.get(that.column.attributes.name).toString().toUpperCase().indexOf(contentString.toUpperCase()) > -1 : false);
    	    	});
    	    	that.idle = true;
    	    	this.collection.reset();
    	    	this.collection.add(models);
    	    	return this.collection.models;
    	    } else {	// Server mode filter!
			    this.collection.fetch({cache: false, reset: true}).always(function() {
			    	// If search string has changed while fetching, perform a new search, else set the
			    	// search input to idle and wait for new input
			    	if (contentString != that.$('#' + that.column.get('name') + '_search').val().replace(/^\s+|\s+$/g, '')) {
			    		setTimeout(function(){that.performSearch(target);}, 250); // Simulate threading
			    	} else {
			    		that.idle = true;
			    	}
			    });
    	    }
    	},
    	
    	// Update Local Storage Query Param
    	updateSessionStorageQueryParam : function(collection, name, value) {
    		var filterStorageKey = collection.app + '.' + collection.subapp + '.' + collection.filterKey + '.filters';
    		var qryParams = window.sessionStorage.getItem(filterStorageKey);
    		var qryParamsObj = JSON.parse(qryParams);
    		// If there is any change in Search, trigger event so that the caller can clear selections
    		if(!qryParamsObj || value != qryParamsObj[name]) {
    			this.collection.trigger('QueryParams:changed');
    		}
    		if(value) { // Add/Update to Storage
				var item = {};
                item[name] = value;
                _.extend(qryParamsObj, item);
			} else if(qryParamsObj) { // Remove from Storage
				delete qryParamsObj[name];
			}
			window.sessionStorage.setItem(filterStorageKey, JSON.stringify(qryParamsObj));
		},
    	
    	/**
    	 * Perform seach when filter changes
    	 */
    	onStringFilter : function(e) {
    	    e.preventDefault();
    	    whichKey = e.which;
    	    if ((whichKey == 13 || whichKey == 44) && this.idle) {
    	    	var that = this;
    	    	this.idle = false;
    	    	setTimeout(function(){that.performSearch(e.target);}, 250); // Simulate threading
    	    }
    	},
    	
    	getHeaderCellSearchTerm: function(collection, column) {
    		var searchTerm = null;
    		if(collection.filterKey) {
    			var filterStorageKey = collection.app + '.' + collection.subapp + '.' + collection.filterKey + '.filters';
    			var qryParams = window.sessionStorage.getItem(filterStorageKey);
    			if(qryParams) {
    				var qryParamsObj = JSON.parse(qryParams);
    				_.each(qryParamsObj, function(value, key, field) {
						if(key == column.get('name')) {
							searchTerm = value;
						}
					});
        		}
    		}
    		return searchTerm && searchTerm.split(',');
		},
    	
    	/**
    	 * Renders a header cell with a sorter and a label
    	 */
    	render: function () {
    		
    		this.$el.html(this.template({
				isSortable : this.column.get('sortable'),
				label : this.column.get('label'),
				defaultValue : (this.collection.queryParams && this.collection.queryParams[this.column.get('name')]) || '',
				id : this.column.get('name') + '_' + 'search',
				isSearchable : this.column.isSearchable == false ? false : true,
			}));
    		
    		
    		
    		// prevent loading tagsinput in mobile app
    		if (this.$el.find('input').length) {
        		// Initiate Bootstrap tagsinput
    		    
    			this.tagInput = this.$el.find('input').tagsinput({
            			tagClass : 'tagspan'
            		});
    		        var searchTerms = this.getHeaderCellSearchTerm(this.collection, this.column);
    		        if(searchTerms) {
    		        	this.blockSearch = true; // Adding silently
    		        	var input = this.$('#' + this.column.get('name') + '_search');
    		        	_.each(searchTerms, function(term) {
    		        		input.tagsinput('add', term);
	    		        }, this);
    		        	// After adding all set the blockSearch flag to false
    		        	this.blockSearch = false;
    		        }    		       
    		   
    		}    		
    		
    		if(this.tooltip && this.tooltip.tooltipText){
                this.$el.tooltip({
                    placement: 'top',
                    container: 'body',
                    trigger: 'hover',
                    title: this.tooltip.tooltipText  
                });                
            }
    		
    		return this;
    	},
    	
    	remove: function() {
    		this.$('#' + this.column.get('name') + '_search').tagsinput('destroy');
    	    this.$el.tooltip('destroy');    	    
    	    return Backgrid.HeaderCell.prototype.remove.apply(this, arguments);
    	}
    });
	
	return StringHeaderCell;
});