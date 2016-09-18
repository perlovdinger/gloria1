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
], function(Gloria, $, _, Handlebars, Marionette, Bootstrap, i18n, select2, UserHelper, GloriaCollection) {

	var FilterSelectorView = Marionette.ItemView.extend({

		initialize : function(options) {
			this.renderUserFilterList();
		},

		renderUserFilterList : function() {
			var collection = new GloriaCollection();
			collection.url = '/user/v1/users/' + UserHelper.getInstance().getUserId() + '/teams?type=DELIVERY_CONTROL';
			collection.fetch({
				async : false
			});
			this.teams = collection;
			this.prepareData(collection.toJSON());
		},

		renderSelect2 : function(select2Data) {
			var currentUser = UserHelper.getInstance().getUserId();
			var that = this;
			this.$el.select2({
				data : select2Data,
				minimumResultsForSearch : -1,
				width : '100%'
			}).on('change', function(e) {
				var thisTeam = that.teams.get(e.currentTarget.value);
				window.localStorage.setItem('Gloria.User.DefaultTeam.' + currentUser, JSON.stringify(thisTeam.toJSON()));
	        });
		},

		prepareData : function(jsonData) {
			var select2Data = [];
			_.each(jsonData, function(item, index) {
				select2Data.push({
					id : item.id,
					text : item.name
				});
			});
			return this.data = select2Data;
		},

		render : function(jsonData) {
			this.$el.empty();
			return this;
		},

		onShow : function() {
			this.renderSelect2(this.data);
			var currentUser = UserHelper.getInstance().getUserId();
			var storageKey = 'Gloria.User.DefaultTeam.' + currentUser;
			if(!window.localStorage.getItem(storageKey) && this.teams.length > 0) {
				window.localStorage.setItem(storageKey, JSON.stringify(this.teams.at(0).toJSON()));
			}
			var thisTeam = window.localStorage.getItem(storageKey);
			try {
				this.$el.select2('val', (JSON.parse(thisTeam)).id);
			} catch(e){
				this.$el.select2('destroy');
				this.$el.empty();
				this.$el.html(
						"<select class='input-block-level form-control' disabled='disabled'><option selected>"
						+ i18n.t('Gloria.i18n.noTeam') + "</option>") + '</select>';
			};
		},

		onDestroy : function() {
			this.$el.select2('destroy');
		}
	});

	return FilterSelectorView;
});