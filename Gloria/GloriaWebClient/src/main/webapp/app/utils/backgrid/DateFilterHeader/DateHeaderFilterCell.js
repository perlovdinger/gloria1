define(['jquery',
        'underscore',
        'backgrid',
        'bootstrap',
        'i18next',
        'utils/DateHelper',
        'utils/backgrid/DateFilterHeader/DateFilterPopup',
        'hbs!utils/backgrid/DateFilterHeader/DateHeaderFilterCell'
], function($, _, Backgrid, Bootstrap, i18n, DateHelper, DateFilterPopup, DateHeaderFilterCellTemplate) {
    
    var DateHeaderFilterCell = Backgrid.HeaderCell.extend({
        
        className: (Backgrid.HeaderCell.prototype.className || '') + ' xs-fixedWidth-Date',
        
        template: DateHeaderFilterCellTemplate,
        
        events: {
            "click a": "onClick",
            "click .open-filter i": "onCalendarClick"                        
        },
        
        initialize: function(options) {
            Backgrid.HeaderCell.prototype.initialize.apply(this, arguments);
            this.listenTo(this.collection, 'filter:date', this.update);
            this.listenTo(options.column, 'ControlPanel:filter:remove', this.removeFilter);
            this.dateFilterPopup = new DateFilterPopup({
                collection: this.collection,
                dateFilterBindings: options.dateFilterBindings,
                headerTitle: options.headerTitle
            });
        },
        
        removeFilter : function() {
        	 this.$('i.fa-filter').addClass('hide');
		},
        
        onCalendarClick: function(e) {            
            this.dateFilterPopup.toggle({                
                parentEl: this.$el
            });            
        },        
        
        hasDateFilter: function() {
        	var isStoredInSessionStorage = false;
        	var isInCollectionQueryParam = false;
        	var filterStorageKey = this.collection.app + '.' + this.collection.subapp + '.' + this.collection.filterKey + '.filters';
			var qryParams = window.sessionStorage.getItem(filterStorageKey);
			if (qryParams) {
				var parsedParams = JSON.parse(qryParams);
				isStoredInSessionStorage = (parsedParams[this.dateFilterPopup.dateFilterBindings.on] || parsedParams[this.dateFilterPopup.dateFilterBindings.from] 
				|| parsedParams[this.dateFilterPopup.dateFilterBindings.to] || parsedParams[this.dateFilterPopup.dateFilterBindings.all]);
			}
			isInCollectionQueryParam = (this.collection.queryParams[this.dateFilterPopup.dateFilterBindings.on]
										|| this.collection.queryParams[this.dateFilterPopup.dateFilterBindings.from] 
										|| this.collection.queryParams[this.dateFilterPopup.dateFilterBindings.to]
										|| this.collection.queryParams[this.dateFilterPopup.dateFilterBindings.all]);
            return isInCollectionQueryParam || isStoredInSessionStorage;
        },
        
        update: function() {
            if(this.hasDateFilter())
                this.$('i.fa-filter').removeClass('hide');
            else 
                this.$('i.fa-filter').addClass('hide');
        },
        
        render: function() {
            this.$el.html(this.template({
                label: this.column.get('label'),
                sortable: this.column.get('sortable'),
                filtered: this.hasDateFilter()
            }));
            
            this.delegateEvents();
            return this;
        },
        
        remove: function() { 
            this.dateFilterPopup.remove();
            Backgrid.HeaderCell.prototype.remove.apply(this, arguments);
        }
        
    }); 
    
    return DateHeaderFilterCell;
});