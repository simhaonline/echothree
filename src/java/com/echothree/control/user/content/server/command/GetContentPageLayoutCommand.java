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

import com.echothree.control.user.content.common.form.GetContentPageLayoutForm;
import com.echothree.control.user.content.common.result.ContentResultFactory;
import com.echothree.control.user.content.common.result.GetContentPageLayoutResult;
import com.echothree.model.control.content.server.ContentControl;
import com.echothree.model.control.content.server.logic.ContentPageLayoutLogic;
import com.echothree.model.control.core.common.ComponentVendors;
import com.echothree.model.control.core.common.EntityTypes;
import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.control.core.server.logic.EntityInstanceLogic;
import com.echothree.model.data.content.server.entity.ContentPageLayout;
import com.echothree.model.data.core.server.entity.EntityInstance;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSingleEntityCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetContentPageLayoutCommand
        extends BaseSingleEntityCommand<ContentPageLayout, GetContentPageLayoutForm> {
    
    // No COMMAND_SECURITY_DEFINITION, anyone may execute this command.
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ContentPageLayoutName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("EntityRef", FieldType.ENTITY_REF, false, null, null),
                new FieldDefinition("Key", FieldType.KEY, false, null, null),
                new FieldDefinition("Guid", FieldType.GUID, false, null, null),
                new FieldDefinition("Ulid", FieldType.ULID, false, null, null)
                ));
    }
    
    /** Creates a new instance of GetContentPageLayoutCommand */
    public GetContentPageLayoutCommand(UserVisitPK userVisitPK, GetContentPageLayoutForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected ContentPageLayout getEntity() {
        ContentControl contentControl = (ContentControl)Session.getModelController(ContentControl.class);
        ContentPageLayout contentPageLayout = null;
        String contentPageLayoutName = form.getContentPageLayoutName();
        int parameterCount = (contentPageLayoutName == null ? 0 : 1) + EntityInstanceLogic.getInstance().countPossibleEntitySpecs(form);

        switch(parameterCount) {
            case 0:
                contentPageLayout = contentControl.getDefaultContentPageLayout();
                break;
            case 1:
                if(contentPageLayoutName == null) {
                    EntityInstance entityInstance = EntityInstanceLogic.getInstance().getEntityInstance(this, form,
                            ComponentVendors.ECHOTHREE.name(), EntityTypes.ContentPageLayout.name());
                    
                    if(!hasExecutionErrors()) {
                        contentPageLayout = contentControl.getContentPageLayoutByEntityInstance(entityInstance);
                    }
                } else {
                    contentPageLayout = ContentPageLayoutLogic.getInstance().getContentPageLayoutByName(this, contentPageLayoutName);
                }
                break;
            default:
                addExecutionError(ExecutionErrors.InvalidParameterCount.name());
                break;
        }
        
        if(contentPageLayout != null) {
            sendEventUsingNames(contentPageLayout.getPrimaryKey(), EventTypes.READ.name(), null, null, getPartyPK());
        }

        return contentPageLayout;
    }
    
    @Override
    protected BaseResult getTransfer(ContentPageLayout contentPageLayout) {
        ContentControl contentControl = (ContentControl)Session.getModelController(ContentControl.class);
        GetContentPageLayoutResult result = ContentResultFactory.getGetContentPageLayoutResult();

        if(contentPageLayout != null) {
            result.setContentPageLayout(contentControl.getContentPageLayoutTransfer(getUserVisit(), contentPageLayout));
        }

        return result;
    }
    
}
