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

package com.echothree.control.user.party.server.command;

import com.echothree.control.user.party.common.form.GetDepartmentChoicesForm;
import com.echothree.control.user.party.common.result.GetDepartmentChoicesResult;
import com.echothree.control.user.party.common.result.PartyResultFactory;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.party.server.logic.CompanyLogic;
import com.echothree.model.control.party.server.logic.DivisionLogic;
import com.echothree.model.data.party.server.entity.PartyCompany;
import com.echothree.model.data.party.server.entity.PartyDivision;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetDepartmentChoicesCommand
        extends BaseSimpleCommand<GetDepartmentChoicesForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("CompanyName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("DivisionName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("PartyName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("DefaultDepartmentChoice", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("AllowNullChoice", FieldType.BOOLEAN, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetDepartmentChoicesCommand */
    public GetDepartmentChoicesCommand(UserVisitPK userVisitPK, GetDepartmentChoicesForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        GetDepartmentChoicesResult result = PartyResultFactory.getGetDepartmentChoicesResult();
        String companyName = form.getCompanyName();
        PartyCompany partyCompany = CompanyLogic.getInstance().getPartyCompanyByName(this, companyName, null, false);

        if(!hasExecutionErrors()) {
            String divisionName = form.getDivisionName();
            String partyName = form.getPartyName();
            PartyDivision partyDivision = DivisionLogic.getInstance().getPartyDivisionByName(this, partyCompany == null ? null : partyCompany.getParty(), divisionName, partyName, true);

            if(!hasExecutionErrors()) {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                String defaultDepartmentChoice = form.getDefaultDepartmentChoice();
                boolean allowNullChoice = Boolean.parseBoolean(form.getAllowNullChoice());

                result.setDepartmentChoices(partyControl.getDepartmentChoices(partyDivision.getParty(), defaultDepartmentChoice, allowNullChoice));
            }
        }
        
        return result;
    }
    
}
