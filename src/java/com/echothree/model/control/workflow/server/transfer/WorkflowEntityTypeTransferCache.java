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

package com.echothree.model.control.workflow.server.transfer;

import com.echothree.model.control.core.common.transfer.EntityTypeTransfer;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.workflow.common.transfer.WorkflowEntityTypeTransfer;
import com.echothree.model.control.workflow.common.transfer.WorkflowTransfer;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.model.data.workflow.server.entity.WorkflowEntityType;
import com.echothree.util.server.persistence.Session;

public class WorkflowEntityTypeTransferCache
        extends BaseWorkflowTransferCache<WorkflowEntityType, WorkflowEntityTypeTransfer> {
    
    /** Creates a new instance of WorkflowEntityTypeTransferCache */
    public WorkflowEntityTypeTransferCache(UserVisit userVisit, WorkflowControl workflowControl) {
        super(userVisit, workflowControl);
    }
    
    public WorkflowEntityTypeTransfer getWorkflowEntityTypeTransfer(WorkflowEntityType workflowEntityType) {
        WorkflowEntityTypeTransfer workflowEntityTypeTransfer = get(workflowEntityType);
        
        if(workflowEntityTypeTransfer == null) {
            CoreControl coreControl = (CoreControl)Session.getModelController(CoreControl.class);
            WorkflowTransfer workflow = workflowControl.getWorkflowTransfer(userVisit, workflowEntityType.getWorkflow());
            EntityTypeTransfer entityType = coreControl.getEntityTypeTransfer(userVisit, workflowEntityType.getEntityType());
            
            workflowEntityTypeTransfer = new WorkflowEntityTypeTransfer(workflow, entityType);
            put(workflowEntityType, workflowEntityTypeTransfer);
        }
        
        return workflowEntityTypeTransfer;
    }
    
}
