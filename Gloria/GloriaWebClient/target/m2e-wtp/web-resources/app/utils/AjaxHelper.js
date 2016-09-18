define(['backbone', 'app'], function(Backbone, Gloria) {
	
	var downloadBinary = function(url, data, options) {
		if(!url || url.length == 0) return;
		options || (options = {});
		url = Gloria.Attributes.apiBaseURL + url;
		var xmlhttp = new XMLHttpRequest();   
		xmlhttp.open("POST", url);
		xmlhttp.responseType = "blob";
		xmlhttp.setRequestHeader("Accept", "*/*");
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.onreadystatechange = options.onReadyStateChange || onReadyStateChange;
		//xmlhttp.onerror = options.onError || onError;
		xmlhttp.send(JSON.stringify(data));			
		return xmlhttp;
	};
	
	var onError = function(e) {
		var responseMsg = null;
		var xhr = e.target;
		if(xhr.responseType === 'blob') {
			var reader = new FileReader();
			reader.readAsText(xhr.response);
			reader.onloadend = function() {
				responseMsg = xhr.responseMsg = reader.result;
				Backbone.$.event.trigger({
		            type: 'ajaxError',
		            source: 'ajax',
		            message: '',
		            target: xhr,
		            time: new Date()
	        }, [xhr, {type: 'POST'}, xhr.status]);
			};	
		}	
	};
	
	var onReadyStateChange = function(e) {
		var xmlhttp = e.target;
		if(!xmlhttp) return;
		if(xmlhttp.readyState == 4 && xmlhttp.status >= 400 && xmlhttp.status < 600) {
			return onError(e);
		}
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {				   
			var matcher = /filename[^;=\\n]*=(.*?\\2|[^;\\n]*)/i;
			var fileName = ((xmlhttp.getResponseHeader('Content-Disposition') || '').match(matcher) || ['', ''])[1].replace('filename=', '');
			var mediaType = xmlhttp.getResponseHeader('Content-Type') || 'text/plain';
			var fileToDownload = new Blob([xmlhttp.response], {type: mediaType});
			if(window.navigator.msSaveOrOpenBlob) {
				window.navigator.msSaveOrOpenBlob(fileToDownload, fileName);
			} else {
				var reportURL = URL.createObjectURL(fileToDownload);
				var a = document.createElement('a');
				a.style = "display: none";  
				a.href = reportURL;
				a.download = fileName;
				document.body.appendChild(a);
				a.click();
				setTimeout(function(){
					document.body.removeChild(a);
					window.URL.revokeObjectURL(reportURL);  
				}, 100); 
			}
			Gloria.trigger('Excel:download:completed');
	    }
	 };
	 
	 return {
		 downloadBinary: downloadBinary
	 };
});