/**
 * An extended header cell for Dropdown
 */
define(['app',
        'jquery',
        'backgrid',
        'select2',
        'i18next',
        'hbs!utils/backgrid/dropdownHeaderCell'
], function(Gloria, $, BackgridRef, select2, i18n, compiledTemplate) {

	var DropdownHeaderCell = Backgrid.HeaderCell.extend({

		initialize : function(options) {
			var that = this;
			this.template = compiledTemplate;
			this.tooltip = options.tooltip;
			this.listenTo(options.column, 'ControlPanel:filter:remove', this.removeFilter);
			Backgrid.HeaderCell.prototype.initialize.call(this, options);
			this.listenTo(this.collection, 'change', function() {
				that.render();
			});
		},

		events : {
			'click a' : 'onClick'
		},
		
		removeFilter : function() {
			try {
				if (this.column.type == 'select') { // Select the first item in the dropdown
					this.$el.find('option:eq(0)').prop('selected', true);
				} else {
					var val = i18n.t('Gloria.i18n.all');
					this.$el.find('#' + this.column.attributes.name).select2('data', {"id" : "", "text" : val});
				}
			} catch (e) {
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
    		return searchTerm;
		},

		// Filter event handler
		onStringFilter : function(e) {
			e.preventDefault();
			var contentString = e.target.value;
			contentString = contentString ? contentString.replace(/^\s+|\s+$/g, '') : '';
			var stringRegex = /^.+$/;
			if (stringRegex.test(contentString)) {
				this.collection.queryParams[this.column.attributes.name] = contentString;
				 // Update local storage query param value
        	    this.updateSessionStorageQueryParam(this.collection, this.column.get('name'), contentString);
				Gloria.trigger('backgrid:HeaderCellSearchApplied');
			} else {
				this.collection.queryParams[this.column.attributes.name] = null;
				 // Update local storage query param value
        	    this.updateSessionStorageQueryParam(this.collection, this.column.get('name'), null);
				Gloria.trigger('backgrid:HeaderCellSearchRemoved');
			}
			this.collection.state.currentPage = 1;
			this.collection.fetch({reset: true});
		},

		render : function() {
			var that = this;
			var selected = this.collection.queryParams[this.column.get('name')] || this.getHeaderCellSearchTerm(this.collection, this.column);
			this.$el.html(this.template({
				isSortable : this.column.get('sortable'),
				id : this.column.attributes.name,
				label : this.column.attributes.label,
				select : this.column.type == 'select'
			}));
			
			if(this.tooltip && this.tooltip.tooltipText){
                this.$el.tooltip({
                    placement: 'top',
                    container: 'body',
                    trigger: 'hover',
                    title: this.tooltip.tooltipText  
                });                
            }
			
			if(this.column.type == 'select') {
				this.$el.find('#' + this.column.attributes.name).empty()
					.append('<select class="form-control"></select>');
				var $element = this.$el.find('#' + this.column.attributes.name).find('select');
				
				if(!that.column.noAll) { // --- All --- options and value can be passed by caller! // select.
					var option = $('<option></option>');
					option.attr('value', '').text(i18n.t('Gloria.i18n.all'));
					$element.append(option);
				}			
				
				_.each(that.column.defaultData, function(item, index) {
					var option = $('<option></option>');
					option.attr('value', item.id).text(item.text);
					if (selected == item.id) {
						option.attr('selected', 'selected');
					}
					$element.append(option);
				}, this);
				$element.on('change', function(e) {
					that.onStringFilter(e);
				});
				return this;
			}

			var select2Data = []; // for select2
			// Add an item 'All' to dropdown list for fetching all items!
			var obj = {};
			obj.id = '';
			obj.text = i18n.t('Gloria.i18n.all');
			select2Data.push(obj);

			// Check if custom data has been provided for dropdown list
			if (this.column.defaultData) {
				select2Data.push.apply(select2Data, this.column.defaultData);
			} else {
				var items = _.uniq(this.collection.pluck(this.column.attributes.name));
				var unsortedItems = [];

				// Check if the item having <blank> value needs to be shown in dropdown list
				_.each(items, function(item) {
					var obj = {};
					obj.id = item ? item.toString() : ' ';
					obj.text = item ? item.toString() : ' ';
					if (item)
						unsortedItems.push(obj);
				});

				// Sort the list by ascending order
				unsortedItems.sort(function(obj1, obj2) {
					return obj1.text - obj2.text;
				});

				select2Data.push.apply(select2Data, unsortedItems);
			}

			// Any custom select2Options supplied?
			var selectOptions;
			if (this.column.select2Options) {
				selectOptions = {
					data : select2Data,
					minimumResultsForSearch : this.column.select2Options.minimumResultsForSearch ? this.column.select2Options.minimumResultsForSearch : -1
				};
				if (this.column.select2Options.formatResult)
					selectOptions.formatResult = this.column.select2Options.formatResult;
				if (this.column.select2Options.formatSelection || this.column.select2Options.formatResult)
					selectOptions.formatSelection = this.column.select2Options.formatSelection || this.column.select2Options.formatResult;
				if (this.column.select2Options.escapeMarkup)
					selectOptions.escapeMarkup = this.column.select2Options.escapeMarkup;
				if (this.column.select2Options.dropdownAutoWidth)
                    selectOptions.dropdownAutoWidth = this.column.select2Options.dropdownAutoWidth;
				// Or else default dropdown!
			} else {
				selectOptions = {
					data : select2Data,
					minimumResultsForSearch : -1
				};
			}

			this.select = this.$el.find('#' + this.column.attributes.name).select2(selectOptions).on('change', function(e) {
				that.onStringFilter(e);
			});

			if (selected)
				this.select.select2('val', selected);
			
			return this;
		},

		remove : function() {
		    this.$el.tooltip('destroy');
			this.select && this.select.select2('destroy');
			return Backgrid.HeaderCell.prototype.remove.apply(this, arguments);
		}
	});

	return DropdownHeaderCell;
});