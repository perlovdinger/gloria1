define(['underscore', 
        'jquery',
        'text!resources/breadcrumbs/warehouse-pages.json',
        'text!resources/breadcrumbs/material-pages.json',
        'text!resources/breadcrumbs/delivery-control-pages.json',
        'text!resources/breadcrumbs/material-request-pages.json',
        'text!resources/breadcrumbs/procurement-pages.json',
        'text!resources/breadcrumbs/testing-utility-pages.json',
        'text!resources/breadcrumbs/admin-pages.json',
        'text!resources/breadcrumbs/report-pages.json'
], function(_, $, warehousePages, materialPages, deliverycontrolPages, materialRequestPages, procurementPages, testingUtilityPages, adminPages, reportPages) {
    // Used to simulate async calls. This is done to provide a
    // consistent interface with stores (like WebSqlStore)
    // that use async data access APIs
    var callLater = function(callback, data) {
        if (callback) {
            setTimeout(function() {
                callback(data);
            });
        }
    };

    var LocalStorageStore = function(successCallback, errorCallback) {
    };

    _.extend(LocalStorageStore.prototype, {
        getBreadCrumbsForPageIds : function(pageIds, callback) {
        	
        	// Warehouse Breadcrumbs
            var warehouseJSON = sessionStorage.getItem('Gloria.warehousepages');
            var warehouseObj = new Object();
            warehouseObj['warehouse'] = JSON && JSON.parse(warehouseJSON) || $.parseJSON(warehouseJSON);
            var breadCrumbsParsedJSON = warehouseObj;
            
            // Material Breadcrumbs
            var materialJSON = sessionStorage.getItem('Gloria.materialpages');
            var materialObj = new Object();
            materialObj['material'] = JSON && JSON.parse(materialJSON) || $.parseJSON(materialJSON);
            $.extend(breadCrumbsParsedJSON, materialObj);
            
            // Delivery Control Breadcrumbs
            var deliveryControlJSON = sessionStorage.getItem('Gloria.deliverycontrolpages');
            var deliveryControlObj = new Object();
            deliveryControlObj['deliverycontrol'] = JSON && JSON.parse(deliveryControlJSON) || $.parseJSON(deliveryControlJSON);
            $.extend(breadCrumbsParsedJSON, deliveryControlObj);
            
            // Material Request Breadcrumbs
            var materialRequestJSON = sessionStorage.getItem('Gloria.materialRequestpages');
            var materialrequestObj = new Object();
            materialrequestObj['materialrequest'] = JSON && JSON.parse(materialRequestJSON) || $.parseJSON(materialRequestJSON);
            $.extend(breadCrumbsParsedJSON, materialrequestObj);
            
            // Procurement Breadcrumbs
            var procurementJSON = sessionStorage.getItem('Gloria.procurementpages');
            var procurementObj = new Object();
            procurementObj['procurement'] = JSON && JSON.parse(procurementJSON) || $.parseJSON(procurementJSON);
            $.extend(breadCrumbsParsedJSON, procurementObj);
            
            // Testing Utility Breadcrumbs
            var testingUtilityJSON = sessionStorage.getItem('Gloria.testingUtilitypages');
            var testingUtilityObj = new Object();
            testingUtilityObj['testingutility'] = JSON && JSON.parse(testingUtilityJSON) || $.parseJSON(testingUtilityJSON);
            $.extend(breadCrumbsParsedJSON, testingUtilityObj);
            
            // Admin Breadcrumbs
            var adminJSON = sessionStorage.getItem('Gloria.adminpages');
            var adminObj = new Object();
            adminObj['admin'] = JSON && JSON.parse(adminJSON) || $.parseJSON(adminJSON);
            $.extend(breadCrumbsParsedJSON, adminObj);
            
            // Report Breadcrumbs
            var reportJSON = sessionStorage.getItem('Gloria.reportpages');
            var reportObj = new Object();
            reportObj['reports'] = JSON && JSON.parse(reportJSON) || $.parseJSON(reportJSON);
            $.extend(breadCrumbsParsedJSON, reportObj);

            var searchedBreadCrumbs = [];
            var subapp = Backbone.history.fragment.split('/')[0];
            _.each(pageIds, function(pageId) {
                if (pageId.length > 0) {
                	var thisBreadcrumb = breadCrumbsParsedJSON[subapp];
                    searchedBreadCrumbs.push(thisBreadcrumb[pageId]);
                } else {
                    searchedBreadCrumbs = null;
                    return;
                }
            });
            callLater(callback, searchedBreadCrumbs);
        }
    });

    var instance = undefined;
    var getInstance = function() {
        if (!instance) {
            instance = new LocalStorageStore();
        }
        
        if(!sessionStorage.getItem('Gloria.warehousepages'))
            sessionStorage.setItem('Gloria.warehousepages', warehousePages);
            
        if(!sessionStorage.getItem('Gloria.materialpages'))
            sessionStorage.setItem('Gloria.materialpages', materialPages);
            
        if(!sessionStorage.getItem('Gloria.deliverycontrolpages'))
            sessionStorage.setItem('Gloria.deliverycontrolpages', deliverycontrolPages);
            
        if(!sessionStorage.getItem('Gloria.materialRequestpages'))
            sessionStorage.setItem('Gloria.materialRequestpages', materialRequestPages);
       
        if(!sessionStorage.getItem('Gloria.procurementpages'))
            sessionStorage.setItem('Gloria.procurementpages', procurementPages);
        
        if(!sessionStorage.getItem('Gloria.testingUtilitypages'))
            sessionStorage.setItem('Gloria.testingUtilitypages', testingUtilityPages);
        
        if(!sessionStorage.getItem('Gloria.adminpages'))
            sessionStorage.setItem('Gloria.adminpages', adminPages);
        
        if(!sessionStorage.getItem('Gloria.reportpages'))
            sessionStorage.setItem('Gloria.reportpages', reportPages);
        
        return instance;
    };

    return {
        getInstance : getInstance
    };

});
