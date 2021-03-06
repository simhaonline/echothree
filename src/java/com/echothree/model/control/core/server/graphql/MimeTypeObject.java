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

import com.echothree.control.user.core.server.command.GetEntityAttributeTypeCommand;
import com.echothree.control.user.core.server.command.GetMimeTypeFileExtensionsCommand;
import com.echothree.control.user.core.server.command.GetMimeTypeUsagesCommand;
import com.echothree.model.control.core.server.CoreControl;
import com.echothree.model.control.graphql.server.graphql.BaseEntityInstanceObject;
import com.echothree.model.control.graphql.server.util.GraphQlContext;
import com.echothree.model.control.user.server.UserControl;
import com.echothree.model.data.core.server.entity.MimeType;
import com.echothree.model.data.core.server.entity.MimeTypeDetail;
import com.echothree.model.data.core.server.entity.MimeTypeFileExtension;
import com.echothree.model.data.core.server.entity.MimeTypeUsage;
import com.echothree.util.server.control.BaseMultipleEntitiesCommand;
import com.echothree.util.server.control.BaseSingleEntityCommand;
import com.echothree.util.server.persistence.Session;
import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.schema.DataFetchingEnvironment;
import java.util.ArrayList;
import java.util.List;

@GraphQLDescription("mime type object")
@GraphQLName("MimeType")
public class MimeTypeObject
        extends BaseEntityInstanceObject {
    
    private final MimeType mimeType; // Always Present
    
    public MimeTypeObject(MimeType mimeType) {
        super(mimeType.getPrimaryKey());
        
        this.mimeType = mimeType;
    }

    private MimeTypeDetail mimeTypeDetail; // Optional, use getMimeTypeDetail()
    
    private MimeTypeDetail getMimeTypeDetail() {
        if(mimeTypeDetail == null) {
            mimeTypeDetail = mimeType.getLastDetail();
        }
        
        return mimeTypeDetail;
    }

    private Boolean hasEntityAttributeTypeAccess;

    private boolean getHasEntityAttributeTypeAccess(final DataFetchingEnvironment env) {
        if(hasEntityAttributeTypeAccess == null) {
            GraphQlContext context = env.getContext();
            BaseSingleEntityCommand baseSingleEntityCommand = new GetEntityAttributeTypeCommand(context.getUserVisitPK(), null);

            baseSingleEntityCommand.security();

            hasEntityAttributeTypeAccess = !baseSingleEntityCommand.hasSecurityMessages();
        }

        return hasEntityAttributeTypeAccess;
    }

    private Boolean hasMimeTypeUsagesAccess;

    private boolean getHasMimeTypeUsagesAccess(final DataFetchingEnvironment env) {
        if(hasMimeTypeUsagesAccess == null) {
            GraphQlContext context = env.getContext();
            BaseMultipleEntitiesCommand baseMultipleEntitiesCommand = new GetMimeTypeUsagesCommand(context.getUserVisitPK(), null);

            baseMultipleEntitiesCommand.security();

            hasMimeTypeUsagesAccess = !baseMultipleEntitiesCommand.hasSecurityMessages();
        }

        return hasMimeTypeUsagesAccess;
    }

    private Boolean hasMimeTypeFileExtensionsAccess;

    private boolean getHasMimeTypeFileExtensionsAccess(final DataFetchingEnvironment env) {
        if(hasMimeTypeFileExtensionsAccess == null) {
            GraphQlContext context = env.getContext();
            BaseMultipleEntitiesCommand baseMultipleEntitiesCommand = new GetMimeTypeFileExtensionsCommand(context.getUserVisitPK(), null);

            baseMultipleEntitiesCommand.security();

            hasMimeTypeFileExtensionsAccess = !baseMultipleEntitiesCommand.hasSecurityMessages();
        }

        return hasMimeTypeFileExtensionsAccess;
    }

    @GraphQLField
    @GraphQLDescription("mime type name")
    @GraphQLNonNull
    public String getMimeTypeName() {
        return getMimeTypeDetail().getMimeTypeName();
    }

    @GraphQLField
    @GraphQLDescription("entity attribute type")
    @GraphQLNonNull
    public EntityAttributeTypeObject getEntityAttributeType(final DataFetchingEnvironment env) {
        return getHasEntityAttributeTypeAccess(env) ? new EntityAttributeTypeObject(getMimeTypeDetail().getEntityAttributeType()) : null;
    }

    @GraphQLField
    @GraphQLDescription("is default")
    @GraphQLNonNull
    public boolean getIsDefault() {
        return getMimeTypeDetail().getIsDefault();
    }
    
    @GraphQLField
    @GraphQLDescription("sort order")
    @GraphQLNonNull
    public int getSortOrder() {
        return getMimeTypeDetail().getSortOrder();
    }
    
    @GraphQLField
    @GraphQLDescription("description")
    @GraphQLNonNull
    public String getDescription(final DataFetchingEnvironment env) {
        var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
        var userControl = (UserControl)Session.getModelController(UserControl.class);
        GraphQlContext context = env.getContext();
        
        return coreControl.getBestMimeTypeDescription(mimeType, userControl.getPreferredLanguageFromUserVisit(context.getUserVisit()));
    }
    
    @GraphQLField
    @GraphQLDescription("mime type usages")
    @GraphQLNonNull
    public List<MimeTypeUsageObject> getMimeTypeUsages(final DataFetchingEnvironment env) {
        List<MimeTypeUsageObject> mimeTypeUsages = null;
        
        if(getHasMimeTypeUsagesAccess(env)) {
            var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
            List<MimeTypeUsage> entities = coreControl.getMimeTypeUsagesByMimeType(mimeType);
            List<MimeTypeUsageObject> objects = new ArrayList<>(entities.size());

            entities.forEach((entity) -> {
                objects.add(new MimeTypeUsageObject(entity));
            });
            
            mimeTypeUsages = objects;
        }

        return mimeTypeUsages;
    }
    
    @GraphQLField
    @GraphQLDescription("mime type file extensions")
    @GraphQLNonNull
    public List<MimeTypeFileExtensionObject> getMimeTypeFileExtensions(final DataFetchingEnvironment env) {
        List<MimeTypeFileExtensionObject> mimeTypeFileExtensions = null;
        
        if(getHasMimeTypeFileExtensionsAccess(env)) {
            var coreControl = (CoreControl)Session.getModelController(CoreControl.class);
            List<MimeTypeFileExtension> entities = coreControl.getMimeTypeFileExtensionsByMimeType(mimeType);
            List<MimeTypeFileExtensionObject> objects = new ArrayList<>(entities.size());

            entities.forEach((entity) -> {
                objects.add(new MimeTypeFileExtensionObject(entity));
            });
            
            mimeTypeFileExtensions = objects;
        }

        return mimeTypeFileExtensions;
    }
    
}
