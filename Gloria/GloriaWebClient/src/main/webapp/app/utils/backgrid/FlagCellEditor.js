define(['jquery',
        'backgrid',
        'bootstrap',
        'select2',        
        'hbs!utils/backgrid/FlagCellEditor'
], function($, Backgrid, Bootstrap, Select2, FlagCellEditorTemplate) {

    var FlagCellEditor = Backgrid.CellEditor.extend({
        
        initialize: function(options) {
            options || (options = {});
            Backgrid.CellEditor.prototype.initialize.apply(this, arguments);
            this.dropDownOptions = this.makeOptions(options);
        },
        
        events: {            
            "change": "onChange",
            // Th below event (blur) is not catched by listener. Instead as a work around (close) has been used.
            //"select2-blur": "onBlur",
            "select2-close": "onDropDownClose"
        },
        
        onChange: function(e) {            
            var model = this.model;
            var column = this.column;
                    
            e.preventDefault();
            e.stopPropagation();
            $('.tooltip').hide(); //TODO: not correct way to implemet
            var val = this.$el.select2('val');              
            if (_.isUndefined(val)) {
                model.trigger("backgrid:error", model, column, val);
            } else {
                val = val == 'DEFAULT' ? '' : val;
                model.set(column.get("name"), val);
                model.trigger("backgrid:edited", model, column, new Backgrid.Command({
                    keyCode: 13
                }));
            }            
        },
        
        onDropDownClose: function(e) {
            var model = this.model;
            var column = this.column;
            $('.tooltip').hide(); //TODO: not correct way to implemet
            var previousValue = model.get(this.column.get("name"));
            var val = this.$el.select2('val');
            val = val || val.length > 0 ? val : null;
            var isChanged = (previousValue !== val);
            if(!isChanged) {
                e.stopPropagation();
                model.trigger("backgrid:edited", model, column, new Backgrid.Command({
                    keyCode: 27
                }));
            }
        },
        
        render: function() {
            this.$el.select2(this.dropDownOptions);
            this.delegateEvents();
            $('.tooltip').hide(); //TODO: not correct way to implemet
            return this;
        },
        
        postRender: function (model, column) {
            if (column == null || column.get("name") == this.column.get("name")) {
                this.$el.select2('val', this.model.get(this.column.get("name")));
                this.$el.select2('open');
            }
            return this;
        },
        
        makeFlagArray: function(flags) {
            var flagsArray = [];            
            _.each(flags, function(value, key){
                if(value) {                    
                        flagsArray.push({
                            id: key,                                            
                            text: FlagCellEditorTemplate({value: value})
                        });                   
                }
            });
            
            return flagsArray;
        },
        
        makeOptions: function(options) {
          var flagsArray = this.makeFlagArray(options.flags || {});
          var defaultOptions = {
              escapeMarkup: function(text) { 
                  return text; 
              },
              
              minimumResultsForSearch: -1,
              
              data: flagsArray,
              
              width: 'element'
          };
          
          return _.extend(defaultOptions, options);
        },
        
        remove: function() {
            this.$el.select2('destroy');
            $('.tooltip').hide(); //TODO: not correct way to implemet
            Backgrid.CellEditor.prototype.remove.apply(this, arguments);
        }
    });

    return FlagCellEditor;
});