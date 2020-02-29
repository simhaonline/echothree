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

package com.echothree.model.control.batch.common.transfer;

import com.echothree.model.control.workflow.common.transfer.WorkflowEntityStatusTransfer;
import com.echothree.util.common.transfer.BaseTransfer;
import com.echothree.util.common.transfer.ListWrapper;

public abstract class GenericBatchTransfer
        extends BaseTransfer {
    
    private BatchTypeTransfer batchType;
    private String batchName;
    private WorkflowEntityStatusTransfer batchStatus;
    
    private ListWrapper<BatchAliasTransfer> batchAliases;
    private ListWrapper<BatchEntityTransfer> batchEntities;
    
    /** Creates a new instance of GenericBatchTransfer */
    public GenericBatchTransfer(BatchTypeTransfer batchType, String batchName, WorkflowEntityStatusTransfer batchStatus) {
        this.batchType = batchType;
        this.batchName = batchName;
        this.batchStatus = batchStatus;
    }

    /**
     * @return the batchType
     */
    public BatchTypeTransfer getBatchType() {
        return batchType;
    }

    /**
     * @param batchType the batchType to set
     */
    public void setBatchType(BatchTypeTransfer batchType) {
        this.batchType = batchType;
    }

    /**
     * @return the batchName
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * @param batchName the batchName to set
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    /**
     * @return the batchStatus
     */
    public WorkflowEntityStatusTransfer getBatchStatus() {
        return batchStatus;
    }

    /**
     * @param batchStatus the batchStatus to set
     */
    public void setBatchStatus(WorkflowEntityStatusTransfer batchStatus) {
        this.batchStatus = batchStatus;
    }

    /**
     * @return the batchAliases
     */
    public ListWrapper<BatchAliasTransfer> getBatchAliases() {
        return batchAliases;
    }

    /**
     * @param batchAliases the batchAliases to set
     */
    public void setBatchAliases(ListWrapper<BatchAliasTransfer> batchAliases) {
        this.batchAliases = batchAliases;
    }

    /**
     * @return the batchEntities
     */
    public ListWrapper<BatchEntityTransfer> getBatchEntities() {
        return batchEntities;
    }

    /**
     * @param batchEntities the batchEntities to set
     */
    public void setBatchEntities(ListWrapper<BatchEntityTransfer> batchEntities) {
        this.batchEntities = batchEntities;
    }

}
