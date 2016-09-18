/**
 * An extended Backgrid Paginator, which handles setting number of lines per page
 */
define(['backgrid-paginator',
        'i18next',
        'hbs!utils/backgrid/gloriaPaginator'
        ], function (BackgridPaginator, i18n, compiledTemplate) {
	
	var GloriaPaginator = Backgrid.Extension.Paginator.extend({
		tagName : 'caption',
		initialize : function(options) {
			this.template = compiledTemplate;
			//GloriaPaginator.__super__.initialize.call(this, options);
		},
        events: {
        	"click a": "changePage",
        	"change select" : "changeItemsPerPage"
        },
        changeItemsPerPage : function(e, val) {
        	if (e.target.value) {
        	    this.collection.state.currentPage = 0;
        	    this.collection.setPageSize(parseInt(e.target.value));        		
        	}
        }
    });
	
	return GloriaPaginator;
});