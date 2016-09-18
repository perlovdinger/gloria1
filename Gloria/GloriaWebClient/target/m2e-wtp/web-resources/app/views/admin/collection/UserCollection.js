define(['utils/backbone/GloriaPageableCollection',
		'views/admin/model/UserModel'
], function(PageableCollection, UserModel) {

	var UserCollection = PageableCollection.extend({
		model : UserModel,
		url : '/user/v1/users',
	});

	return UserCollection;
});