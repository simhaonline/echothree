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

package com.echothree.control.user.security.server.command;

import com.echothree.control.user.security.common.form.CreateSecurityRolePartyTypeForm;
import com.echothree.model.control.party.common.PartyTypes;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.control.security.server.SecurityControl;
import com.echothree.model.control.selector.common.SelectorConstants;
import com.echothree.model.control.selector.server.SelectorControl;
import com.echothree.model.data.party.server.entity.PartyType;
import com.echothree.model.data.security.server.entity.SecurityRole;
import com.echothree.model.data.security.server.entity.SecurityRoleGroup;
import com.echothree.model.data.security.server.entity.SecurityRolePartyType;
import com.echothree.model.data.selector.server.entity.Selector;
import com.echothree.model.data.selector.server.entity.SelectorKind;
import com.echothree.model.data.selector.server.entity.SelectorType;
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

public class CreateSecurityRolePartyTypeCommand
        extends BaseSimpleCommand<CreateSecurityRolePartyTypeForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyTypes.UTILITY.name(), null),
                new PartyTypeDefinition(PartyTypes.EMPLOYEE.name(), Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.SecurityRolePartyType.name(), SecurityRoles.Create.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("SecurityRoleGroupName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("SecurityRoleName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("PartyTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("PartySelectorName", FieldType.ENTITY_NAME, false, null, null)
                ));
    }
    
    /** Creates a new instance of CreateSecurityRolePartyTypeCommand */
    public CreateSecurityRolePartyTypeCommand(UserVisitPK userVisitPK, CreateSecurityRolePartyTypeForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var securityControl = (SecurityControl)Session.getModelController(SecurityControl.class);
        String securityRoleGroupName = form.getSecurityRoleGroupName();
        SecurityRoleGroup securityRoleGroup = securityControl.getSecurityRoleGroupByName(securityRoleGroupName);
        
        if(securityRoleGroup != null) {
            String securityRoleName = form.getSecurityRoleName();
            SecurityRole securityRole = securityControl.getSecurityRoleByName(securityRoleGroup, securityRoleName);
            
            if(securityRole != null) {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                String partyTypeName = form.getPartyTypeName();
                PartyType partyType = partyControl.getPartyTypeByName(partyTypeName);
                
                if(partyType != null) {
                    SecurityRolePartyType securityRolePartyType = securityControl.getSecurityRolePartyType(securityRole, partyType);
                    
                    if(securityRolePartyType == null) {
                        String partySelectorName = form.getPartySelectorName();
                        Selector partySelector = null;
                        
                        if(partySelectorName != null) {
                            if(partyType.getAllowUserLogins()) {
                                var selectorControl = (SelectorControl)Session.getModelController(SelectorControl.class);
                                SelectorKind selectorKind = selectorControl.getSelectorKindByName(partyTypeName);

                                if(selectorKind != null) {
                                    SelectorType selectorType = selectorControl.getSelectorTypeByName(selectorKind,
                                            SelectorConstants.SelectorType_SECURITY_ROLE);
                                    
                                    if(selectorType != null) {
                                        partySelector = selectorControl.getSelectorByName(selectorType, partySelectorName);
                                        
                                        if(partySelector == null) {
                                            addExecutionError(ExecutionErrors.UnknownPartySelectorName.name(), partyTypeName,
                                                    SelectorConstants.SelectorType_SECURITY_ROLE, partySelectorName);
                                        }
                                    } else {
                                        addExecutionError(ExecutionErrors.UnknownPartySelectorTypeName.name(), partyTypeName,
                                                SelectorConstants.SelectorType_SECURITY_ROLE);
                                    }
                                } else {
                                    addExecutionError(ExecutionErrors.UnknownPartySelectorKindName.name(), partyTypeName);
                                }
                            } else {
                                addExecutionError(ExecutionErrors.InvalidPartyType.name(), partyTypeName);
                            }
                        }
                        
                        if(partySelectorName == null || partySelector != null) {
                            securityControl.createSecurityRolePartyType(securityRole, partyType, partySelector, getPartyPK());
                        }
                    } else {
                        addExecutionError(ExecutionErrors.DuplicateSecurityRolePartyType.name(), securityRoleGroupName, securityRoleName, partyTypeName);
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnknownPartyTypeName.name(), partyTypeName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownSecurityRoleName.name(), securityRoleGroupName, securityRoleName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownSecurityRoleGroupName.name(), securityRoleGroupName);
        }
        
        return null;
    }
    
}
