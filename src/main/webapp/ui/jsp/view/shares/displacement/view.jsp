<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/css/bootstrap-select.min.css">

<div class="breadcrumbs">
    <div class="breadcrumbs-inner">
        <div class="row m-0">
            <div class="col">
                <div class="page-header float-left">
                    <div class="page-title">
                        <h1 class="text-uppercase"><spring:message code="shares.displacement.title" /></h1>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="page-header float-right">
                    <button type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#createModal">
                        <spring:message code="create" />
                    </button>
                </div>
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
                    <div class="title mb-0">
                        <spring:message code="shares.displacement.title" />
                    </div>

                    <div class="table-responsive">
                        <table id="dTable" class="table table-striped table-borderer dataTable w-100">
                            <thead>
                            <tr>
                                <th><spring:message code="id" /></th>
                                <th><spring:message code="project" /></th>
                                <th><spring:message code="description" /></th>
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

<div id="createModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">
                    <h5><spring:message code="shares.displacement.create.title" /></h5>
                </div>
            </div>

            <div class="modal-body">
                <form id="createForm">
                    <div class="row">
                        <div class="col col-md-6">
                            <div class="form-group">
                                <label class="col-form-label w-100"><spring:message code="project"/>
                                    <select class="form-control input" name="projectId" required>
                                        <option></option>
                                        <c:forEach items="${projects}" var="project">
                                            <option value="${project.id}"><spring:message code="${project.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </label>
                            </div>
                        </div>

                        <div class="col col-md-6">
                            <div class="form-group">
                                <label class="col-form-label w-100"><spring:message code="description" />
                                    <input name="description" type="text" class="form-control" required />
                                </label>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col col-md-6">
                            <div class="form-group">
                                <label class="col-form-label w-100"><spring:message code="start.date" />
                                    <input name="startDate" type="datetime-local" class="form-control" required>
                                </label>
                            </div>
                        </div>

                        <div class="col col-md-6">
                            <div class="form-group">
                                <label class="col-form-label w-100"><spring:message code="end.date" />
                                    <input name="endDate" type="datetime-local" class="form-control" required>
                                </label>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col">
                            <div class="form-group">
                                <label class="col-form-label w-100"><spring:message code="shares.displacement.observations" />
                                    <textarea name="observations" type="text" class="form-control"></textarea>
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
                            <spring:message code="close" />
                        </button>
                    </div>
                    <div class="float-right">
                        <button type="button" class="btn btn-default btn-sm" onclick="create()">
                            <spring:message code="create" />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<spring:message code="create.signing.error" var="errorTitle"/>
<spring:message code="close" var="closeBtn"/>
<spring:message code="close.record" var="hrefTitleVal"/>

<jsp:include page = "../../signings/errorModal.jsp">
    <jsp:param name="id" value = "errorModal"/>
    <jsp:param name="modalTitle" value="${errorTitle}"/>
    <jsp:param name="closeButton" value="${closeBtn}"/>
    <jsp:param name="hrefTitle" value="${hrefTitleVal}"/>
</jsp:include>

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/js/bootstrap-select.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ui/static/js/select2/select2-utils.js?v=<%= System.currentTimeMillis() %>"></script>

<script>

    let locale = '${locale}';
    let userId = ${user.id};

    $(document).ready(function() {

        let columns = ['id', 'project.name', 'description', 'startDate', 'endDate', 'id']
        let endpoint = '/v1/shares/displacement';
        let actions = [ { action: 'view', url: '/shares/displacement/{id}', permission: 'read_displacement_shares' }, { action: 'delete', permission: 'edit_displacement_shares' }]
        let expand = [ 'project' ]
        let filters = [{'userIds': userId}]
        let columnsDef = [
            {
                targets: [3, 4],
                render: function (data) {
                    return moment(data).format('DD-MM-YYYY HH:mm');
                }
            },
        ]

        customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, filters, columnsDef);
        dTable = createDataTable('#dTable', customDataTable, locale);
        customDataTable.setCurrentTable(dTable);

        initializeSelects();
    });

    function initializeSelects() {
        const form = document.querySelector('#createForm');
        const selects = form.querySelectorAll('select');

        selects.forEach(select => {
            createBootstrapSelect2($(select));
        });
    }

</script>