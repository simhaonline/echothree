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

package com.echothree.model.control.carrier.common.transfer;

import com.echothree.model.control.party.common.transfer.PartyTransfer;
import com.echothree.util.common.transfer.BaseTransfer;

public class PartyCarrierAccountTransfer
        extends BaseTransfer {
    
    private PartyTransfer party;
    private CarrierTransfer carrier;
    private String account;
    private Boolean alwaysUseThirdPartyBilling;
    
    /** Creates a new instance of PartyCarrierAccountTransfer */
    public PartyCarrierAccountTransfer(PartyTransfer party, CarrierTransfer carrier, String account, Boolean alwaysUseThirdPartyBilling) {
        this.party = party;
        this.carrier = carrier;
        this.account = account;
        this.alwaysUseThirdPartyBilling = alwaysUseThirdPartyBilling;
    }

    /**
     * @return the party
     */
    public PartyTransfer getParty() {
        return party;
    }

    /**
     * @param party the party to set
     */
    public void setParty(PartyTransfer party) {
        this.party = party;
    }

    /**
     * @return the carrier
     */
    public CarrierTransfer getCarrier() {
        return carrier;
    }

    /**
     * @param carrier the carrier to set
     */
    public void setCarrier(CarrierTransfer carrier) {
        this.carrier = carrier;
    }

    /**
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return the alwaysUseThirdPartyBilling
     */
    public Boolean getAlwaysUseThirdPartyBilling() {
        return alwaysUseThirdPartyBilling;
    }

    /**
     * @param alwaysUseThirdPartyBilling the alwaysUseThirdPartyBilling to set
     */
    public void setAlwaysUseThirdPartyBilling(Boolean alwaysUseThirdPartyBilling) {
        this.alwaysUseThirdPartyBilling = alwaysUseThirdPartyBilling;
    }
    
}
