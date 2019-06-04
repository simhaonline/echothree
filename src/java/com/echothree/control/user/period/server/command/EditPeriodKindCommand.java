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

package com.echothree.control.user.period.server.command;

import com.echothree.control.user.period.common.edit.PeriodEditFactory;
import com.echothree.control.user.period.common.edit.PeriodKindEdit;
import com.echothree.control.user.period.common.form.EditPeriodKindForm;
import com.echothree.control.user.period.common.result.EditPeriodKindResult;
import com.echothree.control.user.period.common.result.PeriodResultFactory;
import com.echothree.control.user.period.common.spec.PeriodKindSpec;
import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.period.server.PeriodControl;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.period.server.entity.PeriodKind;
import com.echothree.model.data.period.server.entity.PeriodKindDescription;
import com.echothree.model.data.period.server.entity.PeriodKindDetail;
import com.echothree.model.data.period.server.value.PeriodKindDescriptionValue;
import com.echothree.model.data.period.server.value.PeriodKindDetailValue;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.workflow.server.entity.Workflow;
import com.echothree.model.data.workflow.server.entity.WorkflowEntrance;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseEditCommand;
import com.echothree.util.server.control.CommandSecurityDefinition;
import com.echothree.util.server.control.PartyTypeDefinition;
import com.echothree.util.server.control.SecurityRoleDefinition;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditPeriodKindCommand
        extends BaseEditCommand<PeriodKindSpec, PeriodKindEdit> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                    new SecurityRoleDefinition(SecurityRoleGroups.PeriodKind.name(), SecurityRoles.Edit.name())
                    )))
                )));
        
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("PeriodKindName", FieldType.ENTITY_NAME, true, null, null)
                ));
        
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("PeriodKindName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("WorkflowName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("WorkflowEntranceName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of EditPeriodKindCommand */
    public EditPeriodKindCommand(UserVisitPK userVisitPK, EditPeriodKindForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    protected BaseResult execute() {
        var periodControl = (PeriodControl)Session.getModelController(PeriodControl.class);
        EditPeriodKindResult result = PeriodResultFactory.getEditPeriodKindResult();
        
        if(editMode.equals(EditMode.LOCK) || editMode.equals(EditMode.ABANDON)) {
            String periodKindName = spec.getPeriodKindName();
            PeriodKind periodKind = periodControl.getPeriodKindByName(periodKindName);
            
            if(periodKind != null) {
                if(editMode.equals(EditMode.LOCK)) {
                    if(lockEntity(periodKind)) {
                        PeriodKindDescription periodKindDescription = periodControl.getPeriodKindDescription(periodKind, getPreferredLanguage());
                        PeriodKindEdit edit = PeriodEditFactory.getPeriodKindEdit();
                        PeriodKindDetail periodKindDetail = periodKind.getLastDetail();
                        WorkflowEntrance workflowEntrance = periodKindDetail.getWorkflowEntrance();
                        Workflow workflow = workflowEntrance == null? null: workflowEntrance.getLastDetail().getWorkflow();

                        result.setPeriodKind(periodControl.getPeriodKindTransfer(getUserVisit(), periodKind));
                        sendEventUsingNames(periodKind.getPrimaryKey(), EventTypes.READ.name(), null, null, getPartyPK());

                        result.setEdit(edit);
                        edit.setPeriodKindName(periodKindDetail.getPeriodKindName());
                        edit.setWorkflowName(workflow == null? null: workflow.getLastDetail().getWorkflowName());
                        edit.setWorkflowEntranceName(workflowEntrance == null? null: workflowEntrance.getLastDetail().getWorkflowEntranceName());
                        edit.setIsDefault(periodKindDetail.getIsDefault().toString());
                        edit.setSortOrder(periodKindDetail.getSortOrder().toString());

                        if(periodKindDescription != null) {
                            edit.setDescription(periodKindDescription.getDescription());
                        }
                    } else {
                        addExecutionError(ExecutionErrors.EntityLockFailed.name());
                    }

                    result.setEntityLock(getEntityLockTransfer(periodKind));
                } else { // EditMode.ABANDON
                    unlockEntity(periodKind);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownPeriodKindName.name(), periodKindName);
            }
        } else if(editMode.equals(EditMode.UPDATE)) {
            String periodKindName = spec.getPeriodKindName();
            PeriodKind periodKind = periodControl.getPeriodKindByNameForUpdate(periodKindName);

            if(periodKind != null) {
                periodKindName = edit.getPeriodKindName();
                PeriodKind duplicatePeriodKind = periodControl.getPeriodKindByName(periodKindName);

                if(duplicatePeriodKind == null || periodKind.equals(duplicatePeriodKind)) {
                    String workflowName = edit.getWorkflowName();
                    String workflowEntranceName = edit.getWorkflowEntranceName();
                    int parameterCount = (workflowName == null ? 0 : 1) + (workflowEntranceName == null ? 0 : 1);

                    if(parameterCount == 0 || parameterCount == 2) {
                        var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
                        Workflow workflow = workflowName == null ? null : workflowControl.getWorkflowByName(workflowName);

                        if(workflowName == null || workflow != null) {
                             WorkflowEntrance workflowEntrance = workflowEntranceName == null? null: workflowControl.getWorkflowEntranceByName(workflow, workflowEntranceName);

                            if(workflowEntranceName == null || workflowEntrance != null) {
                                if(lockEntityForUpdate(periodKind)) {
                                    try {
                                        PartyPK partyPK = getPartyPK();
                                        PeriodKindDetailValue periodKindDetailValue = periodControl.getPeriodKindDetailValueForUpdate(periodKind);
                                        PeriodKindDescription periodKindDescription = periodControl.getPeriodKindDescriptionForUpdate(periodKind, getPreferredLanguage());
                                        String description = edit.getDescription();

                                        periodKindDetailValue.setPeriodKindName(edit.getPeriodKindName());
                                        periodKindDetailValue.setWorkflowEntrancePK(workflowEntrance == null? null: workflowEntrance.getPrimaryKey());
                                        periodKindDetailValue.setIsDefault(Boolean.valueOf(edit.getIsDefault()));
                                        periodKindDetailValue.setSortOrder(Integer.valueOf(edit.getSortOrder()));

                                        periodControl.updatePeriodKindFromValue(periodKindDetailValue, partyPK);

                                        if(periodKindDescription == null && description != null) {
                                            periodControl.createPeriodKindDescription(periodKind, getPreferredLanguage(), description, partyPK);
                                        } else if(periodKindDescription != null && description == null) {
                                            periodControl.deletePeriodKindDescription(periodKindDescription, partyPK);
                                        } else if(periodKindDescription != null && description != null) {
                                            PeriodKindDescriptionValue periodKindDescriptionValue = periodControl.getPeriodKindDescriptionValue(periodKindDescription);

                                            periodKindDescriptionValue.setDescription(description);
                                            periodControl.updatePeriodKindDescriptionFromValue(periodKindDescriptionValue, partyPK);
                                        }
                                    } finally {
                                        unlockEntity(periodKind);
                                    }
                                } else {
                                    addExecutionError(ExecutionErrors.EntityLockStale.name());
                                }
                            } else {
                                addExecutionError(ExecutionErrors.UnknownWorkflowEntranceName.name(), workflowName, workflowEntranceName);
                            }
                        } else {
                            addExecutionError(ExecutionErrors.UnknownWorkflowName.name(), workflowName);
                        }
                    } else {
                        addExecutionError(ExecutionErrors.InvalidParameterCount.name());
                    }
                } else {
                    addExecutionError(ExecutionErrors.DuplicatePeriodKindName.name(), periodKindName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownPeriodKindName.name(), periodKindName);
            }
        }
        
        return result;
    }
    
}
