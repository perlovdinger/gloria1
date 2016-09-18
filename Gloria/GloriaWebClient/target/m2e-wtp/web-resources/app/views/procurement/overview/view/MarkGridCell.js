define(['app',
        'jquery',
        'underscore',
        'backgrid',
        'bootstrap',
        'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Backgrid, Bootstrap, GloriaCollection) {

    var MarkGridCell = Backgrid.Cell.extend({

		template : _.template('<i class="fa fa-exclamation" data-toggle="popover"></i>'),
        
        initialize: function(options) {
        	this.model = options.model;
        	this.isInternalProcurer = options.isInternalProcurer;
            Backgrid.Cell.prototype.initialize.apply(this, arguments);
        },
        
        events : {
        	'mouseenter' : 'handleMouseEnter',
        	'mouseleave' : 'hanldeMouseLeave'
        },
        
        handleMouseEnter: function(e) {
        	var that = this;
        	e.stopPropagation();
			// Destroy popover if any before showing new one
			$('[data-toggle=popover]').each(function() {
				$(this).popover('destroy');
			});
			
			var contentVal = that.buildUrls(that.fetchChangeIds());
			// Initialize popover
			this.$el.find('i').popover({
            	placement: 'bottom',
            	animation: true,
            	trigger: 'manual',
            	html: true,
            	content: contentVal
			});
			// Show popover
			this.$el.find('i').popover('show');
		},
		
		hanldeMouseLeave: function(e) {
			e.stopPropagation();
			// Show popover for 5 sec before it is hidden
			setTimeout(function() {
				$('#' + e.currentTarget.id).find('i').popover('destroy');
			}, 5000);
		},
        
        /**
         * Fetch Change Id information
         * @returns collection in JSON format
         */
        fetchChangeIds: function () {
			var collection = new GloriaCollection();
			collection.url = '/procurement/v1/procurelines/' + this.model.get('id') + '/changeids/waitconfirm';
			collection.fetch({
				cache: false,
				async: false
			});
			return collection.toJSON();
		},
        
		/**
		 * Build Cell Urls
		 * @param jsonData
		 * @returns urlString
		 */
        buildUrls: function name(jsonData) {
        	var that = this;
        	var urlString = '';
        	_.each(jsonData, function(data) {
        			if(that.isInternalProcurer){
        				urlString += data.changeId;
        			}else{
                		var hash = 'procurement/overview/change/changeDetails/' + data.id;
        				urlString += '<a href="#' + hash + '">' + data.changeId + '</a>, ';
                	}
    		});
			return urlString.replace(/,\s*$/, '');
		},
		
		/**
		 * Bind Popover to Backgrid Cell
		 */
		bindPopover: function() {
			// Check if body is already registered with click event
			var hasClickEventRegistered = $._data($('body')[0], 'events')
						&& $._data($('body')[0], 'events').click.length != 0;
			// If not, register! - only once!
			if(!hasClickEventRegistered) {
				$('body').bind('click', function() {
					$('[data-toggle=popover]').each(function() {
						$(this).popover('destroy');
					});
				});
			};
		},
        
        render: function() {
            this.$el.html(this.template({
            	cid : this.cid
            }));
            this.bindPopover();
            this.delegateEvents();
            return this;
        },
        
        remove: function() {
        	this.$('i').popover('destroy');
        	$('body').unbind('click');
            return Backgrid.Cell.prototype.remove.apply(this, arguments);
        }
    });

    return MarkGridCell;
});