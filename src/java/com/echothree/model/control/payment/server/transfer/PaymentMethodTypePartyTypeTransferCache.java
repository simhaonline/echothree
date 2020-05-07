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

import com.echothree.model.control.party.common.transfer.PartyTypeTransfer;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.payment.common.transfer.PaymentMethodTypePartyTypeTransfer;
import com.echothree.model.control.payment.common.transfer.PaymentMethodTypeTransfer;
import com.echothree.model.control.payment.server.PaymentControl;
import com.echothree.model.control.payment.server.PaymentMethodTypeControl;
import com.echothree.model.control.workflow.common.transfer.WorkflowTransfer;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.data.payment.server.entity.PaymentMethodTypePartyType;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.server.persistence.Session;

public class PaymentMethodTypePartyTypeTransferCache
        extends BasePaymentTransferCache<PaymentMethodTypePartyType, PaymentMethodTypePartyTypeTransfer> {
    
    PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
    PaymentMethodTypeControl paymentMethodTypeControl = (PaymentMethodTypeControl) Session.getModelController(PaymentMethodTypeControl.class);
    WorkflowControl workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);

    /** Creates a new instance of PaymentMethodTypePartyTypeTransferCache */
    public PaymentMethodTypePartyTypeTransferCache(UserVisit userVisit, PaymentControl paymentControl) {
        super(userVisit, paymentControl);
    }

    @Override
    public PaymentMethodTypePartyTypeTransfer getTransfer(PaymentMethodTypePartyType paymentMethodTypePartyType) {
        PaymentMethodTypePartyTypeTransfer paymentMethodTypePartyTypeTransfer = get(paymentMethodTypePartyType);
        
        if(paymentMethodTypePartyTypeTransfer == null) {
            PaymentMethodTypeTransfer paymentMethodType = paymentMethodTypeControl.getPaymentMethodTypeTransfer(userVisit, paymentMethodTypePartyType.getPaymentMethodType());
            PartyTypeTransfer partyType = partyControl.getPartyTypeTransfer(userVisit, paymentMethodTypePartyType.getPartyType());
            WorkflowTransfer partyPaymentMethodWorkflow = workflowControl.getWorkflowTransfer(userVisit, paymentMethodTypePartyType.getPartyPaymentMethodWorkflow());
            WorkflowTransfer partyContactMechanismWorkflow = workflowControl.getWorkflowTransfer(userVisit, paymentMethodTypePartyType.getPartyContactMechanismWorkflow());
            
            paymentMethodTypePartyTypeTransfer = new PaymentMethodTypePartyTypeTransfer(paymentMethodType, partyType, partyPaymentMethodWorkflow,
                    partyContactMechanismWorkflow);
            put(paymentMethodTypePartyType, paymentMethodTypePartyTypeTransfer);
        }
        return paymentMethodTypePartyTypeTransfer;
    }
    
}
