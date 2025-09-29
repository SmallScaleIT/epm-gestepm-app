<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
    .content {
        height: auto;
    }
</style>

<div class="breadcrumbs">
    <div class="breadcrumbs-inner">
        <div class="row m-0">
            <div class="col-12 col-lg-10">
                <div class="page-header float-left">
                    <div class="page-title">
                        <h1 id="title" class="text-uppercase p-0"></h1>
                        <a id="forumTitle" class="page-sub-title p-0"></a>
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
                <spring:message code="noprogrammedshare"/>
            </div>

            <form id="editForm" class="needs-validation">
                <div class="row">
                    <div class="col-12 col-lg-6">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="start.date"/>
                                <input type="datetime-local" name="startDate" class="form-control mt-1"
                                       value="${share.startDate}"/>
                            </label>
                        </div>
                    </div>
                    <div class="col-12 col-lg-6">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="end.date"/>
                                <input type="datetime-local" name="endDate" class="form-control mt-1"
                                       value="${share.endDate}"/>
                            </label>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-12 col-lg-6">
                        <div class="form-group">
                            <label class="col-form-label w-100">
                                <spring:message code="family"/>
                                <select name="familyId" class="form-control" required>
                                    <option value><spring:message code="family.selection"/></option>
                                    <c:forEach items="${families}" var="family">
                                        <option value="${family.id}">
                                            <c:choose>
                                                <c:when test="${locale == 'es'}">${family.nameES}</c:when>
                                                <c:otherwise>${family.nameFR}</c:otherwise>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </label>
                        </div>
                    </div>

                    <div class="col-12 col-lg-6">
                        <div class="form-group">
                            <label class="col-form-label w-100">
                                <spring:message code="subfamily"/>
                                <select name="subFamilyId" class="form-control" required>
                                    <option value selected><spring:message code="subfamily.selection"/></option>
                                </select>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-12 col-lg-6">
                        <div class="d-flex flex-wrap gap-2">
                            <div id="filesFormGroup" class="form-group">
                                <label class="col-form-label w-100"><spring:message code="files"/>
                                    <input type="file" class="form-control" name="files" accept=".jpg, .jpeg, .png"
                                           multiple>
                                </label>
                            </div>
                        </div>
                    </div>

                    <div class="col-12 col-md-6">
                        <div class="form-group">
                            <label class="col-form-label w-100">
                                <spring:message code="operator.name"/>
                                <input type="text" name="user" class="form-control" readonly/>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-12">
                        <div class="form-group mb-0">
                            <spring:message code="description.placeholder" var="placeholder"/>
                            <label class="col-form-label w-100">
                                <spring:message code="description"/>
                                <textarea class="form-control" placeholder="${placeholder}" name="description"
                                          required></textarea>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row mt-2 actionable">
                    <div class="col text-right">
                        <button id="finishBtn" type="button" class="btn btn-danger btn-sm">Finalizar</button>
                        <button id="editBtn" type="button" class="btn btn-standard btn-sm movile-full"><spring:message
                                code="save"/></button>
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
                        <spring:message code="inspection"/>
                    </div>
                </div>
                <div class="col text-right">
                    <button id="createInspectionBtn" type="button" class="btn btn-default btn-sm" data-toggle="modal"
                            data-target="#createModal">
                        <spring:message code="inspection.add"/>
                    </button>
                </div>
            </div>

            <div class="table-responsive">
                <table id="dTable" class="table table-striped table-borderer dataTable w-100">
                    <thead>
                    <tr>
                        <th><spring:message code="id"/></th>
                        <th><spring:message code="start.date"/></th>
                        <th><spring:message code="end.date"/></th>
                        <th><spring:message code="action"/></th>
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
                    <h5><spring:message code="noprogrammedshare"/></h5>
                </div>
            </div>

            <div class="modal-body">
                <form id="createForm">
                    <div class="row">
                        <div class="col">
                            <div class="form-group">
                                <label class="col-form-label w-100"><spring:message code="action.next"/>
                                    <select name="action" class="form-control" required disabled>
                                        <option value="DIAGNOSIS"><spring:message
                                                code="inspection.type.diagnosis"/></option>
                                        <option value="INTERVENTION"><spring:message
                                                code="inspection.type.intervention"/></option>
                                        <option value="FOLLOWING"><spring:message
                                                code="inspection.type.following"/></option>
                                    </select>
                                </label>
                                <div class="w-100 text-right">
                                    <div class="badge badge-inspection"></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col">
                            <div class="form-group">
                                <label class="col-form-label w-100"><spring:message code="inspection.second.technical"/>
                                    <select name="secondTechnical" class="form-control" required>
                                        <option value="" disabled selected="selected">
                                            <spring:message code="shares.intervention.create.technical.placeholder"/>
                                        </option>
                                        <c:forEach items="${usersTeam}" var="technical">
                                            <c:if test="${ user.id != technical.userId }">
                                                <option value="${technical.userId}">
                                                    <spring:message code="${technical.name} ${technical.surnames}"/>
                                                </option>
                                            </c:if>
                                        </c:forEach>
                                    </select>
                                </label>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col">
                            <div id="hasNotRoleAlert" class="badge badge-danger mb-1 d-none"><spring:message
                                    code="share.detail.init.diag.role"/></div>
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
                        <button type="button" class="btn btn-default btn-sm" onclick="createInspection()">
                            <spring:message code="create"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .file-url {
        display: block;
        font-size: 12px;
        color: blue;
        font-style: italic;
    }
