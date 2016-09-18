/**
 * An extended header cell for filtering dates
 */
define(['backgrid',
        'hbs!utils/backgrid/integerHeaderCell',
        'app'
        ], function (BackgridRef, compiledTemplate, Gloria) {
	
    var IntegerHeaderCell = Backgrid.HeaderCell.extend({
		initialize : function(options) {
			this.template = compiledTemplate;
			IntegerHeaderCell.__super__.initialize.call(this, options);
		},

		events: {
    		"click a": "onClick",
    		"change input" : "onIntegerFilter",
       		"keyup input" : "onIntegerFilter"
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
    		var integerString = target.value;
    	    
    	    var intRegex = /^-?\d+$/;
    	    
    	    // Check if the value is an integer
    	    if(intRegex.test(integerString)) {
    	    	this.collection.queryParams[this.column.attributes.name] = parseInt(integerString);
    	    } else {
    	    	this.collection.queryParams[this.column.attributes.name] = null;
    	    }
    	    this.collection.state.currentPage = 1; 
    	    
    	    // Client mode filter!
    	    if(that.collection.mode == 'client') {
    	    	var models = this.collection.fullCollection.filter(function(model) {
    	    		try {
    	    			return (parseInt(model.get(that.column.attributes.name)) == parseInt(integerString)) || !integerString;
    	    		} catch(e) {
    	    			return false;
    	    		};
    	    	});
    	    	that.idle = true;
    	    	this.collection.reset();
    	    	this.collection.add(models);
    	    	return this.collection.models;
    	    } else {	// Server mode filter!
    	    	// if(String(this.collection.url).indexOf("undefined") == -1){
		    	    this.collection.fetch({reset: true}).always(function() {
		    	    	// If search string has changed while fetching, perform a new search, else set the
		    	    	// search input to idle and wait for new input
		    	    	if (integerString != target.value) {
		    	    		setTimeout(function(){that.performSearch(target);}, 250); // Simulate threading
		    	    	} else {
		    	    		that.idle = true;
		    	    	}
		    	    });
		        // }
    	    }
    	},

    	/**
    	 * Perform seach when filter changes
    	 */
    	onIntegerFilter : function(e) {
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
    		//this.$el.css('width', '1px');
    		this.$el.append(
    		        this.template({
    		        	isSortable : this.column.get("sortable"),
    		            label : this.column.get("label"),
                            id : this.column.get("name") + '_' + 'search'
                        }));
    		this.delegateEvents();
    		return this;
    	}
    });
	
	return IntegerHeaderCell;
});