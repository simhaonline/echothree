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

package com.echothree.control.user.purchase.server;

import com.echothree.control.user.purchase.common.PurchaseRemote;
import com.echothree.control.user.purchase.common.form.*;
import com.echothree.control.user.purchase.server.command.*;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.command.CommandResult;
import javax.ejb.Stateless;

@Stateless
public class PurchaseBean
        extends PurchaseFormsImpl
        implements PurchaseRemote, PurchaseLocal {
    
    // -------------------------------------------------------------------------
    //   Testing
    // -------------------------------------------------------------------------
    
    @Override
    public String ping() {
        return "PurchaseBean is alive!";
    }
    
    // --------------------------------------------------------------------------------
    //   Purchase Invoices
    // --------------------------------------------------------------------------------
    
    @Override
    public CommandResult createPurchaseInvoice(UserVisitPK userVisitPK, CreatePurchaseInvoiceForm form) {
        return new CreatePurchaseInvoiceCommand(userVisitPK, form).run();
    }

    @Override
    public CommandResult getPurchaseInvoiceStatusChoices(UserVisitPK userVisitPK, GetPurchaseInvoiceStatusChoicesForm form) {
        return new GetPurchaseInvoiceStatusChoicesCommand(userVisitPK, form).run();
    }
    
    @Override
    public CommandResult setPurchaseInvoiceStatus(UserVisitPK userVisitPK, SetPurchaseInvoiceStatusForm form) {
        return new SetPurchaseInvoiceStatusCommand(userVisitPK, form).run();
    }
    
    // --------------------------------------------------------------------------------
    //   Purchase Invoice Lines
    // --------------------------------------------------------------------------------
    
    @Override
    public CommandResult createPurchaseInvoiceLine(UserVisitPK userVisitPK, CreatePurchaseInvoiceLineForm form) {
        return new CreatePurchaseInvoiceLineCommand(userVisitPK, form).run();
    }

}
