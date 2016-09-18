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

    var TransportLabelSelectCell = Backgrid.Cell.extend({

        initialize : function(options) {  
            this.collection = options.transportLabels;
            this.listenTo(Gloria.WarehouseApp, 'TransportLabel:fetched', function(transportLabels) {
				this.collection = transportLabels;
				this.render();
			});
            this.listenTo(Gloria.WarehouseApp, 'TransportLabel:refresh', this.render);
            Backgrid.Cell.prototype.initialize.call(this, options);
        },
        
        events: _.extend(Backgrid.Cell.prototype.events, {
        	'change': 'onDropdownChange'
        }),
        
        onDropdownChange: function(e) {
			//Warehouse Receive(DNLGrid) & QualityInspection(Mandatory/BLocked grid) use this TransportLabelSelectCell
			//deliveryNoteLineVersion attribute is in MaterialLineQIModel and not in DeliveryNoteLineModel
			//The below update on DeliveryNoteLineModel, needs to be triggered only in DeliveryNoteLineGrid but not in QIInspection Grid.			
			this.model.set(this.column.attributes.name, e.val, {silent : true});
			
			if(!this.model.attributes.deliveryNoteLineVersion) {
				Gloria.WarehouseApp.trigger('TransportLabel:change', this.model);
			}
       },

        render : function() {        				
			this.$el.html('<span></span>');
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
				width: 'off',
				containerCss: {
				    minWidth: '100%',
				    width: '0px',
				    maxWidth: '100%'
				},
				dropdownAutoWidth: true
			};
        	
			this.$('span').select2(selectOptions);
			
			this.$('span').select2('val', this.model.get(this.column.attributes.name));
			
            return this;
        },
        
        remove: function() {
            this.$('span').select2('destroy');
            return Backgrid.Cell.prototype.remove.apply(this, arguments);
        }
    });

    return TransportLabelSelectCell;
});
