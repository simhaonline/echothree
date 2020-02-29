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

package com.echothree.model.control.chain.server.transfer;

import com.echothree.model.control.chain.common.transfer.ChainEntityRoleTypeTransfer;
import com.echothree.model.control.chain.common.transfer.ChainInstanceEntityRoleTransfer;
import com.echothree.model.control.chain.common.transfer.ChainInstanceTransfer;
import com.echothree.model.control.chain.server.ChainControl;
import com.echothree.model.control.core.common.transfer.EntityInstanceTransfer;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.data.chain.server.entity.ChainInstanceEntityRole;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.server.persistence.Session;

public class ChainInstanceEntityRoleTransferCache
        extends BaseChainTransferCache<ChainInstanceEntityRole, ChainInstanceEntityRoleTransfer> {
    
    CoreControl coreControl = (CoreControl)Session.getModelController(CoreControl.class);
    
    /** Creates a new instance of ChainInstanceEntityRoleTransferCache */
    public ChainInstanceEntityRoleTransferCache(UserVisit userVisit, ChainControl chainControl) {
        super(userVisit, chainControl);
    }
    
    public ChainInstanceEntityRoleTransfer getChainInstanceEntityRoleTransfer(ChainInstanceEntityRole chainInstanceEntityRole) {
        ChainInstanceEntityRoleTransfer chainInstanceEntityRoleTransfer = get(chainInstanceEntityRole);
        
        if(chainInstanceEntityRoleTransfer == null) {
            ChainInstanceTransfer chainInstance = chainControl.getChainInstanceTransfer(userVisit, chainInstanceEntityRole.getChainInstance());
            ChainEntityRoleTypeTransfer chainEntityRoleType = chainControl.getChainEntityRoleTypeTransfer(userVisit, chainInstanceEntityRole.getChainEntityRoleType());
            EntityInstanceTransfer entityInstance = coreControl.getEntityInstanceTransfer(userVisit, chainInstanceEntityRole.getEntityInstance(), false, false, false, false, false);
            
            chainInstanceEntityRoleTransfer = new ChainInstanceEntityRoleTransfer(chainInstance, chainEntityRoleType, entityInstance);
            put(chainInstanceEntityRole, chainInstanceEntityRoleTransfer);
        }
        
        return chainInstanceEntityRoleTransfer;
    }
    
}
