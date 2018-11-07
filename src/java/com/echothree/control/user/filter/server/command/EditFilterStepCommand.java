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

package com.echothree.control.user.filter.server.command;

import com.echothree.control.user.filter.common.edit.FilterEditFactory;
import com.echothree.control.user.filter.common.edit.FilterStepEdit;
import com.echothree.control.user.filter.common.form.EditFilterStepForm;
import com.echothree.control.user.filter.common.result.EditFilterStepResult;
import com.echothree.control.user.filter.common.result.FilterResultFactory;
import com.echothree.control.user.filter.common.spec.FilterStepSpec;
import com.echothree.model.control.filter.server.FilterControl;
import com.echothree.model.control.selector.common.SelectorConstants;
import com.echothree.model.control.selector.server.SelectorControl;
import com.echothree.model.data.filter.server.entity.Filter;
import com.echothree.model.data.filter.server.entity.FilterKind;
import com.echothree.model.data.filter.server.entity.FilterStep;
import com.echothree.model.data.filter.server.entity.FilterStepDescription;
import com.echothree.model.data.filter.server.entity.FilterStepDetail;
import com.echothree.model.data.filter.server.entity.FilterType;
import com.echothree.model.data.filter.server.value.FilterStepDescriptionValue;
import com.echothree.model.data.filter.server.value.FilterStepDetailValue;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.selector.server.entity.Selector;
import com.echothree.model.data.selector.server.entity.SelectorKind;
import com.echothree.model.data.selector.server.entity.SelectorType;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseEditCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditFilterStepCommand
        extends BaseEditCommand<FilterStepSpec, FilterStepEdit> {
    
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("FilterKindName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("FilterTypeName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("FilterName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("FilterStepName", FieldType.ENTITY_NAME, true, null, null)
        ));
        
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            new FieldDefinition("FilterStepName", FieldType.ENTITY_NAME, true, null, null),
            new FieldDefinition("FilterItemSelectorName", FieldType.ENTITY_NAME, false, null, null),
            new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
        ));
    }
    
    /** Creates a new instance of EditFilterStepCommand */
    public EditFilterStepCommand(UserVisitPK userVisitPK, EditFilterStepForm form) {
        super(userVisitPK, form, null, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    protected BaseResult execute() {
        FilterControl filterControl = (FilterControl)Session.getModelController(FilterControl.class);
        EditFilterStepResult result = FilterResultFactory.getEditFilterStepResult();
        String filterKindName = spec.getFilterKindName();
        FilterKind filterKind = filterControl.getFilterKindByName(filterKindName);
        
        if(filterKind != null) {
            String filterTypeName = spec.getFilterTypeName();
            FilterType filterType = filterControl.getFilterTypeByName(filterKind, filterTypeName);
            
            if(filterType != null) {
                String filterName = spec.getFilterName();
                Filter filter = filterControl.getFilterByName(filterType, filterName);
                
                if(filter != null) {
                    if(editMode.equals(EditMode.LOCK)) {
                        String filterStepName = spec.getFilterStepName();
                        FilterStep filterStep = filterControl.getFilterStepByName(filter, filterStepName);
                        
                        if(filterStep != null) {
                            result.setFilterStep(filterControl.getFilterStepTransfer(getUserVisit(), filterStep));
                            
                            if(lockEntity(filterStep)) {
                                FilterStepDescription filterStepDescription = filterControl.getFilterStepDescription(filterStep, getPreferredLanguage());
                                FilterStepEdit edit = FilterEditFactory.getFilterStepEdit();
                                FilterStepDetail filterStepDetail = filterStep.getLastDetail();
                                Selector filterItemSelector = filterStepDetail.getFilterItemSelector();
                                
                                result.setEdit(edit);
                                edit.setFilterStepName(filterStepDetail.getFilterStepName());
                                edit.setFilterItemSelectorName(filterItemSelector == null? null: filterItemSelector.getLastDetail().getSelectorName());
                                
                                if(filterStepDescription != null) {
                                    edit.setDescription(filterStepDescription.getDescription());
                                }
                            } else {
                                addExecutionError(ExecutionErrors.EntityLockFailed.name());
                            }
                            
                            result.setEntityLock(getEntityLockTransfer(filterStep));
                        } else {
                            addExecutionError(ExecutionErrors.UnknownFilterStepName.name(), filterStepName);
                        }
                    } else if(editMode.equals(EditMode.UPDATE)) {
                        String filterStepName = spec.getFilterStepName();
                        FilterStep filterStep = filterControl.getFilterStepByNameForUpdate(filter, filterStepName);
                        
                        if(filterStep != null) {
                            filterStepName = edit.getFilterStepName();
                            FilterStep duplicateFilterStep = filterControl.getFilterStepByName(filter, filterStepName);
                            
                            if(duplicateFilterStep == null || filterStep.equals(duplicateFilterStep)) {
                                String filterItemSelectorName = edit.getFilterItemSelectorName();
                                Selector filterItemSelector = null;
                                
                                if(filterItemSelectorName != null) {
                                    SelectorControl selectorControl = (SelectorControl)Session.getModelController(SelectorControl.class);
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
                                    if(lockEntityForUpdate(filterStep)) {
                                        try {
                                            PartyPK partyPK = getPartyPK();
                                            FilterStepDetailValue filterStepDetailValue = filterControl.getFilterStepDetailValueForUpdate(filterStep);
                                            FilterStepDescription filterStepDescription = filterControl.getFilterStepDescriptionForUpdate(filterStep, getPreferredLanguage());
                                            String description = edit.getDescription();
                                            
                                            filterStepDetailValue.setFilterStepName(edit.getFilterStepName());
                                            filterStepDetailValue.setFilterItemSelectorPK(filterItemSelector == null? null: filterItemSelector.getPrimaryKey());
                                            
                                            filterControl.updateFilterStepFromValue(filterStepDetailValue, partyPK);
                                            
                                            if(filterStepDescription == null && description != null) {
                                                filterControl.createFilterStepDescription(filterStep, getPreferredLanguage(), description, partyPK);
                                            } else if(filterStepDescription != null && description == null) {
                                                filterControl.deleteFilterStepDescription(filterStepDescription, partyPK);
                                            } else if(filterStepDescription != null && description != null) {
                                                FilterStepDescriptionValue filterStepDescriptionValue = filterControl.getFilterStepDescriptionValue(filterStepDescription);
                                                
                                                filterStepDescriptionValue.setDescription(description);
                                                filterControl.updateFilterStepDescriptionFromValue(filterStepDescriptionValue, partyPK);
                                            }
                                        } finally {
                                            unlockEntity(filterStep);
                                        }
                                    } else {
                                        addExecutionError(ExecutionErrors.EntityLockStale.name());
                                    }
                                } else {
                                    addExecutionError(ExecutionErrors.UnknownFilterItemSelectorName.name(), filterItemSelectorName);
                                }
                            } else {
                                addExecutionError(ExecutionErrors.DuplicateFilterStepName.name(), filterStepName);
                            }
                        } else {
                            addExecutionError(ExecutionErrors.UnknownFilterStepName.name(), filterStepName);
                        }
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnknownFilterName.name(), filterName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownFilterTypeName.name(), filterTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownFilterKindName.name(), filterKindName);
        }
        
        return result;
    }
    
}
