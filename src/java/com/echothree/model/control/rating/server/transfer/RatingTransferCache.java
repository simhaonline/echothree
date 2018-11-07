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

package com.echothree.model.control.rating.server.transfer;

import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.rating.common.transfer.RatingTransfer;
import com.echothree.model.control.rating.server.RatingControl;
import com.echothree.model.data.rating.server.entity.Rating;
import com.echothree.model.data.rating.server.entity.RatingDetail;
import com.echothree.model.data.user.server.entity.UserVisit;
import com.echothree.util.server.persistence.Session;

public class RatingTransferCache
        extends BaseRatingTransferCache<Rating, RatingTransfer> {
    
    CoreControl coreControl;
    
    /** Creates a new instance of RatingTransferCache */
    public RatingTransferCache(UserVisit userVisit, RatingControl ratingControl) {
        super(userVisit, ratingControl);
        
        coreControl = (CoreControl)Session.getModelController(CoreControl.class);
    }
    
    public RatingTransfer getRatingTransfer(Rating rating) {
        RatingTransfer ratingTransfer = get(rating);
        
        if(ratingTransfer == null) {
            RatingDetail ratingDetail = rating.getLastDetail();
            
            ratingTransfer = new RatingTransfer();
            put(rating, ratingTransfer);
            
            ratingTransfer.setRatingName(ratingDetail.getRatingName());
            ratingTransfer.setRatingTypeListItem(ratingControl.getRatingTypeListItemTransfer(userVisit, ratingDetail.getRatingTypeListItem()));
            ratingTransfer.setRatedEntityInstance(coreControl.getEntityInstanceTransfer(userVisit, ratingDetail.getRatedEntityInstance(), false, false, false, false, false));
            ratingTransfer.setRatedByEntityInstance(coreControl.getEntityInstanceTransfer(userVisit, ratingDetail.getRatedByEntityInstance(), false, false, false, false, false));
            ratingTransfer.setEntityInstance(coreControl.getEntityInstanceTransfer(userVisit, coreControl.getEntityInstanceByBasePK(rating.getPrimaryKey()), false, false, false, false, false));
            
        }
        
        return ratingTransfer;
    }
    
}
