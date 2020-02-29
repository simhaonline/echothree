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

import com.echothree.control.user.subscription.common.edit.SubscriptionEditFactory;
import com.echothree.control.user.subscription.common.edit.SubscriptionTypeDescriptionEdit;
import com.echothree.control.user.subscription.common.form.EditSubscriptionTypeDescriptionForm;
import com.echothree.control.user.subscription.common.result.EditSubscriptionTypeDescriptionResult;
import com.echothree.control.user.subscription.common.result.SubscriptionResultFactory;
import com.echothree.control.user.subscription.common.spec.SubscriptionTypeDescriptionSpec;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.subscription.server.SubscriptionControl;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.subscription.server.entity.SubscriptionKind;
import com.echothree.model.data.subscription.server.entity.SubscriptionType;
import com.echothree.model.data.subscription.server.entity.SubscriptionTypeDescription;
import com.echothree.model.data.subscription.server.value.SubscriptionTypeDescriptionValue;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseEditCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditSubscriptionTypeDescriptionCommand
        extends BaseEditCommand<SubscriptionTypeDescriptionSpec, SubscriptionTypeDescriptionEdit> {
    
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("SubscriptionKindName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("SubscriptionTypeName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, true, null, null)
        ));
        
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("Description", FieldType.STRING, true, 1L, 80L)
        ));
    }
    
    /** Creates a new instance of EditSubscriptionTypeDescriptionCommand */
    public EditSubscriptionTypeDescriptionCommand(UserVisitPK userVisitPK, EditSubscriptionTypeDescriptionForm form) {
        super(userVisitPK, form, null, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    protected BaseResult execute() {
        var subscriptionControl = (SubscriptionControl)Session.getModelController(SubscriptionControl.class);
        EditSubscriptionTypeDescriptionResult result = SubscriptionResultFactory.getEditSubscriptionTypeDescriptionResult();
        String subscriptionKindName = spec.getSubscriptionKindName();
        SubscriptionKind subscriptionKind = subscriptionControl.getSubscriptionKindByName(subscriptionKindName);
        
        if(subscriptionKind != null) {
            String subscriptionTypeName = spec.getSubscriptionTypeName();
            SubscriptionType subscriptionType = subscriptionControl.getSubscriptionTypeByName(subscriptionKind, subscriptionTypeName);
            
            if(subscriptionType != null) {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                String languageIsoName = spec.getLanguageIsoName();
                Language language = partyControl.getLanguageByIsoName(languageIsoName);
                
                if(language != null) {
                    if(editMode.equals(EditMode.LOCK)) {
                        SubscriptionTypeDescription subscriptionTypeDescription = subscriptionControl.getSubscriptionTypeDescription(subscriptionType, language);
                        
                        if(subscriptionTypeDescription != null) {
                            result.setSubscriptionTypeDescription(subscriptionControl.getSubscriptionTypeDescriptionTransfer(getUserVisit(), subscriptionTypeDescription));
                            
                            if(lockEntity(subscriptionType)) {
                                SubscriptionTypeDescriptionEdit edit = SubscriptionEditFactory.getSubscriptionTypeDescriptionEdit();
                                
                                result.setEdit(edit);
                                edit.setDescription(subscriptionTypeDescription.getDescription());
                            } else {
                                addExecutionError(ExecutionErrors.EntityLockFailed.name());
                            }
                            
                            result.setEntityLock(getEntityLockTransfer(subscriptionType));
                        } else {
                            addExecutionError(ExecutionErrors.UnknownSubscriptionTypeDescription.name());
                        }
                    } else if(editMode.equals(EditMode.UPDATE)) {
                        SubscriptionTypeDescriptionValue subscriptionTypeDescriptionValue = subscriptionControl.getSubscriptionTypeDescriptionValueForUpdate(subscriptionType, language);
                        
                        if(subscriptionTypeDescriptionValue != null) {
                            if(lockEntityForUpdate(subscriptionType)) {
                                try {
                                    String description = edit.getDescription();
                                    
                                    subscriptionTypeDescriptionValue.setDescription(description);
                                    
                                    subscriptionControl.updateSubscriptionTypeDescriptionFromValue(subscriptionTypeDescriptionValue, getPartyPK());
                                } finally {
                                    unlockEntity(subscriptionType);
                                }
                            } else {
                                addExecutionError(ExecutionErrors.EntityLockStale.name());
                            }
                        } else {
                            addExecutionError(ExecutionErrors.UnknownSubscriptionTypeDescription.name());
                        }
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnknownLanguageIsoName.name(), languageIsoName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownSubscriptionTypeName.name(), subscriptionTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownSubscriptionKindName.name(), subscriptionKindName);
        }
        
        return result;
    }
    
}
