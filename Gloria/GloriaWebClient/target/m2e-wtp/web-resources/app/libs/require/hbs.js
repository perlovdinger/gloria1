define(["handlebars"], function(Handlebars) {
  var buildMap = {};      
  var templateExtension = ".html";
  var preCompiledTemplateExtension = ".hbs";
  var isProduction = false;

  return {
      
      load: function (name, parentRequire, onload, config) {       
          // Get the template extension.
          var ext = (config.hbs && config.hbs.templateExtension) ? config.hbs.templateExtension : templateExtension;
          var isProduction = config.isProduction || isProduction;
          
          if (config.isBuild) {
            // For Build mode by r.js
            // Use node.js file system module to load the template.
              // Sorry, no Rhino support.
              var fs = nodeRequire("fs");
              var fsPath = config.dirBaseUrl + "/" + name + ext;
              buildMap[name] = fs.readFileSync(fsPath).toString();
              onload();
          } else if(isProduction) {
              // For Production mode.
              // It is supposed that the templates are precompiled in production mode, using Handlebars and Grunt.         
              parentRequire([name + preCompiledTemplateExtension], function(compiledTemplate){
                  onload(compiledTemplate);   
              });
          } else {
              // For Development mode
              // In the Development environment it is not required to precompile the templates by Grunt and Handlebars.
              parentRequire(["text!" + name + ext], function(raw) {
                  // return the compiled template on development
                  onload(Handlebars.compile(raw));
              });
          }
        },    

    // http://requirejs.org/docs/plugins.html#apiwrite
    write: function (pluginName, name, write) {        
      var compiled = Handlebars.precompile(buildMap[name]);
      // Write out precompiled version of the template function as AMD
      // definition.
      write(
        "define('" + pluginName + "!" + name + "', ['handlebars'], function(Handlebars){ \n" +
          "return Handlebars.template(" + compiled.toString() + ");\n" +
        "});\n"
      );
    }

  };
});
