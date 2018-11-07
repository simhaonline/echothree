// --------------------------------------------------------------------------------
// Copyright 2002-2018 Echo Three, LLC
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

package com.echothree.control.user.party.server.command;

import com.echothree.control.user.party.common.form.DeleteNameSuffixForm;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.party.server.entity.NameSuffix;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.EntityPermission;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeleteNameSuffixCommand
        extends BaseSimpleCommand<DeleteNameSuffixForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("NameSuffixId", FieldType.ID, true, null, null)
        ));
    }
    
    /** Creates a new instance of DeleteNameSuffixCommand */
    public DeleteNameSuffixCommand(UserVisitPK userVisitPK, DeleteNameSuffixForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        String nameSuffixId = form.getNameSuffixId();
        NameSuffix nameSuffix = partyControl.convertNameSuffixIdToEntity(nameSuffixId, EntityPermission.READ_WRITE);
        
        if(nameSuffix != null) {
            partyControl.deleteNameSuffix(nameSuffix, getPartyPK());
        } else {
            addExecutionError(ExecutionErrors.UnknownNameSuffixId.name(), nameSuffixId);
        }
        
        return null;
    }
    
}
