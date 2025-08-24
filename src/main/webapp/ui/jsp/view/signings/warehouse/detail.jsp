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
                    <div class="col-sm-12 col-md-4">
                        <div class="form-group mb-1">
                            <label class="col-form-label w-100"><spring:message code="end.date"/>
                                <input type="datetime-local" name="closedAt" class="form-control mt-1" value="${warehouseSigning.closedAt}" disabled />
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row actionable d-none">
                    <div class="col text-right">
                        <button id="finishBtn" type="button" class="btn btn-danger btn-sm"><spring:message code="finish"/></button></button>
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

<div id="createModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">
                    <h5><spring:message code="warehousesigning"/></h5>
                </div>
            </div>

            <div class="modal-body">
                <form id="createForm">
                    <div class="row">
                        <div class="col">
                            <div class="form-group">
                                <label class="col-form-label w-100"><spring:message code="userId"/>
                                    <input type="text" class="form-control mt-1" value="${user.name}" disabled />
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
    let locale = '${locale}';
    let returnBtn = document.getElementById('returnBtn');
    let returnBtnJQ = $(returnBtn);

    let warehouseSigning;

    let hasRole;
    let canClose;

    let userId = ${user.id};

    async function init() {
        const editForm = document.getElementById('editForm');
        const editFormJQ = $(editForm);

        const warehouseId = getWarehouseId();

        await setWarehouseEndDate(warehouseId);
    }

    async function setWarehouseEndDate(id) {
        warehouseSigning = await getWarehouse(id);

        const closeAtEditor = document.querySelector('[name="closedAt"]');

        if (closeAtEditor)
        {
            if (warehouseSigning.closedAt !== null && warehouseSigning.closedAt !== undefined)
            {
                $(closeAtEditor).removeClass('d-none');
                closeAtEditor.value = warehouseSigning.closedAt;
            }
            else
            {
                $(closeAtEditor).addClass('d-none');
                closeAtEditor.value = null;
            }
        }
    }

    function close(id) {
        const finishBtn = document.getElementById('finishBtn');
        const finishBtnJQ = $(finishBtn);

        finishBtnJQ.click(async () => {
            showLoading();

            axios.patch('/v1/signings/warehouse/' + id, {})
            .then(response => {
                setWarehouseEndDate(response.data.data.id);
                showNotify(messages.signings.warehouse.update.success);
            })
            .catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
        });
    }

    function setWorkingMode() {
        const createWorkshopBtn = document.getElementById('createWorkshopBtn');
                const finishBtn = document.querySelector('.actionable');


        if (finishBtn && warehouseSigning.workshopSignings.length > 0)
            finishBtn.classList.remove('d-none');
        else if (finishBtn && warehouseSigning.workshopSignings.length == 0)
            finishBtn.classList.add('d-none');

        if (createWorkshopBtn)
            createWorkshopBtn.classList.remove('d-none');

        disableForm('#editForm');
    }

    function setCompletedMode() {
        const createWorkshopBtn = document.getElementById('createWorkshopBtn');
        const finishBtn = document.querySelector('.actionable');

        if (finishBtn)
            finishBtn.classList.add('d-none');

        if (createWorkshopBtn)
            createWorkshopBtn.classList.add('d-none');

        disableForm('#editForm');
    }

    function loadDataTables() {
        let columns = ['id', 'startedAt', 'closedAt', 'id'];

        let endpoint = '/v1/signings/warehouse/' + warehouseSigning.id + "/workshop-signings";

        let actions = [{
            action: 'edit'
            , type: 'redirect'
            , url: '/signings/warehouse/' + warehouseSigning.id + '/workshop-signings/{id}'
            , permission: 'edit_warehouse-signing'
        }];

        if (warehouseSigning.closedAt === null)
            actions.push({action: 'delete', permission: 'edit_warehouse_signing'});

        let expand = [];

        let columnDefs = [
            {
                targets: [1, 2],
                render: function (data) {
                    return data ? parseDate(data, 'DD-MM-YYYY HH:mm') : '-';
                }
            }
        ];

        customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, null, columnDefs);
        dTable = createDataTable('#dTable', customDataTable, locale);
        customDataTable.setCurrentTable(dTable);
    }

    function createWorkshop() {
        const createForm = document.getElementById('createForm');
        const createFormJQ = $(createForm);

        const projectEditor = document.querySelector('[name="projectId"]');

        const projectId = null;

        if (projectEditor)
            projectId = projectEditor.value;

        showLoading();

        axios.post('/v1/signings/warehouse/' + warehouseSigning.id + "/workshop-signings", {
            'projectId': projectId
            ,'userId': ${user.id}
        })
        .then((response) => {
            const workshop = response.data.data;
            window.location.replace(window.location.pathname + '/workshop-signings/' + workshop.id);
        })
        .catch(error => showNotify(error.response.data.detail, 'danger'))
        .finally(() => {
            hideLoading();
            $('#createModal').modal('hide');
        });
    }

    function remove(id) {
        const alertMessage = messages.signings.warehouse.delete.alert.replace('{0}', inspectionId);

        if (!confirm(alertMessage))
            return ;

        showLoading();

        axios.delete('/v1/signings/warehouse/' + warehouseSigning.id + "/workshop-signings/" + id)
        .then(() => {
            dTable.ajax.reload();
            const successMessage = messages.signings.warehouse.delete.success.replace('{0}', inspectionId);
            showNotify(successMessage);
        })
        .catch(error => showNotify(error.response.data.detail, 'danger'))
        .finally(() => hideLoading());
    }

    async function getWarehouse(id) {
        const response = await axios.get('/v1/signings/warehouse/' + id, {
            locale: locale
            , _expand: "user,project"
        });

        return response.data.data;
    }

    function getWarehouseId() {
        const currentUrl = window.location.href;
        const elements = currentUrl.split('/');

        if (elements.length === 0)
            return "0";

        return elements[elements.length - 1];
    }

    function setMode() {
        if (warehouseSigning.closedAt !== null && warehouseSigning.closedAt !== undefined)
            setCompletedMode();
        else
            setWorkingMode();
    }

    function setReturnButtonUrl() {
        let lastPageUrl = '/signings/warehouse';

        if (document.referrer) {
            const lastPagePath = new URL(document.referrer).pathname;

            if (/^\/signings\/warehouse\/\d+\/workshop-signings\/\d+$/.test(lastPagePath)) {
                lastPageUrl = sessionStorage.getItem('warehouseSigningFilter');
            } else {
                const queryParams = document.referrer.split('?')[1];
                lastPageUrl = lastPagePath + (queryParams ? '?' + queryParams : '');
                sessionStorage.setItem('warehouseSigningFilter', lastPageUrl);
            }
        }

        returnBtnJQ.attr('href', lastPageUrl);
    }

    $(document).ready(async () => {
        await init();
        close(warehouseSigning.id);
        setMode();
        loadDataTables();
        setReturnButtonUrl();
    });
</script>
