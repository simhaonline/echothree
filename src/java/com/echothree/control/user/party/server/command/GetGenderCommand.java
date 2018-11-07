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

import com.echothree.control.user.party.common.form.GetGenderForm;
import com.echothree.control.user.party.common.result.GetGenderResult;
import com.echothree.control.user.party.common.result.PartyResultFactory;
import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.party.server.entity.Gender;
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

public class GetGenderCommand
        extends BaseSimpleCommand<GetGenderForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("GenderName", FieldType.ENTITY_NAME, true, null, null)
        ));
    }
    
    /** Creates a new instance of GetGenderCommand */
    public GetGenderCommand(UserVisitPK userVisitPK, GetGenderForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        GetGenderResult result = PartyResultFactory.getGetGenderResult();
        String genderName = form.getGenderName();
        Gender gender = partyControl.getGenderByName(genderName);
        
        if(gender != null) {
            result.setGender(partyControl.getGenderTransfer(getUserVisit(), gender));
            
            sendEventUsingNames(gender.getPrimaryKey(), EventTypes.READ.name(), null, null, getPartyPK());
        } else {
            addExecutionError(ExecutionErrors.UnknownGenderName.name(), genderName);
        }
        
        return result;
    }
    
}
