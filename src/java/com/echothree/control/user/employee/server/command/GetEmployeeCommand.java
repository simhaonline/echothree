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

package com.echothree.control.user.employee.server.command;

import com.echothree.control.user.employee.common.form.GetEmployeeForm;
import com.echothree.control.user.employee.common.result.EmployeeResultFactory;
import com.echothree.control.user.employee.common.result.GetEmployeeResult;
import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.control.employee.server.EmployeeControl;
import com.echothree.model.control.party.common.PartyTypes;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.party.server.logic.PartyLogic;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.employee.server.entity.PartyEmployee;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.control.CommandSecurityDefinition;
import com.echothree.util.server.control.PartyTypeDefinition;
import com.echothree.util.server.control.SecurityRoleDefinition;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetEmployeeCommand
        extends BaseSimpleCommand<GetEmployeeForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyTypes.UTILITY.name(), null),
                new PartyTypeDefinition(PartyTypes.EMPLOYEE.name(), Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.Employee.name(), SecurityRoles.Review.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("EmployeeName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("PartyName", FieldType.ENTITY_NAME, false, null, null)
                ));
    }
    
    /** Creates a new instance of GetEmployeeCommand */
    public GetEmployeeCommand(UserVisitPK userVisitPK, GetEmployeeForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        GetEmployeeResult result = EmployeeResultFactory.getGetEmployeeResult();
        var employeeControl = (EmployeeControl)Session.getModelController(EmployeeControl.class);
        String employeeName = form.getEmployeeName();
        String partyName = form.getPartyName();
        int parameterCount = (employeeName == null? 0: 1) + (partyName == null? 0: 1);
        
        if(parameterCount < 2) {
            Party party = null;
            
            if(employeeName != null) {
                PartyEmployee partyEmployee = employeeControl.getPartyEmployeeByName(employeeName);
                
                if(partyEmployee != null) {
                    party = partyEmployee.getParty();
                } else {
                    addExecutionError(ExecutionErrors.UnknownEmployeeName.name(), employeeName);
                }
            } else if(partyName != null) {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                
                party = partyControl.getPartyByName(partyName);
                
                if(party == null) {
                    addExecutionError(ExecutionErrors.UnknownPartyName.name(), partyName);
                }
            } else {
                party = getParty();
            }
            
            if(!hasExecutionErrors()) {
                PartyLogic.getInstance().checkPartyType(this, party, PartyTypes.EMPLOYEE.name());
                
                if(!hasExecutionErrors()) {
                    result.setEmployee(employeeControl.getEmployeeTransfer(getUserVisit(), party));

                    sendEventUsingNames(party.getPrimaryKey(), EventTypes.READ.name(), null, null, getPartyPK());
                }
            }
        } else {
            addExecutionError(ExecutionErrors.InvalidParameterCount.name());
        }
        
        return result;
    }
    
}
