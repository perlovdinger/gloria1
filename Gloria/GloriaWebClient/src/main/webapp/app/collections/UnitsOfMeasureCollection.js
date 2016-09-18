define([ 
    'utils/backbone/GloriaCollection', 
    'models/UnitsOfMeasureModel' 
    ], function(Collection, UnitsOfMeasureModel) {

	var UnitsOfMeasureCollection = Collection.extend({

		model : UnitsOfMeasureModel,

		url : '/procurement/v1/unitsofmeasure'

	});

	return UnitsOfMeasureCollection;
});