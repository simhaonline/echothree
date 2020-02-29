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

package com.echothree.model.control.printer.common.transfer;

import com.echothree.model.control.document.common.transfer.DocumentTransfer;
import com.echothree.model.control.workflow.common.transfer.WorkflowEntityStatusTransfer;
import com.echothree.util.common.transfer.BaseTransfer;

public class PrinterGroupJobTransfer
        extends BaseTransfer {
    
    private String printerGroupJobName;
    private PrinterGroupTransfer printerGroup;
    private DocumentTransfer document;
    private Integer copies;
    private Integer priority;
    private WorkflowEntityStatusTransfer printerGroupJobStatus;
    
    /** Creates a new instance of PrinterGroupJobTransfer */
    public PrinterGroupJobTransfer(String printerGroupJobName, PrinterGroupTransfer printerGroup, DocumentTransfer document, Integer copies, Integer priority,
            WorkflowEntityStatusTransfer printerGroupJobStatus) {
        this.printerGroupJobName = printerGroupJobName;
        this.printerGroup = printerGroup;
        this.document = document;
        this.copies = copies;
        this.priority = priority;
        this.printerGroupJobStatus = printerGroupJobStatus;
    }

    /**
     * @return the printerGroupJobName
     */
    public String getPrinterGroupJobName() {
        return printerGroupJobName;
    }

    /**
     * @param printerGroupJobName the printerGroupJobName to set
     */
    public void setPrinterGroupJobName(String printerGroupJobName) {
        this.printerGroupJobName = printerGroupJobName;
    }

    /**
     * @return the printerGroup
     */
    public PrinterGroupTransfer getPrinterGroup() {
        return printerGroup;
    }

    /**
     * @param printerGroup the printerGroup to set
     */
    public void setPrinterGroup(PrinterGroupTransfer printerGroup) {
        this.printerGroup = printerGroup;
    }

    /**
     * @return the document
     */
    public DocumentTransfer getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(DocumentTransfer document) {
        this.document = document;
    }

    /**
     * @return the copies
     */
    public Integer getCopies() {
        return copies;
    }

    /**
     * @param copies the copies to set
     */
    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    /**
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return the printerGroupJobStatus
     */
    public WorkflowEntityStatusTransfer getPrinterGroupJobStatus() {
        return printerGroupJobStatus;
    }

    /**
     * @param printerGroupJobStatus the printerGroupJobStatus to set
     */
    public void setPrinterGroupJobStatus(WorkflowEntityStatusTransfer printerGroupJobStatus) {
        this.printerGroupJobStatus = printerGroupJobStatus;
    }

}
