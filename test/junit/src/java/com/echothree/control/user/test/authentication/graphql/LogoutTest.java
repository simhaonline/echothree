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

package com.echothree.control.user.test.authentication.graphql;

import com.echothree.control.user.test.common.graphql.GraphQlTestCase;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class LogoutTest
        extends GraphQlTestCase {

    @Test
    public void singleLogoutTest()
            throws Exception {
        Map<String, Object> body = executeUsingPost("mutation { logout(input: { clientMutationId: \"1\" }) { hasErrors } }");
        Assert.assertFalse(getBoolean(body, "data.logout.hasErrors"));
    }
    
    @Test
    public void multipleLogoutTest()
            throws Exception {
        Map<String, Object> body1 = executeUsingPost("mutation { logout(input: { clientMutationId: \"1\" }) { hasErrors } }");
        Assert.assertFalse(getBoolean(body1, "data.logout.hasErrors"));

        Map<String, Object> body2 = executeUsingPost("mutation { logout(input: { clientMutationId: \"1\" }) { hasErrors } }");
        Assert.assertFalse(getBoolean(body2, "data.logout.hasErrors"));
    }
    
}
