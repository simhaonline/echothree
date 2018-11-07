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

package com.echothree.ui.cli.dataloader.data.handler.inventory;

import com.echothree.control.user.inventory.common.InventoryUtil;
import com.echothree.control.user.inventory.common.InventoryService;
import com.echothree.control.user.inventory.common.form.CreateInventoryConditionUseTypeDescriptionForm;
import com.echothree.control.user.inventory.common.form.InventoryFormFactory;
import com.echothree.ui.cli.dataloader.data.InitialDataParser;
import com.echothree.ui.cli.dataloader.data.handler.BaseHandler;
import javax.naming.NamingException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class InventoryConditionUseTypeHandler
        extends BaseHandler {
    InventoryService inventoryService;
    String inventoryConditionUseTypeName;
    
    /** Creates a new instance of InventoryConditionUseTypeHandler */
    public InventoryConditionUseTypeHandler(InitialDataParser initialDataParser, BaseHandler parentHandler, String inventoryConditionUseTypeName) {
        super(initialDataParser, parentHandler);
        
        try {
            inventoryService = InventoryUtil.getHome();
        } catch (NamingException ne) {
            // TODO: Handle Exception
        }
        
        this.inventoryConditionUseTypeName = inventoryConditionUseTypeName;
    }
    
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
    throws SAXException {
        if(localName.equals("inventoryConditionUseTypeDescription")) {
            String attrLanguageIsoName = null;
            String attrDescription = null;
            
            int attrCount = attrs.getLength();
            for(int i = 0; i < attrCount; i++) {
                if(attrs.getQName(i).equals("languageIsoName"))
                    attrLanguageIsoName = attrs.getValue(i);
                else if(attrs.getQName(i).equals("description"))
                    attrDescription = attrs.getValue(i);
            }
            
            try {
                CreateInventoryConditionUseTypeDescriptionForm commandForm = InventoryFormFactory.getCreateInventoryConditionUseTypeDescriptionForm();
                
                commandForm.setInventoryConditionUseTypeName(inventoryConditionUseTypeName);
                commandForm.setLanguageIsoName(attrLanguageIsoName);
                commandForm.setDescription(attrDescription);
                
                checkCommandResult(inventoryService.createInventoryConditionUseTypeDescription(initialDataParser.getUserVisit(), commandForm));
            } catch (Exception e) {
                throw new SAXException(e);
            }
        }
    }
    
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException {
        if(localName.equals("inventoryConditionUseType")) {
            initialDataParser.popHandler();
        }
    }
    
}
