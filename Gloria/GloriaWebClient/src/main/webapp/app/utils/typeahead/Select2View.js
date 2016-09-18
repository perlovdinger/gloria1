define([ 'jquery', 'underscore', 'backbone', 'bootstrap', 'i18next', 'select2' ],
		function($, _, Backbone, Bootstrap, i18n, Select2) {
			
			var TypeaheadView = Backbone.View.extend({
				
				tagName: 'input',
				
				className: 'form-control',
				
				initialize : function(options) {
					options || (options = {});
					this.select2Options = options.select2Options || this.select2Options || {};
				},				
				// Called when Select2 is created to allow the user to initialize the selection based on the value of the element select2 is attached to.
				// Essentially this is an id->object mapping function
				select2InitSelect: function(element, callback) {				
					var id = element.val();
					if (id != "") {				
						callback({id: id, text: id});				
					}
				},
				// making select2 options
				select2DefaultOptions: function() {					
					return _.extend({
					    placeholder: i18n.t('Gloria.i18n.general.pleaseSelect'),
						width: 'resolve',						
						initSelection: _.bind(this.select2InitSelect, this),											
						// apply css that makes the dropdown taller
						dropdownCssClass: "bigdrop",					
						// we do not want to escape markup since we are displaying html in results
						escapeMarkup: function(m) {
							return m;
						}						
					}, this.select2Options);
				},				
				
				onShow: function() {
					var options = this.select2DefaultOptions();
					if(options.disabled) {
						var that = this;
						var currentText = that.$el.val();
						$.each(options.data.results, function(key, result) {
					        if(result['id'] == that.$el.val()) {
					        	currentText = result['text'] || result['id'];
					        }
						});
						this.$el.parent().html('<span>' + currentText + '</span>');
					} else {
						this.$el.select2(options);
					}
				},

				remove : function() {
					this.$el.select2('destroy');
					Backbone.View.prototype.remove.apply(this, arguments);
				}
			});

	return TypeaheadView;
});