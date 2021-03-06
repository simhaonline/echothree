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
        <title>Review (<c:out value="${location.locationName}" />)</title>
        <html:base/>
        <%@ include file="../../include/environment.jsp" %>
    </head>
    <body>
        <div id="Header">
            <h2>
                <a href="<c:url value="/action/Portal" />">Home</a> &gt;&gt;
                <a href="<c:url value="/action/Warehouse/Main" />">Warehouses</a> &gt;&gt;
                <a href="<c:url value="/action/Warehouse/Warehouse/Main" />">Warehouses</a> &gt;&gt;
                <c:url var="locationsUrl" value="/action/Warehouse/Location/Main">
                    <c:param name="WarehouseName" value="${location.warehouse.warehouseName}" />
                </c:url>
                <a href="${locationsUrl}">Locations</a> &gt;&gt;
                Review (<c:out value="${location.locationName}" />)
            </h2>
        </div>
        <div id="Content">
            <p><font size="+2"><b><c:out value="${location.description}" /></b></font></p>
            <br />
            Warehouse: <c:out value="${location.warehouse.partyGroup.name}" /><br />
            Location Name: ${location.locationName}<br />
            Location Type: <c:out value="${location.locationType.description}" /><br />
            Location Use Type: <c:out value="${location.locationUseType.description}" /><br />
            Velocity: ${location.velocity}<br />
            Inventory Location Group: <c:out value="${location.inventoryLocationGroup.description}" /><br />
            Status: <c:out value="${location.locationStatus.workflowStep.description}" />
            <c:url var="editUrl" value="/action/Warehouse/Location/LocationStatus">
                <c:param name="WarehouseName" value="${location.warehouse.warehouseName}" />
                <c:param name="LocationName" value="${location.locationName}" />
            </c:url>
            <a href="${editUrl}">Edit</a>
            <br />
            <br />
            <br />
            Location Volume:
            <c:choose>
                <c:when test="${locationVolume == null}">
                    <i>Not Set.</i>
                </c:when>
                <c:otherwise>
                    Height: <c:out value="${locationVolume.height}" />,
                    Width: <c:out value="${locationVolume.width}" />,
                    Depth: <c:out value="${locationVolume.depth}" />
                </c:otherwise>
            </c:choose>
            <br />
            <br />
            <br />
            <h2>Location Capacities</h2>
            <c:url var="addUrl" value="/action/Warehouse/Location/LocationCapacityAdd">
                <c:param name="WarehouseName" value="${location.warehouse.warehouseName}" />
                <c:param name="LocationName" value="${location.locationName}" />
            </c:url>
            <a href="${addUrl}">Add Location Capacity.</a>
            <c:choose>
                <c:when test="${locationCapacities != null}">
                    <display:table name="locationCapacities" id="locationCapacity" class="displaytag">
                        <display:column titleKey="columnTitle.kind">
                            <c:out value="${locationCapacity.unitOfMeasureType.unitOfMeasureKind.description}" />
                        </display:column>
                        <display:column titleKey="columnTitle.type">
                            <c:out value="${locationCapacity.unitOfMeasureType.description}" />
                        </display:column>
                        <display:column titleKey="columnTitle.capacity" class="quantity">
                            <c:out value="${locationCapacity.capacity}" />
                        </display:column>
                        <display:column>
                            <c:url var="editUrl" value="/action/Warehouse/Location/LocationCapacityEdit">
                                <c:param name="WarehouseName" value="${locationCapacity.location.warehouse.warehouseName}" />
                                <c:param name="LocationName" value="${locationCapacity.location.locationName}" />
                                <c:param name="UnitOfMeasureKindName" value="${locationCapacity.unitOfMeasureType.unitOfMeasureKind.unitOfMeasureKindName}" />
                                <c:param name="UnitOfMeasureTypeName" value="${locationCapacity.unitOfMeasureType.unitOfMeasureTypeName}" />
                            </c:url>
                            <a href="${editUrl}">Edit</a>
                            <c:url var="deleteUrl" value="/action/Warehouse/Location/LocationCapacityDelete">
                                <c:param name="WarehouseName" value="${locationCapacity.location.warehouse.warehouseName}" />
                                <c:param name="LocationName" value="${locationCapacity.location.locationName}" />
                                <c:param name="UnitOfMeasureKindName" value="${locationCapacity.unitOfMeasureType.unitOfMeasureKind.unitOfMeasureKindName}" />
                                <c:param name="UnitOfMeasureTypeName" value="${locationCapacity.unitOfMeasureType.unitOfMeasureTypeName}" />
                            </c:url>
                            <a href="${deleteUrl}">Delete</a>
                        </display:column>
                    </display:table>
                    <br />
                </c:when>
                <c:otherwise>
                    <br />
                    <br />
                    <br />
                </c:otherwise>
            </c:choose>
            <h2>Location Name Elements</h2>
            <display:table name="locationNameElements" id="locationNameElement" class="displaytag">
                <display:column titleKey="columnTitle.locationNameElement">
                    <c:out value="${locationNameElement.description}" />
                </display:column>
                <display:column titleKey="columnTitle.value">
                    <c:out value="${fn:substring(location.locationName, locationNameElement.offset, locationNameElement.offset + locationNameElement.length)}" />
                </display:column>
            </display:table>
            <br />
            Created: <c:out value="${location.entityInstance.entityTime.createdTime}" /><br />
            <c:if test='${location.entityInstance.entityTime.modifiedTime != null}'>
                Modified: <c:out value="${location.entityInstance.entityTime.modifiedTime}" /><br />
            </c:if>
            <c:if test='${location.entityInstance.entityTime.deletedTime != null}'>
                Deleted: <c:out value="${location.entityInstance.entityTime.deletedTime}" /><br />
            </c:if>
            <et:checkSecurityRoles securityRoles="Event.List" />
            <et:hasSecurityRole securityRole="Event.List">
                <c:url var="eventsUrl" value="/action/Core/Event/Main">
                    <c:param name="EntityRef" value="${location.entityInstance.entityRef}" />
                </c:url>
                <a href="${eventsUrl}">Events</a>
            </et:hasSecurityRole>
        </div>
        <jsp:include page="../../include/userSession.jsp" />
        <jsp:include page="../../include/copyright.jsp" />
    </body>
</html:html>
