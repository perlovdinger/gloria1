define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'backgrid',
        'select2'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Backgrid, select2) {

    var TransportLabelSelectCellEditor = Backgrid.CellEditor.extend({

        initialize : function(options) {  
            this.collection = options.transportLabels;
            this.listenTo(this.collection , 'reset', function(transportLabels) {
				this.render();
			});
            this.listenTo(Gloria.WarehouseApp, 'TransportLabel:refresh', this.render);
            Backgrid.CellEditor.prototype.initialize.call(this, options);
        },
        
        events: {
        	'change': 'onDropdownChange',
        	'select2-close': 'onDropDownClose'
        },
        
        onDropdownChange: function(e) {
			//Warehouse Receive(DNLGrid) & QualityInspection(Mandatory/BLocked grid) use this TransportLabelSelectCell
			//deliveryNoteLineVersion attribute is in MaterialLineQIModel and not in DeliveryNoteLineModel
			//The below update on DeliveryNoteLineModel, needs to be triggered only in DeliveryNoteLineGrid but not in QIInspection Grid.			
			this.model.set(this.column.attributes.name, e.val, {silent : true});
			
			if(!this.model.attributes.deliveryNoteLineVersion) {
				//Gloria.WarehouseApp.trigger('TransportLabel:change', this.model);
				this.model.trigger("backgrid:edited", this.model, this.column, new Backgrid.Command({
                    keyCode: 13
                }));
			}
       },
       
       onDropDownClose: function(e) {
           var model = this.model;
           var column = this.column;
                       
           var previousValue = model.get(this.column.get("name"));
           var val = this.$el.select2('val');
           val = val || val.length > 0 ? Number(val) : 0;
           var isChanged = (previousValue !== val);
           if(!isChanged) {
               e.stopPropagation();
               model.trigger("backgrid:edited", model, column, new Backgrid.Command({
                   keyCode: 27
               }));
           }
       },

        render : function() {
			var select2Data = [];
			
			var obj = {};
        	obj.id = '0'; // Coming as 0 from server
        	obj.text = i18n.t('Gloria.i18n.selectBoxDefaultValue');
        	select2Data.push(obj);
        	
        	var jsonData = this.collection ? this.collection.toJSON() : [];
        	
        	var unsortedItems = [];
			_.each(jsonData, function(item) {
				var obj = {};
				obj.id = item.id;
				obj.text = item.code;
				if (item)
					unsortedItems.push(obj);
			});
			// Sort the list by ascending order
			var sortedItems = unsortedItems.sort(function(obj1, obj2) {
				return obj1.text - obj2.text;
			});
			select2Data.push.apply(select2Data, sortedItems);
        	
        	var selectOptions = {
				data : select2Data,
				width: 'element'
			};
        	
			this.$el.select2(selectOptions);
			
			this.$el.select2('val', this.model.get(this.column.attributes.name));
			
            return this;
        },
        
        postRender: function (model, column) {
            if (column == null || column.get("name") == this.column.get("name")) {               
            	this.$el.select2('open');
            }
            return this;
        },
        
        remove: function() {
            this.$el.select2('destroy');
            return Backgrid.CellEditor.prototype.remove.apply(this, arguments);
        }
    });

    return TransportLabelSelectCellEditor;
});
