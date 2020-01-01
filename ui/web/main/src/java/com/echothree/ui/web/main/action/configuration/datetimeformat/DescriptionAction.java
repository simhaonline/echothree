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

package com.echothree.ui.web.main.action.configuration.datetimeformat;

import com.echothree.control.user.party.common.PartyUtil;
import com.echothree.control.user.party.common.form.GetDateTimeFormatDescriptionsForm;
import com.echothree.control.user.party.common.result.GetDateTimeFormatDescriptionsResult;
import com.echothree.model.control.party.common.transfer.DateTimeFormatTransfer;
import com.echothree.ui.web.main.framework.AttributeConstants;
import com.echothree.ui.web.main.framework.ForwardConstants;
import com.echothree.ui.web.main.framework.MainBaseAction;
import com.echothree.ui.web.main.framework.ParameterConstants;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
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

@SproutAction(
    path = "/Configuration/DateTimeFormat/Description",
    mappingClass = SecureActionMapping.class,
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Display", path = "/configuration/datetimeformat/description.jsp")
    }
)
public class DescriptionAction
        extends MainBaseAction<ActionForm> {
    
    @Override
    public ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        String forwardKey = null;
        
        try {
            String dateTimeFormatName = request.getParameter(ParameterConstants.DATE_TIME_FORMAT_NAME);
            GetDateTimeFormatDescriptionsForm commandForm = PartyUtil.getHome().getGetDateTimeFormatDescriptionsForm();
            
            commandForm.setDateTimeFormatName(dateTimeFormatName);
            
            CommandResult commandResult = PartyUtil.getHome().getDateTimeFormatDescriptions(getUserVisitPK(request), commandForm);
            ExecutionResult executionResult = commandResult.getExecutionResult();
            GetDateTimeFormatDescriptionsResult result = (GetDateTimeFormatDescriptionsResult)executionResult.getResult();
            DateTimeFormatTransfer dateTimeFormatTransfer = result.getDateTimeFormat();
            
            request.setAttribute(AttributeConstants.DATE_TIME_FORMAT, dateTimeFormatTransfer);
            request.setAttribute(AttributeConstants.DATE_TIME_FORMAT_NAME, dateTimeFormatTransfer.getDateTimeFormatName());
            request.setAttribute(AttributeConstants.DATE_TIME_FORMAT_DESCRIPTIONS, result.getDateTimeFormatDescriptions());
            forwardKey = ForwardConstants.DISPLAY;
        } catch (NamingException ne) {
            forwardKey = ForwardConstants.ERROR_500;
        }
        
        return mapping.findForward(forwardKey);
    }
    
}