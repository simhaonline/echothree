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

package com.echothree.control.user.item.server.command;

import com.echothree.control.user.item.common.form.GetItemStatusChoicesForm;
import com.echothree.control.user.item.common.result.GetItemStatusChoicesResult;
import com.echothree.control.user.item.common.result.ItemResultFactory;
import com.echothree.model.control.item.server.ItemControl;
import com.echothree.model.data.item.server.entity.Item;
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

public class GetItemStatusChoicesCommand
        extends BaseSimpleCommand<GetItemStatusChoicesForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("ItemName", FieldType.ENTITY_NAME, false, null, null),
            new FieldDefinition("DefaultItemStatusChoice", FieldType.ENTITY_NAME, false, null, null),
            new FieldDefinition("AllowNullChoice", FieldType.BOOLEAN, true, null, null)
        ));
    }
    
    /** Creates a new instance of GetItemStatusChoicesCommand */
    public GetItemStatusChoicesCommand(UserVisitPK userVisitPK, GetItemStatusChoicesForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var itemControl = (ItemControl)Session.getModelController(ItemControl.class);
        GetItemStatusChoicesResult result = ItemResultFactory.getGetItemStatusChoicesResult();
        String itemName = form.getItemName();
        Item item = itemControl.getItemByName(itemName);
        
        if(itemName == null || item != null) {
            String defaultItemStatusChoice = form.getDefaultItemStatusChoice();
            boolean allowNullChoice = Boolean.parseBoolean(form.getAllowNullChoice());
            
            result.setItemStatusChoices(itemControl.getItemStatusChoices(defaultItemStatusChoice, getPreferredLanguage(),
                    allowNullChoice, item, getPartyPK()));
        } else {
            addExecutionError(ExecutionErrors.UnknownItemName.name(), itemName);
        }
        
        return result;
    }
    
}
