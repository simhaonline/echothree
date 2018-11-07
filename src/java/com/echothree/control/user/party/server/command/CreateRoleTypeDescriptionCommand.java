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

import com.echothree.control.user.party.common.form.CreateRoleTypeDescriptionForm;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.party.server.entity.RoleType;
import com.echothree.model.data.party.server.entity.RoleTypeDescription;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateRoleTypeDescriptionCommand
        extends BaseSimpleCommand<CreateRoleTypeDescriptionForm> {

    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("RoleTypeName", FieldType.STRING, true, null, 40L),
                new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, true, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of CreateRoleTypeDescriptionCommand */
    public CreateRoleTypeDescriptionCommand(UserVisitPK userVisitPK, CreateRoleTypeDescriptionForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        String roleTypeName = form.getRoleTypeName();
        RoleType roleType = partyControl.getRoleTypeByName(roleTypeName);
        
        if(roleType != null) {
            String languageIsoName = form.getLanguageIsoName();
            Language language = partyControl.getLanguageByIsoName(languageIsoName);
            
            if(language != null) {
                RoleTypeDescription roleTypeDescription = partyControl.getRoleTypeDescription(roleType, language);
                
                if(roleTypeDescription == null) {
                    String description = form.getDescription();
                    
                    partyControl.createRoleTypeDescription(roleType, language, description);
                } // TODO: error, duplicate roleTypeDescription
            } // TODO: error, unknown languageIsoName
        } // TODO: error, unknown roleTypeName
        
        return null;
    }
    
}
