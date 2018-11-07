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

package com.echothree.control.user.forum.server.command;

import com.echothree.control.user.forum.common.edit.ForumEditFactory;
import com.echothree.control.user.forum.common.edit.ForumMessageAttachmentEdit;
import com.echothree.control.user.forum.common.form.EditForumMessageAttachmentForm;
import com.echothree.control.user.forum.common.result.EditForumMessageAttachmentResult;
import com.echothree.control.user.forum.common.result.ForumResultFactory;
import com.echothree.control.user.forum.common.spec.ForumMessageAttachmentSpec;
import com.echothree.model.control.core.common.EntityAttributeTypes;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.forum.server.ForumControl;
import com.echothree.model.data.core.server.entity.MimeType;
import com.echothree.model.data.forum.server.entity.ForumMessage;
import com.echothree.model.data.forum.server.entity.ForumMessageAttachment;
import com.echothree.model.data.forum.server.entity.ForumMessageBlobAttachment;
import com.echothree.model.data.forum.server.entity.ForumMessageClobAttachment;
import com.echothree.model.data.forum.server.value.ForumMessageAttachmentDetailValue;
import com.echothree.model.data.forum.server.value.ForumMessageBlobAttachmentValue;
import com.echothree.model.data.forum.server.value.ForumMessageClobAttachmentValue;
import com.echothree.model.data.item.server.entity.Item;
import com.echothree.model.data.item.server.entity.ItemDescriptionType;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.common.persistence.type.ByteArray;
import com.echothree.util.server.control.BaseAbstractEditCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditForumMessageAttachmentCommand
        extends BaseAbstractEditCommand<ForumMessageAttachmentSpec, ForumMessageAttachmentEdit, EditForumMessageAttachmentResult, ForumMessageAttachment, ForumMessageAttachment> {
    
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ForumMessageName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("ForumMessageAttachmentSequence", FieldType.UNSIGNED_INTEGER, true, null, null)
                ));
        
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("MimeTypeName", FieldType.MIME_TYPE, true, null, null),
                new FieldDefinition("Clob", FieldType.STRING, false, 1L, null),
                new FieldDefinition("String", FieldType.STRING, false, 1L, 512L)
                ));
    }
    
    /** Creates a new instance of EditForumMessageAttachmentCommand */
    public EditForumMessageAttachmentCommand(UserVisitPK userVisitPK, EditForumMessageAttachmentForm form) {
        super(userVisitPK, form, null, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }

    @Override
    public EditForumMessageAttachmentResult getResult() {
        return ForumResultFactory.getEditForumMessageAttachmentResult();
    }

    @Override
    public ForumMessageAttachmentEdit getEdit() {
        return ForumEditFactory.getForumMessageAttachmentEdit();
    }

    ItemDescriptionType itemDescriptionType;
    Item item;

    @Override
    public ForumMessageAttachment getEntity(EditForumMessageAttachmentResult result) {
        ForumControl forumControl = (ForumControl)Session.getModelController(ForumControl.class);
        ForumMessageAttachment forumMessageAttachment = null;
        String forumMessageName = spec.getForumMessageName();
        ForumMessage forumMessage = forumControl.getForumMessageByNameForUpdate(forumMessageName);

        if(forumMessage != null) {
            Integer forumMessageAttachmentSequence = Integer.valueOf(spec.getForumMessageAttachmentSequence());

            if(editMode.equals(EditMode.LOCK) || editMode.equals(EditMode.ABANDON)) {
                forumMessageAttachment = forumControl.getForumMessageAttachmentBySequence(forumMessage, forumMessageAttachmentSequence);
            } else { // EditMode.UPDATE
                forumMessageAttachment = forumControl.getForumMessageAttachmentBySequenceForUpdate(forumMessage, forumMessageAttachmentSequence);
            }

            if(forumMessageAttachment == null) {
                addExecutionError(ExecutionErrors.UnknownForumMessageAttachment.name(), forumMessageName, forumMessageAttachmentSequence.toString());
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownForumMessageName.name(), forumMessageName);
        }

        return forumMessageAttachment;
    }

    @Override
    public ForumMessageAttachment getLockEntity(ForumMessageAttachment forumMessageAttachment) {
        return forumMessageAttachment;
    }

    @Override
    public void fillInResult(EditForumMessageAttachmentResult result, ForumMessageAttachment forumMessageAttachment) {
        ForumControl forumControl = (ForumControl)Session.getModelController(ForumControl.class);

        result.setForumMessageAttachment(forumControl.getForumMessageAttachmentTransfer(getUserVisit(), forumMessageAttachment));
    }

    MimeType mimeType;

    @Override
    public void doLock(ForumMessageAttachmentEdit edit, ForumMessageAttachment forumMessageAttachment) {
        ForumControl forumControl = (ForumControl)Session.getModelController(ForumControl.class);
        
        mimeType = forumMessageAttachment.getLastDetail().getMimeType();

        edit.setMimeTypeName(mimeType == null? null: mimeType.getLastDetail().getMimeTypeName());

        String entityAttributeTypeName = mimeType.getLastDetail().getEntityAttributeType().getEntityAttributeTypeName();

        // EntityAttributeTypes.BLOB.name() does not return anything in edit
        if(entityAttributeTypeName.equals(EntityAttributeTypes.CLOB.name())) {
            ForumMessageClobAttachment forumMessageAttachmentClob = forumControl.getForumMessageClobAttachment(forumMessageAttachment);

            if(forumMessageAttachmentClob != null) {
                edit.setClob(forumMessageAttachmentClob.getClob());
            }
        }
    }

    @Override
    public void canUpdate(ForumMessageAttachment forumMessageAttachment) {
        CoreControl coreControl = getCoreControl();
        String mimeTypeName = edit.getMimeTypeName();

        mimeType = coreControl.getMimeTypeByName(mimeTypeName);

        if(mimeType != null) {
            String entityAttributeTypeName = mimeType.getLastDetail().getEntityAttributeType().getEntityAttributeTypeName();
            ByteArray blob = edit.getBlob();
            String clob = edit.getClob();

            if(entityAttributeTypeName.equals(EntityAttributeTypes.BLOB.name()) && blob == null) {
                addExecutionError(ExecutionErrors.MissingBlob.name());
            } else {
                if(entityAttributeTypeName.equals(EntityAttributeTypes.CLOB.name()) && clob == null) {
                    addExecutionError(ExecutionErrors.MissingClob.name());
                }
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownMimeTypeName.name(), mimeTypeName);
        }
    }

    @Override
    public void doUpdate(ForumMessageAttachment forumMessageAttachment) {
        ForumControl forumControl = (ForumControl)Session.getModelController(ForumControl.class);
        ForumMessageAttachmentDetailValue forumMessageAttachmentDetailValue = forumControl.getForumMessageAttachmentDetailValueForUpdate(forumMessageAttachment);
        String entityAttributeTypeName = mimeType.getLastDetail().getEntityAttributeType().getEntityAttributeTypeName();
        PartyPK updatedBy = getPartyPK();

        forumMessageAttachmentDetailValue.setMimeTypePK(mimeType.getPrimaryKey());
        forumControl.updateForumMessageAttachmentFromValue(forumMessageAttachmentDetailValue, updatedBy);

        ForumMessageBlobAttachment forumMessageAttachmentBlob = forumControl.getForumMessageBlobAttachmentForUpdate(forumMessageAttachment);
        ForumMessageClobAttachment forumMessageAttachmentClob = forumControl.getForumMessageClobAttachmentForUpdate(forumMessageAttachment);

        if(entityAttributeTypeName.equals(EntityAttributeTypes.BLOB.name())) {
            ByteArray blob = edit.getBlob();

            if(forumMessageAttachmentClob != null) {
                forumControl.deleteForumMessageClobAttachment(forumMessageAttachmentClob, updatedBy);
            }

            if(forumMessageAttachmentBlob == null) {
                forumControl.createForumMessageBlobAttachment(forumMessageAttachment, blob, updatedBy);
            } else {
                ForumMessageBlobAttachmentValue forumMessageAttachmentBlobValue = forumControl.getForumMessageBlobAttachmentValue(forumMessageAttachmentBlob);

                forumMessageAttachmentBlobValue.setBlob(blob);
            }
        } else if(entityAttributeTypeName.equals(EntityAttributeTypes.CLOB.name())) {
            String clob = edit.getClob();
            
            if(forumMessageAttachmentBlob != null) {
                forumControl.deleteForumMessageBlobAttachment(forumMessageAttachmentBlob, updatedBy);
            }

            if(forumMessageAttachmentClob == null) {
                forumControl.createForumMessageClobAttachment(forumMessageAttachment, clob, updatedBy);
            } else {
                ForumMessageClobAttachmentValue forumMessageAttachmentClobValue = forumControl.getForumMessageClobAttachmentValue(forumMessageAttachmentClob);

                forumMessageAttachmentClobValue.setClob(clob);
            }
        }
    }
    
}
