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

package com.echothree.control.user.scale.server.command;

import com.echothree.control.user.scale.common.form.CreatePartyScaleUseForm;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.scale.server.ScaleControl;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.scale.server.entity.PartyScaleUse;
import com.echothree.model.data.scale.server.entity.Scale;
import com.echothree.model.data.scale.server.entity.ScaleUseType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.control.CommandSecurityDefinition;
import com.echothree.util.server.control.PartyTypeDefinition;
import com.echothree.util.server.control.SecurityRoleDefinition;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreatePartyScaleUseCommand
        extends BaseSimpleCommand<CreatePartyScaleUseForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.PartyScaleUse.name(), SecurityRoles.Create.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("PartyName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("ScaleUseTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("ScaleName", FieldType.ENTITY_NAME, true, null, null)
                ));
    }

    /** Creates a new instance of CreatePartyScaleUseCommand */
    public CreatePartyScaleUseCommand(UserVisitPK userVisitPK, CreatePartyScaleUseForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        String partyName = form.getPartyName();
        Party party = null;

        if(partyName != null) {
            var partyControl = (PartyControl)Session.getModelController(PartyControl.class);

            party = partyControl.getPartyByName(partyName);
            if(party == null) {
                addExecutionError(ExecutionErrors.UnknownPartyName.name(), partyName);
            }
        } else {
            party = getParty();
        }

        if(!hasExecutionErrors()) {
            var scaleControl = (ScaleControl)Session.getModelController(ScaleControl.class);
            String scaleUseTypeName = form.getScaleUseTypeName();
            ScaleUseType scaleUseType = scaleControl.getScaleUseTypeByName(scaleUseTypeName);

            if(scaleUseType != null) {
                PartyScaleUse partyScaleUse = scaleControl.getPartyScaleUse(party, scaleUseType);

                if(partyScaleUse == null) {
                    String scaleName = form.getScaleName();
                    Scale scale = scaleControl.getScaleByName(scaleName);

                    if(scale != null) {
                        scaleControl.createPartyScaleUse(party, scaleUseType, scale, getPartyPK());
                    } else {
                        addExecutionError(ExecutionErrors.UnknownScaleName.name(), scaleName);
                    }
                } else {
                    addExecutionError(ExecutionErrors.DuplicatePartyScaleUse.name(), party.getLastDetail().getPartyName(), scaleUseTypeName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownScaleUseTypeName.name(), scaleUseTypeName);
            }
        }

        return null;
    }
    
}
