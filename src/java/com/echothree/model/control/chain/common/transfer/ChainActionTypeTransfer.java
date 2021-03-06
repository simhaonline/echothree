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

package com.echothree.model.control.chain.common.transfer;

import com.echothree.util.common.transfer.BaseTransfer;

public class ChainActionTypeTransfer
        extends BaseTransfer {
    
    private String chainActionTypeName;
    private Boolean allowMultiple;
    private Boolean isDefault;
    private Integer sortOrder;
    private String description;
    
    /** Creates a new instance of ChainActionTypeTransfer */
    public ChainActionTypeTransfer(String chainActionTypeName, Boolean allowMultiple, Boolean isDefault, Integer sortOrder,
            String description) {
        this.chainActionTypeName = chainActionTypeName;
        this.allowMultiple = allowMultiple;
        this.isDefault = isDefault;
        this.sortOrder = sortOrder;
        this.description = description;
    }

    /**
     * @return the chainActionTypeName
     */
    public String getChainActionTypeName() {
        return chainActionTypeName;
    }

    /**
     * @param chainActionTypeName the chainActionTypeName to set
     */
    public void setChainActionTypeName(String chainActionTypeName) {
        this.chainActionTypeName = chainActionTypeName;
    }

    /**
     * @return the allowMultiple
     */
    public Boolean getAllowMultiple() {
        return allowMultiple;
    }

    /**
     * @param allowMultiple the allowMultiple to set
     */
    public void setAllowMultiple(Boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
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
