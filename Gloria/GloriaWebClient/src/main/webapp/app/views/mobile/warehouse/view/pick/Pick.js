define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'jquery-validation',
        'hbs!views/mobile/warehouse/view/pick/Pick'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Validation, compiledTemplate) {

	Gloria.module('WarehouseApp.Pick.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.Pick = Marionette.ItemView.extend({

			initialize : function() {
				Marionette.ItemView.prototype.initialize.apply(this, arguments);
				this.listenTo(this.model, 'invalid change:pickedQuantity', this.showHideError);
			},

			getTemplate : function() {
				var that = this;
				return function(data) {
					return compiledTemplate({
						empty : _.isEmpty(that.model.attributes),
						model : data
					});
				};
			},

			bindings : {
				'#location' : 'binLocationCode',
				'#partNumber' : 'pPartNumber',
				'#version' : 'pPartVersion',
				'#partName' : 'pPartName',
				'#partModification' : 'pPartModification',	
				'#requestListId' : 'requestListID',				
				'#pullQuantity' : {
					observe : 'quantity',
				},
				'#unitOfMeasure' : 'unitOfMeasure',
				'#pickedQuantity' : {
					observe : 'pickedQuantity',
					setOptions : {
						validate : true
					}
				},
				'#binlocationBalance' : 'binlocationBalance',
				'#balanceExceeded' : 'balanceExceeded'
			},

			events : {
				'input #pickedQuantity' : 'updateCheckBox'
			},

			updateCheckBox : function(e) {
				if (this.model.get('quantity') != e.currentTarget.value) {
					this.model.set('balanceExceeded', false);
					$('input#balanceExceeded').attr('checked', false);
					$('input#balanceExceeded').attr('disabled', true);
				} else {
					$('input#balanceExceeded').removeAttr('disabled');
				}
			},

			showHideError : function(model, error) {
				this.$('#pickedQuantityContianer').removeClass('has-error');
				if (error instanceof RangeError) {
					this.$('#pickedQuantityContianer').addClass('has-error');
				}
			},

			render : function() {
				var parentRender = Marionette.ItemView.prototype.render.apply(this, arguments);
				this.stickit();
				return parentRender;
			}
		});
	});

	return Gloria.WarehouseApp.Pick.View.Pick;
});