var base = 'src/main/webapp/';
var source = base + 'app/';
var target = 'target/dist/';
var dest = target + '/app';

module.exports = function(grunt) {

    grunt.initConfig({
        pkg : grunt.file.readJSON('package.json'),

        baseDir : base,
        sourceDir : source,
        targetDir : target,
        destDir : dest,

        bower : {
            install : {
                options : {
                    targetDir : '<%= sourceDir %>/libs',
                    layout : 'byComponent',
                    install : true,
                    copy : true,
                    cleanTargetDir : true,
                    bowerOptions: {
                    	forceLatest: true,    // Force latest version on conflict
                        production: true
                    }
                }
            }
        },

        copy : {
            localLibs : {
                files :
                    [{
                        expand : true,
                        cwd: 'libtemplate/',
                        src : '**',
                        dest : '<%= sourceDir %>/libs'
                    }]
            },
            main : {
                files :
                    [
                    // includes files within path
                    {
                        expand : true,
                        src : ['<%= baseDir %>/*', '!**/sso.html'],
                        dest : '<%= targetDir %>',
                        filter : 'isFile',
                        flatten : true
                    }]
            },
            libs: {
            	files :
                    [{
                        expand : true,
                        cwd: '<%= sourceDir %>/libs',
                        src : '**',
                        dest : '<%= destDir %>/libs'
                    }]
            }
        },

        handlebars : {
            compile : {
                options : {
                    amd : true,
                    namespace : false
                },
                files :
                    [{
                        cwd : '<%= sourceDir %>',
                        src : ['**/*.html'],
                        dest : dest,
                        expand : true,
                        ext : '.hbs.js',
                    }]
            }
        },

        requirejs : {
            compile : {
                options : require('./buildconfig')({
                    baseDir : base,
                    sourceDir : source,
                    destDir : dest
                })
            }
        },

        uglify : {
            compile : {
                options : {
                    report : 'min',
                    preserveComments : false,
                    ASCIIOnly : true
                },
                files :
                    [{
                        expand : true,
                        cwd : '<%= destDir %>',
                        src : ['libs/**/*.js', '**/*.hbs.js'],
                        dest : '<%= destDir %>'
                    }]
            }
        },

        // minify json resources
        minjson : {
            build : {
                files :
                    [{
                        cwd : '<%= destDir %>',
                        src : ['**/*.json'],
                        dest : '<%= destDir %>',
                        expand : true
                    }]
            }
        },

        clean : {
            build : {
                files :
                    [{
                        cwd : '<%= destDir %>',
                        src : ['**/*.html', '**/build.txt'],
                        expand : true
                    }]
            }
        },

        manifest : {
            generate : {
                options : {
                    basePath : '<%= targetDir %>',
                    // There is no possiblity to put custom comment on the file,
                    // So in this case I use the below cache param to add a line of comment
                    // We can add files names which need to be cached expilictly
                    cache : ['# ${buildNumber}', 'app/views/common/exception/ExceptionHandlerView.js'],
                    network : ['*'],
                    // fallback: ["/ /offline.html"],
                    exclude : ['gloria.appcache', 'index.html', 'app/config.js'],
                    preferOnline : false,
                    verbose : true,
                    timestamp : true
                },
                files :
                    [{
                        cwd : target,
                        src : ['**', '!**/*.js.map', '!**/*.js.src.js'],
                        filter : 'isFile',
                        dest : '<%= targetDir %>gloria.appcache'
                    }]
            }
        }
    });

    require('load-grunt-tasks')(grunt);
    require('./manifest')(grunt);

    grunt.registerTask('prepareLibs',
        ['bower:install', 'copy:localLibs']);
    grunt.registerTask('compile',
        ['copy:main', 'copy:libs', 'handlebars', 'uglify', 'minjson', 'clean']);
    grunt.registerTask('build',
        ['requirejs', 'compile', 'manifest']);
};