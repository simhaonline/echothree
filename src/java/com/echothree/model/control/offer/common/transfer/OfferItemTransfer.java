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

package com.echothree.model.control.offer.common.transfer;

import com.echothree.model.control.item.common.transfer.ItemTransfer;
import com.echothree.util.common.transfer.BaseTransfer;
import com.echothree.util.common.transfer.ListWrapper;

public class OfferItemTransfer
        extends BaseTransfer {
    
    private OfferTransfer offer;
    private ItemTransfer item;

    private ListWrapper<OfferItemPriceTransfer> offerItemPrices;

    /** Creates a new instance of OfferItemTransfer */
    public OfferItemTransfer(OfferTransfer offer, ItemTransfer item) {
        this.offer = offer;
        this.item = item;
    }

    /**
     * @return the offer
     */
    public OfferTransfer getOffer() {
        return offer;
    }

    /**
     * @param offer the offer to set
     */
    public void setOffer(OfferTransfer offer) {
        this.offer = offer;
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
     * @return the offerItemPrices
     */
    public ListWrapper<OfferItemPriceTransfer> getOfferItemPrices() {
        return offerItemPrices;
    }

    /**
     * @param offerItemPrices the offerItemPrices to set
     */
    public void setOfferItemPrices(ListWrapper<OfferItemPriceTransfer> offerItemPrices) {
        this.offerItemPrices = offerItemPrices;
    }

}