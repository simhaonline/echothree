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

package com.echothree.model.control.inventory.server.control;

import com.echothree.model.control.inventory.server.transfer.InventoryTransferCaches;
import com.echothree.model.control.payment.server.control.PaymentControl;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.server.control.BaseModelControl;
import com.echothree.util.server.persistence.Session;

public abstract class BaseInventoryControl
        extends BaseModelControl {

    /** Creates a new instance of BaseInventoryControl */
    protected BaseInventoryControl() {
        super();
    }
    
    // --------------------------------------------------------------------------------
    //   Inventory Transfer Caches
    // --------------------------------------------------------------------------------
    
    private InventoryTransferCaches inventoryTransferCaches = null;
    
    public InventoryTransferCaches getInventoryTransferCaches(UserVisit userVisit) {
        if(inventoryTransferCaches == null) {
            var inventoryControl = (InventoryControl)Session.getModelController(InventoryControl.class);

            inventoryTransferCaches = new InventoryTransferCaches(userVisit, inventoryControl);
        }
        
        return inventoryTransferCaches;
    }

}