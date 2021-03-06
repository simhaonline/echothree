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

package com.echothree.model.control.returnpolicy.common;

public interface ReturnPolicyConstants {
    
    String ReturnKind_CUSTOMER_RETURN = "CUSTOMER_RETURN";
    String ReturnKind_VENDOR_RETURN   = "VENDOR_RETURN";
    
    String ReturnType_CREDIT_ON_RETURN                   = "CREDIT_ON_RETURN";
    String ReturnType_REFUND_ITEM_ON_RETURN              = "REFUND_ITEM_ON_RETURN";
    String ReturnType_REFUND_ITEM_AND_SHIPPING_ON_RETURN = "REFUND_ITEM_AND_SHIPPING_ON_RETURN";
    String ReturnType_REPLACEMENT_WITHOUT_RETURN         = "REPLACEMENT_WITHOUT_RETURN";
    String ReturnType_CROSS_SHIP_REPLACEMENT             = "CROSS_SHIP_REPLACEMENT";
    
}
