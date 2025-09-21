<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>

    .locationFrame {
        width: 100%;
        height: 450px;
        border: 0;
    }

</style>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/css/bootstrap-select.min.css">

<div class="breadcrumbs">
    <div class="breadcrumbs-inner">
        <div class="row m-0">
            <div class="col-12 col-lg-10">
                <div class="page-header float-left">
                    <div class="page-title">
                        <h1 class="text-uppercase"><spring:message code="signing.warehouse"/></h1>
                    </div>
                </div>
            </div>
            <div class="col-12 col-lg-2">
                <div class="page-header float-right">
                    <a class="btn btn-default btn-sm" id="returnBtn"><spring:message code="back"/></a>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="clearfix"></div>

<div class="content">
    <div class="card">
        <div class="card-body">
            <div class="title mb-0">
                <spring:message code="warehousesigning"/>
            </div>

            <form id="editForm" class="needs-validation">
                <div class="row">
                    <div class="col-sm-12 col-md-4">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="project"/>
                                <input type="text" class="form-control mt-1" value="${projectName}" disabled/>
                            </label>
                        </div>
                    </div>

                    <div class="col-sm-12 col-md-4">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="start.date"/>
                                <input type="datetime-local" name="startedAt" class="form-control mt-1"
                                       value="${warehouseSigning.startedAt}" disabled/>
                            </label>
                        </div>
                    </div>
                    <div class="col-sm-12 col-md-4" id="endDateBox">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="end.date"/>
                                <input type="datetime-local" name="closedAt" class="form-control mt-1"
                                       value="${warehouseSigning.closedAt}" disabled/>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row actionable">
                    <div class="col text-right">
                        <button id="finishBtn" type="button" class="btn btn-danger btn-sm"><spring:message
                                code="finish"/></button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-body">
            <div class="row">
                <div class="col">
                    <div class="title mb-0">
                        <spring:message code="workshop"/>
                    </div>
                </div>
                <div class="col text-right">
                    <button id="createWorkshopBtn" type="button" class="btn btn-default btn-sm" data-toggle="modal"
                            data-target="#createModal">
                        <spring:message code="workshop.add"/>
                    </button>
                </div>
            </div>

            <div class="table-responsive">
                <table id="dTable" class="table table-striped table-borderer dataTable w-100">
                    <thead>
                    <tr>
                        <th><spring:message code="id"/></th>
                        <th><spring:message code="project"/></th>
                        <th><spring:message code="start.date"/></th>
                        <th><spring:message code="end.date"/></th>
                        <th><spring:message code="description"/></th>
                        <th><spring:message code="actions"/></th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>

<div id="createModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">
                    <h5><spring:message code="workshop.add"/></h5>
                </div>
            </div>

            <div class="modal-body">
                <form id="createForm">
                    <div class="row">
                        <div class="col">
                            <div class="form-group">
                                <label class="col-form-label w-100">
                                    <spring:message code="project"/>
                                    <select name="projectId" class="form-control" required>
                                        <option value="" disabled selected="selected">
                                            <spring:message code="select.placeholder"/>
                                        </option>
                                        <c:forEach items="${projects}" var="project">
                                            <option value="${project.id}">
                                                    ${project.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </label>
                            </div>
                        </div>
                    </div>
                </form>
            </div>

            <div class="modal-footer clearfix">
                <div class="w-100">
                    <div class="float-left">
                        <button type="button" class="btn btn-sm" data-dismiss="modal">
                            <spring:message code="close"/>
                        </button>
                    </div>
                    <div class="float-right">
                        <button type="button" class="btn btn-default btn-sm" onclick="createWorkshop()">
                            <spring:message code="create"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    let userId = ${user.id};
    let canUpdate = ${canUpdate};
</script>
