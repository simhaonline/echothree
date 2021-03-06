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

package com.echothree.model.control.vendor.server.transfer;

import com.echothree.model.control.accounting.common.transfer.CurrencyTransfer;
import com.echothree.model.control.accounting.common.transfer.GlAccountTransfer;
import com.echothree.model.control.accounting.server.AccountingControl;
import com.echothree.model.control.cancellationpolicy.common.transfer.CancellationPolicyTransfer;
import com.echothree.model.control.cancellationpolicy.server.CancellationPolicyControl;
import com.echothree.model.control.carrier.server.CarrierControl;
import com.echothree.model.control.comment.common.CommentConstants;
import com.echothree.model.control.communication.server.CommunicationControl;
import com.echothree.model.control.contact.server.ContactControl;
import com.echothree.model.control.contactlist.server.ContactListControl;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.document.server.DocumentControl;
import com.echothree.model.control.invoice.server.InvoiceControl;
import com.echothree.model.control.item.common.transfer.ItemAliasTypeTransfer;
import com.echothree.model.control.item.server.ItemControl;
import com.echothree.model.control.party.common.PartyOptions;
import com.echothree.model.control.party.common.transfer.DateTimeFormatTransfer;
import com.echothree.model.control.party.common.transfer.LanguageTransfer;
import com.echothree.model.control.party.common.transfer.PartyGroupTransfer;
import com.echothree.model.control.party.common.transfer.PartyTypeTransfer;
import com.echothree.model.control.party.common.transfer.PersonTransfer;
import com.echothree.model.control.party.common.transfer.TimeZoneTransfer;
import com.echothree.model.control.party.server.PartyControl;
import com.echothree.model.control.payment.server.control.BillingControl;
import com.echothree.model.control.printer.server.PrinterControl;
import com.echothree.model.control.returnpolicy.common.transfer.ReturnPolicyTransfer;
import com.echothree.model.control.returnpolicy.server.ReturnPolicyControl;
import com.echothree.model.control.scale.server.ScaleControl;
import com.echothree.model.control.subscription.server.SubscriptionControl;
import com.echothree.model.control.term.server.TermControl;
import com.echothree.model.control.user.server.UserControl;
import com.echothree.model.control.vendor.common.VendorOptions;
import com.echothree.model.control.vendor.common.transfer.VendorTransfer;
import com.echothree.model.control.vendor.common.transfer.VendorTypeTransfer;
import com.echothree.model.control.vendor.common.workflow.VendorStatusConstants;
import com.echothree.model.control.vendor.server.VendorControl;
import com.echothree.model.control.workflow.common.transfer.WorkflowEntityStatusTransfer;
import com.echothree.model.control.workflow.server.WorkflowControl;
import com.echothree.model.data.accounting.server.entity.Currency;
import com.echothree.model.data.accounting.server.entity.GlAccount;
import com.echothree.model.data.cancellationpolicy.server.entity.CancellationPolicy;
import com.echothree.model.data.communication.server.factory.CommunicationEventFactory;
import com.echothree.model.data.core.server.entity.EntityInstance;
import com.echothree.model.data.invoice.server.factory.InvoiceFactory;
import com.echothree.model.data.item.server.entity.ItemAliasType;
import com.echothree.model.data.party.server.entity.DateTimeFormat;
import com.echothree.model.data.party.server.entity.Language;
import com.echothree.model.data.party.server.entity.Party;
import com.echothree.model.data.party.server.entity.PartyDetail;
import com.echothree.model.data.party.server.entity.PartyGroup;
import com.echothree.model.data.party.server.entity.Person;
import com.echothree.model.data.party.server.entity.TimeZone;
import com.echothree.model.data.returnpolicy.server.entity.ReturnPolicy;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.model.data.vendor.server.entity.Vendor;
import com.echothree.model.data.vendor.server.factory.VendorItemFactory;
import com.echothree.util.common.transfer.ListWrapper;
import com.echothree.util.server.persistence.Session;
import com.echothree.util.server.string.AmountUtils;
import java.util.Set;

