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

package com.echothree.model.control.core.common.transfer;

import com.echothree.util.common.transfer.BaseTransfer;

public class EntityLongRangeTransfer
        extends BaseTransfer {
    
    private EntityAttributeTransfer entityAttribute;
    private String entityLongRangeName;
    private Long minimumLongValue;
    private Long maximumLongValue;
    private Boolean isDefault;
    private Integer sortOrder;
    private String description;
    
    /** Creates a new instance of EntityLongRangeTransfer */
    public EntityLongRangeTransfer(EntityAttributeTransfer entityAttribute, String entityLongRangeName, Long minimumLongValue, Long maximumLongValue,
            Boolean isDefault, Integer sortOrder, String description) {
        this.entityAttribute = entityAttribute;
        this.entityLongRangeName = entityLongRangeName;
        this.minimumLongValue = minimumLongValue;
        this.maximumLongValue = maximumLongValue;
        this.isDefault = isDefault;
        this.sortOrder = sortOrder;
        this.description = description;
    }

    /**
     * @return the entityAttribute
     */
    public EntityAttributeTransfer getEntityAttribute() {
        return entityAttribute;
    }

    /**
     * @param entityAttribute the entityAttribute to set
     */
    public void setEntityAttribute(EntityAttributeTransfer entityAttribute) {
        this.entityAttribute = entityAttribute;
    }

    /**
     * @return the entityLongRangeName
     */
    public String getEntityLongRangeName() {
        return entityLongRangeName;
    }

    /**
     * @param entityLongRangeName the entityLongRangeName to set
     */
    public void setEntityLongRangeName(String entityLongRangeName) {
        this.entityLongRangeName = entityLongRangeName;
    }

    /**
     * @return the minimumLongValue
     */
    public Long getMinimumLongValue() {
        return minimumLongValue;
    }

    /**
     * @param minimumLongValue the minimumLongValue to set
     */
    public void setMinimumLongValue(Long minimumLongValue) {
        this.minimumLongValue = minimumLongValue;
    }

    /**
     * @return the maximumLongValue
     */
    public Long getMaximumLongValue() {
        return maximumLongValue;
    }

    /**
     * @param maximumLongValue the maximumLongValue to set
     */
    public void setMaximumLongValue(Long maximumLongValue) {
        this.maximumLongValue = maximumLongValue;
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