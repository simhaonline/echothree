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

package com.echothree.ui.web.main.action.customer.customer;

import com.echothree.control.user.comment.common.CommentUtil;
import com.echothree.control.user.comment.common.form.DeleteCommentForm;
import com.echothree.control.user.comment.common.form.GetCommentForm;
import com.echothree.control.user.comment.common.result.GetCommentResult;
import com.echothree.control.user.customer.common.CustomerUtil;
import com.echothree.control.user.customer.common.form.GetCustomerForm;
import com.echothree.control.user.customer.common.result.GetCustomerResult;
import com.echothree.model.control.core.common.EntityTypes;
import com.echothree.ui.web.main.framework.AttributeConstants;
import com.echothree.ui.web.main.framework.MainBaseDeleteAction;
import com.echothree.ui.web.main.framework.ParameterConstants;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.sprout.annotation.SproutAction;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForward;
import com.echothree.view.client.web.struts.sprout.annotation.SproutProperty;
import com.echothree.view.client.web.struts.sslext.config.SecureActionMapping;
import java.util.Map;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

@SproutAction(
    path = "/Customer/Customer/CommentDelete",
    mappingClass = SecureActionMapping.class,
    name = "CustomerCommentDelete",
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Display", path = "/action/Customer/Customer/Review", redirect = true),
        @SproutForward(name = "Form", path = "/customer/customer/commentDelete.jsp")
    }
)
public class CommentDeleteAction
        extends MainBaseDeleteAction<CommentDeleteActionForm> {
    
    @Override
    public String getEntityTypeName() {
        return EntityTypes.Comment.name();
    }
    
    @Override
    public void setupParameters(CommentDeleteActionForm actionForm, HttpServletRequest request) {
        actionForm.setPartyName(findParameter(request, ParameterConstants.PARTY_NAME, actionForm.getPartyName()));
        actionForm.setCommentName(findParameter(request, ParameterConstants.COMMENT_NAME, actionForm.getCommentName()));
    }
    
    public void setupCustomerTransfer(CommentDeleteActionForm actionForm, HttpServletRequest request)
            throws NamingException {
        GetCustomerForm commandForm = CustomerUtil.getHome().getGetCustomerForm();
        
        commandForm.setPartyName(actionForm.getPartyName());
        
        CommandResult commandResult = CustomerUtil.getHome().getCustomer(getUserVisitPK(request), commandForm);
        ExecutionResult executionResult = commandResult.getExecutionResult();
        GetCustomerResult result = (GetCustomerResult)executionResult.getResult();
        
        request.setAttribute(AttributeConstants.CUSTOMER, result.getCustomer());
    }
    
    public void setupCommentTransfer(CommentDeleteActionForm actionForm, HttpServletRequest request)
            throws NamingException {
        GetCommentForm commandForm = CommentUtil.getHome().getGetCommentForm();
        
        commandForm.setCommentName(actionForm.getCommentName());

        CommandResult commandResult = CommentUtil.getHome().getComment(getUserVisitPK(request), commandForm);
        ExecutionResult executionResult = commandResult.getExecutionResult();
        GetCommentResult result = (GetCommentResult)executionResult.getResult();
        
        request.setAttribute(AttributeConstants.COMMENT, result.getComment());
    }
    
    @Override
    public void setupTransfer(CommentDeleteActionForm actionForm, HttpServletRequest request)
            throws NamingException {
        setupCustomerTransfer(actionForm, request);
        setupCommentTransfer(actionForm, request);
    }
    
    @Override
    public CommandResult doDelete(CommentDeleteActionForm actionForm, HttpServletRequest request)
            throws NamingException {
        DeleteCommentForm commandForm = CommentUtil.getHome().getDeleteCommentForm();
        
        commandForm.setCommentName(actionForm.getCommentName());

        return CommentUtil.getHome().deleteComment(getUserVisitPK(request), commandForm);
    }
    
    @Override
    public void setupForwardParameters(CommentDeleteActionForm actionForm, Map<String, String> parameters) {
        parameters.put(ParameterConstants.PARTY_NAME, actionForm.getPartyName());
    }
    
}
