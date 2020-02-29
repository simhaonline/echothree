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

package com.echothree.control.user.forum.server.command;

import com.echothree.control.user.forum.common.form.GetForumMessageAttachmentForm;
import com.echothree.control.user.forum.common.result.ForumResultFactory;
import com.echothree.control.user.forum.common.result.GetForumMessageAttachmentResult;
import com.echothree.model.control.content.server.logic.ContentLogic;
import com.echothree.model.control.forum.common.ForumConstants;
import com.echothree.model.control.forum.server.ForumControl;
import com.echothree.model.control.forum.server.logic.ForumLogic;
import com.echothree.model.data.forum.server.entity.ForumMessage;
import com.echothree.model.data.forum.server.entity.ForumMessageAttachment;
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

public class GetForumMessageAttachmentCommand
        extends BaseSimpleCommand<GetForumMessageAttachmentForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ForumMessageName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("ForumMessageAttachmentSequence", FieldType.UNSIGNED_INTEGER, true, null, null),
                new FieldDefinition("Referrer", FieldType.URL, false, null, null)
                ));
    }
    
    /** Creates a new instance of GetForumMessageAttachmentCommand */
    public GetForumMessageAttachmentCommand(UserVisitPK userVisitPK, GetForumMessageAttachmentForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        GetForumMessageAttachmentResult result = ForumResultFactory.getGetForumMessageAttachmentResult();
        ContentLogic.getInstance().checkReferrer(this, form.getReferrer());

        if(!hasExecutionErrors()) {
            var forumControl = (ForumControl)Session.getModelController(ForumControl.class);
            String forumMessageName = form.getForumMessageName();
            ForumMessage forumMessage = forumControl.getForumMessageByNameForUpdate(forumMessageName);

            if(forumMessage != null) {
                if(ForumLogic.getInstance().isForumRoleTypePermitted(this, forumMessage, getParty(), ForumConstants.ForumRoleType_READER)) {
                    Integer forumMessageAttachmentSequence = Integer.valueOf(form.getForumMessageAttachmentSequence());
                    ForumMessageAttachment forumMessageAttachment = forumControl.getForumMessageAttachmentBySequence(forumMessage, forumMessageAttachmentSequence);

                    if(forumMessageAttachment != null) {
                        result.setForumMessageAttachment(forumControl.getForumMessageAttachmentTransfer(getUserVisit(), forumMessageAttachment));
                    } else {
                        addExecutionError(ExecutionErrors.UnknownForumMessageAttachment.name(), forumMessageName, forumMessageAttachmentSequence.toString());
                    }
                } else {
                    addExecutionError(ExecutionErrors.MissingRequiredForumRoleType.name(), ForumConstants.ForumRoleType_READER);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownForumMessageName.name(), forumMessageName);
            }
        }

        return result;
    }
    
}
