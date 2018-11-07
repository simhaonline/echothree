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

package com.echothree.control.user.user.server.command;

import com.echothree.control.user.user.common.form.GetRecoveryQuestionDescriptionsForm;
import com.echothree.control.user.user.common.result.GetRecoveryQuestionDescriptionsResult;
import com.echothree.control.user.user.common.result.UserResultFactory;
import com.echothree.model.control.user.server.UserControl;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.user.server.entity.RecoveryQuestion;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetRecoveryQuestionDescriptionsCommand
        extends BaseSimpleCommand<GetRecoveryQuestionDescriptionsForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("RecoveryQuestionName", FieldType.ENTITY_NAME, true, null, null)
        ));
    }
    
    /** Creates a new instance of GetRecoveryQuestionDescriptionsCommand */
    public GetRecoveryQuestionDescriptionsCommand(UserVisitPK userVisitPK, GetRecoveryQuestionDescriptionsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        UserControl userControl = getUserControl();
        GetRecoveryQuestionDescriptionsResult result = UserResultFactory.getGetRecoveryQuestionDescriptionsResult();
        String recoveryQuestionName = form.getRecoveryQuestionName();
        RecoveryQuestion recoveryQuestion = userControl.getRecoveryQuestionByName(recoveryQuestionName);
        
        if(recoveryQuestion != null) {
            result.setRecoveryQuestion(userControl.getRecoveryQuestionTransfer(getUserVisit(), recoveryQuestion));
            result.setRecoveryQuestionDescriptions(userControl.getRecoveryQuestionDescriptionTransfers(getUserVisit(), recoveryQuestion));
        } else {
            addExecutionError(ExecutionErrors.UnknownRecoveryQuestionName.name(), recoveryQuestionName);
        }
        
        return result;
    }
    
}
