/**
 * An extended header cell for filtering dates
 */
define(['backgrid',
        'hbs!utils/backgrid/numberHeaderCell',
        'app'
        ], function (BackgridRef, compiledTemplate, Gloria) {
	
    var NumberHeaderCell = Backgrid.HeaderCell.extend({
		initialize : function(options) {
			this.template = compiledTemplate;
			this.tooltip = options.tooltip;
			NumberHeaderCell.__super__.initialize.call(this, options);
		},

		events: {
    		"click a": "onClick",
    		"change input" : "onNumberFilter",
       		"keyup input" : "onNumberFilter"
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
    		var numberString = target.value;
    	    
    	    var intRegex = /^-?\d+$/;
    	    
    	    // Check if the value is an number
    	    if(intRegex.test(numberString)) {
    	    	this.collection.queryParams[this.column.attributes.name] = parseInt(numberString);
    	    	Gloria.trigger('backgrid:HeaderCellSearchApplied');
    	    } else {
    	    	this.collection.queryParams[this.column.attributes.name] = null;
    	    	Gloria.trigger('backgrid:HeaderCellSearchRemoved');
    	    }
    	    this.collection.state.currentPage = 1;  
//    	    if(String(this.collection.url).indexOf("undefined") == -1){
	    	    this.collection.fetch({reset: true}).always(function() {
	    	    	// If search string has changed while fetching, perform a new search, else set the
	    	    	// search input to idle and wait for new input
	    	    	if (numberString != target.value) {
	    	    		setTimeout(function(){that.performSearch(target);}, 250); // Simulate threading
	    	    	} else {
	    	    		that.idle = true;
	    	    	}
	    	    });
//    	    }
    	},

    	/**
    	 * Perform seach when filter changes
    	 */
    	onNumberFilter : function(e) {
    	    e.preventDefault();
    	    if (this.idle) {
    	    	var that = this;
    	    	this.idle = false;
    	    	setTimeout(function(){that.performSearch(e.target);}, 250); // Simulate threading
    	    }    	    
    	},
    	
    	/**
    	 * Renders a header cell with a sorter and a label.
    	 */
    	render: function () {    		
    		this.$el.html(this.template({
    			isSortable : this.column.get("sortable"),
    			label : this.column.get("label"),
                id : this.column.get("name") + '_' + 'search',
                noSearchAndSort: this.tooltip ? this.tooltip.noSearchAndSort : null
            }));
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
            this.$el.tooltip('destroy');
            Backgrid.HeaderCell.prototype.remove.apply(this, arguments);
        }
    });
	
	return NumberHeaderCell;
});