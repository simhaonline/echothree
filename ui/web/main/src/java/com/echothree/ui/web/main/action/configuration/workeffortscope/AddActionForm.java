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

package com.echothree.ui.web.main.action.configuration.workeffortscope;

import com.echothree.control.user.uom.common.UomUtil;
import com.echothree.control.user.uom.common.form.GetUnitOfMeasureTypeChoicesForm;
import com.echothree.control.user.uom.common.result.GetUnitOfMeasureTypeChoicesResult;
import com.echothree.model.control.uom.common.UomConstants;
import com.echothree.model.control.uom.common.choice.UnitOfMeasureTypeChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BaseActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="WorkEffortScopeAdd")
public class AddActionForm
        extends BaseActionForm {
    
    private UnitOfMeasureTypeChoicesBean scheduledTimeUnitOfMeasureTypeChoices;
    private UnitOfMeasureTypeChoicesBean estimatedTimeAllowedUnitOfMeasureTypeChoices;
    private UnitOfMeasureTypeChoicesBean maximumTimeAllowedUnitOfMeasureTypeChoices;

    private String workEffortTypeName;
    private String workEffortScopeName;
    private String scheduledTime;
    private String scheduledTimeUnitOfMeasureTypeChoice;
    private String estimatedTimeAllowed;
    private String estimatedTimeAllowedUnitOfMeasureTypeChoice;
    private String maximumTimeAllowed;
    private String maximumTimeAllowedUnitOfMeasureTypeChoice;
    private Boolean isDefault;
    private String sortOrder;
    private String description;
    
    private void setupScheduledTimeUnitOfMeasureTypeChoices() {
        if(scheduledTimeUnitOfMeasureTypeChoices == null) {
            try {
                GetUnitOfMeasureTypeChoicesForm form = UomUtil.getHome().getGetUnitOfMeasureTypeChoicesForm();

                form.setDefaultUnitOfMeasureTypeChoice(scheduledTimeUnitOfMeasureTypeChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());
                form.setUnitOfMeasureKindUseTypeName(UomConstants.UnitOfMeasureKindUseType_TIME);

                CommandResult commandResult = UomUtil.getHome().getUnitOfMeasureTypeChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetUnitOfMeasureTypeChoicesResult getUnitOfMeasureTypeChoicesResult = (GetUnitOfMeasureTypeChoicesResult)executionResult.getResult();
                scheduledTimeUnitOfMeasureTypeChoices = getUnitOfMeasureTypeChoicesResult.getUnitOfMeasureTypeChoices();

                if(scheduledTimeUnitOfMeasureTypeChoice == null) {
                    scheduledTimeUnitOfMeasureTypeChoice = scheduledTimeUnitOfMeasureTypeChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, unitOfMeasureTypeChoices remains null, no default
            }
        }
    }

    private void setupEstimatedTimeAllowedUnitOfMeasureTypeChoices() {
        if(estimatedTimeAllowedUnitOfMeasureTypeChoices == null) {
            try {
                GetUnitOfMeasureTypeChoicesForm form = UomUtil.getHome().getGetUnitOfMeasureTypeChoicesForm();

                form.setDefaultUnitOfMeasureTypeChoice(estimatedTimeAllowedUnitOfMeasureTypeChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());
                form.setUnitOfMeasureKindUseTypeName(UomConstants.UnitOfMeasureKindUseType_TIME);

                CommandResult commandResult = UomUtil.getHome().getUnitOfMeasureTypeChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetUnitOfMeasureTypeChoicesResult getUnitOfMeasureTypeChoicesResult = (GetUnitOfMeasureTypeChoicesResult)executionResult.getResult();
                estimatedTimeAllowedUnitOfMeasureTypeChoices = getUnitOfMeasureTypeChoicesResult.getUnitOfMeasureTypeChoices();

                if(estimatedTimeAllowedUnitOfMeasureTypeChoice == null) {
                    estimatedTimeAllowedUnitOfMeasureTypeChoice = estimatedTimeAllowedUnitOfMeasureTypeChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, unitOfMeasureTypeChoices remains null, no default
            }
        }
    }

    private void setupMaximumTimeAllowedUnitOfMeasureTypeChoices() {
        if(maximumTimeAllowedUnitOfMeasureTypeChoices == null) {
            try {
                GetUnitOfMeasureTypeChoicesForm form = UomUtil.getHome().getGetUnitOfMeasureTypeChoicesForm();

                form.setDefaultUnitOfMeasureTypeChoice(maximumTimeAllowedUnitOfMeasureTypeChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());
                form.setUnitOfMeasureKindUseTypeName(UomConstants.UnitOfMeasureKindUseType_TIME);

                CommandResult commandResult = UomUtil.getHome().getUnitOfMeasureTypeChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetUnitOfMeasureTypeChoicesResult getUnitOfMeasureTypeChoicesResult = (GetUnitOfMeasureTypeChoicesResult)executionResult.getResult();
                maximumTimeAllowedUnitOfMeasureTypeChoices = getUnitOfMeasureTypeChoicesResult.getUnitOfMeasureTypeChoices();

                if(maximumTimeAllowedUnitOfMeasureTypeChoice == null) {
                    maximumTimeAllowedUnitOfMeasureTypeChoice = maximumTimeAllowedUnitOfMeasureTypeChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, unitOfMeasureTypeChoices remains null, no default
            }
        }
    }

    public void setWorkEffortTypeName(String workEffortTypeName) {
        this.workEffortTypeName = workEffortTypeName;
    }

    public String getWorkEffortTypeName() {
        return workEffortTypeName;
    }

    public void setWorkEffortScopeName(String workEffortScopeName) {
        this.workEffortScopeName = workEffortScopeName;
    }

    public String getWorkEffortScopeName() {
        return workEffortScopeName;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public List<LabelValueBean> getScheduledTimeUnitOfMeasureTypeChoices() {
        List<LabelValueBean> choices = null;

        setupScheduledTimeUnitOfMeasureTypeChoices();
        if(scheduledTimeUnitOfMeasureTypeChoices != null) {
            choices = convertChoices(scheduledTimeUnitOfMeasureTypeChoices);
        }

        return choices;
    }

    public String getScheduledTimeUnitOfMeasureTypeChoice() {
        setupScheduledTimeUnitOfMeasureTypeChoices();
        return scheduledTimeUnitOfMeasureTypeChoice;
    }

    public void setScheduledTimeUnitOfMeasureTypeChoice(String scheduledTimeUnitOfMeasureTypeChoice) {
        this.scheduledTimeUnitOfMeasureTypeChoice = scheduledTimeUnitOfMeasureTypeChoice;
    }

    public String getEstimatedTimeAllowed() {
        return estimatedTimeAllowed;
    }

    public void setEstimatedTimeAllowed(String estimatedTimeAllowed) {
        this.estimatedTimeAllowed = estimatedTimeAllowed;
    }

    public List<LabelValueBean> getEstimatedTimeAllowedUnitOfMeasureTypeChoices() {
        List<LabelValueBean> choices = null;

        setupEstimatedTimeAllowedUnitOfMeasureTypeChoices();
        if(estimatedTimeAllowedUnitOfMeasureTypeChoices != null) {
            choices = convertChoices(estimatedTimeAllowedUnitOfMeasureTypeChoices);
        }

        return choices;
    }

    public String getEstimatedTimeAllowedUnitOfMeasureTypeChoice() {
        setupEstimatedTimeAllowedUnitOfMeasureTypeChoices();
        return estimatedTimeAllowedUnitOfMeasureTypeChoice;
    }

    public void setEstimatedTimeAllowedUnitOfMeasureTypeChoice(String estimatedTimeAllowedUnitOfMeasureTypeChoice) {
        this.estimatedTimeAllowedUnitOfMeasureTypeChoice = estimatedTimeAllowedUnitOfMeasureTypeChoice;
    }

    public String getMaximumTimeAllowed() {
        return maximumTimeAllowed;
    }

    public void setMaximumTimeAllowed(String maximumTimeAllowed) {
        this.maximumTimeAllowed = maximumTimeAllowed;
    }

    public List<LabelValueBean> getMaximumTimeAllowedUnitOfMeasureTypeChoices() {
        List<LabelValueBean> choices = null;

        setupMaximumTimeAllowedUnitOfMeasureTypeChoices();
        if(maximumTimeAllowedUnitOfMeasureTypeChoices != null) {
            choices = convertChoices(maximumTimeAllowedUnitOfMeasureTypeChoices);
        }

        return choices;
    }

    public String getMaximumTimeAllowedUnitOfMeasureTypeChoice() {
        setupMaximumTimeAllowedUnitOfMeasureTypeChoices();
        return maximumTimeAllowedUnitOfMeasureTypeChoice;
    }

    public void setMaximumTimeAllowedUnitOfMeasureTypeChoice(String maximumTimeAllowedUnitOfMeasureTypeChoice) {
        this.maximumTimeAllowedUnitOfMeasureTypeChoice = maximumTimeAllowedUnitOfMeasureTypeChoice;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);

        isDefault = Boolean.FALSE;
    }
    
}
