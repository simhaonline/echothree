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

package com.echothree.control.user.inventory.server.command;

import com.echothree.control.user.inventory.common.edit.InventoryEditFactory;
import com.echothree.control.user.inventory.common.edit.LotAliasTypeDescriptionEdit;
import com.echothree.control.user.inventory.common.form.EditLotAliasTypeDescriptionForm;
import com.echothree.control.user.inventory.common.result.EditLotAliasTypeDescriptionResult;
import com.echothree.control.user.inventory.common.result.InventoryResultFactory;
import com.echothree.control.user.inventory.common.spec.LotAliasTypeDescriptionSpec;
import com.echothree.model.control.inventory.server.InventoryControl;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.inventory.server.entity.LotAliasType;
import com.echothree.model.data.inventory.server.entity.LotAliasTypeDescription;
import com.echothree.model.data.inventory.server.entity.LotType;
import com.echothree.model.data.inventory.server.value.LotAliasTypeDescriptionValue;
import com.echothree.model.data.party.server.entity.Language;
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

public class EditLotAliasTypeDescriptionCommand
        extends BaseAbstractEditCommand<LotAliasTypeDescriptionSpec, LotAliasTypeDescriptionEdit, EditLotAliasTypeDescriptionResult, LotAliasTypeDescription, LotAliasType> {

    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;

    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyConstants.PartyType_UTILITY, null),
                new PartyTypeDefinition(PartyConstants.PartyType_EMPLOYEE, Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.LotAliasType.name(), SecurityRoles.Description.name())
                        )))
                )));

        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("LotTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("LotAliasTypeName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("LanguageIsoName", FieldType.ENTITY_NAME, true, null, null)
                ));

        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("Description", FieldType.STRING, true, 1L, 80L)
                ));
    }

    /** Creates a new instance of EditLotAliasTypeDescriptionCommand */
    public EditLotAliasTypeDescriptionCommand(UserVisitPK userVisitPK, EditLotAliasTypeDescriptionForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }

    @Override
    public EditLotAliasTypeDescriptionResult getResult() {
        return InventoryResultFactory.getEditLotAliasTypeDescriptionResult();
    }

    @Override
    public LotAliasTypeDescriptionEdit getEdit() {
        return InventoryEditFactory.getLotAliasTypeDescriptionEdit();
    }

    @Override
    public LotAliasTypeDescription getEntity(EditLotAliasTypeDescriptionResult result) {
        var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);
        LotAliasTypeDescription lotAliasTypeDescription = null;
        String lotTypeName = spec.getLotTypeName();
        LotType lotType = inventoryControl.getLotTypeByName(lotTypeName);

        if(lotType != null) {
            String lotAliasTypeName = spec.getLotAliasTypeName();
            LotAliasType lotAliasType = inventoryControl.getLotAliasTypeByName(lotType, lotAliasTypeName);

            if(lotAliasType != null) {
                var partyControl = (PartyControl)Session.getModelController(PartyControl.class);
                String languageIsoName = spec.getLanguageIsoName();
                Language language = partyControl.getLanguageByIsoName(languageIsoName);

                if(language != null) {
                    if(editMode.equals(EditMode.LOCK) || editMode.equals(EditMode.ABANDON)) {
                        lotAliasTypeDescription = inventoryControl.getLotAliasTypeDescription(lotAliasType, language);
                    } else { // EditMode.UPDATE
                        lotAliasTypeDescription = inventoryControl.getLotAliasTypeDescriptionForUpdate(lotAliasType, language);
                    }

                    if(lotAliasTypeDescription == null) {
                        addExecutionError(ExecutionErrors.UnknownLotAliasTypeDescription.name(), lotTypeName, lotAliasTypeName, languageIsoName);
                    }
                } else {
                    addExecutionError(ExecutionErrors.UnknownLanguageIsoName.name(), languageIsoName);
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownLotAliasTypeName.name(), lotTypeName, lotAliasTypeName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownLotTypeName.name(), lotTypeName);
        }

        return lotAliasTypeDescription;
    }

    @Override
    public LotAliasType getLockEntity(LotAliasTypeDescription lotAliasTypeDescription) {
        return lotAliasTypeDescription.getLotAliasType();
    }

    @Override
    public void fillInResult(EditLotAliasTypeDescriptionResult result, LotAliasTypeDescription lotAliasTypeDescription) {
        var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);

        result.setLotAliasTypeDescription(inventoryControl.getLotAliasTypeDescriptionTransfer(getUserVisit(), lotAliasTypeDescription));
    }

    @Override
    public void doLock(LotAliasTypeDescriptionEdit edit, LotAliasTypeDescription lotAliasTypeDescription) {
        edit.setDescription(lotAliasTypeDescription.getDescription());
    }

    @Override
    public void doUpdate(LotAliasTypeDescription lotAliasTypeDescription) {
        var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);
        LotAliasTypeDescriptionValue lotAliasTypeDescriptionValue = inventoryControl.getLotAliasTypeDescriptionValue(lotAliasTypeDescription);

        lotAliasTypeDescriptionValue.setDescription(edit.getDescription());

        inventoryControl.updateLotAliasTypeDescriptionFromValue(lotAliasTypeDescriptionValue, getPartyPK());
    }


}
