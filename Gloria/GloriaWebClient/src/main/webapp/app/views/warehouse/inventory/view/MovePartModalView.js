define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'jquery-validation',
        'backbone.syphon',
        'views/warehouse/inventory/helper/Select2SelectorView',
        /*'views/warehouse/inventory/helper/BilnLocationSelectorHelper',*/
        'utils/typeahead/BinLocationTypeaheadView',
		'hbs!views/warehouse/inventory/view/move-part-modal'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, Select2SelectorView, /*BilnLocationSelectorHelper,*/BinLocationTypeaheadView, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MovePartModalView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'movePartModal',

			events : {
				'click #move' : 'handleMoveClick',
				'click #cancel' : 'handleCancelClick'
			},
			
			regions: {
			    binLocationContainer: 'div#binLocationContainer'
			},

			initialize : function(options) {
				this.model = options.model;
                this.listenTo(Gloria.WarehouseApp, 'Inventory:moved', this.handleMoveInventoryResponse);
			},

			handleMoveClick : function(e) {
				e.preventDefault();
				if (this.isValidForm()) {
					var formData = Backbone.Syphon.serialize(this);
					formData.partbalance.binLocationCode = $('#codeNew').select2('data').text;
					Gloria.WarehouseApp.trigger('Inventory:move', formData.partbalance, this.model);
				}
			},
			
			handleMoveInventoryResponse : function(flag) {
			    this.$el.modal('hide');
			    if(flag) {
                    Backbone.history.loadUrl(Backbone.history.fragment);
                } else {
                    // Show error
                }
            },
			
			validator : function() {
				var that = this;

				$.validator.addMethod('greaterThanZero', function(value, element) {
					return Number(value) > 0;
				});
				
				$.validator.addMethod('mQLessThanEqualToBB', function(value, element) {
					return Number(value) <= that.model.get('quantity');
				});
				
				return this.$el.find('#moveForm').validate({
					rules: {
						'partbalance[quantity]': {
							required : true,
							min: true,
							mQLessThanEqualToBB: true,
							digits: true
						},
						'partbalance[binLocationCode]' : {
							required: true
						}
					},
					messages: {						
						'partbalance[quantity]': {
							required: i18n.t('errormessages:errors.GLO_ERR_054'),
							digits: i18n.t('errormessages:errors.GLO_ERR_054'),
							min: i18n.t('errormessages:errors.GLO_ERR_054'),
							mQLessThanEqualToBB: i18n.t('errormessages:errors.GLO_ERR_054')
						},
						'partbalance[binLocationCode]' : {
							required: i18n.t('errormessages:errors.GLO_ERR_051')
						}
					},
					showErrors: function (errorMap, errorList) {
			        	that.showErrors(errorList);
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},
			
			showErrors : function(errorList) {
				Gloria.trigger('showAppMessageView', {
					modal : true,
					type : 'error',
					title : i18n.t('errormessages:general.title'),
					message : errorList
				});
			},
			
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {}
				}));
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
			},
			
			onShow : function() {			    
				this.$el.modal('show');
				this.binLocationContainer.show(new BinLocationTypeaheadView({
				    el: this.$('input#codeNew')
				}));
			},

			constructSelect2Data : function(jsonData){
                var select2Data = [];
                select2Data.push({
                    id : '',
                    text : i18n.t('Gloria.i18n.selectBoxDefaultValue')
                });
                _.each(jsonData, function(item, index) {
                    select2Data.push({
                        id : item.id,
                        text : item.code
                    });
                });
                return select2Data;
            },
            
			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');			
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.MovePartModalView;
});
