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

package com.echothree.control.user.accounting.common.result;

import com.echothree.model.control.accounting.common.transfer.GlAccountCategoryTransfer;
import com.echothree.model.control.accounting.common.transfer.GlAccountTransfer;
import com.echothree.util.common.command.BaseResult;
import java.util.List;

public interface GetGlAccountsResult
        extends BaseResult {
    
    GlAccountCategoryTransfer getGlAccountCategory();
    void setGlAccountCategory(GlAccountCategoryTransfer glAccountCategory);
    
    List<GlAccountTransfer> getGlAccounts();
    void setGlAccounts(List<GlAccountTransfer> glAccounts);
    
}