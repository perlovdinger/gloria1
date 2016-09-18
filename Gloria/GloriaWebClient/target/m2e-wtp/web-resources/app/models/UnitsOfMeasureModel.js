define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var UnitsOfMeasureModel = Model.extend({

		urlRoot : '/procurement/v1/unitsofmeasure'

	});

	return UnitsOfMeasureModel;
});