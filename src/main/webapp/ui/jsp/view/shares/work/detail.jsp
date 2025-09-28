<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    final ObjectMapper mapper = new ObjectMapper();
    final String filesJson = mapper.writeValueAsString(request.getAttribute("files"));
%>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/css/bootstrap-select.min.css">

<div class="breadcrumbs">
    <div class="breadcrumbs-inner">
        <div class="row m-0">
            <div class="col-12 col-lg-10">
                <div class="page-header float-left">
                    <div class="page-title">
                        <h1 class="text-uppercase"><spring:message code="shares.work.title"/></h1>
                    </div>
                </div>
            </div>
            <div class="col-12 col-lg-2">
                <div class="page-header float-right">
                    <a id="returnBtn" class="btn btn-default btn-sm"><spring:message code="back"/></a>
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
                            <label class="col-form-label w-100 bootstrap-select"><spring:message code="project"/>
                                <select class="form-control input mt-1" name="projectId" required>
                                    <option></option>
                                    <c:forEach items="${projects}" var="project">
                                        <option value="${project.id}" ${project.id == workShare.projectId ? 'selected' : ''}><spring:message code="${project.name}"/></option>
                                    </c:forEach>
                                </select>
                            </label>
                        </div>
                    </div>

                    <div class="col-sm-12 col-md-4">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="start.date" />
                                <input name="startDate" type="datetime-local" class="form-control mt-1" value="${workShare.startDate}" required />
                            </label>
                        </div>
                    </div>

                    <div class="col-sm-12 col-md-4">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="end.date"/>
                                <input name="endDate" type="datetime-local" class="form-control mt-1" value="${workShare.endDate}" />
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-12 col-md-8">
                        <div class="form-group">
                            <label class="col-form-label w-100"><spring:message code="observations" />
                                <textarea name="observations" type="text" class="form-control" rows="6" required>${workShare.observations}</textarea>
                            </label>
                        </div>
                    </div>

                    <div class="col-sm-12 col-md-4">
                        <div class="form-group">
                            <label class="col-form-label pb-0"><spring:message code="operator.signature" /></label>
                            <div class="signature-pad operator-signature">
                                <div class="signature-pad--body">
                                    <canvas class="signature-canvas"></canvas>
                                </div>
                                <div class="signature-pad--footer">
                                    <div class="description"><spring:message code="operator.signature" /></div>
                                    <div class="signature-pad--actions">
                                        <div>
                                            <button class="clearSignatureButton" type="button"><i class="fa fa-redo"></i></button>
                                        </div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col">
                        <div id="filesFormGroup" class="form-group">
                            <label class="col-form-label">
                                <spring:message code="files" />
                                <input type="file" class="form-control mt-1" name="files" accept=".jpg, .jpeg, .png" multiple>
                            </label>
                        </div>
                    </div>

                    <div class="col text-right">
                        <div class="form-check form-check-inline mr-0">
                            <label class="form-check-label">
                                <spring:message code="shares.intervention.create.share.client.notif.mail" />
                                <input class="form-check-input" type="checkbox" name="clientNotif">
                            </label>
                        </div>
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

    <div class="card">
        <div class="card-body">
            <div class="row">
                <div class="col">
                    <div class="title mb-0">
                        <spring:message code="breaks"/>
                    </div>
                </div>
                <div class="col text-right">
                    <c:if test="${workShare.endDate == null}">
                        <c:if test="${empty currentShareBreak}">
                            <button type="button" class="btn btn-default btn-sm" onclick="startBreak()">
                                <spring:message code="breaks.start"/>
                            </button>
                        </c:if>

                        <c:if test="${not empty currentShareBreak}">
                            <button type="button" class="btn btn-danger btn-sm" onclick="finishBreak()">
                                <spring:message code="breaks.end"/>
                            </button>
                        </c:if>
                    </c:if>
                </div>
            </div>

            <div class="table-responsive">
                <table id="dTable" class="table table-striped table-borderer dataTable w-100">
                    <thead>
                    <tr>
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

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap-select/1.13.17/js/bootstrap-select.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ui/static/js/plugins/signature_pad/signature_pad.umd.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ui/static/js/select2/select2-utils.js?v=<%= System.currentTimeMillis() %>"></script>

