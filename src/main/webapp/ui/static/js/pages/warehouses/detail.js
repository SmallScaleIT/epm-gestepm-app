let returnBtn = document.getElementById('returnBtn');
let returnBtnJQ = $(returnBtn);

let warehouseSigning;

$(document).ready(async () => {
    await init();
    close(warehouseSigning.id);
    setMode();
    loadDataTables();
    setReturnButtonUrl();
});

async function init() {
    const warehouseId = getWarehouseId();

    warehouseSigning = await getWarehouse(warehouseId);

    initializeSelects();
}

function initializeSelects() {
    const form = document.getElementById('createForm');
    const selects = form.querySelectorAll('select');

    selects.forEach(select => {
        createBootstrapSelect2($(select));
    });
}

function setEndDate() {
    const endDateEditor = document.querySelector('[name="closedAt"]');

    if (endDateEditor && warehouseSigning && warehouseSigning.closedAt)
        endDateEditor.value = warehouseSigning.closedAt;
}

function close(id) {
    const finishBtn = document.getElementById('finishBtn');
    const finishBtnJQ = $(finishBtn);

    finishBtnJQ.click(async () => {
        showLoading();

        axios.patch('/v1/signings/warehouse/' + id, {})
            .then(async response => {
                const warehouseId = response.data.data.id;
                warehouseSigning = await getWarehouse(warehouseId);
                setEndDate();
                setMode();
                dTable.ajax.reload();
                const successMessage = messages.signings.warehouse.update.success.replace('{0}', id);
                showNotify(successMessage);
            })
            .catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => {
                hideLoading();
            });
    });
}

function setWorkingMode() {
    const createWorkshopBtn = document.getElementById('createWorkshopBtn');

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
    let columns = ['id', 'project.name', 'startedAt', 'closedAt', 'description', 'id'];
    let endpoint = '/v1/signings/warehouse/' + warehouseSigning.id + "/workshop-signings";

    let actions = [{
        action: 'edit'
        , type: 'redirect'
        , url: '/signings/warehouse/' + warehouseSigning.id + '/workshop-signings/{id}'
        , permission: 'edit_warehouse-signing'
    }];

    if (!warehouseSigning.closedAt)
        actions.push({action: 'delete', permission: 'edit_warehouse_signings'});

    let expand = ['project'];

    let columnDefs = [
        {
            targets: [2, 3],
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
    const createFromJQ = $('#createForm');

    if (!isValidForm('#createForm')) {
        createFromJQ.addClass('was-validated');
    } else {

        showLoading();
        createFromJQ.removeClass('was-validated');

        const form = document.querySelector('#createForm');

        axios.post('/v1/signings/warehouse/' + warehouseSigning.id + "/workshop-signings", {
            projectId: form.querySelector('[name="projectId"]').value,
            userId: userId
    }).then((response) => {
            const workshop = response.data.data;
            window.location.replace(window.location.pathname + '/workshop-signings/' + workshop.id);
        })
            .catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => {
                hideLoading();
                $('#createModal').modal('hide');
            });
    }
}

function remove(id) {
    const alertMessage = messages.signings.workshop.delete.alert.replace('{0}', id);

    if (!confirm(alertMessage))
        return;

    showLoading();

    axios.delete('/v1/signings/warehouse/' + warehouseSigning.id + "/workshop-signings/" + id)
        .then(() => {
            dTable.ajax.reload();
            const successMessage = messages.signings.workshop.delete.success.replace('{0}', id);
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
