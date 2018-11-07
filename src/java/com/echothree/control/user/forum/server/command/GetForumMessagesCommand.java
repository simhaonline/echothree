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

import com.echothree.control.user.forum.common.form.GetForumMessagesForm;
import com.echothree.control.user.forum.common.result.ForumResultFactory;
import com.echothree.control.user.forum.common.result.GetForumMessagesResult;
import com.echothree.model.control.core.common.ComponentVendors;
import com.echothree.model.control.core.common.EntityTypes;
import com.echothree.model.control.core.server.logic.EntityInstanceLogic;
import com.echothree.model.control.forum.common.ForumConstants;
import com.echothree.model.control.forum.server.ForumControl;
import com.echothree.model.control.forum.server.logic.ForumLogic;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.data.core.server.entity.EntityInstance;
import com.echothree.model.data.forum.server.entity.ForumThread;
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

public class GetForumMessagesCommand
        extends BaseSimpleCommand<GetForumMessagesForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("ForumThreadName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("EntityRef", FieldType.ENTITY_REF, false, null, null),
                new FieldDefinition("Key", FieldType.KEY, false, null, null),
                new FieldDefinition("Guid", FieldType.GUID, false, null, null),
                new FieldDefinition("Ulid", FieldType.ULID, false, null, null)
                ));
    }
    
    /** Creates a new instance of GetForumMessagesCommand */
    public GetForumMessagesCommand(UserVisitPK userVisitPK, GetForumMessagesForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        GetForumMessagesResult result = ForumResultFactory.getGetForumMessagesResult();
        String forumThreadName = form.getForumThreadName();
        int parameterCount = (forumThreadName == null ? 0 : 1) + EntityInstanceLogic.getInstance().countPossibleEntitySpecs(form);

        if(parameterCount == 1) {
            ForumControl forumControl = (ForumControl)Session.getModelController(ForumControl.class);
            ForumThread forumThread = null;

            if(forumThreadName == null) {
                EntityInstance entityInstance = EntityInstanceLogic.getInstance().getEntityInstance(this, form, ComponentVendors.ECHOTHREE.name(),
                        EntityTypes.ForumThread.name());
                
                if(!hasExecutionErrors()) {
                    forumThread = forumControl.getForumThreadByEntityInstance(entityInstance);
                }
            } else {
                forumThread = forumControl.getForumThreadByName(forumThreadName);

                if(forumThread == null) {
                    addExecutionError(ExecutionErrors.UnknownForumThreadName.name(), forumThreadName);
                }
            }

            if(!hasExecutionErrors()) {
                if(forumThread.getLastDetail().getPostedTime() <= session.START_TIME
                        || (getParty() == null ? false : getPartyTypeName().equals(PartyConstants.PartyType_EMPLOYEE))) {
                    if(form.getKey() != null || ForumLogic.getInstance().isForumRoleTypePermitted(this, forumThread, getParty(), ForumConstants.ForumRoleType_READER)) {
                        result.setForumMessages(forumControl.getForumMessageTransfersByForumThread(getUserVisit(), forumThread));
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnpublishedForumThread.name(), forumThread.getLastDetail().getForumThreadName());
                }
            }
        } else {
            addExecutionError(ExecutionErrors.InvalidParameterCount.name());
        }
        
        return result;
    }
    
}
