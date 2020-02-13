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

package com.echothree.model.control.workrequirement.server.logic;

import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.sequence.server.SequenceControl;
import com.echothree.model.control.sequence.common.SequenceTypes;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.control.workflow.server.logic.WorkflowDestinationLogic;
import com.echothree.model.control.workrequirement.common.workflow.WorkAssignmentStatusConstants;
import com.echothree.model.control.workrequirement.common.workflow.WorkRequirementStatusConstants;
import com.echothree.model.control.workrequirement.common.workflow.WorkTimeStatusConstants;
import com.echothree.model.control.workrequirement.server.WorkRequirementControl;
import com.echothree.model.data.core.server.entity.EntityInstance;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.sequence.server.entity.Sequence;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.model.data.workeffort.server.entity.WorkEffort;
import com.echothree.model.data.workeffort.server.entity.WorkEffortScope;
import com.echothree.model.data.workeffort.server.entity.WorkEffortType;
import com.echothree.model.data.workflow.server.entity.WorkflowDestination;
import com.echothree.model.data.workflow.server.entity.WorkflowEntityStatus;
import com.echothree.model.data.workrequirement.server.entity.WorkAssignment;
import com.echothree.model.data.workrequirement.server.entity.WorkRequirement;
import com.echothree.model.data.workrequirement.server.entity.WorkRequirementScope;
import com.echothree.model.data.workrequirement.server.entity.WorkRequirementScopeDetail;
import com.echothree.model.data.workrequirement.server.entity.WorkRequirementStatus;
import com.echothree.model.data.workrequirement.server.entity.WorkRequirementType;
import com.echothree.model.data.workrequirement.server.entity.WorkRequirementTypeDetail;
import com.echothree.model.data.workrequirement.server.entity.WorkTime;
import com.echothree.model.data.workrequirement.server.entity.WorkTimeUserVisit;
import com.echothree.model.data.workrequirement.server.value.WorkTimeDetailValue;
import com.echothree.util.common.persistence.BasePK;
import com.echothree.util.server.persistence.Session;
import java.util.List;

public class WorkRequirementLogic {
    
    private WorkRequirementLogic() {
        super();
    }
    
    private static class WorkRequirementLogicHolder {
        static WorkRequirementLogic instance = new WorkRequirementLogic();
    }
    
    public static WorkRequirementLogic getInstance() {
        return WorkRequirementLogicHolder.instance;
    }

    public WorkRequirement createWorkRequirementUsingNames(final Session session, final WorkEffort workEffort, final String workRequirementTypeName,
            final Party assignedParty, final Long assignedEndTime, final Long requiredTime, final BasePK createdBy) {
        var workRequirementControl = (WorkRequirementControl)Session.getModelController(WorkRequirementControl.class);
        WorkEffortScope workEffortScope = workEffort.getLastDetail().getWorkEffortScope();
        WorkEffortType workEffortType = workEffortScope.getLastDetail().getWorkEffortType();
        WorkRequirementType workRequirementType = workRequirementControl.getWorkRequirementTypeByName(workEffortType, workRequirementTypeName);
        WorkRequirementScope workRequirementScope = workRequirementControl.getWorkRequirementScope(workEffortScope, workRequirementType);

        return createWorkRequirement(session, workEffort, workRequirementScope, assignedParty, assignedEndTime, requiredTime, createdBy);
    }

    public WorkRequirement createWorkRequirement(final Session session, final WorkEffort workEffort, final WorkRequirementScope workRequirementScope,
            final Party assignedParty, final Long assignedEndTime, Long requiredTime, final BasePK createdBy) {
        var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
        var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
        var workRequirementControl = (WorkRequirementControl)Session.getModelController(WorkRequirementControl.class);
        var sequenceControl = (SequenceControl)Session.getModelController(SequenceControl.class);
        WorkRequirementScopeDetail workRequirementScopeDetail = workRequirementScope.getLastDetail();
        WorkRequirementTypeDetail workRequirementTypeDetail = workRequirementScopeDetail.getWorkRequirementType().getLastDetail();
        Sequence workRequirementSequence = workRequirementScopeDetail.getWorkRequirementSequence();

        if(workRequirementSequence == null) {
            workRequirementSequence = workRequirementTypeDetail.getWorkRequirementSequence();

            if(workRequirementSequence == null) {
                workRequirementSequence = sequenceControl.getDefaultSequenceUsingNames(SequenceTypes.WORK_REQUIREMENT.toString());
            }
        }

        String workRequirementName = sequenceControl.getNextSequenceValue(workRequirementSequence);
        Long startTime = session.START_TIME_LONG;
        Long estimatedTimeAllowed = workRequirementScopeDetail.getEstimatedTimeAllowed();

        if(estimatedTimeAllowed == null) {
            estimatedTimeAllowed = workRequirementTypeDetail.getEstimatedTimeAllowed();
        }

        // If a requiredTime wasn't supplied, and there's an estimatedTimeAllowed available, then take the current
        // time + the esimtatedTimeAllowed and use that as the requiredTime.
        if(requiredTime == null && estimatedTimeAllowed != null) {
            requiredTime = session.START_TIME + estimatedTimeAllowed;
        }

        WorkRequirement workRequirement = workRequirementControl.createWorkRequirement(workRequirementName, workEffort, workRequirementScope, startTime,
                requiredTime, createdBy);
        EntityInstance entityInstance = coreControl.getEntityInstanceByBasePK(workRequirement.getPrimaryKey());

        // TODO: should requiredTime map into triggerTime?
        workflowControl.addEntityToWorkflowUsingNames(null, WorkRequirementStatusConstants.Workflow_WORK_REQUIREMENT_STATUS,
                assignedParty == null ? WorkRequirementStatusConstants.WorkflowEntrance_NEW_UNASSIGNED : WorkRequirementStatusConstants.WorkflowEntrance_NEW_ASSIGNED,
                entityInstance, null, null, createdBy);

        if(assignedParty != null) {
            // If an assignedParty is specified, then create a WorkAssignment and do not give that Party a choice on acceptance.
            createWorkAssignment(workRequirement, assignedParty, session.START_TIME_LONG, assignedEndTime,
                    WorkAssignmentStatusConstants.WorkflowEntrance_NEW_ACCEPTED, createdBy);
        }

        return workRequirement;
    }

