define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
		'collections/DangerousGoodsCollection'
], function(Backbone, Marionette, Handlebars, i18n, DangerousGoodsCollection) {

	var viewAttrs = ['id', 'className', 'selected', 'idPrefix', 'namePrefix', 'modelName', 'multiple', 'isReadOnly', 'isDisabled'];
	
	var DangerousGoodsSelectorView = Marionette.ItemView.extend({

		tagName : 'select',
		className : 'input-block.name form-control',

		initialize : function(options) {
			_.extend(this, _.pick(options, viewAttrs));			
            this.isReadOnly = options.isReadOnly;
            if (this.isReadOnly) {
                // It should be a span instead of a select...
                this.$el.unwrap();
                this.setElement($('<span></span>'));
                this.$el.addClass('controls');
            } else {
                this.$el.attr('name', this.modelName + '[' + (this.id ? (this.id + '][') : '') + this.namePrefix + ']');                
            }
            this.$el.attr('id', this.idPrefix + (this.id ? this.id : ''));
			this.constructDangerousGoodsList();
		},

		constructDangerousGoodsList : function() {
			var that = this;
			var dangerousGoodsOptions = sessionStorage.getItem('DangerousGoodsListInfo');
			if (dangerousGoodsOptions) {
				that.render(JSON.parse(dangerousGoodsOptions));
			} else {
				var dangerousGoodsCollection = new DangerousGoodsCollection();
				dangerousGoodsCollection.fetch({
					async : false,
					success : function(data) {
						sessionStorage.setItem('DangerousGoodsListInfo', JSON.stringify(data.toJSON()));
						that.render(data.toJSON());
					}
				});
			}
		},

		render : function(jsonData) {
			this.$el.empty();	          
	        
	         if (this.isReadOnly) {
                _.each(jsonData, function(item, index) {
                    if (this.selected && this.selected == item.id) {
                        this.$el.append(item.name);
                        return false;
                    }
                }, this);
            } else {
                _.each(jsonData, function(item, index) {
                    var option = $("<option></option>");                                  
                    option.attr("value", item.id).text(item.name);
                    if (this.selected && this.selected == item.id) {
                        option.attr("selected", "selected");
                    } 
                    this.$el.append(option);
                }, this);
    			if(this.isDisabled){
    				this.$el.attr("disabled", true);
    			}
            }
			return this;
		}
	});

	return DangerousGoodsSelectorView;
});