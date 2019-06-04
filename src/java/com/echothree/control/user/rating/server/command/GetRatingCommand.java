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

package com.echothree.control.user.rating.server.command;

import com.echothree.control.user.rating.common.form.GetRatingForm;
import com.echothree.control.user.rating.common.result.GetRatingResult;
import com.echothree.control.user.rating.common.result.RatingResultFactory;
import com.echothree.model.control.core.common.EventTypes;
import com.echothree.model.control.rating.server.RatingControl;
import com.echothree.model.data.rating.server.entity.Rating;
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

public class GetRatingCommand
        extends BaseSimpleCommand<GetRatingForm> {
    
    private final static List<FieldDefinition> FORM_FIELD_DEFINITIONS;
    
    static {
        FORM_FIELD_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
                new FieldDefinition("RatingName", FieldType.ENTITY_NAME, true, null, null)
                ));
    }
    
    /** Creates a new instance of GetRatingCommand */
    public GetRatingCommand(UserVisitPK userVisitPK, GetRatingForm form) {
        super(userVisitPK, form, null, FORM_FIELD_DEFINITIONS, true);
    }
    
    @Override
    protected BaseResult execute() {
        var ratingControl = (RatingControl)Session.getModelController(RatingControl.class);
        GetRatingResult result = RatingResultFactory.getGetRatingResult();
        String ratingName = form.getRatingName();
        Rating rating = ratingControl.getRatingByName(ratingName);

        if(rating != null) {
            result.setRating(ratingControl.getRatingTransfer(getUserVisit(), rating));

            sendEventUsingNames(rating.getPrimaryKey(), EventTypes.READ.name(), null, null, getPartyPK());
        } else {
            addExecutionError(ExecutionErrors.UnknownRatingName.name(), ratingName);
        }

        return result;
    }
    
}
