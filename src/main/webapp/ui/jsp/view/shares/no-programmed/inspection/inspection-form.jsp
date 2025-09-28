<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .clearSignatureButton {
        color: #333;
        font-weight: bold;
        line-height: 2.25em;
        text-decoration: underline;
        border: 0;
        background: transparent;
        font-size: 0.75em;
    }

    .signature-pad {
        position: relative;
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-orient: vertical;
        -webkit-box-direction: normal;
        -ms-flex-direction: column;
        flex-direction: column;
        font-size: 10px;
        width: 100%;
        height: 100%;
        max-height: 200px;
        min-height: 160px;
        border: 1px solid #ced4da;
        background-color: #fff;
        border-radius: 4px;
        padding: 16px;
    }

    .signature-pad::before, .signature-pad::after {
        position: absolute;
        z-index: -1;
        content: "";
        width: 40%;
        height: 10px;
        bottom: 10px;
        background: transparent;
    }

    .signature-pad::before {
        left: 20px;
        -webkit-transform: skew(-3deg) rotate(-3deg);
        transform: skew(-3deg) rotate(-3deg);
    }

    .signature-pad::after {
        right: 20px;
        -webkit-transform: skew(3deg) rotate(3deg);
        transform: skew(3deg) rotate(3deg);
    }

    .signature-pad--body {
        position: relative;
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
        border: 1px solid lightgray;
    }

    .signature-pad--body canvas {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        border-radius: 4px;
        box-shadow: 0 0 5px rgba(0, 0, 0, 0.02) inset;
    }

    .signature-pad--footer {
        color: gray;
        text-align: center;
        font-size: 1.2em;
        margin-top: 8px;
    }

    .signature-pad--actions {
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-pack: justify;
        -ms-flex-pack: justify;
        justify-content: space-between;
        margin-top: -20px;
    }
</style>

<div class="row">
    <div class="col">
        <div id="hasNotRoleAlert" class="badge badge-danger mb-1 d-none"><spring:message code="share.detail.init.diag.role" /></div>
    </div>
</div>

<form id="editForm" class="needs-validation">
    <div class="row">
        <div class="col-12 col-lg-6">
            <div class="form-group mb-1">
                <label class="col-form-label w-100"><spring:message code="start.date"/>
                    <input type="datetime-local" name="startDate" class="form-control mt-1"/>
                </label>
            </div>
        </div>
        <div class="col-12 col-lg-6">
            <div class="form-group mb-1">
                <label class="col-form-label w-100"><spring:message code="end.date"/>
                    <input type="datetime-local" name="endDate" class="form-control mt-1"/>
                </label>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-6">
            <div class="form-group mb-1">
                <spring:message code="shares.intervention.create.intervention.desc.placeholder" var="placeholder" />
                <label class="col-form-label w-100"><spring:message code="shares.intervention.create.intervention.desc" />
                    <textarea class="form-control mt-1" placeholder="${placeholder}" name="description" rows="4" style="resize: none" required></textarea>
                </label>
            </div>

            <div class="form-group mb-1">
                <label class="col-form-label w-100">
                    <spring:message code="shares.intervention.create.equipment.hours" />
                    <input name="equipmentHours" type="number" class="form-control mt-1" />
                </label>
            </div>

            <div class="form-group mb-1">
                <label class="col-form-label w-100">
                    <spring:message code="shares.intervention.create.technical.1" />
                    <input name="firstTechnical" type="text" class="form-control mt-1" disabled />
                </label>
            </div>

            <div class="form-group mb-1">
                <label class="col-form-label w-100">
                    <spring:message code="shares.intervention.create.technical.2" />
                    <input name="secondTechnical" type="text" class="form-control mt-1" disabled />
                </label>
            </div>
        </div>

        <div class="col-md-6">
            <div class="form-group mb-0">
                <div id="filesFormGroup" class="form-group">
                    <label class="col-form-label w-100">
                        <spring:message code="shares.intervention.create.intervention.files" />
                        <input type="file" class="form-control mt-1" name="files" accept=".jpg, .jpeg, .png" multiple>
                    </label>
                </div>
            </div>

            <div class="visibility-id">
                <div class="form-group">
                    <div id="materialsFileFormGroup" class="form-group">
                        <label class="col-form-label w-100">
                            <spring:message code="shares.intervention.create.intervention.material.files" />
                            <input type="file" class="form-control mt-1" name="materialsFile" accept=".xlsx, .xls, .csv, .pdf">
                        </label>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="visibility-id">
        <div class="row mt-2">
            <div class="col text-right">
                <button type="button" class="btn btn-standard-outline btn-sm movile-full" onclick="edit(null)"><spring:message code="shares.intervention.detail.add.material.btn" /></button>
            </div>
        </div>

        <div class="row mb-2">
            <div class="col">
                <div class="table-responsive">
                    <table id="materialsTable" class="table table-striped table-borderer dataTable w-100">
                        <thead>
                            <tr>
                                <th>Id</th>
                                <th><spring:message code="shares.intervention.create.materials.desc" /></th>
                                <th><spring:message code="shares.intervention.create.materials.uds" /></th>
                                <th><spring:message code="shares.intervention.create.materials.ref" /></th>
                                <th><spring:message code="shares.displacement.table.actions" /></th>
                            </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>

        <div class="row mb-2">
            <div class="col-sm-12 col-md-6 mt-2">
                <div class="signature-pad customer-signature">
                    <div class="signature-pad--body">
                        <canvas class="signature-canvas"></canvas>
                    </div>
                    <div class="signature-pad--footer">
                        <div class="description"><spring:message code="shares.intervention.create.signature" /></div>
                        <div class="signature-pad--actions">
                            <div>
                                <button class="clearSignatureButton" type="button"><i class="fa fa-redo"></i></button>
                            </div>
                            <div></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-sm-12 col-md-6 mt-2">
                <div class="signature-pad operator-signature">
                    <div class="signature-pad--body">
                        <canvas class="signature-canvas"></canvas>
                    </div>
                    <div class="signature-pad--footer">
                        <div class="description"><spring:message code="shares.intervention.create.signature.two" /></div>
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

        <div class="row">
            <div class="col">
                <div class="form-group">
                    <label class="col-form-label w-100"><input type="text" class="form-control" placeholder="<spring:message code="shares.intervention.create.client.name" />" name="clientName" /></label>
                </div>
            </div>
            <div class="col text-right">
                <div class="form-group">
                    <label><input type="checkbox" name="clientNotif"><spring:message code="shares.intervention.create.share.client.notif.mail" /></label>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col text-right">
            <button id="editBtn" type="button" class="btn btn-standard btn-sm movile-full"><spring:message code="save" /></button>
        </div>
    </div>
