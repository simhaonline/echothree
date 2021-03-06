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

package com.echothree.model.control.order.server.transfer;

import com.echothree.model.control.order.common.transfer.OrderAliasTypeTransfer;
import com.echothree.model.control.order.common.transfer.OrderTypeTransfer;
import com.echothree.model.control.order.server.OrderControl;
import com.echothree.model.data.order.server.entity.OrderAliasType;
import com.echothree.model.data.order.server.entity.OrderAliasTypeDetail;
import com.echothree.model.data.user.server.entity.UserVisit;

public class OrderAliasTypeTransferCache
        extends BaseOrderTransferCache<OrderAliasType, OrderAliasTypeTransfer> {
    
    /** Creates a new instance of OrderAliasTypeTransferCache */
    public OrderAliasTypeTransferCache(UserVisit userVisit, OrderControl orderControl) {
        super(userVisit, orderControl);
        
        setIncludeEntityInstance(true);
    }
    
    public OrderAliasTypeTransfer getOrderAliasTypeTransfer(OrderAliasType orderAliasType) {
        OrderAliasTypeTransfer orderAliasTypeTransfer = get(orderAliasType);
        
        if(orderAliasTypeTransfer == null) {
            OrderAliasTypeDetail orderAliasTypeDetail = orderAliasType.getLastDetail();
            OrderTypeTransfer orderType = orderControl.getOrderTypeTransfer(userVisit, orderAliasTypeDetail.getOrderType());
            String orderAliasTypeName = orderAliasTypeDetail.getOrderAliasTypeName();
            String validationPattern = orderAliasTypeDetail.getValidationPattern();
            Boolean isDefault = orderAliasTypeDetail.getIsDefault();
            Integer sortOrder = orderAliasTypeDetail.getSortOrder();
            String description = orderControl.getBestOrderAliasTypeDescription(orderAliasType, getLanguage());
            
            orderAliasTypeTransfer = new OrderAliasTypeTransfer(orderType, orderAliasTypeName, validationPattern, isDefault, sortOrder, description);
            put(orderAliasType, orderAliasTypeTransfer);
        }
        
        return orderAliasTypeTransfer;
    }
    
}
