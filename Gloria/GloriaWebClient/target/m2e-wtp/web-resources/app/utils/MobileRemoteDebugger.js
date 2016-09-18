require([ 'underscore', 'http://10.222.74.199:9080/socket.io/socket.io.js' ], function(_, socketio) {
               
               var socket = socketio('http://10.222.74.199:9080');
               
               socket.on('connect', function() {
               });
               
               socket.on('disconnect', function() {
               });
               
               _.each(_.allKeys(window.console), function(methodName) {
                              var origMethod = window.console[methodName];
                              window.console[methodName] = function() {
                                             var args = _.toArray(arguments);
                                             var result = origMethod.apply(this, arguments) || args;
                                             socket.emit('console', {output: JSON.stringify(result)});
                              };
               });
               
               socket.on('command', function(data) {
                              eval(data.command);
               });           
});
