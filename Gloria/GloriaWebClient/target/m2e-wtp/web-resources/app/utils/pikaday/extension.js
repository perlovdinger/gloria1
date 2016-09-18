define(['underscore', 'i18next', 'pikaday'], function(_, i18next, Pikaday){
	
	var preOpen = function() {
		if(window.innerHeight > this.el.offsetHeight) {
			this.el.style.top = ((window.innerHeight - this.el.offsetHeight) / 2) + 'px';
		}
		if(window.innerWidth > this.el.offsetWidth) {
			this.el.style.left = ((window.innerWidth - this.el.offsetWidth) / 2) + 'px';
		}
		
		this.el.style.position = 'fixed';
	};
	
	var setFormat = function(options) {
		options || (options = {});	
		_.defaults(options, {
			format: i18next.t('Gloria.i18n.dateformat'),
            firstDay: parseInt(i18next.t('Gloria.i18n.weekStart')),
            i18n: i18next.t('Gloria.i18n.datepicker', {returnObjects : true })
		});		
	}; 
	
	
	var initialize = function() {
		//Override config to wrap opts.onOpen which is passed to that to 
		//reposition the datepicker to center of mobile screen 
		var origConfig = Pikaday.prototype.config;	
		Pikaday.prototype.config = function(opts) {		
			opts || (opts = {});
			opts.onOpen || (opts.onOpen = function() {});
			opts.onOpen = _.wrap(opts.onOpen, function(onOpen) {
				var args = _.rest(_.toArray(arguments), 1);
				preOpen.apply(this, args);
				onOpen.apply(this, args);
			});
			
			//Set formatting options if not defined 
			setFormat(opts);
			return origConfig.apply(this, arguments);
		};
	};
	
	return {
		initialize: initialize
	};	
		
});