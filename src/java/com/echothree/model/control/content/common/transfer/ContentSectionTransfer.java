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

package com.echothree.model.control.content.common.transfer;

import com.echothree.util.common.transfer.BaseTransfer;
import com.echothree.util.common.transfer.ListWrapper;

public class ContentSectionTransfer
        extends BaseTransfer {
    
    private ContentCollectionTransfer contentCollection;
    private String contentSectionName;
    private ContentSectionTransfer parentContentSection;
    private Boolean isDefault;
    private Integer sortOrder;
    private String description;
    
    private ListWrapper<ContentPageTransfer> contentPages;
    
    /** Creates a new instance of ContentSectionTransfer */
    public ContentSectionTransfer(ContentCollectionTransfer contentCollection, String contentSectionName, ContentSectionTransfer parentContentSection,
            Boolean isDefault, Integer sortOrder, String description) {
        this.contentCollection = contentCollection;
        this.contentSectionName = contentSectionName;
        this.parentContentSection = parentContentSection;
        this.isDefault = isDefault;
        this.sortOrder = sortOrder;
        this.description = description;
    }

    /**
     * @return the contentCollection
     */
    public ContentCollectionTransfer getContentCollection() {
        return contentCollection;
    }

    /**
     * @param contentCollection the contentCollection to set
     */
    public void setContentCollection(ContentCollectionTransfer contentCollection) {
        this.contentCollection = contentCollection;
    }

    /**
     * @return the contentSectionName
     */
    public String getContentSectionName() {
        return contentSectionName;
    }

    /**
     * @param contentSectionName the contentSectionName to set
     */
    public void setContentSectionName(String contentSectionName) {
        this.contentSectionName = contentSectionName;
    }

    /**
     * @return the parentContentSection
     */
    public ContentSectionTransfer getParentContentSection() {
        return parentContentSection;
    }

    /**
     * @param parentContentSection the parentContentSection to set
     */
    public void setParentContentSection(ContentSectionTransfer parentContentSection) {
        this.parentContentSection = parentContentSection;
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
     * @return the contentPages
     */
    public ListWrapper<ContentPageTransfer> getContentPages() {
        return contentPages;
    }

    /**
     * @param contentPages the contentPages to set
     */
    public void setContentPages(ListWrapper<ContentPageTransfer> contentPages) {
        this.contentPages = contentPages;
    }

 }