    public WorkAssignment createWorkAssignment(final WorkRequirement workRequirement, final Party party, final Long startTime, final Long endTime,
            final String workflowEntranceName, final BasePK createdBy) {
        var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
        var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
        var workRequirementControl = (WorkRequirementControl)Session.getModelController(WorkRequirementControl.class);
        WorkAssignment workAssignment = workRequirementControl.createWorkAssignment(workRequirement, party, startTime, endTime, createdBy);
        WorkRequirementStatus workRequirementStatus = workRequirementControl.getWorkRequirementStatusForUpdate(workRequirement);
        EntityInstance entityInstance = coreControl.getEntityInstanceByBasePK(workAssignment.getPrimaryKey());

        workRequirementStatus.setLastWorkAssignment(workAssignment);

        // TODO: endTime should be used as a triggerTime for the Workflow, and dump it into a difference status. Only if the workflowEntranceName
        // isn't 'NEW_ACCEPTED' perhaps? This could trigger automatic reassignment if it hasn't been forced to a particular party.
        workflowControl.addEntityToWorkflowUsingNames(null, WorkAssignmentStatusConstants.Workflow_WORK_ASSIGNMENT_STATUS, workflowEntranceName,
                entityInstance, null, null, createdBy);

        return workAssignment;
    }

    public WorkTime createWorkTime(final UserVisit userVisit, final WorkRequirement workRequirement, final Party party, final Long startTime,
            final Long endTime, final boolean complete, final BasePK createdBy) {
        var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
        var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
        var workRequirementControl = (WorkRequirementControl)Session.getModelController(WorkRequirementControl.class);
        WorkTime workTime = workRequirementControl.createWorkTime(workRequirement, party, startTime, endTime, createdBy);
        WorkRequirementStatus workRequirementStatus = workRequirementControl.getWorkRequirementStatusForUpdate(workRequirement);
        EntityInstance entityInstance = coreControl.getEntityInstanceByBasePK(workTime.getPrimaryKey());

        workRequirementStatus.setLastWorkTime(workTime);

        workflowControl.addEntityToWorkflowUsingNames(null, WorkTimeStatusConstants.Workflow_WORK_TIME_STATUS,
                endTime == null ? WorkTimeStatusConstants.WorkflowEntrance_NEW_IN_PROGRESS : complete ? WorkTimeStatusConstants.WorkflowEntrance_NEW_COMPLETE : WorkTimeStatusConstants.WorkflowEntrance_NEW_INCOMPLETE,
                entityInstance, null, null, createdBy);

        // If there's a UserVisit and it is "IN_PROGRESS," then set it up so that it'll be ended as "INCOMPLETE" if the
        // UserVisit is abandoned.
        if(userVisit != null && endTime != null) {
            workRequirementControl.createWorkTimeUserVisit(workTime, userVisit);
        }
        
        return workTime;
    }
    
    public void endWorkTime(final WorkTime workTime, final Long endTime, final boolean complete, final PartyPK endedBy) {
        var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
        var workRequirementControl = (WorkRequirementControl)Session.getModelController(WorkRequirementControl.class);
        var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
        EntityInstance entityInstance = coreControl.getEntityInstanceByBasePK(workTime.getPrimaryKey());
        WorkflowEntityStatus workflowEntityStatus = workflowControl.getWorkflowEntityStatusByEntityInstanceForUpdateUsingNames(WorkTimeStatusConstants.Workflow_WORK_TIME_STATUS, entityInstance);
        WorkflowDestination workflowDestination = WorkflowDestinationLogic.getInstance().getWorkflowDestinationByName(null, workflowEntityStatus.getWorkflowStep(),
                complete ? WorkTimeStatusConstants.WorkflowDestination_IN_PROGRESS_TO_COMPLETE : WorkTimeStatusConstants.WorkflowDestination_IN_PROGRESS_TO_INCOMPLETE);
        WorkTimeDetailValue workTimeDetailValue = workRequirementControl.getWorkTimeDetailValueForUpdate(workTime);
        
        workTimeDetailValue.setEndTime(endTime);        
        workRequirementControl.updateWorkTimeFromValue(workTimeDetailValue, endedBy);
        
        workflowControl.transitionEntityInWorkflow(null, workflowEntityStatus, workflowDestination, null, endedBy);
    }
    
    public void endWorkTimesByUserVisit(final UserVisit userVisit, final Long endTime, final PartyPK updatedBy) {
        var workRequirementControl = (WorkRequirementControl)Session.getModelController(WorkRequirementControl.class);
        List<WorkTimeUserVisit> workTimeUserVisits = workRequirementControl.getWorkTimeUserVisitsByUserVisitForUpdate(userVisit);
        
        workTimeUserVisits.stream().map((workTimeUserVisit) -> {
            endWorkTime(workTimeUserVisit.getWorkTime(), endTime == null ? null : userVisit.getLastCommandTime(), false, updatedBy);
            return workTimeUserVisit;            
        }).forEach((workTimeUserVisit) -> {
            workRequirementControl.deleteWorkTimeUserVisit(workTimeUserVisit);
        });
    }

}
