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

package com.echothree.control.user.subscription.server.command;

import com.echothree.control.user.subscription.common.form.GetSubscriptionsForm;
import com.echothree.control.user.subscription.common.result.GetSubscriptionsResult;
import com.echothree.control.user.subscription.common.result.SubscriptionResultFactory;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.subscription.server.SubscriptionControl;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.subscription.server.entity.SubscriptionKind;
import com.echothree.model.data.subscription.server.entity.SubscriptionType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetSubscriptionsCommand
        extends BaseSimpleCommand<GetSubscriptionsForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("SubscriptionKindName", FieldType.ENTITY_NAME, false, null, null),
            new FieldDefinition("SubscriptionTypeName", FieldType.ENTITY_NAME, false, null, null),
            new FieldDefinition("PartyName", FieldType.ENTITY_NAME, false, null, null)
        ));
    }
    
    /** Creates a new instance of GetSubscriptionsCommand */
    public GetSubscriptionsCommand(UserVisitPK userVisitPK, GetSubscriptionsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        SubscriptionControl subscriptionControl = (SubscriptionControl)Session.getModelController(SubscriptionControl.class);
        GetSubscriptionsResult result = SubscriptionResultFactory.getGetSubscriptionsResult();
        String subscriptionKindName = form.getSubscriptionKindName();
        String subscriptionTypeName = form.getSubscriptionTypeName();
        String partyName = form.getPartyName();
        UserVisit userVisit = getUserVisit();
        
        if(subscriptionKindName != null && subscriptionTypeName != null && partyName == null) {
            SubscriptionKind subscriptionKind = subscriptionControl.getSubscriptionKindByName(subscriptionKindName);
            
            if(subscriptionKind != null) {
                SubscriptionType subscriptionType = subscriptionControl.getSubscriptionTypeByName(subscriptionKind, subscriptionTypeName);
                
                result.setSubscriptionKind(subscriptionControl.getSubscriptionKindTransfer(userVisit, subscriptionKind));
                
                if(subscriptionType != null) {
                    result.setSubscriptionType(subscriptionControl.getSubscriptionTypeTransfer(userVisit, subscriptionType));
                    result.setSubscriptions(subscriptionControl.getSubscriptionTransfersBySubscriptionType(userVisit, subscriptionType));
                } else {
                    addExecutionError(ExecutionErrors.UnknownSubscriptionTypeName.name(), subscriptionTypeName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownSubscriptionKindName.name(), subscriptionKindName);
            }
        } else if(subscriptionKindName == null && subscriptionTypeName == null && partyName != null) {
            PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
            Party party = partyControl.getPartyByName(partyName);
            
            if(party != null) {
                result.setParty(partyControl.getPartyTransfer(userVisit, party));
                result.setSubscriptions(subscriptionControl.getSubscriptionTransfersByParty(userVisit, party));
            } else {
                addExecutionError(ExecutionErrors.UnknownPartyName.name(), partyName);
            }
        } else {
            addExecutionError(ExecutionErrors.InvalidParameterCount.name());
        }
        
        return result;
    }
    
}
