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

package com.echothree.control.user.contact.server.command;

import com.echothree.control.user.contact.common.form.CreateContactEmailAddressForm;
import com.echothree.control.user.contact.common.result.ContactResultFactory;
import com.echothree.control.user.contact.common.result.CreateContactEmailAddressResult;
import com.echothree.model.control.contact.server.logic.ContactEmailAddressLogic;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.contact.server.entity.ContactMechanism;
import com.echothree.model.data.contact.server.entity.PartyContactMechanism;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.persistence.BasePK;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateContactEmailAddressCommand
        extends BaseSimpleCommand<CreateContactEmailAddressForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("PartyName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("EmailAddress", FieldType.EMAIL_ADDRESS, true, null, null),
                new FieldDefinition("AllowSolicitation", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of CreateContactEmailAddressCommand */
    public CreateContactEmailAddressCommand(UserVisitPK userVisitPK, CreateContactEmailAddressForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        CreateContactEmailAddressResult result = ContactResultFactory.getCreateContactEmailAddressResult();
        PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        String partyName = form.getPartyName();
        Party party = partyControl.getPartyByName(partyName);
        
        if(party != null) {
            BasePK createdBy = getPartyPK();
            String emailAddress = form.getEmailAddress();
            Boolean allowSolicitation = Boolean.valueOf(form.getAllowSolicitation());
            String description = form.getDescription();
            
            PartyContactMechanism partyContactMechanism = ContactEmailAddressLogic.getInstance().createContactEmailAddress(party,
                    emailAddress, allowSolicitation, description, null, createdBy);
            ContactMechanism contactMechanism = partyContactMechanism.getLastDetail().getContactMechanism();
            
            result.setContactMechanismName(contactMechanism.getLastDetail().getContactMechanismName());
            result.setEntityRef(contactMechanism.getPrimaryKey().getEntityRef());
        } else {
            addExecutionError(ExecutionErrors.UnknownPartyName.name(), partyName);
        }
        
        return result;
    }

}
