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

package com.echothree.ui.web.main.action.item.item;

import com.echothree.control.user.comment.common.CommentUtil;
import com.echothree.control.user.comment.common.form.GetCommentStatusChoicesForm;
import com.echothree.control.user.comment.common.result.GetCommentStatusChoicesResult;
import com.echothree.model.control.comment.common.choice.CommentStatusChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BaseActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="ItemCommentStatus")
public class CommentStatusActionForm
        extends BaseActionForm {
    
    private CommentStatusChoicesBean commentStatusChoices;
    
    private String itemName;
    private String commentName;
    private String commentStatusChoice;
    
    public void setupCommentStatusChoices() {
        if(commentStatusChoices == null) {
            try {
                GetCommentStatusChoicesForm form = CommentUtil.getHome().getGetCommentStatusChoicesForm();
                
                form.setCommentName(commentName);
                form.setDefaultCommentStatusChoice(commentStatusChoice);
                form.setAllowNullChoice(Boolean.FALSE.toString());
                
                CommandResult commandResult = CommentUtil.getHome().getCommentStatusChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetCommentStatusChoicesResult result = (GetCommentStatusChoicesResult)executionResult.getResult();
                commentStatusChoices = result.getCommentStatusChoices();
                
                if(commentStatusChoice == null)
                    commentStatusChoice = commentStatusChoices.getDefaultValue();
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, commentStatusChoices remains null, no default
            }
        }
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getCommentName() {
        return commentName;
    }
    
    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }
    
    public String getCommentStatusChoice() {
        return commentStatusChoice;
    }
    
    public void setCommentStatusChoice(String commentStatusChoice) {
        this.commentStatusChoice = commentStatusChoice;
    }
    
    public List<LabelValueBean> getCommentStatusChoices() {
        List<LabelValueBean> choices = null;
        
        setupCommentStatusChoices();
        if(commentStatusChoices != null)
            choices = convertChoices(commentStatusChoices);
        
        return choices;
    }
    
}
