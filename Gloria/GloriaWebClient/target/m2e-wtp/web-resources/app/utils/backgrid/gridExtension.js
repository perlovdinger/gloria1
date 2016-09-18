define(['underscore', 'backbone'], function(_, Backbone) {
    
    var initialize = function() {
        
        Backgrid.Extension.SelectAllHeaderCell.prototype.enableMultiPageSelection = function() {
            this.isMultiPageSelection = true;
            this.stopListening(this.collection, "remove");
            this.stopListening(this.collection, "backgrid:refresh");
            this.listenTo(this.collection, "backgrid:refresh", function() {
                this.$el.find("input[type=checkbox]").prop("checked", false);
                var selectedAll = true;
                for ( var i = 0; i < this.collection.length; i++) {
                    var model = this.collection.at(i);
                    if (this.selectedModels[model.id || model.cid]) {
                        model.trigger("backgrid:select", model, true);
                    } else {
                        selectedAll = false;
                    }
                }
                this.$el.find("input[type=checkbox]").prop("checked", selectedAll);
            });
        };

        Backgrid.Extension.SelectAllHeaderCell.prototype.disableMultiPageSelection = function() {
            if (this.isMultiPageSelection) {
                this.listenTo(this.collection, "remove", function(model) {
                    delete this.selectedModels[model.id || model.cid];
                });
                this.stopListening(this.collection, "backgrid:refresh");
                this.listenTo(this.collection, "backgrid:refresh", function() {
                    var checked = this.$el.find("input[type=checkbox]").prop("checked");
                    for ( var i = 0; i < this.collection.length; i++) {
                        var model = this.collection.at(i);
                        if (checked || this.selectedModels[model.id || model.cid]) {
                            model.trigger("backgrid:select", model, true);
                        }
                    }
                });
            }
        };

        Backgrid.Grid.prototype.enableMultiPageSelection = function() {
        	this.selectedModels = [];
            var selectAllHeaderCell;
            var headerCells = this.header.row.cells;
            for ( var i = 0, l = headerCells.length; i < l; i++) {
                var headerCell = headerCells[i];
                if (headerCell instanceof Backgrid.Extension.SelectAllHeaderCell) {
                    selectAllHeaderCell = headerCell;
                    selectAllHeaderCell.enableMultiPageSelection();
                    break;
                }
            }
        };

        Backgrid.Grid.prototype.disableMultiPageSelection = function() {
            var selectAllHeaderCell;
            var headerCells = this.header.row.cells;
            for ( var i = 0, l = headerCells.length; i < l; i++) {
                var headerCell = headerCells[i];
                if (headerCell instanceof Backgrid.Extension.SelectAllHeaderCell) {
                    selectAllHeaderCell = headerCell;
                    selectAllHeaderCell.disableMultiPageSelection();
                    break;
                }
            }
        };

        /**
         * Before calling this method make sure that the grid is multipage selectable. You can enable this feature by
         * calling enableMultiPageSelection method after creating a new instance of a grid. This method returns all the
         * selected models' IDs of the collection.
         * 
         * @returns An array of string containing selected models' ids.
         */
        Backgrid.Grid.prototype.getAllSelectedModelIds = function() {
            var selectAllHeaderCell;
            var headerCells = this.header.row.cells;
            for ( var i = 0, l = headerCells.length; i < l; i++) {
                var headerCell = headerCells[i];
                if (headerCell instanceof Backgrid.Extension.SelectAllHeaderCell) {
                    selectAllHeaderCell = headerCell;
                    break;
                }
            }

            var result = [];
            if (selectAllHeaderCell) {
                for ( var modelId in selectAllHeaderCell.selectedModels) {
                    result.push(modelId);
                }
            }

            return result;
        };
        
        /**
         * Before calling this method make sure that the grid is multipage selectable. You can enable this feature by
         * calling enableMultiPageSelection method after creating a new instance of a grid. This method returns all the
         * selected models.
         * 
         * @returns An array of selected models.
         */
        Backgrid.Grid.prototype.getAllSelectedModels = function() {
    		var collectionTemp = new Backbone.Collection(this.selectedModels);
    		collectionTemp.remove(this.collection.models, {silent: true});
    		this.selectedModels = collectionTemp.models;
    		this.selectedModels = this.selectedModels.concat(
    				this.getSelectedModels().filter(function(modl) {
					return modl != undefined;
				})
			);
            return this.selectedModels;
        };
        
        /**
         * This method returns the selected models on the current page.
         * 
         * @returns An array of Backbone Models.
         */
        Backgrid.Grid.prototype.getCurrentPageSelectedModels = function() {
            var selectAllHeaderCell;
            var headerCells = this.header.row.cells;
            for ( var i = 0, l = headerCells.length; i < l; i++) {
                var headerCell = headerCells[i];
                if (headerCell instanceof Backgrid.Extension.SelectAllHeaderCell) {
                    selectAllHeaderCell = headerCell;
                    break;
                }
            }

            var result = [];
            if (selectAllHeaderCell) {
                var collection = this.collection.fullCollection || this.collection;
                for ( var modelId in selectAllHeaderCell.selectedModels) {
                    var model = collection.get(modelId);
                    if (model) result.push(model);
                }
            }

            return result;
        };
        
        Backgrid.Grid.prototype.clearAllSelectedModels = function () {
        	_.each(this.getAllSelectedModels(), function(model) {
        		model.trigger("backgrid:select", model, false);
			});
        	this.selectedModels = [];
        	var selectAllHeaderCell;
            var headerCells = this.header.row.cells;
            for (var i = 0, l = headerCells.length; i < l; i++) {
              var headerCell = headerCells[i];
              if (headerCell instanceof Backgrid.Extension.SelectAllHeaderCell) {
                selectAllHeaderCell = headerCell;
                break;
              }
            }
            
            if (selectAllHeaderCell && selectAllHeaderCell.selectedModels) {
            	for(var key in selectAllHeaderCell.selectedModels) {
            		delete selectAllHeaderCell.selectedModels[key];
            	}
            }
        };        
    };
    
    return {
        initialize: initialize 
    };
});