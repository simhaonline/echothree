<!DOCTYPE html>
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

<%@ include file="../include/taglibs.jsp" %>

<html>
    <head>
        <title><fmt:message key="pageTitle.contactLists" /></title>
        <%@ include file="../include/environment-b.jsp" %>
    </head>
    <%@ include file="../include/body-start-b.jsp" %>
        <et:checkSecurityRoles securityRoles="ContactListType.List:ContactListGroup.List:ContactListFrequency.List:ContactList.List" />
        <%@ include file="../include/breadcrumb/breadcrumbs-start.jsp" %>
            <jsp:include page="../include/breadcrumb/portal.jsp">
                <jsp:param name="showAsLink" value="true"/>
            </jsp:include>
            <jsp:include page="navigation.jsp">
                <jsp:param name="showAsLink" value="false"/>
            </jsp:include>
        <%@ include file="../include/breadcrumb/breadcrumbs-end.jsp" %>
        <et:hasSecurityRole securityRole="ContactListType.List">
            <a href="<c:url value="/action/ContactList/ContactListType/Main" />">Contact List Types</a><br />
        </et:hasSecurityRole>
        <et:hasSecurityRole securityRole="ContactListGroup.List">
            <a href="<c:url value="/action/ContactList/ContactListGroup/Main" />">Contact List Groups</a><br />
        </et:hasSecurityRole>
        <et:hasSecurityRole securityRole="ContactListFrequency.List">
            <a href="<c:url value="/action/ContactList/ContactListFrequency/Main" />">Contact List Frequencies</a><br />
        </et:hasSecurityRole>
        <et:hasSecurityRole securityRole="ContactList.List">
            <a href="<c:url value="/action/ContactList/ContactList/Main" />">Contact Lists</a><br />
        </et:hasSecurityRole>
    <%@ include file="../include/body-end-b.jsp" %>
</html>
