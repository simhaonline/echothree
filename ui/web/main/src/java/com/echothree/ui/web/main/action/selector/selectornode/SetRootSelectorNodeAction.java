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

package com.echothree.ui.web.main.action.selector.selectornode;

import com.echothree.control.user.selector.common.SelectorUtil;
import com.echothree.control.user.selector.common.form.SetRootSelectorNodeForm;
import com.echothree.ui.web.main.framework.ForwardConstants;
import com.echothree.ui.web.main.framework.MainBaseAction;
import com.echothree.ui.web.main.framework.ParameterConstants;
import com.echothree.view.client.web.struts.CustomActionForward;
import com.echothree.view.client.web.struts.sprout.annotation.SproutAction;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForward;
import com.echothree.view.client.web.struts.sprout.annotation.SproutProperty;
import com.echothree.view.client.web.struts.sslext.config.SecureActionMapping;
import java.util.HashMap;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

@SproutAction(
    path = "/Selector/SelectorNode/SetRootSelectorNode",
    mappingClass = SecureActionMapping.class,
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Display", path = "/action/Selector/SelectorNode/Main", redirect = true)
    }
)
public class SetRootSelectorNodeAction
        extends MainBaseAction<ActionForm> {
    
    @Override
    public ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String forwardKey = null;
        final String selectorKindName = request.getParameter(ParameterConstants.SELECTOR_KIND_NAME);
        final String selectorTypeName = request.getParameter(ParameterConstants.SELECTOR_TYPE_NAME);
        final String selectorName = request.getParameter(ParameterConstants.SELECTOR_NAME);
        
        try {
            String selectorNodeName = request.getParameter(ParameterConstants.SELECTOR_NODE_NAME);
            SetRootSelectorNodeForm commandForm = SelectorUtil.getHome().getSetRootSelectorNodeForm();
            
            commandForm.setSelectorKindName(selectorKindName);
            commandForm.setSelectorTypeName(selectorTypeName);
            commandForm.setSelectorName(selectorName);
            commandForm.setSelectorNodeName(selectorNodeName);
            
            SelectorUtil.getHome().setRootSelectorNode(getUserVisitPK(request), commandForm);
            
            forwardKey = ForwardConstants.DISPLAY;
        } catch (NamingException ne) {
            forwardKey = ForwardConstants.ERROR_500;
        }
        
        CustomActionForward customActionForward = new CustomActionForward(mapping.findForward(forwardKey));
        if(forwardKey.equals(ForwardConstants.DISPLAY)) {
            customActionForward.setParameters(new HashMap<String, String>(3) {{
                put(ParameterConstants.SELECTOR_KIND_NAME, selectorKindName);
                put(ParameterConstants.SELECTOR_TYPE_NAME, selectorTypeName);
                put(ParameterConstants.SELECTOR_NAME, selectorName);
            }});
        }
        
        return customActionForward;
    }
    
}