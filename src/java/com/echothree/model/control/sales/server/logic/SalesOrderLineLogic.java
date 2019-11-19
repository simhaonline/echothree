// --------------------------------------------------------------------------------
// Copyright 2002-2019 Echo Three, LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// --------------------------------------------------------------------------------

package com.echothree.model.control.sales.server.logic;

import com.echothree.model.control.inventory.common.exception.UnknownDefaultInventoryConditionException;
import com.echothree.model.control.inventory.server.InventoryControl;
import com.echothree.model.control.item.common.ItemPriceTypes;
import com.echothree.model.control.item.common.exception.UnknownDefaultItemUnitOfMeasureTypeException;
import com.echothree.model.control.item.common.workflow.ItemStatusConstants;
import com.echothree.model.control.item.server.ItemControl;
import com.echothree.model.control.offer.common.exception.UnknownOfferItemPriceException;
import com.echothree.model.control.offer.server.OfferControl;
import com.echothree.model.control.offer.server.logic.OfferItemLogic;
import com.echothree.model.control.order.common.OrderConstants;
import com.echothree.model.control.order.server.logic.OrderLineLogic;
import com.echothree.model.control.sales.common.exception.CurrentTimeAfterSalesOrderEndTimeException;
import com.echothree.model.control.sales.common.exception.CurrentTimeBeforeSalesOrderStartTimeException;
import com.echothree.model.control.sales.common.exception.ItemDiscontinuedException;
import com.echothree.model.control.sales.common.exception.QuantityAboveMaximumItemUnitCustomerTypeLimitException;
import com.echothree.model.control.sales.common.exception.QuantityAboveMaximumItemUnitLimitException;
import com.echothree.model.control.sales.common.exception.QuantityBelowMinimumItemUnitCustomerTypeLimitException;
import com.echothree.model.control.sales.common.exception.QuantityBelowMinimumItemUnitLimitException;
import com.echothree.model.control.sales.common.exception.UnitAmountAboveMaximumItemUnitPriceLimitException;
import com.echothree.model.control.sales.common.exception.UnitAmountAboveMaximumUnitPriceException;
import com.echothree.model.control.sales.common.exception.UnitAmountBelowMinimumItemUnitPriceLimitException;
import com.echothree.model.control.sales.common.exception.UnitAmountBelowMinimumUnitPriceException;
import com.echothree.model.control.sales.common.exception.UnitAmountNotMultipleOfUnitPriceIncrementException;
import com.echothree.model.control.sales.common.exception.UnitAmountRequiredException;
import com.echothree.model.control.sales.server.SalesControl;
import com.echothree.model.control.workflow.server.logic.WorkflowStepLogic;
import com.echothree.model.data.associate.server.entity.AssociateReferral;
import com.echothree.model.data.cancellationpolicy.server.entity.CancellationPolicy;
import com.echothree.model.data.contact.server.entity.PartyContactMechanism;
import com.echothree.model.data.inventory.server.entity.InventoryCondition;
import com.echothree.model.data.item.server.entity.Item;
import com.echothree.model.data.offer.server.entity.OfferItem;
import com.echothree.model.data.offer.server.entity.OfferUse;
import com.echothree.model.data.order.server.entity.Order;
import com.echothree.model.data.order.server.entity.OrderLine;
import com.echothree.model.data.order.server.entity.OrderShipmentGroup;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.returnpolicy.server.entity.ReturnPolicy;
import com.echothree.model.data.shipping.server.entity.ShippingMethod;
import com.echothree.model.data.uom.server.entity.UnitOfMeasureType;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.server.message.ExecutionErrorAccumulator;
import com.echothree.util.server.persistence.Session;

