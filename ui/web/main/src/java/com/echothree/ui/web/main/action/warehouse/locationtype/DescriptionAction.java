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

package com.echothree.ui.web.main.action.warehouse.locationtype;

import com.echothree.control.user.warehouse.common.WarehouseUtil;
import com.echothree.control.user.warehouse.common.form.GetLocationTypeDescriptionsForm;
import com.echothree.control.user.warehouse.common.result.GetLocationTypeDescriptionsResult;
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
    path = "/Warehouse/LocationType/Description",
    mappingClass = SecureActionMapping.class,
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Display", path = "/warehouse/locationtype/description.jsp")
    }
)
public class DescriptionAction
        extends MainBaseAction<ActionForm> {
    
    @Override
    public ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        String forwardKey = null;
        
        try {
            String warehouseName = request.getParameter(ParameterConstants.WAREHOUSE_NAME);
            String locationTypeName = request.getParameter(ParameterConstants.LOCATION_TYPE_NAME);
            GetLocationTypeDescriptionsForm commandForm = WarehouseUtil.getHome().getGetLocationTypeDescriptionsForm();
            
            commandForm.setWarehouseName(warehouseName);
            commandForm.setLocationTypeName(locationTypeName);
            
            CommandResult commandResult = WarehouseUtil.getHome().getLocationTypeDescriptions(getUserVisitPK(request), commandForm);
            ExecutionResult executionResult = commandResult.getExecutionResult();
            GetLocationTypeDescriptionsResult result = (GetLocationTypeDescriptionsResult)executionResult.getResult();
            
            request.setAttribute("warehouse", result.getWarehouse());
            request.setAttribute("locationType", result.getLocationType());
            request.setAttribute("locationTypeDescriptions", result.getLocationTypeDescriptions());
            forwardKey = ForwardConstants.DISPLAY;
        } catch (NamingException ne) {
            forwardKey = ForwardConstants.ERROR_500;
        }
        
        return mapping.findForward(forwardKey);
    }
    
}