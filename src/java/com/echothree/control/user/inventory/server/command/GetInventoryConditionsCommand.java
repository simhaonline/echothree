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

import com.echothree.control.user.inventory.common.form.GetInventoryConditionsForm;
import com.echothree.control.user.inventory.common.result.InventoryResultFactory;
import com.echothree.control.user.inventory.common.result.GetInventoryConditionsResult;
import com.echothree.model.control.inventory.server.InventoryControl;
import com.echothree.model.data.inventory.server.entity.InventoryCondition;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseMultipleEntitiesCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GetInventoryConditionsCommand
        extends BaseMultipleEntitiesCommand<InventoryCondition, GetInventoryConditionsForm> {
    
    // No COMMAND_SECURITY_DEFINITION, anyone may execute this command.
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                ));
    }
    
    /** Creates a new instance of GetInventoryConditionsCommand */
    public GetInventoryConditionsCommand(UserVisitPK userVisitPK, GetInventoryConditionsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected Collection<InventoryCondition> getEntities() {
        var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);
        
        return inventoryControl.getInventoryConditions();
    }
    
    @Override
    protected BaseResult getTransfers(Collection<InventoryCondition> entities) {
        GetInventoryConditionsResult result = InventoryResultFactory.getGetInventoryConditionsResult();
        var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);
        
        result.setInventoryConditions(inventoryControl.getInventoryConditionTransfers(getUserVisit(), entities));
        
        return result;
    }
    
}
