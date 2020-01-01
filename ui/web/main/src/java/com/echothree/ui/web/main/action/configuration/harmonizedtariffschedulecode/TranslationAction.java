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

package com.echothree.ui.web.main.action.configuration.harmonizedtariffschedulecode;

import com.echothree.control.user.item.common.ItemUtil;
import com.echothree.control.user.item.common.form.GetHarmonizedTariffScheduleCodeTranslationsForm;
import com.echothree.control.user.item.common.result.GetHarmonizedTariffScheduleCodeTranslationsResult;
import com.echothree.model.control.item.common.transfer.HarmonizedTariffScheduleCodeTransfer;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

@SproutAction(
    path = "/Configuration/HarmonizedTariffScheduleCode/Translation",
    mappingClass = SecureActionMapping.class,
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Display", path = "/configuration/harmonizedtariffschedulecode/translation.jsp")
    }
)
public class TranslationAction
        extends MainBaseAction<ActionForm> {

    @Override
    public ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String forwardKey;
        GetHarmonizedTariffScheduleCodeTranslationsForm commandForm = ItemUtil.getHome().getGetHarmonizedTariffScheduleCodeTranslationsForm();

        commandForm.setCountryName(request.getParameter(ParameterConstants.COUNTRY_NAME));
        commandForm.setHarmonizedTariffScheduleCodeName(request.getParameter(ParameterConstants.HARMONIZED_TARIFF_SCHEDULE_CODE_NAME));

        CommandResult commandResult = ItemUtil.getHome().getHarmonizedTariffScheduleCodeTranslations(getUserVisitPK(request), commandForm);
        GetHarmonizedTariffScheduleCodeTranslationsResult result = null;
        HarmonizedTariffScheduleCodeTransfer harmonizedTariffScheduleCode = null;
        
        if(!commandResult.hasErrors()) {
            ExecutionResult executionResult = commandResult.getExecutionResult();
            
            result = (GetHarmonizedTariffScheduleCodeTranslationsResult) executionResult.getResult();
            harmonizedTariffScheduleCode = result.getHarmonizedTariffScheduleCode();
        }
        
        if(harmonizedTariffScheduleCode == null) {
            forwardKey = ForwardConstants.ERROR_404;
        } else {
            request.setAttribute(AttributeConstants.HARMONIZED_TARIFF_SCHEDULE_CODE, harmonizedTariffScheduleCode);
            request.setAttribute(AttributeConstants.HARMONIZED_TARIFF_SCHEDULE_CODE_TRANSLATIONS, result.getHarmonizedTariffScheduleCodeTranslations());
            forwardKey = ForwardConstants.DISPLAY;
        }

        return mapping.findForward(forwardKey);
    }
    
}
