define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',
        'backbone.syphon',
        'hbs!views/admin/view/assign-team',
        'hbs!views/admin/view/assign-team-row',
        'views/admin/helper/AdminTeamHelper',
        'utils/backbone/GloriaCollection',
        'utils/backbone/GloriaModel'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, Syphon, compiledTemplate, TeamRowTemplate, AdminTeamHelper, GloriaCollection, GloriaModel) {
    
    Gloria.module('AdminTeamApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.AssignTeamModalView = Marionette.View.extend({
            
            className: 'modal',
            
            id: 'AssignTeamModal',
            
            events: {
            	'change #team' : 'handleTeamChangeEvent',
            	'click #add-button' : 'handleAddButtonClick',
            	'click a[id^="remove_"]' : 'handleRemoveButtonClick',
                'click #save-button' : 'handleSaveButtonClick',
                'click #cancel-button' : 'handleCancelButtonClick'
            },
            
            initialize: function(options) {
            	this.module = options.module;
            	this.model = options.model;
    		},
    		
    		handleTeamChangeEvent: function(e) {
    			this.updateAddButton();
			},
			
			updateAddButton: function() {
				var selectedTeam = $('#team').val();
				if(selectedTeam && !this.addedTeamCollection.findWhere({name : selectedTeam})) {
    				$('#add-button').removeAttr('disabled');
    			} else {
    				$('#add-button').attr('disabled', true);
    			}
			},
    		
    		handleAddButtonClick: function(e) {
    			var selectedTeam = $('#team').val();
    			if(this.addedTeamCollection.findWhere({name : selectedTeam})) {
    				this.showErrors('Duplicate Team!');
    			} else {
    				this.addedTeamCollection.add(this.teamCollection.findWhere({name : selectedTeam}));
    				this.updateAddButton();
    			}
			},
			
			handleAddTeam: function(model) {
				$('#teamList').prepend(TeamRowTemplate({
					data: model.toJSON()
				}));
			},
			
			showErrors : function(errorMessage) {
				this.hideErrors();
				if (errorMessage) {
					$('#appModalMessage').find('.help-inline').text(errorMessage).addClass('has-error');
				};
			},

			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
				$('#appModalMessage').find('.help-inline').text('');
			},

			handleRemoveButtonClick: function(e) {
				var selectedTeam = e.currentTarget.id.split('remove_')[1];
    			var teamModel = this.teamCollection && this.teamCollection.get(selectedTeam);
    			teamModel && this.addedTeamCollection.remove(teamModel);
    			this.updateAddButton();
			},
			
			handleRemoveTeam: function(model) {
				$('#team_' + model.get('id')).remove();
			},
			
    		handleSaveButtonClick: function() {
                this.$el.modal('hide');
                Gloria.AdminTeamApp.trigger('AdminGrid:AssignUserModal:save', 
                	this.addedTeamCollection, this.model.get('id'), this.getTeamType(this.module));
            },
			
            handleCancelButtonClick: function() {
                this.$el.modal('hide');
            },

            getTeamType: function(module) {
            	var type = '';
				switch (module) {
				case 'mc':
					type = 'MATERIAL_CONTROL';
					break;
				case 'dc':
					type = 'DELIVERY_CONTROL';
					break;
				case 'ip':
					type = 'INTERNAL_PROCURE';
					break;
				default:
					break;
				}
				return type;
			},
            
            render : function() {
				var that = this;
				Handlebars.registerHelper('renderTeamDropDown', function(options) {
					var ret = '';
					$.each(options, function(i, item) {
						ret += '<option value="' + item.name + '">' + item.name + '</option>';
					});
					return ret;
				});
				
				var teamList = AdminTeamHelper.getTeamListByTeamType(that.getTeamType(that.module));
				this.teamCollection = new GloriaCollection(teamList);
				
				//var addedTeamList = AdminTeamHelper.getTeamListByTeamType(that.getTeamType(that.module));
				var addedTeamList = AdminTeamHelper.getTeamListByUserId(that.model.get('id'), that.getTeamType(that.module));
				this.addedTeamCollection = new GloriaCollection(addedTeamList);
				this.addedTeamCollection.on('add', this.handleAddTeam, this);
				this.addedTeamCollection.on('remove', this.handleRemoveTeam, this);
				
				this.$el.html(compiledTemplate({
					teamList : teamList,
					addedTeamList : addedTeamList
				}));
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
				});
				return this;
			},
            
            onShow: function() {
                this.$el.modal('show');                
            },
            
            onDestroy: function() {
                this.$el.modal('hide');
                this.$el.off('.modal');                
            }
        });
    });
    
    return Gloria.AdminTeamApp.View.AssignTeamModalView;
});