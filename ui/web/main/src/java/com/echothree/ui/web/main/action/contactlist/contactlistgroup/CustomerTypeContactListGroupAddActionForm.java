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

package com.echothree.ui.web.main.action.contactlist.contactlistgroup;

import com.echothree.control.user.customer.common.CustomerUtil;
import com.echothree.control.user.customer.common.form.GetCustomerTypeChoicesForm;
import com.echothree.control.user.customer.common.result.GetCustomerTypeChoicesResult;
import com.echothree.model.control.customer.common.choice.CustomerTypeChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BaseActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="CustomerTypeContactListGroupAdd")
public class CustomerTypeContactListGroupAddActionForm
        extends BaseActionForm {
    
    private CustomerTypeChoicesBean customerTypeChoices;

    private String contactListGroupName;
    private String customerTypeChoice;
    private Boolean addWhenCreated;
    
    private void setupCustomerTypeChoices() {
        if(customerTypeChoices == null) {
            try {
                GetCustomerTypeChoicesForm form = CustomerUtil.getHome().getGetCustomerTypeChoicesForm();

                form.setDefaultCustomerTypeChoice(customerTypeChoice);
                form.setAllowNullChoice(Boolean.FALSE.toString());

                CommandResult commandResult = CustomerUtil.getHome().getCustomerTypeChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetCustomerTypeChoicesResult getCustomerTypeChoicesResult = (GetCustomerTypeChoicesResult)executionResult.getResult();
                customerTypeChoices = getCustomerTypeChoicesResult.getCustomerTypeChoices();

                if(customerTypeChoice == null) {
                    customerTypeChoice = customerTypeChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, customerTypeChoices remains null, no default
            }
        }
    }

    public void setContactListGroupName(String contactListGroupName) {
        this.contactListGroupName = contactListGroupName;
    }
    
    public String getContactListGroupName() {
        return contactListGroupName;
    }
    
    public String getCustomerTypeChoice() {
        setupCustomerTypeChoices();

        return customerTypeChoice;
    }

    public void setCustomerTypeChoice(String customerTypeChoice) {
        this.customerTypeChoice = customerTypeChoice;
    }

    public List<LabelValueBean> getCustomerTypeChoices() {
        List<LabelValueBean> choices = null;

        setupCustomerTypeChoices();
        if(customerTypeChoices != null) {
            choices = convertChoices(customerTypeChoices);
        }

        return choices;
    }
    
    public Boolean getAddWhenCreated() {
        return addWhenCreated;
    }

    public void setAddWhenCreated(Boolean addWhenCreated) {
        this.addWhenCreated = addWhenCreated;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);

        addWhenCreated = Boolean.FALSE;
    }

}
