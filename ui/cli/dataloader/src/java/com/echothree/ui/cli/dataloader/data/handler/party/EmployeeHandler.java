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

package com.echothree.ui.cli.dataloader.data.handler.party;

import com.echothree.control.user.employee.common.EmployeeUtil;
import com.echothree.control.user.employee.common.EmployeeService;
import com.echothree.control.user.employee.common.form.CreateEmploymentForm;
import com.echothree.control.user.employee.common.form.CreateLeaveForm;
import com.echothree.control.user.employee.common.form.EmployeeFormFactory;
import com.echothree.control.user.party.common.PartyUtil;
import com.echothree.control.user.party.common.PartyService;
import com.echothree.control.user.party.common.form.AddEmployeeToCompanyForm;
import com.echothree.control.user.party.common.form.CreateProfileForm;
import com.echothree.control.user.party.common.form.PartyFormFactory;
import com.echothree.ui.cli.dataloader.data.InitialDataParser;
import com.echothree.ui.cli.dataloader.data.handler.BaseHandler;
import com.echothree.ui.cli.dataloader.data.handler.comment.CommentsHandler;
import com.echothree.ui.cli.dataloader.data.handler.contact.ContactMechanismsHandler;
import com.echothree.ui.cli.dataloader.data.handler.core.EntityAttributesHandler;
import com.echothree.ui.cli.dataloader.data.handler.core.PartyApplicationEditorUsesHandler;
import com.echothree.ui.cli.dataloader.data.handler.employee.PartyResponsibilitiesHandler;
import com.echothree.ui.cli.dataloader.data.handler.employee.PartySkillsHandler;
import com.echothree.ui.cli.dataloader.data.handler.tag.EntityTagsHandler;
import com.echothree.ui.cli.dataloader.data.handler.training.PartyTrainingClassesHandler;
import javax.naming.NamingException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EmployeeHandler
        extends BaseHandler {

    EmployeeService employeeService;
    PartyService partyService;
    String partyName;
    String employeeName;
    String entityRef;
    
    /** Creates a new instance of EmployeeHandler */
    public EmployeeHandler(InitialDataParser initialDataParser, BaseHandler parentHandler, String partyName, String employeeName,
            String entityRef) {
        super(initialDataParser, parentHandler);
        
        try {
            employeeService = EmployeeUtil.getHome();
            partyService = PartyUtil.getHome();
        } catch (NamingException ne) {
            // TODO: Handle Exception
        }
        
        this.partyName = partyName;
        this.employeeName = employeeName;
        this.entityRef = entityRef;
    }
    
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
            throws SAXException {
        if(localName.equals("employeeCompany")) {
            AddEmployeeToCompanyForm commandForm = PartyFormFactory.getAddEmployeeToCompanyForm();
            
            commandForm.setEmployeeName(employeeName);
            commandForm.set(getAttrsMap(attrs));
            
            partyService.addEmployeeToCompany(initialDataParser.getUserVisit(), commandForm);
        } else if(localName.equals("employment")) {
            CreateEmploymentForm commandForm = EmployeeFormFactory.getCreateEmploymentForm();

            commandForm.setPartyName(partyName);
            commandForm.set(getAttrsMap(attrs));

            employeeService.createEmployment(initialDataParser.getUserVisit(), commandForm);
        } else if(localName.equals("leave")) {
            CreateLeaveForm commandForm = EmployeeFormFactory.getCreateLeaveForm();

            commandForm.setPartyName(partyName);
            commandForm.set(getAttrsMap(attrs));

            employeeService.createLeave(initialDataParser.getUserVisit(), commandForm);
        } else if(localName.equals("partyResponsibilities")) {
            initialDataParser.pushHandler(new PartyResponsibilitiesHandler(initialDataParser, this, partyName));
        } else if(localName.equals("partySkills")) {
            initialDataParser.pushHandler(new PartySkillsHandler(initialDataParser, this, partyName));
        } else if(localName.equals("partyTrainingClasses")) {
            initialDataParser.pushHandler(new PartyTrainingClassesHandler(initialDataParser, this, partyName));
        } else if(localName.equals("profile")) {
            CreateProfileForm commandForm = PartyFormFactory.getCreateProfileForm();
            
            commandForm.setPartyName(partyName);
            commandForm.set(getAttrsMap(attrs));
            
            partyService.createProfile(initialDataParser.getUserVisit(), commandForm);
        } else if(localName.equals("comments")) {
            initialDataParser.pushHandler(new CommentsHandler(initialDataParser, this, entityRef));
        } else if(localName.equals("entityAttributes")) {
            initialDataParser.pushHandler(new EntityAttributesHandler(initialDataParser, this, entityRef));
        } else if(localName.equals("entityTags")) {
            initialDataParser.pushHandler(new EntityTagsHandler(initialDataParser, this, entityRef));
        } else if(localName.equals("contactMechanisms")) {
            initialDataParser.pushHandler(new ContactMechanismsHandler(initialDataParser, this, partyName));
        } else if(localName.equals("partyApplicationEditorUses")) {
            initialDataParser.pushHandler(new PartyApplicationEditorUsesHandler(initialDataParser, this, partyName));
        }
    }
    
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if(localName.equals("employee")) {
            initialDataParser.popHandler();
        }
    }
    
}
