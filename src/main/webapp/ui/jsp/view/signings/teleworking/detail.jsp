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
                        <h1 class="text-uppercase"><spring:message code="signing.teleworking"/></h1>
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
        <div class="card-body">
            <div class="title mb-0">
                <spring:message code="info"/>
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
                                <input type="datetime-local" name="startedAt" class="form-control mt-1" value="${teleworkingSigning.startedAt}" />
                            </label>
                        </div>
                    </div>

                    <div class="col-sm-12 col-md-4">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="end.date"/>
                                <input type="datetime-local" name="closedAt" class="form-control mt-1" value="${teleworkingSigning.closedAt}" />
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-12 col-md-6">
                        <label class="col-form-label w-100"><spring:message code="signing.geolocation.start"/>
                            <iframe
                                    class="locationFrame"
                                    loading="lazy"
                                    allowfullscreen
                                    referrerpolicy="no-referrer-when-downgrade"
                                    src="https://www.google.com/maps?q=${teleworkingSigning.startedLocation}&output=embed">
                            </iframe>
                        </label>
                    </div>
                    <div class="col-sm-12 col-md-6">
                        <label class="col-form-label w-100"><spring:message code="signing.geolocation.end"/>
                            <iframe
                                    class="locationFrame"
                                    loading="lazy"
                                    allowfullscreen
                                    referrerpolicy="no-referrer-when-downgrade"
                                    src="https://www.google.com/maps?q=${teleworkingSigning.closedLocation}&output=embed">
                            </iframe>
                        </label>
                    </div>
                </div>

                <div class="row actionable">
                    <div class="col text-right">
                        <button id="editBtn" type="button" class="btn btn-standard btn-sm movile-full"><spring:message code="save"/></button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script>

    let locale = '${locale}';
    let canUpdate = ${canUpdate};
    let isSigningFinished = ${ teleworkingSigning.closedAt != null };

    $(document).ready(function() {
        initialize();
        save();
    });

    function initialize() {
        const editForm = document.getElementById('editForm');

        if (!canUpdate) {
            editForm.querySelector('.actionable').style.display = 'none';

            editForm.querySelectorAll('input, select, textarea, button').forEach(el => {
                el.disabled = true;
            });
        }
    }

    function save() {
        const editBtn = $('#editBtn');
        const editFormJQ = $('#editForm');
        const editForm = document.querySelector('#editForm');

        editBtn.click(async () => {

            if (!isValidForm('#editForm')) {
                editFormJQ.addClass('was-validated');
            } else {

                showLoading();
                editFormJQ.removeClass('was-validated');

                const startedAt = editForm.querySelector('[name="startedAt"]').value;
                const closedAt = editForm.querySelector('[name="closedAt"]').value;

                let params = {
                    startedAt: startedAt,
                    closedAt: closedAt
                };

                axios.patch('/v1' + window.location.pathname, params).then(() => {
                    showNotify(messages.signings.teleworking.update.success);
                }).catch(error => showNotify(error.response.data.detail, 'danger'))
                    .finally(() => hideLoading());
            }
        })
    }

</script>