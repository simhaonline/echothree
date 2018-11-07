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

package com.echothree.model.control.accounting.server.transfer;

import com.echothree.model.control.accounting.common.transfer.TransactionTypeDescriptionTransfer;
import com.echothree.model.control.accounting.common.transfer.TransactionTypeTransfer;
import com.echothree.model.control.accounting.server.AccountingControl;
import com.echothree.model.control.party.common.transfer.LanguageTransfer;
import com.echothree.model.data.accounting.server.entity.TransactionTypeDescription;
import com.echothree.model.data.user.server.entity.UserVisit;

public class TransactionTypeDescriptionTransferCache
        extends BaseAccountingDescriptionTransferCache<TransactionTypeDescription, TransactionTypeDescriptionTransfer> {
    
    /** Creates a new instance of TransactionTypeDescriptionTransferCache */
    public TransactionTypeDescriptionTransferCache(UserVisit userVisit, AccountingControl accountingControl) {
        super(userVisit, accountingControl);
    }
    
    @Override
    public TransactionTypeDescriptionTransfer getTransfer(TransactionTypeDescription transactionTypeDescription) {
        TransactionTypeDescriptionTransfer transactionTypeDescriptionTransfer = get(transactionTypeDescription);
        
        if(transactionTypeDescriptionTransfer == null) {
            TransactionTypeTransferCache transactionTypeTransferCache = accountingControl.getAccountingTransferCaches(userVisit).getTransactionTypeTransferCache();
            TransactionTypeTransfer transactionTypeTransfer = transactionTypeTransferCache.getTransfer(transactionTypeDescription.getTransactionType());
            LanguageTransfer languageTransfer = partyControl.getLanguageTransfer(userVisit, transactionTypeDescription.getLanguage());
            
            transactionTypeDescriptionTransfer = new TransactionTypeDescriptionTransfer(languageTransfer, transactionTypeTransfer, transactionTypeDescription.getDescription());
            put(transactionTypeDescription, transactionTypeDescriptionTransfer);
        }
        
        return transactionTypeDescriptionTransfer;
    }
    
}
