define(['app',
        'utils/backbone/GloriaCollection',
        'utils/UserHelper'
], function(Gloria, Collection, UserHelper) {
    
    var prepareTeamMembers = function(teamCollection, teamType, userCategory, options) {
        if(!teamType) return;
        var deferred = Backbone.$.Deferred();
        var promises = [];
        var transformedJSON = [];
        
        teamCollection.each(function(team) {            
            /* create and push 
             * teamObj = {"teamName": [
             *                          {member1...}, 
             *                          {member2...},
             *                          ...]}
             * here to keep the order of the teams.            
            */
            var teamObj = {};
            teamObj[team.get('name')] = null;
            // transformedJSON contains an array of teamObjs
            transformedJSON.push(teamObj);
            
            var procureTeamMember = new Collection();  
            procureTeamMember.comparator = 'id';
            procureTeamMember.url = '/user/v1/teams/' + team.get('name') + '/users';
            promises.push(procureTeamMember.fetch(_.extend({
                cache : false,
                //async: false,
                data: {
                    type: teamType,
                    userCategory : userCategory
                },
                success: function(collection) {
                    teamObj = _.find(transformedJSON, function(obj){ return _(obj).keys()[0] == team.get('name'); });
                    if(teamObj) {
                        teamObj[team.get('name')] = collection.toJSON();
                    }
                }
            }, options || {})));
        });
        
        Backbone.$.when.apply($, promises).done(function() {                                     
            deferred.resolve(transformedJSON);
        }); 
        
        return deferred.promise();
    };
    
    var setDefaultUser = function(transformedJSON) {
		var currentUser = UserHelper.getInstance().getUserId();
		var defaultUserIdKey = null, defaultUserTeamKey = null, hasRolePROCURE_INTERNAL = false;
		var userRoles = UserHelper.getInstance().getUserRoles();
	    _.each(userRoles, function(ur) {
			if(ur.roleName == 'PROCURE-INTERNAL') {
				hasRolePROCURE_INTERNAL = true;
			}
		});
	    if(hasRolePROCURE_INTERNAL) {
	    	defaultUserIdKey = 'Gloria.User.DefaultIP.' + currentUser;
			defaultUserTeamKey = 'Gloria.User.DefaultIPTeam.' + currentUser;
	    } else {
	    	defaultUserIdKey = 'Gloria.User.DefaultMC.' + currentUser;
			defaultUserTeamKey = 'Gloria.User.DefaultMCTeam.' + currentUser;
	    }
		var defaultUserId = window.localStorage.getItem(defaultUserIdKey);
		var defaultUserTeam = window.localStorage.getItem(defaultUserTeamKey);
		if(transformedJSON.length == 0) { // Remove if there is NO team/user
			window.localStorage.removeItem(defaultUserIdKey);
			window.localStorage.removeItem(defaultUserTeamKey);
		} else if(!defaultUserId || !defaultUserTeam || !findTeamUser(transformedJSON, defaultUserTeam, defaultUserId)) {
			window.localStorage.setItem(defaultUserIdKey, currentUser);
			window.localStorage.setItem(defaultUserTeamKey, _.keys(transformedJSON[0])[0]);
		}
	};
	
	var findTeamUser = function(transformedJSON, team, userId) {
		return _.any(transformedJSON, function(value, key) {
			if(value[team]) {
				return _.any(value[team], function(value, key) {
					return value.id == userId;
				});
			}
		});
	};
	
	var constructProcureTeamMembersList = function(options) {
	    var teamType = 'MATERIAL_CONTROL';
	    var userCategory = 'MATERIAL_CONTROLLER';
	    var deferred = Backbone.$.Deferred();
	    UserHelper.getInstance().getUserTeams(teamType, options).then(function(procureTeams) {
            return prepareTeamMembers(procureTeams, teamType, userCategory, options);  
        }).then(function(transformedJSON) {
        	setDefaultUser(transformedJSON);
            deferred.resolve(transformedJSON);
            Gloria.ProcurementApp.trigger('Procurement:user:fetched', transformedJSON);
        });
	    return deferred.promise();
    };
    
    var constructInternalProcureTeamList = function(options) {
        var teamType = 'INTERNAL_PROCURE';
        var userCategory = 'INTERNAL_PROCURER';
        var deferred = Backbone.$.Deferred();
        UserHelper.getInstance().getUserTeams(teamType, options).then(function(procureTeams) {  
            return prepareTeamMembers(procureTeams, teamType, userCategory, options);  
        }).then(function(transformedJSON) {
        	setDefaultUser(transformedJSON);
            deferred.resolve(transformedJSON);
            Gloria.ProcurementApp.trigger('Procurement:user:fetched', transformedJSON);            
        });
        return deferred.promise();
    };
    
    var constructProcureTeamMembersListFlat = function() {
        var options = null;
        var userCollection = new Collection();
        userCollection.url = '/user/v1/users/' + UserHelper.getInstance().getUserId() + '/procureteammembers';
        userCollection.fetch({
            cache : false,
            async : false,
            success : function(data) {
                options = JSON.stringify(data.toJSON());
            }
        });
        Gloria.ProcurementApp.trigger('Procurement:user:fetched', JSON.parse(options));
        return JSON.parse(options);
    };
    
    var constructInternalProcureTeamListFlat = function() {
    	var options = null;
        var userCollection = new Collection();
        userCollection.url = '/user/v1/teams?type=INTERNAL_PROCURE';
        userCollection.fetch({
            cache : false,
            async : false,
            success : function(data) {
                options = JSON.stringify(data.toJSON());                   
            }
        });
        Gloria.ProcurementApp.trigger('Procurement:internal:fetched', JSON.parse(options));
        return JSON.parse(options);
    };
    
    var constructInternalProcureUserList = function(teamName) {
        var jsonData = null;
        var userCollection = new Collection();
        userCollection.url = '/user/v1/teams/' + (teamName || UserHelper.getInstance().getUserAttribute('internalProcureTeam')) + '/users';
        userCollection.fetch({
            data: {
                type: 'INTERNAL_PROCURE',
                userCategory: 'INTERNAL_PROCURER'
            },
            cache : false,
            async : false,
            success : function(data) {
            	jsonData = data.toJSON();
            }
        });
        return jsonData;        
    };
    
    return {
        'constructProcureTeamMembersList' : constructProcureTeamMembersList,
        'constructInternalProcureTeamList' : constructInternalProcureTeamList,
        'constructInternalProcureUserList' : constructInternalProcureUserList,
        'constructProcureTeamMembersListFlat': constructProcureTeamMembersListFlat,
        'constructInternalProcureTeamListFlat': constructInternalProcureTeamListFlat
    };
    
});