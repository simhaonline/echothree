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

package com.echothree.ui.web.main.action.item.itemaliastype;

import com.echothree.control.user.item.common.ItemUtil;
import com.echothree.control.user.item.common.form.CreateItemAliasTypeForm;
import com.echothree.ui.web.main.framework.MainBaseAddAction;
import com.echothree.util.common.command.CommandResult;
import com.echothree.view.client.web.struts.sprout.annotation.SproutAction;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForward;
import com.echothree.view.client.web.struts.sprout.annotation.SproutProperty;
import com.echothree.view.client.web.struts.sslext.config.SecureActionMapping;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

@SproutAction(
    path = "/Item/ItemAliasType/Add",
    mappingClass = SecureActionMapping.class,
    name = "ItemAliasTypeAdd",
    properties = {
        @SproutProperty(property = "secure", value = "true")
    },
    forwards = {
        @SproutForward(name = "Display", path = "/action/Item/ItemAliasType/Main", redirect = true),
        @SproutForward(name = "Form", path = "/item/itemaliastype/add.jsp")
    }
)
public class AddAction
        extends MainBaseAddAction<AddActionForm> {

    @Override
    public void setupDefaults(AddActionForm actionForm)
            throws NamingException {
        actionForm.setSortOrder("1");
    }
    
    @Override
    public CommandResult doAdd(AddActionForm actionForm, HttpServletRequest request)
            throws NamingException {
        CreateItemAliasTypeForm commandForm = ItemUtil.getHome().getCreateItemAliasTypeForm();

        commandForm.setItemAliasTypeName(actionForm.getItemAliasTypeName());
        commandForm.setValidationPattern(actionForm.getValidationPattern());
        commandForm.setItemAliasChecksumTypeName(actionForm.getItemAliasChecksumTypeChoice());
        commandForm.setAllowMultiple(actionForm.getAllowMultiple().toString());
        commandForm.setIsDefault(actionForm.getIsDefault().toString());
        commandForm.setSortOrder(actionForm.getSortOrder());
        commandForm.setDescription(actionForm.getDescription());

        return ItemUtil.getHome().createItemAliasType(getUserVisitPK(request), commandForm);
    }
    
}
