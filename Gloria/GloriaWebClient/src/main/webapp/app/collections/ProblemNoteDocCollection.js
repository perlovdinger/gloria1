define([ 
    'utils/backbone/GloriaCollection', 
    'models/ProblemNoteDocModel' 
    ], function(Collection, ProblemNoteDocModel) {

	var ProblemNoteDocCollection = Collection.extend({
		url : '/v1/problemdocs',
		model : ProblemNoteDocModel
	});

	return ProblemNoteDocCollection;
});
