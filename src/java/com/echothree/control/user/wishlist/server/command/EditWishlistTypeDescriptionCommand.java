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

package com.echothree.control.user.wishlist.server.command;

import com.echothree.control.user.wishlist.common.edit.WishlistEditFactory;
import com.echothree.control.user.wishlist.common.edit.WishlistTypeDescriptionEdit;
import com.echothree.control.user.wishlist.common.form.EditWishlistTypeDescriptionForm;
import com.echothree.control.user.wishlist.common.result.EditWishlistTypeDescriptionResult;
import com.echothree.control.user.wishlist.common.result.WishlistResultFactory;
import com.echothree.control.user.wishlist.common.spec.WishlistTypeDescriptionSpec;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.wishlist.server.WishlistControl;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.wishlist.server.entity.WishlistType;
import com.echothree.model.data.wishlist.server.entity.WishlistTypeDescription;
import com.echothree.model.data.wishlist.server.value.WishlistTypeDescriptionValue;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseEditCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditWishlistTypeDescriptionCommand
        extends BaseEditCommand<WishlistTypeDescriptionSpec, WishlistTypeDescriptionEdit> {
    
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("WishlistTypeName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, true, null, null)
        ));
        
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("Description", FieldType.STRING, true, 1L, 80L)
        ));
    }
    
    /** Creates a new instance of EditWishlistTypeDescriptionCommand */
    public EditWishlistTypeDescriptionCommand(UserVisitPK userVisitPK, EditWishlistTypeDescriptionForm form) {
        super(userVisitPK, form, null, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    protected BaseResult execute() {
        WishlistControl wishlistControl = (WishlistControl)Session.getModelController(WishlistControl.class);
        EditWishlistTypeDescriptionResult result = WishlistResultFactory.getEditWishlistTypeDescriptionResult();
        String wishlistTypeName = spec.getWishlistTypeName();
        WishlistType wishlistType = wishlistControl.getWishlistTypeByName(wishlistTypeName);
        
        if(wishlistType != null) {
            PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
            String languageIsoName = spec.getLanguageIsoName();
            Language language = partyControl.getLanguageByIsoName(languageIsoName);
            
            if(language != null) {
                if(editMode.equals(EditMode.LOCK)) {
                    WishlistTypeDescription wishlistTypeDescription = wishlistControl.getWishlistTypeDescription(wishlistType, language);
                    
                    if(wishlistTypeDescription != null) {
                        result.setWishlistTypeDescription(wishlistControl.getWishlistTypeDescriptionTransfer(getUserVisit(), wishlistTypeDescription));
                        
                        if(lockEntity(wishlistType)) {
                            WishlistTypeDescriptionEdit edit = WishlistEditFactory.getWishlistTypeDescriptionEdit();
                            
                            result.setEdit(edit);
                            edit.setDescription(wishlistTypeDescription.getDescription());
                        } else {
                            addExecutionError(ExecutionErrors.EntityLockFailed.name());
                        }
                        
                        result.setEntityLock(getEntityLockTransfer(wishlistType));
                    } else {
                        addExecutionError(ExecutionErrors.UnknownWishlistTypeDescription.name());
                    }
                } else if(editMode.equals(EditMode.UPDATE)) {
                    WishlistTypeDescriptionValue wishlistTypeDescriptionValue = wishlistControl.getWishlistTypeDescriptionValueForUpdate(wishlistType, language);
                    
                    if(wishlistTypeDescriptionValue != null) {
                        if(lockEntityForUpdate(wishlistType)) {
                            try {
                                String description = edit.getDescription();
                                
                                wishlistTypeDescriptionValue.setDescription(description);
                                
                                wishlistControl.updateWishlistTypeDescriptionFromValue(wishlistTypeDescriptionValue, getPartyPK());
                            } finally {
                                unlockEntity(wishlistType);
                            }
                        } else {
                            addExecutionError(ExecutionErrors.EntityLockStale.name());
                        }
                    } else {
                        addExecutionError(ExecutionErrors.UnknownWishlistTypeDescription.name());
                    }
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownLanguageIsoName.name(), languageIsoName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownWishlistTypeName.name(), wishlistTypeName);
        }
        
        return result;
    }
    
}
