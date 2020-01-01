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

package com.echothree.control.user.party.server.command;

import com.echothree.control.user.party.common.form.CreatePartyRelationshipTypeForm;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.party.server.entity.PartyRelationshipType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreatePartyRelationshipTypeCommand
        extends BaseSimpleCommand<CreatePartyRelationshipTypeForm> {

    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("PartyRelationshipTypeName", FieldType.STRING, true, null, 40L)
                ));
    }
    
    /** Creates a new instance of CreatePartyRelationshipTypeCommand */
    public CreatePartyRelationshipTypeCommand(UserVisitPK userVisitPK, CreatePartyRelationshipTypeForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        String partyRelationshipTypeName = form.getPartyRelationshipTypeName();
        PartyRelationshipType partyRelationshipType = partyControl.getPartyRelationshipTypeByName(partyRelationshipTypeName);
        
        if(partyRelationshipType == null) {
            partyControl.createPartyRelationshipType(partyRelationshipTypeName);
        } // TODO: error, duplicate partyRelationshipTypeName
        
        return null;
    }
    
}
