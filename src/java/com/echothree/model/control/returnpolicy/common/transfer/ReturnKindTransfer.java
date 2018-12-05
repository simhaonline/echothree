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

package com.echothree.model.control.returnpolicy.common.transfer;

import com.echothree.model.control.sequence.common.transfer.SequenceTypeTransfer;
import com.echothree.util.common.transfer.BaseTransfer;

public class ReturnKindTransfer
        extends BaseTransfer {
    
    private String returnKindName;
    private SequenceTypeTransfer returnSequenceType;
    private Boolean isDefault;
    private Integer sortOrder;
    private String description;
    
    /** Creates a new instance of ReturnKindTransfer */
    public ReturnKindTransfer(String returnKindName, SequenceTypeTransfer returnSequenceType, Boolean isDefault, Integer sortOrder, String description) {
        this.returnKindName = returnKindName;
        this.returnSequenceType = returnSequenceType;
        this.isDefault = isDefault;
        this.sortOrder = sortOrder;
        this.description = description;
    }

    /**
     * @return the returnKindName
     */
    public String getReturnKindName() {
        return returnKindName;
    }

    /**
     * @param returnKindName the returnKindName to set
     */
    public void setReturnKindName(String returnKindName) {
        this.returnKindName = returnKindName;
    }

    /**
     * @return the returnSequenceType
     */
    public SequenceTypeTransfer getReturnSequenceType() {
        return returnSequenceType;
    }

    /**
     * @param returnSequenceType the returnSequenceType to set
     */
    public void setReturnSequenceType(SequenceTypeTransfer returnSequenceType) {
        this.returnSequenceType = returnSequenceType;
    }

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault the isDefault to set
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder the sortOrder to set
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    
}