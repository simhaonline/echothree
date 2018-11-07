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

package com.echothree.control.user.printer.server.command;

import com.echothree.control.user.printer.common.form.GetPrinterGroupChoicesForm;
import com.echothree.control.user.printer.common.result.GetPrinterGroupChoicesResult;
import com.echothree.control.user.printer.common.result.PrinterResultFactory;
import com.echothree.model.control.printer.server.PrinterControl;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.validation.FieldDefinition;
import com.echothree.util.common.validation.FieldType;
import com.echothree.util.common.command.BaseResult;
import com.echothree.util.server.control.BaseSimpleCommand;
import com.echothree.util.server.persistence.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetPrinterGroupChoicesCommand
        extends BaseSimpleCommand<GetPrinterGroupChoicesForm> {
    
   private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;

    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("DefaultPrinterGroupChoice", FieldType.ENTITY_NAME, false, null, null),
                new FieldDefinition("AllowNullChoice", FieldType.BOOLEAN, true, null, null)
                ));
    }

    /** Creates a new instance of GetPrinterGroupChoicesCommand */
    public GetPrinterGroupChoicesCommand(UserVisitPK userVisitPK, GetPrinterGroupChoicesForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, false);
    }
    
   @Override
    protected BaseResult execute() {
        PrinterControl printerControl = (PrinterControl)Session.getModelController(PrinterControl.class);
        GetPrinterGroupChoicesResult result = PrinterResultFactory.getGetPrinterGroupChoicesResult();
        String defaultPrinterGroupChoice = form.getDefaultPrinterGroupChoice();
        boolean allowNullChoice = Boolean.parseBoolean(form.getAllowNullChoice());
        
        result.setPrinterGroupChoices(printerControl.getPrinterGroupChoices(defaultPrinterGroupChoice, getPreferredLanguage(),
                allowNullChoice));
        
        return result;
    }
    
}
