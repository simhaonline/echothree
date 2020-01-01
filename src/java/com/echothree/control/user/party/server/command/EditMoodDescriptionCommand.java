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

import com.echothree.control.user.party.common.edit.MoodDescriptionEdit;
import com.echothree.control.user.party.common.edit.PartyEditFactory;
import com.echothree.control.user.party.common.form.EditMoodDescriptionForm;
import com.echothree.control.user.party.common.result.EditMoodDescriptionResult;
import com.echothree.control.user.party.common.result.PartyResultFactory;
import com.echothree.control.user.party.common.spec.MoodDescriptionSpec;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.party.server.entity.Mood;
import com.echothree.model.data.party.server.entity.MoodDescription;
import com.echothree.model.data.party.server.value.MoodDescriptionValue;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseEditCommand;
import com.echothree.util.server.persistence.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditMoodDescriptionCommand
        extends BaseEditCommand<MoodDescriptionSpec, MoodDescriptionEdit> {
    
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        List<FieldDefinition> temp = new ArrayList<>(2);
        temp.add(new FieldDefinition("MoodName", FieldType.ENTITY_NAME, true, null, null));
        temp.add(new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, true, null, null));
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(temp);
        
        temp = new ArrayList<>(1);
        temp.add(new FieldDefinition("Description", FieldType.STRING, true, 1L, 80L));
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(temp);
    }
    
    /** Creates a new instance of EditMoodDescriptionCommand */
    public EditMoodDescriptionCommand(UserVisitPK userVisitPK, EditMoodDescriptionForm form) {
        super(userVisitPK, form, null, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    protected BaseResult execute() {
        var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        EditMoodDescriptionResult result = PartyResultFactory.getEditMoodDescriptionResult();
        String moodName = spec.getMoodName();
        Mood mood = partyControl.getMoodByName(moodName);
        
        if(mood != null) {
            String languageIsoName = spec.getLanguageIsoName();
            Language language = partyControl.getLanguageByIsoName(languageIsoName);
            
            if(language != null) {
                if(editMode.equals(EditMode.LOCK)) {
                    MoodDescription moodDescription = partyControl.getMoodDescription(mood, language);
                    
                    if(moodDescription != null) {
                        result.setMoodDescription(partyControl.getMoodDescriptionTransfer(getUserVisit(), moodDescription));
                        
                        if(lockEntity(mood)) {
                            MoodDescriptionEdit edit = PartyEditFactory.getMoodDescriptionEdit();
                            
                            result.setEdit(edit);
                            edit.setDescription(moodDescription.getDescription());
                        } else {
                            addExecutionError(ExecutionErrors.EntityLockFailed.name());
                        }
                        
                        result.setEntityLock(getEntityLockTransfer(mood));
                    } else {
                        addExecutionError(ExecutionErrors.UnknownMoodDescription.name());
                    }
                } else if(editMode.equals(EditMode.UPDATE)) {
                    MoodDescriptionValue moodDescriptionValue = partyControl.getMoodDescriptionValueForUpdate(mood, language);
                    
                    if(moodDescriptionValue != null) {
                        if(lockEntityForUpdate(mood)) {
                            try {
                                String description = edit.getDescription();
                                
                                moodDescriptionValue.setDescription(description);
                                
                                partyControl.updateMoodDescriptionFromValue(moodDescriptionValue, getPartyPK());
                            } finally {
                                unlockEntity(mood);
                            }
                        } else {
                            addExecutionError(ExecutionErrors.EntityLockStale.name());
                        }
                    } else {
                        addExecutionError(ExecutionErrors.UnknownMoodDescription.name());
                    }
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownLanguageIsoName.name(), languageIsoName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownMoodName.name(), moodName);
        }
        
        return result;
    }
    
}
