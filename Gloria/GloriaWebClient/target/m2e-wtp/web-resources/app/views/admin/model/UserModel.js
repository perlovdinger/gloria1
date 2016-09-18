define([ 'utils/backbone/GloriaModel' ], function(Model) {

	var UserModel = Model.extend({
		urlRoot : '/user/v1/users'
	});

	return UserModel;
});