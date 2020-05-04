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

package com.echothree.model.control.payment.server.transfer;

import com.echothree.model.control.payment.common.transfer.PaymentProcessorActionTypeDescriptionTransfer;
import com.echothree.model.control.payment.common.transfer.PaymentProcessorActionTypeTransfer;
import com.echothree.model.control.payment.server.PaymentControl;
import com.echothree.model.control.party.common.transfer.LanguageTransfer;
import com.echothree.model.data.payment.server.entity.PaymentProcessorActionTypeDescription;
import com.echothree.model.data.user.server.entity.UserVisit;

public class PaymentProcessorActionTypeDescriptionTransferCache
        extends BasePaymentDescriptionTransferCache<PaymentProcessorActionTypeDescription, PaymentProcessorActionTypeDescriptionTransfer> {
    
    /** Creates a new instance of PaymentProcessorActionTypeDescriptionTransferCache */
    public PaymentProcessorActionTypeDescriptionTransferCache(UserVisit userVisit, PaymentControl paymentControl) {
        super(userVisit, paymentControl);
    }
    
    @Override
    public PaymentProcessorActionTypeDescriptionTransfer getTransfer(PaymentProcessorActionTypeDescription paymentProcessorActionTypeDescription) {
        PaymentProcessorActionTypeDescriptionTransfer paymentProcessorActionTypeDescriptionTransfer = get(paymentProcessorActionTypeDescription);
        
        if(paymentProcessorActionTypeDescriptionTransfer == null) {
            PaymentProcessorActionTypeTransferCache paymentProcessorActionTypeTransferCache = paymentControl.getPaymentTransferCaches(userVisit).getPaymentProcessorActionTypeTransferCache();
            PaymentProcessorActionTypeTransfer paymentProcessorActionTypeTransfer = paymentProcessorActionTypeTransferCache.getTransfer(paymentProcessorActionTypeDescription.getPaymentProcessorActionType());
            LanguageTransfer languageTransfer = partyControl.getLanguageTransfer(userVisit, paymentProcessorActionTypeDescription.getLanguage());
            
            paymentProcessorActionTypeDescriptionTransfer = new PaymentProcessorActionTypeDescriptionTransfer(languageTransfer, paymentProcessorActionTypeTransfer, paymentProcessorActionTypeDescription.getDescription());
            put(paymentProcessorActionTypeDescription, paymentProcessorActionTypeDescriptionTransfer);
        }
        
        return paymentProcessorActionTypeDescriptionTransfer;
    }
    
}