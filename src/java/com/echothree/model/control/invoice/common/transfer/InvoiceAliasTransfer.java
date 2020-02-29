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

package com.echothree.model.control.invoice.common.transfer;

import com.echothree.util.common.transfer.BaseTransfer;

public class InvoiceAliasTransfer
        extends BaseTransfer {
    
    private InvoiceTransfer invoice;
    private InvoiceAliasTypeTransfer invoiceAliasType;
    private String alias;
    
    /** Creates a new instance of InvoiceAliasTransfer */
    public InvoiceAliasTransfer(InvoiceTransfer invoice, InvoiceAliasTypeTransfer invoiceAliasType, String alias) {
        this.invoice = invoice;
        this.invoiceAliasType = invoiceAliasType;
        this.alias = alias;
    }

    public InvoiceTransfer getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceTransfer invoice) {
        this.invoice = invoice;
    }

    public InvoiceAliasTypeTransfer getInvoiceAliasType() {
        return invoiceAliasType;
    }

    public void setInvoiceAliasType(InvoiceAliasTypeTransfer invoiceAliasType) {
        this.invoiceAliasType = invoiceAliasType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
