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

package com.echothree.model.control.vendor.server;

import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.control.vendor.common.choice.ItemPurchasingCategoryChoicesBean;
import com.echothree.model.control.vendor.common.choice.VendorItemStatusChoicesBean;
import com.echothree.model.control.vendor.common.choice.VendorStatusChoicesBean;
import com.echothree.model.control.vendor.common.choice.VendorTypeChoicesBean;
import com.echothree.model.control.vendor.common.transfer.ItemPurchasingCategoryDescriptionTransfer;
import com.echothree.model.control.vendor.common.transfer.ItemPurchasingCategoryTransfer;
import com.echothree.model.control.vendor.common.transfer.VendorItemCostTransfer;
import com.echothree.model.control.vendor.common.transfer.VendorItemTransfer;
import com.echothree.model.control.vendor.common.transfer.VendorTransfer;
import com.echothree.model.control.vendor.common.transfer.VendorTypeDescriptionTransfer;
import com.echothree.model.control.vendor.common.transfer.VendorTypeTransfer;
import com.echothree.model.control.vendor.server.transfer.ItemPurchasingCategoryDescriptionTransferCache;
import com.echothree.model.control.vendor.server.transfer.ItemPurchasingCategoryTransferCache;
import com.echothree.model.control.vendor.server.transfer.VendorItemCostTransferCache;
import com.echothree.model.control.vendor.server.transfer.VendorItemTransferCache;
import com.echothree.model.control.vendor.server.transfer.VendorTransferCaches;
import com.echothree.model.control.vendor.server.transfer.VendorTypeDescriptionTransferCache;
import com.echothree.model.control.vendor.server.transfer.VendorTypeTransferCache;
import com.echothree.model.control.vendor.common.workflow.VendorItemStatusConstants;
import com.echothree.model.control.vendor.common.workflow.VendorStatusConstants;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.data.accounting.common.pk.GlAccountPK;
import com.echothree.model.data.accounting.server.entity.GlAccount;
import com.echothree.model.data.cancellationpolicy.common.pk.CancellationPolicyPK;
import com.echothree.model.data.cancellationpolicy.server.entity.CancellationPolicy;
import com.echothree.model.data.core.server.entity.EntityInstance;
import com.echothree.model.data.filter.common.pk.FilterPK;
import com.echothree.model.data.filter.server.entity.Filter;
import com.echothree.model.data.inventory.common.pk.InventoryConditionPK;
import com.echothree.model.data.inventory.server.entity.InventoryCondition;
import com.echothree.model.data.item.common.pk.ItemAliasTypePK;
import com.echothree.model.data.item.common.pk.ItemPK;
import com.echothree.model.data.item.server.entity.Item;
import com.echothree.model.data.item.server.entity.ItemAliasType;
import com.echothree.model.data.party.common.pk.PartyPK;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.returnpolicy.common.pk.ReturnPolicyPK;
import com.echothree.model.data.returnpolicy.server.entity.ReturnPolicy;
import com.echothree.model.data.selector.common.pk.SelectorPK;
import com.echothree.model.data.selector.server.entity.Selector;
import com.echothree.model.data.uom.common.pk.UnitOfMeasureTypePK;
import com.echothree.model.data.uom.server.entity.UnitOfMeasureType;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.model.data.vendor.common.pk.ItemPurchasingCategoryPK;
import com.echothree.model.data.vendor.common.pk.VendorItemPK;
import com.echothree.model.data.vendor.common.pk.VendorTypePK;
import com.echothree.model.data.vendor.server.entity.ItemPurchasingCategory;
import com.echothree.model.data.vendor.server.entity.ItemPurchasingCategoryDescription;
import com.echothree.model.data.vendor.server.entity.ItemPurchasingCategoryDetail;
import com.echothree.model.data.vendor.server.entity.Vendor;
import com.echothree.model.data.vendor.server.entity.VendorItem;
import com.echothree.model.data.vendor.server.entity.VendorItemCost;
import com.echothree.model.data.vendor.server.entity.VendorItemDetail;
import com.echothree.model.data.vendor.server.entity.VendorType;
import com.echothree.model.data.vendor.server.entity.VendorTypeDescription;
import com.echothree.model.data.vendor.server.entity.VendorTypeDetail;
import com.echothree.model.data.vendor.server.factory.ItemPurchasingCategoryDescriptionFactory;
import com.echothree.model.data.vendor.server.factory.ItemPurchasingCategoryDetailFactory;
import com.echothree.model.data.vendor.server.factory.ItemPurchasingCategoryFactory;
import com.echothree.model.data.vendor.server.factory.VendorFactory;
import com.echothree.model.data.vendor.server.factory.VendorItemCostFactory;
import com.echothree.model.data.vendor.server.factory.VendorItemDetailFactory;
import com.echothree.model.data.vendor.server.factory.VendorItemFactory;
import com.echothree.model.data.vendor.server.factory.VendorTypeDescriptionFactory;
import com.echothree.model.data.vendor.server.factory.VendorTypeDetailFactory;
import com.echothree.model.data.vendor.server.factory.VendorTypeFactory;
import com.echothree.model.data.vendor.server.value.ItemPurchasingCategoryDescriptionValue;
import com.echothree.model.data.vendor.server.value.ItemPurchasingCategoryDetailValue;
import com.echothree.model.data.vendor.server.value.VendorItemCostValue;
import com.echothree.model.data.vendor.server.value.VendorItemDetailValue;
import com.echothree.model.data.vendor.server.value.VendorTypeDescriptionValue;
import com.echothree.model.data.vendor.server.value.VendorTypeDetailValue;
import com.echothree.model.data.vendor.server.value.VendorValue;
import com.echothree.model.data.workflow.server.entity.WorkflowDestination;
import com.echothree.model.data.workflow.server.entity.WorkflowEntityStatus;
import com.echothree.util.common.exception.PersistenceDatabaseException;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.persistence.BasePK;
import com.echothree.util.server.control.BaseModelControl;
import com.echothree.util.server.message.ExecutionErrorAccumulator;
import com.echothree.util.server.persistence.EntityPermission;
import com.echothree.util.server.persistence.Session;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VendorControl
        extends BaseModelControl {
    
    /** Creates a new instance of VendorControl */
    public VendorControl() {
        super();
    }
    
    // --------------------------------------------------------------------------------
    //   Vendor Transfer Caches
    // --------------------------------------------------------------------------------
    
    private VendorTransferCaches vendorTransferCaches = null;
    
    public VendorTransferCaches getVendorTransferCaches(UserVisit userVisit) {
        if(vendorTransferCaches == null) {
            vendorTransferCaches = new VendorTransferCaches(userVisit, this);
        }
        
        return vendorTransferCaches;
    }
    
    // --------------------------------------------------------------------------------
    //   Vendor Types
    // --------------------------------------------------------------------------------
    
    public VendorType createVendorType(String vendorTypeName, CancellationPolicy defaultCancellationPolicy, ReturnPolicy defaultReturnPolicy,
            GlAccount defaultApGlAccount, Boolean defaultHoldUntilComplete, Boolean defaultAllowBackorders, Boolean defaultAllowSubstitutions,
            Boolean defaultAllowCombiningShipments, Boolean defaultRequireReference, Boolean defaultAllowReferenceDuplicates,
            String defaultReferenceValidationPattern, Boolean isDefault, Integer sortOrder, BasePK createdBy) {
        VendorType defaultVendorType = getDefaultVendorType();
        boolean defaultFound = defaultVendorType != null;
        
        if(defaultFound && isDefault) {
            VendorTypeDetailValue defaultVendorTypeDetailValue = getDefaultVendorTypeDetailValueForUpdate();
            
            defaultVendorTypeDetailValue.setIsDefault(Boolean.FALSE);
            updateVendorTypeFromValue(defaultVendorTypeDetailValue, false, createdBy);
        } else if(!defaultFound) {
            isDefault = Boolean.TRUE;
        }
        
        VendorType vendorType = VendorTypeFactory.getInstance().create();
        VendorTypeDetail vendorTypeDetail = VendorTypeDetailFactory.getInstance().create(vendorType, vendorTypeName, defaultCancellationPolicy,
                defaultReturnPolicy, defaultApGlAccount, defaultHoldUntilComplete, defaultAllowBackorders, defaultAllowSubstitutions,
                defaultAllowCombiningShipments, defaultRequireReference, defaultAllowReferenceDuplicates, defaultReferenceValidationPattern, isDefault,
                sortOrder, session.START_TIME_LONG, Session.MAX_TIME_LONG);
        
        // Convert to R/W
        vendorType = VendorTypeFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE,
                vendorType.getPrimaryKey());
        vendorType.setActiveDetail(vendorTypeDetail);
        vendorType.setLastDetail(vendorTypeDetail);
        vendorType.store();
        
        sendEventUsingNames(vendorType.getPrimaryKey(), EventTypes.CREATE.name(), null, null, createdBy);
        
        return vendorType;
    }
    
    private List<VendorType> getVendorTypes(EntityPermission entityPermission) {
        String query = null;
        
        if(entityPermission.equals(EntityPermission.READ_ONLY)) {
            query = "SELECT _ALL_ "
                    + "FROM vendortypes, vendortypedetails "
                    + "WHERE vndrty_activedetailid = vndrtydt_vendortypedetailid "
                    + "ORDER BY vndrtydt_sortorder, vndrtydt_vendortypename "
                    + "_LIMIT_";
        } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
            query = "SELECT _ALL_ "
                    + "FROM vendortypes, vendortypedetails "
                    + "WHERE vndrty_activedetailid = vndrtydt_vendortypedetailid "
                    + "FOR UPDATE";
        }
        
        PreparedStatement ps = VendorTypeFactory.getInstance().prepareStatement(query);
        
        return VendorTypeFactory.getInstance().getEntitiesFromQuery(entityPermission, ps);
    }
    
    public List<VendorType> getVendorTypes() {
        return getVendorTypes(EntityPermission.READ_ONLY);
    }
    
    public List<VendorType> getVendorTypesForUpdate() {
        return getVendorTypes(EntityPermission.READ_WRITE);
    }
    
    private VendorType getDefaultVendorType(EntityPermission entityPermission) {
        String query = null;
        
        if(entityPermission.equals(EntityPermission.READ_ONLY)) {
            query = "SELECT _ALL_ " +
                    "FROM vendortypes, vendortypedetails " +
                    "WHERE vndrty_activedetailid = vndrtydt_vendortypedetailid AND vndrtydt_isdefault = 1";
        } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
            query = "SELECT _ALL_ " +
                    "FROM vendortypes, vendortypedetails " +
                    "WHERE vndrty_activedetailid = vndrtydt_vendortypedetailid AND vndrtydt_isdefault = 1 " +
                    "FOR UPDATE";
        }
        
        PreparedStatement ps = VendorTypeFactory.getInstance().prepareStatement(query);
        
        return VendorTypeFactory.getInstance().getEntityFromQuery(entityPermission, ps);
    }
    
    public VendorType getDefaultVendorType() {
        return getDefaultVendorType(EntityPermission.READ_ONLY);
    }
    
    public VendorType getDefaultVendorTypeForUpdate() {
        return getDefaultVendorType(EntityPermission.READ_WRITE);
    }
    
    public VendorTypeDetailValue getDefaultVendorTypeDetailValueForUpdate() {
        return getDefaultVendorTypeForUpdate().getLastDetailForUpdate().getVendorTypeDetailValue().clone();
    }
    
    private VendorType getVendorTypeByName(String vendorTypeName, EntityPermission entityPermission) {
        VendorType vendorType = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ " +
                        "FROM vendortypes, vendortypedetails " +
                        "WHERE vndrty_activedetailid = vndrtydt_vendortypedetailid AND vndrtydt_vendortypename = ?";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ " +
                        "FROM vendortypes, vendortypedetails " +
                        "WHERE vndrty_activedetailid = vndrtydt_vendortypedetailid AND vndrtydt_vendortypename = ? " +
                        "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorTypeFactory.getInstance().prepareStatement(query);
            
            ps.setString(1, vendorTypeName);
            
            vendorType = VendorTypeFactory.getInstance().getEntityFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorType;
    }
    
    public VendorType getVendorTypeByName(String vendorTypeName) {
        return getVendorTypeByName(vendorTypeName, EntityPermission.READ_ONLY);
    }
    
    public VendorType getVendorTypeByNameForUpdate(String vendorTypeName) {
        return getVendorTypeByName(vendorTypeName, EntityPermission.READ_WRITE);
    }
    
    public VendorTypeDetailValue getVendorTypeDetailValueForUpdate(VendorType vendorType) {
        return vendorType == null? null: vendorType.getLastDetailForUpdate().getVendorTypeDetailValue().clone();
    }
    
    public VendorTypeDetailValue getVendorTypeDetailValueByNameForUpdate(String vendorTypeName) {
        return getVendorTypeDetailValueForUpdate(getVendorTypeByNameForUpdate(vendorTypeName));
    }
    
    public VendorTypeChoicesBean getVendorTypeChoices(String defaultVendorTypeChoice, Language language,
            boolean allowNullChoice) {
        List<VendorType> vendorTypes = getVendorTypes();
        int size = vendorTypes.size();
        List<String> labels = new ArrayList<>(size);
        List<String> values = new ArrayList<>(size);
        String defaultValue = null;
        
        if(allowNullChoice) {
            labels.add("");
            values.add("");
            
            if(defaultVendorTypeChoice == null) {
                defaultValue = "";
            }
        }
        
        for(VendorType vendorType: vendorTypes) {
            VendorTypeDetail vendorTypeDetail = vendorType.getLastDetail();
            String label = getBestVendorTypeDescription(vendorType, language);
            String value = vendorTypeDetail.getVendorTypeName();
            
            labels.add(label == null? value: label);
            values.add(value);
            
            boolean usingDefaultChoice = defaultVendorTypeChoice == null? false: defaultVendorTypeChoice.equals(value);
            if(usingDefaultChoice || (defaultValue == null && vendorTypeDetail.getIsDefault())) {
                defaultValue = value;
            }
        }
        
        return new VendorTypeChoicesBean(labels, values, defaultValue);
    }
    
    public VendorTypeTransfer getVendorTypeTransfer(UserVisit userVisit, VendorType vendorType) {
        return getVendorTransferCaches(userVisit).getVendorTypeTransferCache().getVendorTypeTransfer(vendorType);
    }
    
    public List<VendorTypeTransfer> getVendorTypeTransfers(UserVisit userVisit) {
        List<VendorType> vendorTypes = getVendorTypes();
        List<VendorTypeTransfer> vendorTypeTransfers = null;
        
        if(vendorTypes != null) {
            VendorTypeTransferCache vendorTypeTransferCache = getVendorTransferCaches(userVisit).getVendorTypeTransferCache();
            
            vendorTypeTransfers = new ArrayList<>(vendorTypes.size());
            
            for(VendorType vendorType: vendorTypes) {
                vendorTypeTransfers.add(vendorTypeTransferCache.getVendorTypeTransfer(vendorType));
            }
        }
        
        return vendorTypeTransfers;
    }
    
    private void updateVendorTypeFromValue(VendorTypeDetailValue vendorTypeDetailValue, boolean checkDefault,
            BasePK updatedBy) {
        if(vendorTypeDetailValue.hasBeenModified()) {
            VendorType vendorType = VendorTypeFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE,
                     vendorTypeDetailValue.getVendorTypePK());
            VendorTypeDetail vendorTypeDetail = vendorType.getActiveDetailForUpdate();
            
            vendorTypeDetail.setThruTime(session.START_TIME_LONG);
            vendorTypeDetail.store();
            
            VendorTypePK vendorTypePK = vendorTypeDetail.getVendorTypePK();
            String vendorTypeName = vendorTypeDetailValue.getVendorTypeName();
            CancellationPolicyPK defaultCancellationPolicyPK = vendorTypeDetailValue.getDefaultCancellationPolicyPK();
            ReturnPolicyPK defaultReturnPolicyPK = vendorTypeDetailValue.getDefaultReturnPolicyPK();
            GlAccountPK defaultApGlAccountPK = vendorTypeDetailValue.getDefaultApGlAccountPK();
            Boolean defaultHoldUntilComplete = vendorTypeDetailValue.getDefaultHoldUntilComplete();
            Boolean defaultAllowBackorders = vendorTypeDetailValue.getDefaultAllowBackorders();
            Boolean defaultAllowSubstitutions = vendorTypeDetailValue.getDefaultAllowSubstitutions();
            Boolean defaultAllowCombiningShipments = vendorTypeDetailValue.getDefaultAllowCombiningShipments();
            Boolean defaultRequireReference = vendorTypeDetailValue.getDefaultRequireReference();
            Boolean defaultAllowReferenceDuplicates = vendorTypeDetailValue.getDefaultAllowReferenceDuplicates();
            String defaultReferenceValidationPattern = vendorTypeDetailValue.getDefaultReferenceValidationPattern();
            Boolean isDefault = vendorTypeDetailValue.getIsDefault();
            Integer sortOrder = vendorTypeDetailValue.getSortOrder();
            
            if(checkDefault) {
                VendorType defaultVendorType = getDefaultVendorType();
                boolean defaultFound = defaultVendorType != null && !defaultVendorType.equals(vendorType);
                
                if(isDefault && defaultFound) {
                    // If I'm the default, and a default already existed...
                    VendorTypeDetailValue defaultVendorTypeDetailValue = getDefaultVendorTypeDetailValueForUpdate();
                    
                    defaultVendorTypeDetailValue.setIsDefault(Boolean.FALSE);
                    updateVendorTypeFromValue(defaultVendorTypeDetailValue, false, updatedBy);
                } else if(!isDefault && !defaultFound) {
                    // If I'm not the default, and no other default exists...
                    isDefault = Boolean.TRUE;
                }
            }
            
            vendorTypeDetail = VendorTypeDetailFactory.getInstance().create(vendorTypePK, vendorTypeName, defaultCancellationPolicyPK, defaultReturnPolicyPK,
                    defaultApGlAccountPK, defaultHoldUntilComplete, defaultAllowBackorders, defaultAllowSubstitutions, defaultAllowCombiningShipments,
                    defaultRequireReference, defaultAllowReferenceDuplicates, defaultReferenceValidationPattern, isDefault, sortOrder,
                    session.START_TIME_LONG, Session.MAX_TIME_LONG);
            
            vendorType.setActiveDetail(vendorTypeDetail);
            vendorType.setLastDetail(vendorTypeDetail);
            
            sendEventUsingNames(vendorTypePK, EventTypes.MODIFY.name(), null, null, updatedBy);
        }
    }
    
    public void updateVendorTypeFromValue(VendorTypeDetailValue vendorTypeDetailValue, BasePK updatedBy) {
        updateVendorTypeFromValue(vendorTypeDetailValue, true, updatedBy);
    }
    
    public void deleteVendorType(VendorType vendorType, BasePK deletedBy) {
        deleteVendorTypeDescriptionsByVendorType(vendorType, deletedBy);
        
        VendorTypeDetail vendorTypeDetail = vendorType.getLastDetailForUpdate();
        vendorTypeDetail.setThruTime(session.START_TIME_LONG);
        vendorType.setActiveDetail(null);
        vendorType.store();
        
        // Check for default, and pick one if necessary
        VendorType defaultVendorType = getDefaultVendorType();
        if(defaultVendorType == null) {
            List<VendorType> vendorTypes = getVendorTypesForUpdate();
            
            if(!vendorTypes.isEmpty()) {
                Iterator<VendorType> iter = vendorTypes.iterator();
                if(iter.hasNext()) {
                    defaultVendorType = iter.next();
                }
                VendorTypeDetailValue vendorTypeDetailValue = defaultVendorType.getLastDetailForUpdate().getVendorTypeDetailValue().clone();
                
                vendorTypeDetailValue.setIsDefault(Boolean.TRUE);
                updateVendorTypeFromValue(vendorTypeDetailValue, false, deletedBy);
            }
        }
        
        sendEventUsingNames(vendorType.getPrimaryKey(), EventTypes.DELETE.name(), null, null, deletedBy);
    }
    
    // --------------------------------------------------------------------------------
    //   Vendor Type Descriptions
    // --------------------------------------------------------------------------------
    
    public VendorTypeDescription createVendorTypeDescription(VendorType vendorType, Language language, String description,
            BasePK createdBy) {
        VendorTypeDescription vendorTypeDescription = VendorTypeDescriptionFactory.getInstance().create(vendorType,
                language, description,
                session.START_TIME_LONG, Session.MAX_TIME_LONG);
        
        sendEventUsingNames(vendorType.getPrimaryKey(), EventTypes.MODIFY.name(), vendorTypeDescription.getPrimaryKey(),
                null, createdBy);
        
        return vendorTypeDescription;
    }
    
    private VendorTypeDescription getVendorTypeDescription(VendorType vendorType, Language language, EntityPermission entityPermission) {
        VendorTypeDescription vendorTypeDescription = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ " +
                        "FROM vendortypedescriptions " +
                        "WHERE vndrtyd_vndrty_vendortypeid = ? AND vndrtyd_lang_languageid = ? AND vndrtyd_thrutime = ?";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ " +
                        "FROM vendortypedescriptions " +
                        "WHERE vndrtyd_vndrty_vendortypeid = ? AND vndrtyd_lang_languageid = ? AND vndrtyd_thrutime = ? " +
                        "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorTypeDescriptionFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, vendorType.getPrimaryKey().getEntityId());
            ps.setLong(2, language.getPrimaryKey().getEntityId());
            ps.setLong(3, Session.MAX_TIME);
            
            vendorTypeDescription = VendorTypeDescriptionFactory.getInstance().getEntityFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorTypeDescription;
    }
    
    public VendorTypeDescription getVendorTypeDescription(VendorType vendorType, Language language) {
        return getVendorTypeDescription(vendorType, language, EntityPermission.READ_ONLY);
    }
    
    public VendorTypeDescription getVendorTypeDescriptionForUpdate(VendorType vendorType, Language language) {
        return getVendorTypeDescription(vendorType, language, EntityPermission.READ_WRITE);
    }
    
    public VendorTypeDescriptionValue getVendorTypeDescriptionValue(VendorTypeDescription vendorTypeDescription) {
        return vendorTypeDescription == null? null: vendorTypeDescription.getVendorTypeDescriptionValue().clone();
    }
    
    public VendorTypeDescriptionValue getVendorTypeDescriptionValueForUpdate(VendorType vendorType, Language language) {
        return getVendorTypeDescriptionValue(getVendorTypeDescriptionForUpdate(vendorType, language));
    }
    
    private List<VendorTypeDescription> getVendorTypeDescriptionsByVendorType(VendorType vendorType, EntityPermission entityPermission) {
        List<VendorTypeDescription> vendorTypeDescriptions = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ "
                        + "FROM vendortypedescriptions, languages "
                        + "WHERE vndrtyd_vndrty_vendortypeid = ? AND vndrtyd_thrutime = ? AND vndrtyd_lang_languageid = lang_languageid "
                        + "ORDER BY lang_sortorder, lang_languageisoname "
                        + "_LIMIT_";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ "
                        + "FROM vendortypedescriptions "
                        + "WHERE vndrtyd_vndrty_vendortypeid = ? AND vndrtyd_thrutime = ? "
                        + "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorTypeDescriptionFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, vendorType.getPrimaryKey().getEntityId());
            ps.setLong(2, Session.MAX_TIME);
            
            vendorTypeDescriptions = VendorTypeDescriptionFactory.getInstance().getEntitiesFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorTypeDescriptions;
    }
    
    public List<VendorTypeDescription> getVendorTypeDescriptionsByVendorType(VendorType vendorType) {
        return getVendorTypeDescriptionsByVendorType(vendorType, EntityPermission.READ_ONLY);
    }
    
    public List<VendorTypeDescription> getVendorTypeDescriptionsByVendorTypeForUpdate(VendorType vendorType) {
        return getVendorTypeDescriptionsByVendorType(vendorType, EntityPermission.READ_WRITE);
    }
    
    public String getBestVendorTypeDescription(VendorType vendorType, Language language) {
        String description;
        VendorTypeDescription vendorTypeDescription = getVendorTypeDescription(vendorType, language);
        
        if(vendorTypeDescription == null && !language.getIsDefault()) {
            vendorTypeDescription = getVendorTypeDescription(vendorType, getPartyControl().getDefaultLanguage());
        }
        
        if(vendorTypeDescription == null) {
            description = vendorType.getLastDetail().getVendorTypeName();
        } else {
            description = vendorTypeDescription.getDescription();
        }
        
        return description;
    }
    
    public VendorTypeDescriptionTransfer getVendorTypeDescriptionTransfer(UserVisit userVisit, VendorTypeDescription vendorTypeDescription) {
        return getVendorTransferCaches(userVisit).getVendorTypeDescriptionTransferCache().getVendorTypeDescriptionTransfer(vendorTypeDescription);
    }
    
    public List<VendorTypeDescriptionTransfer> getVendorTypeDescriptionTransfers(UserVisit userVisit, VendorType vendorType) {
        List<VendorTypeDescription> vendorTypeDescriptions = getVendorTypeDescriptionsByVendorType(vendorType);
        List<VendorTypeDescriptionTransfer> vendorTypeDescriptionTransfers = null;
        
        if(vendorTypeDescriptions != null) {
            VendorTypeDescriptionTransferCache vendorTypeDescriptionTransferCache = getVendorTransferCaches(userVisit).getVendorTypeDescriptionTransferCache();
            
            vendorTypeDescriptionTransfers = new ArrayList<>(vendorTypeDescriptions.size());
            
            for(VendorTypeDescription vendorTypeDescription: vendorTypeDescriptions) {
                vendorTypeDescriptionTransfers.add(vendorTypeDescriptionTransferCache.getVendorTypeDescriptionTransfer(vendorTypeDescription));
            }
        }
        
        return vendorTypeDescriptionTransfers;
    }
    
    public void updateVendorTypeDescriptionFromValue(VendorTypeDescriptionValue vendorTypeDescriptionValue, BasePK updatedBy) {
        if(vendorTypeDescriptionValue.hasBeenModified()) {
            VendorTypeDescription vendorTypeDescription = VendorTypeDescriptionFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE,
                     vendorTypeDescriptionValue.getPrimaryKey());
            
            vendorTypeDescription.setThruTime(session.START_TIME_LONG);
            vendorTypeDescription.store();
            
            VendorType vendorType = vendorTypeDescription.getVendorType();
            Language language = vendorTypeDescription.getLanguage();
            String description = vendorTypeDescriptionValue.getDescription();
            
            vendorTypeDescription = VendorTypeDescriptionFactory.getInstance().create(vendorType, language,
                    description, session.START_TIME_LONG, Session.MAX_TIME_LONG);
            
            sendEventUsingNames(vendorType.getPrimaryKey(), EventTypes.MODIFY.name(), vendorTypeDescription.getPrimaryKey(),
                    null, updatedBy);
        }
    }
    
    public void deleteVendorTypeDescription(VendorTypeDescription vendorTypeDescription, BasePK deletedBy) {
        vendorTypeDescription.setThruTime(session.START_TIME_LONG);
        
        sendEventUsingNames(vendorTypeDescription.getVendorTypePK(), EventTypes.MODIFY.name(),
                vendorTypeDescription.getPrimaryKey(), null, deletedBy);
    }
    
    public void deleteVendorTypeDescriptionsByVendorType(VendorType vendorType, BasePK deletedBy) {
        List<VendorTypeDescription> vendorTypeDescriptions = getVendorTypeDescriptionsByVendorTypeForUpdate(vendorType);
        
        vendorTypeDescriptions.stream().forEach((vendorTypeDescription) -> {
            deleteVendorTypeDescription(vendorTypeDescription, deletedBy);
        });
    }
    
    // --------------------------------------------------------------------------------
    //   Vendors
    // --------------------------------------------------------------------------------
    
    public Vendor createVendor(Party party, String vendorName, VendorType vendorType, Integer minimumPurchaseOrderLines, Integer maximumPurchaseOrderLines,
            Long minimumPurchaseOrderAmount, Long maximumPurchaseOrderAmount, Boolean useItemPurchasingCategories, ItemAliasType defaultItemAliasType,
            CancellationPolicy cancellationPolicy, ReturnPolicy returnPolicy, GlAccount apGlAccount, Boolean holdUntilComplete, Boolean allowBackorders,
            Boolean allowSubstitutions, Boolean allowCombiningShipments, Boolean requireReference, Boolean allowReferenceDuplicates,
            String referenceValidationPattern, Selector vendorItemSelector, Filter vendorItemCostFilter, BasePK createdBy) {
        Vendor vendor = VendorFactory.getInstance().create(party, vendorName, vendorType, minimumPurchaseOrderLines, maximumPurchaseOrderLines,
                minimumPurchaseOrderAmount, maximumPurchaseOrderAmount, useItemPurchasingCategories, defaultItemAliasType, cancellationPolicy, returnPolicy,
                apGlAccount, holdUntilComplete, allowBackorders, allowSubstitutions, allowCombiningShipments, requireReference, allowReferenceDuplicates,
                referenceValidationPattern, vendorItemSelector, vendorItemCostFilter, session.START_TIME_LONG, Session.MAX_TIME_LONG);
        
        sendEventUsingNames(party.getPrimaryKey(), EventTypes.MODIFY.name(), vendor.getPrimaryKey(), EventTypes.CREATE.name(), createdBy);
        
        return vendor;
    }
    
    private Vendor getVendor(Party party, EntityPermission entityPermission) {
        Vendor vendor = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ " +
                        "FROM vendors " +
                        "WHERE vndr_par_partyid = ? AND vndr_thrutime = ?";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ " +
                        "FROM vendors " +
                        "WHERE vndr_par_partyid = ? AND vndr_thrutime = ? " +
                        "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, party.getPrimaryKey().getEntityId());
            ps.setLong(2, Session.MAX_TIME);
            
            vendor = VendorFactory.getInstance().getEntityFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendor;
    }
    
    public Vendor getVendor(Party party) {
        return getVendor(party, EntityPermission.READ_ONLY);
    }
    
    public Vendor getVendorForUpdate(Party party) {
        return getVendor(party, EntityPermission.READ_WRITE);
    }
    
    private Vendor getVendorByName(String vendorName, EntityPermission entityPermission) {
        Vendor vendor = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ " +
                        "FROM vendors " +
                        "WHERE vndr_vendorname = ? AND vndr_thrutime = ?";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ " +
                        "FROM vendors " +
                        "WHERE vndr_vendorname = ? AND vndr_thrutime = ? " +
                        "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorFactory.getInstance().prepareStatement(query);
            
            ps.setString(1, vendorName);
            ps.setLong(2, Session.MAX_TIME);
            
            vendor = VendorFactory.getInstance().getEntityFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendor;
    }
    
    public Vendor getVendorByName(String vendorName) {
        return getVendorByName(vendorName, EntityPermission.READ_ONLY);
    }
    
    public Vendor getVendorByNameForUpdate(String vendorName) {
        return getVendorByName(vendorName, EntityPermission.READ_WRITE);
    }
    
    private List<Vendor> getVendorsByDefaultItemAliasType(ItemAliasType defaultItemAliasType, EntityPermission entityPermission) {
        List<Vendor> vendors = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ "
                        + "FROM vendors "
                        + "WHERE vndr_defaultitemaliastypeid = ? AND vndr_thrutime = ? "
                        + "_LIMIT_";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ "
                        + "FROM vendors "
                        + "WHERE vndr_defaultitemaliastypeid = ? AND vndr_thrutime = ? "
                        + "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, defaultItemAliasType.getPrimaryKey().getEntityId());
            ps.setLong(2, Session.MAX_TIME);
            
            vendors = VendorFactory.getInstance().getEntitiesFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendors;
    }
    
    public List<Vendor> getVendorsByDefaultItemAliasType(ItemAliasType defaultItemAliasType) {
        return getVendorsByDefaultItemAliasType(defaultItemAliasType, EntityPermission.READ_ONLY);
    }
    
    public List<Vendor> getVendorsByDefaultItemAliasTypeForUpdate(ItemAliasType defaultItemAliasType) {
        return getVendorsByDefaultItemAliasType(defaultItemAliasType, EntityPermission.READ_WRITE);
    }
    
    public VendorValue getVendorValue(Vendor vendor) {
        return vendor == null? null: vendor.getVendorValue().clone();
    }
    
    public VendorValue getVendorValueByNameForUpdate(String vendorName) {
        return getVendorValue(getVendorByNameForUpdate(vendorName));
    }
    
    public VendorStatusChoicesBean getVendorStatusChoices(String defaultVendorStatusChoice, Language language,
            boolean allowNullChoice, Party vendorParty, PartyPK partyPK) {
        var workflowControl = getWorkflowControl();
        VendorStatusChoicesBean vendorStatusChoicesBean = new VendorStatusChoicesBean();

        if(vendorParty == null) {
            workflowControl.getWorkflowEntranceChoices(vendorStatusChoicesBean, defaultVendorStatusChoice, language, allowNullChoice,
                    workflowControl.getWorkflowByName(VendorStatusConstants.Workflow_VENDOR_STATUS), partyPK);
        } else {
            EntityInstance entityInstance = getCoreControl().getEntityInstanceByBasePK(vendorParty.getPrimaryKey());
            WorkflowEntityStatus workflowEntityStatus = workflowControl.getWorkflowEntityStatusByEntityInstanceUsingNames(VendorStatusConstants.Workflow_VENDOR_STATUS,
                    entityInstance);

            workflowControl.getWorkflowDestinationChoices(vendorStatusChoicesBean, defaultVendorStatusChoice, language, allowNullChoice,
                    workflowEntityStatus.getWorkflowStep(), partyPK);
        }

        return vendorStatusChoicesBean;
    }

    public void setVendorStatus(ExecutionErrorAccumulator eea, Party party, String vendorStatusChoice, PartyPK modifiedBy) {
        var workflowControl = getWorkflowControl();
        EntityInstance entityInstance = getEntityInstanceByBaseEntity(party);
        WorkflowEntityStatus workflowEntityStatus = workflowControl.getWorkflowEntityStatusByEntityInstanceForUpdateUsingNames(VendorStatusConstants.Workflow_VENDOR_STATUS,
                entityInstance);
        WorkflowDestination workflowDestination = vendorStatusChoice == null? null:
            workflowControl.getWorkflowDestinationByName(workflowEntityStatus.getWorkflowStep(), vendorStatusChoice);

        if(workflowDestination != null || vendorStatusChoice == null) {
            workflowControl.transitionEntityInWorkflow(eea, workflowEntityStatus, workflowDestination, null, modifiedBy);
        } else {
            eea.addExecutionError(ExecutionErrors.UnknownVendorStatusChoice.name(), vendorStatusChoice);
        }
    }

    public VendorTransfer getVendorTransfer(UserVisit userVisit, Vendor vendor) {
        return getVendorTransferCaches(userVisit).getVendorTransferCache().getVendorTransfer(vendor);
    }
    
    public VendorTransfer getVendorTransfer(UserVisit userVisit, Party party) {
        return getVendorTransferCaches(userVisit).getVendorTransferCache().getVendorTransfer(party);
    }
    
    public void updateVendorFromValue(VendorValue vendorValue, BasePK updatedBy) {
        if(vendorValue.hasBeenModified()) {
            Vendor vendor = VendorFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE, vendorValue.getPrimaryKey());
            
            vendor.setThruTime(session.START_TIME_LONG);
            vendor.store();
            
            PartyPK partyPK = vendor.getPartyPK(); // Not updated
            String vendorName = vendorValue.getVendorName();
            VendorTypePK vendorTypePK = vendorValue.getVendorTypePK();
            Integer minimumPurchaseOrderLines = vendorValue.getMinimumPurchaseOrderLines();
            Integer maximumPurchaseOrderLines = vendorValue.getMaximumPurchaseOrderLines();
            Long minimumPurchaseOrderAmount = vendorValue.getMinimumPurchaseOrderAmount();
            Long maximumPurchaseOrderAmount = vendorValue.getMaximumPurchaseOrderAmount();
            Boolean useItemPurchasingCategories = vendorValue.getUseItemPurchasingCategories();
            ItemAliasTypePK defaultItemAliasTypePK = vendorValue.getDefaultItemAliasTypePK();
            CancellationPolicyPK cancellationPolicyPK = vendorValue.getCancellationPolicyPK();
            ReturnPolicyPK returnPolicyPK = vendorValue.getReturnPolicyPK();
            GlAccountPK apGlAccountPK = vendorValue.getApGlAccountPK();
            Boolean holdUntilComplete = vendorValue.getHoldUntilComplete();
            Boolean allowBackorders = vendorValue.getAllowBackorders();
            Boolean allowSubstitutions = vendorValue.getAllowSubstitutions();
            Boolean allowCombiningShipments = vendorValue.getAllowCombiningShipments();
            Boolean requireReference = vendorValue.getRequireReference();
            Boolean allowReferenceDuplicates = vendorValue.getAllowReferenceDuplicates();
            String referenceValidationPattern = vendorValue.getReferenceValidationPattern();
            SelectorPK vendorItemSelectorPK = vendorValue.getVendorItemSelectorPK();
            FilterPK vendorItemCostFilterPK = vendorValue.getVendorItemCostFilterPK();
            
            vendor = VendorFactory.getInstance().create(partyPK, vendorName, vendorTypePK, minimumPurchaseOrderLines, maximumPurchaseOrderLines,
                    minimumPurchaseOrderAmount, maximumPurchaseOrderAmount, useItemPurchasingCategories, defaultItemAliasTypePK, cancellationPolicyPK,
                    returnPolicyPK, apGlAccountPK, holdUntilComplete, allowBackorders, allowSubstitutions, allowCombiningShipments, requireReference,
                    allowReferenceDuplicates, referenceValidationPattern, vendorItemSelectorPK, vendorItemCostFilterPK, session.START_TIME_LONG,
                    Session.MAX_TIME_LONG);
            
            sendEventUsingNames(partyPK, EventTypes.MODIFY.name(), vendor.getPrimaryKey(), EventTypes.MODIFY.name(), updatedBy);
        }
    }
    
    // --------------------------------------------------------------------------------
    //   Vendor Items
    // --------------------------------------------------------------------------------
    
    public VendorItem createVendorItem(Item item, Party vendorParty, String vendorItemName, String description, Integer priority,
            CancellationPolicy cancellationPolicy, ReturnPolicy returnPolicy, BasePK createdBy) {
        VendorItem vendorItem = VendorItemFactory.getInstance().create();
        VendorItemDetail vendorItemDetail = VendorItemDetailFactory.getInstance().create(vendorItem, item, vendorParty,
                vendorItemName, description, priority, cancellationPolicy, returnPolicy, session.START_TIME_LONG,
                Session.MAX_TIME_LONG);
        
        // Convert to R/W
        vendorItem = VendorItemFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE, vendorItem.getPrimaryKey());
        vendorItem.setActiveDetail(vendorItemDetail);
        vendorItem.setLastDetail(vendorItemDetail);
        vendorItem.store();
        
        sendEventUsingNames(vendorItem.getPrimaryKey(), EventTypes.CREATE.name(), null, null, createdBy);
        
        return vendorItem;
    }
    
    /** Assume that the entityInstance passed to this function is a ECHOTHREE.VendorItem */
    public VendorItem getVendorItemByEntityInstance(EntityInstance entityInstance) {
        VendorItemPK pk = new VendorItemPK(entityInstance.getEntityUniqueId());
        VendorItem vendorItem = VendorItemFactory.getInstance().getEntityFromPK(EntityPermission.READ_ONLY, pk);
        
        return vendorItem;
    }
    
    public long countVendorItemsByItem(Item item) {
        return session.queryForLong(
                "SELECT COUNT(*) " +
                "FROM vendoritems, vendoritemdetails " +
                "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid " +
                "AND vndritmdt_itm_itemid = ?",
                item);
    }
    
    public long countVendorItemsByVendorParty(Party vendorParty) {
        return session.queryForLong(
                "SELECT COUNT(*) " +
                "FROM vendoritems, vendoritemdetails " +
                "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid " +
                "AND vndritmdt_vendorpartyid = ?",
                vendorParty);
    }
    
    public long countVendorItemsByCancellationPolicy(CancellationPolicy cancellationPolicy) {
        return session.queryForLong(
                "SELECT COUNT(*) " +
                "FROM vendoritems, vendoritemdetails " +
                "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid " +
                "AND vndritmdt_cnclplcy_cancellationpolicyid = ?",
                cancellationPolicy);
    }

    public long countVendorItemsByReturnPolicy(ReturnPolicy returnPolicy) {
        return session.queryForLong(
                "SELECT COUNT(*) " +
                "FROM vendoritems, vendoritemdetails " +
                "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid " +
                "AND vndritmdt_rtnplcy_returnpolicyid = ?",
                returnPolicy);
    }

    private VendorItem getVendorItemByVendorPartyAndVendorItemName(Party vendorParty, String vendorItemName, EntityPermission entityPermission) {
        VendorItem vendorItem = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ " +
                        "FROM vendoritems, vendoritemdetails " +
                        "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid AND vndritmdt_vendorpartyid = ? " +
                        "AND vndritmdt_vendoritemname = ?";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ " +
                        "FROM vendoritems, vendoritemdetails " +
                        "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid AND vndritmdt_vendorpartyid = ? " +
                        "AND vndritmdt_vendoritemname = ? " +
                        "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorItemFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, vendorParty.getPrimaryKey().getEntityId());
            ps.setString(2, vendorItemName);
            
            vendorItem = VendorItemFactory.getInstance().getEntityFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItem;
    }
    
    public VendorItem getVendorItemByVendorPartyAndVendorItemName(Party vendorParty, String vendorItemName) {
        return getVendorItemByVendorPartyAndVendorItemName(vendorParty, vendorItemName, EntityPermission.READ_ONLY);
    }
    
    public VendorItem getVendorItemByVendorPartyAndVendorItemNameForUpdate(Party vendorParty, String vendorItemName) {
        return getVendorItemByVendorPartyAndVendorItemName(vendorParty, vendorItemName, EntityPermission.READ_WRITE);
    }
    
    public VendorItemDetailValue getVendorItemDetailValueForUpdate(VendorItem vendorItem) {
        return vendorItem == null? null: vendorItem.getLastDetailForUpdate().getVendorItemDetailValue().clone();
    }
    
    public VendorItemDetailValue getVendorItemDetailValueByVendorPartyAndVendorItemNameForUpdate(Party vendorParty, String vendorItemName) {
        return getVendorItemDetailValueForUpdate(getVendorItemByVendorPartyAndVendorItemNameForUpdate(vendorParty, vendorItemName));
    }
    
    private List<VendorItem> getVendorItemsByItem(Item item, EntityPermission entityPermission) {
        List<VendorItem> vendorItems = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ "
                        + "FROM vendoritems, vendoritemdetails, vendors "
                        + "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid AND vndritmdt_itm_itemid = ? "
                        + "AND vndritmdt_vendorpartyid = vndr_par_partyid AND vndr_thrutime = ? "
                        + "ORDER BY vndr_vendorname "
                        + "_LIMIT_";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ "
                        + "FROM vendoritems, vendoritemdetails, vendors "
                        + "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid AND vndritmdt_itm_itemid = ? "
                        + "AND vndritmdt_vendorpartyid = vndr_par_partyid AND vndr_thrutime = ? "
                        + "FOR UPDATE";
            }
            PreparedStatement ps = VendorItemFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, item.getPrimaryKey().getEntityId());
            ps.setLong(2, Session.MAX_TIME);
            
            vendorItems = VendorItemFactory.getInstance().getEntitiesFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItems;
    }
    
    public List<VendorItem> getVendorItemsByItem(Item item) {
        return getVendorItemsByItem(item, EntityPermission.READ_ONLY);
    }
    
    public List<VendorItem> getVendorItemsByItemForUpdate(Item item) {
        return getVendorItemsByItem(item, EntityPermission.READ_WRITE);
    }
    
    private List<VendorItem> getVendorItemsByVendorParty(Party vendorParty, EntityPermission entityPermission) {
        List<VendorItem> vendorItems = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ "
                        + "FROM vendoritems, vendoritemdetails, items, itemdetails "
                        + "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid AND vndritmdt_vendorpartyid = ? "
                        + "AND vndritmdt_itm_itemid = itm_itemid AND itm_lastdetailid = itmdt_itemdetailid "
                        + "ORDER BY itmdt_itemname "
                        + "_LIMIT_";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ " 
                        + "FROM vendoritems, vendoritemdetails, items, itemdetails "
                        + "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid AND vndritmdt_vendorpartyid = ? "
                        + "AND vndritmdt_itm_itemid = itm_itemid AND itm_lastdetailid = itmdt_itemdetailid "
                        + "FOR UPDATE";
            }
            PreparedStatement ps = VendorItemFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, vendorParty.getPrimaryKey().getEntityId());
            
            vendorItems = VendorItemFactory.getInstance().getEntitiesFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItems;
    }
    
    public List<VendorItem> getVendorItemsByVendorParty(Party vendorParty) {
        return getVendorItemsByVendorParty(vendorParty, EntityPermission.READ_ONLY);
    }
    
    public List<VendorItem> getVendorItemsByVendorPartyForUpdate(Party vendorParty) {
        return getVendorItemsByVendorParty(vendorParty, EntityPermission.READ_WRITE);
    }
    
    private List<VendorItem> getVendorItemsByVendorItemName(String vendorItemName, EntityPermission entityPermission) {
        List<VendorItem> vendorItems = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ "
                        + "FROM vendoritems, vendoritemdetails, vendors "
                        + "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid "
                        + "AND vndritmdt_vendoritemname = ? "
                        + "AND vndritmdt_vendorpartyid = vndr_par_partyid AND vndr_thrutime = ? "
                        + "ORDER BY vndr_vendorname "
                        + "_LIMIT_";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ "
                        + "FROM vendoritems, vendoritemdetails "
                        + "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid "
                        + "AND vndritmdt_vendoritemname = ? "
                        + "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorItemFactory.getInstance().prepareStatement(query);
            
            ps.setString(1, vendorItemName);
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                ps.setLong(2, Session.MAX_TIME);
            }
            
            vendorItems = VendorItemFactory.getInstance().getEntitiesFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItems;
    }
    
    public List<VendorItem> getVendorItemsByVendorItemName(String vendorItemName) {
        return getVendorItemsByVendorItemName(vendorItemName, EntityPermission.READ_ONLY);
    }
    
    public List<VendorItem> getVendorItemsByVendorItemNameForUpdate(String vendorItemName) {
        return getVendorItemsByVendorItemName(vendorItemName, EntityPermission.READ_WRITE);
    }
    
    public VendorItemStatusChoicesBean getVendorItemStatusChoices(String defaultVendorItemStatusChoice, Language language,
            boolean allowNullChoice, VendorItem vendorItem, PartyPK partyPK) {
        var workflowControl = getWorkflowControl();
        VendorItemStatusChoicesBean vendorItemStatusChoicesBean = new VendorItemStatusChoicesBean();

        if(vendorItem == null) {
            workflowControl.getWorkflowEntranceChoices(vendorItemStatusChoicesBean, defaultVendorItemStatusChoice, language, allowNullChoice,
                    workflowControl.getWorkflowByName(VendorItemStatusConstants.Workflow_VENDOR_ITEM_STATUS), partyPK);
        } else {
            EntityInstance entityInstance = getCoreControl().getEntityInstanceByBasePK(vendorItem.getPrimaryKey());
            WorkflowEntityStatus workflowEntityStatus = workflowControl.getWorkflowEntityStatusByEntityInstanceUsingNames(VendorItemStatusConstants.Workflow_VENDOR_ITEM_STATUS,
                    entityInstance);

            workflowControl.getWorkflowDestinationChoices(vendorItemStatusChoicesBean, defaultVendorItemStatusChoice, language, allowNullChoice,
                    workflowEntityStatus.getWorkflowStep(), partyPK);
        }

        return vendorItemStatusChoicesBean;
    }

    public void setVendorItemStatus(ExecutionErrorAccumulator eea, VendorItem vendorItem, String vendorItemStatusChoice, PartyPK modifiedBy) {
        var workflowControl = getWorkflowControl();
        EntityInstance entityInstance = getEntityInstanceByBaseEntity(vendorItem);
        WorkflowEntityStatus workflowEntityStatus = workflowControl.getWorkflowEntityStatusByEntityInstanceForUpdateUsingNames(VendorItemStatusConstants.Workflow_VENDOR_ITEM_STATUS,
                entityInstance);
        WorkflowDestination workflowDestination = vendorItemStatusChoice == null? null:
            workflowControl.getWorkflowDestinationByName(workflowEntityStatus.getWorkflowStep(), vendorItemStatusChoice);

        if(workflowDestination != null || vendorItemStatusChoice == null) {
            workflowControl.transitionEntityInWorkflow(eea, workflowEntityStatus, workflowDestination, null, modifiedBy);
        } else {
            eea.addExecutionError(ExecutionErrors.UnknownVendorItemStatusChoice.name(), vendorItemStatusChoice);
        }
    }

    public VendorItemTransfer getVendorItemTransfer(UserVisit userVisit, VendorItem vendorItem) {
        return getVendorTransferCaches(userVisit).getVendorItemTransferCache().getVendorItemTransfer(vendorItem);
    }
    
    public List<VendorItemTransfer> getVendorItemTransfers(UserVisit userVisit, List<VendorItem> vendorItems) {
        List<VendorItemTransfer> vendorItemTransfers = new ArrayList<>(vendorItems.size());
        VendorItemTransferCache vendorItemTransferCache = getVendorTransferCaches(userVisit).getVendorItemTransferCache();
        
        vendorItems.stream().forEach((vendorItem) -> {
            vendorItemTransfers.add(vendorItemTransferCache.getVendorItemTransfer(vendorItem));
        });
        
        return vendorItemTransfers;
    }
    
    public List<VendorItemTransfer> getVendorItemTransfersByItem(UserVisit userVisit, Item item) {
        return getVendorItemTransfers(userVisit, getVendorItemsByItem(item));
    }
    
    public List<VendorItemTransfer> getVendorItemTransfersByVendorParty(UserVisit userVisit, Party vendorParty) {
        return getVendorItemTransfers(userVisit, getVendorItemsByVendorParty(vendorParty));
    }
    
    public void updateVendorItemFromValue(VendorItemDetailValue vendorItemDetailValue, BasePK updatedBy) {
        if(vendorItemDetailValue.hasBeenModified()) {
            VendorItem vendorItem = VendorItemFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE,
                     vendorItemDetailValue.getVendorItemPK());
            VendorItemDetail vendorItemDetail = vendorItem.getActiveDetailForUpdate();
            
            vendorItemDetail.setThruTime(session.START_TIME_LONG);
            vendorItemDetail.store();
            
            VendorItemPK vendorItemPK = vendorItemDetail.getVendorItemPK(); // Not updated
            ItemPK itemPK = vendorItemDetail.getItemPK(); // Not updated
            PartyPK vendorPartyPK = vendorItemDetail.getVendorPartyPK(); // Not updated
            String vendorItemName = vendorItemDetailValue.getVendorItemName();
            String description = vendorItemDetailValue.getDescription();
            Integer priority = vendorItemDetailValue.getPriority();
            CancellationPolicyPK cancellationPolicyPK = vendorItemDetailValue.getCancellationPolicyPK();
            ReturnPolicyPK returnPolicyPK = vendorItemDetailValue.getReturnPolicyPK();
            
            vendorItemDetail = VendorItemDetailFactory.getInstance().create(vendorItemPK, itemPK, vendorPartyPK,
                    vendorItemName, description, priority, cancellationPolicyPK, returnPolicyPK, session.START_TIME_LONG,
                    Session.MAX_TIME_LONG);
            
            vendorItem.setActiveDetail(vendorItemDetail);
            vendorItem.setLastDetail(vendorItemDetail);
            
            sendEventUsingNames(vendorItemPK, EventTypes.MODIFY.name(), null, null, updatedBy);
        }
    }
    
    public void deleteVendorItem(VendorItem vendorItem, BasePK deletedBy) {
        deleteVendorItemCostsByVendorItem(vendorItem, deletedBy);
        
        VendorItemDetail vendorItemDetail = vendorItem.getLastDetailForUpdate();
        vendorItemDetail.setThruTime(session.START_TIME_LONG);
        vendorItem.setActiveDetail(null);
        vendorItem.store();
        
        sendEventUsingNames(vendorItem.getPrimaryKey(), EventTypes.DELETE.name(), null, null, deletedBy);
    }
    
    public void deleteVendorItems(List<VendorItem> vendorItems, BasePK deletedBy) {
        vendorItems.stream().forEach((vendorItem) -> {
            deleteVendorItem(vendorItem, deletedBy);
        });
    }
    
    public void deleteVendorItemsByItem(Item item, BasePK deletedBy) {
        deleteVendorItems(getVendorItemsByItemForUpdate(item), deletedBy);
    }
    
    public void deleteVendorItemsByVendorParty(Party vendorParty, BasePK deletedBy) {
        deleteVendorItems(getVendorItemsByVendorPartyForUpdate(vendorParty), deletedBy);
    }
    
    // --------------------------------------------------------------------------------
    //   Vendor Item Costs
    // --------------------------------------------------------------------------------
    
    public VendorItemCost createVendorItemCost(VendorItem vendorItem, InventoryCondition inventoryCondition,
            UnitOfMeasureType unitOfMeasureType, Long unitCost, BasePK createdBy) {
        VendorItemCost vendorItemCost = VendorItemCostFactory.getInstance().create(vendorItem, inventoryCondition,
                unitOfMeasureType, unitCost, session.START_TIME_LONG, Session.MAX_TIME_LONG);
        
        sendEventUsingNames(vendorItem.getPrimaryKey(), EventTypes.MODIFY.name(), vendorItemCost.getPrimaryKey(), EventTypes.CREATE.name(), createdBy);
        
        return vendorItemCost;
    }
    
    private VendorItemCost getVendorItemCost(VendorItem vendorItem, InventoryCondition inventoryCondition,
            UnitOfMeasureType unitOfMeasureType, EntityPermission entityPermission) {
        VendorItemCost vendorItemCost = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ " +
                        "FROM vendoritemcosts " +
                        "WHERE vndritmc_vndritm_vendoritemid = ? AND vndritmc_invcon_inventoryconditionid = ? " +
                        "AND vndritmc_uomt_unitofmeasuretypeid = ? AND vndritmc_thrutime = ?";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ " +
                        "FROM vendoritemcosts " +
                        "WHERE vndritmc_vndritm_vendoritemid = ? AND vndritmc_invcon_inventoryconditionid = ? " +
                        "AND vndritmc_uomt_unitofmeasuretypeid = ? AND vndritmc_thrutime = ? " +
                        "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorItemCostFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, vendorItem.getPrimaryKey().getEntityId());
            ps.setLong(2, inventoryCondition.getPrimaryKey().getEntityId());
            ps.setLong(3, unitOfMeasureType.getPrimaryKey().getEntityId());
            ps.setLong(4, Session.MAX_TIME);
            
            vendorItemCost = VendorItemCostFactory.getInstance().getEntityFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItemCost;
    }
    
    public VendorItemCost getVendorItemCost(VendorItem vendorItem, InventoryCondition inventoryCondition,
            UnitOfMeasureType unitOfMeasureType) {
        return getVendorItemCost(vendorItem, inventoryCondition, unitOfMeasureType, EntityPermission.READ_ONLY);
    }
    
    public VendorItemCost getVendorItemCostForUpdate(VendorItem vendorItem, InventoryCondition inventoryCondition,
            UnitOfMeasureType unitOfMeasureType) {
        return getVendorItemCost(vendorItem, inventoryCondition, unitOfMeasureType, EntityPermission.READ_WRITE);
    }
    
    public VendorItemCostValue getVendorItemCostValue(VendorItemCost vendorItemCost) {
        return vendorItemCost == null? null: vendorItemCost.getVendorItemCostValue().clone();
    }
    
    public VendorItemCostValue getVendorItemCostValueForUpdate(VendorItem vendorItem, InventoryCondition inventoryCondition,
            UnitOfMeasureType unitOfMeasureType) {
        return getVendorItemCostValue(getVendorItemCostForUpdate(vendorItem, inventoryCondition, unitOfMeasureType));
    }
    
    private List<VendorItemCost> getVendorItemCostsByVendorItem(VendorItem vendorItem, EntityPermission entityPermission) {
        List<VendorItemCost> vendorItemCosts = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ "
                        + "FROM vendoritemcosts, inventoryconditions, inventoryconditiondetails, unitofmeasuretypes, unitofmeasuretypedetails "
                        + "WHERE vndritmc_vndritm_vendoritemid = ? AND vndritmc_thrutime = ? "
                        + "AND vndritmc_invcon_inventoryconditionid = invcon_inventoryconditionid AND invcon_lastdetailid = invcondt_inventoryconditiondetailid "
                        + "AND vndritmc_uomt_unitofmeasuretypeid = uomt_unitofmeasuretypeid AND uomt_lastdetailid = uomtdt_unitofmeasuretypedetailid "
                        + "ORDER BY invcondt_sortorder, invcondt_inventoryconditionname, uomtdt_sortorder, uomtdt_unitofmeasuretypename "
                        + "_LIMIT_";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ "
                        + "FROM vendoritemcosts, inventoryconditions, inventoryconditiondetails, unitofmeasuretypes, unitofmeasuretypedetails "
                        + "WHERE vndritmc_vndritm_vendoritemid = ? AND vndritmc_thrutime = ? "
                        + "AND vndritmc_invcon_inventoryconditionid = invcon_inventoryconditionid AND invcon_lastdetailid = invcondt_inventoryconditiondetailid "
                        + "AND vndritmc_uomt_unitofmeasuretypeid = uomt_unitofmeasuretypeid AND uomt_lastdetailid = uomtdt_unitofmeasuretypedetailid "
                        + "FOR UPDATE";
            }
            
            PreparedStatement ps = VendorItemCostFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, vendorItem.getPrimaryKey().getEntityId());
            ps.setLong(2, Session.MAX_TIME);
            
            vendorItemCosts = VendorItemCostFactory.getInstance().getEntitiesFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItemCosts;
    }
    
    public List<VendorItemCost> getVendorItemCostsByVendorItem(VendorItem vendorItem) {
        return getVendorItemCostsByVendorItem(vendorItem, EntityPermission.READ_ONLY);
    }
    
    public List<VendorItemCost> getVendorItemCostsByVendorItemForUpdate(VendorItem vendorItem) {
        return getVendorItemCostsByVendorItem(vendorItem, EntityPermission.READ_WRITE);
    }
    
    public List<VendorItemCost> getVendorItemCostsByInventoryConditionForUpdate(InventoryCondition inventoryCondition) {
        List<VendorItemCost> vendorItemCosts = null;
        
        try {
            PreparedStatement ps = VendorItemCostFactory.getInstance().prepareStatement(
                    "SELECT _ALL_ " +
                    "FROM vendoritemcosts " +
                    "WHERE vndritmc_invcon_inventoryconditionid = ? AND vndritmc_thrutime = ? " +
                    "FOR UPDATE");
            
            ps.setLong(1, inventoryCondition.getPrimaryKey().getEntityId());
            ps.setLong(2, Session.MAX_TIME);
            
            vendorItemCosts = VendorItemCostFactory.getInstance().getEntitiesFromQuery(EntityPermission.READ_WRITE, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItemCosts;
    }
    
    public List<VendorItemCost> getVendorItemCostsByUnitOfMeasureTypeForUpdate(UnitOfMeasureType unitOfMeasureType) {
        List<VendorItemCost> vendorItemCosts = null;
        
        try {
            PreparedStatement ps = VendorItemCostFactory.getInstance().prepareStatement(
                    "SELECT _ALL_ " +
                    "FROM vendoritemcosts " +
                    "WHERE vndritmc_uomt_unitofmeasuretypeid = ? AND vndritmc_thrutime = ? " +
                    "FOR UPDATE");
            
            ps.setLong(1, unitOfMeasureType.getPrimaryKey().getEntityId());
            ps.setLong(2, Session.MAX_TIME);
            
            vendorItemCosts = VendorItemCostFactory.getInstance().getEntitiesFromQuery(EntityPermission.READ_WRITE, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItemCosts;
    }
    
    public List<VendorItemCost> getVendorItemCostsByItemAndUnitOfMeasureTypeForUpdate(Item item, UnitOfMeasureType unitOfMeasureType) {
        List<VendorItemCost> vendorItemCosts = null;
        
        try {
            PreparedStatement ps = VendorItemCostFactory.getInstance().prepareStatement(
                    "SELECT _ALL_ " +
                    "FROM vendoritems, vendoritemdetails, vendoritemcosts " +
                    "WHERE vndritm_activedetailid = vndritmdt_vendoritemdetailid AND vndritmdt_itm_itemid = ? " +
                    "AND vndritm_vendoritemid = vndritmc_vndritm_vendoritemid AND vndritmc_uomt_unitofmeasuretypeid = ? AND vndritmc_thrutime = ? " +
                    "FOR UPDATE");
            
            ps.setLong(1, item.getPrimaryKey().getEntityId());
            ps.setLong(2, unitOfMeasureType.getPrimaryKey().getEntityId());
            ps.setLong(3, Session.MAX_TIME);
            
            vendorItemCosts = VendorItemCostFactory.getInstance().getEntitiesFromQuery(EntityPermission.READ_WRITE, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return vendorItemCosts;
    }
    
    public VendorItemCostTransfer getVendorItemCostTransfer(UserVisit userVisit, VendorItemCost vendorItemCost) {
        return getVendorTransferCaches(userVisit).getVendorItemCostTransferCache().getVendorItemCostTransfer(vendorItemCost);
    }
    
    public List<VendorItemCostTransfer> getVendorItemCostTransfers(UserVisit userVisit, List<VendorItemCost> vendorItemCosts) {
        List<VendorItemCostTransfer> vendorItemCostTransfers = vendorItemCostTransfers = new ArrayList<>(vendorItemCosts.size());
        VendorItemCostTransferCache vendorItemCostTransferCache = getVendorTransferCaches(userVisit).getVendorItemCostTransferCache();
        
        for(VendorItemCost vendorItemCost: vendorItemCosts) {
            vendorItemCostTransfers.add(vendorItemCostTransferCache.getVendorItemCostTransfer(vendorItemCost));
        }
        
        return vendorItemCostTransfers;
    }
    
    public List<VendorItemCostTransfer> getVendorItemCostTransfersByVendorItem(UserVisit userVisit, VendorItem vendorItem) {
        return getVendorItemCostTransfers(userVisit, getVendorItemCostsByVendorItem(vendorItem));
    }
    
    public void updateVendorItemCostFromValue(VendorItemCostValue vendorItemCostValue, BasePK updatedBy) {
        if(vendorItemCostValue.hasBeenModified()) {
            VendorItemCost vendorItemCost = VendorItemCostFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE,
                     vendorItemCostValue.getPrimaryKey());
            
            vendorItemCost.setThruTime(session.START_TIME_LONG);
            vendorItemCost.store();
            
            VendorItemPK vendorItemPK = vendorItemCost.getVendorItemPK();
            InventoryConditionPK inventoryConditionPK = vendorItemCost.getInventoryConditionPK();
            UnitOfMeasureTypePK unitOfMeasureTypePK = vendorItemCost.getUnitOfMeasureTypePK();
            Long unitCost = vendorItemCostValue.getUnitCost();
            
            vendorItemCost = VendorItemCostFactory.getInstance().create(vendorItemPK, inventoryConditionPK,
                    unitOfMeasureTypePK, unitCost, session.START_TIME_LONG, Session.MAX_TIME_LONG);
            
            sendEventUsingNames(vendorItemPK, EventTypes.MODIFY.name(), vendorItemCost.getPrimaryKey(), EventTypes.MODIFY.name(), updatedBy);
        }
    }
    
    public void deleteVendorItemCost(VendorItemCost vendorItemCost, BasePK deletedBy) {
        vendorItemCost.setThruTime(session.START_TIME_LONG);
        
        sendEventUsingNames(vendorItemCost.getVendorItemPK(), EventTypes.MODIFY.name(), vendorItemCost.getPrimaryKey(), EventTypes.DELETE.name(), deletedBy);
    }
    
    public void deleteVendorItemCosts(List<VendorItemCost> vendorItemCosts, BasePK deletedBy) {
        vendorItemCosts.stream().forEach((vendorItemCost) -> {
            deleteVendorItemCost(vendorItemCost, deletedBy);
        });
    }
    
    public void deleteVendorItemCostsByVendorItem(VendorItem vendorItem, BasePK deletedBy) {
        deleteVendorItemCosts(getVendorItemCostsByVendorItemForUpdate(vendorItem), deletedBy);
    }
    
    public void deleteVendorItemCostsByInventoryCondition(InventoryCondition inventoryCondition, BasePK deletedBy) {
        deleteVendorItemCosts(getVendorItemCostsByInventoryConditionForUpdate(inventoryCondition), deletedBy);
    }
    
    public void deleteVendorItemCostsByUnitOfMeasureType(UnitOfMeasureType unitOfMeasureType, BasePK deletedBy) {
        deleteVendorItemCosts(getVendorItemCostsByUnitOfMeasureTypeForUpdate(unitOfMeasureType), deletedBy);
    }
    
    public void deleteVendorItemCostsByItemAndUnitOfMeasureType(Item item, UnitOfMeasureType unitOfMeasureType, BasePK deletedBy) {
        deleteVendorItemCosts(getVendorItemCostsByItemAndUnitOfMeasureTypeForUpdate(item, unitOfMeasureType), deletedBy);
    }
    
    // --------------------------------------------------------------------------------
    //   Item Purchasing Categories
    // --------------------------------------------------------------------------------
    
    public ItemPurchasingCategory createItemPurchasingCategory(String itemPurchasingCategoryName,
            ItemPurchasingCategory parentItemPurchasingCategory, Boolean isDefault, Integer sortOrder, BasePK createdBy) {
        ItemPurchasingCategory defaultItemPurchasingCategory = getDefaultItemPurchasingCategory();
        boolean defaultFound = defaultItemPurchasingCategory != null;
        
        if(defaultFound && isDefault) {
            ItemPurchasingCategoryDetailValue defaultItemPurchasingCategoryDetailValue = getDefaultItemPurchasingCategoryDetailValueForUpdate();
            
            defaultItemPurchasingCategoryDetailValue.setIsDefault(Boolean.FALSE);
            updateItemPurchasingCategoryFromValue(defaultItemPurchasingCategoryDetailValue, false, createdBy);
        } else if(!defaultFound) {
            isDefault = Boolean.TRUE;
        }
        
        ItemPurchasingCategory itemPurchasingCategory = ItemPurchasingCategoryFactory.getInstance().create();
        ItemPurchasingCategoryDetail itemPurchasingCategoryDetail = ItemPurchasingCategoryDetailFactory.getInstance().create(session,
                itemPurchasingCategory, itemPurchasingCategoryName, parentItemPurchasingCategory, isDefault,
                sortOrder, session.START_TIME_LONG, Session.MAX_TIME_LONG);
        
        // Convert to R/W
        itemPurchasingCategory = ItemPurchasingCategoryFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE,
                itemPurchasingCategory.getPrimaryKey());
        itemPurchasingCategory.setActiveDetail(itemPurchasingCategoryDetail);
        itemPurchasingCategory.setLastDetail(itemPurchasingCategoryDetail);
        itemPurchasingCategory.store();
        
        sendEventUsingNames(itemPurchasingCategory.getPrimaryKey(), EventTypes.CREATE.name(), null, null, createdBy);
        
        return itemPurchasingCategory;
    }
    
    private static final Map<EntityPermission, String> getItemPurchasingCategoryByNameQueries;

    static {
        Map<EntityPermission, String> queryMap = new HashMap<>(2);

        queryMap.put(EntityPermission.READ_ONLY,
                "SELECT _ALL_ " +
                "FROM itempurchasingcategories, itempurchasingcategorydetails " +
                "WHERE iprchc_activedetailid = iprchcdt_itempurchasingcategorydetailid " +
                "AND iprchcdt_itempurchasingcategoryname = ?");
        queryMap.put(EntityPermission.READ_WRITE,
                "SELECT _ALL_ " +
                "FROM itempurchasingcategories, itempurchasingcategorydetails " +
                "WHERE iprchc_activedetailid = iprchcdt_itempurchasingcategorydetailid " +
                "AND iprchcdt_itempurchasingcategoryname = ? " +
                "FOR UPDATE");
        getItemPurchasingCategoryByNameQueries = Collections.unmodifiableMap(queryMap);
    }

    private ItemPurchasingCategory getItemPurchasingCategoryByName(String itemPurchasingCategoryName, EntityPermission entityPermission) {
        return ItemPurchasingCategoryFactory.getInstance().getEntityFromQuery(entityPermission, getItemPurchasingCategoryByNameQueries, itemPurchasingCategoryName);
    }

    public ItemPurchasingCategory getItemPurchasingCategoryByName(String itemPurchasingCategoryName) {
        return getItemPurchasingCategoryByName(itemPurchasingCategoryName, EntityPermission.READ_ONLY);
    }

    public ItemPurchasingCategory getItemPurchasingCategoryByNameForUpdate(String itemPurchasingCategoryName) {
        return getItemPurchasingCategoryByName(itemPurchasingCategoryName, EntityPermission.READ_WRITE);
    }

    public ItemPurchasingCategoryDetailValue getItemPurchasingCategoryDetailValueForUpdate(ItemPurchasingCategory itemPurchasingCategory) {
        return itemPurchasingCategory == null? null: itemPurchasingCategory.getLastDetailForUpdate().getItemPurchasingCategoryDetailValue().clone();
    }

    public ItemPurchasingCategoryDetailValue getItemPurchasingCategoryDetailValueByNameForUpdate(String itemPurchasingCategoryName) {
        return getItemPurchasingCategoryDetailValueForUpdate(getItemPurchasingCategoryByNameForUpdate(itemPurchasingCategoryName));
    }

    private static final Map<EntityPermission, String> getDefaultItemPurchasingCategoryQueries;

    static {
        Map<EntityPermission, String> queryMap = new HashMap<>(2);

        queryMap.put(EntityPermission.READ_ONLY,
                "SELECT _ALL_ " +
                "FROM itempurchasingcategories, itempurchasingcategorydetails " +
                "WHERE iprchc_activedetailid = iprchcdt_itempurchasingcategorydetailid " +
                "AND iprchcdt_isdefault = 1");
        queryMap.put(EntityPermission.READ_WRITE,
                "SELECT _ALL_ " +
                "FROM itempurchasingcategories, itempurchasingcategorydetails " +
                "WHERE iprchc_activedetailid = iprchcdt_itempurchasingcategorydetailid " +
                "AND iprchcdt_isdefault = 1 " +
                "FOR UPDATE");
        getDefaultItemPurchasingCategoryQueries = Collections.unmodifiableMap(queryMap);
    }

    private ItemPurchasingCategory getDefaultItemPurchasingCategory(EntityPermission entityPermission) {
        return ItemPurchasingCategoryFactory.getInstance().getEntityFromQuery(entityPermission, getDefaultItemPurchasingCategoryQueries);
    }

    public ItemPurchasingCategory getDefaultItemPurchasingCategory() {
        return getDefaultItemPurchasingCategory(EntityPermission.READ_ONLY);
    }

    public ItemPurchasingCategory getDefaultItemPurchasingCategoryForUpdate() {
        return getDefaultItemPurchasingCategory(EntityPermission.READ_WRITE);
    }

    public ItemPurchasingCategoryDetailValue getDefaultItemPurchasingCategoryDetailValueForUpdate() {
        return getDefaultItemPurchasingCategoryForUpdate().getLastDetailForUpdate().getItemPurchasingCategoryDetailValue().clone();
    }

    private static final Map<EntityPermission, String> getItemPurchasingCategoriesQueries;

    static {
        Map<EntityPermission, String> queryMap = new HashMap<>(2);

        queryMap.put(EntityPermission.READ_ONLY,
                "SELECT _ALL_ "
                + "FROM itempurchasingcategories, itempurchasingcategorydetails "
                + "WHERE iprchc_activedetailid = iprchcdt_itempurchasingcategorydetailid "
                + "ORDER BY iprchcdt_sortorder, iprchcdt_itempurchasingcategoryname "
                + "_LIMIT_");
        queryMap.put(EntityPermission.READ_WRITE,
                "SELECT _ALL_ "
                + "FROM itempurchasingcategories, itempurchasingcategorydetails "
                + "WHERE iprchc_activedetailid = iprchcdt_itempurchasingcategorydetailid "
                + "FOR UPDATE");
        getItemPurchasingCategoriesQueries = Collections.unmodifiableMap(queryMap);
    }

    private List<ItemPurchasingCategory> getItemPurchasingCategories(EntityPermission entityPermission) {
        return ItemPurchasingCategoryFactory.getInstance().getEntitiesFromQuery(entityPermission, getItemPurchasingCategoriesQueries);
    }

    public List<ItemPurchasingCategory> getItemPurchasingCategories() {
        return getItemPurchasingCategories(EntityPermission.READ_ONLY);
    }

    public List<ItemPurchasingCategory> getItemPurchasingCategoriesForUpdate() {
        return getItemPurchasingCategories(EntityPermission.READ_WRITE);
    }

    private static final Map<EntityPermission, String> getItemPurchasingCategoriesByParentItemPurchasingCategoryQueries;

    static {
        Map<EntityPermission, String> queryMap = new HashMap<>(2);

        queryMap.put(EntityPermission.READ_ONLY,
                "SELECT _ALL_ "
                + "FROM itempurchasingcategories, itempurchasingcategorydetails "
                + "WHERE iprchc_activedetailid = iprchcdt_itempurchasingcategorydetailid AND iprchcdt_parentitempurchasingcategoryid = ? "
                + "ORDER BY iprchcdt_sortorder, iprchcdt_itempurchasingcategoryname "
                + "_LIMIT_");
        queryMap.put(EntityPermission.READ_WRITE,
                "SELECT _ALL_ "
                + "FROM itempurchasingcategories, itempurchasingcategorydetails "
                + "WHERE iprchc_activedetailid = iprchcdt_itempurchasingcategorydetailid AND iprchcdt_parentitempurchasingcategoryid = ? "
                + "FOR UPDATE");
        getItemPurchasingCategoriesByParentItemPurchasingCategoryQueries = Collections.unmodifiableMap(queryMap);
    }

    private List<ItemPurchasingCategory> getItemPurchasingCategoriesByParentItemPurchasingCategory(ItemPurchasingCategory parentItemPurchasingCategory,
            EntityPermission entityPermission) {
        return ItemPurchasingCategoryFactory.getInstance().getEntitiesFromQuery(entityPermission, getItemPurchasingCategoriesByParentItemPurchasingCategoryQueries,
                parentItemPurchasingCategory);
    }

    public List<ItemPurchasingCategory> getItemPurchasingCategoriesByParentItemPurchasingCategory(ItemPurchasingCategory parentItemPurchasingCategory) {
        return getItemPurchasingCategoriesByParentItemPurchasingCategory(parentItemPurchasingCategory, EntityPermission.READ_ONLY);
    }

    public List<ItemPurchasingCategory> getItemPurchasingCategoriesByParentItemPurchasingCategoryForUpdate(ItemPurchasingCategory parentItemPurchasingCategory) {
        return getItemPurchasingCategoriesByParentItemPurchasingCategory(parentItemPurchasingCategory, EntityPermission.READ_WRITE);
    }

    public ItemPurchasingCategoryTransfer getItemPurchasingCategoryTransfer(UserVisit userVisit, ItemPurchasingCategory itemPurchasingCategory) {
        return getVendorTransferCaches(userVisit).getItemPurchasingCategoryTransferCache().getItemPurchasingCategoryTransfer(itemPurchasingCategory);
    }
    
    public List<ItemPurchasingCategoryTransfer> getItemPurchasingCategoryTransfers(UserVisit userVisit) {
        List<ItemPurchasingCategory> itemPurchasingCategories = getItemPurchasingCategories();
        List<ItemPurchasingCategoryTransfer> itemPurchasingCategoryTransfers = new ArrayList<>(itemPurchasingCategories.size());
        ItemPurchasingCategoryTransferCache itemPurchasingCategoryTransferCache = getVendorTransferCaches(userVisit).getItemPurchasingCategoryTransferCache();
        
        itemPurchasingCategories.stream().forEach((itemPurchasingCategory) -> {
            itemPurchasingCategoryTransfers.add(itemPurchasingCategoryTransferCache.getItemPurchasingCategoryTransfer(itemPurchasingCategory));
        });
        
        return itemPurchasingCategoryTransfers;
    }
    
    public ItemPurchasingCategoryChoicesBean getItemPurchasingCategoryChoices(String defaultItemPurchasingCategoryChoice,
            Language language, boolean allowNullChoice) {
        List<ItemPurchasingCategory> itemPurchasingCategories = getItemPurchasingCategories();
        int size = itemPurchasingCategories.size();
        List<String> labels = new ArrayList<>(size);
        List<String> values = new ArrayList<>(size);
        String defaultValue = null;
        
        if(allowNullChoice) {
            labels.add("");
            values.add("");
            
            if(defaultItemPurchasingCategoryChoice == null) {
                defaultValue = "";
            }
        }
        
        for(ItemPurchasingCategory itemPurchasingCategory: itemPurchasingCategories) {
            ItemPurchasingCategoryDetail itemPurchasingCategoryDetail = itemPurchasingCategory.getLastDetail();
            
            String label = getBestItemPurchasingCategoryDescription(itemPurchasingCategory, language);
            String value = itemPurchasingCategoryDetail.getItemPurchasingCategoryName();
            
            labels.add(label == null? value: label);
            values.add(value);
            
            boolean usingDefaultChoice = defaultItemPurchasingCategoryChoice == null? false: defaultItemPurchasingCategoryChoice.equals(value);
            if(usingDefaultChoice || (defaultValue == null && itemPurchasingCategoryDetail.getIsDefault())) {
                defaultValue = value;
            }
        }
        
        return new ItemPurchasingCategoryChoicesBean(labels, values, defaultValue);
    }
    
    public boolean isParentItemPurchasingCategorySafe(ItemPurchasingCategory itemPurchasingCategory,
            ItemPurchasingCategory parentItemPurchasingCategory) {
        boolean safe = true;
        
        if(parentItemPurchasingCategory != null) {
            Set<ItemPurchasingCategory> parentItemPurchasingCategories = new HashSet<>();
            
            parentItemPurchasingCategories.add(itemPurchasingCategory);
            do {
                if(parentItemPurchasingCategories.contains(parentItemPurchasingCategory)) {
                    safe = false;
                    break;
                }
                
                parentItemPurchasingCategories.add(parentItemPurchasingCategory);
                parentItemPurchasingCategory = parentItemPurchasingCategory.getLastDetail().getParentItemPurchasingCategory();
            } while(parentItemPurchasingCategory != null);
        }
        
        return safe;
    }
    
    private void updateItemPurchasingCategoryFromValue(ItemPurchasingCategoryDetailValue itemPurchasingCategoryDetailValue, boolean checkDefault,
            BasePK updatedBy) {
        if(itemPurchasingCategoryDetailValue.hasBeenModified()) {
            ItemPurchasingCategory itemPurchasingCategory = ItemPurchasingCategoryFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE,
                     itemPurchasingCategoryDetailValue.getItemPurchasingCategoryPK());
            ItemPurchasingCategoryDetail itemPurchasingCategoryDetail = itemPurchasingCategory.getActiveDetailForUpdate();
            
            itemPurchasingCategoryDetail.setThruTime(session.START_TIME_LONG);
            itemPurchasingCategoryDetail.store();
            
            ItemPurchasingCategoryPK itemPurchasingCategoryPK = itemPurchasingCategoryDetail.getItemPurchasingCategoryPK();
            String itemPurchasingCategoryName = itemPurchasingCategoryDetailValue.getItemPurchasingCategoryName();
            ItemPurchasingCategoryPK parentItemPurchasingCategoryPK = itemPurchasingCategoryDetailValue.getParentItemPurchasingCategoryPK();
            Boolean isDefault = itemPurchasingCategoryDetailValue.getIsDefault();
            Integer sortOrder = itemPurchasingCategoryDetailValue.getSortOrder();
            
            if(checkDefault) {
                ItemPurchasingCategory defaultItemPurchasingCategory = getDefaultItemPurchasingCategory();
                boolean defaultFound = defaultItemPurchasingCategory != null && !defaultItemPurchasingCategory.equals(itemPurchasingCategory);
                
                if(isDefault && defaultFound) {
                    // If I'm the default, and a default already existed...
                    ItemPurchasingCategoryDetailValue defaultItemPurchasingCategoryDetailValue = getDefaultItemPurchasingCategoryDetailValueForUpdate();
                    
                    defaultItemPurchasingCategoryDetailValue.setIsDefault(Boolean.FALSE);
                    updateItemPurchasingCategoryFromValue(defaultItemPurchasingCategoryDetailValue, false, updatedBy);
                } else if(!isDefault && !defaultFound) {
                    // If I'm not the default, and no other default exists...
                    isDefault = Boolean.TRUE;
                }
            }
            
            itemPurchasingCategoryDetail = ItemPurchasingCategoryDetailFactory.getInstance().create(itemPurchasingCategoryPK,
                    itemPurchasingCategoryName, parentItemPurchasingCategoryPK, isDefault, sortOrder, session.START_TIME_LONG,
                    Session.MAX_TIME_LONG);
            
            itemPurchasingCategory.setActiveDetail(itemPurchasingCategoryDetail);
            itemPurchasingCategory.setLastDetail(itemPurchasingCategoryDetail);
            
            sendEventUsingNames(itemPurchasingCategoryPK, EventTypes.MODIFY.name(), null, null, updatedBy);
        }
    }
    
    public void updateItemPurchasingCategoryFromValue(ItemPurchasingCategoryDetailValue itemPurchasingCategoryDetailValue, BasePK updatedBy) {
        updateItemPurchasingCategoryFromValue(itemPurchasingCategoryDetailValue, true, updatedBy);
    }
    
    private void deleteItemPurchasingCategory(ItemPurchasingCategory itemPurchasingCategory, boolean checkDefault, BasePK deletedBy) {
        ItemPurchasingCategoryDetail itemPurchasingCategoryDetail = itemPurchasingCategory.getLastDetailForUpdate();

        deleteItemPurchasingCategoriesByParentItemPurchasingCategory(itemPurchasingCategory, deletedBy);
        deleteItemPurchasingCategoryDescriptionsByItemPurchasingCategory(itemPurchasingCategory, deletedBy);
        
        itemPurchasingCategoryDetail.setThruTime(session.START_TIME_LONG);
        itemPurchasingCategory.setActiveDetail(null);
        itemPurchasingCategory.store();

        if(checkDefault) {
            // Check for default, and pick one if necessary
            ItemPurchasingCategory defaultItemPurchasingCategory = getDefaultItemPurchasingCategory();

            if(defaultItemPurchasingCategory == null) {
                List<ItemPurchasingCategory> itemPurchasingCategories = getItemPurchasingCategoriesForUpdate();

                if(!itemPurchasingCategories.isEmpty()) {
                    Iterator<ItemPurchasingCategory> iter = itemPurchasingCategories.iterator();
                    if(iter.hasNext()) {
                        defaultItemPurchasingCategory = iter.next();
                    }
                    ItemPurchasingCategoryDetailValue itemPurchasingCategoryDetailValue = defaultItemPurchasingCategory.getLastDetailForUpdate().getItemPurchasingCategoryDetailValue().clone();

                    itemPurchasingCategoryDetailValue.setIsDefault(Boolean.TRUE);
                    updateItemPurchasingCategoryFromValue(itemPurchasingCategoryDetailValue, false, deletedBy);
                }
            }
        }

        sendEventUsingNames(itemPurchasingCategory.getPrimaryKey(), EventTypes.DELETE.name(), null, null, deletedBy);
    }
    
    public void deleteItemPurchasingCategory(ItemPurchasingCategory itemPurchasingCategory, BasePK deletedBy) {
        deleteItemPurchasingCategory(itemPurchasingCategory, true, deletedBy);
    }

    private void deleteItemPurchasingCategories(List<ItemPurchasingCategory> itemPurchasingCategories, boolean checkDefault, BasePK deletedBy) {
        itemPurchasingCategories.stream().forEach((itemPurchasingCategory) -> {
            deleteItemPurchasingCategory(itemPurchasingCategory, checkDefault, deletedBy);
        });
    }

    public void deleteItemPurchasingCategories(List<ItemPurchasingCategory> itemPurchasingCategories, BasePK deletedBy) {
        deleteItemPurchasingCategories(itemPurchasingCategories, true, deletedBy);
    }

    private void deleteItemPurchasingCategoriesByParentItemPurchasingCategory(ItemPurchasingCategory parentItemPurchasingCategory, BasePK deletedBy) {
        deleteItemPurchasingCategories(getItemPurchasingCategoriesByParentItemPurchasingCategoryForUpdate(parentItemPurchasingCategory), false, deletedBy);
    }

    // --------------------------------------------------------------------------------
    //   Item Purchasing Category Descriptions
    // --------------------------------------------------------------------------------
    
    public ItemPurchasingCategoryDescription createItemPurchasingCategoryDescription(ItemPurchasingCategory itemPurchasingCategory, Language language, String description, BasePK createdBy) {
        ItemPurchasingCategoryDescription itemPurchasingCategoryDescription = ItemPurchasingCategoryDescriptionFactory.getInstance().create(itemPurchasingCategory, language, description, session.START_TIME_LONG,
                Session.MAX_TIME_LONG);
        
        sendEventUsingNames(itemPurchasingCategory.getPrimaryKey(), EventTypes.MODIFY.name(), itemPurchasingCategoryDescription.getPrimaryKey(), EventTypes.CREATE.name(), createdBy);
        
        return itemPurchasingCategoryDescription;
    }
    
    private ItemPurchasingCategoryDescription getItemPurchasingCategoryDescription(ItemPurchasingCategory itemPurchasingCategory, Language language, EntityPermission entityPermission) {
        ItemPurchasingCategoryDescription itemPurchasingCategoryDescription = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ " +
                        "FROM itempurchasingcategorydescriptions " +
                        "WHERE iprchcd_iprchc_itempurchasingcategoryid = ? AND iprchcd_lang_languageid = ? AND iprchcd_thrutime = ?";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ " +
                        "FROM itempurchasingcategorydescriptions " +
                        "WHERE iprchcd_iprchc_itempurchasingcategoryid = ? AND iprchcd_lang_languageid = ? AND iprchcd_thrutime = ? " +
                        "FOR UPDATE";
            }
            
            PreparedStatement ps = ItemPurchasingCategoryDescriptionFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, itemPurchasingCategory.getPrimaryKey().getEntityId());
            ps.setLong(2, language.getPrimaryKey().getEntityId());
            ps.setLong(3, Session.MAX_TIME);
            
            itemPurchasingCategoryDescription = ItemPurchasingCategoryDescriptionFactory.getInstance().getEntityFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return itemPurchasingCategoryDescription;
    }
    
    public ItemPurchasingCategoryDescription getItemPurchasingCategoryDescription(ItemPurchasingCategory itemPurchasingCategory, Language language) {
        return getItemPurchasingCategoryDescription(itemPurchasingCategory, language, EntityPermission.READ_ONLY);
    }
    
    public ItemPurchasingCategoryDescription getItemPurchasingCategoryDescriptionForUpdate(ItemPurchasingCategory itemPurchasingCategory, Language language) {
        return getItemPurchasingCategoryDescription(itemPurchasingCategory, language, EntityPermission.READ_WRITE);
    }
    
    public ItemPurchasingCategoryDescriptionValue getItemPurchasingCategoryDescriptionValue(ItemPurchasingCategoryDescription itemPurchasingCategoryDescription) {
        return itemPurchasingCategoryDescription == null? null: itemPurchasingCategoryDescription.getItemPurchasingCategoryDescriptionValue().clone();
    }
    
    public ItemPurchasingCategoryDescriptionValue getItemPurchasingCategoryDescriptionValueForUpdate(ItemPurchasingCategory itemPurchasingCategory, Language language) {
        return getItemPurchasingCategoryDescriptionValue(getItemPurchasingCategoryDescriptionForUpdate(itemPurchasingCategory, language));
    }
    
    private List<ItemPurchasingCategoryDescription> getItemPurchasingCategoryDescriptionsByItemPurchasingCategory(ItemPurchasingCategory itemPurchasingCategory, EntityPermission entityPermission) {
        List<ItemPurchasingCategoryDescription> itemPurchasingCategoryDescriptions = null;
        
        try {
            String query = null;
            
            if(entityPermission.equals(EntityPermission.READ_ONLY)) {
                query = "SELECT _ALL_ "
                        + "FROM itempurchasingcategorydescriptions, languages "
                        + "WHERE iprchcd_iprchc_itempurchasingcategoryid = ? AND iprchcd_thrutime = ? AND iprchcd_lang_languageid = lang_languageid "
                        + "ORDER BY lang_sortorder, lang_languageisoname "
                        + "_LIMIT_";
            } else if(entityPermission.equals(EntityPermission.READ_WRITE)) {
                query = "SELECT _ALL_ "
                        + "FROM itempurchasingcategorydescriptions "
                        + "WHERE iprchcd_iprchc_itempurchasingcategoryid = ? AND iprchcd_thrutime = ? "
                        + "FOR UPDATE";
            }
            
            PreparedStatement ps = ItemPurchasingCategoryDescriptionFactory.getInstance().prepareStatement(query);
            
            ps.setLong(1, itemPurchasingCategory.getPrimaryKey().getEntityId());
            ps.setLong(2, Session.MAX_TIME);
            
            itemPurchasingCategoryDescriptions = ItemPurchasingCategoryDescriptionFactory.getInstance().getEntitiesFromQuery(entityPermission, ps);
        } catch (SQLException se) {
            throw new PersistenceDatabaseException(se);
        }
        
        return itemPurchasingCategoryDescriptions;
    }
    
    public List<ItemPurchasingCategoryDescription> getItemPurchasingCategoryDescriptionsByItemPurchasingCategory(ItemPurchasingCategory itemPurchasingCategory) {
        return getItemPurchasingCategoryDescriptionsByItemPurchasingCategory(itemPurchasingCategory, EntityPermission.READ_ONLY);
    }
    
    public List<ItemPurchasingCategoryDescription> getItemPurchasingCategoryDescriptionsByItemPurchasingCategoryForUpdate(ItemPurchasingCategory itemPurchasingCategory) {
        return getItemPurchasingCategoryDescriptionsByItemPurchasingCategory(itemPurchasingCategory, EntityPermission.READ_WRITE);
    }
    
    public String getBestItemPurchasingCategoryDescription(ItemPurchasingCategory itemPurchasingCategory, Language language) {
        String description;
        ItemPurchasingCategoryDescription itemPurchasingCategoryDescription = getItemPurchasingCategoryDescription(itemPurchasingCategory, language);
        
        if(itemPurchasingCategoryDescription == null && !language.getIsDefault()) {
            itemPurchasingCategoryDescription = getItemPurchasingCategoryDescription(itemPurchasingCategory, getPartyControl().getDefaultLanguage());
        }
        
        if(itemPurchasingCategoryDescription == null) {
            description = itemPurchasingCategory.getLastDetail().getItemPurchasingCategoryName();
        } else {
            description = itemPurchasingCategoryDescription.getDescription();
        }
        
        return description;
    }
    
    public ItemPurchasingCategoryDescriptionTransfer getItemPurchasingCategoryDescriptionTransfer(UserVisit userVisit, ItemPurchasingCategoryDescription itemPurchasingCategoryDescription) {
        return getVendorTransferCaches(userVisit).getItemPurchasingCategoryDescriptionTransferCache().getItemPurchasingCategoryDescriptionTransfer(itemPurchasingCategoryDescription);
    }
    
    public List<ItemPurchasingCategoryDescriptionTransfer> getItemPurchasingCategoryDescriptionTransfersByItemPurchasingCategory(UserVisit userVisit, ItemPurchasingCategory itemPurchasingCategory) {
        List<ItemPurchasingCategoryDescription> itemPurchasingCategoryDescriptions = getItemPurchasingCategoryDescriptionsByItemPurchasingCategory(itemPurchasingCategory);
        List<ItemPurchasingCategoryDescriptionTransfer> itemPurchasingCategoryDescriptionTransfers = new ArrayList<>(itemPurchasingCategoryDescriptions.size());
        ItemPurchasingCategoryDescriptionTransferCache itemPurchasingCategoryDescriptionTransferCache = getVendorTransferCaches(userVisit).getItemPurchasingCategoryDescriptionTransferCache();
        
        itemPurchasingCategoryDescriptions.stream().forEach((itemPurchasingCategoryDescription) -> {
            itemPurchasingCategoryDescriptionTransfers.add(itemPurchasingCategoryDescriptionTransferCache.getItemPurchasingCategoryDescriptionTransfer(itemPurchasingCategoryDescription));
        });
        
        return itemPurchasingCategoryDescriptionTransfers;
    }
    
    public void updateItemPurchasingCategoryDescriptionFromValue(ItemPurchasingCategoryDescriptionValue itemPurchasingCategoryDescriptionValue, BasePK updatedBy) {
        if(itemPurchasingCategoryDescriptionValue.hasBeenModified()) {
            ItemPurchasingCategoryDescription itemPurchasingCategoryDescription = ItemPurchasingCategoryDescriptionFactory.getInstance().getEntityFromPK(EntityPermission.READ_WRITE, itemPurchasingCategoryDescriptionValue.getPrimaryKey());
            
            itemPurchasingCategoryDescription.setThruTime(session.START_TIME_LONG);
            itemPurchasingCategoryDescription.store();
            
            ItemPurchasingCategory itemPurchasingCategory = itemPurchasingCategoryDescription.getItemPurchasingCategory();
            Language language = itemPurchasingCategoryDescription.getLanguage();
            String description = itemPurchasingCategoryDescriptionValue.getDescription();
            
            itemPurchasingCategoryDescription = ItemPurchasingCategoryDescriptionFactory.getInstance().create(itemPurchasingCategory, language, description,
                    session.START_TIME_LONG, Session.MAX_TIME_LONG);
            
            sendEventUsingNames(itemPurchasingCategory.getPrimaryKey(), EventTypes.MODIFY.name(), itemPurchasingCategoryDescription.getPrimaryKey(), EventTypes.MODIFY.name(), updatedBy);
        }
    }
    
    public void deleteItemPurchasingCategoryDescription(ItemPurchasingCategoryDescription itemPurchasingCategoryDescription, BasePK deletedBy) {
        itemPurchasingCategoryDescription.setThruTime(session.START_TIME_LONG);
        
        sendEventUsingNames(itemPurchasingCategoryDescription.getItemPurchasingCategoryPK(), EventTypes.MODIFY.name(), itemPurchasingCategoryDescription.getPrimaryKey(), EventTypes.DELETE.name(), deletedBy);
        
    }
    
    public void deleteItemPurchasingCategoryDescriptionsByItemPurchasingCategory(ItemPurchasingCategory itemPurchasingCategory, BasePK deletedBy) {
        List<ItemPurchasingCategoryDescription> itemPurchasingCategoryDescriptions = getItemPurchasingCategoryDescriptionsByItemPurchasingCategoryForUpdate(itemPurchasingCategory);
        
        itemPurchasingCategoryDescriptions.stream().forEach((itemPurchasingCategoryDescription) -> {
            deleteItemPurchasingCategoryDescription(itemPurchasingCategoryDescription, deletedBy);
        });
    }
    
}
