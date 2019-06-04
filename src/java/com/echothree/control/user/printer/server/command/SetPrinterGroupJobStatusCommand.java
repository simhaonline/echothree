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

package com.echothree.control.user.printer.server.command;

import com.echothree.control.user.printer.common.form.SetPrinterGroupJobStatusForm;
import com.echothree.model.control.printer.server.PrinterControl;
import com.echothree.model.data.printer.server.entity.PrinterGroupJob;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.message.ExecutionErrors;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetPrinterGroupJobStatusCommand
        extends BaseSimpleCommand<SetPrinterGroupJobStatusForm> {
    
   private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("PrinterGroupJobName", FieldType.ENTITY_NAME, true, null, null),
                new FieldDefinition("PrinterGroupJobStatusChoice", FieldType.ENTITY_NAME, true, null, null)
                ));
    }

    /** Creates a new instance of SetPrinterGroupJobStatusCommand */
    public SetPrinterGroupJobStatusCommand(UserVisitPK userVisitPK, SetPrinterGroupJobStatusForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
   @Override
    protected BaseResult execute() {
        var printerControl = (PrinterControl)Session.getModelController(PrinterControl.class);
        String printerGroupJobName = form.getPrinterGroupJobName();
        PrinterGroupJob printerGroupJob = printerControl.getPrinterGroupJobByName(printerGroupJobName);
        
        if(printerGroupJob != null) {
            String printerGroupJobStatusChoice = form.getPrinterGroupJobStatusChoice();
            
            printerControl.setPrinterGroupJobStatus(this, printerGroupJob, printerGroupJobStatusChoice, getPartyPK());
        } else {
            addExecutionError(ExecutionErrors.UnknownPrinterGroupJobName.name(), printerGroupJobName);
        }
        
        return null;
    }
    
}
