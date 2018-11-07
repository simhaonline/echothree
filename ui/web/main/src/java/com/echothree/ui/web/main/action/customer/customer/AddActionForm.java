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

package com.echothree.ui.web.main.action.customer.customer;

import com.echothree.control.user.accounting.common.AccountingUtil;
import com.echothree.control.user.accounting.common.form.GetGlAccountChoicesForm;
import com.echothree.control.user.accounting.common.result.GetGlAccountChoicesResult;
import com.echothree.control.user.cancellationpolicy.common.CancellationPolicyUtil;
import com.echothree.control.user.cancellationpolicy.common.form.GetCancellationPolicyChoicesForm;
import com.echothree.control.user.cancellationpolicy.common.result.GetCancellationPolicyChoicesResult;
import com.echothree.control.user.customer.common.CustomerUtil;
import com.echothree.control.user.customer.common.form.GetCustomerCreditStatusChoicesForm;
import com.echothree.control.user.customer.common.form.GetCustomerStatusChoicesForm;
import com.echothree.control.user.customer.common.form.GetCustomerTypeChoicesForm;
import com.echothree.control.user.customer.common.result.GetCustomerCreditStatusChoicesResult;
import com.echothree.control.user.customer.common.result.GetCustomerStatusChoicesResult;
import com.echothree.control.user.customer.common.result.GetCustomerTypeChoicesResult;
import com.echothree.control.user.offer.common.OfferUtil;
import com.echothree.control.user.offer.common.form.GetSourceChoicesForm;
import com.echothree.control.user.offer.common.result.GetSourceChoicesResult;
import com.echothree.control.user.returnpolicy.common.ReturnPolicyUtil;
import com.echothree.control.user.returnpolicy.common.form.GetReturnPolicyChoicesForm;
import com.echothree.control.user.returnpolicy.common.result.GetReturnPolicyChoicesResult;
import com.echothree.model.control.accounting.common.AccountingConstants;
import com.echothree.model.control.accounting.common.choice.GlAccountChoicesBean;
import com.echothree.model.control.cancellationpolicy.common.CancellationPolicyConstants;
import com.echothree.model.control.cancellationpolicy.common.choice.CancellationPolicyChoicesBean;
import com.echothree.model.control.customer.common.choice.CustomerCreditStatusChoicesBean;
import com.echothree.model.control.customer.common.choice.CustomerStatusChoicesBean;
import com.echothree.model.control.customer.common.choice.CustomerTypeChoicesBean;
import com.echothree.model.control.offer.common.choice.SourceChoicesBean;
import com.echothree.model.control.returnpolicy.common.ReturnPolicyConstants;
import com.echothree.model.control.returnpolicy.common.choice.ReturnPolicyChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BasePersonActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="CustomerAdd")
public class AddActionForm
        extends BasePersonActionForm {
    
    private CustomerTypeChoicesBean customerTypeChoices;
    private CancellationPolicyChoicesBean cancellationPolicyChoices;
    private ReturnPolicyChoicesBean returnPolicyChoices;
    private GlAccountChoicesBean arGlAccountChoices;
    private SourceChoicesBean initialSourceChoices;
    private CustomerStatusChoicesBean customerStatusChoices;
    private CustomerCreditStatusChoicesBean customerCreditStatusChoices;
    
    private String customerTypeChoice;
    private String cancellationPolicyChoice;
    private String returnPolicyChoice;
    private String arGlAccountChoice;
    private String initialSourceChoice;
    private String name;
    private String emailAddress;
    private Boolean allowSolicitation;
    private String customerStatusChoice;
    private String customerCreditStatusChoice;
    
    /** Creates a new instance of AddActionForm */
    public AddActionForm() {
        super();
    }
    
    private void setupCustomerTypeChoices() {
        if(customerTypeChoices == null) {
            try {
                GetCustomerTypeChoicesForm form = CustomerUtil.getHome().getGetCustomerTypeChoicesForm();
                
                form.setDefaultCustomerTypeChoice(customerTypeChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());
                
                CommandResult commandResult = CustomerUtil.getHome().getCustomerTypeChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetCustomerTypeChoicesResult result = (GetCustomerTypeChoicesResult)executionResult.getResult();
                customerTypeChoices = result.getCustomerTypeChoices();
                
                if(customerTypeChoice == null) {
                    customerTypeChoice = customerTypeChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                // failed, customerTypeChoices remains null, no default
            }
        }
    }
    
    public void setupCancellationPolicyChoices() {
        if(cancellationPolicyChoices == null) {
            try {
                GetCancellationPolicyChoicesForm form = CancellationPolicyUtil.getHome().getGetCancellationPolicyChoicesForm();

                form.setCancellationKindName(CancellationPolicyConstants.CancellationKind_CUSTOMER_CANCELLATION);
                form.setDefaultCancellationPolicyChoice(cancellationPolicyChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());

                CommandResult commandResult = CancellationPolicyUtil.getHome().getCancellationPolicyChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetCancellationPolicyChoicesResult result = (GetCancellationPolicyChoicesResult)executionResult.getResult();
                cancellationPolicyChoices = result.getCancellationPolicyChoices();

                if(cancellationPolicyChoice == null)
                    cancellationPolicyChoice = cancellationPolicyChoices.getDefaultValue();
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, cancellationPolicyChoices remains null, no
            }
        }
    }

    public void setupReturnPolicyChoices() {
        if(returnPolicyChoices == null) {
            try {
                GetReturnPolicyChoicesForm form = ReturnPolicyUtil.getHome().getGetReturnPolicyChoicesForm();

                form.setReturnKindName(ReturnPolicyConstants.ReturnKind_CUSTOMER_RETURN);
                form.setDefaultReturnPolicyChoice(returnPolicyChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());

                CommandResult commandResult = ReturnPolicyUtil.getHome().getReturnPolicyChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetReturnPolicyChoicesResult result = (GetReturnPolicyChoicesResult)executionResult.getResult();
                returnPolicyChoices = result.getReturnPolicyChoices();

                if(returnPolicyChoice == null)
                    returnPolicyChoice = returnPolicyChoices.getDefaultValue();
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, returnPolicyChoices remains null, no
            }
        }
    }

    public void setupArGlAccountChoices() {
        if(arGlAccountChoices == null) {
            try {
                GetGlAccountChoicesForm form = AccountingUtil.getHome().getGetGlAccountChoicesForm();

                form.setGlAccountCategoryName(AccountingConstants.GlAccountCategory_ACCOUNTS_RECEIVABLE);
                form.setDefaultGlAccountChoice(arGlAccountChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());

                CommandResult commandResult = AccountingUtil.getHome().getGlAccountChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetGlAccountChoicesResult getGlAccountChoicesResult = (GetGlAccountChoicesResult)executionResult.getResult();
                arGlAccountChoices = getGlAccountChoicesResult.getGlAccountChoices();

                if(arGlAccountChoice == null) {
                    arGlAccountChoice = arGlAccountChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, arGlAccountChoices remains null, no default
            }
        }
    }

    public void setupInitialSourceChoices() {
        if(initialSourceChoices == null) {
            try {
                GetSourceChoicesForm form = OfferUtil.getHome().getGetSourceChoicesForm();
                
                form.setDefaultSourceChoice(initialSourceChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());
                
                CommandResult commandResult = OfferUtil.getHome().getSourceChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetSourceChoicesResult result = (GetSourceChoicesResult)executionResult.getResult();
                initialSourceChoices = result.getSourceChoices();
                
                if(initialSourceChoice == null) {
                    initialSourceChoice = initialSourceChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, sourceChoices remains null, no default
            }
        }
    }
    
    public void setupCustomerStatusChoices() {
        if(customerStatusChoices == null) {
            try {
                GetCustomerStatusChoicesForm form = CustomerUtil.getHome().getGetCustomerStatusChoicesForm();
                
                form.setDefaultCustomerStatusChoice(customerStatusChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());
                
                CommandResult commandResult = CustomerUtil.getHome().getCustomerStatusChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetCustomerStatusChoicesResult result = (GetCustomerStatusChoicesResult)executionResult.getResult();
                customerStatusChoices = result.getCustomerStatusChoices();
                
                if(customerStatusChoice == null) {
                    customerStatusChoice = customerStatusChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, customerStatusChoices remains null, no default
            }
        }
    }
    
    public void setupCustomerCreditStatusChoices() {
        if(customerCreditStatusChoices == null) {
            try {
                GetCustomerCreditStatusChoicesForm form = CustomerUtil.getHome().getGetCustomerCreditStatusChoicesForm();
                
                form.setDefaultCustomerCreditStatusChoice(customerCreditStatusChoice);
                form.setAllowNullChoice(Boolean.TRUE.toString());
                
                CommandResult commandResult = CustomerUtil.getHome().getCustomerCreditStatusChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetCustomerCreditStatusChoicesResult result = (GetCustomerCreditStatusChoicesResult)executionResult.getResult();
                customerCreditStatusChoices = result.getCustomerCreditStatusChoices();
                
                if(customerCreditStatusChoice == null) {
                    customerCreditStatusChoice = customerCreditStatusChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, customerCreditStatusChoices remains null, no default
            }
        }
    }
    
    public List<LabelValueBean> getCustomerTypeChoices() {
        List<LabelValueBean> choices = null;
        
        setupCustomerTypeChoices();
        if(customerTypeChoices != null) {
            choices = convertChoices(customerTypeChoices);
        }
        
        return choices;
    }
    
    public void setCustomerTypeChoice(String customerTypeChoice) {
        this.customerTypeChoice = customerTypeChoice;
    }
    
    public String getCustomerTypeChoice() {
        setupCustomerTypeChoices();
        
        return customerTypeChoice;
    }
    
    public List<LabelValueBean> getCancellationPolicyChoices() {
        List<LabelValueBean> choices = null;

        setupCancellationPolicyChoices();
        if(cancellationPolicyChoices != null)
            choices = convertChoices(cancellationPolicyChoices);

        return choices;
    }

    public void setCancellationPolicyChoice(String cancellationPolicyChoice) {
        this.cancellationPolicyChoice = cancellationPolicyChoice;
    }

    public String getCancellationPolicyChoice() {
        setupCancellationPolicyChoices();

        return cancellationPolicyChoice;
    }

    public List<LabelValueBean> getReturnPolicyChoices() {
        List<LabelValueBean> choices = null;

        setupReturnPolicyChoices();
        if(returnPolicyChoices != null)
            choices = convertChoices(returnPolicyChoices);

        return choices;
    }

    public void setReturnPolicyChoice(String returnPolicyChoice) {
        this.returnPolicyChoice = returnPolicyChoice;
    }

    public String getReturnPolicyChoice() {
        setupReturnPolicyChoices();

        return returnPolicyChoice;
    }

    public List<LabelValueBean> getArGlAccountChoices() {
        List<LabelValueBean> choices = null;

        setupArGlAccountChoices();
        if(arGlAccountChoices != null) {
            choices = convertChoices(arGlAccountChoices);
        }

        return choices;
    }

    public void setArGlAccountChoice(String arGlAccountChoice) {
        this.arGlAccountChoice = arGlAccountChoice;
    }

    public String getArGlAccountChoice() {
        setupArGlAccountChoices();

        return arGlAccountChoice;
    }

    public List<LabelValueBean> getInitialSourceChoices() {
        List<LabelValueBean> choices = null;
        
        setupInitialSourceChoices();
        if(initialSourceChoices != null) {
            choices = convertChoices(initialSourceChoices);
        }
        
        return choices;
    }
    
    public void setInitialSourceChoice(String initialSourceChoice) {
        this.initialSourceChoice = initialSourceChoice;
    }
    
    public String getInitialSourceChoice() {
        setupInitialSourceChoices();
        
        return initialSourceChoice;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public Boolean getAllowSolicitation() {
        return allowSolicitation;
    }
    
    public void setAllowSolicitation(Boolean allowSolicitation) {
        this.allowSolicitation = allowSolicitation;
    }
    
    public String getCustomerStatusChoice() {
        return customerStatusChoice;
    }
    
    public void setCustomerStatusChoice(String customerStatusChoice) {
        this.customerStatusChoice = customerStatusChoice;
    }
    
    public List<LabelValueBean> getCustomerStatusChoices() {
        List<LabelValueBean> choices = null;
        
        setupCustomerStatusChoices();
        if(customerStatusChoices != null)
            choices = convertChoices(customerStatusChoices);
        
        return choices;
    }
    
    public String getCustomerCreditStatusChoice() {
        return customerCreditStatusChoice;
    }
    
    public void setCustomerCreditStatusChoice(String customerCreditStatusChoice) {
        this.customerCreditStatusChoice = customerCreditStatusChoice;
    }
    
    public List<LabelValueBean> getCustomerCreditStatusChoices() {
        List<LabelValueBean> choices = null;
        
        setupCustomerCreditStatusChoices();
        if(customerCreditStatusChoices != null)
            choices = convertChoices(customerCreditStatusChoices);
        
        return choices;
    }
    
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        
        setAllowSolicitation(Boolean.FALSE);
    }
    
}
