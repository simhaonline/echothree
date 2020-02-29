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
        <title><fmt:message key="pageTitle.leaveReasons" /></title>
        <html:base/>
        <%@ include file="../../include/environment.jsp" %>
    </head>
    <body>
        <div id="Header">
            <h2>
                <a href="<c:url value="/action/Portal" />"><fmt:message key="navigation.portal" /></a> &gt;&gt;
                <a href="<c:url value="/action/HumanResources/Main" />"><fmt:message key="navigation.humanResources" /></a> &gt;&gt;
                <fmt:message key="navigation.leaveReasons" />
            </h2>
        </div>
        <div id="Content">
            <et:checkSecurityRoles securityRoles="Event.List:LeaveReason.Create:LeaveReason.Edit:LeaveReason.Delete:LeaveReason.Review:LeaveReason.Description" />
            <et:hasSecurityRole securityRole="LeaveReason.Create">
                <p><a href="<c:url value="/action/HumanResources/LeaveReason/Add" />">Add Leave Reason.</a></p>
            </et:hasSecurityRole>
            <et:hasSecurityRole securityRole="LeaveReason.Review" var="includeReviewUrl" />
            <display:table name="leaveReasons" id="leaveReason" class="displaytag">
                <display:column titleKey="columnTitle.name">
                    <c:choose>
                        <c:when test="${includeReviewUrl}">
                            <c:url var="reviewUrl" value="/action/HumanResources/LeaveReason/Review">
                                <c:param name="LeaveReasonName" value="${leaveReason.leaveReasonName}" />
                            </c:url>
                            <a href="${reviewUrl}"><c:out value="${leaveReason.leaveReasonName}" /></a>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${leaveReason.leaveReasonName}" />
                        </c:otherwise>
                    </c:choose>
                </display:column>
                <display:column titleKey="columnTitle.description">
                    <c:out value="${leaveReason.description}" />
                </display:column>
                <display:column property="sortOrder" titleKey="columnTitle.sortOrder" />
                <display:column titleKey="columnTitle.default">
                    <c:choose>
                        <c:when test="${leaveReason.isDefault}">
                            Default
                        </c:when>
                        <c:otherwise>
                            <et:hasSecurityRole securityRole="LeaveReason.Edit">
                                <c:url var="setDefaultUrl" value="/action/HumanResources/LeaveReason/SetDefault">
                                    <c:param name="LeaveReasonName" value="${leaveReason.leaveReasonName}" />
                                </c:url>
                                <a href="${setDefaultUrl}">Set Default</a>
                            </et:hasSecurityRole>
                        </c:otherwise>
                    </c:choose>
                </display:column>
                <et:hasSecurityRole securityRoles="LeaveReason.Edit:LeaveReason.Description:LeaveReason.Delete">
                    <display:column>
                        <et:hasSecurityRole securityRole="LeaveReason.Edit">
                            <c:url var="editUrl" value="/action/HumanResources/LeaveReason/Edit">
                                <c:param name="OriginalLeaveReasonName" value="${leaveReason.leaveReasonName}" />
                            </c:url>
                            <a href="${editUrl}">Edit</a>
                        </et:hasSecurityRole>
                        <et:hasSecurityRole securityRole="LeaveReason.Description">
                            <c:url var="descriptionsUrl" value="/action/HumanResources/LeaveReason/Description">
                                <c:param name="LeaveReasonName" value="${leaveReason.leaveReasonName}" />
                            </c:url>
                            <a href="${descriptionsUrl}">Descriptions</a>
                        </et:hasSecurityRole>
                        <et:hasSecurityRole securityRole="LeaveReason.Delete">
                            <c:url var="deleteUrl" value="/action/HumanResources/LeaveReason/Delete">
                                <c:param name="LeaveReasonName" value="${leaveReason.leaveReasonName}" />
                            </c:url>
                            <a href="${deleteUrl}">Delete</a>
                        </et:hasSecurityRole>
                    </display:column>
                </et:hasSecurityRole>
                <et:hasSecurityRole securityRole="Event.List">
                    <display:column>
                        <c:url var="eventsUrl" value="/action/Core/Event/Main">
                            <c:param name="EntityRef" value="${leaveReason.entityInstance.entityRef}" />
                        </c:url>
                        <a href="${eventsUrl}">Events</a>
                    </display:column>
                </et:hasSecurityRole>
            </display:table>
        </div>
        <jsp:include page="../../include/userSession.jsp" />
        <jsp:include page="../../include/copyright.jsp" />
    </body>
</html:html>
