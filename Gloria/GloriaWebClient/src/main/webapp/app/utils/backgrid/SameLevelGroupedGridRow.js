/**
 * The collection behind this row and the related grid must be soreted 
 * otherwise this module does not work as expected.
 * In most cases the groupBy attribute is the same as collection's comparator.
 * @property groupBy is the attribute which is used to compare 
 * this row's model with the previous row's model.   
 */
define(['app', 
        'utils/backgrid/clickableRow'        
], function (Gloria, ClickableRow) {

    var SameLevelGroupedGridRow = ClickableRow.extend({
        
        initialize: function(options) {
            options || (options = {});
            if(!options.groupBy) throw new Error('Group By attribute must be defined.');
            this.groupBy = options.groupBy;                        
            this.classNames = options.classNames || ['groupedRow-group-1', 'groupedRow-group-2'];           
            ClickableRow.prototype.initialize.apply(this, arguments);
            this.listenTo(this.model.collection, "backgrid:refresh", this.update);
        },
        
        render: function() {
            this.prevModel = this.getPreviousModelInCollection(this.model);
            return ClickableRow.prototype.render.apply(this, arguments);
        },
        
        remove: function() {
            this.prevModel = undefined;
            return ClickableRow.prototype.remove.apply(this, arguments);
        },
        
        update: function(options) {
            if(this.model.collection.length < 2) return;
            this.currentClassNameIndex = this.getPreviousRowClassName();
            var rowClassName = this.getRowClassName();
            this.$el.addClass(rowClassName);            
        },
        
        getPreviousModelInCollection: function(model) {
            var previousModel;
            if(model && model.collection) {
                var collection = this.model.collection;
                var modelIndex = collection.indexOf(this.model) || 0;
                previousModel = collection.at(modelIndex - 1);
            }
            return previousModel;
        },
        
        compareRows: function(model1, model2) {            
            return (model1 && model2 && model1.get(this.groupBy) && 
                    model1.get(this.groupBy) === model2.get(this.groupBy));
        },
        
        getRowClassName: function() {
            var className = '';            
            if(this.compareRows(this.model, this.prevModel)) {
                className =  this.classNames[this.currentClassNameIndex];
            } else {
                this.currentClassNameIndex = this.currentClassNameIndex < this.classNames.length-1 ? (this.currentClassNameIndex + 1) : 0;
                className =  this.classNames[this.currentClassNameIndex];
            }
            return className;
        },
        
        getPreviousRowClassName: function() {
            var previousRowElement = this.$el.prev('tr');
            if(previousRowElement.length === 0) return 0;
            var previousRowClassNames = previousRowElement.attr('class') || '';
            for(var i = 0; i < this.classNames.length; i++) {
                if(previousRowClassNames.indexOf(this.classNames[i]) !== -1) {
                    return i;                    
                }
            }            
        }
    });
    
    return SameLevelGroupedGridRow;
});
