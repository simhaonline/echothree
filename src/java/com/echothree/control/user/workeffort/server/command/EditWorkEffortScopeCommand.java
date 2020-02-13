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

package com.echothree.control.user.workeffort.server.command;

import com.echothree.control.user.workeffort.common.edit.WorkEffortEditFactory;
import com.echothree.control.user.workeffort.common.edit.WorkEffortScopeEdit;
import com.echothree.control.user.workeffort.common.form.EditWorkEffortScopeForm;
import com.echothree.control.user.workeffort.common.result.EditWorkEffortScopeResult;
import com.echothree.control.user.workeffort.common.result.WorkEffortResultFactory;
import com.echothree.control.user.workeffort.common.spec.WorkEffortScopeSpec;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.control.sequence.common.SequenceTypes;
import com.echothree.model.control.sequence.server.SequenceControl;
import com.echothree.model.control.uom.common.UomConstants;
import com.echothree.model.control.uom.server.logic.UnitOfMeasureTypeLogic;
import com.echothree.model.control.workeffort.server.WorkEffortControl;
import com.echothree.model.data.core.server.entity.MimeType;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.sequence.server.entity.Sequence;
import com.echothree.model.data.sequence.server.entity.SequenceType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.workeffort.server.entity.WorkEffortScope;
import com.echothree.model.data.workeffort.server.entity.WorkEffortScopeDescription;
import com.echothree.model.data.workeffort.server.entity.WorkEffortScopeDetail;
import com.echothree.model.data.workeffort.server.entity.WorkEffortType;
import com.echothree.model.data.workeffort.server.value.WorkEffortScopeDescriptionValue;
import com.echothree.model.data.workeffort.server.value.WorkEffortScopeDetailValue;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseAbstractEditCommand;
import com.echothree.util.server.control.CommandSecurityDefinition;
import com.echothree.util.server.control.PartyTypeDefinition;
import com.echothree.util.server.control.SecurityRoleDefinition;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditWorkEffortScopeCommand
        extends BaseAbstractEditCommand<WorkEffortScopeSpec, WorkEffortScopeEdit, EditWorkEffortScopeResult, WorkEffortScope, WorkEffortScope> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.WorkEffortScope.name(), SecurityRoles.Edit.name())
                        )))
                )));

        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("WorkEffortTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("WorkEffortScopeName", FieldType.ENTITY_NAME, true, null, null)
                ));

        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("WorkEffortScopeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("WorkEffortSequenceName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("ScheduledTimeUnitOfMeasureTypeName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("ScheduledTime", FieldType.UNSIGNED_INTEGER, false, null, null),
                new FieldDefinition("EstimatedTimeAllowedUnitOfMeasureTypeName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("EstimatedTimeAllowed", FieldType.UNSIGNED_INTEGER, false, null, null),
                new FieldDefinition("MaximumTimeAllowedUnitOfMeasureTypeName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("MaximumTimeAllowed", FieldType.UNSIGNED_INTEGER, false, null, null),
                new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of EditWorkEffortScopeCommand */
    public EditWorkEffortScopeCommand(UserVisitPK userVisitPK, EditWorkEffortScopeForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    public EditWorkEffortScopeResult getResult() {
        return WorkEffortResultFactory.getEditWorkEffortScopeResult();
    }

    @Override
    public WorkEffortScopeEdit getEdit() {
        return WorkEffortEditFactory.getWorkEffortScopeEdit();
    }

    WorkEffortType workEffortType;

    @Override
    public WorkEffortScope getEntity(EditWorkEffortScopeResult result) {
        var workEffortControl = (WorkEffortControl)Session.getModelController(WorkEffortControl.class);
        WorkEffortScope workEffortScope = null;
        String workEffortTypeName = spec.getWorkEffortTypeName();

        workEffortType = workEffortControl.getWorkEffortTypeByName(workEffortTypeName);

        if(workEffortType != null) {
            String workEffortScopeName = spec.getWorkEffortScopeName();

            if(editMode.equals(EditMode.LOCK) || editMode.equals(EditMode.ABANDON)) {
                workEffortScope = workEffortControl.getWorkEffortScopeByName(workEffortType, workEffortScopeName);
            } else { // EditMode.UPDATE
                workEffortScope = workEffortControl.getWorkEffortScopeByNameForUpdate(workEffortType, workEffortScopeName);
            }

            if(workEffortScope != null) {
                result.setWorkEffortScope(workEffortControl.getWorkEffortScopeTransfer(getUserVisit(), workEffortScope));
            } else {
                addExecutionError(ExecutionErrors.UnknownWorkEffortScopeName.name(), workEffortType, workEffortScopeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownWorkEffortTypeName.name(), workEffortTypeName);
        }

        return workEffortScope;
    }

    @Override
    public WorkEffortScope getLockEntity(WorkEffortScope workEffortScope) {
        return workEffortScope;
    }

    @Override
    public void fillInResult(EditWorkEffortScopeResult result, WorkEffortScope workEffortScope) {
        var workEffortControl = (WorkEffortControl)Session.getModelController(WorkEffortControl.class);

        result.setWorkEffortScope(workEffortControl.getWorkEffortScopeTransfer(getUserVisit(), workEffortScope));
    }

    MimeType overviewMimeType;
    MimeType introductionMimeType;
    
    @Override
    public void doLock(WorkEffortScopeEdit edit, WorkEffortScope workEffortScope) {
        var workEffortControl = (WorkEffortControl)Session.getModelController(WorkEffortControl.class);
        UnitOfMeasureTypeLogic unitOfMeasureTypeLogic = UnitOfMeasureTypeLogic.getInstance();
        WorkEffortScopeDescription workEffortScopeDescription = workEffortControl.getWorkEffortScopeDescription(workEffortScope, getPreferredLanguage());
        WorkEffortScopeDetail workEffortScopeDetail = workEffortScope.getLastDetail();
        Sequence workEffortSequence = workEffortScopeDetail.getWorkEffortSequence();
        UnitOfMeasureTypeLogic.StringUnitOfMeasure stringUnitOfMeasure;

        edit.setWorkEffortScopeName(workEffortScopeDetail.getWorkEffortScopeName());
        edit.setWorkEffortSequenceName(workEffortSequence == null ? null : workEffortSequence.getLastDetail().getSequenceName());
        stringUnitOfMeasure = unitOfMeasureTypeLogic.unitOfMeasureToString(this, UomConstants.UnitOfMeasureKindUseType_TIME, workEffortScopeDetail.getScheduledTime());
        edit.setScheduledTimeUnitOfMeasureTypeName(stringUnitOfMeasure.getUnitOfMeasureTypeName());
        edit.setScheduledTime(stringUnitOfMeasure.getValue());
        stringUnitOfMeasure = unitOfMeasureTypeLogic.unitOfMeasureToString(this, UomConstants.UnitOfMeasureKindUseType_TIME, workEffortScopeDetail.getEstimatedTimeAllowed());
        edit.setEstimatedTimeAllowedUnitOfMeasureTypeName(stringUnitOfMeasure.getUnitOfMeasureTypeName());
        edit.setEstimatedTimeAllowed(stringUnitOfMeasure.getValue());
        stringUnitOfMeasure = unitOfMeasureTypeLogic.unitOfMeasureToString(this, UomConstants.UnitOfMeasureKindUseType_TIME, workEffortScopeDetail.getMaximumTimeAllowed());
        edit.setMaximumTimeAllowedUnitOfMeasureTypeName(stringUnitOfMeasure.getUnitOfMeasureTypeName());
        edit.setMaximumTimeAllowed(stringUnitOfMeasure.getValue());
        edit.setIsDefault(workEffortScopeDetail.getIsDefault().toString());
        edit.setSortOrder(workEffortScopeDetail.getSortOrder().toString());

        if(workEffortScopeDescription != null) {
            edit.setDescription(workEffortScopeDescription.getDescription());
        }
    }

    Sequence workEffortSequence;
    Long scheduledTime;
    Long estimatedTimeAllowed;
    Long maximumTimeAllowed;
    
    @Override
    public void canUpdate(WorkEffortScope workEffortScope) {
        var workEffortControl = (WorkEffortControl)Session.getModelController(WorkEffortControl.class);
        String workEffortScopeName = edit.getWorkEffortScopeName();
        WorkEffortScope duplicateWorkEffortScope = workEffortControl.getWorkEffortScopeByName(workEffortType, workEffortScopeName);

        if(duplicateWorkEffortScope != null && !workEffortScope.equals(duplicateWorkEffortScope)) {
            addExecutionError(ExecutionErrors.DuplicateWorkEffortScopeName.name(), workEffortScopeName);
        } else {
            String workEffortSequenceName = edit.getWorkEffortSequenceName();

            if(workEffortSequenceName != null) {
                var sequenceControl = (SequenceControl)Session.getModelController(SequenceControl.class);
                SequenceType sequenceType = sequenceControl.getSequenceTypeByName(SequenceTypes.WORK_EFFORT.toString());

                if(sequenceType != null) {
                    workEffortSequence = sequenceControl.getSequenceByName(sequenceType, workEffortSequenceName);
                } else {
                    addExecutionError(ExecutionErrors.UnknownSequenceTypeName.name(), SequenceTypes.WORK_EFFORT.toString());
                }
            }

            if(workEffortSequenceName == null || workEffortSequence != null) {
                UnitOfMeasureTypeLogic unitOfMeasureTypeLogic = UnitOfMeasureTypeLogic.getInstance();

                scheduledTime = unitOfMeasureTypeLogic.checkUnitOfMeasure(this, UomConstants.UnitOfMeasureKindUseType_TIME,
                        edit.getScheduledTime(), edit.getScheduledTimeUnitOfMeasureTypeName(),
                        null, ExecutionErrors.MissingRequiredScheduledTime.name(), null, ExecutionErrors.MissingRequiredScheduledTimeUnitOfMeasureTypeName.name(),
                        null, ExecutionErrors.UnknownScheduledTimeUnitOfMeasureTypeName.name());

                if(!hasExecutionErrors()) {
                    estimatedTimeAllowed = unitOfMeasureTypeLogic.checkUnitOfMeasure(this, UomConstants.UnitOfMeasureKindUseType_TIME,
                            edit.getEstimatedTimeAllowed(), edit.getEstimatedTimeAllowedUnitOfMeasureTypeName(),
                            null, ExecutionErrors.MissingRequiredEstimatedTimeAllowed.name(), null, ExecutionErrors.MissingRequiredEstimatedTimeAllowedUnitOfMeasureTypeName.name(),
                            null, ExecutionErrors.UnknownEstimatedTimeAllowedUnitOfMeasureTypeName.name());

                    if(!hasExecutionErrors()) {
                        maximumTimeAllowed = unitOfMeasureTypeLogic.checkUnitOfMeasure(this, UomConstants.UnitOfMeasureKindUseType_TIME,
                                edit.getMaximumTimeAllowed(), edit.getMaximumTimeAllowedUnitOfMeasureTypeName(),
                                null, ExecutionErrors.MissingRequiredMaximumTimeAllowed.name(), null, ExecutionErrors.MissingRequiredMaximumTimeAllowedUnitOfMeasureTypeName.name(),
                                null, ExecutionErrors.UnknownMaximumTimeAllowedUnitOfMeasureTypeName.name());
                    }
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownWorkEffortSequenceName.name(), workEffortSequenceName);
            }
        }
    }
    
    @Override
    public void doUpdate(WorkEffortScope workEffortScope) {
        var workEffortControl = (WorkEffortControl)Session.getModelController(WorkEffortControl.class);
        PartyPK partyPK = getPartyPK();
        WorkEffortScopeDetailValue workEffortScopeDetailValue = workEffortControl.getWorkEffortScopeDetailValueForUpdate(workEffortScope);
        WorkEffortScopeDescription workEffortScopeDescription = workEffortControl.getWorkEffortScopeDescriptionForUpdate(workEffortScope, getPreferredLanguage());
        String description = edit.getDescription();

        workEffortScopeDetailValue.setWorkEffortScopeName(edit.getWorkEffortScopeName());
        workEffortScopeDetailValue.setWorkEffortSequencePK(workEffortSequence == null ? null : workEffortSequence.getPrimaryKey());
        workEffortScopeDetailValue.setScheduledTime(scheduledTime);
        workEffortScopeDetailValue.setEstimatedTimeAllowed(estimatedTimeAllowed);
        workEffortScopeDetailValue.setMaximumTimeAllowed(maximumTimeAllowed);
        workEffortScopeDetailValue.setIsDefault(Boolean.valueOf(edit.getIsDefault()));
        workEffortScopeDetailValue.setSortOrder(Integer.valueOf(edit.getSortOrder()));

        workEffortControl.updateWorkEffortScopeFromValue(workEffortScopeDetailValue, partyPK);

        if(workEffortScopeDescription == null && description != null) {
            workEffortControl.createWorkEffortScopeDescription(workEffortScope, getPreferredLanguage(), description, partyPK);
        } else if(workEffortScopeDescription != null && description == null) {
            workEffortControl.deleteWorkEffortScopeDescription(workEffortScopeDescription, partyPK);
        } else if(workEffortScopeDescription != null && description != null) {
            WorkEffortScopeDescriptionValue workEffortScopeDescriptionValue = workEffortControl.getWorkEffortScopeDescriptionValue(workEffortScopeDescription);

            workEffortScopeDescriptionValue.setDescription(description);
            workEffortControl.updateWorkEffortScopeDescriptionFromValue(workEffortScopeDescriptionValue, partyPK);
        }
    }

}
