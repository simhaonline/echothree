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

package com.echothree.control.user.filter.server.command;

import com.echothree.control.user.filter.common.form.CreateFilterForm;
import com.echothree.model.control.filter.server.FilterControl;
import com.echothree.model.control.selector.common.SelectorConstants;
import com.echothree.model.control.selector.server.SelectorControl;
import com.echothree.model.data.filter.server.entity.Filter;
import com.echothree.model.data.filter.server.entity.FilterAdjustment;
import com.echothree.model.data.filter.server.entity.FilterKind;
import com.echothree.model.data.filter.server.entity.FilterType;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.selector.server.entity.Selector;
import com.echothree.model.data.selector.server.entity.SelectorKind;
import com.echothree.model.data.selector.server.entity.SelectorType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateFilterCommand
        extends BaseSimpleCommand<CreateFilterForm> {

    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("FilterKindName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("FilterTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("FilterName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("InitialFilterAdjustmentName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("FilterItemSelectorName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of CreateFilterCommand */
    public CreateFilterCommand(UserVisitPK userVisitPK, CreateFilterForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
    @Override
    protected BaseResult execute() {
        var filterControl = (FilterControl)Session.getModelController(FilterControl.class);
        String filterKindName = form.getFilterKindName();
        FilterKind filterKind = filterControl.getFilterKindByName(filterKindName);
        
        if(filterKind != null) {
            String filterTypeName = form.getFilterTypeName();
            FilterType filterType = filterControl.getFilterTypeByName(filterKind, filterTypeName);
            
            if(filterType != null) {
                String filterName = form.getFilterName();
                Filter filter = filterControl.getFilterByName(filterType, filterName);
                
                if(filter == null) {
                    String initialFilterAdjustmentName = form.getInitialFilterAdjustmentName();
                    FilterAdjustment initialFilterAdjustment = initialFilterAdjustmentName == null? null: filterControl.getFilterAdjustmentByName(filterKind, initialFilterAdjustmentName);
                    
                    if(initialFilterAdjustmentName == null || initialFilterAdjustment != null) {
                        String filterItemSelectorName = form.getFilterItemSelectorName();
                        Selector filterItemSelector = null;
                        
                        if(filterItemSelectorName != null) {
                            var selectorControl = (SelectorControl)Session.getModelController(SelectorControl.class);
                            SelectorKind selectorKind = selectorControl.getSelectorKindByName(SelectorConstants.SelectorKind_ITEM);
                            
                            if(selectorKind != null) {
                                SelectorType selectorType = selectorControl.getSelectorTypeByName(selectorKind, SelectorConstants.SelectorType_FILTER);
                                
                                if(selectorType != null) {
                                    filterItemSelector = selectorControl.getSelectorByName(selectorType, filterItemSelectorName);
                                } else {
                                    addExecutionError(ExecutionErrors.UnknownSelectorTypeName.name(), SelectorConstants.SelectorType_FILTER);
                                }
                            } else {
                                addExecutionError(ExecutionErrors.UnknownSelectorKindName.name(), SelectorConstants.SelectorKind_ITEM);
                            }
                        }
                        
                        if(filterItemSelectorName == null || filterItemSelector != null) {
                            PartyPK partyPK = getPartyPK();
                            Boolean isDefault = Boolean.valueOf(form.getIsDefault());
                            Integer sortOrder = Integer.valueOf(form.getSortOrder());
                            String description = form.getDescription();
                            
                            filter = filterControl.createFilter(filterType, filterName, initialFilterAdjustment, filterItemSelector,
                                    isDefault, sortOrder, getPartyPK());
                            
                            if(description != null) {
                                Language language = getPreferredLanguage();
                                
                                filterControl.createFilterDescription(filter, language, description, partyPK);
                            }
                        } else {
                            addExecutionError(ExecutionErrors.UnknownFilterItemSelectorName.name(), filterItemSelectorName);
                        }
                    } else {
                        addExecutionError(ExecutionErrors.UnknownInitialFilterAdjustmentName.name(), initialFilterAdjustmentName);
                    }
                } else {
                    addExecutionError(ExecutionErrors.DuplicateFilterName.name(), filterName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownFilterTypeName.name(), filterTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownFilterKindName.name(), filterKindName);
        }
        
        return null;
    }
    
}
