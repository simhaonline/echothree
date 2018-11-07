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

package com.echothree.control.user.tag.server.command;

import com.echothree.control.user.tag.common.form.CreateTagScopeForm;
import com.echothree.control.user.tag.common.result.CreateTagScopeResult;
import com.echothree.control.user.tag.common.result.TagResultFactory;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.control.sequence.common.SequenceConstants;
import com.echothree.model.control.sequence.server.SequenceControl;
import com.echothree.model.control.tag.server.TagControl;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.sequence.server.entity.Sequence;
import com.echothree.model.data.tag.server.entity.TagScope;
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

public class CreateTagScopeCommand
        extends BaseSimpleCommand<CreateTagScopeForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.TagScope.name(), SecurityRoles.Create.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("TagScopeName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of CreateTagScopeCommand */
    public CreateTagScopeCommand(UserVisitPK userVisitPK, CreateTagScopeForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        CreateTagScopeResult result = TagResultFactory.getCreateTagScopeResult();
        TagControl tagControl = (TagControl)Session.getModelController(TagControl.class);
        String tagScopeName = form.getTagScopeName();
        
        if(tagScopeName == null) {
            SequenceControl sequenceControl = (SequenceControl)Session.getModelController(SequenceControl.class);
            Sequence sequence = sequenceControl.getDefaultSequenceUsingNames(SequenceConstants.SequenceType_TAG_SCOPE);
            
            tagScopeName = sequenceControl.getNextSequenceValue(sequence);
        }
        
        TagScope tagScope = tagControl.getTagScopeByName(tagScopeName);
        
        if(tagScope == null) {
            PartyPK createdBy = getPartyPK();
            Boolean isDefault = Boolean.valueOf(form.getIsDefault());
            Integer sortOrder = Integer.valueOf(form.getSortOrder());
            String description = form.getDescription();
            
            tagScope = tagControl.createTagScope(tagScopeName, isDefault, sortOrder, createdBy);
            
            if(description != null) {
                tagControl.createTagScopeDescription(tagScope, getPreferredLanguage(), description, createdBy);
            }
        } else {
            addExecutionError(ExecutionErrors.DuplicateTagScopeName.name(), tagScopeName);
        }
                
        if(tagScope != null) {
            result.setTagScopeName(tagScope.getLastDetail().getTagScopeName());
            result.setEntityRef(tagScope.getPrimaryKey().getEntityRef());
        }
        
        return result;
    }
    
}
