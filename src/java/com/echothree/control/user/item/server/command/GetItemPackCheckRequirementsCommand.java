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

import com.echothree.control.user.item.common.form.GetItemPackCheckRequirementsForm;
import com.echothree.control.user.item.common.result.GetItemPackCheckRequirementsResult;
import com.echothree.control.user.item.common.result.ItemResultFactory;
import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.control.item.server.ItemControl;
import com.echothree.model.data.item.server.entity.Item;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetItemPackCheckRequirementsCommand
        extends BaseSimpleCommand<GetItemPackCheckRequirementsForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("ItemName", FieldType.ENTITY_NAME, true, null, null)
        ));
    }
    
    /** Creates a new instance of GetItemPackCheckRequirementsCommand */
    public GetItemPackCheckRequirementsCommand(UserVisitPK userVisitPK, GetItemPackCheckRequirementsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        var itemControl = (ItemControl)Session.getModelController(ItemControl.class);
        GetItemPackCheckRequirementsResult result = ItemResultFactory.getGetItemPackCheckRequirementsResult();
        String itemName = form.getItemName();
        Item item = itemControl.getItemByName(itemName);
        
        if(item != null) {
            UserVisit userVisit = getUserVisit();
            
            result.setItem(itemControl.getItemTransfer(userVisit, item));
            result.setItemPackCheckRequirements(itemControl.getItemPackCheckRequirementTransfersByItem(userVisit, item));
            
            sendEventUsingNames(item.getPrimaryKey(), EventTypes.READ.name(), null, null, getPartyPK());
        } else {
            addExecutionError(ExecutionErrors.UnknownItemName.name(), itemName);
        }
        
        return result;
    }
    
}
