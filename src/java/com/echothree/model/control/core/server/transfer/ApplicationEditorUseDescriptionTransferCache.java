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

package com.echothree.model.control.core.server.transfer;

import com.echothree.model.control.core.common.transfer.ApplicationEditorUseDescriptionTransfer;
import com.echothree.model.control.core.common.transfer.ApplicationEditorUseTransfer;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.party.common.transfer.LanguageTransfer;
import com.echothree.model.data.core.server.entity.ApplicationEditorUseDescription;
import com.echothree.model.data.user.server.entity.UserVisit;

public class ApplicationEditorUseDescriptionTransferCache
        extends BaseCoreDescriptionTransferCache<ApplicationEditorUseDescription, ApplicationEditorUseDescriptionTransfer> {
    
    /** Creates a new instance of ApplicationEditorUseDescriptionTransferCache */
    public ApplicationEditorUseDescriptionTransferCache(UserVisit userVisit, CoreControl coreControl) {
        super(userVisit, coreControl);
    }
    
    public ApplicationEditorUseDescriptionTransfer getApplicationEditorUseDescriptionTransfer(ApplicationEditorUseDescription applicationEditorUseDescription) {
        ApplicationEditorUseDescriptionTransfer applicationEditorUseDescriptionTransfer = get(applicationEditorUseDescription);
        
        if(applicationEditorUseDescriptionTransfer == null) {
            ApplicationEditorUseTransfer applicationEditorUseTransfer = coreControl.getApplicationEditorUseTransfer(userVisit, applicationEditorUseDescription.getApplicationEditorUse());
            LanguageTransfer languageTransfer = partyControl.getLanguageTransfer(userVisit, applicationEditorUseDescription.getLanguage());
            
            applicationEditorUseDescriptionTransfer = new ApplicationEditorUseDescriptionTransfer(languageTransfer, applicationEditorUseTransfer, applicationEditorUseDescription.getDescription());
            put(applicationEditorUseDescription, applicationEditorUseDescriptionTransfer);
        }
        return applicationEditorUseDescriptionTransfer;
    }
    
}
