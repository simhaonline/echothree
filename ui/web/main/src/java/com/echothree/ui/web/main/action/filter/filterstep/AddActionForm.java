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

package com.echothree.ui.web.main.action.filter.filterstep;

import com.echothree.control.user.selector.common.SelectorUtil;
import com.echothree.control.user.selector.common.form.GetSelectorChoicesForm;
import com.echothree.control.user.selector.common.result.GetSelectorChoicesResult;
import com.echothree.model.control.filter.common.choice.FilterAdjustmentChoicesBean;
import com.echothree.model.control.selector.common.SelectorConstants;
import com.echothree.model.control.selector.common.choice.SelectorChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BaseActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="FilterStepAdd")
public class AddActionForm
        extends BaseActionForm {
    
    private FilterAdjustmentChoicesBean initialFilterAdjustmentChoices;
    private SelectorChoicesBean filterItemSelectorChoices;
    
    private String filterKindName;
    private String filterTypeName;
    private String filterName;
    private String filterStepName;
    private String filterItemSelectorChoice;
    private String description;
    
    private void setupFilterItemSelectorChoices() {
        if(filterItemSelectorChoices == null) {
            try {
                GetSelectorChoicesForm form = SelectorUtil.getHome().getGetSelectorChoicesForm();
                
                form.setSelectorKindName(SelectorConstants.SelectorKind_ITEM);
                form.setSelectorTypeName(SelectorConstants.SelectorType_FILTER);
                form.setDefaultSelectorChoice(filterItemSelectorChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());
                
                CommandResult commandResult = SelectorUtil.getHome().getSelectorChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetSelectorChoicesResult result = (GetSelectorChoicesResult)executionResult.getResult();
                filterItemSelectorChoices = result.getSelectorChoices();
                
                if(filterItemSelectorChoice == null)
                    filterItemSelectorChoice = filterItemSelectorChoices.getDefaultValue();
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, selectorChoices remains null, no default
            }
        }
    }
    
    public void setFilterKindName(String filterKindName) {
        this.filterKindName = filterKindName;
    }
    
    public String getFilterKindName() {
        return filterKindName;
    }
    
    public void setFilterTypeName(String filterTypeName) {
        this.filterTypeName = filterTypeName;
    }
    
    public String getFilterTypeName() {
        return filterTypeName;
    }
    
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
    
    public String getFilterName() {
        return filterName;
    }
    
    public void setFilterStepName(String filterStepName) {
        this.filterStepName = filterStepName;
    }
    
    public String getFilterStepName() {
        return filterStepName;
    }
    
    public List<LabelValueBean> getFilterItemSelectorChoices() {
        List<LabelValueBean> choices = null;
        
        setupFilterItemSelectorChoices();
        if(filterItemSelectorChoices != null)
            choices = convertChoices(filterItemSelectorChoices);
        
        return choices;
    }
    
    public void setFilterItemSelectorChoice(String filterItemSelectorChoice) {
        this.filterItemSelectorChoice = filterItemSelectorChoice;
    }
    
    public String getFilterItemSelectorChoice() {
        setupFilterItemSelectorChoices();
        return filterItemSelectorChoice;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
}