</style>

<script>

    let locale = '${locale}';
    let returnBtn = $('#returnBtn');
    let canUpdate = ${canUpdate};

    let share;
    let nextAction = '${nextAction}';
    let currentMode;

    let hasRole;
    let canClose;

    let userId = ${user.id};

    async function init() {
        const editForm = document.querySelector('#editForm');
        const shareId = getShareId();

        share = await getShare(shareId);
        await getPermissions();

        const familyElement = editForm.querySelector('[name="familyId"]');

        familyElement.onchange = () => {
            updateSubFamilySelect();
        }

        updateModalAction(nextAction);
    }

    function getShareId() {
        const currentUrl = window.location.href;
        const segments = currentUrl.split('/');

        return segments[segments.length - 1];
    }

    async function getShare(id) {
        const response = await axios.get('/v1/shares/no-programmed/' + id, {
            params: {
                _expand: 'family,subFamily,user,project,files',
                locale: locale
            }
        });
        return response.data.data;
    }

    function actionEditClosed(editForm, filesData, id) {
        axios.patch('/v1/shares/no-programmed/' + id, {
            userId: ${user.id},
            familyId: editForm.querySelector('[name="familyId"]').value,
            subFamilyId: editForm.querySelector('[name="subFamilyId"]').value,
            description: editForm.querySelector('[name="description"]').value,
            startDate: editForm.querySelector('[name="startDate"]').value,
            endDate: editForm.querySelector('[name="endDate"]').value,
            state: 'CLOSED',
            files: filesData
        }).then(async (response) => {
            share = await getShare(response.data.data.id);
            getPermissions();
            update(share);
            showNotify(messages.shares.noprogrammed.update.success)
        }).catch(error => showNotify(error.response.data.detail, 'danger'))
        .finally(() => hideLoading());
    }

    function actionEdit(editForm, filesData, id) {
        axios.patch('/v1/shares/no-programmed/' + id, {
            userId: ${user.id},
            familyId: editForm.querySelector('[name="familyId"]').value,
            subFamilyId: editForm.querySelector('[name="subFamilyId"]').value,
            description: editForm.querySelector('[name="description"]').value,
            startDate: editForm.querySelector('[name="startDate"]').value,
            state: 'INITIALIZED',
            files: filesData
        }).then(async (response) => {
            share = await getShare(response.data.data.id);
            getPermissions();
            update(share);
            showNotify(messages.shares.noprogrammed.update.success)
        }).catch(error => showNotify(error.response.data.detail, 'danger'))
        .finally(() => hideLoading());
    }

    function edit(id) {
        const editBtn = $('#editBtn');
        const editForm = document.querySelector('#editForm');

        editBtn.click(async () => {
            await updateShare(id, editForm, share.state === 'NEW' ? 'INITIALIZED' : share.state);
        });
    }

    function close(id) {
        const finishBtn = $('#finishBtn');
        const editForm = document.querySelector('#editForm');

        finishBtn.click(async () => {
            await updateShare(id, editForm, share.endDate ? share.state : 'CLOSED')
        });
    }

    async function updateShare(id, form, state) {
        if (!isValidForm('#editForm')) {
            return;
        }

        showLoading();

        const files = form.querySelector('[name="files"]')?.files;
        let filesData = [];

        for (let i = 0; i < files?.length; i++) {
            const file = files[i];

            filesData.push({
                name: file.name,
                content: await toBase64(file)
            });
        }

        const endDateInput = form.querySelector('[name="endDate"]');

        axios.patch('/v1/shares/no-programmed/' + id, {
            userId: ${user.id},
            familyId: form.querySelector('[name="familyId"]').value,
            subFamilyId: form.querySelector('[name="subFamilyId"]').value,
            description: form.querySelector('[name="description"]').value,
            startDate: form.querySelector('[name="startDate"]').value,
            endDate: endDateInput ? endDateInput.value : null,
            state: state,
            files: filesData
        }).then(() => {
            location.reload();
        }).catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
    }

    function update(share) {
        const form = document.querySelector('#editForm');

        document.querySelector('#title').textContent = messages.shares.noprogrammed.title.replace('{0}', share.id) + ' - ' + share.project.name;

        if (share.family) {
            form.querySelector('[name="familyId"]').value = share.family.id;
        }

        if (share.subFamily) {
            const subFamily = form.querySelector('[name="subFamilyId"]');

            const newOption = document.createElement('option');
            newOption.value = share.subFamily.id;
            newOption.textContent = share.subFamily.name;

            subFamily.appendChild(newOption);
            subFamily.value = newOption.value;
        }

        if (share.user) {
            form.querySelector('[name="user"]').value = share.user.name;
        }

        if (share.description) {
            form.querySelector('[name="description"]').value = share.description;
        }

        if (share.startDate) {
            form.querySelector('[name="startDate"]').value = share.startDate;
        }

        if (share.endDate) {
            form.querySelector('[name="endDate"]').value = share.endDate;
        }

        if (share.forumTitle) {
            const forumTitle = document.querySelector('#forumTitle');
            forumTitle.textContent = share.forumTitle;
            forumTitle.href = '${forumUrl}';
            forumTitle.target = '_blank';
        }

        if (!canUpdate) {
            form.querySelector('.actionable').style.display = 'none';
            document.querySelector('#finishBtn').classList.add('d-none');
            document.querySelector('#createInspectionBtn').classList.add('d-none');

            form.querySelectorAll('input, select, textarea, button').forEach(el => {
                el.disabled = true;
            });
        } else if (share.state === 'NEW') {
            setInitialMode();
        } else if (share.state === 'INITIALIZED' || share.state === 'IN_PROGRESS') {
            setWorkingMode();
        } else {
            setCompletedMode();
        }
    }

    function updateSubFamilySelect() {
        // FIXME: to change for new API v2
        const form = document.querySelector('#editForm');
        const familyId = form.querySelector('[name="familyId"]').value;

        if (familyId) {
            axios.get('/admin/families/' + familyId + '/subfamilies')
                .then((response) => {
                    const subFamily = form.querySelector('[name="subFamilyId"]');
                    subFamily.innerHTML = '';

                    appendElementToList({id: '', name: '<spring:message code="family.selection" />'}, subFamily);

                    response.data.forEach((element) => {
                        appendElementToList(element, subFamily);
                    })
                }).catch(error => showNotify(error.response.data.detail, 'danger'));
        }
    }

    function setInitialMode() {
        currentMode = 'INITIAL';

        document.querySelector('#createInspectionBtn').classList.add('d-none');

        const form = document.querySelector('#editForm');
        form.querySelector('#finishBtn').classList.add('d-none');
        form.querySelector('[name="endDate"]').parentElement.remove();
    }

    function setWorkingMode() {
        currentMode = 'WORKING';

        if (!canClose) {
            document.querySelector('#finishBtn').classList.add('d-none');
        } else {
            document.querySelector('#finishBtn').classList.remove('d-none');
        }

        document.querySelector('#createInspectionBtn').classList.remove('d-none');

        if (!canUpdate) {
            disableForm('#editForm');
        }

        const form = document.querySelector('#editForm');

        const endDate = form.querySelector('[name="endDate"]');
        endDate.parentElement.remove();

        const files = form.querySelector('[name="files"]');

        files.value = '';
        files.disabled = false;

        showFiles();
    }

    function setCompletedMode() {
        currentMode = 'COMPLETED';

        if (!canUpdate) {
            document.querySelector('#editBtn')?.remove();
            document.querySelector('#finishBtn')?.remove();
            document.querySelector('#createInspectionBtn')?.remove();

            disableForm('#editForm');
        }

        const finishBtn = document.getElementById('finishBtn');
        const createInspectionBtn = document.getElementById('createInspectionBtn');

        if (finishBtn) {
            finishBtn.classList.add('d-none');
        }

        if (createInspectionBtn) {
            createInspectionBtn.classList.add('d-none');
        }

        showFiles();
    }

    function appendElementToList(element, list) {
        const newOption = document.createElement('option');
        newOption.value = element.id;
        newOption.textContent = element.name;

        list.appendChild(newOption);
    }

    function loadDataTables() {
        let columns = ['id', 'startDate', 'endDate', 'action', 'id']
        let endpoint = '/v1/shares/no-programmed/' + share.id + '/inspections';
        let actions = [
            {
                action: 'edit',
                type: 'redirect',
                url: '/shares/no-programmed/' + share.id + '/inspections/{id}',
                permission: 'edit_no_programmed_shares'
            },
            {action: 'file-pdf', url: '/v1/shares/no-programmed/' + share.id + '/inspections/{id}/export'}
        ]
        if (share.state !== 'CLOSED') {
            actions.push({action: 'delete', permission: 'edit_no_programmed_shares'});
        }
        let expand = []
        let columnDefs = [
            {
                targets: [1, 2],
                render: function (data) {
                    return data ? parseDate(data, 'DD-MM-YYYY HH:mm') : '-';
                }
            },
            {
                targets: 3,
                render: function (data) {
                    return parseTypeToBadge(data);
                }
            }
        ]

        customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, null, columnDefs);
        dTable = createDataTable('#dTable', customDataTable, locale);
        customDataTable.setCurrentTable(dTable);
    }

    function parseTypeToBadge(data) {
        if (data === 'INTERVENTION') {
            return '<div class="badge badge-warning">' + messages.inspections.intervention + '</div>';
        } else if (data === 'DIAGNOSIS') {
            return '<span class="badge badge-success">' + messages.inspections.diagnosis + '</span>';
        } else if (data === 'FOLLOWING') {
            return '<span class="badge badge-info">' + messages.inspections.following + '</span>';
        }
    }

    function showFiles() {
        const form = document.querySelector('#editForm');
        const filesFormGroup = form.querySelector('#filesFormGroup');
        const filesAttachmentContainer = filesFormGroup.querySelector('.attachment-contianer');

        if (filesAttachmentContainer) {
            filesAttachmentContainer.remove();
        }

        const linksContainer = document.createElement('div');
        linksContainer.classList.add('attachment-contianer');

        const files = share.files;

        const selector = form.querySelector('[name="files"]');
        if (selector && share.state === 'CLOSED') {
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
                const blob = new Blob([byteNumbers], {type: 'application/octet-stream'});

                link.href = URL.createObjectURL(blob);
                link.download = file.name;
                link.textContent = file.name;
                link.classList.add('btn', 'btn-outline-primary', 'btn-xs', 'mr-1');
                link.target = '_blank';

                linksContainer.appendChild(link);

                if (share.state !== 'CLOSED') {
                    const btn = document.createElement('button');
                    btn.type = 'button';
                    btn.textContent = 'x';
                    btn.classList.add('btn', 'btn-outline-danger', 'btn-xs', 'mr-1');
                    btn.addEventListener('click', () => {
                        deleteFile(file);
                        link.remove();
                        btn.remove();
                    });

                    linksContainer.appendChild(btn);
                }
            };

            files.forEach(file => {
                downloadBase64File(file);
            });

            filesFormGroup.appendChild(linksContainer);
        }
    }

    function updateModalAction(action) {
        const modal = document.querySelector('#createModal');
        modal.querySelector('[name="action"]').value = action;

        const badgeInspection = modal.querySelector('.badge-inspection');
        badgeInspection.textContent = messages.inspections.next + messages.inspections.types[action.toLowerCase()];

        if (action === 'DIAGNOSIS') {
            badgeInspection.classList.add('badge-success');
        } else if (action === 'INTERVENTION') {
            badgeInspection.classList.add('badge-warning');
        } else if (action === 'FOLLOWING') {
            badgeInspection.classList.add('badge-info');
        }
    }

    function createInspection() {
        const form = document.querySelector('#createForm');
        const secondTechnical = form.querySelector('[name="secondTechnical"]');
        const action = form.querySelector('[name="action"]').value;

        if (!canCreateInspection(action)) {
            return;
        }

        showLoading();

        axios.post('/v1/shares/no-programmed/' + share.id + '/inspections', {
            firstTechnicalId: ${user.id},
            secondTechnicalId: secondTechnical ? secondTechnical.value : null,
            action: action,
        }).then((response) => {
            const inspection = response.data.data;
            window.location.replace(window.location.pathname + '/inspections/' + inspection.id);
        }).catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => {
                hideLoading();
                $('#createModal').modal('hide');
            });
    }

    function getNextAction(currentAction) {
        if (currentAction === 'DIAGNOSIS') {
            return 'INTERVENTION'
        } else if (currentAction === 'INTERVENTION') {
            return 'FOLLOWING'
        } else if (currentAction === 'FOLLOWING') {
            return 'DIAGNOSIS'
        }
    }

    function remove(inspectionId) {
        const alertMessage = messages.inspections.delete.alert.replace('{0}', inspectionId);
        if (confirm(alertMessage)) {

            showLoading();

            axios.delete('/v1/shares/no-programmed/' + share.id + '/inspections/' + inspectionId).then(() => {
                dTable.ajax.reload();
                const successMessage = messages.inspections.delete.success.replace('{0}', inspectionId);
                showNotify(successMessage);
            }).catch(error => showNotify(error.response.data.detail, 'danger'))
                .finally(() => hideLoading());
        }
    }

    function setReturnButtonUrl() {
        let lastPageUrl = '/shares/no-programmed';

        if (document.referrer) {
            const lastPagePath = new URL(document.referrer).pathname;

            if (/^\/shares\/no-programmed\/\d+\/inspections\/\d+$/.test(lastPagePath)) {
                lastPageUrl = sessionStorage.getItem('noProgrammedSharesFilter');
            } else {
                const queryParams = document.referrer.split('?')[1];
                lastPageUrl = lastPagePath + (queryParams ? '?' + queryParams : '');
                sessionStorage.setItem('noProgrammedSharesFilter', lastPageUrl);
            }
        }

        returnBtn.attr('href', lastPageUrl);
    }

    function deleteFile(file, btn, link) {
        const alertMessage = messages.shares.noprogrammed.files.delete.alert.replace('{0}', file.name);
        if (confirm(alertMessage)) {

            showLoading();

            axios.delete('/v1/shares/no-programmed/' + share.id + '/files/' + file.id).then(() => {
                share.files = share.files.filter(f => f.id !== file.id);
                link.remove();
                btn.remove();
                const successMessage = messages.shares.noprogrammed.files.delete.success.replace('{0}', file.name);
                showNotify(successMessage);
            }).catch(error => showNotify(error.response.data.detail, 'danger'))
                .finally(() => hideLoading());
        }
    }

    function canCreateInspection(action) {
        if (action === 'DIAGNOSIS' && !hasRole) {
            $('#hasNotRoleAlert').removeClass('d-none');
            return false;
        }

        return true;
    }

    async function getPermissions() {
        await axios.get('/provisional/shares/no-programmed/user-permissions', {
            params: {
                shareId: share.id,
                userId: userId
            }
        }).then((response) => {
            hasRole = response.data.hasRole;
            canClose = response.data.canClose;
        }).catch(error => console.log(error));
    }

    $(document).ready(async function () {
        await init();
        update(share);
        edit(share.id);
        close(share.id);
        loadDataTables();
        setReturnButtonUrl();
    });

</script>