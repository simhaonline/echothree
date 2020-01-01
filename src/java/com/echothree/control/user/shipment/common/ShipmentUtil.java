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

package com.echothree.control.user.shipment.common;

import com.echothree.control.user.shipment.common.ShipmentRemote;
import com.echothree.control.user.shipment.server.ShipmentLocal;
import com.echothree.util.common.control.InitialContextUtils;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ShipmentUtil {
    
    private static ShipmentLocal cachedLocal = null;
    private static ShipmentRemote cachedRemote = null;
    
    public static ShipmentLocal getLocalHome()
            throws NamingException {
        if(cachedLocal == null) {
            InitialContext ctx = InitialContextUtils.getInstance().getInitialContext();

            cachedLocal = (ShipmentLocal)ctx.lookup("ejb:echothree/echothree-server/ShipmentBean!com.echothree.control.user.shipment.server.ShipmentLocal");
        }
        
        return cachedLocal;
    }
    
    public static ShipmentRemote getHome()
            throws NamingException {
        if(cachedRemote == null) {
            InitialContext ctx = InitialContextUtils.getInstance().getInitialContext();
            
            cachedRemote = (ShipmentRemote)ctx.lookup("ejb:echothree/echothree-server/ShipmentBean!com.echothree.control.user.shipment.common.ShipmentRemote");
        }
        
        return cachedRemote;
    }
    
}
