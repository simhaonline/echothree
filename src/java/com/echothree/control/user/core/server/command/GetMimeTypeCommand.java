// --------------------------------------------------------------------------------
// Copyright 2002-2019 Echo Three, LLC
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

package com.echothree.control.user.core.server.command;

import com.echothree.control.user.core.common.form.GetMimeTypeForm;
import com.echothree.control.user.core.common.result.CoreResultFactory;
import com.echothree.control.user.core.common.result.GetMimeTypeResult;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.core.server.entity.MimeType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSingleEntityCommand;
import com.echothree.util.server.control.CommandSecurityDefinition;
import com.echothree.util.server.control.PartyTypeDefinition;
import com.echothree.util.server.control.SecurityRoleDefinition;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetMimeTypeCommand
        extends BaseSingleEntityCommand<MimeType, GetMimeTypeForm> {
    
    // No COMMAND_SECURITY_DEFINITION, anyone may execute this command.
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("MimeTypeName", FieldType.MIME_TYPE, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetMimeTypeCommand */
    public GetMimeTypeCommand(UserVisitPK userVisitPK, GetMimeTypeForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }

    @Override
    protected MimeType getEntity() {
        var coreControl = getCoreControl();
        String mimeTypeName = form.getMimeTypeName();
        MimeType mimeType = coreControl.getMimeTypeByName(mimeTypeName);

        if(mimeType == null) {
            addExecutionError(ExecutionErrors.UnknownMimeTypeName.name(), mimeTypeName);
        }

        return mimeType;
    }

    @Override
    protected BaseResult getTransfer(MimeType mimeType) {
        var coreControl = getCoreControl();
        GetMimeTypeResult result = CoreResultFactory.getGetMimeTypeResult();

        if(mimeType != null) {
            result.setMimeType(coreControl.getMimeTypeTransfer(getUserVisit(), mimeType));
        }

        return result;
    }

}
