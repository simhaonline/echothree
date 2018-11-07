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

package com.echothree.model.control.subscription.server.transfer;

import com.echothree.model.control.chain.common.transfer.ChainTransfer;
import com.echothree.model.control.chain.server.ChainControl;
import com.echothree.model.control.subscription.common.transfer.SubscriptionTypeChainTransfer;
import com.echothree.model.control.subscription.common.transfer.SubscriptionTypeTransfer;
import com.echothree.model.control.subscription.server.SubscriptionControl;
import com.echothree.model.control.uom.common.UomConstants;
import com.echothree.model.control.uom.server.UomControl;
import com.echothree.model.data.subscription.server.entity.SubscriptionTypeChain;
import com.echothree.model.data.uom.server.entity.UnitOfMeasureKind;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.server.persistence.Session;

public class SubscriptionTypeChainTransferCache
        extends BaseSubscriptionTransferCache<SubscriptionTypeChain, SubscriptionTypeChainTransfer> {
    
    ChainControl chainControl = (ChainControl)Session.getModelController(ChainControl.class);
    UomControl uomControl = (UomControl)Session.getModelController(UomControl.class);
    UnitOfMeasureKind timeUnitOfMeasureKind = uomControl.getUnitOfMeasureKindByUnitOfMeasureKindUseTypeUsingNames(UomConstants.UnitOfMeasureKindUseType_TIME);
    
    /** Creates a new instance of SubscriptionTypeChainTransferCache */
    public SubscriptionTypeChainTransferCache(UserVisit userVisit, SubscriptionControl subscriptionControl) {
        super(userVisit, subscriptionControl);
    }
    
    public SubscriptionTypeChainTransfer getSubscriptionTypeChainTransfer(SubscriptionTypeChain subscriptionTypeChain) {
        SubscriptionTypeChainTransfer subscriptionTypeChainTransfer = get(subscriptionTypeChain);
        
        if(subscriptionTypeChainTransfer == null) {
            SubscriptionTypeTransfer subscriptionType = subscriptionControl.getSubscriptionTypeTransfer(userVisit, subscriptionTypeChain.getSubscriptionType());
            ChainTransfer chain = chainControl.getChainTransfer(userVisit, subscriptionTypeChain.getChain());
            Long unformattedRemainingTime = subscriptionTypeChain.getRemainingTime();
            String remainingTime = formatUnitOfMeasure(timeUnitOfMeasureKind, unformattedRemainingTime);
            
            subscriptionTypeChainTransfer = new SubscriptionTypeChainTransfer(subscriptionType, chain, unformattedRemainingTime, remainingTime);
            put(subscriptionTypeChain, subscriptionTypeChainTransfer);
        }
        
        return subscriptionTypeChainTransfer;
    }
    
}
