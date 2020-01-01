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

package com.echothree.model.control.core.server.transfer;

import com.echothree.model.control.core.common.CoreProperties;
import com.echothree.model.control.core.common.transfer.ComponentVendorTransfer;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.data.core.server.entity.ComponentVendor;
import com.echothree.model.data.core.server.entity.ComponentVendorDetail;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.common.form.TransferProperties;
import java.util.Set;

public class ComponentVendorTransferCache
        extends BaseCoreTransferCache<ComponentVendor, ComponentVendorTransfer> {
    
    TransferProperties transferProperties;
    boolean filterComponentVendorName;
    boolean filterDescription;
    boolean filterEntityInstance;

    /** Creates a new instance of ComponentVendorTransferCache */
    public ComponentVendorTransferCache(UserVisit userVisit, CoreControl coreControl) {
        super(userVisit, coreControl);

        transferProperties = session.getTransferProperties();
        if(transferProperties != null) {
            Set<String> properties = transferProperties.getProperties(ComponentVendorTransfer.class);
            
            if(properties != null) {
                filterComponentVendorName = !properties.contains(CoreProperties.COMPONENT_VENDOR_NAME);
                filterDescription = !properties.contains(CoreProperties.DESCRIPTION);
                filterEntityInstance = !properties.contains(CoreProperties.ENTITY_INSTANCE);
            }
        }
        
        setIncludeEntityInstance(!filterEntityInstance);
    }
    
    public ComponentVendorTransfer getComponentVendorTransfer(ComponentVendor componentVendor) {
        ComponentVendorTransfer componentVendorTransfer = get(componentVendor);
        
        if(componentVendorTransfer == null) {
            ComponentVendorDetail componentVendorDetail = componentVendor.getLastDetail();
            String componentVendorName = filterComponentVendorName ? null : componentVendorDetail.getComponentVendorName();
            String description = filterDescription ? null : componentVendorDetail.getDescription();
            
            componentVendorTransfer = new ComponentVendorTransfer(componentVendorName, description);
            put(componentVendor, componentVendorTransfer);
        }
        
        return componentVendorTransfer;
    }
    
}
