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

package com.echothree.control.user.comment.server.command;

import com.echothree.control.user.comment.common.form.CreateCommentForm;
import com.echothree.control.user.comment.common.result.CommentResultFactory;
import com.echothree.control.user.comment.common.result.CreateCommentResult;
import com.echothree.model.control.comment.server.CommentControl;
import com.echothree.model.control.core.common.EntityAttributeTypes;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.sequence.common.SequenceConstants;
import com.echothree.model.control.sequence.server.SequenceControl;
import com.echothree.model.control.user.server.UserControl;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.data.comment.server.entity.Comment;
import com.echothree.model.data.comment.server.entity.CommentType;
import com.echothree.model.data.core.server.entity.EntityAttributeType;
import com.echothree.model.data.core.server.entity.EntityInstance;
import com.echothree.model.data.core.server.entity.MimeType;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.sequence.server.entity.Sequence;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.user.server.entity.UserLogin;
import com.echothree.model.data.workflow.server.entity.WorkflowEntrance;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.persistence.BasePK;
import com.echothree.util.common.persistence.type.ByteArray;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateCommentCommand
        extends BaseSimpleCommand<CreateCommentForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("CommentedByUsername", FieldType.STRING, false, 1L, 80L),
                new FieldDefinition("EntityRef", FieldType.ENTITY_REF, true, null, null),
                new FieldDefinition("CommentTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L),
                new FieldDefinition("MimeTypeName", FieldType.MIME_TYPE, true, null, null),
                new FieldDefinition("WorkflowEntranceName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("ClobComment", FieldType.STRING, false, 1L, null),
                new FieldDefinition("StringComment", FieldType.STRING, false, 1L, 512L)
                // BlobComment is not validated
                ));
    }
    
    /** Creates a new instance of CreateCommentCommand */
    public CreateCommentCommand(UserVisitPK userVisitPK, CreateCommentForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    protected String createComment(CoreControl coreControl, CommentControl commentControl, CommentType commentType, EntityInstance commentedEntityInstance, Language language, MimeType mimeType,
            ByteArray blobComment, String clobComment, String stringComment) {
        var sequenceControl = (SequenceControl)Session.getModelController(SequenceControl.class);
        BasePK createdBy = getPartyPK();
        EntityInstance commentedByEntityInstance = null;
        String commentName = null;
        String commentedByUsername = form.getCommentedByUsername();
        String workflowEntranceName = form.getWorkflowEntranceName();
        WorkflowEntrance workflowEntrance = commentType.getLastDetail().getWorkflowEntrance();
        
        if(commentedByUsername != null) {
            UserControl userControl = getUserControl();
            UserLogin userLogin = userControl.getUserLoginByUsername(commentedByUsername);
            
            if(userLogin != null) {
                commentedByEntityInstance = coreControl.getEntityInstanceByBasePK(userLogin.getPartyPK());
            } else {
                addExecutionError(ExecutionErrors.UnknownRatedByUsername.name(), commentedByUsername);
            }
        } else {
            commentedByEntityInstance = coreControl.getEntityInstanceByBasePK(createdBy);
        }
        
        if(!hasExecutionErrors() && (workflowEntranceName != null && workflowEntrance != null)) {
            var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
            
            workflowEntrance = workflowControl.getWorkflowEntranceByName(workflowEntrance.getLastDetail().getWorkflow(),
                    workflowEntranceName);
            
            if(workflowEntrance == null) {
                addExecutionError(ExecutionErrors.UnknownWorkflowEntranceName.name(), workflowEntranceName);
            }
        }
        
        if(!hasExecutionErrors()) {
            String description = form.getDescription();
            Sequence commentSequence = commentType.getLastDetail().getCommentSequence();
            
            if(commentSequence == null) {
                commentSequence = sequenceControl.getDefaultSequenceUsingNames(SequenceConstants.SequenceType_COMMENT);
            }
            
            commentName = sequenceControl.getNextSequenceValue(commentSequence);
            
            Comment comment = commentControl.createComment(commentName, commentType, commentedEntityInstance,
                    commentedByEntityInstance, language == null? getPreferredLanguage(): language, description, mimeType, createdBy);
            
            if(blobComment != null) {
                commentControl.createCommentBlob(comment, blobComment, createdBy);
            } else if(clobComment != null) {
                commentControl.createCommentClob(comment, clobComment, createdBy);
            } else if(stringComment != null) {
                commentControl.createCommentString(comment, stringComment, createdBy);
            }
            
            if(workflowEntrance != null) {
                var workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
                EntityInstance entityInstance = coreControl.getEntityInstanceByBasePK(comment.getPrimaryKey());
                
                // TODO: WorkEffort should be created for addEntityToWorkflow
                workflowControl.addEntityToWorkflow(workflowEntrance, entityInstance, null, null, createdBy);
            }
        }
        
        return commentName;
    }
    
    @Override
    protected BaseResult execute() {
        CreateCommentResult result = CommentResultFactory.getCreateCommentResult();
        var coreControl = getCoreControl();
        String commentName = null;
        String entityRef = form.getEntityRef();
        EntityInstance commentedEntityInstance = coreControl.getEntityInstanceByEntityRef(entityRef);
        
        if(commentedEntityInstance != null) {
            var commentControl = (CommentControl)Session.getModelController(CommentControl.class);
            String commentTypeName = form.getCommentTypeName();
            CommentType commentType = commentControl.getCommentTypeByName(commentedEntityInstance.getEntityType(),
                    commentTypeName);
            
            if(commentType != null) {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                String languageIsoName = form.getLanguageIsoName();
                Language language = languageIsoName == null? null: partyControl.getLanguageByIsoName(languageIsoName);

                if(languageIsoName == null || language != null) {
                    String mimeTypeName = form.getMimeTypeName();

                    if(mimeTypeName == null) {
                        String commentString = form.getStringComment();

                        if(commentString != null) {
                            commentName = createComment(coreControl, commentControl, commentType, commentedEntityInstance, language, null, null,
                                    null, commentString);
                        } else {
                            addExecutionError(ExecutionErrors.MissingStringComment.name());
                        }
                    } else {
                        MimeType mimeType = coreControl.getMimeTypeByName(mimeTypeName);

                        if(mimeType != null) {
                            EntityAttributeType entityAttributeType = mimeType.getLastDetail().getEntityAttributeType();
                            String entityAttributeTypeName = entityAttributeType.getEntityAttributeTypeName();

                            if(entityAttributeTypeName.equals(EntityAttributeTypes.BLOB.name())) {
                                ByteArray blobComment = form.getBlobComment();

                                if(blobComment != null) {
                                    commentName = createComment(coreControl, commentControl, commentType, commentedEntityInstance,
                                            language, mimeType, blobComment, null, null);
                                } else {
                                    addExecutionError(ExecutionErrors.MissingBlobComment.name());
                                }
                            } else if(entityAttributeTypeName.equals(EntityAttributeTypes.CLOB.name())) {
                                String clobComment = form.getClobComment();

                                if(clobComment != null) {
                                    commentName = createComment(coreControl, commentControl, commentType, commentedEntityInstance,
                                            language, mimeType, null, clobComment, null);
                                } else {
                                    addExecutionError(ExecutionErrors.MissingClobComment.name());
                                }
                            } else {
                                addExecutionError(ExecutionErrors.UnknownEntityAttributeTypeName.name(), entityAttributeTypeName);
                            }
                        } else {
                            addExecutionError(ExecutionErrors.UnknownMimeTypeName.name(), mimeTypeName);
                        }
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnknownLanguageIsoName.name(), languageIsoName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownCommentTypeName.name(), commentTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownEntityRef.name(), entityRef);
        }
        
        result.setCommentName(commentName);
        
        return result;
    }
    
}
