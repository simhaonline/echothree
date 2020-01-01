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

package com.echothree.control.user.contact.server.command;

import com.echothree.control.user.contact.common.form.GetPostalAddressLineElementsForm;
import com.echothree.control.user.contact.common.result.ContactResultFactory;
import com.echothree.control.user.contact.common.result.GetPostalAddressLineElementsResult;
import com.echothree.model.control.contact.server.ContactControl;
import com.echothree.model.data.contact.server.entity.PostalAddressFormat;
import com.echothree.model.data.contact.server.entity.PostalAddressLine;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetPostalAddressLineElementsCommand
        extends BaseSimpleCommand<GetPostalAddressLineElementsForm> {

    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("PostalAddressFormatName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("PostalAddressLineSortOrder", FieldType.SIGNED_INTEGER, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetPostalAddressLineElementsCommand */
    public GetPostalAddressLineElementsCommand(UserVisitPK userVisitPK, GetPostalAddressLineElementsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        var contactControl = (ContactControl)Session.getModelController(ContactControl.class);
        GetPostalAddressLineElementsResult result = ContactResultFactory.getGetPostalAddressLineElementsResult();
        String postalAddressFormatName = form.getPostalAddressFormatName();
        PostalAddressFormat postalAddressFormat = contactControl.getPostalAddressFormatByName(postalAddressFormatName);
        
        if(postalAddressFormat != null) {
            Integer postalAddressLineSortOrder = Integer.valueOf(form.getPostalAddressLineSortOrder());
            PostalAddressLine postalAddressLine = contactControl.getPostalAddressLine(postalAddressFormat, postalAddressLineSortOrder);
            
            result.setPostalAddressFormat(contactControl.getPostalAddressFormatTransfer(getUserVisit(), postalAddressFormat));
            
            if(postalAddressLine != null) {
                result.setPostalAddressLine(contactControl.getPostalAddressLineTransfer(getUserVisit(), postalAddressLine));
                result.setPostalAddressLineElements(contactControl.getPostalAddressLineElementTransfersByPostalAddressLine(getUserVisit(), postalAddressLine));
            } else {
                addExecutionError(ExecutionErrors.UnknownPostalAddressLine.name(), postalAddressLineSortOrder);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownPostalAddressFormatName.name(), postalAddressFormatName);
        }
        
        return result;
    }
    
}
