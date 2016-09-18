define([ 'underscore', 'backbone', 'app', 'utils/backbone/GloriaCollection', 'utils/UserHelper' ], 
		function(_, Backbone, Gloria, Collection, UserHelper) {

	var prepareTeamMembers = function(teamCollection, teamType, userCategory, options) {
		if (!teamType)
			return;
		var deferred = Backbone.$.Deferred();
		var promises = [];
		var transformedJSON = [];
		teamCollection.each(function(team) {
			var teamObj = {};
			teamObj[team.get('name')] = null;
			transformedJSON.push(teamObj);
			var dcTeamMember = new Collection();
			dcTeamMember.comparator = 'id';
			dcTeamMember.url = '/user/v1/teams/' + team.get('name') + '/users';
			promises.push(dcTeamMember.fetch(_.extend({
				cache : false,
				// async: false,
				data : {
					type : teamType,
					userCategory : userCategory
				},
				success : function(collection) {
					teamObj = _.find(transformedJSON, function(obj) {
						return _(obj).keys()[0] == team.get('name');
					});
					if (teamObj) {
						teamObj[team.get('name')] = collection.toJSON();
					}
				}
			}, options || {})));
		});
		Backbone.$.when.apply($, promises).done(function() {
			deferred.resolve(teamCollection, transformedJSON);
		});
		return deferred.promise();
	};
	
	var setDefaultDCUser = function(teamCollection, transformedJSON) {
		var currentUser = UserHelper.getInstance().getUserId();
		var defaultDCIdKey = 'Gloria.User.DefaultDCId.' + currentUser;
		var defaultDCTeamKey = 'Gloria.User.DefaultDCTeam.' + currentUser;
		var defaultDCId = window.localStorage.getItem(defaultDCIdKey);
		var defaultDCTeam = window.localStorage.getItem(defaultDCTeamKey);
		if(!defaultDCId || !defaultDCTeam || !findTeamUser(transformedJSON, defaultDCTeam, defaultDCId)) {
			window.localStorage.setItem(defaultDCIdKey, currentUser);
			window.localStorage.setItem(defaultDCTeamKey, _.keys(transformedJSON[0])[0]);
		}		
	};
	
	var findTeamUser = function(transformedJSON, team, userId) {
		return _.any(transformedJSON, function(value, key) {
			if(userId == 'open' || userId == 'unassigned') return true;
			if(value[team]) {
				return _.any(value[team], function(value, key) {
					return value.id == userId;
				});
			}
		});
	};

	var constructDCTeamMembersList = function(options) {
		var teamType = 'DELIVERY_CONTROL';
		var userCategory = 'DELIVERY_CONTROLLER';
		var deferred = Backbone.$.Deferred();
		UserHelper.getInstance().getUserTeams(teamType, options).then(function(dcTeams) {
			return prepareTeamMembers(dcTeams, teamType, userCategory, options);
		}).then(function(teamCollection, transformedJSON) {
			setDefaultDCUser(teamCollection, transformedJSON);
			deferred.resolve(transformedJSON);
			Gloria.DeliveryControlApp.trigger('DeliveryControl:user:fetched', transformedJSON);
		});
		return deferred.promise();
	};

	return {
		'constructDCTeamMembersList' : constructDCTeamMembersList
	};

});
