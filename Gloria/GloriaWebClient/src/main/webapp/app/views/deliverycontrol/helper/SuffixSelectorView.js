define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'bootstrap',
		'i18next',
		'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Collection) {

	var SuffixSelectorView = Marionette.View.extend({
	
		initialize : function(options) {
			this.element = options.element;
			this.deliveryFollowUpTeamId = options.deliveryFollowUpTeamId;
			this.suggestedOption = options.suggestedOption;
			this.constructSuffixSelector();
		},

		constructSuffixSelector : function() {
            var that = this;          
            var collection = new Collection();
            collection.url = '/common/v1/deliveryfollowupteams/' + this.deliveryFollowUpTeamId + '/suppliercounterparts';
            collection.fetch({
                async : false,
                success : function(data) {                   
                    that.render(data.toJSON());
                }
            });
        },
		
		render : function(jsonData) {
			$(this.element).empty();
			var option = $('<option></option>');
			option.attr('value', '').text(i18n.t('Gloria.i18n.selectBoxDefaultValue'));
			$(this.element).append(option);
			_.each(jsonData, function(item, index) {
				var option = $("<option></option>");
				option.attr("value", item.ppSuffix).text(item.ppSuffix);
				if (this.suggestedOption && this.suggestedOption == item.ppSuffix) {
					option.attr("selected", "selected");
				}
				$(this.element).append(option);
			}, this);
			return this;
		}
	});

	return SuffixSelectorView;
});
