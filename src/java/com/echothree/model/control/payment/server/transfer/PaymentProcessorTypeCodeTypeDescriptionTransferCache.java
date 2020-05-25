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

import com.echothree.model.control.party.common.transfer.LanguageTransfer;
import com.echothree.model.control.payment.common.transfer.PaymentProcessorTypeCodeTypeDescriptionTransfer;
import com.echothree.model.control.payment.common.transfer.PaymentProcessorTypeCodeTypeTransfer;
import com.echothree.model.control.payment.server.control.PaymentControl;
import com.echothree.model.data.payment.server.entity.PaymentProcessorTypeCodeTypeDescription;
import com.echothree.model.data.user.server.entity.UserVisit;

public class PaymentProcessorTypeCodeTypeDescriptionTransferCache
        extends BasePaymentDescriptionTransferCache<PaymentProcessorTypeCodeTypeDescription, PaymentProcessorTypeCodeTypeDescriptionTransfer> {
    
    /** Creates a new instance of PaymentProcessorTypeDescriptionTransferCache */
    public PaymentProcessorTypeCodeTypeDescriptionTransferCache(UserVisit userVisit, PaymentControl paymentControl) {
        super(userVisit, paymentControl);
    }
    
    @Override
    public PaymentProcessorTypeCodeTypeDescriptionTransfer getTransfer(PaymentProcessorTypeCodeTypeDescription paymentProcessorTypeCodeTypeDescription) {
        PaymentProcessorTypeCodeTypeDescriptionTransfer paymentProcessorTypeCodeTypeDescriptionTransfer = get(paymentProcessorTypeCodeTypeDescription);
        
        if(paymentProcessorTypeCodeTypeDescriptionTransfer == null) {
            PaymentProcessorTypeCodeTypeTransferCache paymentProcessorTypeCodeTypeTransferCache = paymentControl.getPaymentTransferCaches(userVisit).getPaymentProcessorTypeCodeTypeTransferCache();
            PaymentProcessorTypeCodeTypeTransfer paymentProcessorTypeCodeTypeTransfer = paymentProcessorTypeCodeTypeTransferCache.getTransfer(paymentProcessorTypeCodeTypeDescription.getPaymentProcessorTypeCodeType());
            LanguageTransfer languageTransfer = partyControl.getLanguageTransfer(userVisit, paymentProcessorTypeCodeTypeDescription.getLanguage());
            
            paymentProcessorTypeCodeTypeDescriptionTransfer = new PaymentProcessorTypeCodeTypeDescriptionTransfer(languageTransfer, paymentProcessorTypeCodeTypeTransfer, paymentProcessorTypeCodeTypeDescription.getDescription());
            put(paymentProcessorTypeCodeTypeDescription, paymentProcessorTypeCodeTypeDescriptionTransfer);
        }
        
        return paymentProcessorTypeCodeTypeDescriptionTransfer;
    }
    
}