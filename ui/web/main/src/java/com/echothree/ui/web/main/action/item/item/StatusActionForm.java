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

package com.echothree.ui.web.main.action.item.item;

import com.echothree.control.user.item.common.ItemUtil;
import com.echothree.control.user.item.common.form.GetItemStatusChoicesForm;
import com.echothree.control.user.item.common.result.GetItemStatusChoicesResult;
import com.echothree.model.control.item.common.choice.ItemStatusChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BaseActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="ItemStatus")
public class StatusActionForm
        extends BaseActionForm {
    
    private ItemStatusChoicesBean itemStatusChoices;
    
    private String forwardKey;
    private String itemName;
    private String itemStatusChoice;
    
    public void setupItemStatusChoices() {
        if(itemStatusChoices == null) {
            try {
                GetItemStatusChoicesForm form = ItemUtil.getHome().getGetItemStatusChoicesForm();
                
                form.setItemName(itemName);
                form.setDefaultItemStatusChoice(itemStatusChoice);
                form.setAllowNullChoice(Boolean.FALSE.toString());
                
                CommandResult commandResult = ItemUtil.getHome().getItemStatusChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetItemStatusChoicesResult result = (GetItemStatusChoicesResult)executionResult.getResult();
                itemStatusChoices = result.getItemStatusChoices();
                
                if(itemStatusChoice == null)
                    itemStatusChoice = itemStatusChoices.getDefaultValue();
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, itemStatusChoices remains null, no default
            }
        }
    }
    
    public String getForwardKey() {
        return forwardKey;
    }
    
    public void setForwardKey(String forwardKey) {
        this.forwardKey = forwardKey;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItemStatusChoice() {
        return itemStatusChoice;
    }
    
    public void setItemStatusChoice(String itemStatusChoice) {
        this.itemStatusChoice = itemStatusChoice;
    }
    
    public List<LabelValueBean> getItemStatusChoices() {
        List<LabelValueBean> choices = null;
        
        setupItemStatusChoices();
        if(itemStatusChoices != null)
            choices = convertChoices(itemStatusChoices);
        
        return choices;
    }
    
}
