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

package com.echothree.control.user.party.server.command;

import com.echothree.control.user.party.common.form.CreateLanguageForm;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.party.server.entity.Language;
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

public class CreateLanguageCommand
        extends BaseSimpleCommand<CreateLanguageForm> {

    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null)
                ));
    }
    
    /** Creates a new instance of CreateLanguageCommand */
    public CreateLanguageCommand(UserVisitPK userVisitPK, CreateLanguageForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        String languageIsoName = form.getLanguageIsoName();
        Language language = partyControl.getLanguageByIsoName(languageIsoName);

        if(language == null) {
            Boolean isDefault = Boolean.valueOf(form.getIsDefault());
            Integer sortOrder = Integer.valueOf(form.getSortOrder());
            
            partyControl.createLanguage(languageIsoName, isDefault, sortOrder, getPartyPK());
        } else {
            addExecutionError(ExecutionErrors.DuplicateLanguageIsoName.name(), languageIsoName);
        }

        return null;
    }
    
}
