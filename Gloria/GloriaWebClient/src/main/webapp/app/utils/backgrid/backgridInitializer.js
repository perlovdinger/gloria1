define(['underscore', 'backbone'], function(_, Backbone) {
	
	var overrideInitializeAddNameId = function(Backgrid) {
		var origHeaderCell = Backgrid.HeaderCell.prototype.initialize;
        Backgrid.HeaderCell.prototype.initialize = function(options){
        	origHeaderCell.call(this, options);
            if(this.column && this.column.get('name')) {
                this.$el.attr('id', this.column.get('name'));
            }
        };
    	
        var orig = Backgrid.Cell.prototype.initialize;
        Backgrid.Cell.prototype.initialize = function(options){
            orig.call(this, options);
            if(this.column && this.column.get('name') && this.model) {
                this.$el.attr('id', this.column.get('name') + '_' + (this.model.id || this.model.cid));
            }
        };
	};
	
	var overrideInitializeSortDirection = function(Backgrid) {
		var onSorted = function(column, direction) {
            this.column.set("direction", null);
            if (column.cid == this.column.cid) column.set("direction", direction);
            this.setCellDirection(column, direction);
        };
        
        var hsorig = Backgrid.HeaderCell.prototype.initialize;
        Backgrid.HeaderCell.prototype.initialize = function(options){
            hsorig.call(this, options);
            this.listenTo(this.collection.fullCollection || this.collection, "backgrid:sorted", onSorted);
        };
	};
	
	var overrideEventsPreventDBClick = function(Backgrid) {
		Backgrid.InputCellEditor.prototype.events = { 
                "dblclick": "preventDLBClick" ,
                "blur": "saveOrCancel", 
                "keydown": "saveOrCancel" 
        }; 
            
        Backgrid.InputCellEditor.prototype.preventDLBClick = function(e) { 
            if(this.column && this.column.get('editable')) { 
                e.stopImmediatePropagation(); 
            }
        };
	};
	
	var overrideInitializeAddTooltip = function(Backgrid) {
		//Add Tooltip to Cell and HeaderCell and showing it only 
        //when cell content is larger that cell width
        var origTooltipCellInitialize = Backgrid.Cell.prototype.initialize;
        Backgrid.Cell.prototype.initialize = function(options){            
            (window.setImmediate || window.setTimeout)(_.bind(function() {
            	if(!this.tooltip && !options.column.tooltip) {
                    this.$el.tooltip({
                        placement: 'top',                    
                        container: 'body',
                        trigger: 'hover',
                        delay: { show: 500, hide: 0 },
                        title: _.bind(function() {
                        	var tootiptext = Backbone.$(this.$('>span')[0] || this.$('>div')[0] || this.$('>a')[0] || this.$el).text();
                        	var tooltiptextlength = $.trim(tootiptext);
                        	if(tooltiptextlength.length > 0)
                        		return Backbone.$(this.el.querySelector('span') || this.el.querySelector('div') || this.el.querySelector('a') || this.$el).text(); 
                        	else {
                        		return false;
                        	}
                            
                            //
                            //return Backbone.$(this.el.querySelector('span') || this.el.querySelector('div') || this.el.querySelector('a') || this.$el).text();
                        }, this)                    
                    });
                    this.events = _.extend({}, this.events);
                    this.events['show.bs.tooltip'] = defaultCellTooltip.onShowTooltip;
                    this.delegateEvents();
                }
            }, this), 0);
        	
            return origTooltipCellInitialize.call(this, options);            
        };
        
        var origTooltipHeaderCellInitialize = Backgrid.HeaderCell.prototype.initialize;        
        Backgrid.HeaderCell.prototype.initialize = function(options){ 
        	(window.setImmediate || window.setTimeout)(_.bind(function() {
        		if(!this.tooltip) {
        			this.$el.tooltip({
        				placement: 'top',
        				container: 'body',
        				trigger: 'hover',
        				delay: { show: 500, hide: 0 },
        				title: _.bind(function() {
        					return Backbone.$(this.$('>span')[0] || this.$('>a')[0] || this.$el).text();
        				}, this)                    
        			});
        			this.events = _.extend({}, this.events);
        			this.events['show.bs.tooltip'] = defaultCellTooltip.onShowTooltip;
        			this.delegateEvents();                
        		}
        	}, this), 0);
        	
            return origTooltipHeaderCellInitialize.call(this, options);            
        };  
        
        var defaultCellTooltip = { 
            onShowTooltip: function(e) {
                e.stopImmediatePropagation();
                if(this.$('table')[0]) {                    
                    return false;
                }
                var element = this.$('>span')[0] || this.$('>a')[0] || this.$('>div')[0] || this.el;
                this.isOverflow = element.clientWidth < element.scrollWidth;
                
                /**
                 * Workaround to fix IE and Firefox boxing model for "th" and "td" 
                 * when there is immediate text node under "th" and "td"
                */
                if(element.tagName == 'TH' || element.tagName == 'TD') {
                    var clonedElement = this.$el.clone()
                    .css({display: 'inline', width: 'auto', visibility: 'hidden'})
                    .appendTo('body');                    
                    this.isOverflow = clonedElement.width() >= this.$el.width();                    
                    clonedElement.remove();
                }
                
                if(this.tooltip || !this.isOverflow) {
                    return false;
                }
            }
       };
                
       var origTooltipCellRemove = Backgrid.Cell.prototype.remove;
       Backgrid.Cell.prototype.remove = function(options){  
            if(!this.tooltip) {
                this.$el.tooltip('destroy');
            }
            return origTooltipCellRemove.call(this, options);            
       }; 
       
       var origTooltipHeaderCellRemove = Backgrid.HeaderCell.prototype.remove;
       Backgrid.HeaderCell.prototype.remove = function(options){  
            if(!this.tooltip) {
                this.$el.tooltip('destroy');
            }
            return origTooltipHeaderCellRemove.call(this, options);            
        }; 
        //End Add Tooltip
	};
	
	var overrideGridInitializeFixedTableHeader = function(Backgrid) {		
		var origGridInit = Backgrid.Grid.prototype.initialize;
		Backgrid.Grid.prototype.initialize = function(options) {
			origGridInit.apply(this, arguments);		
			
			var elements = { 
				wrapper: null,
				table: null,
				mask: null
			};
			
			this.listenTo(this.columns, 'reset', _.debounce(_.bind(_.partial(this.reRenderFixedHeader, elements), this), 250));
			
			Backbone.$(window).on('resize.fixedTableHeader' + this.cid, _.debounce(_.bind(_.partial(this.reRenderFixedHeader, elements), this), 250));
			
			Backbone.$(window).on('scroll.fixedTableHeader' + this.cid, _.bind(_.partial(this.onWindowScroll, elements), this));
		};
		
		Backgrid.Grid.prototype.reRenderFixedHeader = function(elements, e) {
			removeFixedHeader(this, elements);
			Backbone.$(window).trigger('scroll');				
		};
		
		Backgrid.Grid.prototype.onWindowScroll = function(elements, e) {			
			var top = Backbone.$(window).scrollTop();
			var headerTop = this.header.$el.offset().top;
			var headerHeight = this.header.$el.height();
			var bodyHeight = this.body.$el.offset().top + this.body.$el.height();
			if(headerTop < top && bodyHeight - headerHeight > top && this.el.tHead) {
				addFixedHeader(this, elements);
			} else {
				removeFixedHeader(this, elements);
			}				
		};
		
		var origGridRemove = Backgrid.Grid.prototype.remove;
		Backgrid.Grid.prototype.remove = function() {
			// Remove listeners which are added by initialize method to window object
			Backbone.$(window).off('resize.fixedTableHeader' + this.cid);
			Backbone.$(window).off('scroll.fixedTableHeader' + this.cid);
			return origGridRemove.apply(this, arguments);
		};
		
		var addFixedHeader = function(grid, elements) {			
			if(!elements.wrapper) {	
				var tableId = grid.$el.attr('id') + grid.cid;
				Backbone.$('body').addClass('js-fixedTableHeaderShown');
				// Hide select2 dropdown if it is open
				var dropMask = Backbone.$('div#select2-drop-mask');
				if(dropMask.get(0)) {					
					dropMask.mousedown();
					dropMask.click();
				}				
				document.body.click();
				// Clone the entire table and remove tbody and tfoot to only keep thead
				elements.table = grid.$el[0].cloneNode(false);				
				elements.table = Backbone.$(elements.table).append(grid.$el.find('thead')[0].cloneNode(true));
				elements.table.attr('id', tableId);
				// Resize all cloned header cells by pixel based on the original header cells
				var origTHs = grid.$el.find('thead>tr')[0].cells;
				elements.table.find('thead').find('th').each(function(index, element) {
					var origWidth = origTHs[index].offsetWidth;
					Backbone.$(element).css({
						width: origWidth,
						maxWidth: origWidth
					});
				});						
				// Add a class to be able to refer to that later if needed
				elements.table.addClass('table-fixed-top');					
				// Create a div as a wrapper for the cloned table
				elements.wrapper = Backbone.$('<div></div>');
				elements.wrapper.css({
					position: 'fixed',
					top: '0px',		
					left: grid.$el.offset().left - window.pageXOffset,
					width: grid.$el.width()
				});
				// Create an extra div to mask the cloned table header to prevent user to interact with that
				elements.mask = Backbone.$('<div></div>');
				elements.mask.css({
					position: 'absolute',
					zIndex: '2',
					width: grid.$el.width(),
					height: grid.$('thead').height(),
					top: '0px',
					cursor: 'not-allowed'
				});
				// Append cloned table and mask div to the wrapper 
				elements.table.appendTo(elements.wrapper);
				elements.mask.appendTo(elements.wrapper);
				// Append wrapper div to parent element of the original table
				elements.wrapper.appendTo(grid.$el.parent());
				// Adds resizing listener to the original table to be able to reflect size changes on the cloned table
				grid.$el.on('resizing.fixedTableHeader' + grid.cid, _.bind(function(e) {
					var leftColumn = e.leftColumn;
					var rightColumn = e.rightColumn;
					var leftColumnWidth = leftColumn.outerWidth();
					var rightColumnWidth = rightColumn.outerWidth();						
					requestAnimationFrame(function() {
						if(elements.table) {
							elements.table.find('th#' + e.leftColumn.attr('id')).css({
								width: leftColumnWidth,
								maxWidth: leftColumnWidth
							});
							elements.table.find('th#' + e.rightColumn.attr('id')).css({
								width: rightColumnWidth,
								maxWidth: rightColumnWidth
							});
						}
					});						
				}, this));				
			}
			Backbone.$(window).on('scroll.horizontal' + grid.cid, function() {
				if(elements.wrapper) {
					elements.wrapper.css({
						left: grid.$el.offset().left - window.pageXOffset
					});
				}
			});
		};
		
		var removeFixedHeader = function(grid, elements) {
			if(elements.wrapper) {
				var body = Backbone.$('body');				
				body.removeClass('js-fixedTableHeaderShown');	
				elements.wrapper.remove();
				elements.table.remove();
				elements.mask.remove();
				elements.wrapper = null;
				elements.table = null;
				elements.mask = null;
				grid.$el.off('resizing.fixedTableHeader' + grid.cid);
				Backbone.$(window).off('scroll.horizontal' + grid.cid);
			}
		};
	};
	
	var overrideBodyRefreshAndRemove = function(Backgrid) {
		var origRefresh = Backgrid.Body.prototype.refresh;
    	Backgrid.Body.prototype.refresh = function() {
    		(window.setImmediate || window.setTimeout)(_.bind(origRefresh, this), 0);
    		return this;
    	};
/*    	var origRemove = Backgrid.Body.prototype.remove;
    	Backgrid.Body.prototype.remove = function() {
    		setTimeout(_.bind(origRemove, this), 0);
    		return this;
    	};*/
	};
	
	var overrideRowRender = function(Backgrid) {
		// Override Backgrid(v0.3.5) Row render method fully, to optimise performance.
		Backgrid.Row.prototype.render = function() {
			this.$el.empty();		    
		    for (var i = 0; i < this.cells.length; i++) {
		      this.el.appendChild(this.cells[i].render().el);
		    }
		    this.delegateEvents();
		    return this;
		};
	};
	
	var overrideBodyRender = function(Backgrid) {
		// Override Backgrid(v0.3.5) Body render method fully, to optimise performance.
		Backgrid.Body.prototype.render = function() {
			this.$el.empty();			
			for (var i = 0; i < this.rows.length; i++) {
		      this.el.appendChild(this.rows[i].render().el);
		    }				
		    this.delegateEvents();			    
		    this.collection.trigger('backgrid:rendered:body');
			    
		    return this;
		};
	};
	
	/**
     * Called when backgrid is loaded
     */
    var initialize = function(Backgrid) {    	
    	overrideInitializeAddNameId(Backgrid);
    	overrideInitializeSortDirection(Backgrid);
    	overrideEventsPreventDBClick(Backgrid);
    	overrideInitializeAddTooltip(Backgrid);
    	overrideGridInitializeFixedTableHeader(Backgrid);
    	overrideBodyRefreshAndRemove(Backgrid);
    	overrideRowRender(Backgrid);
    	overrideBodyRender(Backgrid);
    };
    
    return {
    	initialize : initialize
    };
});