define(['jquery',
        'backgrid',
        'bootstrap',
        'hbs!utils/backgrid/NestedGridCell'
], function($, Backgrid, Bootstrap, GridCellTemplate) {

    var NestedGridCell = Backgrid.Cell.extend({
        
        template: GridCellTemplate,
        
        events: {
            'dblclick': 'doubleClick'
        },
        
        doubleClick: function(e) {
            e.preventDefault();
            e.stopPropagation();
            e.stopImmediatePropagation();
        },
        
        nestedGridOptions: {},
        
        makeDOMObserver: function() {
            if(this.observer) return;
            this.observer = new MutationObserver(_.bind(function (mutations) {               
                _.find(mutations, function (mtn) {                    
                     if(mtn && mtn.addedNodes && mtn.addedNodes[0] && mtn.addedNodes[0].tagName == 'TABLE') {
                         this.attachResizeHandlers();     
                         return true;
                     }
                }, this);
              }, this));
        },
        
        observeDOMElement: function() { 
            this.observer.observe(this.$el.get(0), {
                subtree: true,
                childList: true,
                attributes: false,
                characterData: false
             });
        },
        
        attachResizeHandlers: function() {
        	// By removing the event listeners first and then attaching a new listener, 
        	// only one(the last) Nested grid cell is listening to events.
        	// This has been done for performance optimization.
            var parentTable = this.$el.closest('table');
            parentTable.off('resizing.nestedGrid');
            parentTable.on('resizing.nestedGrid', _.bind(this.resize, this));
            Backbone.$(window).off('resize.nestedGrid');
            Backbone.$(window).on('resize.nestedGrid', _.bind(this.triggerInitialResize, this));
            this.triggerInitialResize(); 
        },
        
        resizer: function(e) {     
        	try {
            	var leftTDId = this.columnMap[e.leftColumn.get(0).id];
                var rightTDId = this.columnMap[e.rightColumn.get(0).id];
                if(e && e.table && e.leftColumn && e.rightColumn && (leftTDId || rightTDId)) {
                	var leftColumn = e.table.find('td[id^="'+ leftTDId +'"]');
                	var rightColumn = e.table.find('td[id^="'+ rightTDId +'"]');
                	(window.requestAnimationFrame || window.setTimeout)(_.bind(function() {
                		var leftWidth = e.leftColumn.innerWidth();
                		var rightWidth = e.rightColumn.innerWidth();
                		leftColumn.css({width: leftWidth, maxWidth: leftWidth});
                		rightColumn.css({width: rightWidth, maxWidth: rightWidth}); 
                	}, this), 0);        	
                }
			} catch (e) {} 
        },
        
        resizerDebounce: _.debounce(function(e) {
        	return this.resizer(e);
    	}, 250),
        
        resize: function(e) {
        	if(e && e.initialResize) {
        		this.resizer(e);
        	} else {
        		this.resizerDebounce(e);
        	}
        },
        
        triggerInitialResize: _.debounce(function() {
        	var parentTable = this.$el.closest('table');
            _.each(this.columnMap, function(value, key) {
                parentTable.trigger({
                    type: 'resizing',
                    table: parentTable,
                    leftColumn: parentTable.find('th#' + key),
                    rightColumn: parentTable.find('th#' + key),
                    initialResize: true
                });
            }, this);
        }, 250),
        
        initialize: function(options) { 
            Backgrid.Cell.prototype.initialize.apply(this, arguments);
            if(this.nestedGridOptions && this.nestedGridOptions.collection) { 
                this.makeDOMObserver();
                this.observeDOMElement();
                this.makeGrid(this.nestedGridOptions);              
            }
            
            this.listenTo(this.nestedGridOptions.collection, 'backgrid:refresh', this.triggerInitialResize);
        },
        
        makeGrid: function(options) {            
            return this.grid = new Backgrid.Grid(options);
        },
        
        render: function() {            
            this.$el.html(this.template({cid: this.cid}));
            if(this.grid) {
                this.$('.nestedGrid').html(this.grid.render().$el);                
            } 
            this.delegateEvents();  
            
            return this;
        },
        
        removeDOMObservers: function() {
        	if(this.observer) {
        		this.observer.disconnect();
        		// Empty the queue of records
        		this.observer.takeRecords();            
        		delete this.observer;
        	}
        },
                
        remove: function () {
            this.$el.closest('table').off('resizing.nestedGrid');
            Backbone.$(window).off('resize.nestedGrid');
            this.stopListening();                       
            this.removeDOMObservers();
            this.grid.remove();
            return Backgrid.Cell.prototype.remove.apply(this, arguments);
        }
    });

    return NestedGridCell;
});