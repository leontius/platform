<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../commons/taglibs.jsp" %>
<html>
    <head>
        <%@ include file="../commons/meta.table.jsp" %>
        <script type="text/javascript" src="${ctx}/static/js/app.user.js"></script>
        <script type="text/javascript">
            $(function() {
                $('div.dt').table({
                    url : '${ctx}/user/list/json',
                    model : User.getColModel()
                });
            })
        </script>
    </head>
    <body>
        <t:nav desc="User Management" title="User Management">
            <t:nav-item active="true" title="User Management"/>
        </t:nav>

        <div class="box">
            <div class="box-header with-border">
                <h3 class="box-title"> 用户列表</h3>
            </div>
            <div class="box-body">
                <div class="dt"></div>
            </div>
        </div>
    </body>
</html>
