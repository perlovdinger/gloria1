/**
 * PhaseName Selector
 */
define([ 'app', 
         'views/reports/components/BaseSelector', 
         'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var PhaseNameSelector = BaseSelector.extend({

		cachePrefix : 'gloria.selectors.PhaseName.',
		
		url : function() {
			return '/report/v1/phases'; 
		}
	});

	return PhaseNameSelector;
});