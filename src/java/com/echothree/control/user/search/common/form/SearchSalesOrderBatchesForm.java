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

package com.echothree.control.user.search.common.form;

import com.echothree.control.user.accounting.common.spec.CurrencySpec;
import com.echothree.control.user.payment.common.spec.PaymentMethodSpec;

public interface SearchSalesOrderBatchesForm
        extends CurrencySpec, PaymentMethodSpec {

    String getBatchName();
    void setBatchName(String batchName);
    
    String getSearchTypeName();
    void setSearchTypeName(String searchTypeName);

    String getBatchAliasTypeName();
    void setBatchAliasTypeName(String batchAliasTypeName);
    
    String getAlias();
    void setAlias(String alias);
    
    String getSalesOrderBatchStatusChoice();
    void setSalesOrderBatchStatusChoice(String salesOrderBatchStatusChoice);

    String getCreatedSince();
    void setCreatedSince(String createdSince);

    String getModifiedSince();
    void setModifiedSince(String modifiedSince);

    String getFields();
    void setFields(String fields);
    
}