public class VendorTransferCache
        extends BaseVendorTransferCache<Party, VendorTransfer> {

    AccountingControl accountingControl = (AccountingControl)Session.getModelController(AccountingControl.class);
    BillingControl billingControl = (BillingControl)Session.getModelController(BillingControl.class);
    CancellationPolicyControl cancellationPolicyControl = (CancellationPolicyControl)Session.getModelController(CancellationPolicyControl.class);
    CarrierControl carrierControl = (CarrierControl)Session.getModelController(CarrierControl.class);
    CommunicationControl communicationControl = (CommunicationControl)Session.getModelController(CommunicationControl.class);
    ContactControl contactControl = (ContactControl)Session.getModelController(ContactControl.class);
    ContactListControl contactListControl = (ContactListControl)Session.getModelController(ContactListControl.class);
    CoreControl coreControl = (CoreControl)Session.getModelController(CoreControl.class);
    DocumentControl documentControl = (DocumentControl)Session.getModelController(DocumentControl.class);
    ItemControl itemControl = (ItemControl)Session.getModelController(ItemControl.class);
    InvoiceControl invoiceControl = (InvoiceControl)Session.getModelController(InvoiceControl.class);
    PartyControl partyControl = (PartyControl)Session.getModelController(PartyControl.class);
    PrinterControl printerControl = (PrinterControl)Session.getModelController(PrinterControl.class);
    ReturnPolicyControl returnPolicyControl = (ReturnPolicyControl)Session.getModelController(ReturnPolicyControl.class);
    ScaleControl scaleControl = (ScaleControl)Session.getModelController(ScaleControl.class);
    SubscriptionControl subscriptionControl = (SubscriptionControl)Session.getModelController(SubscriptionControl.class);
    TermControl termControl = (TermControl)Session.getModelController(TermControl.class);
    UserControl userControl = (UserControl)Session.getModelController(UserControl.class);
    WorkflowControl workflowControl = (WorkflowControl)Session.getModelController(WorkflowControl.class);
    boolean includeUserLogin;
    boolean includeRecoveryAnswer;
    boolean includePartyContactMechanisms;
    boolean includePartyContactLists;
    boolean includePartyDocuments;
    boolean includePartyPrinterGroupUses;
    boolean includePartyScaleUses;
    boolean includePartyCarriers;
    boolean includePartyCarrierAccounts;
    boolean includeVendorItems;
    boolean includeBillingAccounts;
    boolean includeInvoicesFrom;
    boolean includeInvoicesTo;
    boolean includePurchasingComments;
    boolean includePartyCreditLimits;
    boolean includePartyTerm;
    boolean includeSubscriptions;
    boolean includeCommunicationEvents;
    boolean hasVendorItemsLimits;
    boolean hasInvoiceLimits;
    boolean hasCommunicationEventLimits;

    /** Creates a new instance of VendorTransferCache */
    public VendorTransferCache(UserVisit userVisit, VendorControl vendorControl) {
        super(userVisit, vendorControl);

        Set<String> options = session.getOptions();
        if(options != null) {
            setIncludeKey(options.contains(PartyOptions.PartyIncludeKey) || options.contains(VendorOptions.VendorIncludeKey));
            setIncludeGuid(options.contains(PartyOptions.PartyIncludeGuid) || options.contains(VendorOptions.VendorIncludeGuid));
            includeUserLogin = options.contains(PartyOptions.PartyIncludeUserLogin);
            includeRecoveryAnswer = options.contains(PartyOptions.PartyIncludeRecoveryAnswer);
            includePartyContactMechanisms = options.contains(PartyOptions.PartyIncludePartyContactMechanisms);
            includePartyContactLists = options.contains(PartyOptions.PartyIncludePartyContactLists);
            includePartyDocuments = options.contains(PartyOptions.PartyIncludePartyDocuments);
            includePartyPrinterGroupUses = options.contains(PartyOptions.PartyIncludePartyPrinterGroupUses);
            includePartyScaleUses = options.contains(PartyOptions.PartyIncludePartyScaleUses);
            includePartyCarriers = options.contains(PartyOptions.PartyIncludePartyCarriers);
            includePartyCarrierAccounts = options.contains(PartyOptions.PartyIncludePartyCarrierAccounts);
            includeBillingAccounts = options.contains(VendorOptions.VendorIncludeBillingAccounts);
            includeVendorItems = options.contains(VendorOptions.VendorIncludeVendorItems);
            includeInvoicesFrom = options.contains(VendorOptions.VendorIncludeInvoicesFrom);
            includeInvoicesTo = options.contains(VendorOptions.VendorIncludeInvoicesTo);
            includePurchasingComments = options.contains(VendorOptions.VendorIncludePurchasingComments);
            includePartyCreditLimits = options.contains(VendorOptions.VendorIncludePartyCreditLimits);
            includePartyTerm = options.contains(VendorOptions.VendorIncludePartyTerm);
            includeSubscriptions = options.contains(VendorOptions.VendorIncludeSubscriptions);
            includeCommunicationEvents = options.contains(VendorOptions.VendorIncludeCommunicationEvents);
            setIncludeEntityAttributeGroups(options.contains(VendorOptions.VendorIncludeEntityAttributeGroups));
            setIncludeTagScopes(options.contains(VendorOptions.VendorIncludeTagScopes));
        }
        
        setIncludeEntityInstance(true);
        
        hasVendorItemsLimits = session.hasLimit(VendorItemFactory.class);
        hasInvoiceLimits = session.hasLimit(InvoiceFactory.class);
        hasCommunicationEventLimits = session.hasLimit(CommunicationEventFactory.class);
    }

    public VendorTransfer getVendorTransfer(Vendor vendor) {
        return getVendorTransfer(vendor.getParty());
    }

    public VendorTransfer getVendorTransfer(Party party) {
        VendorTransfer vendorTransfer = get(party);

        if(vendorTransfer == null) {
            PartyDetail partyDetail = party.getLastDetail();
            String partyName = partyDetail.getPartyName();
            PartyTypeTransfer partyTypeTransfer = partyControl.getPartyTypeTransfer(userVisit, partyDetail.getPartyType());
            Language preferredLanguage = partyDetail.getPreferredLanguage();
            LanguageTransfer preferredLanguageTransfer = preferredLanguage == null ? null : partyControl.getLanguageTransfer(userVisit, preferredLanguage);
            Currency preferredCurrency = partyDetail.getPreferredCurrency();
            CurrencyTransfer preferredCurrencyTransfer = preferredCurrency == null ? null : accountingControl.getCurrencyTransfer(userVisit, preferredCurrency);
            TimeZone preferredTimeZone = partyDetail.getPreferredTimeZone();
            TimeZoneTransfer preferredTimeZoneTransfer = preferredTimeZone == null ? null : partyControl.getTimeZoneTransfer(userVisit, preferredTimeZone);
            DateTimeFormat preferredDateTimeFormat = partyDetail.getPreferredDateTimeFormat();
            DateTimeFormatTransfer preferredDateTimeFormatTransfer = preferredDateTimeFormat == null ? null : partyControl.getDateTimeFormatTransfer(userVisit, preferredDateTimeFormat);
            Vendor vendor = vendorControl.getVendor(party);
            String vendorName = vendor.getVendorName();
            VendorTypeTransfer vendorType = vendorControl.getVendorTypeTransfer(userVisit, vendor.getVendorType());
            Integer minimumPurchaseOrderLines = vendor.getMinimumPurchaseOrderLines();
            Integer maximumPurchaseOrderLines = vendor.getMaximumPurchaseOrderLines();
            Currency currency = partyControl.getPreferredCurrency(party);
            Long unformattedMinimumPurchaseOrderAmount = vendor.getMinimumPurchaseOrderAmount();
            String minimumPurchaseOrderAmount = AmountUtils.getInstance().formatCostLine(currency, unformattedMinimumPurchaseOrderAmount);
            Long unformattedMaximumPurchaseOrderAmount = vendor.getMaximumPurchaseOrderAmount();
            String maximumPurchaseOrderAmount = AmountUtils.getInstance().formatCostLine(currency, unformattedMaximumPurchaseOrderAmount);
            Boolean useItemPurchasingCategories = vendor.getUseItemPurchasingCategories();
            ItemAliasType defaultItemAliasType = vendor.getDefaultItemAliasType();
            ItemAliasTypeTransfer defaultItemAliasTypeTransfer = defaultItemAliasType == null ? null : itemControl.getItemAliasTypeTransfer(userVisit, defaultItemAliasType);
            CancellationPolicy cancellationPolicy = vendor.getCancellationPolicy();
            CancellationPolicyTransfer cancellationPolicyTransfer = cancellationPolicy == null ? null : cancellationPolicyControl.getCancellationPolicyTransfer(userVisit, cancellationPolicy);
            ReturnPolicy returnPolicy = vendor.getReturnPolicy();
            ReturnPolicyTransfer returnPolicyTransfer = returnPolicy == null ? null : returnPolicyControl.getReturnPolicyTransfer(userVisit, returnPolicy);
            GlAccount apGlAccount = vendor.getApGlAccount();
            GlAccountTransfer apGlAccountTransfer = apGlAccount == null ? null : accountingControl.getGlAccountTransfer(userVisit, apGlAccount);
            Boolean holdUntilComplete = vendor.getHoldUntilComplete();
            Boolean allowBackorders = vendor.getAllowBackorders();
            Boolean allowSubstitutions = vendor.getAllowSubstitutions();
            Boolean allowCombiningShipments = vendor.getAllowCombiningShipments();
            Boolean requireReference = vendor.getRequireReference();
            Boolean allowReferenceDuplicates = vendor.getAllowReferenceDuplicates();
            String referenceValidationPattern = vendor.getReferenceValidationPattern();
            Person person = partyControl.getPerson(party);
            PersonTransfer personTransfer = person == null ? null : partyControl.getPersonTransfer(userVisit, person);
            PartyGroup partyGroup = partyControl.getPartyGroup(party);
            PartyGroupTransfer partyGroupTransfer = partyGroup == null ? null : partyControl.getPartyGroupTransfer(userVisit, partyGroup);

            EntityInstance entityInstance = coreControl.getEntityInstanceByBasePK(party.getPrimaryKey());
            WorkflowEntityStatusTransfer vendorStatusTransfer = workflowControl.getWorkflowEntityStatusTransferByEntityInstanceUsingNames(userVisit,
                    VendorStatusConstants.Workflow_VENDOR_STATUS, entityInstance);

            vendorTransfer = new VendorTransfer(partyName, partyTypeTransfer, preferredLanguageTransfer, preferredCurrencyTransfer, preferredTimeZoneTransfer,
                    preferredDateTimeFormatTransfer, personTransfer, partyGroupTransfer, vendorName, vendorType, minimumPurchaseOrderLines,
                    maximumPurchaseOrderLines, unformattedMinimumPurchaseOrderAmount, minimumPurchaseOrderAmount, unformattedMaximumPurchaseOrderAmount,
                    maximumPurchaseOrderAmount, useItemPurchasingCategories, defaultItemAliasTypeTransfer, cancellationPolicyTransfer, returnPolicyTransfer,
                    apGlAccountTransfer, holdUntilComplete, allowBackorders, allowSubstitutions, allowCombiningShipments, requireReference,
                    allowReferenceDuplicates, referenceValidationPattern, vendorStatusTransfer);
            put(party, vendorTransfer);

            if(includeUserLogin) {
                vendorTransfer.setUserLogin(userControl.getUserLoginTransfer(userVisit, party));
            }

            if(includeRecoveryAnswer) {
                vendorTransfer.setRecoveryAnswer(userControl.getRecoveryAnswerTransfer(userVisit, party));
            }

            if(includePartyContactMechanisms) {
                vendorTransfer.setPartyContactMechanisms(new ListWrapper<>(contactControl.getPartyContactMechanismTransfersByParty(userVisit, party)));
            }
            
            if(includePartyContactLists) {
                vendorTransfer.setPartyContactLists(new ListWrapper<>(contactListControl.getPartyContactListTransfersByParty(userVisit, party)));
            }

            if(includePartyDocuments) {
                vendorTransfer.setPartyDocuments(new ListWrapper<>(documentControl.getPartyDocumentTransfersByParty(userVisit, party)));
            }

            if(includePartyPrinterGroupUses) {
                vendorTransfer.setPartyPrinterGroupUses(new ListWrapper<>(printerControl.getPartyPrinterGroupUseTransfersByParty(userVisit, party)));
            }

            if(includePartyScaleUses) {
                vendorTransfer.setPartyScaleUses(new ListWrapper<>(scaleControl.getPartyScaleUseTransfersByParty(userVisit, party)));
            }

            if(includePartyCarriers) {
                vendorTransfer.setPartyCarriers(new ListWrapper<>(carrierControl.getPartyCarrierTransfersByParty(userVisit, party)));
            }

            if(includePartyCarrierAccounts) {
                vendorTransfer.setPartyCarrierAccounts(new ListWrapper<>(carrierControl.getPartyCarrierAccountTransfersByParty(userVisit, party)));
            }
            
            if(includeBillingAccounts) {
                vendorTransfer.setBillingAccounts(new ListWrapper<>(billingControl.getBillingAccountTransfersByBillFrom(userVisit, party)));
            }
            
            if(includeVendorItems) {
                vendorTransfer.setVendorItems(new ListWrapper<>(vendorControl.getVendorItemTransfersByVendorParty(userVisit, party)));
                
                if(hasVendorItemsLimits) {
                    vendorTransfer.setVendorItemsCount(vendorControl.countVendorItemsByVendorParty(party));
                }
            }
            
            if(includeInvoicesFrom) {
                vendorTransfer.setInvoicesFrom(new ListWrapper<>(invoiceControl.getInvoiceTransfersByInvoiceFrom(userVisit, party)));
                
                if(hasInvoiceLimits) {
                    vendorTransfer.setInvoicesFromCount(invoiceControl.countInvoicesByInvoiceFrom(party));
                }
            }
            
            if(includeInvoicesTo) {
                vendorTransfer.setInvoicesTo(new ListWrapper<>(invoiceControl.getInvoiceTransfersByInvoiceTo(userVisit, party)));
                
                if(hasInvoiceLimits) {
                    vendorTransfer.setInvoicesToCount(invoiceControl.countInvoicesByInvoiceTo(party));
                }
            }

            if(includePurchasingComments) {
                setupComments(null, entityInstance, vendorTransfer, CommentConstants.CommentType_VENDOR_PURCHASING);
            }

            if(includePartyCreditLimits) {
                vendorTransfer.setPartyCreditLimits(new ListWrapper<>(termControl.getPartyCreditLimitTransfersByParty(userVisit, party)));
            }

            if(includePartyTerm) {
                vendorTransfer.setPartyTerm(termControl.getPartyTermTransfer(userVisit, party));
            }

            if(includeSubscriptions) {
                vendorTransfer.setSubscriptions(new ListWrapper<>(subscriptionControl.getSubscriptionTransfersByParty(userVisit, party)));
            }

            if(includeCommunicationEvents) {
                vendorTransfer.setCommunicationEvents(new ListWrapper<>(communicationControl.getCommunicationEventTransfersByParty(userVisit, party)));

                if(hasCommunicationEventLimits) {
                    vendorTransfer.setCommunicationEventsCount(communicationControl.countCommunicationEventsByParty(party));
                }
            }
        }

        return vendorTransfer;
    }
}
