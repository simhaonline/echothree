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

package com.echothree.model.control.comment.server.transfer;

import com.echothree.model.control.comment.common.transfer.CommentUsageTypeDescriptionTransfer;
import com.echothree.model.control.comment.common.transfer.CommentUsageTypeTransfer;
import com.echothree.model.control.comment.server.CommentControl;
import com.echothree.model.control.party.common.transfer.LanguageTransfer;
import com.echothree.model.data.comment.server.entity.CommentUsageTypeDescription;
import com.echothree.model.data.user.server.entity.UserVisit;

public class CommentUsageTypeDescriptionTransferCache
        extends BaseCommentDescriptionTransferCache<CommentUsageTypeDescription, CommentUsageTypeDescriptionTransfer> {
    
    /** Creates a new instance of CommentUsageTypeDescriptionTransferCache */
    public CommentUsageTypeDescriptionTransferCache(UserVisit userVisit, CommentControl commentControl) {
        super(userVisit, commentControl);
    }
    
    public CommentUsageTypeDescriptionTransfer getCommentUsageTypeDescriptionTransfer(CommentUsageTypeDescription commentUsageTypeDescription) {
        CommentUsageTypeDescriptionTransfer commentUsageTypeDescriptionTransfer = get(commentUsageTypeDescription);
        
        if(commentUsageTypeDescriptionTransfer == null) {
            CommentUsageTypeTransferCache commentUsageTypeTransferCache = commentControl.getCommentTransferCaches(userVisit).getCommentUsageTypeTransferCache();
            CommentUsageTypeTransfer commentUsageTypeTransfer = commentUsageTypeTransferCache.getCommentUsageTypeTransfer(commentUsageTypeDescription.getCommentUsageType());
            LanguageTransfer languageTransfer = partyControl.getLanguageTransfer(userVisit, commentUsageTypeDescription.getLanguage());
            
            commentUsageTypeDescriptionTransfer = new CommentUsageTypeDescriptionTransfer(languageTransfer, commentUsageTypeTransfer, commentUsageTypeDescription.getDescription());
            put(commentUsageTypeDescription, commentUsageTypeDescriptionTransfer);
        }
        
        return commentUsageTypeDescriptionTransfer;
    }
    
}
