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

package com.echothree.util.common.persistence;

import java.io.Serializable;

public class BasePK
        extends Object
        implements Serializable {
    
    private final String componentVendorName;
    private final String entityTypeName;
    private final Long entityId;
    
    private transient Integer _hashCode;
    private transient String _entityRef;
    private transient String _stringValue;
    
    /** Creates a new instance of BasePK */
    public BasePK(String componentVendorName, String entityTypeName, Long entityId) {
        this.componentVendorName = componentVendorName;
        this.entityTypeName = entityTypeName;
        this.entityId = entityId;
    }
    
    @Override
    public int hashCode() {
        if(_hashCode == null) {
            _hashCode = this.entityId.hashCode();
        }
        
        return _hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BasePK) {
            BasePK that = (BasePK)obj;
            
            return componentVendorName.equals(that.componentVendorName)
                    && entityTypeName.equals(that.entityTypeName)
                    &&  entityId.equals(that.entityId);
        } else {
            return false;
        }
    }
    
    /**
     * @return String representation of this PK in the form of "componentVendorName.entityTypeName.entityId".
     */
    public String getEntityRef() {
        if(_entityRef == null) {
            _entityRef = new StringBuilder().append(getComponentVendorName()).append(".").append(getEntityTypeName()).append(".").append(getEntityId()).toString();
        }
        
        return _entityRef;
    }

    /**
     * @return String representation of this PK in the form of "[.componentVendorName.entityTypeName.entityId]".
     */
    @Override
    public String toString() {
        if( _stringValue == null ) {
            _stringValue = new StringBuilder("[.").append(getEntityRef()).append(']').toString();
        }
        
        return _stringValue;
    }
    
     /**
     * @return the componentVendorName
     */
   public String getComponentVendorName() {
        return componentVendorName;
    }
    
    /**
     * @return the entityTypeName
     */
    public String getEntityTypeName() {
        return entityTypeName;
    }
    
    /**
     * @return the entityId
     */
    public Long getEntityId() {
        return entityId;
    }
    
}
