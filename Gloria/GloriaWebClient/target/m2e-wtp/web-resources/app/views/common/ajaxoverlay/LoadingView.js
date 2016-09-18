define([ 'jquery', 'i18next', 'marionette' ], function($, i18n, Marionette) {

	var LoadingView = Marionette.View.extend({

		tagName : 'h4',

		id : 'loadingLabel',

		render : function() {
			this.el.innerHTML = i18n.t('Gloria.i18n.general.loading');
			return this;
		}
	});

	return LoadingView;
});