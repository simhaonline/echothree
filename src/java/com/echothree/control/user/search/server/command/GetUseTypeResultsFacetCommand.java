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

package com.echothree.control.user.search.server.command;

import com.echothree.control.user.search.common.form.GetUseTypeResultsFacetForm;
import com.echothree.control.user.search.common.result.GetUseTypeResultsFacetResult;
import com.echothree.control.user.search.common.result.SearchResultFactory;
import com.echothree.model.control.search.common.SearchConstants;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.command.BaseResult;

public class GetUseTypeResultsFacetCommand
        extends BaseGetResultsFacetCommand<GetUseTypeResultsFacetForm, GetUseTypeResultsFacetResult> {

    /** Creates a new instance of GetUseTypeResultsFacetCommand */
    public GetUseTypeResultsFacetCommand(UserVisitPK userVisitPK, GetUseTypeResultsFacetForm form) {
        super(userVisitPK, form, null);
    }

    @Override
    protected BaseResult execute() {
        return execute(SearchConstants.SearchKind_USE_TYPE, SearchResultFactory.getGetUseTypeResultsFacetResult());
    }

}
