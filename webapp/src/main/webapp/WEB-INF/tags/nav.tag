<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ include file="../views/commons/taglibs.jsp" %>
<%@ attribute name="title" required="false" type="java.lang.String" %>
<%@ attribute name="desc" required="false" type="java.lang.String" %>
<div class="page-header">
    <h1>
        ${title}
        <small>${desc}</small>
    </h1>
</div>
<div class="page-breadcrumb">
    <ol class="breadcrumb">
        <li><a href="${ctx}/home"><i class="glyphicon glyphicon-home"></i></a></li>
        <jsp:doBody/>
    </ol>
</div>