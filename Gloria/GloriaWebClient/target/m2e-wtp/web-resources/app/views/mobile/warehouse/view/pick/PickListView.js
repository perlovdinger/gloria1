define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'utils/UserHelper',
        'views/mobile/warehouse/view/pick/Pick',
        'hbs!views/mobile/warehouse/view/pick/PickListView'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, UserHelper, Pick, compiledTemplate) {

    Gloria.module('WarehouseApp.Pick.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.PickListView = Marionette.CompositeView.extend({

            initialize : function(options) {
                this.listenTo(Gloria, 'scanner:scanned', this.onMaterialLineScanned);
                this.currentitem = -1;
                this.pickListTotalItems = options.pickListTotalItems;
                this.shipSkippable = options.shipSkippable;
                this.setButtons();
            },

            /**
             * Override
             */
            _initialEvents : function() {
                if (this.collection) {
                    this.listenTo(this.collection, "remove", this.removeItemView);
                    this.listenTo(this.collection, "reset", function(e) {
                        this.render();
                        this.setButtons();
                    });
                }
            },

            childView : Pick,

            childViewContainer : "#pickDetailView",
            
            className : 'fixedMargin',

            /**
             * Override
             */
            getTemplate : function() {
                var that = this;
                var actualPickedItems;
                if (that.collection.length == that.pickListTotalItems) {
                    actualPickedItems = 1;
                } else if (that.collection.length === 1) {
                    actualPickedItems = that.pickListTotalItems;
                } else {
                    actualPickedItems = ((that.pickListTotalItems - that.collection.length) + 1);
                }
                return function(data) {
                    return compiledTemplate({
                        collection : that.collection,
                        model : data,
                        pickListTotalItems : that.pickListTotalItems,
                        actualPickedItems : actualPickedItems
                    });
                };
            },

            /**
             * Override
             */
            getEmptyView : function() {
                return Pick;
            },

            collectionEvents : {
                'remove' : 'onRemove',
                'add' : 'onAdd'
            },

            onRemove : function() {
                this.currentitem -= 1;
                this.render();
                this.setButtons();
            },

            onAdd : function() {
                if (this.currentitem == -1) {
                    this.render();
                    this.setButtons();
                }
            },

            /**
             * Override
             */
            showCollection : function() {
                this.destroyChildren();
                var item = this.getNextItem();
                var ItemView = this.getChildView(item);
                this.addChild(item, ItemView, this.currentitem);
            },

            getNextItem : function() {
                return this.collection.at(this.getNextPosition());
            },

            getNextPosition : function() {
                var position;
                if (this.currentitem == (this.collection.length - 1)) {
                    position = this.currentitem = 0;
                } else {
                    position = this.currentitem += 1;
                }
                return position;
            },

            setButtons : function() {
                if (this.collection.length === 1) {
                    Gloria.trigger('showAppControlButtonView', this.addPrintButtons(this.buttonEventsDone()));
                } else if (this.collection.length > 1) {
                    Gloria.trigger('showAppControlButtonView', this.addPrintButtons(this.buttonEvents()));
                }
            },
            
            addPrintButtons: function(buttons) {
                return _.extend({}, buttons, {
                	printButtons: {
                		type: 'menu',
                		label : 'Gloria.i18n.warehouse.pick.buttons.print',
                        className : "btn-primary leftAlign",
                		buttons: {
                			 printPullLabelList : {
                                 label : 'Gloria.i18n.warehouse.pick.buttons.printPullLabelList',                                 
                                 callback :  _.bind(this.printPullLabelList, this)
                             },
                             printPullLabelPart : {
                                 label : 'Gloria.i18n.warehouse.pick.buttons.printPullLabelPart',                                 
                                 callback :  _.bind(this.printPullLabelPart, this)
                             }
                		}
                	}                   
                });
            },

            buttonEvents : function() {
                return {                    
                    pick : {
                        label : 'Gloria.i18n.warehouse.pick.buttons.pick',
                        className : "btn-primary rightAlign",
                        isHidden : !UserHelper.getInstance().hasPermission('edit', ['MobilePick']),
                        callback : _.bind(this.onPick, this)
                    },
                    pickLater : {
                        label : 'Gloria.i18n.warehouse.pick.buttons.pickLater',
                        className : "btn-primary rightAlign",
                        callback : _.bind(this.onPickLater, this)
                    }
                
                };
            },

            buttonEventsDone : function() {
                var buttons = {};  
                buttons.done = {
                        label : 'Gloria.i18n.warehouse.pick.buttons.done',
                        className : 'btn-primary rightAlign',
                        isHidden : !UserHelper.getInstance().hasPermission('edit', ['MobilePick']),
                        callback : _.bind(this.onPick, this)
                    };
                
                if (this.shipSkippable) {
                    buttons.doneAndMarkAsShipped = {
                        label : 'Gloria.i18n.warehouse.pick.buttons.doneAndMarkAsShipped',
                        className : 'btn-primary rightAlign',
                        isHidden : !UserHelper.getInstance().hasPermission('edit', ['MobilePick']),
                        callback : _.bind(this.doneAndMarkAsShipped, this)
                    };
                }
               
                return buttons;
            },

            printPullLabelList : function(e) {
                e.preventDefault();
                Gloria.WarehouseApp.trigger('Pick:PullLabel:print:List');
            },
            
            printPullLabelPart : function(e) {
                e.preventDefault();
                var model = this.collection.at(this.currentitem);
                Gloria.WarehouseApp.trigger('Pick:PullLabel:print:Part', model);
            },
            
            onPickLater : function(e) {
                e.preventDefault();
                Gloria.WarehouseApp.trigger('pickLater:materialLine');
                this.showCollection();
            },
            
            onPick : function(e) {
                e.preventDefault();
                var model = this.collection.at(this.currentitem);
                if (!model.validationError && this.isValidForm()) {
                    this.pick(this.collection.at(this.currentitem));
                }
            },
            
            doneAndMarkAsShipped : function(e) {
                e.preventDefault();
                var model = this.collection.at(this.currentitem);
                if (!model.validationError) {
                    Gloria.WarehouseApp.trigger('Pick:materialLine:pickAndMarkAsShip', model);
                }
            },

            onMaterialLineScanned : function(options) {
                if (!options || !options.data)
                    return;
                var part;
                if (this.collection && (part = this.collection.findWhere({pPartNumber : options.data}))) {
                    this.pick(part);
                }
            },

            pick : function(part) {
                Gloria.WarehouseApp.trigger('Pick:materialLine:pick', part);
            },
            
            isValidForm : function() {
                var pickedQuantityValue = this.$('#pickedQuantity').val() || '';
                pickedQuantityValue = pickedQuantityValue.replace(' ', '');
                if(pickedQuantityValue.length == 0) {
                    this.$('#pickedQuantity').closest('div.form-group').addClass('has-error');  
                    return false;
                } 
                return true;
            }
        });
    });

    return Gloria.WarehouseApp.Pick.View.PickListView;
});