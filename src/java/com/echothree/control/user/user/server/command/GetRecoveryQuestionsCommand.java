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

package com.echothree.control.user.user.server.command;

import com.echothree.control.user.user.common.form.GetRecoveryQuestionsForm;
import com.echothree.control.user.user.common.result.GetRecoveryQuestionsResult;
import com.echothree.control.user.user.common.result.UserResultFactory;
import com.echothree.model.control.user.server.UserControl;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.user.server.entity.RecoveryQuestion;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseMultipleEntitiesCommand;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GetRecoveryQuestionsCommand
        extends BaseMultipleEntitiesCommand<RecoveryQuestion, GetRecoveryQuestionsForm> {
    
    // No COMMAND_SECURITY_DEFINITION, anyone may execute this command.
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                ));
    }
    
    /** Creates a new instance of GetRecoveryQuestionsCommand */
    public GetRecoveryQuestionsCommand(UserVisitPK userVisitPK, GetRecoveryQuestionsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected Collection<RecoveryQuestion> getEntities() {
        UserControl userControl = getUserControl();
        
        return userControl.getRecoveryQuestions();
    }
    
    @Override
    protected BaseResult getTransfers(Collection<RecoveryQuestion> entities) {
        GetRecoveryQuestionsResult result = UserResultFactory.getGetRecoveryQuestionsResult();
        UserControl userControl = getUserControl();
        
        result.setRecoveryQuestions(userControl.getRecoveryQuestionTransfers(getUserVisit(), entities));
        
        return result;
    }
    
}
