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

package com.echothree.model.control.item.common.transfer;

import com.echothree.model.control.accounting.common.transfer.CurrencyTransfer;
import com.echothree.model.control.inventory.common.transfer.InventoryConditionTransfer;
import com.echothree.model.control.uom.common.transfer.UnitOfMeasureTypeTransfer;
import com.echothree.util.common.transfer.BaseTransfer;

public class ItemUnitPriceLimitTransfer
        extends BaseTransfer {
    
    private ItemTransfer item;
    private InventoryConditionTransfer inventoryCondition;
    private UnitOfMeasureTypeTransfer unitOfMeasureType;
    private CurrencyTransfer currency;
    private Long unformattedMinimumUnitPrice;
    private String minimumUnitPrice;
    private Long unformattedMaximumUnitPrice;
    private String maximumUnitPrice;
    
    /** Creates a new instance of ItemUnitPriceLimitTransfer */
    public ItemUnitPriceLimitTransfer(ItemTransfer item, InventoryConditionTransfer inventoryCondition, UnitOfMeasureTypeTransfer unitOfMeasureType,
            CurrencyTransfer currency, Long unformattedMinimumUnitPrice, String minimumUnitPrice, Long unformattedMaximumUnitPrice,
            String maximumUnitPrice) {
        this.item = item;
        this.inventoryCondition = inventoryCondition;
        this.unitOfMeasureType = unitOfMeasureType;
        this.currency = currency;
        this.unformattedMinimumUnitPrice = unformattedMinimumUnitPrice;
        this.minimumUnitPrice = minimumUnitPrice;
        this.unformattedMaximumUnitPrice = unformattedMaximumUnitPrice;
        this.maximumUnitPrice = maximumUnitPrice;
    }

    /**
     * @return the item
     */
    public ItemTransfer getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(ItemTransfer item) {
        this.item = item;
    }

    /**
     * @return the inventoryCondition
     */
    public InventoryConditionTransfer getInventoryCondition() {
        return inventoryCondition;
    }

    /**
     * @param inventoryCondition the inventoryCondition to set
     */
    public void setInventoryCondition(InventoryConditionTransfer inventoryCondition) {
        this.inventoryCondition = inventoryCondition;
    }

    /**
     * @return the unitOfMeasureType
     */
    public UnitOfMeasureTypeTransfer getUnitOfMeasureType() {
        return unitOfMeasureType;
    }

    /**
     * @param unitOfMeasureType the unitOfMeasureType to set
     */
    public void setUnitOfMeasureType(UnitOfMeasureTypeTransfer unitOfMeasureType) {
        this.unitOfMeasureType = unitOfMeasureType;
    }

    /**
     * @return the currency
     */
    public CurrencyTransfer getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(CurrencyTransfer currency) {
        this.currency = currency;
    }

    /**
     * @return the unformattedMinimumUnitPrice
     */
    public Long getUnformattedMinimumUnitPrice() {
        return unformattedMinimumUnitPrice;
    }

    /**
     * @param unformattedMinimumUnitPrice the unformattedMinimumUnitPrice to set
     */
    public void setUnformattedMinimumUnitPrice(Long unformattedMinimumUnitPrice) {
        this.unformattedMinimumUnitPrice = unformattedMinimumUnitPrice;
    }

    /**
     * @return the minimumUnitPrice
     */
    public String getMinimumUnitPrice() {
        return minimumUnitPrice;
    }

    /**
     * @param minimumUnitPrice the minimumUnitPrice to set
     */
    public void setMinimumUnitPrice(String minimumUnitPrice) {
        this.minimumUnitPrice = minimumUnitPrice;
    }

    /**
     * @return the unformattedMaximumUnitPrice
     */
    public Long getUnformattedMaximumUnitPrice() {
        return unformattedMaximumUnitPrice;
    }

    /**
     * @param unformattedMaximumUnitPrice the unformattedMaximumUnitPrice to set
     */
    public void setUnformattedMaximumUnitPrice(Long unformattedMaximumUnitPrice) {
        this.unformattedMaximumUnitPrice = unformattedMaximumUnitPrice;
    }

    /**
     * @return the maximumUnitPrice
     */
    public String getMaximumUnitPrice() {
        return maximumUnitPrice;
    }

    /**
     * @param maximumUnitPrice the maximumUnitPrice to set
     */
    public void setMaximumUnitPrice(String maximumUnitPrice) {
        this.maximumUnitPrice = maximumUnitPrice;
    }
    
}
