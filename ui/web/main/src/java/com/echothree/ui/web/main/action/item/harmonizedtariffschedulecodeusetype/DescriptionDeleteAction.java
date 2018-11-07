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

package com.echothree.ui.web.main.action.item.harmonizedtariffschedulecodeusetype;

import com.echothree.control.user.item.common.ItemUtil;
import com.echothree.control.user.item.common.form.DeleteHarmonizedTariffScheduleCodeUseTypeDescriptionForm;
import com.echothree.control.user.item.common.form.GetHarmonizedTariffScheduleCodeUseTypeDescriptionForm;
import com.echothree.control.user.item.common.result.GetHarmonizedTariffScheduleCodeUseTypeDescriptionResult;
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
    path = "/Item/HarmonizedTariffScheduleCodeUseType/DescriptionDelete",
    mappingClass = SecureActionMapping.class,
    name = "HarmonizedTariffScheduleCodeUseTypeDescriptionDelete",
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Display", path = "/action/Item/HarmonizedTariffScheduleCodeUseType/Description", redirect = true),
        @SproutForward(name = "Form", path = "/item/harmonizedtariffschedulecodeusetype/descriptionDelete.jsp")
    }
)
public class DescriptionDeleteAction
        extends MainBaseDeleteAction<DescriptionDeleteActionForm> {

    @Override
    public String getEntityTypeName() {
        return EntityTypes.HarmonizedTariffScheduleCodeUseTypeDescription.name();
    }
    
    @Override
    public void setupParameters(DescriptionDeleteActionForm actionForm, HttpServletRequest request) {
        actionForm.setHarmonizedTariffScheduleCodeUseTypeName(findParameter(request, ParameterConstants.HARMONIZED_TARIFF_SCHEDULE_CODE_USE_TYPE_NAME, actionForm.getHarmonizedTariffScheduleCodeUseTypeName()));
        actionForm.setLanguageIsoName(findParameter(request, ParameterConstants.LANGUAGE_ISO_NAME, actionForm.getLanguageIsoName()));
    }
    
    @Override
    public void setupTransfer(DescriptionDeleteActionForm actionForm, HttpServletRequest request)
            throws NamingException {
        GetHarmonizedTariffScheduleCodeUseTypeDescriptionForm commandForm = ItemUtil.getHome().getGetHarmonizedTariffScheduleCodeUseTypeDescriptionForm();
        
        commandForm.setHarmonizedTariffScheduleCodeUseTypeName(actionForm.getHarmonizedTariffScheduleCodeUseTypeName());
        commandForm.setLanguageIsoName(actionForm.getLanguageIsoName());
        
        CommandResult commandResult = ItemUtil.getHome().getHarmonizedTariffScheduleCodeUseTypeDescription(getUserVisitPK(request), commandForm);
        if(!commandResult.hasErrors()) {
            ExecutionResult executionResult = commandResult.getExecutionResult();
            GetHarmonizedTariffScheduleCodeUseTypeDescriptionResult result = (GetHarmonizedTariffScheduleCodeUseTypeDescriptionResult)executionResult.getResult();

            request.setAttribute(AttributeConstants.HARMONIZED_TARIFF_SCHEDULE_CODE_USE_TYPE_DESCRIPTION, result.getHarmonizedTariffScheduleCodeUseTypeDescription());
        }
    }
    
    @Override
    public CommandResult doDelete(DescriptionDeleteActionForm actionForm, HttpServletRequest request)
            throws NamingException {
        DeleteHarmonizedTariffScheduleCodeUseTypeDescriptionForm commandForm = ItemUtil.getHome().getDeleteHarmonizedTariffScheduleCodeUseTypeDescriptionForm();

        commandForm.setHarmonizedTariffScheduleCodeUseTypeName(actionForm.getHarmonizedTariffScheduleCodeUseTypeName());
        commandForm.setLanguageIsoName(actionForm.getLanguageIsoName());

        return ItemUtil.getHome().deleteHarmonizedTariffScheduleCodeUseTypeDescription(getUserVisitPK(request), commandForm);
    }
    
    @Override
    public void setupForwardParameters(DescriptionDeleteActionForm actionForm, Map<String, String> parameters) {
        parameters.put(ParameterConstants.HARMONIZED_TARIFF_SCHEDULE_CODE_USE_TYPE_NAME, actionForm.getHarmonizedTariffScheduleCodeUseTypeName());
    }
    
}
