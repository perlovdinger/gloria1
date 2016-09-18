define(['jquery',
        'backgrid',
        'bootstrap',
        'i18next',
        'datepicker'        
], function($, Backgrid, Bootstrap, i18n, Datepicker) {

    var DateCellEditor = Backgrid.InputCellEditor.extend({
    	
    	initialize: function(options) {
    		this.datepickerOptions = (options && options.datepickerOptions) || {};
    		return Backgrid.InputCellEditor.prototype.initialize.apply(this, arguments);
    	},
        
        events: {            
            "changeDate": "onChange",            
            "hide": "onClose",
            "dblclick": "doubleClick"
        },
        	                           
        doubleClick: function(e) {
            e.preventDefault();
            e.stopPropagation();
            e.stopImmediatePropagation();
        },
        
        onChange: function(e) {
            this.hasChanged = true;
            var formatter = this.formatter;
            var model = this.model;
            var column = this.column;
                    
            e.preventDefault();
            e.stopPropagation();                
            
            var val = formatter.toRaw(this.$el.val(), model);            
            if (_.isUndefined(val)) {
                model.trigger("backgrid:error", model, column, val);
            } else {
                model.set(column.get("name"), val);
                model.trigger("backgrid:edited", model, column, new Backgrid.Command({
                    keyCode: 13
                }));
            }
        },
        
        onClose: function(e) {
            if(this.hasChanged) return;
            
            var model = this.model;
            var column = this.column;
            e.stopPropagation();
            model.trigger("backgrid:edited", model, column, new Backgrid.Command({
                keyCode: 27
            }));            
        },
        
        render: function() {
            this.$el.datepicker(this.datepickerOptions);            
            this.delegateEvents();
            return this;
        },
        
        postRender: function (model, column) {
            var previousValue = this.model.get(this.column.get("name"));
            
            if (column == null || column.get("name") == this.column.get("name")) {
                if(previousValue)
                    this.$el.datepicker('update', this.formatter.fromRaw(previousValue));
                
                this.$el.datepicker('show');
            }
            return this;
        },
        
        remove: function() {
            this.$el.datepicker('remove');
            Backgrid.CellEditor.prototype.remove.apply(this, arguments);
        }
    });

    return DateCellEditor;
});