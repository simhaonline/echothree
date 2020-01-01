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

package com.echothree.control.user.shipment.server.command;

import com.echothree.control.user.shipment.common.edit.ShipmentEditFactory;
import com.echothree.control.user.shipment.common.edit.ShipmentTypeShippingMethodEdit;
import com.echothree.control.user.shipment.common.form.EditShipmentTypeShippingMethodForm;
import com.echothree.control.user.shipment.common.result.EditShipmentTypeShippingMethodResult;
import com.echothree.control.user.shipment.common.result.ShipmentResultFactory;
import com.echothree.control.user.shipment.common.spec.ShipmentTypeShippingMethodSpec;
import com.echothree.model.control.shipment.server.ShipmentControl;
import com.echothree.model.control.shipping.server.ShippingControl;
import com.echothree.model.data.shipment.server.entity.ShipmentType;
import com.echothree.model.data.shipment.server.entity.ShipmentTypeShippingMethod;
import com.echothree.model.data.shipment.server.value.ShipmentTypeShippingMethodValue;
import com.echothree.model.data.shipping.server.entity.ShippingMethod;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseEditCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditShipmentTypeShippingMethodCommand
        extends BaseEditCommand<ShipmentTypeShippingMethodSpec, ShipmentTypeShippingMethodEdit> {
    
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ShipmentTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("ShippingMethodName", FieldType.ENTITY_NAME, true, null, null)
                ));
        
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null)
                ));
    }
    
    /** Creates a new instance of EditShipmentTypeShippingMethodCommand */
    public EditShipmentTypeShippingMethodCommand(UserVisitPK userVisitPK, EditShipmentTypeShippingMethodForm form) {
        super(userVisitPK, form, null, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    protected BaseResult execute() {
        var shipmentControl = (ShipmentControl)Session.getModelController(ShipmentControl.class);
        EditShipmentTypeShippingMethodResult result = ShipmentResultFactory.getEditShipmentTypeShippingMethodResult();
        String shipmentTypeName = spec.getShipmentTypeName();
        ShipmentType shipmentType = shipmentControl.getShipmentTypeByName(shipmentTypeName);
        
        if(shipmentType != null) {
            var shippingControl = (ShippingControl)Session.getModelController(ShippingControl.class);
            String shippingMethodName = spec.getShippingMethodName();
            ShippingMethod shippingMethod = shippingControl.getShippingMethodByName(shippingMethodName);
            
            if(shippingMethod != null) {
                if(editMode.equals(EditMode.LOCK)) {
                    ShipmentTypeShippingMethod shipmentTypeShippingMethod = shipmentControl.getShipmentTypeShippingMethod(shipmentType, shippingMethod);
                    
                    if(shipmentTypeShippingMethod != null) {
                        result.setShipmentTypeShippingMethod(shipmentControl.getShipmentTypeShippingMethodTransfer(getUserVisit(), shipmentTypeShippingMethod));
                        
                        if(lockEntity(shippingMethod)) {
                            ShipmentTypeShippingMethodEdit edit = ShipmentEditFactory.getShipmentTypeShippingMethodEdit();
                            
                            result.setEdit(edit);
                            edit.setIsDefault(shipmentTypeShippingMethod.getIsDefault().toString());
                            edit.setSortOrder(shipmentTypeShippingMethod.getSortOrder().toString());
                            
                        } else {
                            addExecutionError(ExecutionErrors.EntityLockFailed.name());
                        }
                        
                        result.setEntityLock(getEntityLockTransfer(shipmentTypeShippingMethod));
                    } else {
                        addExecutionError(ExecutionErrors.UnknownShipmentTypeShippingMethod.name());
                    }
                } else if(editMode.equals(EditMode.UPDATE)) {
                    ShipmentTypeShippingMethodValue shipmentTypeShippingMethodValue = shipmentControl.getShipmentTypeShippingMethodValueForUpdate(shipmentType, shippingMethod);
                    
                    if(shipmentTypeShippingMethodValue != null) {
                        if(lockEntityForUpdate(shippingMethod)) {
                            try {
                                shipmentTypeShippingMethodValue.setIsDefault(Boolean.valueOf(edit.getIsDefault()));
                                shipmentTypeShippingMethodValue.setSortOrder(Integer.valueOf(edit.getSortOrder()));
                                
                                shipmentControl.updateShipmentTypeShippingMethodFromValue(shipmentTypeShippingMethodValue, getPartyPK());
                            } finally {
                                unlockEntity(shippingMethod);
                            }
                        } else {
                            addExecutionError(ExecutionErrors.EntityLockStale.name());
                        }
                    } else {
                        addExecutionError(ExecutionErrors.UnknownShipmentTypeShippingMethod.name());
                    }
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownShippingMethodName.name(), shippingMethodName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownShipmentTypeName.name(), shipmentTypeName);
        }
        
        return result;
    }
    
}
