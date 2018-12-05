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

package com.echothree.model.control.workflow.common.transfer;

import com.echothree.util.common.transfer.BaseTransfer;

public class WorkflowStepTypeTransfer
        extends BaseTransfer {
    
    private String workflowStepTypeName;
    private String description;
    
    /** Creates a new instance of WorkflowStepTypeTransfer */
    public WorkflowStepTypeTransfer(String workflowStepTypeName, String description) {
        this.workflowStepTypeName = workflowStepTypeName;
        this.description = description;
    }
    
    public String getWorkflowStepTypeName() {
        return workflowStepTypeName;
    }
    
    public void setWorkflowStepTypeName(String workflowStepTypeName) {
        this.workflowStepTypeName = workflowStepTypeName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
}