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

package com.echothree.model.control.core.common.transfer;

import com.echothree.util.common.transfer.BaseTransfer;

public class EntityGeoPointAttributeTransfer
        extends BaseTransfer {
    
    private EntityAttributeTransfer entityAttribute;
    private EntityInstanceTransfer entityInstance;
    private Integer unformattedLatitude;
    private String latitude;
    private Integer unformattedLongitude;
    private String longitude;
    private String elevation;
    private String altitude;
    
    /** Creates a new instance of EntityGeoPointAttributeTransfer */
    public EntityGeoPointAttributeTransfer(EntityAttributeTransfer entityAttribute, EntityInstanceTransfer entityInstance, Integer unformattedLatitude,
            String latitude, Integer unformattedLongitude, String longitude, String elevation, String altitude) {
        this.entityAttribute = entityAttribute;
        this.entityInstance = entityInstance;
        this.unformattedLatitude = unformattedLatitude;
        this.latitude = latitude;
        this.unformattedLongitude = unformattedLongitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.altitude = altitude;
    }

    /**
     * @return the entityAttribute
     */
    public EntityAttributeTransfer getEntityAttribute() {
        return entityAttribute;
    }

    /**
     * @param entityAttribute the entityAttribute to set
     */
    public void setEntityAttribute(EntityAttributeTransfer entityAttribute) {
        this.entityAttribute = entityAttribute;
    }

    /**
     * @return the entityInstance
     */
    @Override
    public EntityInstanceTransfer getEntityInstance() {
        return entityInstance;
    }

    /**
     * @param entityInstance the entityInstance to set
     */
    @Override
    public void setEntityInstance(EntityInstanceTransfer entityInstance) {
        this.entityInstance = entityInstance;
    }

    /**
     * @return the unformattedLatitude
     */
    public Integer getUnformattedLatitude() {
        return unformattedLatitude;
    }

    /**
     * @param unformattedLatitude the unformattedLatitude to set
     */
    public void setUnformattedLatitude(Integer unformattedLatitude) {
        this.unformattedLatitude = unformattedLatitude;
    }

    /**
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the unformattedLongitude
     */
    public Integer getUnformattedLongitude() {
        return unformattedLongitude;
    }

    /**
     * @param unformattedLongitude the unformattedLongitude to set
     */
    public void setUnformattedLongitude(Integer unformattedLongitude) {
        this.unformattedLongitude = unformattedLongitude;
    }

    /**
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the elevation
     */
    public String getElevation() {
        return elevation;
    }

    /**
     * @param elevation the elevation to set
     */
    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    /**
     * @return the altitude
     */
    public String getAltitude() {
        return altitude;
    }

    /**
     * @param altitude the altitude to set
     */
    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }
    
}