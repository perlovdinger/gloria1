define(['barcodeReader', 'jquery', 'underscore', 'app'], function(BarcodeReader, $, _, Gloria) {
    
    var attrs = ['scannerId', 'eventName', 'autoUpdate', 'autoUpdateClassName'];
    
    var ScannerHelper = function(options) {
        options || (options = {});
        _.extend(this, _.pick(options, attrs));        
        if(window) { 
        	this.setup();
        } else {
            throw new Error('The context does not support scanning function.');
        }        
    };
    
    _.extend(ScannerHelper.prototype, {
    	// On page load create a BarcodeReader object
    	setup: function() {
			this.defaultScanner = new BarcodeReader(null, _.bind(this.onBarcodeReaderComplete, this));
		},
		// After BarcodeReader object is created we can configure our symbologies and add our event listener
		onBarcodeReaderComplete: function(result) {
			if (result.status == 0) {
	            // BarcodeReader object was successfully created
                // Configure the symbologies needed				
				this.defaultScanner.set("Symbology", "Code39", "Enable", "true", this.onSetComplete);
				this.defaultScanner.set("Symbology", "Code128", "EnableCode128", "true", this.onSetComplete);
				//this.defaultScanner.set("Symbology", "QRCode", "Enable", "true", this.onSetComplete);
				this.defaultScanner.set("Symbology", "Aztec", "Enable", "true", this.onSetComplete);
				
                // Add an event handler for the barcodedataready event
				this.defaultScanner.addEventListener("barcodedataready", _.bind(this.onScan, this), false);
			} else {
				this.defaultScanner = null;
				//alert('Failed to create BarcodeReader, ' + result.message);
			}
		},
		// Verify the symbology configuration
		onSetComplete: function (result) {
			if (result.status != 0)
			{
				alert('set Family: ' + result.family + ', Key: ' + result.key + ', Option: ' + result.option +
					', Value: ' + result.value + 'failed. ' + result.message);
			}
		},
        // default scan event name to trigger after scanning is done
        eventName: 'scanner:scanned',
        // default scanner object name to embed to the HTML document
        scannerId: 'scanner_1',
        // update the document's active element if this attribute is true
        autoUpdate: true,
        // only updates input if the input has the below class name
        autoUpdateClassName: 'form-control',
        
        onScan: function(data, type, time) {
            if(data.length == 0) {
                console.error('no data');                
                return;
            }
            
            data = this.normalizeData(data);            
            
            if(this.autoUpdate) {
                this.updateInput(data.trim());
            }
            
            Gloria.trigger(this.eventName, {
                data: data,                
                type: type,
                time: time                
            });
        },
        
        normalizeData: function(data) {        	
        	if(data && data.length) {
        		return data = data.replace(/\u0000/g, '');
        	}
        	return data;
        },
        
        updateInput: function(value) {
            var activeElement;
            if(document && (activeElement = document.activeElement)                    
                    && $(activeElement).hasClass(this.autoUpdateClassName)) {                
                
                $(activeElement).val(value);
            }
        } 
    });
    
    //Making this Module as Singleton 
    var instance;
    var getInstance = function(options) {
        if (!instance) {
            instance = new ScannerHelper(options);
        }
        return instance;
    };

    return {
        getInstance : getInstance
    };    
});