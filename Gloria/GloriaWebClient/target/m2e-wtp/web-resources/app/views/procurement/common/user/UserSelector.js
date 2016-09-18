define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
		'bootstrap',
		'i18next',
		'utils/backbone/GloriaCollection',
		'utils/UserHelper'
], function(Gloria, $, _, Handlebars, Marionette, Bootstrap, i18n, Collection, UserHelper) {

	var UserSelector = Marionette.ItemView.extend({
	
        tagName : 'select',
        
        className : 'input-block-level form-control',

		initialize : function(options) {
			this.usersTeams = options.user;
			this.selected = options && options.selected;			
		},
		
		render : function() {
		    if(this.usersTeams && this.usersTeams.length) { 
		        var ret = '';
		        var user = UserHelper.getInstance().getUser();
		        _.each(this.usersTeams, function(elem, index) {                
		            var key = _.keys(elem);
		            var value = elem[key];
		            if(value && value.length >= 1) {
		                ret += "<optgroup label='" + key + "'>";
		                ret += "<option value='" + user.id + ";" + key + "'>" + i18n.t('Gloria.i18n.me') + " (" + key + ")</option>";
		                _.each(value, function(item, index) {                               
		                    if(user.id !== item.id) { 
		                        ret += "<option value='" + item.id + ";" + key + "'>" + item.id + " - " + item.firstName + ' ' + item.lastName + " (" + key + ")</option>";
		                    }
		                });
		                ret += "</optgroup>";
		            }
		        }, this);
                            
		        var dropDown = Backbone.$(ret);
		        if(this.selected) {
		            dropDown.find('option[value="'+ this.selected +'"]').prop('selected', true);
		        }
		        this.$el.html(dropDown);
		    } else {
		        this.$el.html("<option selected>" + i18n.t('Gloria.i18n.noTeam') + "</option>").attr('disabled', 'disabled'); 
		    }
			return this;
		}
	});

	return UserSelector;
});
