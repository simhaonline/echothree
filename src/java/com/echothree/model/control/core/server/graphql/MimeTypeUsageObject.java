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

package com.echothree.model.control.core.server.graphql;

import com.echothree.control.user.core.server.command.GetMimeTypeCommand;
import com.echothree.control.user.core.server.command.GetMimeTypeUsageTypeCommand;
import com.echothree.model.control.graphql.server.util.GraphQlContext;
import com.echothree.model.data.core.server.entity.MimeTypeUsage;
import com.echothree.util.server.control.BaseSingleEntityCommand;
import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.schema.DataFetchingEnvironment;

@GraphQLDescription("mime type usage object")
@GraphQLName("MimeTypeUsage")
public class MimeTypeUsageObject {
    
    private final MimeTypeUsage mimeTypeUsage; // Always Present
    
    public MimeTypeUsageObject(MimeTypeUsage mimeTypeUsage) {
        this.mimeTypeUsage = mimeTypeUsage;
    }

    private Boolean hasMimeTypeAccess;

    private boolean getHasMimeTypeAccess(final DataFetchingEnvironment env) {
        if(hasMimeTypeAccess == null) {
            GraphQlContext context = env.getContext();
            BaseSingleEntityCommand baseSingleEntityCommand = new GetMimeTypeCommand(context.getUserVisitPK(), null);

            baseSingleEntityCommand.security();

            hasMimeTypeAccess = !baseSingleEntityCommand.hasSecurityMessages();
        }

        return hasMimeTypeAccess;
    }

    private Boolean hasMimeTypeUsageTypeAccess;

    private boolean getHasMimeTypeUsageTypeAccess(final DataFetchingEnvironment env) {
        if(hasMimeTypeUsageTypeAccess == null) {
            GraphQlContext context = env.getContext();
            BaseSingleEntityCommand baseSingleEntityCommand = new GetMimeTypeUsageTypeCommand(context.getUserVisitPK(), null);

            baseSingleEntityCommand.security();

            hasMimeTypeUsageTypeAccess = !baseSingleEntityCommand.hasSecurityMessages();
        }

        return hasMimeTypeUsageTypeAccess;
    }

    @GraphQLField
    @GraphQLDescription("mime type")
    @GraphQLNonNull
    public MimeTypeObject getMimeType(final DataFetchingEnvironment env) {
        return getHasMimeTypeAccess(env) ? new MimeTypeObject(mimeTypeUsage.getMimeType()) : null;
    }
    
    @GraphQLField
    @GraphQLDescription("mime type usage type")
    @GraphQLNonNull
    public MimeTypeUsageTypeObject getMimeTypeUsageType(final DataFetchingEnvironment env) {
        return getHasMimeTypeUsageTypeAccess(env) ? new MimeTypeUsageTypeObject(mimeTypeUsage.getMimeTypeUsageType()) : null;
    }
    
}
