define(['jquery',
        'backgrid',
        'bootstrap',
        'utils/backgrid/FlagCellEditor',
        'hbs!utils/backgrid/FlagCell'
], function($, Backgrid, Bootstrap, FlagCellEditor, FlagCellTemplate) {
    
    var FLAG_TYPES = {
            DEFAULT: {
                className: ""
            },            
            TODAY: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.TODAY",
                className: "fa-flag color-red"
            },
            TOMORROW: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.TOMORROW",
                className: "fa-flag color-orange"
            },
            THIS_WEEK: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.THIS_WEEK",
                className: "fa-flag color-yellow"
            },
            NEXT_WEEK: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.NEXT_WEEK",
                className: "fa-flag color-black"
            },
            ON_TRACK: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.ON_TRACK",
                className: "fa-check color-blue"
            },
            CRITICAL: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.CRITICAL",
                className: "fa-exclamation-triangle color-red"
            },
            WAITING: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.WAITING",
                className: "fa-clock-o color-yellow"
            },
            ANSWERED: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.ANSWERED",
                className: "fa-phone color-green"
            },
            NOT_ANSWERED: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.NOT_ANSWERED",
                className: "fa-phone color-red"
            },
            CONFIRMED: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.CONFIRMED",
                className: "fa-check color-green"
            },
            IN_TRANSIT: {                
                keyTranslate: "Gloria.i18n.deliverycontrol.orderOverview.flags.IN_TRANSIT",
                className: "fa-truck color-blue"
            }
    };
    
    var TOPROCURE_FLAG_TYPES = {
            DEFAULT: {
                className: ""
            },            
            IN_WORK: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.IN_WORK",
                className: "fa-pause color-orange"
            },
            TO_BE_MODIFIED: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.TO_BE_MODIFIED",
                className: "fa-pencil color-orange"
            },
            SLA: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.SLA",
                className: "fa-sla color-orange",
                noBorder:true
            },
            DIRECT_SEND: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.DIRECT_SEND",
                className: "fa-paper-plane-o color-grey",
                noBorder:true
            },
            TOOLING: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.TOOLING",
                className: "fa-tooling color-grey",
                noBorder:true
            },
            MACHINING: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.MACHINING",
                className: "fa-machining color-grey",
                noBorder:true
            },
            SIPD: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.SIPD",
                className: "fa-sipd color-grey",
                noBorder:true
            },
            PRIORITY: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.PRIORITY",
                className: "fa-flag color-red"
            },
            STOPPED_IN_KOLA: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.STOPPED_IN_KOLA",
                className: "fa-ban color-red"
            },
            TO_BE_REMOVED: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.TO_BE_REMOVED",
                className: "fa-times color-red"
            }
    };
    
    
    var TOPROCURE_IP_FLAG_TYPES = {
            DEFAULT: {
                className: ""
            },            
            PENDING_AGREEMENT: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.PENDING_AGREEMENT",
                className: "fa-hourglass-start color-blue"
            },
            REVIEW_LATER: {                
                keyTranslate: "Gloria.i18n.procurement.overviewModule.flags.REVIEW_LATER",
                className: "fa-phone color-orange"
            }
    };


    var FlagCell = Backgrid.Cell.extend({
        
        'FLAG_TYPES': FLAG_TYPES,
        
        'TOPROCURE_FLAG_TYPES' : TOPROCURE_FLAG_TYPES,
        
        'TOPROCURE_IP_FLAG_TYPES' : TOPROCURE_IP_FLAG_TYPES,
        
        template: FlagCellTemplate,
        
        editor: function(options) {
        	if(options.column.attributes.moduleName == 'TOPROCURE' && options.column.attributes.isInternalProcurer == null)
        		{
        			options.flags = TOPROCURE_FLAG_TYPES;
        		} else if(options.column.attributes.moduleName == 'TOPROCURE' && (options.column.attributes.isInternalProcurer)){
        			options.flags = TOPROCURE_IP_FLAG_TYPES;	
        		} else {
        			options.flags = FLAG_TYPES;
        		}
           
            return new FlagCellEditor(options);
        },
        
        render: function() {
        	var flag;
        	if(this.column.attributes.moduleName == 'TOPROCURE'  && this.column.attributes.isInternalProcurer == null) {
        		flag = (this.model.get('statusFlag') && this.model.get('statusFlag') != '0') ? TOPROCURE_FLAG_TYPES[this.model.get('statusFlag')] : TOPROCURE_FLAG_TYPES['DEFAULT'];
        	} else if(this.column.attributes.moduleName == 'TOPROCURE'  && (this.column.attributes.isInternalProcurer)){
        		flag = (this.model.get('statusFlag') && this.model.get('statusFlag') != '0') ? TOPROCURE_IP_FLAG_TYPES[this.model.get('statusFlag')] : TOPROCURE_IP_FLAG_TYPES['DEFAULT'];
        	} else {
             flag = (this.model.get('statusFlag') && this.model.get('statusFlag') != '0') ? FLAG_TYPES[this.model.get('statusFlag')] : FLAG_TYPES['DEFAULT'];           
        	}
        	
        	
        	if (!flag) {
        	flag = 	FLAG_TYPES['DEFAULT'];
        	}
        	
            this.$el.html(this.template({
                className: flag['className'] || '',
                keyTranslate: flag['keyTranslate'] || '',
                id: this.model.id,
                noBorder: flag['noBorder'] || ''
            }));
            this.delegateEvents();
            return this;
        },
        
        /**
         * Override. Removed "this.stopListening(this.currentEditor)" 
         * which caused loosing of the current view listeners on the model.
         */
        exitEditMode: function() {
            this.$el.removeClass("error");
            try {
        		this.currentEditor && this.currentEditor.remove();
        	}catch(e){}           
            delete this.currentEditor;
            this.$el.removeClass("editor");
            this.render();
        },
        
        remove: function() {
        	try {
        		this.currentEditor && this.currentEditor.remove();
        	}catch(e){}
        	Backgrid.Cell.prototype.remove.apply(this, arguments);	
        }
    });

    return FlagCell;
});