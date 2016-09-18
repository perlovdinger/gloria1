define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next', 
        'hbs!views/testingutility/view/init-data'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	Gloria.module('TestingUtilityApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		var dataTypeOtions = [ {
			'' : 'Please Select'
		}, {
			'CleanDB' : 'Clean Database'
		}, {
			'CostCenter' : 'Cost Center'
		}, {
			'WBSElements' : 'WBS Elements'
		}, {
			'CarryOvers1' : 'Carry Overs1'
		}, {
			'CarryOvers2' : 'Carry Overs2'
		}, {
			'CarryOvers3' : 'Carry Overs3'
		}, {
			'CarryOvers4' : 'Carry Overs4'
		}, {
			'CarryOvers5' : 'Carry Overs5'
		}, {
			'Warehouses' : 'Warehouses'
		}];
		
		View.InitDataView = Marionette.LayoutView.extend({
			

			initialize : function(options) {
				this.listenTo(Gloria.TestingUtilityApp,'DataMigration:initiated', this.onDateInitiated);
			},

			events : {
				'change #dataType' : 'dataTypeChangeHandler',
				'click #initiate' : 'initiateClickHandler',
				'click #cancel' : 'cancelClickHandler'
			},
			
			dataTypeChangeHandler : function(e) {
				this.selectedDataType = e.currentTarget.value;
				if(this.selectedDataType) {
					this.$el.find('#initiate').removeAttr('disabled');
				} else {
					this.$el.find('#initiate').attr('disabled', 'disabled');
				}
			},
			
			initiateClickHandler : function(e) {
				if(this.selectedDataType) {
					Gloria.TestingUtilityApp.trigger('DataMigration:initiate', this.selectedDataType);
				}
			},
			
			cancelClickHandler : function(e) {
				Gloria.trigger('goToPreviousRoute');
			},
			
			onDateInitiated: function(status) {
                Gloria.trigger('showAppMessageView', {
                    type : status,
                    message : new Array({
                        message : status == 'error' ? 'There is an issue in initiating data.' : 'The data has been initiated successfully.'
                    })
                });
            },

			render : function() {
				this.$el.html(compiledTemplate({
					dataTypeOtions: dataTypeOtions
				}));
				return this;
			}
		});
	});

	return Gloria.TestingUtilityApp.View.InitDataView;
});
