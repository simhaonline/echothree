<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>

<!--                                                                                  -->
<!-- Copyright 2002-2020 Echo Three, LLC                                              -->
<!--                                                                                  -->
<!-- Licensed under the Apache License, Version 2.0 (the "License");                  -->
<!-- you may not use this file except in compliance with the License.                 -->
<!-- You may obtain a copy of the License at                                          -->
<!--                                                                                  -->
<!--     http://www.apache.org/licenses/LICENSE-2.0                                   -->
<!--                                                                                  -->
<!-- Unless required by applicable law or agreed to in writing, software              -->
<!-- distributed under the License is distributed on an "AS IS" BASIS,                -->
<!-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.         -->
<!-- See the License for the specific language governing permissions and              -->
<!-- limitations under the License.                                                   -->
<!--                                                                                  -->

<%@ include file="../../include/taglibs.jsp" %>

<html:html xhtml="true">
    <head>
        <title>Review (<c:out value="${salesOrderBatch.batchName}" />)</title>
        <html:base/>
        <%@ include file="../../include/environment.jsp" %>
    </head>
    <body>
        <div id="Header">
            <h2>
                <a href="<c:url value="/action/Portal" />">Home</a> &gt;&gt;
                <a href="<c:url value="/action/SalesOrder/Main" />">Sales Orders</a> &gt;&gt;
                Sales Order Batches &gt;&gt;
                <a href="<c:url value="/action/SalesOrder/SalesOrderBatch/Main" />">Search</a> &gt;&gt;
                <et:countSalesOrderBatchResults searchTypeName="SALES_ORDER_BATCH_MAINTENANCE" countVar="salesOrderBatchResultsCount" commandResultVar="countSalesOrderBatchResultsCommandResult" logErrors="false" />
                <c:if test="${salesOrderBatchResultsCount > 0}">
                    <a href="<c:url value="/action/SalesOrder/SalesOrderBatch/Result" />"><fmt:message key="navigation.results" /></a> &gt;&gt;
                </c:if>
                Review (<c:out value="${salesOrderBatch.batchName}" />)
            </h2>
        </div>
        <div id="Content">
            <p><font size="+2"><b>Sales Order Batch <c:out value="${salesOrderBatch.batchName}" /></b></font></p>
            <br />
            <br />
            <br />
        </div>
        <jsp:include page="../../include/userSession.jsp" />
        <jsp:include page="../../include/copyright.jsp" />
    </body>
</html:html>
