// --------------------------------------------------------------------------------
// Copyright 2002-2020 Echo Three, LLC
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

package com.echothree.control.user.item.server.command;

import com.echothree.control.user.item.common.form.GetItemUnitCustomerTypeLimitForm;
import com.echothree.control.user.item.common.result.GetItemUnitCustomerTypeLimitResult;
import com.echothree.control.user.item.common.result.ItemResultFactory;
import com.echothree.model.control.customer.server.CustomerControl;
import com.echothree.model.control.inventory.server.InventoryControl;
import com.echothree.model.control.item.server.ItemControl;
import com.echothree.model.control.uom.server.UomControl;
import com.echothree.model.data.customer.server.entity.CustomerType;
import com.echothree.model.data.inventory.server.entity.InventoryCondition;
import com.echothree.model.data.item.server.entity.Item;
import com.echothree.model.data.item.server.entity.ItemUnitCustomerTypeLimit;
import com.echothree.model.data.uom.server.entity.UnitOfMeasureKind;
import com.echothree.model.data.uom.server.entity.UnitOfMeasureType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetItemUnitCustomerTypeLimitCommand
        extends BaseSimpleCommand<GetItemUnitCustomerTypeLimitForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("ItemName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("InventoryConditionName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("UnitOfMeasureTypeName", FieldType.PERCENT, true, null, null),
            new FieldDefinition("CustomerTypeName", FieldType.PERCENT, true, null, null)
        ));
    }
    
    /** Creates a new instance of GetItemUnitCustomerTypeLimitCommand */
    public GetItemUnitCustomerTypeLimitCommand(UserVisitPK userVisitPK, GetItemUnitCustomerTypeLimitForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var itemControl = (ItemControl)Session.getModelController(ItemControl.class);
        GetItemUnitCustomerTypeLimitResult result = ItemResultFactory.getGetItemUnitCustomerTypeLimitResult();
        String itemName = form.getItemName();
        Item item = itemControl.getItemByName(itemName);
        
        if(item != null) {
            var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);
            String inventoryConditionName = form.getInventoryConditionName();
            InventoryCondition inventoryCondition = inventoryControl.getInventoryConditionByName(inventoryConditionName);
            
            if(inventoryCondition != null) {
                var uomControl = (UomControl)Session.getModelController(UomControl.class);
                String unitOfMeasureTypeName = form.getUnitOfMeasureTypeName();
                UnitOfMeasureKind unitOfMeasureKind = item.getLastDetail().getUnitOfMeasureKind();
                UnitOfMeasureType unitOfMeasureType = uomControl.getUnitOfMeasureTypeByName(unitOfMeasureKind, unitOfMeasureTypeName);
                
                if(unitOfMeasureType != null) {
                    var customerControl = (CustomerControl)Session.getModelController(CustomerControl.class);
                    String customerTypeName = form.getCustomerTypeName();
                    CustomerType customerType = customerControl.getCustomerTypeByName(customerTypeName);
                    
                    if(customerType != null) {
                        ItemUnitCustomerTypeLimit itemUnitCustomerTypeLimit = itemControl.getItemUnitCustomerTypeLimit(item,
                                inventoryCondition, unitOfMeasureType, customerType);
                        
                        if(itemUnitCustomerTypeLimit != null) {
                            result.setItemUnitCustomerTypeLimit(itemControl.getItemUnitCustomerTypeLimitTransfer(getUserVisit(), itemUnitCustomerTypeLimit));
                        } else {
                            addExecutionError(ExecutionErrors.UnknownItemUnitCustomerTypeLimit.name(), itemName, inventoryConditionName, unitOfMeasureTypeName,
                                    customerTypeName);
                        }
                    } else {
                        addExecutionError(ExecutionErrors.UnknownCustomerTypeName.name(), customerTypeName);
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
        
        return result;
    }
    
}
