<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ include file="../views/commons/taglibs.jsp" %>
<%@ attribute name="link" required="false" type="java.lang.String" %>
<%@ attribute name="title" required="false" type="java.lang.String" %>
<%@ attribute name="active" required="false" type="java.lang.Boolean" %>
<c:choose>
    <c:when test="${not empty active and active eq true}">
        <li class="active">${title}</li>
    </c:when>
    <c:otherwise>
        <li><a href="${link}">${title}</a></li>
    </c:otherwise>
</c:choose>
