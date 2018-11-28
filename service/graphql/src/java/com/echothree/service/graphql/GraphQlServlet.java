// --------------------------------------------------------------------------------
// Copyright 2002-2018 Echo Three, LLC
// Copyright 2016 Yurii Rashkovskii and Contributors
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

package com.echothree.service.graphql;

import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.service.graphql.internal.invoker.GraphQlQueryInvoker;
import com.echothree.service.graphql.internal.GraphQlRequest;
import com.echothree.service.graphql.internal.HttpRequestHandler;
import com.echothree.service.graphql.internal.invocation.GraphQlInvocationInputFactory;
import com.echothree.service.graphql.internal.invocation.GraphQlSingleInvocationInput;
import com.echothree.view.client.web.WebConstants;
import com.echothree.view.client.web.util.HttpSessionUtils;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;
import graphql.introspection.IntrospectionQuery;
import javax.naming.NamingException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.net.MediaType.JSON_UTF_8;

@WebServlet(name = "graphql", urlPatterns = {"/"}, asyncSupported = true)
public class GraphQlServlet
        extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(GraphQlServlet.class);

    protected static final MediaType JSON = JSON_UTF_8.withoutParameters();
    protected static final MediaType GRAPHQL_UTF_8 = MediaType.parse("application/graphql;charset=utf-8");
    protected static final MediaType GRAPHQL = GRAPHQL_UTF_8.withoutParameters();

    protected static final GraphQlRequest INTROSPECTION_REQUEST = new GraphQlRequest(IntrospectionQuery.INTROSPECTION_QUERY, null, null);

    protected GraphQlConfiguration configuration;
    protected HttpRequestHandler getHandler;
    protected HttpRequestHandler postHandler;

    protected GraphQlConfiguration getConfiguration() {
        return GraphQlConfiguration.with(GraphQlInvocationInputFactory.newBuilder().build())
                .build();
    }

    @Override
    public void init(ServletConfig servletConfig) {
        this.configuration = getConfiguration();

        this.getHandler = (request, response) -> {
            String path = request.getPathInfo();
            GraphQlInvocationInputFactory invocationInputFactory = configuration.getInvocationInputFactory();
            GraphQlQueryInvoker queryInvoker = configuration.getQueryInvoker();

            if (path == null) {
                path = request.getServletPath();
            }

            if (path.contentEquals("/schema.json")) {
                query(queryInvoker, invocationInputFactory.create(INTROSPECTION_REQUEST, request, response), request, response);
            } else {
                String query = request.getParameter("query");

                if (query != null) {
                    String variables = request.getParameter("variables");
                    String operationName = request.getParameter("operationName");

                    AddCorsResponseHeaders(request, response);
                    query(queryInvoker, invocationInputFactory.createReadOnly(new GraphQlRequest(query, variables, operationName), request, response), request, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    log.info("Bad GET request: path was not \"/schema.json\" or no query variable named \"query\" given");
                }
            }
        };

        this.postHandler = (request, response) -> {
            try {
                GraphQlInvocationInputFactory invocationInputFactory = configuration.getInvocationInputFactory();
                GraphQlQueryInvoker queryInvoker = configuration.getQueryInvoker();
                String contentType = request.getContentType();
                MediaType mediaType = MediaType.parse(contentType);

                if (GRAPHQL.equals(mediaType) || GRAPHQL_UTF_8.equals(mediaType)) {
                    String query = CharStreams.toString(request.getReader());

                    AddCorsResponseHeaders(request, response);
                    query(queryInvoker, invocationInputFactory.create(new GraphQlRequest(query, null, null)), request, response);
                } else if (JSON.equals(mediaType) || JSON_UTF_8.equals(mediaType)) {
                    String json = CharStreams.toString(request.getReader());

                    AddCorsResponseHeaders(request, response);
                    query(queryInvoker, invocationInputFactory.create(new GraphQlRequest(json)), request, response);
                }
            } catch (Exception e) {
                log.info("Bad POST request: parsing failed", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        };
    }

    private void AddCorsResponseHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");

        response.addHeader("Access-Control-Allow-Origin", origin == null ? "*" : origin);
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    private static InputStream asMarkableInputStream(InputStream inputStream) {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }

        return inputStream;
    }

    private void doRequestAsync(HttpServletRequest request, HttpServletResponse response, HttpRequestHandler handler) {
        AsyncContext asyncContext = request.startAsync();
        HttpServletRequest asyncRequest = (HttpServletRequest) asyncContext.getRequest();
        HttpServletResponse asyncResponse = (HttpServletResponse) asyncContext.getResponse();

        HttpSessionUtils.getInstance().setupUserVisit(request, response);

        new Thread(() -> doRequest(asyncRequest, asyncResponse, handler, asyncContext)).start();
    }

    private void doRequest(HttpServletRequest request, HttpServletResponse response, HttpRequestHandler handler, AsyncContext asyncContext) {
        try {
            handler.handle(request, response);
        } catch (Throwable t) {
            response.setStatus(500);
            log.error("Error executing GraphQL request!", t);
        } finally {
            asyncContext.complete();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doRequestAsync(request, response, getHandler);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doRequestAsync(request, response, postHandler);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String origin = request.getHeader("Origin");

        response.addHeader("Access-Control-Allow-Origin", origin == null ? "*" : origin);
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Origin");
        response.addHeader("Access-Control-Max-Age", "86400");
    }

    public UserVisitPK getUserVisitPK(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(true);

        return (UserVisitPK)httpSession.getAttribute(WebConstants.Session_USER_VISIT);
    }

    private void query(GraphQlQueryInvoker queryInvoker, GraphQlSingleInvocationInput invocationInput,
            HttpServletRequest request, HttpServletResponse resp)
            throws IOException, NamingException {
        String result = queryInvoker.query(getUserVisitPK(request), invocationInput);

        resp.setContentType(JSON_UTF_8.toString());
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().write(result.getBytes(Charsets.UTF_8));
    }
}
