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

package com.echothree.control.user.inventory.server.command;

import com.echothree.control.user.inventory.common.form.GetInventoryConditionForm;
import com.echothree.control.user.inventory.common.result.InventoryResultFactory;
import com.echothree.control.user.inventory.common.result.GetInventoryConditionResult;
import com.echothree.model.control.inventory.server.control.InventoryControl;
import com.echothree.model.control.inventory.server.logic.InventoryConditionLogic;
import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.data.inventory.server.entity.InventoryCondition;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSingleEntityCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetInventoryConditionCommand
        extends BaseSingleEntityCommand<InventoryCondition, GetInventoryConditionForm> {
    
    // No COMMAND_SECURITY_DEFINITION, anyone may execute this command.
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("InventoryConditionName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("EntityRef", FieldType.ENTITY_REF, false, null, null),
                new FieldDefinition("Key", FieldType.KEY, false, null, null),
                new FieldDefinition("Guid", FieldType.GUID, false, null, null),
                new FieldDefinition("Ulid", FieldType.ULID, false, null, null)
                ));
    }
    
    /** Creates a new instance of GetInventoryConditionCommand */
    public GetInventoryConditionCommand(UserVisitPK userVisitPK, GetInventoryConditionForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected InventoryCondition getEntity() {
        InventoryCondition inventoryCondition = InventoryConditionLogic.getInstance().getInventoryConditionByUniversalSpec(this, form, true);

        if(inventoryCondition != null) {
            sendEventUsingNames(inventoryCondition.getPrimaryKey(), EventTypes.READ.name(), null, null, getPartyPK());
        }

        return inventoryCondition;
    }
    
    @Override
    protected BaseResult getTransfer(InventoryCondition inventoryCondition) {
        var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);
        GetInventoryConditionResult result = InventoryResultFactory.getGetInventoryConditionResult();

        if(inventoryCondition != null) {
            result.setInventoryCondition(inventoryControl.getInventoryConditionTransfer(getUserVisit(), inventoryCondition));
        }

        return result;
    }
    
}
