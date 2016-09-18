define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'backgrid',
        'utils/backbone/GloriaCollection',
        'select2',
        'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Backgrid, Collection, select2, UserHelper) {

    var ZoneSelectCell = Backgrid.Cell.extend({

        initialize : function(options) {
            
            this.zoneType = options.zoneType;
            this.disabled =  options.disabled;
            this.nextZoneTypeAttribute =options.nextZoneTypeAttribute;
            this.nextZoneCode = options.nextZoneCode;
            Backgrid.Cell.prototype.initialize.call(this, options);
        },

        renderDropdown : function(jsonData) {
            var that = this;            
            this.$el.html('<span></span>');
            var select2Data = [];
            var isZoneCodeMatched = false;
            
            _.each(jsonData, function(item) {
                var obj = {};
                obj.id = item.code;
                obj.text = item.name + ' (' + item.code + ')';
                
                if(that.nextZoneCode == item.code){
					isZoneCodeMatched = true;
				};
				
                if (item)
                    select2Data.push(obj);
            });
            //ddl built
            var selectOptions = {
                data : select2Data,
                width: 'off',
                containerCss: {
                    minWidth: '100%',
                    width: '0px',
                    maxWidth: '100%'
                }
            };
           
            this.$('span').select2(selectOptions).on('change', function(e) {
                that.model.set(that.column.attributes.name, e.val, {silent: true});
                if(that.nextZoneTypeAttribute){
                    that.model.set(that.nextZoneTypeAttribute, that.zoneType, {silent: true});
                }
                Gloria.WarehouseApp.trigger('ZoneCell:change', that.model);
           });
            
            if(select2Data.length > 0) {
                this.$('span').select2('val', select2Data[0].id); 
                that.model.set(that.column.attributes.name, select2Data[0].id, {silent: true});
            }
            if(that.nextZoneTypeAttribute){
                that.model.set(that.nextZoneTypeAttribute, that.zoneType, {silent: true});
            }
            
        	if(isZoneCodeMatched){
    		   this.$('span').select2('val', that.nextZoneCode); 
			}
        	
            if(this.disabled){
                this.$('span').select2('disable', true);
            }
            
        },
        
        render : function() {
            var that = this;            
            var storageKey = 'Zones.' + this.zoneType;
            var collection = new Collection();
            collection.url = '/warehouse/v1/zones?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
            collection.fetch({
                data : {
                    'type' : this.zoneType
                },
                cache : false,
                async : false,
                success : function(data) {                                    
                    that.renderDropdown(data.toJSON());
                }
            });
        
            return this;
        },
        
        remove: function() {
            this.$('span').select2('destroy');
            return Backgrid.Cell.prototype.remove.apply(this, arguments);
        }
    });

    return ZoneSelectCell;
});
