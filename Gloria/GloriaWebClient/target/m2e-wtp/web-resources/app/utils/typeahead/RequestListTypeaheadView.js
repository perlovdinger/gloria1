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

	var RequestListIdSelector = Marionette.View.extend({
	
		initialize : function(options) {
			this.element = options.element;
			this.constructRequestListIds();
		},
		
		constructRequestListIds : function() {
			var that = this;          
            that.render(that.options.matchingRequestListCollection.toJSON());
        },
        
		render : function(jsonData) {
			var that = this;
			$(this.element).empty();
			var select2Data = [];
			select2Data.push({
				id : '',
				text : i18n.t('Gloria.i18n.selectBoxDefaultValue')
			});
			_.each(jsonData, function(item, index) {
				select2Data.push({
					id : item.id,
					text : item.id
				});
			});
			
			var format = function (state) {
				return state.text;
			};

			$(this.element).select2({
				data : select2Data,
				minimumResultsForSearch: -1,
				formatResult: format,
				width: '100%'
			});
			return this;
		},
		
		remove: function() {
		    $(this.element).select2('destroy');
		    Marionette.View.prototype.remove.apply(this, arguments);
		}
	});

	return RequestListIdSelector;
});
