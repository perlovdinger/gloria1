define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var DispatchNoteModel = Model.extend({
		urlRoot : '/material/v1/dispatchnotes'
	});

	return DispatchNoteModel;
});