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

package com.echothree.ui.web.main.action.core.entityblobattribute;

import com.echothree.control.user.core.common.CoreUtil;
import com.echothree.control.user.core.common.edit.EntityBlobAttributeEdit;
import com.echothree.control.user.core.common.form.EditEntityBlobAttributeForm;
import com.echothree.control.user.core.common.result.EditEntityBlobAttributeResult;
import com.echothree.control.user.core.common.spec.EntityBlobAttributeSpec;
import com.echothree.ui.web.main.framework.AttributeConstants;
import com.echothree.ui.web.main.framework.ForwardConstants;
import com.echothree.ui.web.main.framework.MainBaseAction;
import com.echothree.ui.web.main.framework.ParameterConstants;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.util.common.persistence.type.ByteArray;
import com.echothree.view.client.web.struts.sprout.annotation.SproutAction;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForward;
import com.echothree.view.client.web.struts.sprout.annotation.SproutProperty;
import com.echothree.view.client.web.struts.sslext.config.SecureActionMapping;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

@SproutAction(
    path = "/Core/EntityBlobAttribute/Edit",
    mappingClass = SecureActionMapping.class,
    name = "EntityBlobAttributeEdit",
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Form", path = "/core/entityblobattribute/edit.jsp")
    }
)
public class EditAction
        extends MainBaseAction<ActionForm> {
    
    @Override
    public ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String forwardKey = null;
        String returnUrl = request.getParameter(ParameterConstants.RETURN_URL);
        
        try {
            String entityRef = request.getParameter(ParameterConstants.ENTITY_REF);
            String entityAttributeName = request.getParameter(ParameterConstants.ENTITY_ATTRIBUTE_NAME);
            String languageIsoName = request.getParameter(ParameterConstants.LANGUAGE_ISO_NAME);
            
            if(forwardKey == null) {
                EditActionForm actionForm = (EditActionForm)form;
                EditEntityBlobAttributeForm commandForm = CoreUtil.getHome().getEditEntityBlobAttributeForm();
                EntityBlobAttributeSpec spec = CoreUtil.getHome().getEntityBlobAttributeSpec();
                FormFile blobAttribute = actionForm.getBlobAttribute();
                
                if(entityRef == null)
                    entityRef = actionForm.getEntityRef();
                if(entityAttributeName == null)
                    entityAttributeName = actionForm.getEntityAttributeName();
                if(returnUrl == null)
                    returnUrl = actionForm.getReturnUrl();
                if(languageIsoName == null)
                    languageIsoName = actionForm.getLanguageIsoName();
                
                commandForm.setSpec(spec);
                spec.setEntityRef(entityRef);
                spec.setEntityAttributeName(entityAttributeName);
                spec.setLanguageIsoName(languageIsoName);
                
                if(wasPost(request)) {
                    EntityBlobAttributeEdit edit = CoreUtil.getHome().getEntityBlobAttributeEdit();
                    
                    commandForm.setEditMode(EditMode.UPDATE);
                    commandForm.setEdit(edit);
                    
                    edit.setMimeTypeName(blobAttribute.getContentType());
                    edit.setBlobAttribute(new ByteArray(blobAttribute.getFileData()));
                    
                    CommandResult commandResult = CoreUtil.getHome().editEntityBlobAttribute(getUserVisitPK(request), commandForm);
                    
                    if(commandResult.hasErrors()) {
                        ExecutionResult executionResult = commandResult.getExecutionResult();
                        
                        if(executionResult != null) {
                            EditEntityBlobAttributeResult result = (EditEntityBlobAttributeResult)executionResult.getResult();
                            
                            if(result != null) {
                                request.setAttribute(AttributeConstants.ENTITY_LOCK, result.getEntityLock());
                            }
                        }
                        
                        setCommandResultAttribute(request, commandResult);
                        
                        forwardKey = ForwardConstants.FORM;
                    }
                } else {
                    commandForm.setEditMode(EditMode.LOCK);
                    
                    CommandResult commandResult = CoreUtil.getHome().editEntityBlobAttribute(getUserVisitPK(request), commandForm);
                    ExecutionResult executionResult = commandResult.getExecutionResult();
                    EditEntityBlobAttributeResult result = (EditEntityBlobAttributeResult)executionResult.getResult();
                    
                    if(result != null) {
                        EntityBlobAttributeEdit edit = result.getEdit();
                        
                        if(edit != null) {
                            actionForm.setEntityRef(entityRef);
                            actionForm.setEntityAttributeName(entityAttributeName);
                            actionForm.setReturnUrl(returnUrl);
                            actionForm.setLanguageIsoName(languageIsoName);
                        }
                        
                        request.setAttribute(AttributeConstants.ENTITY_LOCK, result.getEntityLock());
                    }
                    
                    setCommandResultAttribute(request, commandResult);
                    
                    forwardKey = ForwardConstants.FORM;
                }
            }
        } catch (NamingException ne) {
            forwardKey = ForwardConstants.ERROR_500;
        }
        
        return forwardKey == null ? new ActionForward(returnUrl, true) : mapping.findForward(forwardKey);
    }
    
}
