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

package com.echothree.control.user.document.server.command;

import com.echothree.control.user.document.common.form.CreateDocumentTypeUsageForm;
import com.echothree.model.control.document.server.DocumentControl;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.document.server.entity.DocumentType;
import com.echothree.model.data.document.server.entity.DocumentTypeUsage;
import com.echothree.model.data.document.server.entity.DocumentTypeUsageType;
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

public class CreateDocumentTypeUsageCommand
        extends BaseSimpleCommand<CreateDocumentTypeUsageForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.DocumentTypeUsage.name(), SecurityRoles.Create.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("DocumentTypeUsageTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("DocumentTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null),
                new FieldDefinition("MaximumInstances", FieldType.SIGNED_INTEGER, false, null, null)
                ));
    }
    
    /** Creates a new instance of CreateDocumentTypeUsageCommand */
    public CreateDocumentTypeUsageCommand(UserVisitPK userVisitPK, CreateDocumentTypeUsageForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var documentControl = (DocumentControl)Session.getModelController(DocumentControl.class);
        String documentTypeUsageTypeName = form.getDocumentTypeUsageTypeName();
        DocumentTypeUsageType documentTypeUsageType = documentControl.getDocumentTypeUsageTypeByName(documentTypeUsageTypeName);
        
        if(documentTypeUsageType != null) {
            String documentTypeName = form.getDocumentTypeName();
            DocumentType documentType = documentControl.getDocumentTypeByName(documentTypeName);
            
            if(documentType != null) {
                DocumentTypeUsage documentTypeUsage = documentControl.getDocumentTypeUsage(documentTypeUsageType, documentType);
                
                if(documentTypeUsage == null) {
                    Boolean isDefault = Boolean.valueOf(form.getIsDefault());
                    Integer sortOrder = Integer.valueOf(form.getSortOrder());
                    String strMaximumInstances = form.getMaximumInstances();
                    Integer maximumInstances = strMaximumInstances == null ? null : Integer.valueOf(strMaximumInstances);
                    
                    documentControl.createDocumentTypeUsage(documentTypeUsageType, documentType, isDefault, sortOrder, maximumInstances, getPartyPK());
                } else {
                    addExecutionError(ExecutionErrors.DuplicateDocumentTypeUsage.name(), documentTypeUsageTypeName, documentTypeName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownDocumentTypeName.name(), documentTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownDocumentTypeUsageTypeName.name(), documentTypeUsageTypeName);
        }
        
        return null;
    }
    
}
