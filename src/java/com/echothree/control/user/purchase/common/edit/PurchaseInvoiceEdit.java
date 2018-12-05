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

package com.echothree.control.user.purchase.common.edit;

import com.echothree.control.user.term.common.spec.TermSpec;

public interface PurchaseInvoiceEdit
        extends TermSpec {
    
    String getBillFromContactMechanismName();
    void setBillFromContactMechanismName(String billFromContactMechanismName);
    
    String getBillToContactMechanismName();
    void setBillToContactMechanismName(String billToContactMechanismName);
    
    String getInvoicedTime();
    void setInvoicedTime(String invoicedTime);
    
    String getDueTime();
    void setDueTime(String dueTime);
    
    String getPaidTime();
    void setPaidTime(String paidTime);
    
    String getReference();
    void setReference(String reference);
    
    String getDescription();
    void setDescription(String description);
    
}