/**
 * Material Controller Team Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var MCTeamSelector = BaseSelector.extend({

		cachePrefix : 'gloria.selectors.materialControllerTeams.',
		
		url : function() {
			return '/report/v1/materialControllerTeams?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return MCTeamSelector;
});