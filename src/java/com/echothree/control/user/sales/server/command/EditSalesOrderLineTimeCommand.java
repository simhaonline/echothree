// --------------------------------------------------------------------------------
// Copyright 2002-2019 Echo Three, LLC
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

package com.echothree.control.user.sales.server.command;

import com.echothree.control.user.sales.common.edit.SalesEditFactory;
import com.echothree.control.user.sales.common.edit.SalesOrderLineTimeEdit;
import com.echothree.control.user.sales.common.form.EditSalesOrderLineTimeForm;
import com.echothree.control.user.sales.common.result.EditSalesOrderLineTimeResult;
import com.echothree.control.user.sales.common.result.SalesResultFactory;
import com.echothree.control.user.sales.common.spec.SalesOrderLineTimeSpec;
import com.echothree.model.control.order.server.OrderControl;
import com.echothree.model.control.sales.server.logic.SalesOrderLogic;
import com.echothree.model.data.order.server.entity.Order;
import com.echothree.model.data.order.server.entity.OrderLine;
import com.echothree.model.data.order.server.entity.OrderLineTime;
import com.echothree.model.data.order.server.entity.OrderTimeType;
import com.echothree.model.data.order.server.entity.OrderType;
import com.echothree.model.data.order.server.value.OrderLineTimeValue;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.server.control.BaseAbstractEditCommand;
import com.echothree.util.server.persistence.Session;
import com.echothree.util.server.string.DateUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditSalesOrderLineTimeCommand
        extends BaseAbstractEditCommand<SalesOrderLineTimeSpec, SalesOrderLineTimeEdit, EditSalesOrderLineTimeResult, OrderLineTime, Order> {

    private final static List<FieldDefinition> SPEC_FIELD_DEFINITIONS;
    private final static List<FieldDefinition> EDIT_FIELD_DEFINITIONS;

    static {
        SPEC_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("OrderName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("OrderLineSequence", FieldType.UNSIGNED_INTEGER, true, null, null),
                new FieldDefinition("OrderTimeTypeName", FieldType.ENTITY_NAME, true, null, null)
                ));

        EDIT_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("Time", FieldType.DATE_TIME, true, null, null)
                ));
    }

    /** Creates a new instance of EditSalesOrderLineTimeCommand */
    public EditSalesOrderLineTimeCommand(UserVisitPK userVisitPK, EditSalesOrderLineTimeForm form) {
        super(userVisitPK, form, null, SPEC_FIELD_DEFINITIONS, EDIT_FIELD_DEFINITIONS);
    }

    @Override
    public EditSalesOrderLineTimeResult getResult() {
        return SalesResultFactory.getEditSalesOrderLineTimeResult();
    }

    @Override
    public SalesOrderLineTimeEdit getEdit() {
        return SalesEditFactory.getSalesOrderLineTimeEdit();
    }

    @Override
    public OrderLineTime getEntity(EditSalesOrderLineTimeResult result) {
        String orderName = spec.getOrderName();
        String orderLineSequence = spec.getOrderLineSequence();
        OrderLine orderLine = SalesOrderLogic.getInstance().getOrderLineByName(this, orderName, orderLineSequence);
        OrderLineTime orderLineTime = null;
        
        if(!hasExecutionErrors()) {
            var orderControl = (OrderControl)Session.getModelController(OrderControl.class);
            OrderType orderType = orderLine.getLastDetail().getOrder().getLastDetail().getOrderType();
            String orderTimeTypeName = spec.getOrderTimeTypeName();
            OrderTimeType orderTimeType = orderControl.getOrderTimeTypeByName(orderType, orderTimeTypeName);

            if(orderTimeType != null) {
                if(editMode.equals(EditMode.LOCK) || editMode.equals(EditMode.ABANDON)) {
                    orderLineTime = orderControl.getOrderLineTime(orderLine, orderTimeType);
                } else { // EditMode.UPDATE
                    orderLineTime = orderControl.getOrderLineTimeForUpdate(orderLine, orderTimeType);
                }

                if(orderLineTime == null) {
                    addExecutionError(ExecutionErrors.UnknownOrderLineTime.name(), orderType.getLastDetail().getOrderTypeName(), orderName, orderTimeTypeName);
                }
            }
        }
        
        return orderLineTime;
    }

    @Override
    public Order getLockEntity(OrderLineTime orderLineTime) {
        return orderLineTime.getOrderLine().getLastDetail().getOrder();
    }

    @Override
    public void fillInResult(EditSalesOrderLineTimeResult result, OrderLineTime orderLineTime) {
        var orderControl = (OrderControl)Session.getModelController(OrderControl.class);

        result.setOrderLineTime(orderControl.getOrderLineTimeTransfer(getUserVisit(), orderLineTime));
    }

    @Override
    public void doLock(SalesOrderLineTimeEdit edit, OrderLineTime orderLineTime) {
        edit.setTime(DateUtils.getInstance().formatTypicalDateTime(getUserVisit(), getPreferredDateTimeFormat(), orderLineTime.getTime()));
    }

    @Override
    public void doUpdate(OrderLineTime orderLineTime) {
        var orderControl = (OrderControl)Session.getModelController(OrderControl.class);
        OrderLineTimeValue orderLineTimeValue = orderControl.getOrderLineTimeValue(orderLineTime);
        Long time = Long.valueOf(edit.getTime());
        
        orderLineTimeValue.setTime(time);
        
        orderControl.updateOrderLineTimeFromValue(orderLineTimeValue, getPartyPK());
    }

}
