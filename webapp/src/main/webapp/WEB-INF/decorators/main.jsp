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
        <%@ include file="../views/commons/header.jsp" %>

        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-2 sidebar">
                    <%@ include file="../views/commons/sidebar.jsp" %>
                </div>
                <div class="col-lg-10 col-lg-offset-2 main">
                    <sitemesh:write property='body'/>
                </div>
            </div>
        </div>
    </body>
</html>