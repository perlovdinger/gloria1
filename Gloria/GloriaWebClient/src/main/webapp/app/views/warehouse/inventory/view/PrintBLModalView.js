define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'select2',
        'backbone.syphon',
		'hbs!views/warehouse/inventory/view/print-bl-modal',
		'utils/typeahead/BinLocationTypeaheadView'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, select2, Syphon, compiledTemplate, BinLocationTypeaheadView) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PrintBLModalView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'printBLModal',

			events : {
				'change #printSelect' : 'handlePrintSelectChange',
				'change #zone' : 'handleZoneChange',
				'change #aisle' : 'handleAisleChange',
				'change #bay' : 'handleBayChange',
				'click #print' : 'handlePrintClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				
			},
			
			handlePrintSelectChange : function(e) {
				e.preventDefault();
				this.$el.find('#print').removeAttr('disabled');
				if(e.currentTarget.value == '2') {
					this.printType = 2;
					this.$('#binLocationCode').select2('val', '');
					this.$('#binLocationCode').select2('enable', false);
					this.$el.find('select').each(function() {
						$('#' + this.id).prop('disabled', false);
					});
				} else {
					this.printType = 1;
					this.$('#binLocationCode').select2('enable', true);
					this.$el.find('select').each(function() {
						this.selectedIndex = 0;
						$('#' + this.id).prop('disabled', true);
					});
				};
			},
			
			handleZoneChange : function(e) {
				e.preventDefault();
				this.zoneId = e.currentTarget.value;
				Gloria.WarehouseApp.trigger('Inventory:PrintBL:Zone:change', this.zoneId);
			},
			
			handleAisleChange : function(e) {
				e.preventDefault();
				this.aisleId =  e.currentTarget.value;
				Gloria.WarehouseApp.trigger('Inventory:PrintBL:Aisle:change', this.aisleId);
			},
			
			handleBayChange : function(e) {
				e.preventDefault();
				this.bayId =  e.currentTarget.value;
				Gloria.WarehouseApp.trigger('Inventory:PrintBL:Bay:change', this.bayId);
			},
			
			handlePrintClick : function(e) {
				var queryFinal = null;
				var formData = Backbone.Syphon.serialize(this);
                query = formData.query;
                if(this.printType == 1) {
                	queryFinal = {
                		binlocation : $('#binLocationCode').select2('data').text
                	};
                } else {
                	queryFinal = {
     				   zone : query.zone ? this.$el.find('#zone option:selected').text() : '',
     				   aisle : query.aisle ? this.$el.find('#aisle option:selected').text() : '',
     				   bay : query.bay ? this.$el.find('#bay option:selected').text() : '',
     				   level : query.level ? this.$el.find('#level option:selected').text() : ''
     				};
                }                
				Gloria.WarehouseApp.trigger('Inventory:BL:print', queryFinal);
				this.$el.modal('hide');
			},
			
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate());
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
			},
			
			showBinLocation : function() {
            	var regionName = 'binLocationContainer';
            	if(!this[regionName]) {
        			this.addRegion(regionName, '#' + regionName);
        			var blCombobox = new BinLocationTypeaheadView({
        				el: this.$('#binLocationCode')
        			});
        			this[regionName].show(blCombobox);
            	}
            },

			onShow : function() {
				this.$el.modal('show');
				this.showBinLocation();
				this.$('#binLocationCode').select2('enable', false);
				this.$el.find('select').each(function() {
					$('#' + this.id).prop('disabled', true);
				});
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.PrintBLModalView;
});
