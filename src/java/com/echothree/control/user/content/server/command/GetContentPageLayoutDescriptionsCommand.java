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

package com.echothree.control.user.content.server.command;

import com.echothree.control.user.content.common.form.GetContentPageLayoutDescriptionsForm;
import com.echothree.control.user.content.common.result.ContentResultFactory;
import com.echothree.control.user.content.common.result.GetContentPageLayoutDescriptionsResult;
import com.echothree.model.control.content.server.ContentControl;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.content.server.entity.ContentPageLayout;
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

public class GetContentPageLayoutDescriptionsCommand
        extends BaseSimpleCommand<GetContentPageLayoutDescriptionsForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.ContentPageLayout.name(), SecurityRoles.Description.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ContentPageLayoutName", FieldType.ENTITY_NAME, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetContentPageLayoutDescriptionsCommand */
    public GetContentPageLayoutDescriptionsCommand(UserVisitPK userVisitPK, GetContentPageLayoutDescriptionsForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        ContentControl contentControl = (ContentControl)Session.getModelController(ContentControl.class);
        GetContentPageLayoutDescriptionsResult result = ContentResultFactory.getGetContentPageLayoutDescriptionsResult();
        String contentPageLayoutName = form.getContentPageLayoutName();
        ContentPageLayout contentPageLayout = contentControl.getContentPageLayoutByName(contentPageLayoutName);
        
        if(contentPageLayout != null) {
            result.setContentPageLayout(contentControl.getContentPageLayoutTransfer(getUserVisit(), contentPageLayout));
            result.setContentPageLayoutDescriptions(contentControl.getContentPageLayoutDescriptionTransfersByContentPageLayout(getUserVisit(), contentPageLayout));
        } else {
            addExecutionError(ExecutionErrors.UnknownContentPageLayoutName.name(), contentPageLayoutName);
        }
        
        return result;
    }
    
}