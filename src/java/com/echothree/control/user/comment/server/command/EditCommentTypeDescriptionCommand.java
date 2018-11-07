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

package com.echothree.control.user.comment.server.command;

import com.echothree.control.user.comment.common.edit.CommentEditFactory;
import com.echothree.control.user.comment.common.edit.CommentTypeDescriptionEdit;
import com.echothree.control.user.comment.common.form.EditCommentTypeDescriptionForm;
import com.echothree.control.user.comment.common.result.CommentResultFactory;
import com.echothree.control.user.comment.common.result.EditCommentTypeDescriptionResult;
import com.echothree.control.user.comment.common.spec.CommentTypeDescriptionSpec;
import com.echothree.model.control.comment.server.CommentControl;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.data.comment.server.entity.CommentType;
import com.echothree.model.data.comment.server.entity.CommentTypeDescription;
import com.echothree.model.data.comment.server.value.CommentTypeDescriptionValue;
import com.echothree.model.data.core.server.entity.ComponentVendor;
import com.echothree.model.data.core.server.entity.EntityType;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.user.common.pk.UserVisitPK;
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

public class EditCommentTypeDescriptionCommand
        extends BaseEditCommand<CommentTypeDescriptionSpec, CommentTypeDescriptionEdit> {
    
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ComponentVendorName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("EntityTypeName", FieldType.ENTITY_TYPE_NAME, true, null, null),
                new FieldDefinition("CommentTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, true, null, null)
                ));
        
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("Description", FieldType.STRING, true, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of EditCommentTypeDescriptionCommand */
    public EditCommentTypeDescriptionCommand(UserVisitPK userVisitPK, EditCommentTypeDescriptionForm form) {
        super(userVisitPK, form, null, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    protected BaseResult execute() {
        CoreControl coreControl = getCoreControl();
        EditCommentTypeDescriptionResult result = CommentResultFactory.getEditCommentTypeDescriptionResult();
        String componentVendorName = spec.getComponentVendorName();
        ComponentVendor componentVendor = coreControl.getComponentVendorByName(componentVendorName);
        
        if(componentVendor != null) {
            String entityTypeName = spec.getEntityTypeName();
            EntityType entityType = coreControl.getEntityTypeByName(componentVendor, entityTypeName);
            
            if(entityType != null) {
                CommentControl commentControl = (CommentControl)Session.getModelController(CommentControl.class);
                String commentTypeName = spec.getCommentTypeName();
                CommentType commentType = commentControl.getCommentTypeByName(entityType, commentTypeName);
                
                if(commentType != null) {
                    PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                    String languageIsoName = spec.getLanguageIsoName();
                    Language language = partyControl.getLanguageByIsoName(languageIsoName);
                    
                    if(language != null) {
                        if(editMode.equals(EditMode.LOCK)) {
                            CommentTypeDescription commentTypeDescription = commentControl.getCommentTypeDescription(commentType, language);
                            
                            if(commentTypeDescription != null) {
                                result.setCommentTypeDescription(commentControl.getCommentTypeDescriptionTransfer(getUserVisit(), commentTypeDescription));
                                
                                if(lockEntity(commentType)) {
                                    CommentTypeDescriptionEdit edit = CommentEditFactory.getCommentTypeDescriptionEdit();
                                    
                                    result.setEdit(edit);
                                    edit.setDescription(commentTypeDescription.getDescription());
                                } else {
                                    addExecutionError(ExecutionErrors.EntityLockFailed.name());
                                }
                                
                                result.setEntityLock(getEntityLockTransfer(commentType));
                            } else {
                                addExecutionError(ExecutionErrors.UnknownCommentTypeDescription.name());
                            }
                        } else if(editMode.equals(EditMode.UPDATE)) {
                            CommentTypeDescriptionValue commentTypeDescriptionValue = commentControl.getCommentTypeDescriptionValueForUpdate(commentType, language);
                            
                            if(commentTypeDescriptionValue != null) {
                                if(lockEntityForUpdate(commentType)) {
                                    try {
                                        String description = edit.getDescription();
                                        
                                        commentTypeDescriptionValue.setDescription(description);
                                        
                                        commentControl.updateCommentTypeDescriptionFromValue(commentTypeDescriptionValue, getPartyPK());
                                    } finally {
                                        unlockEntity(commentType);
                                    }
                                } else {
                                    addExecutionError(ExecutionErrors.EntityLockStale.name());
                                }
                            } else {
                                addExecutionError(ExecutionErrors.UnknownCommentTypeDescription.name());
                            }
                        }
                    } else {
                        addExecutionError(ExecutionErrors.UnknownLanguageIsoName.name(), languageIsoName);
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnknownCommentTypeName.name(), commentTypeName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownEntityTypeName.name(), entityTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownComponentVendorName.name(), componentVendorName);
        }
        
        return result;
    }
    
}
