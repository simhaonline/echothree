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

package com.echothree.model.control.core.server.database;

import com.echothree.model.data.core.server.entity.EntityAttribute;
import com.echothree.util.server.persistence.Session;
import java.util.List;

public class EntityInstancesByEntityEntityAttributeQuery
        extends BaseEntityAttributeQuery<EntityInstanceResult> {
    
    public EntityInstancesByEntityEntityAttributeQuery() {
        super("SELECT eea_eni_entityinstanceid AS EntityInstancePK "
                + "FROM entityentityattributes "
                + "WHERE eea_ena_entityattributeid = ? AND eea_thrutime = ?");
    }
    
    @Override
    public List<EntityInstanceResult> execute(final EntityAttribute entityAttribute) {
        return super.execute(entityAttribute, Session.MAX_TIME_LONG);
    }
    
}
