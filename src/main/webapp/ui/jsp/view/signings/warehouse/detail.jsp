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
                    <a class="btn btn-default btn-sm" href="javascript:history.back()"><spring:message code="back"/></a>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="clearfix"></div>

<div class="content">
    <div class="card">
        <div class="title mb-0">
            <spring:message code="info"/>
        </div>

        <form id="editForm" class="needs-validation">
            <div class="row">
                <div class="col-sm-12 col-md-4">
                    <div class="form-group mb-1">
                        <label class="col-form-label w-100"><spring:message code="project"/>
                            <input type="text" class="form-control mt-1" value="${projectName}" disabled />
                        </label>
                    </div>
                </div>

                <div class="col-sm-12 col-md-4">
                    <div class="form-group mb-1">
                        <label class="col-form-label w-100"><spring:message code="start.date"/>
                            <input type="datetime-local" name="startedAt" class="form-control mt-1" value="${warehouseSigning.startedAt}" disabled />
                        </label>
                    </div>
                </div>
                <c:if test="${warehouseSigning.closedAt != null}">
                    <div class="col-sm-12 col-md-4">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="end.date"/>
                                <input type="datetime-local" name="closedAt" class="form-control mt-1" value="${warehouseSigning.closedAt}" disabled />
                            </label>
                        </div>
                    </div>
                </c:if>
            </div>

            <div class="row actionable d-none">
                <div class="col text-right">
                    <button id="editBtn" type="button" class="btn btn-sm" data-dismiss="modal"><spring:message code="close"/></button>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
    let locale = '${locale}';
    let canUpdate = ${canUpdate};

    $(document).ready(function(){
        initialize();
        save();
    });

    function initialize() {
        if (!canUpdate)
            return ;

        const editForm = document.querySelector('#editForm');

        editForm.querySelector('.actionable').classList.remove('d-none');

        editForm.querySelector('[name="startedAt"]').disabled = true;

        const closeAtEditor = editForm.querySelector('[name="closedAt"]');

        if (closeAtEditor)
            closeAtEditor.disabled = true;
    }

    function save() {
        const editBtnJQ = $('#editBtn');
        const editForm = document.getElementById('editForm');
        const editFormJQ = $('#editForm');

        editBtnJQ.click(async () => {
            if (!isValidForm('#editForm'))
            {
                console.log('Invalido');
                editFormJQ.addClass('was-validated');
                return ;
            }

            console.log('Loading');
            showLoading();
            editFormJQ.removeClass('was-validated');

            const editStart = editForm.querySelector('[name="startedAt"]');
            const editEnd = editForm.querySelector('[name="closedAt"]');

            let startedAt = null;
            let closedAt = null;

            if (editStart)
                startedAt = editStart.value;

            if (editEnd)
                closedAt = editEnd.value;

            let params = {
                startedAt: startedAt
                , closedAt: closedAt
            };

            axios.patch('/v1' + window.location.pathname, params).then(() => {
                showNotify(messages.signings.warehouse.update.success);
            })
            .catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
        });
    }
</script>
