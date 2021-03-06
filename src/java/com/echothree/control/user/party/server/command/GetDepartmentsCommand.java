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

import com.echothree.control.user.party.common.form.GetDepartmentsForm;
import com.echothree.control.user.party.common.result.GetDepartmentsResult;
import com.echothree.control.user.party.common.result.PartyResultFactory;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.party.server.entity.PartyCompany;
import com.echothree.model.data.party.server.entity.PartyDivision;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetDepartmentsCommand
        extends BaseSimpleCommand<GetDepartmentsForm> {

    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("CompanyName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("DivisionName", FieldType.ENTITY_NAME, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetDepartmentsCommand */
    public GetDepartmentsCommand(UserVisitPK userVisitPK, GetDepartmentsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        GetDepartmentsResult result = PartyResultFactory.getGetDepartmentsResult();
        var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
        String companyName = form.getCompanyName();
        PartyCompany partyCompany = partyControl.getPartyCompanyByName(companyName);
        
        if(partyCompany != null) {
            Party companyParty = partyCompany.getParty();
            UserVisit userVisit = getUserVisit();
            String divisionName = form.getDivisionName();
            PartyDivision partyDivision = partyControl.getPartyDivisionByName(companyParty, divisionName);
            
            result.setCompany(partyControl.getCompanyTransfer(userVisit, companyParty));
            
            if(partyDivision != null) {
                Party divisionParty = partyDivision.getParty();
                
                result.setDivision(partyControl.getDivisionTransfer(userVisit, partyDivision));
                result.setDepartments(partyControl.getDepartmentTransfersByDivision(userVisit, divisionParty));
            } else {
                addExecutionError(ExecutionErrors.UnknownDivisionName.name(), divisionName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownCompanyName.name(), companyName);
        }
        
        return result;
    }
    
}
