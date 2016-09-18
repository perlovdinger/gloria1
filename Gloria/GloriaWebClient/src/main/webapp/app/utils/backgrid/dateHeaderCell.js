/**
 * An extended header cell for filtering dates
 */
define(['jquery',
        'backgrid',
        'utils/DateHelper',
        'i18next',
        'datepicker',
        'hbs!utils/backgrid/dateHeaderCell'
], function ($, BackgridRef, DateHelper, i18n, Datepicker, compiledTemplate) {
	
    var DateHeaderCell = Backgrid.HeaderCell.extend({
		initialize : function(options) {
			this.template = compiledTemplate;
			this.tooltip = options.tooltip;
			this.listenTo(options.column, 'ControlPanel:filter:remove', this.removeFilter);
			DateHeaderCell.__super__.initialize.call(this, options);
		},

		events: {
    		"click a": "onClick",
    		"changeDate .js-date" : "onDateFilter",
    		//"keypress .js-date" : "ie10BrowserImplicitEventTrigger",
    		"keydown .js-date" : "preventManualChange",
    		"paste .js-date" : "preventManualChange"
    	},
    	
    	removeFilter : function() {
    		this.$('.js-date').val('');
		},
    	
    	preventManualChange: function(e) {
    		e.preventDefault();
    		return false;
		},
    	
    	/**
    	 * Perform seach when filter changes
    	 */
    	onDateFilter : function(e) {    	    
    	    e.preventDefault();
    	    var dateString = e.target.value;
    	    
    	    if (DateHelper.isValidDate(dateString)) {
    	    	this.collection.queryParams[this.column.attributes.name] = DateHelper.parseDate(dateString, true);
    	    	// Update local storage query param value
        	    this.updateSessionStorageQueryParam(this.collection, this.column.get('name'), DateHelper.parseDate(dateString, true));
    	    } else {
    	    	this.collection.queryParams[this.column.attributes.name] = null;
    	    	// Update local storage query param value
        	    this.updateSessionStorageQueryParam(this.collection, this.column.get('name'), null);
    	    }
    	    this.collection.state.currentPage = 1;    
   	    	this.collection.fetch({reset: true});
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
    		return searchTerm && DateHelper.formatDate(searchTerm);
		},
    	
    	ie10BrowserImplicitEventTrigger : function(event){
    	   
    	    var keycode = (event.keyCode ? event.keyCode : event.which);
    	    if(keycode == '13'){
    	        if(navigator.userAgent.match(/MSIE 10/)){                    
                    this.$el.find('.js-date').change();
                };
    	    }
    	    event.stopPropagation();  
    	},
    	
    	/**
    	 * Renders a header cell with a sorter and a label.
    	 */
    	render: function () {
    		var that = this;
    		this.$el.append(
		        this.template({
		        	isSortable : this.column.get("sortable"),
		            label : this.column.get("label"),
		            id : this.column.get("name") + '_' + 'search'
		        })
		    );    		
    		
    	 	this.$('.js-date').datepicker({
    	 	    orientation: 'bottom',
    	 	    clearBtn: true
    	 	});
    	 	
    		this.$('.js-date').val(that.getHeaderCellSearchTerm(that.collection, that.column));

        	this.delegateEvents();
        	
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
            this.$('.js-date').datepicker('remove');
            Backgrid.HeaderCell.prototype.remove.apply(this, arguments);
        }
    });
	
	return DateHeaderCell;
});