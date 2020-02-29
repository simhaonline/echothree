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

package com.echothree.control.user.index.server.command;

import com.echothree.control.user.index.common.edit.IndexEditFactory;
import com.echothree.control.user.index.common.edit.IndexTypeEdit;
import com.echothree.control.user.index.common.form.EditIndexTypeForm;
import com.echothree.control.user.index.common.result.EditIndexTypeResult;
import com.echothree.control.user.index.common.result.IndexResultFactory;
import com.echothree.control.user.index.common.spec.IndexTypeSpec;
import com.echothree.model.control.core.server.logic.EntityTypeLogic;
import com.echothree.model.control.index.server.IndexControl;
import com.echothree.model.control.party.common.PartyTypes;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.core.server.entity.ComponentVendor;
import com.echothree.model.data.core.server.entity.EntityType;
import com.echothree.model.data.core.server.entity.EntityTypeDetail;
import com.echothree.model.data.index.server.entity.IndexType;
import com.echothree.model.data.index.server.entity.IndexTypeDescription;
import com.echothree.model.data.index.server.entity.IndexTypeDetail;
import com.echothree.model.data.index.server.value.IndexTypeDescriptionValue;
import com.echothree.model.data.index.server.value.IndexTypeDetailValue;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseAbstractEditCommand;
import com.echothree.util.server.control.CommandSecurityDefinition;
import com.echothree.util.server.control.PartyTypeDefinition;
import com.echothree.util.server.control.SecurityRoleDefinition;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditIndexTypeCommand
        extends BaseAbstractEditCommand<IndexTypeSpec, IndexTypeEdit, EditIndexTypeResult, IndexType, IndexType> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyTypes.UTILITY.name(), null),
                new PartyTypeDefinition(PartyTypes.EMPLOYEE.name(), Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.IndexType.name(), SecurityRoles.Edit.name())
                        )))
                )));
        
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("IndexTypeName", FieldType.ENTITY_NAME, true, null, null)
                ));
        
        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("IndexTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("ComponentVendorName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("EntityTypeName", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("IsDefault", FieldType.BOOLEAN, true, null, null),
                new FieldDefinition("SortOrder", FieldType.SIGNED_INTEGER, true, null, null),
                new FieldDefinition("Description", FieldType.STRING, false, 1L, 80L)
                ));
    }
    
    /** Creates a new instance of EditIndexTypeCommand */
    public EditIndexTypeCommand(UserVisitPK userVisitPK, EditIndexTypeForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }
    
    @Override
    public EditIndexTypeResult getResult() {
        return IndexResultFactory.getEditIndexTypeResult();
    }

    @Override
    public IndexTypeEdit getEdit() {
        return IndexEditFactory.getIndexTypeEdit();
    }

    @Override
    public IndexType getEntity(EditIndexTypeResult result) {
        var indexControl = (IndexControl)Session.getModelController(IndexControl.class);
        IndexType indexType;
        String indexTypeName = spec.getIndexTypeName();

        if(editMode.equals(EditMode.LOCK) || editMode.equals(EditMode.ABANDON)) {
            indexType = indexControl.getIndexTypeByName(indexTypeName);
        } else { // EditMode.UPDATE
            indexType = indexControl.getIndexTypeByNameForUpdate(indexTypeName);
        }

        if(indexType == null) {
            addExecutionError(ExecutionErrors.UnknownIndexTypeName.name(), indexTypeName);
        }

        return indexType;
    }

    @Override
    public IndexType getLockEntity(IndexType indexType) {
        return indexType;
    }

    @Override
    public void fillInResult(EditIndexTypeResult result, IndexType indexType) {
        var indexControl = (IndexControl)Session.getModelController(IndexControl.class);

        result.setIndexType(indexControl.getIndexTypeTransfer(getUserVisit(), indexType));
    }

    EntityType entityType;
    
    @Override
    public void doLock(IndexTypeEdit edit, IndexType indexType) {
        var indexControl = (IndexControl)Session.getModelController(IndexControl.class);
        IndexTypeDescription indexTypeDescription = indexControl.getIndexTypeDescription(indexType, getPreferredLanguage());
        IndexTypeDetail indexTypeDetail = indexType.getLastDetail();
        
        entityType = indexTypeDetail.getEntityType();
        
        EntityTypeDetail entityTypeDetail = entityType == null ? null : entityType.getLastDetail();
        ComponentVendor componentVendor = entityTypeDetail == null ? null : entityTypeDetail.getComponentVendor();

        edit.setIndexTypeName(indexTypeDetail.getIndexTypeName());
        edit.setComponentVendorName(componentVendor == null ? null : componentVendor.getLastDetail().getComponentVendorName());
        edit.setEntityTypeName(entityTypeDetail == null ? null : entityTypeDetail.getEntityTypeName());
        edit.setIsDefault(indexTypeDetail.getIsDefault().toString());
        edit.setSortOrder(indexTypeDetail.getSortOrder().toString());

        if(indexTypeDescription != null) {
            edit.setDescription(indexTypeDescription.getDescription());
        }
    }
    
    @Override
    public void canUpdate(IndexType indexType) {
        var indexControl = (IndexControl)Session.getModelController(IndexControl.class);
        String indexTypeName = edit.getIndexTypeName();
        IndexType duplicateIndexType = indexControl.getIndexTypeByName(indexTypeName);

        if(duplicateIndexType != null && !indexType.equals(duplicateIndexType)) {
            addExecutionError(ExecutionErrors.DuplicateIndexTypeName.name(), indexTypeName);
        } else {
            String componentVendorName = edit.getComponentVendorName();
            String entityTypeName = edit.getEntityTypeName();
            int parameterCount = (componentVendorName == null ? 0 : 1) + (entityTypeName == null ? 0 : 1);

            if(parameterCount == 0 || parameterCount == 2) {
                entityType = EntityTypeLogic.getInstance().getEntityTypeByName(this, componentVendorName, entityTypeName);
            } else {
                addExecutionError(ExecutionErrors.InvalidParameterCount.name());
            }
        }
    }

    @Override
    public void doUpdate(IndexType indexType) {
        var indexControl = (IndexControl)Session.getModelController(IndexControl.class);
        PartyPK partyPK = getPartyPK();
        IndexTypeDetailValue indexTypeDetailValue = indexControl.getIndexTypeDetailValueForUpdate(indexType);
        IndexTypeDescription indexTypeDescription = indexControl.getIndexTypeDescriptionForUpdate(indexType, getPreferredLanguage());
        String description = edit.getDescription();

        indexTypeDetailValue.setIndexTypeName(edit.getIndexTypeName());
        indexTypeDetailValue.setEntityTypePK(entityType == null ? null : entityType.getPrimaryKey());
        indexTypeDetailValue.setIsDefault(Boolean.valueOf(edit.getIsDefault()));
        indexTypeDetailValue.setSortOrder(Integer.valueOf(edit.getSortOrder()));

        indexControl.updateIndexTypeFromValue(indexTypeDetailValue, partyPK);

        if(indexTypeDescription == null && description != null) {
            indexControl.createIndexTypeDescription(indexType, getPreferredLanguage(), description, partyPK);
        } else if(indexTypeDescription != null && description == null) {
            indexControl.deleteIndexTypeDescription(indexTypeDescription, partyPK);
        } else if(indexTypeDescription != null && description != null) {
            IndexTypeDescriptionValue indexTypeDescriptionValue = indexControl.getIndexTypeDescriptionValue(indexTypeDescription);

            indexTypeDescriptionValue.setDescription(description);
            indexControl.updateIndexTypeDescriptionFromValue(indexTypeDescriptionValue, partyPK);
        }
    }
    
}
