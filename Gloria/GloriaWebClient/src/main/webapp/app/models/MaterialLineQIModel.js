define([ 'underscore',
         'utils/backbone/GloriaCollection',
         'utils/backbone/GloriaModel' 
         ], function(_, Collection, Model) {
	
	var MaterialLineQIModel = Model.extend({
		urlRoot : '/procurement/v1/materiallines/qi',
		
		constructor: function() {
			this.directsends = new Collection();        	        	
            Model.apply(this, arguments);
        },  
        
        removeDirectsends: function() {
        	this.stopListening(this.directsends);
    		this.directsends = null;
    		delete this.directsends;
        },
		
		parse: function(resp, options) {			
			if(resp && resp.approvedQty === 0) {
				resp.approvedQty = null;
			}			
			if(resp && resp.deliveryNoteSubLineDTOs) {
        		this.directsends.reset(resp.deliveryNoteSubLineDTOs);
        	} else {
        		this.removeDirectsends();
        	}            
			
		    return resp;
		},
		
		validate: function(attrs, options) {
			var errors = [];
			var action = (options && options.action);
			if(action && action.toUpperCase() === 'APPROVE') {
				var validateApprove = this.validateApprove(attrs, options);
				validateApprove && errors.push(validateApprove);
			}
			return errors.length ? errors : null;
		},
		
		validateApprove: function(attrs, options) {
			var approvedQty;		

			if(attrs && (approvedQty = attrs.approvedQty)) {
				approvedQty = Number(approvedQty);
				if(isNaN(approvedQty)) {
					return {
						errorCode: 112,
						errorType: 'TypeError',
						errorAttr: 'approvedQty'
					};
				}
				var precision = (attrs.approvedQty).split(".")[1] && (attrs.approvedQty).split(".")[1].length ;		
				
				if(approvedQty%1 !== 0) {
					return {
						errorCode: 114,
						errorType: 'NumberTypeError',
						errorAttr: 'approvedQty'
					};
				}else if(precision) {
					return {
						errorCode: 114,
						errorType: 'NumberTypeError',
						errorAttr: 'approvedQty'
					};
				}
				
				if(approvedQty > this.get('quantity') || approvedQty < 0) {
					return {
						errorCode: 115,
						errorType: 'RangeError',
						errorAttr: 'approvedQty'
					};
				}		
			}
		},
		
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

	return MaterialLineQIModel;
});