define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var ProblemNoteDocModel = Model.extend({

	    urlRoot : '/v1/problemdocs'
	});

	return ProblemNoteDocModel;
});