<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/css/bootstrap-select.min.css">

<style>
    .right-panel .page-header button {
        margin-top: 0;
    }

    .start-date-info {
        font-size: 11px;
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
                                <h1 class="text-uppercase"><spring:message code="signing.warehouse"/></h1>
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
                <form id="form">
                    <div id="form-page-header" class="page-header pl-3 d-md-flex align-items-center">
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
                                <button type="button" class="btn btn-default btn-sm w-100" onclick="submitWarehouse()">
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
    <div class="row">
        <div class="col">
            <div class="card">
                <div class="card-body">
                    <div class="title mb-0">
                        <spring:message code="signing.warehouse"/>
                    </div>

                    <div class="table-responsive">
                        <table id="dTable" class="table table-striped table-borderer dataTable w-100">
                            <thead>
                                <tr>
                                    <th><spring:message code="id"/></th>
                                    <th><spring:message code="project"/></th>
                                    <th><spring:message code="start.date"/></th>
                                    <th><spring:message code="end.date"/></th>
                                    <th><spring:message code="actions"/></th>
                                </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>

    let locale = '${locale}';
    let userId = ${user.id};

    $(document).ready(async function () {
        initializeDataTables();
        initializeSelects();
    });

</script>

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/js/bootstrap-select.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ui/static/js/select2/select2-utils.js?v=<%= System.currentTimeMillis() %>"></script>