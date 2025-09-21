<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/css/bootstrap-select.min.css">

<style>
    .right-panel .page-header button {
        margin-top: 0;
    }
</style>

<div class="row m-0">
    <div class="col-12 p-0">
        <div class="breadcrumbs">
            <div class="breadcrumbs-inner">
                <div class="row m-0">
                    <div class="col">
                        <div class="page-header">
                            <div class="page-title">
                                <h1 class="text-uppercase"><spring:message code="signing.personal"/></h1>
                            </div>
                        </div>
                    </div>
                </div>
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
                        <spring:message code="signing.personal"/>
                    </div>

                    <div class="table-responsive">
                        <table id="dTable" class="table table-striped table-borderer dataTable w-100">
                            <thead>
                                <tr>
                                    <th><spring:message code="id"/></th>
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

<div id="editModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">
                    <h5><spring:message code="edit.personal.signing"/></h5>
                </div>
            </div>

            <div class="modal-body">
                <form id="editForm" class="needs-validation">
                    <div class="row">
                        <div class="col-sm-12 col-md-6">
                            <label class="col-form-label w-100"><spring:message code="start.date"/>
                                <input type="datetime-local" name="startDate" class="form-control mt-1"/>
                            </label>
                        </div>
                        <div class="col-sm-12 col-md-6">
                            <label class="col-form-label w-100"><spring:message code="end.date"/>
                                <input type="datetime-local" name="endDate" class="form-control mt-1"/>
                            </label>
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
                        <button id="saveBtn" type="button" class="btn btn-sm btn-success">
                            <spring:message code="edit"/>
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
    let isAdmin = ${isAdmin};

    $(document).ready(async function () {
        initializeDataTables();
    });

</script>

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/js/bootstrap-select.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ui/static/js/select2/select2-utils.js?v=<%= System.currentTimeMillis() %>"></script>