public class SalesOrderLineLogic
        extends OrderLineLogic {

    private SalesOrderLineLogic() {
        super();
    }

    private static class LogicHolder {
        static SalesOrderLineLogic instance = new SalesOrderLineLogic();
    }

    public static SalesOrderLineLogic getInstance() {
        return LogicHolder.instance;
    }

    /**
     * 
     * @param eea Optional.
     * @param order Required.
     * @param orderShipmentGroup Optional.
     * @param orderShipmentGroupSequence Optional.
     * @param orderLineSequence Optional.
     * @param parentOrderLine Optional.
     * @param partyContactMechanism Optional.
     * @param shippingMethod Optional.
     * @param item Required.
     * @param inventoryCondition Optional.
     * @param unitOfMeasureType Optional.
     * @param quantity Required.
     * @param unitAmount Optional for Items with a FIXED ItemPriceType, Required for VARIABLE.
     * @param description Optional.
     * @param taxable Optional.
     * @param offerUse Optional.
     * @param associateReferral Optional.
     * @param createdBy Required.
     * @return The newly created OrderLine, otherwise null if there was an error.
     */
    public OrderLine createSalesOrderLine(final Session session, final ExecutionErrorAccumulator eea, final Order order, OrderShipmentGroup orderShipmentGroup,
            final Integer orderShipmentGroupSequence, Integer orderLineSequence, final OrderLine parentOrderLine,
            PartyContactMechanism partyContactMechanism, ShippingMethod shippingMethod, final Item item,
            InventoryCondition inventoryCondition, UnitOfMeasureType unitOfMeasureType, final Long quantity, Long unitAmount,
            final String description, CancellationPolicy cancellationPolicy, ReturnPolicy returnPolicy, Boolean taxable, OfferUse offerUse,
            final AssociateReferral associateReferral, final PartyPK createdBy) {
        var salesOrderLogic = SalesOrderLogic.getInstance();
        OrderLine orderLine = null;

        salesOrderLogic.checkOrderAvailableForModification(session, eea, order, createdBy);

        if(eea == null || !eea.hasExecutionErrors()) {
            var itemControl = (ItemControl)Session.getModelController(ItemControl.class);
            var offerControl = (OfferControl)Session.getModelController(OfferControl.class);
            var salesControl = (SalesControl)Session.getModelController(SalesControl.class);
            var orderDetail = order.getLastDetail();
            var itemDetail = item.getLastDetail();
            var itemDeliveryType = itemDetail.getItemDeliveryType();
            var currency = orderDetail.getCurrency();
            var customerType = salesOrderLogic.getOrderBillToCustomerType(order);

            if(customerType != null && shippingMethod != null) {
                salesOrderLogic.checkCustomerTypeShippingMethod(eea, customerType, shippingMethod);
            }

            if(eea == null || !eea.hasExecutionErrors()) {
                // ItemDeliveryType must be checked against the ContactMechanismType for the partyContactMechanism.

                // Check to see if an orderShipmentGroup was supplied. If it wasn't, try to get a default one for this order and itemDeliveryType.
                // If a default doesn't exist, then create one.
                if(orderShipmentGroup == null) {
                    orderShipmentGroup = salesOrderLogic.getDefaultOrderShipmentGroup(order, itemDeliveryType);

                    if(orderShipmentGroup == null) {
                        var holdUntilComplete = order.getLastDetail().getHoldUntilComplete();
                        var orderShipToParty = salesOrderLogic.getOrderShipToParty(order, true, createdBy);

                        // If partyContactMechanism is null, attempt to get from SHIP_TO party for the order.
                        // If no SHIP_TO party exists, try to copy from the BILL_TO party.

                        // TODO.

                        // Select an appropriate partyContactMechanism for the itemDeliveryType.

                        // TODO.

                        orderShipmentGroup = salesOrderLogic.createSalesOrderShipmentGroup(session, eea, order,
                                orderShipmentGroupSequence, itemDeliveryType, Boolean.TRUE, partyContactMechanism,
                                shippingMethod, holdUntilComplete, createdBy);
                    } else {
                        var orderShipmentGroupDetail = orderShipmentGroup.getLastDetail();

                        partyContactMechanism = orderShipmentGroupDetail.getPartyContactMechanism();
                        shippingMethod = orderShipmentGroupDetail.getShippingMethod();
                    }
                }

                // If shippingMethod was specified, check to see if it can be used for this item and partyContactMechanism.

                // Check InventoryCondition.
                if(inventoryCondition == null) {
                    var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);

                    inventoryCondition = inventoryControl.getDefaultInventoryCondition();

                    if(inventoryControl == null) {
                        handleExecutionError(UnknownDefaultInventoryConditionException.class, eea, ExecutionErrors.UnknownDefaultInventoryCondition.name());
                    }
                }

                // Check UnitOfMeasureType.
                if(unitOfMeasureType == null) {
                    var itemUnitOfMeasureType = itemControl.getDefaultItemUnitOfMeasureType(item);

                    if(itemUnitOfMeasureType == null) {
                        handleExecutionError(UnknownDefaultItemUnitOfMeasureTypeException.class, eea, ExecutionErrors.UnknownDefaultItemUnitOfMeasureType.name());
                    } else {
                        unitOfMeasureType = itemUnitOfMeasureType.getUnitOfMeasureType();
                    }
                }

                // Verify the OfferItem exists.
                if(offerUse == null) {
                    var salesOrder = salesControl.getSalesOrder(order);

                    offerUse = salesOrder.getOfferUse();
                }

                OfferItem offerItem = OfferItemLogic.getInstance().getOfferItem(eea, offerUse.getLastDetail().getOffer(), item);

                // Verify unitAmount.
                if(offerItem != null) {
                    var offerItemPrice = offerControl.getOfferItemPrice(offerItem, inventoryCondition, unitOfMeasureType, currency);

                    if(offerItemPrice == null) {
                        handleExecutionError(UnknownOfferItemPriceException.class, eea, ExecutionErrors.UnknownOfferItemPrice.name());
                    } else {
                        var itemPriceTypeName = itemDetail.getItemPriceType().getItemPriceTypeName();

                        if(itemPriceTypeName.equals(ItemPriceTypes.FIXED.name())) {
                            // We'll accept the supplied unitAmount as long as it passes the limit checks later on. Any enforcement of
                            // security should come in the UC.
                            if(unitAmount == null) {
                                var offerItemFixedPrice = offerControl.getOfferItemFixedPrice(offerItemPrice);

                                unitAmount = offerItemFixedPrice.getUnitPrice();
                            }
                        } else if(itemPriceTypeName.equals(ItemPriceTypes.VARIABLE.name())) {
                            if(unitAmount == null) {
                                handleExecutionError(UnitAmountRequiredException.class, eea, ExecutionErrors.UnitAmountRequired.name());
                            } else {
                                var offerItemVariablePrice = offerControl.getOfferItemVariablePrice(offerItemPrice);

                                if(unitAmount < offerItemVariablePrice.getMinimumUnitPrice()) {
                                    handleExecutionError(UnitAmountBelowMinimumUnitPriceException.class, eea, ExecutionErrors.UnitAmountBelowMinimumUnitPrice.name());
                                }

                                if(unitAmount > offerItemVariablePrice.getMaximumUnitPrice()) {
                                    handleExecutionError(UnitAmountAboveMaximumUnitPriceException.class, eea, ExecutionErrors.UnitAmountAboveMaximumUnitPrice.name());
                                }

                                if(unitAmount % offerItemVariablePrice.getUnitPriceIncrement() != 0) {
                                    handleExecutionError(UnitAmountNotMultipleOfUnitPriceIncrementException.class, eea, ExecutionErrors.UnitAmountNotMultipleOfUnitPriceIncrement.name());
                                }
                            }
                        } else {
                            handleExecutionError(UnknownOfferItemPriceException.class, eea, ExecutionErrors.UnknownItemPriceType.name(), itemPriceTypeName);
                        }
                    }
                }

                // Check ItemUnitPriceLimits.
                if(unitAmount != null) {
                    var itemUnitPriceLimit = itemControl.getItemUnitPriceLimit(item, inventoryCondition, unitOfMeasureType, currency);

                    // This isn't required. If it is missing, no check is performed.
                    if(itemUnitPriceLimit != null) {
                        var minimumUnitPrice = itemUnitPriceLimit.getMinimumUnitPrice();
                        var maximumUnitPrice = itemUnitPriceLimit.getMaximumUnitPrice();

                        if(minimumUnitPrice != null && unitAmount < minimumUnitPrice) {
                            handleExecutionError(UnitAmountBelowMinimumItemUnitPriceLimitException.class, eea, ExecutionErrors.UnitAmountBelowMinimumItemUnitPriceLimit.name());
                        }

                        if(maximumUnitPrice != null && unitAmount > maximumUnitPrice) {
                            handleExecutionError(UnitAmountAboveMaximumItemUnitPriceLimitException.class, eea, ExecutionErrors.UnitAmountAboveMaximumItemUnitPriceLimit.name());
                        }
                    }
                }

                // Check quantity being ordered and make sure that it's within acceptible limits. Both ItemUnitLimits and ItemUnitCustomerTypeLimits.
                if(inventoryCondition != null && unitOfMeasureType != null) {
                    var itemUnitLimit = itemControl.getItemUnitLimit(item, inventoryCondition, unitOfMeasureType);

                    if(itemUnitLimit != null) {
                        var minimumQuantity = itemUnitLimit.getMinimumQuantity();
                        var maximumQuantity = itemUnitLimit.getMaximumQuantity();

                        if(minimumQuantity != null && quantity < minimumQuantity) {
                            handleExecutionError(QuantityBelowMinimumItemUnitLimitException.class, eea, ExecutionErrors.QuantityBelowMinimumItemUnitLimit.name());
                        }

                        if(maximumQuantity != null && quantity > maximumQuantity) {
                            handleExecutionError(QuantityAboveMaximumItemUnitLimitException.class, eea, ExecutionErrors.QuantityAboveMaximumItemUnitLimit.name());
                        }
                    }

                    if(customerType != null) {
                        var itemUnitCustomerTypeLimit = itemControl.getItemUnitCustomerTypeLimit(item, inventoryCondition, unitOfMeasureType, customerType);

                        if(itemUnitCustomerTypeLimit != null) {
                            var minimumQuantity = itemUnitCustomerTypeLimit.getMinimumQuantity();
                            var maximumQuantity = itemUnitCustomerTypeLimit.getMaximumQuantity();

                            if(minimumQuantity != null && quantity < minimumQuantity) {
                                handleExecutionError(QuantityBelowMinimumItemUnitCustomerTypeLimitException.class, eea, ExecutionErrors.QuantityBelowMinimumItemUnitCustomerTypeLimit.name());
                            }

                            if(maximumQuantity != null && quantity > maximumQuantity) {
                                handleExecutionError(QuantityAboveMaximumItemUnitCustomerTypeLimitException.class, eea, ExecutionErrors.QuantityAboveMaximumItemUnitCustomerTypeLimit.name());
                            }
                        }
                    }
                }

                // Check Item's SalesOrderStartTime and SalesOrderEndTime.
                var salesOrderStartTime = itemDetail.getSalesOrderStartTime();
                if(salesOrderStartTime != null && session.START_TIME < salesOrderStartTime) {
                    handleExecutionError(CurrentTimeBeforeSalesOrderStartTimeException.class, eea, ExecutionErrors.CurrentTimeBeforeSalesOrderStartTime.name());
                }

                Long salesOrderEndTime = itemDetail.getSalesOrderEndTime();
                if(salesOrderEndTime != null && session.START_TIME > salesOrderEndTime) {
                    handleExecutionError(CurrentTimeAfterSalesOrderEndTimeException.class, eea, ExecutionErrors.CurrentTimeAfterSalesOrderEndTime.name());
                }

                // Check Item's status.
                if(!WorkflowStepLogic.getInstance().isEntityInWorkflowSteps(eea, ItemStatusConstants.Workflow_ITEM_STATUS, item,
                        ItemStatusConstants.WorkflowStep_ITEM_STATUS_DISCONTINUED).isEmpty()) {
                    handleExecutionError(ItemDiscontinuedException.class, eea, ExecutionErrors.ItemDiscontinued.name(), item.getLastDetail().getItemName());
                }

                // Create the line.
                if(eea == null || !eea.hasExecutionErrors()) {
                    // If a specific CancellationPolicy was specified, use that. Otherwise, try to use the one for the Item.
                    // If that's null, we'll leave it null for the OrderLine, which indicates that we should fall back to the
                    // one on the Order if it's ever needed.
                    if(cancellationPolicy == null) {
                        cancellationPolicy = itemDetail.getCancellationPolicy();
                    }

                    // If a specific ReturnPolicy was specified, use that. Otherwise, try to use the one for the Item.
                    // If that's null, we'll leave it null for the OrderLine, which indicates that we should fall back to the
                    // one on the Order if it's ever needed.
                    if(returnPolicy == null) {
                        returnPolicy = itemDetail.getReturnPolicy();
                    }

                    // If there was no taxable flag passed in, then get the taxable value from the Item. If that is true,
                    // the taxable flag from the Order will override it.
                    if(taxable == null) {
                        // taxable = itemDetail.getTaxable();
                        taxable = Boolean.TRUE; // TODO: This needs to consider the GeoCode-aware taxing system.
                        
                        if(taxable) {
                            taxable = orderDetail.getTaxable();
                        }
                    }

                    orderLine = createOrderLine(session, eea, order, orderLineSequence, parentOrderLine, orderShipmentGroup, item, inventoryCondition,
                            unitOfMeasureType, quantity, unitAmount, description, cancellationPolicy, returnPolicy, taxable, createdBy);

                    if(eea == null || !eea.hasExecutionErrors()) {
                        salesControl.createSalesOrderLine(orderLine, offerUse, associateReferral, createdBy);
                    }
                }
            }
        }
        
        return orderLine;
    }

    public OrderLine getOrderLineByName(final ExecutionErrorAccumulator eea, final String orderName, final String orderLineSequence) {
        return getOrderLineByName(eea, OrderConstants.OrderType_SALES_ORDER, orderName, orderLineSequence);
    }

    public OrderLine getOrderLineByNameForUpdate(final ExecutionErrorAccumulator eea, final String orderName, final String orderLineSequence) {
        return getOrderLineByNameForUpdate(eea, OrderConstants.OrderType_SALES_ORDER, orderName, orderLineSequence);
    }

}