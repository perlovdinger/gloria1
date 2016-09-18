define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
		'bootstrap',
		'i18next',
		'select2',
		'utils/UserHelper',
		'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Handlebars, Marionette, Bootstrap, i18n, select2, UserHelper, Collection) {

	var DeliveryControllerSelector = Marionette.View.extend({
	
		initialize : function(options) {
			this.element = options.element;
			this.name = options.name;
			this.suggestedDC = options.suggestedDC;
			this.defaultDC = options.defaultDC;
			this.loggedInDC = UserHelper.getInstance().getUserId();
			this.teamId = options.teamId;
			this.constructDeliveryControllerList();
		},
		
		constructDeliveryControllerList : function() {
			var that = this;          
            var collection = new Collection();
            collection.url = '/user/v1/teams/' + this.teamId + '/users?type=DELIVERY_CONTROL';
            collection.fetch({
                async : false,
                success : function(data) {
                    that.render(data.toJSON());
                }
            });           
        },
        
        setHiddenDOMForSyphon : function(e) {
        	if ($('input[name="'+this.name+'"]').length) {
        		$('input[name="'+this.name+'"]').val(e.val);
        	}
		},
		
		render : function(jsonData) {
			var that = this;
			$(this.element).empty();
			var select2Data = [];
			var defaultDC = this.defaultDC;
			select2Data.push({
				id : '',
				text : i18n.t('Gloria.i18n.selectBoxDefaultValue')
			});
			_.each(jsonData, function(item, index) {
				select2Data.push({
					id : item.id,
					text : item.firstName + ' ' + item.lastName
				});
			});

			var format = function (state) {
				if(state.id && state.id == defaultDC) {
					return state.text + '<i class="fa fa-user pull-right"></i>';
				}
				return state.text;
			};
			
			$(this.element).select2({
				data : select2Data,
				formatResult: format,
			    //formatSelection: format,
				minimumResultsForSearch: -1,
				width: '100%'
			}).on('change', function(e) {
	        	that.setHiddenDOMForSyphon(e);
	        });
			
			// Add a hidden input, so that Syphon will pick the value
			// Make this required=true, so that jquery validator will validate when ignore attr is set to null!
			$('<input/>', {
				type : 'hidden',
				name : this.name,
				value : that.suggestedDC
			}).attr('required', 'true').appendTo($(this.element));
			if(that.suggestedDC) {
				$(this.element).select2('val', that.suggestedDC);
			} else {
				$(this.element).select2('val', []);
			}
			return this;
		},
		
		remove: function() {
		    $(this.element).select2('destroy');
		    Marionette.View.prototype.remove.apply(this, arguments);
		}
	});

	return DeliveryControllerSelector;
});
