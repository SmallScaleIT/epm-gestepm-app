<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/css/bootstrap-select.min.css">

<style>
    .right-panel .page-header button {
        margin-top: 0;
    }

     #filterForm {
         top: auto !important;
         left: auto !important;
         right: 15px;
         min-width: 20rem;
     }

</style>

<div class="row m-0">
    <div class="col-sm-12 col-md-9 p-0">
        <div class="breadcrumbs">
            <div class="breadcrumbs-inner">
                <div class="row m-0">
                    <div class="col">
                        <div class="page-header">
                            <div class="page-title">
                                <h1 class="text-uppercase"><spring:message code="shares.work.title"/></h1>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-sm-12 col-md-3 p-0">
        <div class="breadcrumbs">
            <div class="breadcrumbs-inner">
                <form id="createForm">
                    <div id="form-page-header" class="page-header d-md-flex align-items-center pl-3">
                        <div class="row w-100">
                            <div class="col-sm-12 col-md-9 pr-0">
                                <select id="projectDropdown" class="form-control input" name="projectId" required>
                                    <option></option>
                                    <c:forEach items="${projects}" var="project">
                                        <option value="${project.id}"><spring:message
                                                code="${project.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-sm-12 col-md-3 p-0 pl-1">
                                <button type="button" class="btn btn-default btn-sm w-100" onclick="create()">
                                    <spring:message code="init"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="clearfix"></div>

<div class="content">
    <div class="row h-100">
        <div class="col h-100">
            <div class="card">
                <div class="card-body">
                    <div class="title mb-0 d-flex justify-content-between align-items-center">
                        <div>
                            <spring:message code="shares.work.title" />
                        </div>
                        <div class="dropdown">
                            <button type="button" class="btn btn-outline-secondary btn-sm" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <spring:message code="filters" />
                            </button>

                            <form id="filterForm" class="dropdown-menu dropdown-menu-right filter-dropdown p-4">
                                <div class="form-group">
                                    <label class="w-100">
                                        <spring:message code="id" />
                                        <input type="text" name="id" class="form-control" placeholder="Id">
                                    </label>
                                </div>
                                <div class="form-group">
                                    <label class="w-100 bootstrap-select">
                                        <spring:message code="technical" />
                                        <select name="userId" class="form-control select2">
                                            <option value=""><spring:message code="select.placeholder" /></option>
                                            <c:forEach items="${users}" var="user">
                                                <option value="${user.id}"><spring:message code="${user.fullName}" /></option>
                                            </c:forEach>
                                        </select>
                                    </label>
                                </div>
                                <div class="form-group">
                                    <label class="w-100 bootstrap-select">
                                        <spring:message code="project" />
                                        <select name="projectId" class="form-control select2">
                                            <option value=""><spring:message code="select.placeholder" /></option>
                                            <c:forEach items="${projects}" var="project">
                                                <option value="${project.id}"><spring:message code="${project.name}" /></option>
                                            </c:forEach>
                                        </select>
                                    </label>
                                </div>
                                <div class="form-group">
                                    <label class="w-100 bootstrap-select">
                                        <spring:message code="status" />
                                        <select name="status" class="form-control select2">
                                            <option value=""><spring:message code="select.placeholder" /></option>
                                            <option value="NOT_FINISHED"><spring:message code="not.finished" /></option>
                                            <option value="FINISHED"><spring:message code="finished" /></option>
                                        </select>
                                    </label>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <button type="button" onclick="filter(true)" class="btn btn-outline-secondary btn-sm"><spring:message code="reset" /></button>
                                    <button type="button" onclick="filter()" class="btn btn-default"><spring:message code="filter" /></button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table id="dTable" class="table table-striped table-borderer dataTable w-100">
                            <thead>
                            <tr>
                                <th><spring:message code="id" /></th>
                                <th><spring:message code="technical" /></th>
                                <th><spring:message code="project" /></th>
                                <th><spring:message code="start.date" /></th>
                                <th><spring:message code="end.date" /></th>
                                <th><spring:message code="actions" /></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="errorModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">
                    <h5><spring:message code="create.record.error" /></h5>
                </div>
            </div>

            <div class="modal-body"></div>

            <div class="modal-footer clearfix">
                <div class="w-100">
                    <div class="float-left">
                        <button type="button" class="btn btn-sm" data-dismiss="modal">
                            <spring:message code="close"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>

    let locale = '${locale}';
    let userId = ${user.id};

    $(document).ready(function() {
        initializeDataTables();
        initializeSelects();
        filterByParams();
    });

</script>

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/js/bootstrap-select.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ui/static/js/select2/select2-utils.js?v=<%= System.currentTimeMillis() %>"></script>
