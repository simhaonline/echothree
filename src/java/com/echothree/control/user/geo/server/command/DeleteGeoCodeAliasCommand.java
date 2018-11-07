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

package com.echothree.control.user.geo.server.command;

import com.echothree.control.user.geo.common.form.DeleteGeoCodeAliasForm;
import com.echothree.model.control.geo.server.GeoControl;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.geo.server.entity.GeoCode;
import com.echothree.model.data.geo.server.entity.GeoCodeAlias;
import com.echothree.model.data.geo.server.entity.GeoCodeAliasType;
import com.echothree.model.data.geo.server.entity.GeoCodeType;
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

public class DeleteGeoCodeAliasCommand
        extends BaseSimpleCommand<DeleteGeoCodeAliasForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.GeoCodeAlias.name(), SecurityRoles.Delete.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("GeoCodeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("GeoCodeAliasTypeName", FieldType.ENTITY_NAME, true, null, null)
                ));
    }
    
    /** Creates a new instance of DeleteGeoCodeAliasCommand */
    public DeleteGeoCodeAliasCommand(UserVisitPK userVisitPK, DeleteGeoCodeAliasForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        GeoControl geoControl = (GeoControl)Session.getModelController(GeoControl.class);
        String geoCodeName = form.getGeoCodeName();
        GeoCode geoCode = geoControl.getGeoCodeByName(geoCodeName);

        if(geoCode != null) {
            GeoCodeType geoCodeType = geoCode.getLastDetail().getGeoCodeType();
            String geoCodeAliasTypeName = form.getGeoCodeAliasTypeName();
            GeoCodeAliasType geoCodeAliasType = geoControl.getGeoCodeAliasTypeByNameForUpdate(geoCodeType, geoCodeAliasTypeName);

            if(geoCodeAliasType != null) {
                if(!geoCodeAliasType.getLastDetail().getIsRequired()) {
                    GeoCodeAlias geoCodeAlias = geoControl.getGeoCodeAliasForUpdate(geoCode, geoCodeAliasType);

                    if(geoCodeAlias != null) {
                        geoControl.deleteGeoCodeAlias(geoCodeAlias, getPartyPK());
                    } else {
                        addExecutionError(ExecutionErrors.UnknownGeoCodeAlias.name(), geoCodeName, geoCodeAliasTypeName);
                    }
                } else {
                    addExecutionError(ExecutionErrors.CannotDeleteRequiredGeoCodeAlias.name(), geoCodeName, geoCodeAliasTypeName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownGeoCodeAliasTypeName.name(), geoCodeType.getLastDetail().getGeoCodeTypeName(), geoCodeAliasTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownGeoCodeName.name(), geoCodeName);
        }
        
        return null;
    }
    
}
