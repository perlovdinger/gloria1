define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'backbone.syphon',
        'jquery-validation',
        'moment',
        'pikaday',
        'utils/UserHelper',
        'utils/DateHelper',
        'hbs!views/mobile/warehouse/view/receive/receive'
], function(Gloria, _, Handlebars, Backbone, Marionette, Syphon, Validation, moment, Pickaday, UserHelper, DateHelper, compiledTemplate) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ReceiveView = Marionette.View.extend({

			initialize : function(options) {
				this.module = options.module;
				this.orderModel = options.orderModel;
				this.transferReturnModel = options.transferReturnModel;
				this.deliveryNoteModel = options.deliveryNoteModel;
				this.regularTabViewModel = options.regularTabViewModel;				
				this.listenTo(Gloria.WarehouseApp, 'Revice:order:exist', this.orderExist);
				this.listenTo(Gloria.WarehouseApp, 'Revice:order:notexist', this.orderNotExist);
				this.listenTo(Gloria.WarehouseApp, 'receive:TabChanged', this.clearBindingsOnTabchange);
			},

			className : 'fixedMargin',
			
			events : {
				'focusout #orderNo' : 'searchOrderline',
				'focusout #deliveryNoteNo' : 'searchOrderlineTR',
				'touchstart #deliveryNoteTransferReturnLookup' : 'openLookupPopup',
				'touchstart #orderLookup' : 'openLookupPopup'
			},
			
			orderModelBindings : {				
				'#orderNo' : 'orderNo',
				'#supplierId' : 'supplierId',
				'#supplierName' : 'supplierName',
				'#projectId' : 'projectId',
				'#transportationNo' : 'transportationNo',
				'#carrier' : 'carrier',
				'#save' : {
					observe : 'id',
					update : 'orderNotExist'
				}
			},

			transferReturnModelBindings : {
				'#deliveryNoteNo' : 'dispatchNoteNo',
				'#supplierId' : 'parmaID',
				'#supplierName' : 'parmaName',
				'#projectId' : 'projectId',
				'#transportationNo' : 'transportationNo',
				'#carrier' : 'carrier',
				'#save' : {
					observe : 'id',
					update : 'orderNotExist'
				}
			},
			
			regularTabViewModelBindings: {
				'#deliveryNoteNo' : 'deliveryNoteNo',
				'#deliveryNoteDate': {
					observe: 'deliveryNoteDate',
					update: function(el, val) {
						if(!val) return;
						return el.val(val);
					}
				}
			},

			disableSave : function() {
				if (!$('#toReceive').hasClass('disabled')) {
					$('#toReceive').addClass('disabled');
				}
				this.hideErrors();
			},

			enableSave : function() {
				if ($('#toReceive').hasClass('disabled')) {
					$('#toReceive').removeClass('disabled');
				}
				this.hideErrors();
			},

			orderExist : function() {
				this.enableSave();
			},

			orderNotExist : function() {
				this.disableSave();
				if(this.module == 'regular') {
					$('div.form-group.orderNo').addClass('has-error');
				} else {
					$('div.form-group.deliveryNoteNo').addClass('has-error');
				}
			},

			openLookupPopup : function(e) {
				e.preventDefault();
				Gloria.WarehouseApp.trigger('Receive:search:show', this.module);
			},

			clearBindingsOnTabchange : function() {
				this.orderModel.clear();
				this.transferReturnModel.clear();
			},

			bindSelectedModelValues : function() {
				if (this.module == 'regular') {
					this.unstickit();
					this.stickit(this.orderModel, this.orderModelBindings);
					this.stickit(this.regularTabViewModel, this.regularTabViewModelBindings);
				} else {
					this.unstickit();
					this.stickit(this.transferReturnModel, this.transferReturnModelBindings);
				}
			},

			searchOrderline : function(e) {
				if(this.module != 'regular') return;
				var query = this.$('#orderNo').val().trim();
				if (query.length == 0) {
					this.orderModel.clear();
					return;
				}
				Gloria.WarehouseApp.trigger('Receive:fetch:orderline', query);
			},
			
			searchOrderlineTR : function() {
				if(this.module == 'regular') return;
				var query = this.$('#deliveryNoteNo').val().trim();
				if (query.length == 0) {
					this.transferReturnModel.clear();
					return;
				}
				Gloria.WarehouseApp.trigger('Receive:fetch:orderline', query);
			},

			buttonEvents : function() {
				var that = this;
				return {
					createTL : {
						label : 'Gloria.i18n.buttons.createTL',
						className : "btn-primary leftAlign",
						callback : function(e) {
							e.preventDefault();
							Gloria.WarehouseApp.trigger('Receive:TransLabel:show');
						}
					},
					toReceive : {
						label : 'Gloria.i18n.buttons.toReceive',
						className : "btn-primary rightAlign disabled",
						isHidden : !UserHelper.getInstance().hasPermission('edit', ['MobileReceive']),
						callback : function(e) {
							e.preventDefault();
							if (that.isValidForm()) {
								var formData = Backbone.Syphon.serialize(that).deliveryNote;
								if(that.module == 'regular') {
			                    	_.extend(formData, that.orderModel.pick(['supplierId', 'supplierName']));
								} else {
									that.transferReturnModel.set('deliveryNoteNo', that.transferReturnModel.get('dispatchNoteNo'));
			                        formData['supplierId'] = that.transferReturnModel.get('parmaID');
			                        formData['supplierName'] = that.transferReturnModel.get('parmaName');
			                    	_.extend(formData, that.transferReturnModel.pick(['carrier', 'transportationNo', 'deliveryNoteNo']));
								}
								formData.receiveType = that.module.toUpperCase();
								if(formData.receiveType == 'RETURNTRANSFER') {
									formData.receiveType = 'RETURN_TRANSFER';
								}
								Gloria.WarehouseApp.trigger('Receive:order:save', formData);
							}
						}
					}
				};
			},

			validator : function() {
				var that = this;
				return this.$el.find('#receiveForm').validate({
					showErrors : function(errorMap, errorList) {
						that.showErrors(errorList);
					}
				});
			},

			isValidForm : function() {
				return this.validator().form();
			},

			showErrors : function(errors) {
				this.hideErrors();
				_.each(errors, function(error) {
					var controlGroup = this.$('.' + error.element.id);
					controlGroup.addClass('has-error');
				}, this);
			},

			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
			},

			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					isRegular : this.module == 'regular' ? true : false,
					deliveryNoteModel : this.deliveryNoteModel ? this.deliveryNoteModel.toJSON() : {}
				}));
				this.bindSelectedModelValues();
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			},

			onShow : function() {
                var today = DateHelper.getCurrentLocalizedDate();
                this.picker = new Pickaday({
                    field : this.$('.js-date')[0]
                });
                this.picker.setMaxDate(new Date(today)); 
			    
				if(this.module == 'regular') {
					this.searchOrderline();
				} else {
					this.searchOrderlineTR();
				}
			},

			onDestroy : function() {
				this.picker.destroy();
			}
		});
	});

	return Gloria.WarehouseApp.Receive.View.ReceiveView;
});
