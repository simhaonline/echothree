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

package com.echothree.model.control.sequence.server.transfer;

import com.echothree.model.control.sequence.common.transfer.SequenceChecksumTypeTransfer;
import com.echothree.model.control.sequence.server.SequenceControl;
import com.echothree.model.data.sequence.server.entity.SequenceChecksumType;
import com.echothree.model.data.user.server.entity.UserVisit;

public class SequenceChecksumTypeTransferCache
        extends BaseSequenceTransferCache<SequenceChecksumType, SequenceChecksumTypeTransfer> {
    
    /** Creates a new instance of SequenceChecksumTypeTransferCache */
    public SequenceChecksumTypeTransferCache(UserVisit userVisit, SequenceControl sequenceControl) {
        super(userVisit, sequenceControl);
    }
    
    public SequenceChecksumTypeTransfer getSequenceChecksumTypeTransfer(SequenceChecksumType sequenceChecksumType) {
        SequenceChecksumTypeTransfer sequenceChecksumTypeTransfer = get(sequenceChecksumType);
        
        if(sequenceChecksumTypeTransfer == null) {
            String sequenceChecksumTypeName = sequenceChecksumType.getSequenceChecksumTypeName();
            Boolean isDefault = sequenceChecksumType.getIsDefault();
            Integer sortOrder = sequenceChecksumType.getSortOrder();
            String description = sequenceControl.getBestSequenceChecksumTypeDescription(sequenceChecksumType, getLanguage());
            
            sequenceChecksumTypeTransfer = new SequenceChecksumTypeTransfer(sequenceChecksumTypeName, isDefault, sortOrder, description);
            put(sequenceChecksumType, sequenceChecksumTypeTransfer);
        }
        return sequenceChecksumTypeTransfer;
    }
    
}
