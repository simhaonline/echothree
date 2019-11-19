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

package com.echothree.control.user.sales.server.command;

import com.echothree.control.user.sales.common.form.CreateSalesOrderLineForm;
import com.echothree.control.user.sales.common.result.SalesResultFactory;
import com.echothree.model.control.cancellationpolicy.common.CancellationPolicyConstants;
import com.echothree.model.control.cancellationpolicy.server.logic.CancellationPolicyLogic;
import com.echothree.model.control.inventory.server.InventoryControl;
import com.echothree.model.control.item.server.ItemControl;
import com.echothree.model.control.offer.server.logic.OfferUseLogic;
import com.echothree.model.control.order.server.logic.OrderLogic;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.returnpolicy.common.ReturnPolicyConstants;
import com.echothree.model.control.returnpolicy.server.logic.ReturnPolicyLogic;
import com.echothree.model.control.sales.server.logic.SalesOrderLineLogic;
import com.echothree.model.control.sales.server.logic.SalesOrderLogic;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.control.uom.server.UomControl;
import com.echothree.model.data.associate.server.entity.AssociateReferral;
import com.echothree.model.data.offer.server.entity.OfferUse;
import com.echothree.model.data.order.server.entity.Order;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.control.CommandSecurityDefinition;
import com.echothree.util.server.control.PartyTypeDefinition;
import com.echothree.util.server.control.SecurityRoleDefinition;
import com.echothree.util.server.persistence.Session;
import com.echothree.util.server.validation.Validator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateSalesOrderLineCommand
        extends BaseSimpleCommand<CreateSalesOrderLineForm> {

    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                    new SecurityRoleDefinition(SecurityRoleGroups.SalesOrderLine.name(), SecurityRoles.Create.name())
                    )))
                )));

        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("OrderName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("OrderLineSequence", FieldType.UNSIGNED_INTEGER, false, null, null),
                new FieldDefinition("ItemName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("InventoryConditionName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("UnitOfMeasureTypeName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("Quantity", FieldType.UNSIGNED_LONG, true, null, null),
                new FieldDefinition("UnitAmount", FieldType.UNSIGNED_PRICE_UNIT, false, null, null),
                new FieldDefinition("Taxable", FieldType.BOOLEAN, false, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L),
                new FieldDefinition("CancellationPolicyName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("ReturnPolicyName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("OfferName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("UseName", FieldType.ENTITY_NAME, false, null, null)
                ));
    }

    /** Creates a new instance of CreateSalesOrderLineCommand */
    public CreateSalesOrderLineCommand(UserVisitPK userVisitPK, CreateSalesOrderLineForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }

    @Override
    protected void setupValidator(Validator validator) {
        String orderName = form.getOrderName();
        Order order = orderName == null ? null : SalesOrderLogic.getInstance().getOrderByName(this, orderName);
        
        if(order != null) {
            validator.setCurrency(OrderLogic.getInstance().getOrderCurrency(order));
        }
    }
    
    @Override
    protected BaseResult execute() {
        var result = SalesResultFactory.getCreateSalesOrderLineResult();
        var orderName = form.getOrderName();
        var order = SalesOrderLogic.getInstance().getOrderByName(this, orderName);

        if(!hasExecutionErrors()) {
            var itemControl = (ItemControl)Session.getModelController(ItemControl.class);
            var itemName = form.getItemName();
            var item = itemControl.getItemByNameThenAlias(itemName);
            
            if(item != null) {
                var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);
                var inventoryConditionName = form.getInventoryConditionName();
                var inventoryCondition = inventoryConditionName == null ? null : inventoryControl.getInventoryConditionByName(inventoryConditionName);
                
                if(inventoryConditionName == null || inventoryCondition != null) {
                    var uomControl = (UomControl)Session.getModelController(UomControl.class);
                    var itemDetail = item.getLastDetail();
                    var unitOfMeasureKind = itemDetail.getUnitOfMeasureKind();
                    var unitOfMeasureTypeName = form.getUnitOfMeasureTypeName();
                    var unitOfMeasureType = unitOfMeasureTypeName == null ? null : uomControl.getUnitOfMeasureTypeByName(unitOfMeasureKind, unitOfMeasureTypeName);

                    if(unitOfMeasureTypeName == null || unitOfMeasureType != null) {
                        var cancellationPolicyName = form.getCancellationPolicyName();
                        var cancellationPolicy = cancellationPolicyName == null ? null : CancellationPolicyLogic.getInstance().getCancellationPolicyByName(this, CancellationPolicyConstants.CancellationKind_CUSTOMER_CANCELLATION, cancellationPolicyName);
                        
                        if(!hasExecutionErrors()) {
                            var returnPolicyName = form.getReturnPolicyName();
                            var returnPolicy = returnPolicyName == null ? null : ReturnPolicyLogic.getInstance().getReturnPolicyByName(this, ReturnPolicyConstants.ReturnKind_CUSTOMER_RETURN, returnPolicyName);
                            
                            if(!hasExecutionErrors()) {
                                var offerName = form.getOfferName();
                                var useName = form.getUseName();
                                var parameterCount = (offerName == null ? 0 : 1) + (useName == null ? 0 : 1);
                                
                                if(parameterCount == 0 || parameterCount == 2) {
                                    OfferUse offerUse = null;
                                    
                                    if(offerName != null) {
                                        offerUse = OfferUseLogic.getInstance().getOfferUseByName(this, offerName, useName);
                                    }
                                    
                                    if(!hasExecutionErrors()) {
                                        var strOrderLineSequence = form.getOrderLineSequence();
                                        var orderLineSequence = strOrderLineSequence == null ? null : Integer.valueOf(strOrderLineSequence);
                                        var quantity = Long.valueOf(form.getQuantity());
                                        var strUnitAmount = form.getUnitAmount();
                                        var unitAmount = strUnitAmount == null ? null : Long.valueOf(strUnitAmount);;
                                        var description = form.getDescription();
                                        var strTaxable = form.getTaxable();
                                        var taxable = strTaxable == null ? null : Boolean.valueOf(strTaxable);
                                        AssociateReferral associateReferral = null;
                                        
                                        var orderLine = SalesOrderLineLogic.getInstance().createSalesOrderLine(session, this, order, null, null, orderLineSequence,
                                                null, null, null, item, inventoryCondition, unitOfMeasureType, quantity, unitAmount, description,
                                                cancellationPolicy, returnPolicy, taxable, offerUse, associateReferral, getPartyPK());

                                        var orderLineDetail = orderLine.getLastDetail();

                                        result.setOrderName(orderLineDetail.getOrder().getLastDetail().getOrderName());
                                        result.setOrderLineSequence(orderLineDetail.getOrderLineSequence().toString());
                                        result.setEntityRef(orderLine.getPrimaryKey().getEntityRef());
                                    }
                                } else {
                                    addExecutionError(ExecutionErrors.InvalidParameterCount.name());
                                }
                            }
                        }
                    } else {
                        addExecutionError(ExecutionErrors.UnknownUnitOfMeasureTypeName.name(), unitOfMeasureKind.getLastDetail().getUnitOfMeasureKindName(), unitOfMeasureTypeName);
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnknownInventoryConditionName.name(), inventoryConditionName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownItemName.name(), itemName);
            }
        }

        return result;
    }

}
