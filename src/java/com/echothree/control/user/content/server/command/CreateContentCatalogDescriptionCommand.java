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

package com.echothree.control.user.content.server.command;

import com.echothree.control.user.content.common.form.CreateContentCatalogDescriptionForm;
import com.echothree.model.control.content.server.ContentControl;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.content.server.entity.ContentCatalog;
import com.echothree.model.data.content.server.entity.ContentCatalogDescription;
import com.echothree.model.data.content.server.entity.ContentCollection;
import com.echothree.model.data.party.server.entity.Language;
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

public class CreateContentCatalogDescriptionCommand
        extends BaseSimpleCommand<CreateContentCatalogDescriptionForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.ContentCatalog.name(), SecurityRoles.Description.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ContentCollectionName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("ContentCatalogName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, true, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of CreateContentCatalogDescriptionCommand */
    public CreateContentCatalogDescriptionCommand(UserVisitPK userVisitPK, CreateContentCatalogDescriptionForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var contentControl = (ContentControl)Session.getModelController(ContentControl.class);
        String contentCollectionName = form.getContentCollectionName();
        ContentCollection contentCollection = contentControl.getContentCollectionByName(contentCollectionName);
        
        if(contentCollection != null) {
            String contentCatalogName = form.getContentCatalogName();
            ContentCatalog contentCatalog = contentControl.getContentCatalogByName(contentCollection, contentCatalogName);
            
            if(contentCatalog != null) {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                String languageIsoName = form.getLanguageIsoName();
                Language language = partyControl.getLanguageByIsoName(languageIsoName);
                
                if(language != null) {
                    ContentCatalogDescription contentCatalogDescription = contentControl.getContentCatalogDescription(contentCatalog, language);
                    
                    if(contentCatalogDescription == null) {
                        String description = form.getDescription();
                        
                        contentControl.createContentCatalogDescription(contentCatalog, language, description, getPartyPK());
                    } else {
                        addExecutionError(ExecutionErrors.DuplicateContentCatalogDescription.name());
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnknownLanguageIsoName.name(), languageIsoName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownContentCatalogName.name(),
                        contentCollection.getLastDetail().getContentCollectionName(), contentCatalogName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownContentCollectionName.name(), contentCollectionName);
        }
        
        return null;
    }
    
}
