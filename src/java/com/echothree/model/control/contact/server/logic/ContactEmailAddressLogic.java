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

package com.echothree.model.control.contact.server.logic;

import com.echothree.model.control.contact.common.ContactMechanismTypes;
import com.echothree.model.control.contact.common.ContactMechanismPurposes;
import com.echothree.model.control.contact.common.workflow.EmailAddressStatusConstants;
import com.echothree.model.control.contact.common.workflow.EmailAddressVerificationConstants;
import com.echothree.model.control.contact.server.ContactControl;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.sequence.common.SequenceConstants;
import com.echothree.model.control.sequence.server.logic.SequenceLogic;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.data.contact.server.entity.ContactMechanism;
import com.echothree.model.data.contact.server.entity.ContactMechanismPurpose;
import com.echothree.model.data.contact.server.entity.ContactMechanismType;
import com.echothree.model.data.contact.server.entity.PartyContactMechanism;
import com.echothree.model.data.core.server.entity.EntityInstance;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.util.common.persistence.BasePK;
import com.echothree.util.server.control.BaseLogic;
import com.echothree.util.server.persistence.Session;

public class ContactEmailAddressLogic
    extends BaseLogic {
    
    private ContactEmailAddressLogic() {
        super();
    }
    
    private static class ContactEmailAddressLogicHolder {
        static ContactEmailAddressLogic instance = new ContactEmailAddressLogic();
    }
    
    public static ContactEmailAddressLogic getInstance() {
        return ContactEmailAddressLogicHolder.instance;
    }
    
    public PartyContactMechanism createContactEmailAddress(Party party, String emailAddress, Boolean allowSolicitation,
            String description, String contactMechanismPurposeName, BasePK createdBy) {
        var contactControl = (ContactControl)Session.getModelController(ContactControl.class);
        var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
        var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
        String contactMechanismName = SequenceLogic.getInstance().getNextSequenceValue(null, SequenceConstants.SequenceType_CONTACT_MECHANISM);
        ContactMechanismType contactMechanismType = contactControl.getContactMechanismTypeByName(ContactMechanismTypes.EMAIL_ADDRESS.name());

        ContactMechanism contactMechanism = contactControl.createContactMechanism(contactMechanismName, contactMechanismType,
                allowSolicitation, createdBy);

        contactControl.createContactEmailAddress(contactMechanism, emailAddress, createdBy);

        PartyContactMechanism partyContactMechanism = contactControl.createPartyContactMechanism(party, contactMechanism, description, Boolean.FALSE, 1, createdBy);

        EntityInstance entityInstance = coreControl.getEntityInstanceByBasePK(contactMechanism.getPrimaryKey());
        workflowControl.addEntityToWorkflowUsingNames(null, EmailAddressStatusConstants.Workflow_EMAIL_ADDRESS_STATUS,
                EmailAddressStatusConstants.WorkflowEntrance_NEW_EMAIL_ADDRESS, entityInstance, null, null, createdBy);
        workflowControl.addEntityToWorkflowUsingNames(null, EmailAddressVerificationConstants.Workflow_EMAIL_ADDRESS_VERIFICATION,
                EmailAddressVerificationConstants.WorkflowEntrance_NEW_EMAIL_ADDRESS, entityInstance, null, null, createdBy);

        if(contactMechanismPurposeName != null) {
            ContactMechanismPurpose contactMechanismPurpose = contactControl.getContactMechanismPurposeByName(ContactMechanismPurposes.PRIMARY_EMAIL.name());

            contactControl.createPartyContactMechanismPurpose(partyContactMechanism, contactMechanismPurpose, Boolean.FALSE, 1, createdBy);
        }
        
        return partyContactMechanism;
    }
    
}
