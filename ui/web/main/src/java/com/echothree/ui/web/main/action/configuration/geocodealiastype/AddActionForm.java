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

package com.echothree.ui.web.main.action.configuration.geocodealiastype;

import com.echothree.view.client.web.struts.BaseActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;

@SproutForm(name="GeoCodeAliasTypeAdd")
public class AddActionForm
        extends BaseActionForm {
    
    private String geoCodeTypeName;
    private String geoCodeAliasTypeName;
    private String validationPattern;
    private Boolean isRequired;
    private Boolean isDefault;
    private String sortOrder;
    private String description;
    
    /**
     * @return the geoCodeTypeName
     */
    public String getGeoCodeTypeName() {
        return geoCodeTypeName;
    }

    /**
     * @param geoCodeTypeName the geoCodeTypeName to set
     */
    public void setGeoCodeTypeName(String geoCodeTypeName) {
        this.geoCodeTypeName = geoCodeTypeName;
    }

    /**
     * @return the geoCodeAliasTypeName
     */
    public String getGeoCodeAliasTypeName() {
        return geoCodeAliasTypeName;
    }

    /**
     * @param geoCodeAliasTypeName the geoCodeAliasTypeName to set
     */
    public void setGeoCodeAliasTypeName(String geoCodeAliasTypeName) {
        this.geoCodeAliasTypeName = geoCodeAliasTypeName;
    }

    /**
     * @return the validationPattern
     */
    public String getValidationPattern() {
        return validationPattern;
    }

    /**
     * @param validationPattern the validationPattern to set
     */
    public void setValidationPattern(String validationPattern) {
        this.validationPattern = validationPattern;
    }

    /**
     * @return the isRequired
     */
    public Boolean getIsRequired() {
        return isRequired;
    }

    /**
     * @param isRequired the isRequired to set
     */
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
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
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder the sortOrder to set
     */
    public void setSortOrder(String sortOrder) {
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
    
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);

        isRequired = Boolean.FALSE;
        isDefault = Boolean.FALSE;
    }

}
