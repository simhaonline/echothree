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

package com.echothree.ui.web.main.action.configuration.workflowdestinationstep;

import com.echothree.control.user.workflow.common.WorkflowUtil;
import com.echothree.control.user.workflow.common.form.GetWorkflowStepChoicesForm;
import com.echothree.control.user.workflow.common.result.GetWorkflowStepChoicesResult;
import com.echothree.model.control.workflow.common.choice.WorkflowStepChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BaseActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="WorkflowDestinationStepAdd")
public class AddActionForm
        extends BaseActionForm {
    
    private WorkflowStepChoicesBean destinationWorkflowStepChoices;

    private String workflowName;
    private String workflowStepName;
    private String workflowDestinationName;
    private String destinationWorkflowName;
    private String destinationWorkflowStepChoice;

    private void setupDestinationWorkflowStepChoices() {
        if(destinationWorkflowStepChoices == null) {
            try {
                GetWorkflowStepChoicesForm form = WorkflowUtil.getHome().getGetWorkflowStepChoicesForm();

                form.setWorkflowName(destinationWorkflowName);
                form.setDefaultWorkflowStepChoice(destinationWorkflowStepChoice);
                form.setAllowNullChoice(Boolean.FALSE.toString());

                CommandResult commandResult = WorkflowUtil.getHome().getWorkflowStepChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetWorkflowStepChoicesResult result = (GetWorkflowStepChoicesResult)executionResult.getResult();
                destinationWorkflowStepChoices = result.getWorkflowStepChoices();

                if(destinationWorkflowStepChoice == null) {
                    destinationWorkflowStepChoice = destinationWorkflowStepChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, destinationWorkflowStepChoices remains null, no default
            }
        }
    }

    /**
     * @return the workflowName
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * @param workflowName the workflowName to set
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * @return the workflowStepName
     */
    public String getWorkflowStepName() {
        return workflowStepName;
    }

    /**
     * @param workflowStepName the workflowStepName to set
     */
    public void setWorkflowStepName(String workflowStepName) {
        this.workflowStepName = workflowStepName;
    }

    /**
     * @return the workflowDestinationName
     */
    public String getWorkflowDestinationName() {
        return workflowDestinationName;
    }

    /**
     * @param workflowDestinationName the workflowDestinationName to set
     */
    public void setWorkflowDestinationName(String workflowDestinationName) {
        this.workflowDestinationName = workflowDestinationName;
    }

    /**
     * @return the destinationWorkflowName
     */
    public String getDestinationWorkflowName() {
        return destinationWorkflowName;
    }

    /**
     * @param destinationWorkflowName the destinationWorkflowName to set
     */
    public void setDestinationWorkflowName(String destinationWorkflowName) {
        this.destinationWorkflowName = destinationWorkflowName;
    }

    public String getDestinationWorkflowStepChoice() {
        setupDestinationWorkflowStepChoices();
        
        return destinationWorkflowStepChoice;
    }

    public void setDestinationWorkflowStepChoice(String destinationWorkflowStepChoice) {
        this.destinationWorkflowStepChoice = destinationWorkflowStepChoice;
    }

    public List<LabelValueBean> getDestinationWorkflowStepChoices() {
        List<LabelValueBean> choices = null;

        setupDestinationWorkflowStepChoices();
        if(destinationWorkflowStepChoices != null) {
            choices = convertChoices(destinationWorkflowStepChoices);
        }

        return choices;
    }
    
}
