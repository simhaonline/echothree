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
package com.echothree.model.control.graphql.server.util;

import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.model.data.user.server.entity.UserSession;
import com.echothree.model.data.user.server.entity.UserVisit;

public class GraphQlContext {

    private final UserVisitPK userVisitPK;
    private final UserVisit userVisit;
    private final UserSession userSession;
    private final String remoteInet4Address;

    public GraphQlContext(UserVisitPK userVisitPK, UserVisit userVisit, UserSession userSession, String remoteInet4Address) {
        this.userVisitPK = userVisitPK;
        this.userVisit = userVisit;
        this.userSession = userSession;
        this.remoteInet4Address = remoteInet4Address;
    }
    
    public UserVisitPK getUserVisitPK() {
        return userVisitPK;
    }
    
    public UserVisit getUserVisit() {
        return userVisit;
    }
    
    public UserSession getUserSession() {
        return userSession;
    }
    
    String getRemoteInet4Address() {
        return remoteInet4Address;
    }

}
