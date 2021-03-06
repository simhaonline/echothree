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

package com.echothree.control.user.offer.server.command;

import com.echothree.control.user.offer.common.form.GetOfferItemForm;
import com.echothree.control.user.offer.common.result.GetOfferItemResult;
import com.echothree.control.user.offer.common.result.OfferResultFactory;
import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.control.item.server.ItemControl;
import com.echothree.model.control.offer.server.OfferControl;
import com.echothree.model.control.party.common.PartyTypes;
import com.echothree.model.control.security.common.SecurityRoleGroups;
import com.echothree.model.control.security.common.SecurityRoles;
import com.echothree.model.data.item.server.entity.Item;
import com.echothree.model.data.offer.server.entity.Offer;
import com.echothree.model.data.offer.server.entity.OfferItem;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSingleEntityCommand;
import com.echothree.util.server.control.CommandSecurityDefinition;
import com.echothree.util.server.control.PartyTypeDefinition;
import com.echothree.util.server.control.SecurityRoleDefinition;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetOfferItemCommand
        extends BaseSingleEntityCommand<OfferItem, GetOfferItemForm> {
    
    private final static CommandSecurityDefinition COMMAND_SECURITY_DEFINITION;
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        COMMAND_SECURITY_DEFINITION = new CommandSecurityDefinition(Collections.unmodifiableList(Arrays.asList(
                new PartyTypeDefinition(PartyTypes.UTILITY.name(), null),
                new PartyTypeDefinition(PartyTypes.EMPLOYEE.name(), Collections.unmodifiableList(Arrays.asList(
                        new SecurityRoleDefinition(SecurityRoleGroups.OfferItem.name(), SecurityRoles.Review.name())
                        )))
                )));
        
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("OfferName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("ItemName", FieldType.ENTITY_NAME, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetOfferItemCommand */
    public GetOfferItemCommand(UserVisitPK userVisitPK, GetOfferItemForm form) {
        super(userVisitPK, form, COMMAND_SECURITY_DEFINITION, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected OfferItem getEntity() {
        var offerControl = (OfferControl)Session.getModelController(OfferControl.class);
        OfferItem offerItem = null;
        String offerName = form.getOfferName();
        Offer offer = offerControl.getOfferByName(offerName);
        
        if(offer != null) {
            var itemControl = (ItemControl)Session.getModelController(ItemControl.class);
            String itemName = form.getItemName();
            Item item = itemControl.getItemByName(itemName);
            
            if(item != null) {
                offerItem = offerControl.getOfferItem(offer, item);
                
                if(offerItem != null) {
                    sendEventUsingNames(offerItem.getPrimaryKey(), EventTypes.READ.name(), null, null, getPartyPK());
                } else {
                    addExecutionError(ExecutionErrors.UnknownOfferItem.name(), offer.getLastDetail().getOfferName(),
                            item.getLastDetail().getItemName());
                }
            } else {
                addExecutionError(ExecutionErrors.UnknownItemName.name(), itemName);
            }
        } else {
            addExecutionError(ExecutionErrors.UnknownOfferName.name(), offerName);
        }
        
        return offerItem;
    }
    
    @Override
    protected BaseResult getTransfer(OfferItem offerItem) {
        var offerControl = (OfferControl)Session.getModelController(OfferControl.class);
        GetOfferItemResult result = OfferResultFactory.getGetOfferItemResult();

        if(offerItem != null) {
            result.setOfferItem(offerControl.getOfferItemTransfer(getUserVisit(), offerItem));
        }
        
        return result;
    }
    
}