<script>

    const locale = '${locale}';
    const canUpdate = ${canUpdate};
    const isShareFinished = ${ workShare.endDate != null };
    const files = <%= filesJson %>;

    let signatures = { operator: null }
    let canvas = { operator: null }

    $(document).ready(function() {
        initializeSelects();
        loadSignatures('${workShare.operatorSignature}');
        loadFiles(files);
        initialize();
        save();
        loadDataTables();
        setReturnButtonUrl();
    });

    function initializeSelects() {
        const form = document.querySelector('#editForm');
        const selects = form.querySelectorAll('select');

        selects.forEach(select => {
            createBasicSelect2($(select));
        });
    }

    function initialize() {
        const editForm = document.querySelector('#editForm');

        if (!isShareFinished) {
            const fieldsToDisable = ['projectId', 'startDate', 'endDate'];

            fieldsToDisable.forEach(name => {
                const field = editForm.querySelector('[name="' + name + '"]');
                if (field) field.disabled = true;
            });
        } else if (!canUpdate) {
            editForm.querySelector('.actionable').style.display = 'none';

            editForm.querySelectorAll('input, select, textarea, button').forEach(el => {
                el.disabled = true;
            });

            if (signatures.operator)
                signatures.operator.off();
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

                const projectId = editForm.querySelector('[name="projectId"]').value;
                const startDate = editForm.querySelector('[name="startDate"]').value;
                const endDate = editForm.querySelector('[name="endDate"]').value;
                const observations = editForm.querySelector('[name="observations"]').value;
                const operatorSignature = !signatures.operator.isEmpty() ? signatures.operator.toDataURL() : null;
                const files = await parseFiles(editForm);
                const notify = editForm.querySelector('[name="clientNotif"]');

                let params = {
                    projectId: projectId,
                    startDate: startDate,
                    endDate: endDate,
                    observations: observations,
                    operatorSignature: operatorSignature,
                    files: files,
                    notify: notify.checked
                };

                axios.put('/v1' + window.location.pathname, params).then(() => {
                    location.reload();
                }).catch(error => showNotify(error.response.data.detail, 'danger'))
                    .finally(() => hideLoading());
            }
        })
    }

    function loadSignatures(signature) {
        preloadSignatures(signatures, canvas, 'operator', signature);

        $('.operator-signature .clearSignatureButton').click(function () { signatures.operator.clear(); });
    }

    function loadFiles(files) {
        if (files && files.length) {
            showFiles(files, 'files', '#filesFormGroup');
        }
    }

    function showFiles(files, inputName, filesId) {
        const form = document.querySelector('#editForm');
        const filesFormGroup = form.querySelector(filesId);
        const filesAttachmentContainer = filesFormGroup.querySelector('.attachment-contianer');

        if (filesAttachmentContainer) {
            filesAttachmentContainer.remove();
        }

        const linksContainer = document.createElement('div');
        linksContainer.classList.add('attachment-contianer');

        const selector = form.querySelector('[name="' + inputName + '"]');
        if (selector && (isShareFinished && !canUpdate)) {
            selector.remove();
        }

        if (files.length > 0) {
            const downloadBase64File = (file) => {
                const binaryData = atob(file.content);
                const byteNumbers = new Uint8Array(binaryData.length);

                for (let i = 0; i < binaryData.length; i++) {
                    byteNumbers[i] = binaryData.charCodeAt(i);
                }

                const link = document.createElement('a');
                const blob = new Blob([byteNumbers], { type: 'application/octet-stream' });

                link.href = URL.createObjectURL(blob);
                link.download = file.name;
                link.textContent = file.name;
                link.classList.add('btn', 'btn-outline-primary', 'btn-xs', 'mr-1');
                link.target = '_blank';

                linksContainer.appendChild(link);

                if (inputName === 'files' && canUpdate) {
                    const btn = document.createElement('button');
                    btn.type = 'button';
                    btn.textContent = 'x';
                    btn.classList.add('btn', 'btn-outline-danger', 'btn-xs', 'mr-1');
                    btn.addEventListener('click', () => {
                        deleteFile(file, btn, link);
                    });

                    linksContainer.appendChild(btn);
                }
            };

            files.forEach(file => downloadBase64File(file));

            filesFormGroup.appendChild(linksContainer);
        }
    }

    function deleteFile(file, btn, link) {
        const alertMessage = messages.shares.work.files.delete.alert.replace('{0}', file.name);
        if (confirm(alertMessage)) {

            showLoading();

            axios.delete('/v1/shares/work/${workShare.id}/files/' + file.id).then(() => {
                const successMessage = messages.shares.work.files.delete.success.replace('{0}', file.name);
                link.remove();
                btn.remove();
                showNotify(successMessage);
            }).catch(error => showNotify(error.response.data.detail, 'danger'))
                .finally(() => hideLoading());
        }
    }

    function loadDataTables() {
        let columns = ['startDate', 'endDate', 'id']
        let endpoint = '/v1/shares/work/${workShare.id}/breaks';
        let actions = []
        if (!isShareFinished) {
            actions.push(
                {
                    action: 'delete',
                    permission: 'edit_work_shares',
                    conditionGroups: [
                        {
                            conditions: [
                                { key: 'endDate', value: [ undefined ], operation: '!==' }
                            ]
                        }
                    ]
                }
            );
        }
        let expand = []
        let columnDefs = [
            {
                targets: [0, 1],
                render: function (data) {
                    return data ? parseDate(data, 'DD-MM-YYYY HH:mm') : '-';
                }
            }
        ]

        customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, null, columnDefs);
        dTable = createDataTable('#dTable', customDataTable, locale);
        customDataTable.setCurrentTable(dTable);
    }

    function startBreak() {
        showLoading();

        axios.post('/v1/shares/work/${workShare.id}/breaks', { })
            .then(() => location.reload())
            .catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
    }

    function finishBreak() {
        showLoading();

        axios.put('/v1/shares/work/${workShare.id}/breaks/${currentShareBreak.id}', { })
            .then(() => location.reload())
            .catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
    }

    function remove(id) {
        const alertMessage = messages.breaks.delete.alert;
        if (confirm(alertMessage)) {

            showLoading();

            axios.delete('/v1/shares/work/${workShare.id}/breaks/' + id).then(() => {
                dTable.ajax.reload();
                const successMessage = messages.breaks.delete.success;
                showNotify(successMessage);
            }).catch(error => showNotify(error.response.data.detail, 'danger'))
                .finally(() => hideLoading());
        }
    }

    function setReturnButtonUrl() {
        let lastPageUrl = '/shares/work';

        if (document.referrer) {
            const lastPagePath = new URL(document.referrer).pathname;
            const queryParams = document.referrer.split('?')[1];

            lastPageUrl = lastPagePath + (queryParams ? '?' + queryParams : '');
        }

        $('#returnBtn').attr('href', lastPageUrl);
    }

</script>