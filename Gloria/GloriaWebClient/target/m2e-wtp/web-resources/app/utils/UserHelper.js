/**
 * Singleton helper to get user settings
 */
define(['app',
        'jquery',
        'underscore',
        'backbone',
        'i18next',
        'utils/backbone/GloriaModel',
        'utils/backbone/GloriaCollection',
        'text!resources/configs/user-access-config.json',
        'moment'
], function (Gloria, $, _, Backbone, i18n, Model, Collection, UserAccessConfig, moment) {
    
    var instance = undefined;   
    var userSites= {};
    var userWarehouses = undefined; 
    var UserCollection = Collection.extend({
        model : Model.extend({}),
        url : '/user/v1/users/current'
    });
    
    var UserHelper = function() {
        this.userAccessConfigObj = JSON.parse(UserAccessConfig);        
    };
    
    UserHelper.prototype = {
        DEFAULT_WAREHOUSE_PREFIX: 'Gloria.User.DefaultWarehouse',
        DEFAULT_USER_TEAM_KEY: 'Gloria.User.DefaultUserTeam',
        getUser: function() {
            var that = this;
            var user = sessionStorage.getItem('Gloria.User.User');              
            if (user) {
                user = JSON.parse(user);
            } else {
                var userCollection = new UserCollection();
                userCollection.fetch({
                    async : false,
                    success : function(data) {
                        user = data.shift().toJSON();
                        sessionStorage.setItem('Gloria.User.User', JSON.stringify(user));
                        that.changeUserSettings(user);
                    }
                });
                
            }

            return user;
        },
        getUserId: function() {
            if(!sessionStorage.getItem('Gloria.User.User')) this.getUser();
            var user = sessionStorage.getItem('Gloria.User.User');
            try {
                return JSON.parse(user)['id'];
            } catch (e) {
                return null;
            };
        },
        getUserAttribute: function(attr) {
            if(!sessionStorage.getItem('Gloria.User.User')) this.getUser();
            var user = sessionStorage.getItem('Gloria.User.User');
            try {
                return JSON.parse(user)[attr];
            } catch (e) {
                return null;
            };
        },
        hasUserRole: function(role) {
            var hasRole = false;
            var userRoles = this.getUserRoles();
            if(userRoles) {
                _.each(userRoles, function(thisRole) {
                    if(thisRole.roleName === role) {
                        hasRole = true;
                        return false;
                    }
                });
            }
            return hasRole;
        },
        isUserLoggedIn: function() {
            return sessionStorage && sessionStorage.getItem('Gloria.User.User');
        },
        resetUser: function() {
            sessionStorage.removeItem('Gloria.User.User');
            sessionStorage.removeItem('Gloria.User.UserRoles');
        },
        getDCList: function() {
            var dcList = null;
            var collection = new UserCollection();
            collection.url = '/user/v1/users/' + this.getUserId() + '/deliveryfollowupteammembers';
            collection.fetch({
                async : false,
                success : function(data) {  
                    dcList = data.toJSON();
                }
            });
            return dcList;
        },
        getDCIdName: function(dcId) {
            var dcList = this.getDCList();
            var formattedDC = '';
            _.each(dcList, function(item, index) {
                if (item.id === dcId) {
                    formattedDC =  item.id + ' - ' + item.firstName + ' ' + item.lastName;
                }
            });
            return formattedDC;
        },
        /**
         * Update user settings. 
         * For now, only changing language.     
         */
        changeUserSettings: function(userSettings) {
            userSettings || (userSettings = {});
            var languageCode = userSettings['languageCode'] || (Gloria && Gloria.Attributes.defaultLang);
            var countryCode = userSettings['countryCode'] ? ('-' + userSettings['countryCode']) : '';               
            var newLocale = languageCode + countryCode;             
            
            if (languageCode) {
                i18n.changeLanguage(newLocale, function(err, t) {
                    Gloria.trigger('initialized:locale');
                    $('title').html(i18n.t('Gloria.i18n.gloria'));
                    moment.locale(languageCode);
                });             
            }
            
            window.sessionStorage.setItem('Gloria.Attributes.locale', newLocale);
            window.sessionStorage.setItem('Gloria.Attributes.language', languageCode);
            
            Gloria.trigger('initialized:locale');
            // Update the key
            this.DEFAULT_WAREHOUSE_KEY = this.DEFAULT_WAREHOUSE_PREFIX + '.' + this.getUserId();
        },
        /**
         * Read user sites. Always returns synchronously.
         * @returns JSON representation of current user sites.
         */
        getUserSites: function() {
            var user = this.getUser();                
            var userSiteCollection = new Collection();                
            userSiteCollection.fetch({
                url: '/user/v1/users/' + user['id'] + '/sites',
                async : false,
                success : function(sites) {                        
                    userSites = sites.toJSON();                       
                }
            });
            return userSites;                           
        },
        /**
         * Read user roles. Always returns synchronously.
         * @returns JSON representation of current user roles.
         */
        getUserRoles: function() {          
            var userRoles = sessionStorage.getItem('Gloria.User.UserRoles');
            
            if (userRoles) {
                userRoles = JSON.parse(userRoles);
            } else {                
                var user = this.getUser();
                
                if (user) {
                    var userRoleCollection = new Collection();
                    
                    userRoleCollection.fetch({
                        url: '/user/v1/users/' + user['id'] + '/roles',
                        async : false,
                        success : function(roles) { 
                            userRoles = roles.toJSON();
                            sessionStorage.setItem('Gloria.User.UserRoles', JSON.stringify(userRoles));
                        }
                    });
                } else {
                    userRoles = {};
                }
            }
            
            return userRoles;                           
        },
        /**
         * This method checks the User Access Config data to see 
         * if the given roleNames are defined for the specified action(e.g. view,edit,etc) on the specified module.
         * You can find the User Access Config data stored in resources/configs/user-access-config.json on to see its structure.  
         * 
         * 2014-05-15: Removed the roleNames argument!
         */
        checkPermission: function(moduleName, action) {
            var perm = false;
            
            var roles = this.getUserRoles();
            
            var roleNames = _.map(roles, function(role) { 
                return role.id; 
            });     

            if(this.userAccessConfigObj && roleNames) {
                try { 
                    if($.type(roleNames) === "string") {
                        roleNames = [roleNames];
                    }                   
                    //Get the defined roles for each Page
                    var pageRoleNames = this.userAccessConfigObj['Gloria'][moduleName]['actions'][action]['roles']; 
                    
                    //Check if any roleName has permission to see the page
                    $.each(roleNames, function(index, roleName) {
                        roleName = roleName.toUpperCase();
                        if($.inArray(roleName, pageRoleNames) > -1) {
                            perm = true;
                            //Break each loop
                            return false;
                        }
                    });
                                    
                } catch(e) {
                    
                }
            }
            return perm;
        },
        // As of now the module access is based on roles and warehouse
        checkPermissionWarehouseBased: function(moduleName, action) {
            var perm = false;
            try {
                var warehouseBased = this.userAccessConfigObj['Gloria'][moduleName]['actions'][action]['warehouse'];
                var defaultWarehouse = this.getDefaultWarehouse();
                if($.inArray('qiSupported', warehouseBased) > -1) {
                    if(defaultWarehouse) {
                        perm = this.isQISupportedWarehouse(defaultWarehouse);
                    }
                } else {
                    perm = true;
                }
            } catch(e) {}
            return perm;
        },
        /**
         * User Roles fetches at login time, 
         * so it is assumed that user roles are in place(in sessionStorage)      
         */ 
        hasPermission: function(action, moduleNames) {
            var perm = false;
            _.each(moduleNames, function(moduleName) {
                if(this.checkPermission(moduleName, action) && this.checkPermissionWarehouseBased(moduleName, action)) {
                    perm = true;
                    return false;
                }
            }, this);
            return perm;      
        },
        /**
         * Check if the warehouse is QI supported!
         */
        isQISupportedWarehouse: function(warehouseId) {
            var isQISupported = false;
            if(warehouseId) {
                if(!userWarehouses) {
                    var thisWarehouses = new Collection();
                    thisWarehouses.fetch({
                        url: '/warehouse/v1/users/' + this.getUserId() + '/warehouses',
                        async: false,
                        success: function(resp) {
                            if(resp) {
                                userWarehouses = resp;
                                var thisWarehouse = _.first(resp.where({siteId : warehouseId}));
                                if(thisWarehouse) {
                                    isQISupported = thisWarehouse.get('qiSupported');
                                }
                            }
                        }
                    });
                } else {
                    var thisWarehouse = _.first(userWarehouses.where({siteId : warehouseId}));
                    if(thisWarehouse) {
                        isQISupported = thisWarehouse.get('qiSupported');
                    }
                }
            }
            return isQISupported;
        },
        // Get the default warehouse id from local storage
        getDefaultWarehouse: function() {
            var localStorage = window.localStorage;
            if(localStorage) {  
                var whSite = String(this.getUserAttribute('whSite'));
                var defaultWhSite = localStorage.getItem(this.DEFAULT_WAREHOUSE_KEY);
                
                if (this.hasUserRole("IT_SUPPORT")) {
                    return this.setDefaultWarehouse(defaultWhSite);
                }
                
                if(!defaultWhSite || whSite.indexOf(defaultWhSite) == -1){                  
                    defaultWhSite = whSite ? whSite.split(',')[0] : '';
                    return this.setDefaultWarehouse(defaultWhSite);
                } else {
                    return defaultWhSite; // 1st warehouse is default
                }
            }
            return null;
        },
        // Set the default warehouse id as a local storage item
        setDefaultWarehouse: function(warehouseId) {
            var localStorage = window.localStorage;
            if(localStorage && warehouseId) {
                localStorage.setItem(this.DEFAULT_WAREHOUSE_KEY, warehouseId);
                return warehouseId;
             }            
        },
        // Fetch the user warehouses from server
        getUserWarehouses: (function() {            
            var inProgrss = false;
            return function() {
                var that = this;
                if (!inProgrss && this.isUserLoggedIn() && this.hasPermission('view', ['WarehouseSelection'])) {
                    inProgrss = true;
                    require(['collections/WarehouseCollection'], function(WarehouseCollection) {
                        var userSiteCollection = new Collection(that.getUserSites());                       
                        if(userSiteCollection.length == 1 || (userSiteCollection.length > 1 && !that.getDefaultWarehouse())) {
                            that.setDefaultWarehouse(userSiteCollection.at(0).get('siteId'));
                        }
                        Gloria.trigger('user:warehouses:loaded', userSiteCollection);
                        inProgrss = false;
                    });
                }
            };
        })(),
        // Get the default warehouse id from local storage
        getDefaultUserTeam: function(storageKey) {
            var localStorage = window.localStorage;
            if(localStorage) {               
                return localStorage.getItem(storageKey || this.DEFAULT_USER_TEAM_KEY) || ''; // 1st team is default
            }
            return null;
        },
        // Set the default warehouse id as a local storage item
        setDefaultUserTeam: function(storageKey, userTeam) {
            if(!userTeam) {
                userTeam = storageKey;
                storageKey = null;
            }
            var localStorage = window.localStorage;
            if(localStorage && userTeam) {
                localStorage.setItem(storageKey || this.DEFAULT_USER_TEAM_KEY, userTeam);
            }            
        },
        getUserTeams: function(storageKey, teamType, options) {
            var that = this;
            if(!options) {
                options = teamType;
                teamType = storageKey;
                storageKey = null;
            }
            if(!teamType) return;
            var deferred = Backbone.$.Deferred();
            var procureTeams = new Collection();
            procureTeams.comparator = 'id';
            var userID = this.getUserId();          
            procureTeams.url = '/user/v1/users/' + userID + '/teams';
            procureTeams.fetch(_.extend({
                data: {
                    type: teamType,
                    companyCode : options ? options.companyCode : null
                },
                cache : false,
                //async: false,
                success: function(collection) {
                    that.setDefaultUserTeam(storageKey, collection.length && collection.at(0).get('name'));
                    deferred.resolve(collection);
                }
            }, options || {}));
            return deferred.promise();
        },
        logout: function() {
            return Backbone.$.ajax('/user/v1/logout', {
                type : 'GET',                   
                global : false,
                cache: false
            });
        }
    };    
    
    var getInstance = function() {
        if (!instance) {
            instance = new UserHelper();
        }       
        return instance; 
    };  
    
    return {
        getInstance : getInstance
    };  
});