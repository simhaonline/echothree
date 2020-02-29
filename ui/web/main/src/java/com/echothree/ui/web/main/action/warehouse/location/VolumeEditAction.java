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

package com.echothree.ui.web.main.action.warehouse.location;

import com.echothree.control.user.warehouse.common.WarehouseUtil;
import com.echothree.control.user.warehouse.common.edit.LocationVolumeEdit;
import com.echothree.control.user.warehouse.common.form.EditLocationVolumeForm;
import com.echothree.control.user.warehouse.common.result.EditLocationVolumeResult;
import com.echothree.control.user.warehouse.common.spec.LocationSpec;
import com.echothree.ui.web.main.framework.AttributeConstants;
import com.echothree.ui.web.main.framework.ForwardConstants;
import com.echothree.ui.web.main.framework.MainBaseAction;
import com.echothree.ui.web.main.framework.ParameterConstants;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.CustomActionForward;
import com.echothree.view.client.web.struts.sprout.annotation.SproutAction;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForward;
import com.echothree.view.client.web.struts.sprout.annotation.SproutProperty;
import com.echothree.view.client.web.struts.sslext.config.SecureActionMapping;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

@SproutAction(
    path = "/Warehouse/Location/VolumeEdit",
    mappingClass = SecureActionMapping.class,
    name = "LocationVolumeEdit",
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Display", path = "/action/Warehouse/Location/Main", redirect = true),
        @SproutForward(name = "Form", path = "/warehouse/location/volumeEdit.jsp")
    }
)
public class VolumeEditAction
        extends MainBaseAction<VolumeEditActionForm> {
    
    @Override
    public ActionForward executeAction(ActionMapping mapping, VolumeEditActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String forwardKey = null;
        String warehouseName = request.getParameter(ParameterConstants.WAREHOUSE_NAME);
        String locationName = request.getParameter(ParameterConstants.LOCATION_NAME);
        EditLocationVolumeForm commandForm = WarehouseUtil.getHome().getEditLocationVolumeForm();
        LocationSpec spec = WarehouseUtil.getHome().getLocationSpec();
        
        if(warehouseName == null)
            warehouseName = form.getWarehouseName();
        if(locationName == null)
            locationName = form.getLocationName();
        
        commandForm.setSpec(spec);
        spec.setWarehouseName(warehouseName);
        spec.setLocationName(locationName);
        
        if(wasPost(request)) {
            LocationVolumeEdit edit = WarehouseUtil.getHome().getLocationVolumeEdit();
            
            commandForm.setEditMode(EditMode.UPDATE);
            commandForm.setEdit(edit);
            
            edit.setHeightUnitOfMeasureTypeName(form.getHeightUnitOfMeasureTypeChoice());
            edit.setHeight(form.getHeight());
            edit.setWidthUnitOfMeasureTypeName(form.getWidthUnitOfMeasureTypeChoice());
            edit.setWidth(form.getWidth());
            edit.setDepthUnitOfMeasureTypeName(form.getDepthUnitOfMeasureTypeChoice());
            edit.setDepth(form.getDepth());
            
            CommandResult commandResult = WarehouseUtil.getHome().editLocationVolume(getUserVisitPK(request), commandForm);
            
            if(commandResult.hasErrors()) {
                ExecutionResult executionResult = commandResult.getExecutionResult();
                
                if(executionResult != null) {
                    EditLocationVolumeResult result = (EditLocationVolumeResult)executionResult.getResult();
                    
                    request.setAttribute(AttributeConstants.ENTITY_LOCK, result.getEntityLock());
                }
                
                setCommandResultAttribute(request, commandResult);
                
                forwardKey = ForwardConstants.FORM;
            } else {
                forwardKey = ForwardConstants.DISPLAY;
            }
        } else {
            commandForm.setEditMode(EditMode.LOCK);
            
            CommandResult commandResult = WarehouseUtil.getHome().editLocationVolume(getUserVisitPK(request), commandForm);
            ExecutionResult executionResult = commandResult.getExecutionResult();
            EditLocationVolumeResult result = (EditLocationVolumeResult)executionResult.getResult();
            
            if(result != null) {
                LocationVolumeEdit edit = result.getEdit();
                
                if(edit != null) {
                    form.setWarehouseName(warehouseName);
                    form.setLocationName(locationName);
                    form.setHeightUnitOfMeasureTypeChoice(edit.getHeightUnitOfMeasureTypeName());
                    form.setHeight(edit.getHeight());
                    form.setWidthUnitOfMeasureTypeChoice(edit.getWidthUnitOfMeasureTypeName());
                    form.setWidth(edit.getWidth());
                    form.setDepthUnitOfMeasureTypeChoice(edit.getDepthUnitOfMeasureTypeName());
                    form.setDepth(edit.getDepth());
                }
                
                request.setAttribute(AttributeConstants.ENTITY_LOCK, result.getEntityLock());
            }
            
            setCommandResultAttribute(request, commandResult);
            
            forwardKey = ForwardConstants.FORM;
        }
        
        CustomActionForward customActionForward = new CustomActionForward(mapping.findForward(forwardKey));
        if(forwardKey.equals(ForwardConstants.FORM)) {
            request.setAttribute(AttributeConstants.WAREHOUSE_NAME, warehouseName);
            request.setAttribute(AttributeConstants.LOCATION_NAME, locationName);
        } else if(forwardKey.equals(ForwardConstants.DISPLAY)) {
            Map<String, String> parameters = new HashMap<>(2);
            
            parameters.put(ParameterConstants.WAREHOUSE_NAME, warehouseName);
            parameters.put(ParameterConstants.LOCATION_NAME, locationName);
            customActionForward.setParameters(parameters);
        }
        
        return customActionForward;
    }
    
}
