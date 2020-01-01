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

package com.echothree.control.user.subscription.server.command;

import com.echothree.control.user.subscription.common.form.GetSubscriptionTypeChoicesForm;
import com.echothree.control.user.subscription.common.result.GetSubscriptionTypeChoicesResult;
import com.echothree.control.user.subscription.common.result.SubscriptionResultFactory;
import com.echothree.model.control.subscription.server.SubscriptionControl;
import com.echothree.model.data.subscription.server.entity.SubscriptionKind;
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

public class GetSubscriptionTypeChoicesCommand
        extends BaseSimpleCommand<GetSubscriptionTypeChoicesForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("SubscriptionKindName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("DefaultSubscriptionTypeChoice", FieldType.ENTITY_NAME, false, null, null),
            new FieldDefinition("AllowNullChoice", FieldType.BOOLEAN, true, null, null)
        ));
    }
    
    /** Creates a new instance of GetSubscriptionTypeChoicesCommand */
    public GetSubscriptionTypeChoicesCommand(UserVisitPK userVisitPK, GetSubscriptionTypeChoicesForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var subscriptionControl = (SubscriptionControl)Session.getModelController(SubscriptionControl.class);
        GetSubscriptionTypeChoicesResult result = SubscriptionResultFactory.getGetSubscriptionTypeChoicesResult();
        String subscriptionKindName = form.getSubscriptionKindName();
        SubscriptionKind subscriptionKind = subscriptionControl.getSubscriptionKindByName(subscriptionKindName);
        
        if(subscriptionKind != null) {
            String defaultSubscriptionTypeChoice = form.getDefaultSubscriptionTypeChoice();
            boolean allowNullChoice = Boolean.parseBoolean(form.getAllowNullChoice());
            
            result.setSubscriptionTypeChoices(subscriptionControl.getSubscriptionTypeChoices(defaultSubscriptionTypeChoice,
                    getPreferredLanguage(), allowNullChoice, subscriptionKind));
        } else {
            addExecutionError(ExecutionErrors.UnknownSubscriptionKindName.name(), subscriptionKindName);
        }
        
        return result;
    }
    
}
