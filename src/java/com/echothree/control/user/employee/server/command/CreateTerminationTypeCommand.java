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

package com.echothree.control.user.employee.server.command;

import com.echothree.control.user.employee.common.form.CreateTerminationTypeForm;
import com.echothree.model.control.employee.server.EmployeeControl;
import com.echothree.model.data.employee.server.entity.TerminationType;
import com.echothree.model.data.party.common.pk.PartyPK;
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

public class CreateTerminationTypeCommand
        extends BaseSimpleCommand<CreateTerminationTypeForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("TerminationTypeName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
            new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null),
            new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
        ));
    }
    
    /** Creates a new instance of CreateTerminationTypeCommand */
    public CreateTerminationTypeCommand(UserVisitPK userVisitPK, CreateTerminationTypeForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        EmployeeControl employeeControl = (EmployeeControl)Session.getModelController(EmployeeControl.class);
        String terminationTypeName = form.getTerminationTypeName();
        TerminationType terminationType = employeeControl.getTerminationTypeByName(terminationTypeName);
        
        if(terminationType == null) {
            PartyPK createdBy = getPartyPK();
            Boolean isDefault = Boolean.valueOf(form.getIsDefault());
            Integer sortOrder = Integer.valueOf(form.getSortOrder());
            String description = form.getDescription();
            
            terminationType = employeeControl.createTerminationType(terminationTypeName, isDefault, sortOrder, createdBy);
            
            if(description != null) {
                employeeControl.createTerminationTypeDescription(terminationType, getPreferredLanguage(), description, createdBy);
            }
        } else {
            addExecutionError(ExecutionErrors.DuplicateTerminationTypeName.name(), terminationTypeName);
        }
        
        return null;
    }
    
}
