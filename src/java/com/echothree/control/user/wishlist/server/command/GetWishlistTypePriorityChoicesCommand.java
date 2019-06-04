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

package com.echothree.control.user.wishlist.server.command;

import com.echothree.control.user.wishlist.common.form.GetWishlistTypePriorityChoicesForm;
import com.echothree.control.user.wishlist.common.result.GetWishlistTypePriorityChoicesResult;
import com.echothree.control.user.wishlist.common.result.WishlistResultFactory;
import com.echothree.model.control.wishlist.server.WishlistControl;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.wishlist.server.entity.WishlistType;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetWishlistTypePriorityChoicesCommand
        extends BaseSimpleCommand<GetWishlistTypePriorityChoicesForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("WishlistTypeName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("DefaultWishlistTypePriorityChoice", FieldType.ENTITY_NAME, false, null, null),
            new FieldDefinition("AllowNullChoice", FieldType.BOOLEAN, true, null, null)
        ));
    }
    
    /** Creates a new instance of GetWishlistTypePriorityChoicesCommand */
    public GetWishlistTypePriorityChoicesCommand(UserVisitPK userVisitPK, GetWishlistTypePriorityChoicesForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var wishlistControl = (WishlistControl)Session.getModelController(WishlistControl.class);
        GetWishlistTypePriorityChoicesResult result = WishlistResultFactory.getGetWishlistTypePriorityChoicesResult();
        String wishlistTypeName = form.getWishlistTypeName();
        WishlistType wishlistType = wishlistControl.getWishlistTypeByName(wishlistTypeName);
        
        if(wishlistType != null) {
            String defaultWishlistTypePriorityChoice = form.getDefaultWishlistTypePriorityChoice();
            boolean allowNullChoice = Boolean.parseBoolean(form.getAllowNullChoice());
            
            result.setWishlistTypePriorityChoices(wishlistControl.getWishlistTypePriorityChoices(defaultWishlistTypePriorityChoice,
                    getPreferredLanguage(), allowNullChoice, wishlistType));
        } else {
            addExecutionError(ExecutionErrors.UnknownWishlistTypeName.name(), wishlistTypeName);
        }
        
        return result;
    }
    
}
