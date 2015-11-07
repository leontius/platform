(function($) {
    $.createTable = function(dt, p) {
        p = $.extend({
            width : '100%',
            url : false,
            model : false,          // 模型
            view : 'list',          // grid 网格显示 list 列表显示
            template : false,       // grid 模板
            pager : true,           // 是否显示分页
            header : true,          // 是否显示标题
            viewer : true,          // 是否显示风格切换
            page : 1,               // 当前页数
            limit : 5,              // 每页记录数
            total : 1,              // 总记录数
            totalPage : 1,          // 总页数
            preProcess : false,     // 预处理回调函数
            onSuccess : false,      // 完成后回调函数
            onError : false,        // 错误回调函数
            sortName : '',          // 排序列
            sortOrder : '',         // 排序方式
            params : []
        }, p);


        // 添加记录
        dt.addData = function(result) {
            if (!result) {
                dt.loadError();
                return false;
            }

            result = $.extend({
                rows : [],
                page : 1,
                limit : 0,
                total : 0
            }, result);

            // 预处理
            if (p.preProcess && typeof p.preProcess == 'function') {
                result = p.preProcess(result);
            }

            $('.datatable-pager-btn-reload', dt.pager).removeClass('datatable-pager-btn-reload-loading');

            dt.loading = false;

            // 当前页
            p.page = parseInt(p.page);
            // 计算总页数
            p.totalPage = (parseInt(result.total) + parseInt(result.limit) - 1) / parseInt(result.limit);
            // 当前页记录开始
            p.start = (parseInt(p.page) - 1) * parseInt(result.limit) + 1;
            // 当前页记录结束
            p.end = (parseInt(p.page) * parseInt(result.limit));
            // 记录数据
            p.data = result.rows;

            // 重建分页
            dt.buildPager(result.total > 0);

            var tbody = $("tbody:first", dt.table).get(0);
            if (!tbody) {
                tbody = document.createElement('tbody');
                $(dt.table).append(tbody);
            }
            $(tbody).empty();

            if (p.total == 0) {
                $('tr, a, td, div', dt).unbind();
                if (dt.stat) {
                    $(dt.stat).show();
                    $('span', dt.stat).html("No records found.").show();
                }
                return false;
            }

            if (p.view && p.view === 'grid') {
                $("thead:first", dt.table).hide();

                var tr = document.createElement('tr');
                var td = document.createElement('td');
                var grid = $('<div class="row"></div>');
                $.each(result.data, function(i, row) {
                    if (p.template && typeof p.template == 'function') {
                        $(grid).append('<div class="col-md-1">' + p.template(row) + "</div>");
                    }
                });
                $(td).append(grid);
                $(tr).append(td);
                $(tbody).append(tr);
            } else {
                $("thead:first", dt.table).show();

                $.each(result.rows, function(i, row) {
                    var tr = document.createElement('tr');

                    for (var k = 0; k < p.model.length; k++) {
                        var cm = p.model[k];

                        var td = document.createElement('td');
                        if (cm.align) {
                            td.align = cm.align;
                        }

                        if (cm.format !== undefined && typeof cm.format == 'function') {
                            td.innerHTML = cm.format(row);
                        } else {
                            if (cm.name != '') {
                                td.innerHTML = row[cm.name];
                            }
                        }
                        $(tr).append(td);
                        td = null;
                        cm = null;
                    }
                    $(tbody).append(tr);
                    tr = null;
                });
            }

            // 调用完成后的回调函数
            if (p.onSuccess) {
                p.onSuccess(this);
            }
        };

        dt.loadError = function() {
        };

        dt.loadData = function() {
            if (dt.loading) {
                return true;
            }

            dt.loading = true;
            if (!p.url) {
                return false;
            }

            var param = [
                {
                    name : 'page',
                    value : p.page
                },
                {
                    name : 'limit',
                    value : p.limit
                },
                {
                    name : 'sort',
                    value : p.sort
                },
                {
                    name : 'order',
                    value : p.order
                }
            ];

            if (p.params) {
                for (var pi = 0; pi < p.params.length; pi++) {
                    param[param.length] = p.params[pi];
                }
            }

            $.ajax({
                type : 'POST',
                dataType : 'json',
                url : p.url,
                data : param,
                success : function(data) {
                    dt.addData(data);
                },
                error : function(XMLHttpRequest, textStatus, errorThrown) {
                    try {
                        dt.loadError();
                        if (p.onError) {
                            p.onError(XMLHttpRequest, textStatus, errorThrown);
                        }
                    } catch (e) {
                    }
                }
            });
        };

        // 页面跳转
        dt.changePage = function(num) {
            if (this.loading) {
                return true;
            }
            num = isNaN(num) ? 1 : num;
            p.page = (num >= 1 && num <= p.totalPage) ? num : 1;
            dt.loadData();
        };

        // 切换显示视图
        dt.changeView = function(view) {
            if (this.loading) {
                return true;
            }
            p.view = view;
            dt.loadData();
        };

        // 更改排序
        dt.changeSort = function(th) {
        };

        // 生成分页信息
        dt.buildPager = function(notEmpty) {
            var html = '';
            if (notEmpty === true) {
                html += '<ul class="pagination">';

                var offset = 3;
                var start = (p.page - offset) > 2 ? (p.page - offset) : 1;
                var end = (p.page + offset) < p.totalPage ? (p.page + offset) : p.totalPage;

                if (start > 1) {
                    html += '<li page="1" class="datatable-pager-link"><a href="#">1</a></li>';
                    html += '<li class="datatable-pager-link"><a href="#">...</a></li>';
                }
                for (var i = start; i <= end; i++) {
                    html += '<li page="' + i + '" class="datatable-pager-link"><a href="#">' + i + '</a></li>';
                }
                if ((end + 1) < p.totalPage) {
                    html += '<li class="datatable-pager-link"><a href="#">...</a></li>';
                    html += '<li page="' + p.totalPage + '" class="datatable-pager-link"><a href="#">' + p.totalPage + '</a></li>';
                }
                html += '</ul>';

                if (p.model && p.model.length > 0 && p.viewer) {
                    html += '<ul class="datatable-tools pagination pull-rigth">';
                    html += '<li class="datatable-tools-reload"><a href="#"><i class="fa fa-refresh"></i></a></li>';
                    html += '<li class="datatable-tools-switch datatable-tools-list" view="list"><a href="#"><i class="fa fa-th-list"></i></a></li>';
                    html += '<li class="datatable-tools-switch datatable-tools-grid" view="grid"><a href="#"><i class="fa fa-th-large"></i></a></li>';
                    html += '</ul>';
                }
            }
            $(dt.pager).html(html);

            // 重新加载数据
            $('.datatable-tools-reload', dt.pager).click(function() {
                dt.loadData();
            });
            $('.datatable-pager-link', dt.pager).click(function() {
                if ($(this).attr('page')) {
                    dt.changePage($(this).attr('page'));
                }
            });
            $('.datatable-pager-switch', dt.pager).click(function() {
                dt.changeView($(this).attr('view'));
            });
        };

        $(dt).addClass('datatable');
        if (p.width != 'auto') {
            $(dt).width(p.width);
        }

        dt.body = document.createElement('div');
        $(dt.body).addClass('datatable-body');
        if (p.width != 'auto') {
            $(dt.body).width(p.width);
        }

        dt.table = document.createElement('table');
        dt.table.cellPadding = 0;
        dt.table.cellSpacing = 0;
        $(dt.table).addClass('table table-hover');

        if (p.model && p.header) {
            var thead = document.createElement('thead');

            var tr = document.createElement('tr');
            for (var i = 0; i < p.model.length; i++) {
                var cm = p.model[i];

                var th = document.createElement('th');
                th.innerHTML = cm.display;

                if (cm.name && cm.sortable) {
                    $(th).attr('abbr', cm.name);

                    $(th).click(function() {
                        dt.changeSort(this);
                    });

                    if ($(th).attr('abbr') == p.sortName) {
                        $(th).addClass('sort' + p.sortOrder);
                    }
                }

                if (cm.align) {
                    th.align = cm.align;
                }
                if (cm.width) {
                    $(th).attr('width', cm.width);
                }

                if (p.order == '') {
                    p.order = 'asc';
                }
                $(tr).append(th);
                cm = null;
                th = null;
            }
            $(thead).append(tr);
            $(dt.table).append(thead);
        }

        if (p.view == 'grid') { // 显示网格的时候，不显示列标题
            $('thead:first', dt.table).hide();
        }

        $(dt.body).append(dt.table);
        $(dt).append(dt.body);

        // 分页
        if (p.pager) {
            dt.pager = document.createElement('div');
            dt.stat = document.createElement('div');
            dt.footer = document.createElement('div');
            $(dt.stat).addClass('datatable-stat col-md-4').html('<span>Showing 1 to 10 of 57 entries</span>');
            $(dt.pager).addClass('datatable-pager col-md-8 text-right').html('');
            $(dt.footer).addClass('datatable-footer clearfix').append(dt.stat).append(dt.pager);

            $(dt).append((dt.footer));
        }

        dt.params = p;
        dt.datatable = dt;

        if (p.url) {
            dt.loadData();
        }
        return dt;
    };

    // 修改配置参数
    $.fn.setParams = function(p) {
        p = $.extend({
            page : 1
        }, p);

        return this.each(function() {
            if (this.datatable) {
                $.extend(this.params, p);
            }
        });
    };

    // 根据新的参数重新加载列表
    $.fn.reloadTable = function(params) {
        $(this).setParams(params);
        return this.each(function() {
            if (this.datatable && this.params.url) {
                this.datatable.loadData();
            }
        });
    };

    // 初始化列表
    $.fn.table = function(p) {
        $(this).each(function() {
            $.createTable(this, p);
        });
        return this;
    };
})(jQuery);
