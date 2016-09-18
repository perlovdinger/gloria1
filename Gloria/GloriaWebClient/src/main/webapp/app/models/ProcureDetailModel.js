define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var ProcureDetailModel = Model.extend({		
		
		/**
		 * @author a043104
		 * This method is required to save the current data of the model.
		 */
		snapshot : function() {
			var changed, prev, current;
			
			previous = this.previousAttributes();
			current = _.clone(this.attributes);
			changed = _.clone(this.changed);
			
			return (this.snapshotAttr = {
					id: _.uniqueId('snp_'),
					previous: previous,
					current: current,
					changed: changed
			}); 
		},
		
		/**
		 * @author a043104
		 * This method is used to set the data of the model to the snapshot moment.
		 * The reason for using this method instead of reseting the model is we need to 
		 * reset the data inside a model without effecting on hasChanged() state of the model
		 * and without triggering ant event.
		 * Whenever we call model's set method it effects on hasChanged() state of the model.    
		 */
		rollback : function(options) {
			if(this.snapshotAttr && !_.isEmpty(this.snapshotAttr)) {
				this._previousAttributes = this.snapshotAttr.previous;				
				this.attributes = this.snapshotAttr.current;				
				this.changed = this.snapshotAttr.changed;	
				return this;
			} else {
				return;
			}
		}
	});

	return ProcureDetailModel;
});