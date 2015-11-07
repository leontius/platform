<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../views/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <sitemesh:write property='title'/>

        <%@ include file="../views/commons/meta.jsp" %>

        <sitemesh:write property='head'/>
    </head>
    <body>
        <sitemesh:write property='body'/>
    </body>
</html>