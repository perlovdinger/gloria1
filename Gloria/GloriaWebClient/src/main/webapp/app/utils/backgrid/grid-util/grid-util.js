define(['jquery', 'underscore', 'backbone', 'backgrid', 'utils/UserHelper'], function($, _, Backbone, Backgrid, UserHelper) {
	require.loadCss('utils/backgrid/grid-util/grid-util.css');
	Backgrid.Grid.prototype.enableGridColumnManipulation = function(options) {
		
		var grid = options && options.grid;
		var gridCID = '' + (grid ? grid.cid : Date.now());
		if(grid) {
			var origRemove = grid.remove;
			grid.remove = function() {
				$(window, document, "*").off('.' + gridCID);
				grid.columns.stopListening(grid.columns, 'reset', null);
				return origRemove.apply(this, arguments);
			};
		}
		
		// User inputs
		this.resizable = options.resizable;

		/**
		 * If resizable attr is passed, enable table column re-size feature
		 */
		if(this.resizable) {
			
			Backgrid.Grid.Resizable = function(options) {
				var that = this;
				grid.columns.listenTo(grid.columns, 'reset', function() {
					that.removeColumnWidths();
					that.init({grid: grid});
				});
				
				/**
				 * Resizable Default Options
				 */
				Backgrid.Grid.Resizable.prototype.defaults = {
					minWidth: 0, 					//minimum width value in pixels allowed for a column 
					headerOnly: false,				//specifies that the size of the the column resizing anchors will be bounded to the size of the first row 
					postbackSafe: false				//when it is enabled, table layout can persist after postback. 
	            };
				
				/**
				 * Resizable initialize
				 * entry method for Resizable feature set up
				 */
				Backgrid.Grid.Resizable.prototype.init = function(options) {
					
					// Do nothing, if context is not a TABLE!
					if(!options.grid.$el.is('table')) return; // tagName == 'TABLE
					
					// User inputs
					this.grid = options.grid;
					
					// app, subapp, id -> mainly used for storing table layout in local storage
					this.app = 'Gloria';
					this.subapp = Backbone.history.fragment.split('/')[0];
					this.id = this.grid.id;
					this.storageKey = this.app + '.' + this.subapp + '.' + this.id + '.' + UserHelper.getInstance().getUserId();
					
					this.destroy();
					
					// All options for Resizable
					this.resizableOptions =  $.extend(this.defaults, options.resizable);
					this.$table = this.grid.$el;
					
					// Configure Header
					this.configureHeaders();
					
					// Restore column width if postbackSafe is true!
					if(this.resizableOptions.postbackSafe) {
						this.restoreColumnWidths();
					}
					
					$(window).on('resize.' + gridCID, ((function(_this) {
				        return function() {
				          return _this.initResize();
				        };
				      })(this)));
					
					grid.listenTo(grid.collection, 'backgrid:refresh', ((function(_this) {
				        return function() {
				        	return _this.initResize();
				        };
					})(this)));
					
					this.initResize();
				};
				
				Backgrid.Grid.Resizable.prototype.destroy = function() {
					if(this.$handleContainer) {
						this.$handleContainer.remove();
					}
				};
				
				/**
				 * Configure Header
				 */
				Backgrid.Grid.Resizable.prototype.configureHeaders = function() {
					this.$tableHeaders = this.$table.find('tr th:visible');
					if(this.resizableOptions.postbackSafe) {
						this.setIdToNonIdTH(this.$tableHeaders);
					}
					this.assignPercentageWidths();
					return this.setupGrips();
				};
				
				Backgrid.Grid.Resizable.prototype.initResize = function() {
					// Sync grip widths
					var grips = this.syncGripWidths();					
					$(grips[0]).trigger({
					    type: 'mousedown',
					    firstRender: true
					});
                    $(grips[0]).trigger({
                        type: 'mousemove',
                        firstRender: true
                    });
                    $(grips[0]).trigger({
                        type: 'mouseup',
                        firstRender: true
                    });
				};
				
				/**
				 * Set id to element(s) having no id set, e.g. select-all-header-cell
				 * This is required to save the table layout in local storage
				 */
				Backgrid.Grid.Resizable.prototype.setIdToNonIdTH = function(tableHeaders) {
					tableHeaders.each(function(index, header) {
						if($(header).hasClass('select-all-header-cell')) {
							$(header).attr('id', 'selectAll');
						};
						
						if(!$(header)[0].id) {
						    $(header)[0].id = $(header).text().replace(' ', '');
						}
					});
				};
				
				/**
				 * Assign Percentage Widths - Style width in % not in PX, EM...
				 */
				Backgrid.Grid.Resizable.prototype.assignPercentageWidths = function() {
					var that = this;
					return this.$tableHeaders.each((function(_this) {
						return function(_, el) {
							var	$el = $(el);
							return that.setWidth($el[0], $el.outerWidth() / _this.$table.width() * 100);
						};
					})(this));
				};
				
				/**
				 * Set Width
				 */
				Backgrid.Grid.Resizable.prototype.setWidth = function(node, width) {
					if (typeof width === 'string') {
						width = parseFloat(width);
					}
					//width = (width && width.toFixed(2)) || 0;
					return node.style.width = "" + ( width || 0 ) + "%";
				};
				
				/**
				 * Parse Width : Save width style in % format!
				 */
				Backgrid.Grid.Resizable.prototype.parseWidth = function(node) {
					return parseFloat(node.style.width.replace('%', ''));
				};
				
				/**
				 * Set up the grips for user interaction!
				 * Now all columns are resizable
				 */
				Backgrid.Grid.Resizable.prototype.setupGrips = function() {
					var that = this;
					this.$table.before((this.$handleContainer = $("<div class='rc-handle-container' />"))); // Grip container
					this.$tableHeaders.each((function(_this) {
						return function(i, el) {
							var $handle;
							if (_this.$tableHeaders.eq(i + 1).length === 0) {
								return;
							}
							$handle = $("<div class='rc-handle' />"); // Grips
							$handle.data('th', $(el));
							return $handle.appendTo(_this.$handleContainer);
						};
					})(this));
					return this.$handleContainer.on('mousedown.' + gridCID, '.rc-handle', function(e) {
						that.listenMouseEvents(e);
					});
				};
				
				/**
				 * Sync Grip widths
				 */
				Backgrid.Grid.Resizable.prototype.syncGripWidths = function() {
					return this.$handleContainer.width(this.$table.width()).find('.rc-handle').each((function(_this) {
						return function(_, el) {
							var $el = $(el);
							if($el.data('th')) {
								return $el.css({
									left : $el.data('th').outerWidth() + ($el.data('th').offset().left - _this.$handleContainer.offset().left),
									height : _this.resizableOptions.headerOnly ? _this.$table.find('thead').height() 
											: (_this.$table.find('thead').height() + _this.$table.find('tbody').height())
								});
							}							
						};
					})(this));
				};
				
				/**
				 * Listen mouse events : mousemove, mouseup
				 */
				Backgrid.Grid.Resizable.prototype.listenMouseEvents = function(e) {	
				    e.preventDefault();
                    
                    var that = this;
                    var $currentGrip, $leftColumn, $ownerDocument, $rightColumn, newWidths, startPosition, widths;
                    $ownerDocument = $(e.currentTarget.ownerDocument);
                    startPosition = e.pageX;
                    $currentGrip = $(e.currentTarget);
                    $leftColumn = $currentGrip.data('th');
                    $rightColumn = this.$tableHeaders.eq(this.$tableHeaders.index($leftColumn) + 1);
                    
                    this.$table.addClass('resize-start');
                    
                    this.$table.trigger({
                        type: 'resizestart',
                        table: this.$table,
                        leftColumn: $leftColumn,
                        rightColumn: $rightColumn
                    });
                    
                    var leftColumnLeft = $leftColumn.offset().left;
                    var rightColumnRight = $rightColumn.offset().left + $rightColumn.width(); 
                    
                    // Clone the table and thead to measure the possible minimum width of left column and right column                    
                    var clonedTable = this.$table.clone().css({display: 'flex', visibility: 'hidden', width: '1px'}).width(1).appendTo('body');
                    clonedTable.find('tbody,caption').remove();                    
                    clonedTable.leftColumn = clonedTable.find('th#'+$leftColumn[0].id).width(1);
                    clonedTable.rightColumn = clonedTable.find('th#'+$rightColumn[0].id).width(1);
                    clonedTable.leftColumnWidth = clonedTable.leftColumn.outerWidth();
                    clonedTable.rightColumnWidth = clonedTable.rightColumn.outerWidth();
                    clonedTable.remove();
                
                
                    // The most left possible pixel for left column and the most right possible pixel for right column 
                    var minLeft = leftColumnLeft + clonedTable.leftColumnWidth;
                    var maxRight = rightColumnRight - clonedTable.rightColumnWidth;
                    
                    
                    widths = {
                        left : this.parseWidth($leftColumn[0]),
                        right : this.parseWidth($rightColumn[0])
                    };
                    
                    newWidths = {
                        left : widths.left,
                        right : widths.right
                    };                 
                    
                    var tableWidth = this.$table.width();
                    
                    // mousemove
                    $ownerDocument.on('mousemove.' + gridCID, (function(_this) {
                        return function(event) {
                            (window.requestAnimationFrame || window.setTimeout)(function() {
                                if(event.pageX > minLeft && event.pageX < maxRight) {                                
                                    var difference = (event.pageX - startPosition) / tableWidth * 100;
                                    that.setWidth($leftColumn[0], newWidths.left = widths.left + difference);
                                    that.setWidth($rightColumn[0], newWidths.right = widths.right - difference);
                                    that.$table.trigger({
                                        type: 'resizing',
                                        table: that.$table,
                                        leftColumn: $leftColumn,
                                        rightColumn: $rightColumn
                                    });
                                } 
                            }, 0);
                        };
                    })(this));
                    
                    // mouseup
                    return $ownerDocument.one('mouseup.' + gridCID, (function(_this) {
                        return function(event) {   
                        	that.$table.removeClass('resize-start');                            
                            $ownerDocument.off('mousemove.' + gridCID);
                            _this.syncGripWidths();
                            if(!e.firstRender && that.resizableOptions.postbackSafe && window.localStorage) {
                                _this.saveColumnWidths();
                            }
                            that.$table.trigger({
                                type: 'resizeend',
                                table: that.$table,
                                leftColumn: $leftColumn,
                                rightColumn: $rightColumn
                            });
                        };
                    })(this));
				};
				
				/**
				 * Restore Column Widths if postbackSafe is true & window.localStorage is available
				 */
				Backgrid.Grid.Resizable.prototype.restoreColumnWidths = function() {
					var that = this;
					var objTemp = JSON.parse(window.localStorage.getItem(that.storageKey));
					var object = objTemp && objTemp['resize'];
					return this.$tableHeaders.each((function(_this) {
						return function(_, el) {
							var $el = $(el);
							if (object) {
								return that.setWidth($el[0], object[$el[0].id]);
							};
						};
					})(this));
				};
				
				/**
				 * Save column widths if postbackSafe is true & window.localStorage is available
				 */
				Backgrid.Grid.Resizable.prototype.saveColumnWidths = function() {
					var that = this;
					var object = new Object();
					this.$tableHeaders.each((function(_this) {
						return function(_, el) {
							var $el = $(el);
							return object[$el[0].id] = that.parseWidth($el[0]);
						};
					})(this));
					// Set stringify data in local storage with key : <App>.<SubApp>.<ID>,
					// e.g. Gloria.Warehouse.QISettingspart
					if(that.subapp && that.id) { // If Grid <id> is set then store data in local storage!
						var objectTemp = JSON.parse(window.localStorage.getItem(that.storageKey)) || new Object();
						objectTemp['resize'] = object;
						window.localStorage.setItem(that.storageKey, JSON.stringify(objectTemp));
					}
				};
				
				Backgrid.Grid.Resizable.prototype.removeColumnWidths = function() {
					var objTemp = JSON.parse(window.localStorage.getItem(this.storageKey));
					var object = objTemp && objTemp['resize'];
					if(object && this.grid.$el.find('th.renderable').length != _.keys(object).length) {
						delete objTemp.resize;
						window.localStorage.setItem(this.storageKey, JSON.stringify(objTemp));
					}
				};
			};
			
			(new Backgrid.Grid.Resizable).init(options);
		}
	};

});
