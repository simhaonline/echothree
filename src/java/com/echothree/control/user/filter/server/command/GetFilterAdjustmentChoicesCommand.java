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

package com.echothree.control.user.filter.server.command;

import com.echothree.control.user.filter.common.form.GetFilterAdjustmentChoicesForm;
import com.echothree.control.user.filter.common.result.FilterResultFactory;
import com.echothree.control.user.filter.common.result.GetFilterAdjustmentChoicesResult;
import com.echothree.model.control.filter.server.FilterControl;
import com.echothree.model.data.filter.server.entity.FilterKind;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetFilterAdjustmentChoicesCommand
        extends BaseSimpleCommand<GetFilterAdjustmentChoicesForm> {

    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("FilterKindName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("DefaultFilterAdjustmentChoice", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("InitialAdjustmentsOnly", FieldType.BOOLEAN, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetFilterAdjustmentChoicesCommand */
    public GetFilterAdjustmentChoicesCommand(UserVisitPK userVisitPK, GetFilterAdjustmentChoicesForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var filterControl = (FilterControl)Session.getModelController(FilterControl.class);
        GetFilterAdjustmentChoicesResult result = FilterResultFactory.getGetFilterAdjustmentChoicesResult();
        String filterKindName = form.getFilterKindName();
        FilterKind filterKind = filterControl.getFilterKindByName(filterKindName);
        
        if(filterKind != null) {
            String defaultFilterAdjustmentChoice = form.getDefaultFilterAdjustmentChoice();
            boolean initialAdjustmentsOnly = Boolean.parseBoolean(form.getInitialAdjustmentsOnly());
            
            result.setFilterAdjustmentChoices(filterControl.getFilterAdjustmentChoices(defaultFilterAdjustmentChoice, getPreferredLanguage(), filterKind, initialAdjustmentsOnly));
        } else {
            addExecutionError(ExecutionErrors.UnknownFilterKindName.name(), filterKindName);
        }
        
        return result;
    }
    
}
