// --------------------------------------------------------------------------------
// Copyright 2002-2019 Echo Three, LLC
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

import com.echothree.control.user.search.common.form.GetSalesOrderResultsForm;
import com.echothree.control.user.search.common.result.GetSalesOrderResultsResult;
import com.echothree.control.user.search.common.result.SearchResultFactory;
import com.echothree.model.control.search.common.SearchConstants;
import com.echothree.model.control.search.server.SearchControl;
import com.echothree.model.control.search.server.logic.SearchLogic;
import com.echothree.model.data.search.server.entity.SearchKind;
import com.echothree.model.data.search.server.entity.SearchType;
import com.echothree.model.data.search.server.entity.UserVisitSearch;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetSalesOrderResultsCommand
        extends BaseSimpleCommand<GetSalesOrderResultsForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("SearchTypeName", FieldType.ENTITY_NAME, true, null, null)
                ));
    }

    /** Creates a new instance of GetSalesOrderResultsCommand */
    public GetSalesOrderResultsCommand(UserVisitPK userVisitPK, GetSalesOrderResultsForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        GetSalesOrderResultsResult result = SearchResultFactory.getGetSalesOrderResultsResult();
        var searchControl = (SearchControl)Session.getModelController(SearchControl.class);
        SearchKind searchKind = searchControl.getSearchKindByName(SearchConstants.SearchKind_SALES_ORDER);
        
        if(searchKind != null) {
            String searchTypeName = form.getSearchTypeName();
            SearchType searchType = searchControl.getSearchTypeByName(searchKind, searchTypeName);
            
            if(searchType != null) {
                UserVisit userVisit = getUserVisit();
                UserVisitSearch userVisitSearch = searchControl.getUserVisitSearch(userVisit, searchType);
                
                if(userVisitSearch != null) {
                    if(session.hasLimit(com.echothree.model.data.search.server.factory.SearchResultFactory.class)) {
                        result.setSalesOrderResultCount(SearchLogic.getInstance().countSearchResults(userVisitSearch.getSearch()));
                    }

                    result.setSalesOrderResults(searchControl.getSalesOrderResultTransfers(userVisit, userVisitSearch));
                } else {
                    addExecutionError(ExecutionErrors.UnknownUserVisitSearch.name());
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownSearchTypeName.name(), SearchConstants.SearchKind_SALES_ORDER, searchTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownSearchKindName.name(), SearchConstants.SearchKind_SALES_ORDER);
        }
        
        return result;
    }
}
