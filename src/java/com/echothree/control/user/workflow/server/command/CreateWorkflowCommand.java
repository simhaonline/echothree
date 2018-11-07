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

package com.echothree.control.user.workflow.server.command;

import com.echothree.control.user.workflow.common.form.CreateWorkflowForm;
import com.echothree.model.control.security.server.SecurityControl;
import com.echothree.model.control.selector.server.SelectorControl;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.security.server.entity.SecurityRoleGroup;
import com.echothree.model.data.selector.server.entity.SelectorKind;
import com.echothree.model.data.selector.server.entity.SelectorType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.workflow.server.entity.Workflow;
import com.echothree.model.data.workflow.server.entity.WorkflowType;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateWorkflowCommand
        extends BaseSimpleCommand<CreateWorkflowForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("WorkflowName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("WorkflowTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("SelectorKindName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("SelectorTypeName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("SecurityRoleGroupName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of CreateWorkflowCommand */
    public CreateWorkflowCommand(UserVisitPK userVisitPK, CreateWorkflowForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        WorkflowControl workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
        String workflowName = form.getWorkflowName();
        Workflow workflow = workflowControl.getWorkflowByName(workflowName);
        
        if(workflow == null) {
            String workflowTypeName = form.getWorkflowTypeName();
            WorkflowType workflowType = workflowControl.getWorkflowTypeByName(workflowTypeName);
            
            if(workflowType != null) {
                SelectorControl selectorControl = (SelectorControl)Session.getModelController(SelectorControl.class);
                String selectorKindName = form.getSelectorKindName();
                String selectorTypeName = form.getSelectorTypeName();
                int parameterCount = (selectorKindName == null? 0: 1) + (selectorTypeName == null? 0: 1);
                
                if(parameterCount == 0 || parameterCount == 2) {
                    SelectorKind selectorKind = selectorKindName == null? null: selectorControl.getSelectorKindByName(selectorKindName);
                    
                    if(selectorKindName == null || selectorKind != null) {
                        SelectorType selectorType = selectorTypeName == null? null: selectorControl.getSelectorTypeByName(selectorKind, selectorTypeName);
                        
                        if(selectorTypeName == null || selectorType != null) {
                            SecurityControl securityControl = (SecurityControl)Session.getModelController(SecurityControl.class);
                            String securityRoleGroupName = form.getSecurityRoleGroupName();
                            SecurityRoleGroup securityRoleGroup = securityRoleGroupName == null? null: securityControl.getSecurityRoleGroupByName(securityRoleGroupName);
                            
                            if(securityRoleGroupName == null || securityRoleGroup != null) {
                                PartyPK partyPK = getPartyPK();
                                Integer sortOrder = Integer.valueOf(form.getSortOrder());
                                String description = form.getDescription();

                                workflow = workflowControl.createWorkflow(workflowName, workflowType, selectorType, securityRoleGroup, sortOrder, partyPK);

                                if(description != null) {
                                    workflowControl.createWorkflowDescription(workflow, getPreferredLanguage(), description, partyPK);
                                }
                            } else {
                                addExecutionError(ExecutionErrors.UnknownSecurityRoleGroupName.name(), securityRoleGroupName);
                            }
                        } else {
                            addExecutionError(ExecutionErrors.UnknownSelectorTypeName.name(), selectorKindName, selectorTypeName);
                        }
                    } else {
                        addExecutionError(ExecutionErrors.UnknownSelectorKindName.name(), selectorKindName);
                    }
                } else {
                    addExecutionError(ExecutionErrors.InvalidParameterCount.name());
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownWorkflowTypeName.name(), workflowTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownWorkflowName.name(), workflowName);
        }
        
        return null;
    }
    
}
