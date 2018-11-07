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

package com.echothree.model.control.accounting.server.transfer;

import com.echothree.model.control.accounting.common.AccountingProperties;
import com.echothree.model.control.accounting.common.transfer.CurrencyTransfer;
import com.echothree.model.control.accounting.common.transfer.SymbolPositionTransfer;
import com.echothree.model.control.accounting.server.AccountingControl;
import com.echothree.model.data.accounting.server.entity.Currency;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.common.form.TransferProperties;
import java.util.Set;

public class CurrencyTransferCache
        extends BaseAccountingTransferCache<Currency, CurrencyTransfer> {
    
    TransferProperties transferProperties;
    boolean filterCurrencyIsoName;
    boolean filterSymbol;
    boolean filterSymbolPosition;
    boolean filterSymbolOnListStart;
    boolean filterSymbolOnListMember;
    boolean filterSymbolOnSubtotal;
    boolean filterSymbolOnTotal;
    boolean filterGroupingSeparator;
    boolean filterGroupingSize;
    boolean filterFractionSeparator;
    boolean filterDefaultFractionDigits;
    boolean filterPriceUnitFractionDigits;
    boolean filterPriceLineFractionDigits;
    boolean filterCostUnitFractionDigits;
    boolean filterCostLineFractionDigits;
    boolean filterMinusSign;
    boolean filterisDefault;
    boolean filterSortOrder;
    boolean filterDescription;
    
    /** Creates a new instance of CurrencyTransferCache */
    public CurrencyTransferCache(UserVisit userVisit, AccountingControl accountingControl) {
        super(userVisit, accountingControl);

        transferProperties = session.getTransferProperties();
        if(transferProperties != null) {
            Set<String> properties = transferProperties.getProperties(CurrencyTransfer.class);
            
            if(properties != null) {
                filterCurrencyIsoName = !properties.contains(AccountingProperties.CURRENCY_ISO_NAME);
                filterSymbol = !properties.contains(AccountingProperties.SYMBOL);
                filterSymbolPosition = !properties.contains(AccountingProperties.SYMBOL_POSITION);
                filterSymbolOnListStart = !properties.contains(AccountingProperties.SYMBOL_ON_LIST_START);
                filterSymbolOnListMember = !properties.contains(AccountingProperties.SYMBOL_ON_LIST_MEMBER);
                filterSymbolOnSubtotal = !properties.contains(AccountingProperties.SYMBOL_ON_SUBTOTAL);
                filterSymbolOnTotal = !properties.contains(AccountingProperties.SYMBOL_ON_TOTAL);
                filterGroupingSeparator = !properties.contains(AccountingProperties.GROUPING_SEPARATOR);
                filterGroupingSize = !properties.contains(AccountingProperties.GROUPING_SIZE);
                filterFractionSeparator = !properties.contains(AccountingProperties.FRACTION_SEPARATOR);
                filterDefaultFractionDigits = !properties.contains(AccountingProperties.DEFAULT_FRACTION_DIGITS);
                filterPriceUnitFractionDigits = !properties.contains(AccountingProperties.PRICE_UNIT_FRACTION_DIGITS);
                filterPriceLineFractionDigits = !properties.contains(AccountingProperties.PRICE_LINE_FRACTION_DIGITS);
                filterCostUnitFractionDigits = !properties.contains(AccountingProperties.COST_UNIT_FRACTION_DIGITS);
                filterCostLineFractionDigits = !properties.contains(AccountingProperties.COST_LINE_FRACTION_DIGITS);
                filterMinusSign = !properties.contains(AccountingProperties.MINUS_SIGN);
                filterisDefault = !properties.contains(AccountingProperties.IS_DEFAULT);
                filterSortOrder = !properties.contains(AccountingProperties.SORT_ORDER);
                filterDescription = !properties.contains(AccountingProperties.DESCRIPTION);
            }
        }
    }
    
    @Override
    public CurrencyTransfer getTransfer(Currency currency) {
        CurrencyTransfer currencyTransfer = get(currency);
        
        if(currencyTransfer == null) {
            String currencyIsoName = filterCurrencyIsoName ? null : currency.getCurrencyIsoName();
            String symbol = filterSymbol ? null : currency.getSymbol();
            SymbolPositionTransfer symbolPosition = filterSymbolPosition ? null : accountingControl.getSymbolPositionTransfer(userVisit, currency.getSymbolPosition());
            Boolean symbolOnListStart = filterSymbolOnListStart ? null : currency.getSymbolOnListStart();
            Boolean symbolOnListMember = filterSymbolOnListMember ? null : currency.getSymbolOnListMember();
            Boolean symbolOnSubtotal = filterSymbolOnSubtotal ? null : currency.getSymbolOnSubtotal();
            Boolean symbolOnTotal = filterSymbolOnTotal ? null :  currency.getSymbolOnTotal();
            String groupingSeparator = filterGroupingSeparator ? null : currency.getGroupingSeparator();
            Integer groupingSize = filterGroupingSize ? null : currency.getGroupingSize();
            String fractionSeparator = filterFractionSeparator ? null : currency.getFractionSeparator();
            Integer defaultFractionDigits = filterDefaultFractionDigits ? null : currency.getDefaultFractionDigits();
            Integer priceUnitFractionDigits = filterPriceUnitFractionDigits ? null : currency.getPriceUnitFractionDigits();
            Integer priceLineFractionDigits = filterPriceLineFractionDigits ? null : currency.getPriceLineFractionDigits();
            Integer costUnitFractionDigits = filterCostUnitFractionDigits ? null : currency.getCostUnitFractionDigits();
            Integer costLineFractionDigits = filterCostLineFractionDigits ? null : currency.getCostLineFractionDigits();
            String minusSign = filterMinusSign ? null : currency.getMinusSign();
            Boolean isDefault = filterisDefault ? null : currency.getIsDefault();
            Integer sortOrder = filterSortOrder ? null : currency.getSortOrder();
            String description = filterDescription ? null : accountingControl.getBestCurrencyDescription(currency, getLanguage());
            
            currencyTransfer = new CurrencyTransfer(currencyIsoName, symbol, symbolPosition, symbolOnListStart, symbolOnListMember,
                    symbolOnSubtotal, symbolOnTotal, groupingSeparator, groupingSize, fractionSeparator, defaultFractionDigits,
                    priceUnitFractionDigits, priceLineFractionDigits, costUnitFractionDigits, costLineFractionDigits, minusSign,
                    isDefault, sortOrder, description);
            put(currency, currencyTransfer);
        }
        return currencyTransfer;
    }
    
}
