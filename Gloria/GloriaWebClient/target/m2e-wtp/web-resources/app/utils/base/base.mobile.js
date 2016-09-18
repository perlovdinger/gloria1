/**
 * Base Libraries required for Gloria
 */
define([], function() {
	
	require.loadCss = function(url) {
		var link = document.createElement("link");
		link.type = "text/css";
		link.rel = "stylesheet";
		link.href = require.toUrl(url);
		link.media = "all";
		var head = document.getElementsByTagName("head")[0];
		head.insertBefore(link, head.getElementsByTagName("link")[0]);
	};	
});
