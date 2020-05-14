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

package com.echothree.control.user.payment.server.command;

import com.echothree.control.user.payment.common.form.GetPartyPaymentMethodsForm;
import com.echothree.control.user.payment.common.result.GetPartyPaymentMethodsResult;
import com.echothree.control.user.payment.common.result.PaymentResultFactory;
import com.echothree.model.control.party.common.PartyTypes;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.payment.server.control.PaymentControl;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.party.server.entity.Party;
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

public class GetPartyPaymentMethodsCommand
        extends BaseSimpleCommand<GetPartyPaymentMethodsForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyTypes.UTILITY.name(), null),
                new PartyTypeDefinition(PartyTypes.CUSTOMER.name(), null),
                new PartyTypeDefinition(PartyTypes.EMPLOYEE.name(), Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.PartyPaymentMethod.name(), SecurityRoles.List.name())
                        )))
                )));

        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("PartyName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("DefaultPaymentMethod", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("AllowNull", FieldType.BOOLEAN, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetPartyPaymentMethodsCommand */
    public GetPartyPaymentMethodsCommand(UserVisitPK userVisitPK, GetPartyPaymentMethodsForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        GetPartyPaymentMethodsResult result = PaymentResultFactory.getGetPartyPaymentMethodsResult();
        Party party = getParty();
        String partyTypeName = party.getLastDetail().getPartyType().getPartyTypeName();

        // If the caller is a CUSTOMER, then they're the Party. If they're not, the PartyName parameter is
        // required, and we'll look them up.
        if(!partyTypeName.equals(PartyTypes.CUSTOMER.name())) {
            String partyName = form.getPartyName();

            if(partyName == null) {
                addExecutionError(ExecutionErrors.PartyNameRequired.name());
            } else {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);

                party = partyControl.getPartyByName(partyName);

                if(party == null) {
                    addExecutionError(ExecutionErrors.UnknownPartyName.name(), partyName);
                }
            }
        }

        if(!hasExecutionErrors()) {
            var paymentControl = (PaymentControl)Session.getModelController(PaymentControl.class);

            result.setPartyPaymentMethods(paymentControl.getPartyPaymentMethodTransfersByParty(getUserVisit(), party));
        }

        return result;
    }
    
}
