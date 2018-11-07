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

package com.echothree.control.user.party.server.command;

import com.echothree.control.user.party.common.form.GetDateTimeFormatDescriptionsForm;
import com.echothree.control.user.party.common.result.GetDateTimeFormatDescriptionsResult;
import com.echothree.control.user.party.common.result.PartyResultFactory;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.party.server.entity.DateTimeFormat;
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

public class GetDateTimeFormatDescriptionsCommand
        extends BaseSimpleCommand<GetDateTimeFormatDescriptionsForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("DateTimeFormatName", FieldType.ENTITY_NAME, true, null, null)
        ));
    }
    
    /** Creates a new instance of GetDateTimeFormatDescriptionsCommand */
    public GetDateTimeFormatDescriptionsCommand(UserVisitPK userVisitPK, GetDateTimeFormatDescriptionsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        GetDateTimeFormatDescriptionsResult result = PartyResultFactory.getGetDateTimeFormatDescriptionsResult();
        String dateTimeFormatName = form.getDateTimeFormatName();
        DateTimeFormat dateTimeFormat = partyControl.getDateTimeFormatByName(dateTimeFormatName);
        
        if(dateTimeFormat != null) {
            result.setDateTimeFormat(partyControl.getDateTimeFormatTransfer(getUserVisit(), dateTimeFormat));
            result.setDateTimeFormatDescriptions(partyControl.getDateTimeFormatDescriptionTransfers(getUserVisit(), dateTimeFormat));
        } else {
            addExecutionError(ExecutionErrors.UnknownDateTimeFormatName.name(), dateTimeFormatName);
        }
        
        return result;
    }
    
}
