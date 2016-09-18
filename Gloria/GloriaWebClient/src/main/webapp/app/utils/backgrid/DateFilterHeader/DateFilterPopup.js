define(['jquery',
        'underscore',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'utils/DateHelper',
        'hbs!utils/backgrid/DateFilterHeader/DateFilterPopup'
], function($, _, Backbone, Marionette, Bootstrap, i18n, DateHelper, DateFilterPopupTemplate) {
    
    var DateHeaderFilterCell = Marionette.View.extend({
        
        template: DateFilterPopupTemplate,
        
        isShown: false,
        
        className: 'date-filter left-popover',
        
        events: {
            "shown.bs.popover": "onShow",
            "hide.bs.popover": "onHide",
            "change [name='filterType']": 'onSelectFilterType',
            "click #filter": "onFilterClick",
            "click #resetFilter": "onResetFilterClick",
            "click #close": "onCloseClick",
            "click": "onClick",
            // Prevent hiding the popover on selecting a date from datepicker
            "changeDate .js-date": "onDatepickerChanged",
            "clearDate .js-date": "onDatepickerChanged",
            "changeYear .js-date": "onDatepickerChanged",
            "changeMonth .js-date": "onDatepickerChanged",
            "show .js-date": "onDatepickerChanged",
            "hide .js-date": "onDatepickerChanged"            	
        },
        
        initialize: function(options) {
            options || (options = {});
            Marionette.View.prototype.initialize.apply(this, arguments); 
            this.dateFilterBindings = options.dateFilterBindings;
            this.title = options.headerTitle;
            this.templateEl = Backbone.$(this.template({
            	dateFilterBindings: this.dateFilterBindings
            }));             
        },
        
        setParent: function(parentEl) {            
            this.parentEl = parentEl;            
            this.$el.appendTo(this.parentEl); 
        },
              
        popOverOptions: function() {
            return {
                html : true,
                container: this.$el,
                placement: 'bottom',
                trigger: 'manual',            
                title: this.title || this.templateEl.find(".popover-title").html(),
                content: this.templateEl.find(".popover-content").html()
            };            
        },
        
        onClick: function(e) {
        	e.originalEvent && (e.originalEvent.popupClicked = true);
        }, 
        // Prevent hiding the popover on selecting a date from datepicker
        onDatepickerChanged: function(e) {
        	this.datepickerChanged = true;
        },
        
        onShow: function(e) {            
            this.$('.js-date').datepicker();
            
            var on = this.collection.queryParams[this.dateFilterBindings.on] || false;
            var from = this.collection.queryParams[this.dateFilterBindings.from] || false;
            var to = this.collection.queryParams[this.dateFilterBindings.to] || false;
            var all = this.collection.queryParams[this.dateFilterBindings.all] || false;
            
            if(on) {                
                this.$('#on').datepicker('update', DateHelper.formatDate(on));
                this.$('#exactDate').prop('checked', true);
                this.$('#on').removeAttr('disabled');
            }            
            if(from) {
                this.$('#from').datepicker('update', DateHelper.formatDate(from));
                this.$('#rangeDate').prop('checked', true);
                this.$('#from').removeAttr('disabled');
                this.$('#to').removeAttr('disabled');
            }
            if(to) {
                this.$('#to').datepicker('update', DateHelper.formatDate(to));
                this.$('#rangeDate').prop('checked', true);
                this.$('#from').removeAttr('disabled');
                this.$('#to').removeAttr('disabled');
            }
            if(all) {
                this.$('#allDate').prop('checked', true);
            }            
            // Attach event handler to document object to close the popup when user clicks outside
            Backbone.$(document).on('click.' + this.cid, _.bind(function(e) {
            	e.originalEvent || (e.originalEvent = {});
            	if(!e.originalEvent.popupClicked && !this.datepickerChanged 
            			&& !Backbone.$(e.target).parents('div.datepicker').length > 0 && this.$el.has(document.activeElement).length == 0) {
            		this.toggle();
            	}
            	this.datepickerChanged = false;
            }, this));
        },
        
        onHide: function(e) {
            // To prevent conflict between hide events of datepickers inside the popover 
            // and hide effect of popover itself.
            if(e.target == this.el) {
                this.$('.js-date').datepicker('remove');                
                Backbone.$(document).off('click.' + this.cid);
            }
        },
        
        onSelectFilterType: function(e) {
        	e.preventDefault();
			if(e.currentTarget.value == 'exactDate') {
				this.$('#from').val('');
				this.$('#to').val('');
				this.$('#from').attr('disabled', 'disabled');
				this.$('#to').attr('disabled', 'disabled');
				this.$('#on').removeAttr('disabled');
			} else if(e.currentTarget.value == 'rangeDate') {
				this.$('#on').val('');
				this.$('#on').attr('disabled', 'disabled');
				this.$('#from').removeAttr('disabled');
				this.$('#to').removeAttr('disabled');
			} else {
				this.$('#on').val('');
				this.$('#from').val('');
				this.$('#to').val('');
				this.$('#on').attr('disabled', 'disabled');
				this.$('#from').attr('disabled', 'disabled');
				this.$('#to').attr('disabled', 'disabled');
			}
		},
        
        onFilterClick: function(e) {
            this.toggle();
            this.dateFilter();
    		this.collection.trigger('QueryParams:changed');
        },
        
        onResetFilterClick: function(e) {  
            this.toggle();
            this.clearFilter();
            this.collection.trigger('QueryParams:changed');
        },
        
        onCloseClick: function(e) {
            this.toggle();
        },
        
        clearFilter: function() {
            this.removeFiltersFromCollection();
            this.filterAndFetchCollection();
        },
        
        removeFiltersFromCollection: function() {
            this.collection.queryParams[this.dateFilterBindings.on] = null;
            this.collection.queryParams[this.dateFilterBindings.from] = null;
            this.collection.queryParams[this.dateFilterBindings.to] = null;
            this.collection.queryParams[this.dateFilterBindings.all] = null;
            this.collection.trigger('QueryParams:reset', [this.dateFilterBindings.on, this.dateFilterBindings.from,
                                                          this.dateFilterBindings.to, this.dateFilterBindings.all]);
        },
        
        hasDateFilter: function() {
            return (this.collection.queryParams[this.dateFilterBindings.on] || 
                    this.collection.queryParams[this.dateFilterBindings.from] ||
                    this.collection.queryParams[this.dateFilterBindings.to] ||
                    this.collection.queryParams[this.dateFilterBindings.all]);
        },
        
        dateFilter: function() {
            this.removeFiltersFromCollection();
            
            var filterType = this.$('input[name="filterType"]:checked').val();
            var on = this.$('#on').val();
            var from = this.$('#from').val();
            var to = this.$('#to').val();
            var all = this.$('#all').val();
            if(filterType && filterType.toLowerCase() == 'exactdate') {
                if (on && DateHelper.isValidDate(on)) {
                    this.collection.queryParams[this.dateFilterBindings.on] = DateHelper.parseDate(on, true);
                } 
            } else if(filterType && filterType.toLowerCase() == 'rangedate') {
                if (from && DateHelper.isValidDate(from)) {
                    this.collection.queryParams[this.dateFilterBindings.from] = DateHelper.parseDate(from, true);                    
                } 
                
                if(to && DateHelper.isValidDate(to)) {
                    this.collection.queryParams[this.dateFilterBindings.to] = DateHelper.parseDate(to, true);
                } 
            } else {
            	this.collection.queryParams[this.dateFilterBindings.all] = all;
            }            
                         
            this.filterAndFetchCollection();                
        },
        
        filterAndFetchCollection: function() {
            this.collection.state.currentPage = 1;    
            this.collection.fetch();
            this.collectionTriggerFilter();
        },
        
        collectionTriggerFilter: function() {
            this.collection.trigger('filter:date');
        },
        
        toggle: function(options) {
            if(this.isShown) {
                this.$el.popover('destroy');
            } else {
                this.setParent(options.parentEl);
                this.$el.popover(this.popOverOptions());
                this.$el.popover('show');
            }
            this.delegateEvents();
            this.isShown = !this.isShown;            
        }, 
        
        remove: function() {
            this.$el.popover('hide');
            this.$el.popover('destroy');        
            delete this.templateEl;            
            Backbone.$(document).off('.' + this.cid);
            Marionette.View.prototype.remove.apply(this, arguments);
        }        
    }); 
    
    return DateHeaderFilterCell;
});