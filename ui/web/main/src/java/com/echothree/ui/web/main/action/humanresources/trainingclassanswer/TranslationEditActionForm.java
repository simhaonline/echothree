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

package com.echothree.ui.web.main.action.humanresources.trainingclassanswer;

import com.echothree.control.user.core.common.CoreUtil;
import com.echothree.control.user.core.common.form.GetMimeTypeChoicesForm;
import com.echothree.control.user.core.common.result.GetMimeTypeChoicesResult;
import com.echothree.model.control.core.common.MimeTypeUsageTypes;
import com.echothree.model.control.core.common.choice.MimeTypeChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BaseActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="TrainingClassAnswerTranslationEdit")
public class TranslationEditActionForm
        extends BaseActionForm {
    
    private MimeTypeChoicesBean answerMimeTypeChoices;
    private MimeTypeChoicesBean selectedMimeTypeChoices;
    
    private String trainingClassName;
    private String trainingClassSectionName;
    private String trainingClassQuestionName;
    private String trainingClassAnswerName;
    private String languageIsoName;
    private String answerMimeTypeChoice;
    private String answer;
    private String selectedMimeTypeChoice;
    private String selected;
    
     private void setupAnswerMimeTypeChoices() {
        if(answerMimeTypeChoices == null) {
            try {
                GetMimeTypeChoicesForm commandForm = CoreUtil.getHome().getGetMimeTypeChoicesForm();
                
                commandForm.setDefaultMimeTypeChoice(answerMimeTypeChoice);
                commandForm.setAllowNullChoice(Boolean.FALSE.toString());
                commandForm.setMimeTypeUsageTypeName(MimeTypeUsageTypes.TEXT.name());
                
                CommandResult commandResult = CoreUtil.getHome().getMimeTypeChoices(userVisitPK, commandForm);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetMimeTypeChoicesResult result = (GetMimeTypeChoicesResult)executionResult.getResult();
                answerMimeTypeChoices = result.getMimeTypeChoices();
                
                if(answerMimeTypeChoice == null) {
                    answerMimeTypeChoice = answerMimeTypeChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                // failed, answerMimeTypeChoices remains null, no default
            }
        }
    }
    
    private void setupSelectedMimeTypeChoices() {
        if(selectedMimeTypeChoices == null) {
            try {
                GetMimeTypeChoicesForm commandForm = CoreUtil.getHome().getGetMimeTypeChoicesForm();
                
                commandForm.setDefaultMimeTypeChoice(selectedMimeTypeChoice);
                commandForm.setAllowNullChoice(Boolean.TRUE.toString());
                commandForm.setMimeTypeUsageTypeName(MimeTypeUsageTypes.TEXT.name());
                
                CommandResult commandResult = CoreUtil.getHome().getMimeTypeChoices(userVisitPK, commandForm);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetMimeTypeChoicesResult result = (GetMimeTypeChoicesResult)executionResult.getResult();
                selectedMimeTypeChoices = result.getMimeTypeChoices();
                
                if(selectedMimeTypeChoice == null) {
                    selectedMimeTypeChoice = selectedMimeTypeChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                // failed, selectedMimeTypeChoices remains null, no default
            }
        }
    }
    
    public void setTrainingClassName(String trainingClassName) {
        this.trainingClassName = trainingClassName;
    }
    
    public String getTrainingClassName() {
        return trainingClassName;
    }
    
    public void setTrainingClassSectionName(String trainingClassSectionName) {
        this.trainingClassSectionName = trainingClassSectionName;
    }

    public String getTrainingClassSectionName() {
        return trainingClassSectionName;
    }

    public void setTrainingClassQuestionName(String trainingClassQuestionName) {
        this.trainingClassQuestionName = trainingClassQuestionName;
    }

    public String getTrainingClassQuestionName() {
        return trainingClassQuestionName;
    }

    public void setTrainingClassAnswerName(String trainingClassAnswerName) {
        this.trainingClassAnswerName = trainingClassAnswerName;
    }
    
    public String getTrainingClassAnswerName() {
        return trainingClassAnswerName;
    }
    
    public String getLanguageIsoName() {
        return languageIsoName;
    }
    
    public void setLanguageIsoName(String languageIsoName) {
        this.languageIsoName = languageIsoName;
    }
    
    public List<LabelValueBean> getAnswerMimeTypeChoices() {
        List<LabelValueBean> choices = null;
        
        setupAnswerMimeTypeChoices();
        if(answerMimeTypeChoices != null) {
            choices = convertChoices(answerMimeTypeChoices);
        }
        
        return choices;
    }
    
    public void setAnswerMimeTypeChoice(String answerMimeTypeChoice) {
        this.answerMimeTypeChoice = answerMimeTypeChoice;
    }
    
    public String getAnswerMimeTypeChoice() {
        setupAnswerMimeTypeChoices();
        
        return answerMimeTypeChoice;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public List<LabelValueBean> getSelectedMimeTypeChoices() {
        List<LabelValueBean> choices = null;
        
        setupSelectedMimeTypeChoices();
        if(selectedMimeTypeChoices != null) {
            choices = convertChoices(selectedMimeTypeChoices);
        }
        
        return choices;
    }
    
    public void setSelectedMimeTypeChoice(String selectedMimeTypeChoice) {
        this.selectedMimeTypeChoice = selectedMimeTypeChoice;
    }
    
    public String getSelectedMimeTypeChoice() {
        setupSelectedMimeTypeChoices();
        
        return selectedMimeTypeChoice;
    }
    
    public String getSelected() {
        return selected;
    }
    
    public void setSelected(String selected) {
        this.selected = selected;
    }
    
}
