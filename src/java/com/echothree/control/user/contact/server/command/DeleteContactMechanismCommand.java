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

package com.echothree.control.user.contact.server.command;

import com.echothree.control.user.contact.common.form.DeleteContactMechanismForm;
import com.echothree.control.user.party.common.spec.PartySpec;
import com.echothree.model.control.contact.server.ContactControl;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.contact.server.entity.ContactMechanism;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.command.SecurityResult;
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

public class DeleteContactMechanismCommand
        extends BaseSimpleCommand<DeleteContactMechanismForm> {

    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_CUSTOMER, null),
                new PartyTypeDefinition(PartyConstants.PartyType_VENDOR, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.ContactMechanism.name(), SecurityRoles.Delete.name())
                        )))
                )));

        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ContactMechanismName", FieldType.ENTITY_NAME, true, null, null)
                ));
    }
    
    /** Creates a new instance of DeleteContactMechanismCommand */
    public DeleteContactMechanismCommand(UserVisitPK userVisitPK, DeleteContactMechanismForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected SecurityResult security() {
        var securityResult = super.security();

        if(securityResult == null) {
            var partyTypeName = getPartyTypeName();

            // If the executing Party's PartyType is CUSTOMER or VENDOR, then the specified ContactMechanism
            // MUST belong to the executing Party.
            if(partyTypeName.equals(PartyConstants.PartyType_CUSTOMER) || partyTypeName.equals(PartyConstants.PartyType_VENDOR)) {
                var contactControl = (ContactControl)Session.getModelController(ContactControl.class);
                var contactMechanismName = form.getContactMechanismName();
                var contactMechanism = contactControl.getContactMechanismByNameForUpdate(contactMechanismName);

                // If the specified ContactMechanism doesn't exist, that's OK - we'll report that later
                // during executing of the command.
                if(contactMechanism != null) {
                    var partyContactMechanism = contactControl.getPartyContactMechanism(getParty(), contactMechanism);

                    if(partyContactMechanism == null) {
                        getInsufficientSecurityResult(); // Fail.
                    }
                }
            }
        }

        return securityResult;
    }

    @Override
    protected BaseResult execute() {
        ContactControl contactControl = (ContactControl)Session.getModelController(ContactControl.class);
        String contactMechanismName = form.getContactMechanismName();
        ContactMechanism contactMechanism = contactControl.getContactMechanismByNameForUpdate(contactMechanismName);
        
        if(contactMechanism != null) {
            contactControl.deleteContactMechanism(contactMechanism, getPartyPK());
        } else {
            addExecutionError(ExecutionErrors.UnknownContactMechanismName.name(), contactMechanismName);
        }
        
        return null;
    }
    
}
