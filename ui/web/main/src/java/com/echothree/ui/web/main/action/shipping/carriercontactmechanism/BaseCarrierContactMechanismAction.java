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

package com.echothree.ui.web.main.action.shipping.carriercontactmechanism;

import com.echothree.control.user.carrier.common.CarrierUtil;
import com.echothree.control.user.carrier.common.form.GetCarrierForm;
import com.echothree.control.user.carrier.common.result.GetCarrierResult;
import com.echothree.control.user.contact.common.ContactUtil;
import com.echothree.control.user.contact.common.form.GetContactMechanismForm;
import com.echothree.control.user.contact.common.result.GetContactMechanismResult;
import com.echothree.control.user.geo.common.GeoUtil;
import com.echothree.control.user.geo.common.form.GetCountryForm;
import com.echothree.control.user.geo.common.result.GetCountryResult;
import com.echothree.model.control.geo.common.GeoOptions;
import com.echothree.ui.web.main.framework.AttributeConstants;
import com.echothree.ui.web.main.framework.MainBaseAction;
import com.echothree.ui.web.main.framework.ParameterConstants;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import java.util.HashSet;
import java.util.Set;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;

public abstract class BaseCarrierContactMechanismAction<A
        extends ActionForm>
        extends MainBaseAction<A> {

    public void setupDefaultCountry(HttpServletRequest request)
            throws NamingException {
        GetCountryForm commandForm =  GeoUtil.getHome().getGetCountryForm();
        
        Set<String> options = new HashSet<>();
        options.add(GeoOptions.CountryIncludeAliases);
        commandForm.setOptions(options);

        CommandResult commandResult = GeoUtil.getHome().getCountry(getUserVisitPK(request), commandForm);
        ExecutionResult executionResult = commandResult.getExecutionResult();
        GetCountryResult result = (GetCountryResult)executionResult.getResult();

        request.setAttribute(AttributeConstants.DEFAULT_COUNTRY, result.getCountry());
    }
    
    public void setupCountry(HttpServletRequest request, String countryName)
            throws NamingException {
        GetCountryForm commandForm =  GeoUtil.getHome().getGetCountryForm();
        
        commandForm.setCountryName(countryName);

        CommandResult commandResult = GeoUtil.getHome().getCountry(getUserVisitPK(request), commandForm);
        ExecutionResult executionResult = commandResult.getExecutionResult();
        GetCountryResult result = (GetCountryResult)executionResult.getResult();

        request.setAttribute(AttributeConstants.COUNTRY, result.getCountry());
    }
    
    public static void setupCarrier(HttpServletRequest request, String partyName)
            throws NamingException {
        GetCarrierForm commandForm = CarrierUtil.getHome().getGetCarrierForm();

        commandForm.setPartyName(partyName);

        CommandResult commandResult = CarrierUtil.getHome().getCarrier(getUserVisitPK(request), commandForm);
        ExecutionResult executionResult = commandResult.getExecutionResult();
        GetCarrierResult result = (GetCarrierResult)executionResult.getResult();

        request.setAttribute(AttributeConstants.CARRIER, result.getCarrier());
    }

    public void setupCarrier(HttpServletRequest request)
            throws NamingException {
        setupCarrier(request, request.getParameter(ParameterConstants.PARTY_NAME));
    }

    public static void setupContactMechanismTransfer(HttpServletRequest request, String contactMechanismName)
            throws NamingException {
        GetContactMechanismForm commandForm = ContactUtil.getHome().getGetContactMechanismForm();

        commandForm.setContactMechanismName(contactMechanismName);

        CommandResult commandResult = ContactUtil.getHome().getContactMechanism(getUserVisitPK(request), commandForm);
        ExecutionResult executionResult = commandResult.getExecutionResult();
        GetContactMechanismResult result = (GetContactMechanismResult)executionResult.getResult();

        request.setAttribute(AttributeConstants.CONTACT_MECHANISM, result.getContactMechanism());
    }

    public static void setupPartyContactMechanismTransfer(HttpServletRequest request, String partyName, String contactMechanismName)
            throws NamingException {
        GetContactMechanismForm commandForm = ContactUtil.getHome().getGetContactMechanismForm();

        commandForm.setPartyName(partyName);
        commandForm.setContactMechanismName(contactMechanismName);

        CommandResult commandResult = ContactUtil.getHome().getContactMechanism(getUserVisitPK(request), commandForm);
        ExecutionResult executionResult = commandResult.getExecutionResult();
        GetContactMechanismResult result = (GetContactMechanismResult)executionResult.getResult();

        request.setAttribute(AttributeConstants.PARTY_CONTACT_MECHANISM, result.getPartyContactMechanism());
    }

}
