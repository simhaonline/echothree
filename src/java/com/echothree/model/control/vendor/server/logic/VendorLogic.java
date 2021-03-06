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

package com.echothree.model.control.vendor.server.logic;

import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.party.common.PartyTypes;
import com.echothree.model.control.party.common.exception.UnknownPartyNameException;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.party.server.logic.PartyLogic;
import com.echothree.model.control.user.server.logic.UserKeyLogic;
import com.echothree.model.control.user.server.logic.UserSessionLogic;
import com.echothree.model.control.vendor.common.exception.CannotSpecifyVendorNameAndPartyNameException;
import com.echothree.model.control.vendor.common.exception.MustSpecifyVendorNameOrPartyNameException;
import com.echothree.model.control.vendor.common.exception.UnknownVendorNameException;
import com.echothree.model.control.vendor.common.exception.UnknownVendorStatusChoiceException;
import com.echothree.model.control.vendor.server.VendorControl;
import com.echothree.model.control.vendor.common.workflow.VendorStatusConstants;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.control.workflow.server.logic.WorkflowDestinationLogic;
import com.echothree.model.control.workflow.server.logic.WorkflowLogic;
import com.echothree.model.data.core.server.entity.EntityInstance;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.vendor.server.entity.Vendor;
import com.echothree.model.data.workflow.server.entity.Workflow;
import com.echothree.model.data.workflow.server.entity.WorkflowDestination;
import com.echothree.model.data.workflow.server.entity.WorkflowEntityStatus;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.server.control.BaseLogic;
import com.echothree.util.server.message.ExecutionErrorAccumulator;
import com.echothree.util.server.persistence.Session;
import java.util.Map;
import java.util.Set;

public class VendorLogic
        extends BaseLogic {
    
    private VendorLogic() {
        super();
    }
    
    private static class VendorLogicHolder {
        static VendorLogic instance = new VendorLogic();
    }
    
    public static VendorLogic getInstance() {
        return VendorLogicHolder.instance;
    }

    public Vendor getVendorByName(final ExecutionErrorAccumulator eea, final String vendorName, final String partyName) {
        int parameterCount = (vendorName == null? 0: 1) + (partyName == null? 0: 1);
        Vendor vendor = null;

        if(parameterCount == 1) {
            var vendorControl = (VendorControl)Session.getModelController(VendorControl.class);

            if(vendorName != null) {
                vendor = vendorControl.getVendorByName(vendorName);

                if(vendor == null) {
                    handleExecutionError(UnknownVendorNameException.class, eea, ExecutionErrors.UnknownVendorName.name(), vendorName);
                }
            } else {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                Party party = partyControl.getPartyByName(partyName);

                if(party != null) {
                    PartyLogic.getInstance().checkPartyType(eea, party, PartyTypes.VENDOR.name());

                    vendor = vendorControl.getVendor(party);
                } else {
                    handleExecutionError(UnknownPartyNameException.class, eea, ExecutionErrors.UnknownPartyName.name(), partyName);
                }
            }
        } else {
            if(parameterCount == 2) {
                handleExecutionError(CannotSpecifyVendorNameAndPartyNameException.class, eea, ExecutionErrors.CannotSpecifyVendorNameAndPartyName.name());
            } else {
                handleExecutionError(MustSpecifyVendorNameOrPartyNameException.class, eea, ExecutionErrors.MustSpecifyVendorNameOrPartyName.name());
            }
        }

        return vendor;
    }

    public void setVendorStatus(final Session session, ExecutionErrorAccumulator eea, Party party, String vendorStatusChoice, PartyPK modifiedBy) {
        var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
        var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
        Workflow workflow = WorkflowLogic.getInstance().getWorkflowByName(eea, VendorStatusConstants.Workflow_VENDOR_STATUS);
        EntityInstance entityInstance = coreControl.getEntityInstanceByBasePK(party.getPrimaryKey());
        WorkflowEntityStatus workflowEntityStatus = workflowControl.getWorkflowEntityStatusByEntityInstanceForUpdate(workflow, entityInstance);
        WorkflowDestination workflowDestination = vendorStatusChoice == null ? null : workflowControl.getWorkflowDestinationByName(workflowEntityStatus.getWorkflowStep(), vendorStatusChoice);

        if(workflowDestination != null || vendorStatusChoice == null) {
            var workflowDestinationLogic = WorkflowDestinationLogic.getInstance();
            String currentWorkflowStepName = workflowEntityStatus.getWorkflowStep().getLastDetail().getWorkflowStepName();
            Map<String, Set<String>> map = workflowDestinationLogic.getWorkflowDestinationsAsMap(workflowDestination);
            Long triggerTime = null;

            if(currentWorkflowStepName.equals(VendorStatusConstants.WorkflowStep_ACTIVE)) {
                if(workflowDestinationLogic.workflowDestinationMapContainsStep(map, VendorStatusConstants.Workflow_VENDOR_STATUS, VendorStatusConstants.WorkflowStep_INACTIVE)) {
                    UserKeyLogic.getInstance().clearUserKeysByParty(party);
                    UserSessionLogic.getInstance().deleteUserSessionsByParty(party);
                }
            } else if(currentWorkflowStepName.equals(VendorStatusConstants.WorkflowStep_INACTIVE)) {
                if(workflowDestinationLogic.workflowDestinationMapContainsStep(map, VendorStatusConstants.Workflow_VENDOR_STATUS, VendorStatusConstants.WorkflowStep_ACTIVE)) {
                    // Nothing at this time.
                }
            }

            if(eea == null || !eea.hasExecutionErrors()) {
                workflowControl.transitionEntityInWorkflow(eea, workflowEntityStatus, workflowDestination, triggerTime, modifiedBy);
            }
        } else {
            handleExecutionError(UnknownVendorStatusChoiceException.class, eea, ExecutionErrors.UnknownVendorStatusChoice.name(), vendorStatusChoice);
        }
    }

}
