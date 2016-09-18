define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars',
        'marionette',
		'bootstrap',
		'utils/DateHelper',
		'jquery-validation',
		'hbs!views/material/overview/view/pull-part-dialog',
		'views/material/helper/AllWarehouseListSelectorHelper',
		'views/material/helper/AllWarehouseSelectorViewHelper'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, DateHelper, Validation,
		compiledTemplate, AllWarehouseListHelper, AllWarehouseSelectorViewHelper) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PullPartDialogView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
			},

			className : 'modal',

			id : 'pull-modal',

			events : {
				'change input[name="deliveryAddressType"]' : 'handleNewDeliveryAddressChange',
				'click #save' : 'save',
				'click #cancel' : 'cancel'
			},

			handleNewDeliveryAddressChange : function(e) {
				e.preventDefault();
				if (e.currentTarget.id == 'newDeliveryAddress') {
					this.$('#transferToWarehouse').attr('checked', false);
					this.$('#warehouseId').hide();
					this.$("input[name='request[deliveryAddressId]']").val('');
					this.$('#deliveryAddressType').val('NEW_DELIVERY_ADDRESS');
					if (e.currentTarget.checked) {
						this.$('#deliveryAddressTextArea').show();
					} else {
						this.$('#deliveryAddressTextArea').hide();
						this.$('#deliveryAddressTextArea').val('');
					}
				} else if (e.currentTarget.id == 'transferToWarehouse') {
					this.$('#newDeliveryAddress').attr('checked', false);
					this.$('#deliveryAddressTextArea').hide();
					this.$('#deliveryAddressTextArea').val('');
					this.$('#deliveryAddressType').val('WH_SITE');
					if (e.currentTarget.checked) {
						this.$('#warehouseId').show();
						var jsonStringData = AllWarehouseListHelper.constructAllWarehouseList();
						// Remove the Ship To warehouse/site
						jsonStringData = JSON.parse(jsonStringData);
						var whSiteId = this.model.get('whSiteId');
						jsonStringData = jsonStringData.filter(function(el) {
							return el.siteId != whSiteId;
						});
						new AllWarehouseSelectorViewHelper({
							element : '#tranferToWarehouseDropDown',
							select2Data : this.constructSelect2Data(jsonStringData),
							select2Options : {
								minimumResultsForSearch : 1
							}
						});
					} else {
						this.$('#warehouseId').hide();
						this.$("input[name='request[deliveryAddressId]']").val('');
					}
				}
				if (!e.currentTarget.checked) {
					this.$('#deliveryAddressType').val('');
				}
			},

			constructSelect2Data : function(jsonData) {
				var select2Data = [];
				_.each(jsonData, function(item,	index) {
					select2Data.push({
						id : item.siteId,
						text : item.siteId + ' - ' + item.siteName
					});
				});
				return select2Data;
			},

			save : function(e) {
				if (this.isValidForm()) {
					var formData = Backbone.Syphon.serialize(this);
					if(formData.request.deliveryAddressType == 'WH_SITE') {
						try {
							formData.request.deliveryAddressName = $('#tranferToWarehouseDropDown').select2('data').text.split(' - ')[1].trim();
						} catch(e){};
					} else if(!formData.request.deliveryAddressType) {
						formData.request.deliveryAddressId = $('span#outBoundLocationId').text().split(' - ')[0];
						formData.request.deliveryAddressName = $('span#outBoundLocationId').text().split(' - ')[1];
					}
					//formData.request.requestDeliveryDate = DateHelper.formatDatewithDefault(formData.request.requestDeliveryDate);
					Gloria.MaterialApp.trigger('MaterialRequestList:pullPart', this.model, formData.request);
				}
			},

			cancel : function() {
				this.$el.modal('hide');
			},

			validator : function() {
				var that = this;
				return this.$el.find('form').validate({
					ignore: [],
					rules : {
						'request[quantity]' : {
							required : true,
							digits : true
						},
						'request[requestDeliveryDate]' : {
							required : true
						},
						'request[deliveryAddressType]': {
							required: {
								depends: function(element) {
									return $('span#outBoundLocationId').text() == ' - ';
								}
							}
						},
						'request[deliveryAddressId]': {
							required: {
								depends: function(element) {
									return $('#transferToWarehouse').is(':checked');
								}
							}
						},
						'request[deliveryAddressName]': {
							required: {
								depends: function(element) {
									return $('#newDeliveryAddress').is(':checked');
								}
							}
						}
					},
					messages : {
						'request[quantity]' : {
							required : i18n.t('errormessages:errors.GLO_ERR_072'),
							digits : i18n.t('errormessages:errors.GLO_ERR_072')
						},
						'request[requestDeliveryDate]' : {
							required : i18n.t('errormessages:errors.GLO_ERR_073')
						},
						'request[deliveryAddressType]': {
							required: i18n.t('errormessages:errors.GLO_ERR_068')
						},
						'request[deliveryAddressId]': {
							required: i18n.t('errormessages:errors.GLO_ERR_034')
						},
						'request[deliveryAddressName]': {
							required: i18n.t('errormessages:errors.GLO_ERR_033')
						}
					},
					showErrors : function(errorMap, errorList) {
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
					message : errorList
				});
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					data : that.model ? that.model.toJSON() : {}
				}));
				this.$('.js-date').val(DateHelper.formatDatewithoutUTC(new Date()));
				var today = DateHelper.getCurrentLocalizedDate();
                this.$('.requestDeliveryDateDiv').datepicker('setStartDate',today);

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
			},

			onDestroy : function() {
				this.$('.date').datepicker('remove');
				this.$el.modal('hide');
				this.$el.off('.modal');
			}
		});
	});

	return Gloria.MaterialApp.View.PullPartDialogView;
});