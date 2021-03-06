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

package com.echothree.model.control.core.common.transfer;

import com.echothree.util.common.transfer.BaseTransfer;

public class EventTransfer
        extends BaseTransfer {
    
    private Long unformattedEventTime;
    private String eventTime;
    private Integer eventTimeSequence;
    private EntityInstanceTransfer entityInstance;
    private EventTypeTransfer eventType;
    private EntityInstanceTransfer relatedEntityInstance;
    private EventTypeTransfer relatedEventType;
    private EntityInstanceTransfer createdBy;
    
    /** Creates a new instance of EventTransfer */
    public EventTransfer(Long unformattedEventTime, String eventTime, Integer eventTimeSequence, EntityInstanceTransfer entityInstance,
            EventTypeTransfer eventType, EntityInstanceTransfer relatedEntityInstance, EventTypeTransfer relatedEventType, EntityInstanceTransfer createdBy) {
        this.unformattedEventTime = unformattedEventTime;
        this.eventTime = eventTime;
        this.eventTimeSequence = eventTimeSequence;
        this.entityInstance = entityInstance;
        this.eventType = eventType;
        this.relatedEntityInstance = relatedEntityInstance;
        this.relatedEventType = relatedEventType;
        this.createdBy = createdBy;
    }

    /**
     * @return the unformattedEventTime
     */
    public Long getUnformattedEventTime() {
        return unformattedEventTime;
    }

    /**
     * @param unformattedEventTime the unformattedEventTime to set
     */
    public void setUnformattedEventTime(Long unformattedEventTime) {
        this.unformattedEventTime = unformattedEventTime;
    }

    /**
     * @return the eventTime
     */
    public String getEventTime() {
        return eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * @return the eventTimeSequence
     */
    public Integer getEventTimeSequence() {
        return eventTimeSequence;
    }

    /**
     * @param eventTimeSequence the eventTimeSequence to set
     */
    public void setEventTimeSequence(Integer eventTimeSequence) {
        this.eventTimeSequence = eventTimeSequence;
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
     * @return the eventType
     */
    public EventTypeTransfer getEventType() {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(EventTypeTransfer eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the relatedEntityInstance
     */
    public EntityInstanceTransfer getRelatedEntityInstance() {
        return relatedEntityInstance;
    }

    /**
     * @param relatedEntityInstance the relatedEntityInstance to set
     */
    public void setRelatedEntityInstance(EntityInstanceTransfer relatedEntityInstance) {
        this.relatedEntityInstance = relatedEntityInstance;
    }

    /**
     * @return the relatedEventType
     */
    public EventTypeTransfer getRelatedEventType() {
        return relatedEventType;
    }

    /**
     * @param relatedEventType the relatedEventType to set
     */
    public void setRelatedEventType(EventTypeTransfer relatedEventType) {
        this.relatedEventType = relatedEventType;
    }

    /**
     * @return the createdBy
     */
    public EntityInstanceTransfer getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(EntityInstanceTransfer createdBy) {
        this.createdBy = createdBy;
    }

}
