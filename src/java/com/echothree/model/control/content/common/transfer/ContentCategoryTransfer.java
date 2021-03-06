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

package com.echothree.model.control.content.common.transfer;

import com.echothree.model.control.offer.common.transfer.OfferUseTransfer;
import com.echothree.model.control.selector.common.transfer.SelectorTransfer;
import com.echothree.util.common.transfer.BaseTransfer;
import com.echothree.util.common.transfer.ListWrapper;

public class ContentCategoryTransfer
        extends BaseTransfer {

    private ContentCatalogTransfer contentCatalog;
    private String contentCategoryName;
    private ContentCategoryTransfer parentContentCategory;
    private OfferUseTransfer defaultOfferUse;
    private SelectorTransfer contentCategoryItemSelector;
    private Boolean isDefault;
    private Integer sortOrder;
    private String description;

    private ListWrapper<ContentCategoryItemTransfer> contentCategoryItems;
    
    /** Creates a new instance of ContentCategoryTransfer */
    public ContentCategoryTransfer(ContentCatalogTransfer contentCatalog, String contentCategoryName, ContentCategoryTransfer parentContentCategory,
            OfferUseTransfer defaultOfferUse, SelectorTransfer contentCategoryItemSelector, Boolean isDefault, Integer sortOrder, String description) {
        this.contentCatalog = contentCatalog;
        this.contentCategoryName = contentCategoryName;
        this.parentContentCategory = parentContentCategory;
        this.defaultOfferUse = defaultOfferUse;
        this.contentCategoryItemSelector = contentCategoryItemSelector;
        this.isDefault = isDefault;
        this.sortOrder = sortOrder;
        this.description = description;
    }

    /**
     * @return the contentCatalog
     */
    public ContentCatalogTransfer getContentCatalog() {
        return contentCatalog;
    }

    /**
     * @param contentCatalog the contentCatalog to set
     */
    public void setContentCatalog(ContentCatalogTransfer contentCatalog) {
        this.contentCatalog = contentCatalog;
    }

    /**
     * @return the contentCategoryName
     */
    public String getContentCategoryName() {
        return contentCategoryName;
    }

    /**
     * @param contentCategoryName the contentCategoryName to set
     */
    public void setContentCategoryName(String contentCategoryName) {
        this.contentCategoryName = contentCategoryName;
    }

    /**
     * @return the parentContentCategory
     */
    public ContentCategoryTransfer getParentContentCategory() {
        return parentContentCategory;
    }

    /**
     * @param parentContentCategory the parentContentCategory to set
     */
    public void setParentContentCategory(ContentCategoryTransfer parentContentCategory) {
        this.parentContentCategory = parentContentCategory;
    }

    /**
     * @return the defaultOfferUse
     */
    public OfferUseTransfer getDefaultOfferUse() {
        return defaultOfferUse;
    }

    /**
     * @param defaultOfferUse the defaultOfferUse to set
     */
    public void setDefaultOfferUse(OfferUseTransfer defaultOfferUse) {
        this.defaultOfferUse = defaultOfferUse;
    }

    /**
     * @return the contentCategoryItemSelector
     */
    public SelectorTransfer getContentCategoryItemSelector() {
        return contentCategoryItemSelector;
    }

    /**
     * @param contentCategoryItemSelector the contentCategoryItemSelector to set
     */
    public void setContentCategoryItemSelector(SelectorTransfer contentCategoryItemSelector) {
        this.contentCategoryItemSelector = contentCategoryItemSelector;
    }

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault the isDefault to set
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder the sortOrder to set
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the contentCategoryItems
     */
    public ListWrapper<ContentCategoryItemTransfer> getContentCategoryItems() {
        return contentCategoryItems;
    }

    /**
     * @param contentCategoryItems the contentCategoryItems to set
     */
    public void setContentCategoryItems(ListWrapper<ContentCategoryItemTransfer> contentCategoryItems) {
        this.contentCategoryItems = contentCategoryItems;
    }

}