</form>

<jsp:include page="material-modal.jsp" />

<script>
    let materialsModified = false;
    let hasRole = ${hasRole};

    let canUpdate = ${canUpdate};

    let signatures = { customer: null, operator: null }
    let canvas = { customer: null, operator: null }

    function save() {
        const editBtn = $('#editBtn');
        const editForm = document.querySelector('#editForm');

        editBtn.click(async () => {
            showLoading();

            const description = editForm.querySelector('[name="description"]');
            const clientName = editForm.querySelector('[name="clientName"]');
            const equipmentHours = editForm.querySelector('[name="equipmentHours"]');
            const notify = editForm.querySelector('[name="clientNotif"]');
            const startDate = editForm.querySelector('[name="startDate"]');
            const endDate = editForm.querySelector('[name="endDate"]');

            const materials = materialsDataTable.rows().data().toArray().map(row => ({ ...row, id: row.id < 0 ? null : row.id}));
            const materialsFile = await parseMaterialsFile(editForm);
            const files = await parseFiles(editForm);

            axios.patch('/v1' + window.location.pathname, {
                userId: ${user.id},
                description: description ? description.value : null,
                signature: !signatures.customer.isEmpty() ? signatures.customer.toDataURL() : null,
                operatorSignature: !signatures.operator.isEmpty() ? signatures.operator.toDataURL() : null,
                clientName: clientName ? clientName?.value?.trim() : null,
                materials: materialsModified ? materials : null,
                materialsFile: materialsFile,
                equipmentHours: equipmentHours ? equipmentHours.value : null,
                files: files,
                notify: notify.checked,
                startDate: startDate?.value,
                endDate: endDate?.value
            }).then((response) => {
                const inspection = response.data.data;
                window.location.replace('/shares/no-programmed/' + inspection.share.id);
            }).catch(error => showNotify(error.response.data.detail, 'danger'))
                .finally(() => hideLoading());
        })
    }

    function update() {
        const editForm = document.querySelector('#editForm');
        const editBtn = editForm.querySelector('#editBtn');

        if (inspection.description) {
            editForm.querySelector('[name="description"]').value = inspection.description;
        }

        if (inspection.equipmentHours) {
            editForm.querySelector('[name="equipmentHours"]').value = inspection.equipmentHours;
        }

        if (inspection.firstTechnical && inspection.firstTechnical.id) {
            editForm.querySelector('[name="firstTechnical"]').value = inspection.firstTechnical.name + ' ' + inspection.firstTechnical.surnames;
        }

        if (inspection.secondTechnical && inspection.secondTechnical.id) {
            editForm.querySelector('[name="secondTechnical"]').value = inspection.secondTechnical.name + ' ' + inspection.secondTechnical.surnames;
        }

        if (inspection.clientName) {
            editForm.querySelector('[name="clientName"]').value = inspection.clientName;
        }

        if (inspection.startDate)
        {
            const startDatePicker = editForm.querySelector('[name="startDate"]');

            if (startDatePicker)
            {
                startDatePicker.value = inspection.startDate;
            }
        }

        if (inspection.endDate)
        {
            const endDatePicker = editForm.querySelector('[name="endDate"]');

            if (endDatePicker)
            {
                endDatePicker.value = inspection.endDate;
            }
        }

        let materialsFile = null;
        if (inspection.materialsFile) {
            materialsFile = {
                name: inspection.materialsFileName,
                content: inspection.materialsFile
            }
        }

        if (inspection.action === 'FOLLOWING') {
            $('.visibility-id').hide();
        }

        if (!canUpdate)
        {
            editBtn.remove();
            disableForm('#editForm');
        }
        else if (share.state !== 'CLOSED') {
            updateEditButton(editBtn);
        }

        loadFiles(inspection.files, materialsFile);
        loadMaterialsDataTable();
        loadSignatures(inspection.signature, inspection.operatorSignature);
    }

    function loadFiles(files, materialsFile) {
        if (files && files.length) {
            showFiles(files, 'files', '#filesFormGroup');
        }

        if (materialsFile) {
            showFiles([materialsFile], 'materialsFile', '#materialsFileFormGroup');
        }
    }

    function loadSignatures(customerSignature, operatorSignature) {
        preloadSignatures(signatures, canvas, 'customer', customerSignature);
        preloadSignatures(signatures, canvas, 'operator', operatorSignature);

        $('.customer-signature .clearSignatureButton').click(function () { signatures.customer.clear(); });
        $('.operator-signature .clearSignatureButton').click(function () { signatures.operator.clear(); });
    }

    function loadMaterialsDataTable() {
        let columns = ['id', 'description', 'units', 'reference', 'id']
        let actions = [
            { action: 'edit', permission: 'edit_inspections' },
            { action: 'delete', permission: 'edit_inspections' }
        ]
        let columnDefs = [
            {
                targets: 0,
                visible: false
            }
        ]

        customDataTable = new CustomDataTable(columns, null, inspection.materials, actions, null,  null, columnDefs, null, 'Material');
        materialsDataTable = createSimpleDataTable('#materialsTable', customDataTable, locale);
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
        if (selector && (share.state === 'CLOSED' || inputName === 'materialsFile')) {
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

                if (share.state !== 'CLOSED' && inputName === 'files') {
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

    async function parseMaterialsFile(editForm) {
        const selector = editForm.querySelector('[name="materialsFile"]');
        if (selector && selector.files && selector.files.length > 0) {
            return {
                name: selector.files[0].name,
                content: await toBase64(selector.files[0])
            }
        }
        return null;
    }

    function deleteFile(file, btn, link) {
        const alertMessage = messages.inspections.files.delete.alert.replace('{0}', file.name);
        if (confirm(alertMessage)) {

            showLoading();

            axios.delete('/v1/shares/no-programmed/0/inspections/' + inspection.id + '/files/' + file.id).then(() => {
                inspection.files = inspection.files.filter(i => i.id !== file.id)
                const successMessage = messages.inspections.files.delete.success.replace('{0}', file.name);
                link.remove();
                btn.remove();
                showNotify(successMessage);
            }).catch(error => showNotify(error.response.data.detail, 'danger'))
                .finally(() => hideLoading());
        }
    }

    function updateEditButton(editBtn) {
        const currentAction = inspection.action;

        if (currentAction === 'DIAGNOSIS' && !hasRole) {
            editBtn.disabled = true;
            $('#hasNotRoleAlert').removeClass('d-none');
        }
    }

</script>