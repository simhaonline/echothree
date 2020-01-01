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

package com.echothree.ui.web.main.action.configuration.geocodealias;

import com.echothree.control.user.geo.common.GeoUtil;
import com.echothree.control.user.geo.common.form.GetGeoCodeAliasTypeChoicesForm;
import com.echothree.control.user.geo.common.result.GetGeoCodeAliasTypeChoicesResult;
import com.echothree.model.control.geo.common.choice.GeoCodeAliasTypeChoicesBean;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.view.client.web.struts.BaseLanguageActionForm;
import com.echothree.view.client.web.struts.sprout.annotation.SproutForm;
import java.util.List;
import javax.naming.NamingException;
import org.apache.struts.util.LabelValueBean;

@SproutForm(name="GeoCodeAliasAdd")
public class AddActionForm
        extends BaseLanguageActionForm {
    
    private GeoCodeAliasTypeChoicesBean geoCodeAliasTypeChoices;

    private String geoCodeName;
    private String geoCodeAliasTypeChoice;
    private String alias;

    public void setupGeoCodeAliasTypeChoices() {
        if(geoCodeAliasTypeChoices == null) {
            try {
                GetGeoCodeAliasTypeChoicesForm form = GeoUtil.getHome().getGetGeoCodeAliasTypeChoicesForm();

                form.setGeoCodeName(geoCodeName);
                form.setDefaultGeoCodeAliasTypeChoice(geoCodeAliasTypeChoice);
                form.setAllowNullChoice(Boolean.FALSE.toString());

                CommandResult commandResult = GeoUtil.getHome().getGeoCodeAliasTypeChoices(userVisitPK, form);
                ExecutionResult executionResult = commandResult.getExecutionResult();
                GetGeoCodeAliasTypeChoicesResult getGeoCodeAliasTypeChoicesResult = (GetGeoCodeAliasTypeChoicesResult)executionResult.getResult();
                geoCodeAliasTypeChoices = getGeoCodeAliasTypeChoicesResult.getGeoCodeAliasTypeChoices();

                if(geoCodeAliasTypeChoice == null) {
                    geoCodeAliasTypeChoice = geoCodeAliasTypeChoices.getDefaultValue();
                }
            } catch (NamingException ne) {
                ne.printStackTrace();
                // failed, geoCodeAliasTypeChoices remains null, no default
            }
        }
    }

    /**
     * @return the geoCodeName
     */
    public String getGeoCodeName() {
        return geoCodeName;
    }

    /**
     * @param geoCodeName the geoCodeName to set
     */
    public void setGeoCodeName(String geoCodeName) {
        this.geoCodeName = geoCodeName;
    }

    public List<LabelValueBean> getGeoCodeAliasTypeChoices() {
        List<LabelValueBean> choices = null;

        setupGeoCodeAliasTypeChoices();
        if(geoCodeAliasTypeChoices != null) {
            choices = convertChoices(geoCodeAliasTypeChoices);
        }

        return choices;
    }

    public void setGeoCodeAliasTypeChoice(String geoCodeAliasTypeChoice) {
        this.geoCodeAliasTypeChoice = geoCodeAliasTypeChoice;
    }

    public String getGeoCodeAliasTypeChoice() {
        setupGeoCodeAliasTypeChoices();
        return geoCodeAliasTypeChoice;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
    
}
