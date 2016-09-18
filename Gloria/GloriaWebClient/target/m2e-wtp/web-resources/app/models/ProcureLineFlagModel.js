/*this class is currently not used , but created with the intension on having all flag variables at one point*/

define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var ProcureLineFlagModel = Model.extend({		
		
		getProcureLineNonIPFlags : function() {
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
			 return TOPROCURE_FLAG_TYPES;
		},
		
		getProcureLineIPFlags : function(options) {
			   
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

		    return TOPROCURE_IP_FLAG_TYPES;
		}
	});

	return ProcureLineFlagModel;
});