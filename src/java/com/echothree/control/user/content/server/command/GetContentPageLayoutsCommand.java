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

import com.echothree.control.user.content.common.form.GetContentPageLayoutsForm;
import com.echothree.control.user.content.common.result.ContentResultFactory;
import com.echothree.control.user.content.common.result.GetContentPageLayoutsResult;
import com.echothree.model.control.content.server.ContentControl;
import com.echothree.model.data.content.server.entity.ContentPageLayout;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseMultipleEntitiesCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GetContentPageLayoutsCommand
        extends BaseMultipleEntitiesCommand<ContentPageLayout, GetContentPageLayoutsForm> {
    
    // No COMMAND_SECURITY_DEFINITION, anyone may execute this command.
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                ));
    }
    
    /** Creates a new instance of GetContentPageLayoutsCommand */
    public GetContentPageLayoutsCommand(UserVisitPK userVisitPK, GetContentPageLayoutsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected Collection<ContentPageLayout> getEntities() {
        ContentControl contentControl = (ContentControl)Session.getModelController(ContentControl.class);
        
        return contentControl.getContentPageLayouts();
    }
    
    @Override
    protected BaseResult getTransfers(Collection<ContentPageLayout> entities) {
        GetContentPageLayoutsResult result = ContentResultFactory.getGetContentPageLayoutsResult();
        ContentControl contentControl = (ContentControl)Session.getModelController(ContentControl.class);
        
        result.setContentPageLayouts(contentControl.getContentPageLayoutTransfers(getUserVisit(), entities));
        
        return result;
    }
    
}