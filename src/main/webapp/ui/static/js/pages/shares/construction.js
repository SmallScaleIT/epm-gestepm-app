const endpoint = '/v1/shares/construction';

function initializeDataTables() {
    let columns = ['id', 'user.name', 'project.name', 'startDate', 'endDate', 'id']
    let actions = [
        {
            action: 'view',
            url: '/shares/construction/{id}',
            permission: 'read_construction_shares'
        },
        {
            action: 'file-pdf',
            url: '/v1/shares/construction/{id}/export',
            permission: 'edit_construction_shares',
            conditionGroups: [
                {
                    conditions: [
                        { key: 'endDate', value: [ undefined ], operation: '!==' }
                    ]
                }
            ]
        },
        {
            action: 'delete',
            permission: 'edit_construction_shares'
        }
    ]
    let expand = ['user,project']
    let filters = [];
    let columnsDef = [
        {
            targets: [3, 4],
            render: function (data) {
                return data ? moment(data).format('DD-MM-YYYY HH:mm') : null;
            }
        },
    ]

    customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, filters, columnsDef);
    dTable = createDataTable('#dTable', customDataTable, locale);
    customDataTable.setCurrentTable(dTable);
}

function initializeSelects() {
    const createForm = document.querySelector('#createForm');
    const createSelects = createForm.querySelectorAll('select');

    createSelects.forEach(select => {
        createBasicSelect2($(select));
    });

    const filterForm = document.querySelector('#filterForm');
    const filterSelects = filterForm.querySelectorAll('select');

    filterSelects.forEach(select => {
        createBasicSelect2($(select), 'filterForm');
    });
}

function filterByParams() {

    const queryParams = new URLSearchParams(window.location.search);

    let filters = [];

    let ids = queryParams.get('ids');
    let userIds = queryParams.get('userIds');
    let projectIds = queryParams.get('projectIds');
    let status = queryParams.get('status');
    let pageNumber = queryParams.get('pageNumber');

    const filterForm = document.querySelector('#filterForm');

    filterForm.querySelector('[name="id"]').value = ids;
    filterForm.querySelector('[name="userId"]').value = userIds;
    filterForm.querySelector('[name="projectId"]').value = projectIds;
    filterForm.querySelector('[name="status"]').value = status;

    if (ids) { filters.push({ 'ids': ids }); }
    if (userIds) { filters.push({ 'userIds': userIds }); }
    if (projectIds) { filters.push({ 'projectIds': projectIds }); }
    if (status) { filters.push({ 'status': status }); }
    if (pageNumber) { filters.push({ 'pageNumber': pageNumber }); }

    customDataTable.setFilters(filters);
}

function filter(isReset) {

    let filters = [];

    if (!isReset) {

        let form = document.querySelector('#filterForm');

        let id = form.querySelector('[name="id"]');
        let userId = form.querySelector('[name="userId"]');
        let projectId = form.querySelector('[name="projectId"]');
        let status = form.querySelector('[name="status"]');

        id.value && filters.push({'ids': id.value});
        userId.value && filters.push({'userIds': userId.value});
        projectId.value && filters.push({'projectIds': projectId.value});
        status.value && filters.push({'status': status.value});
    } else {
        resetForm('#filterForm');
    }

    customDataTable.setFilters(filters);
    dTable.order([[0]]).ajax.reload();
}

function create() {

    const createFromJQ = $('#createForm');

    if (!isValidForm('#createForm')) {
        createFromJQ.addClass('was-validated');
    } else {

        showLoading();
        createFromJQ.removeClass('was-validated');

        const form = document.querySelector('#createForm');

        const projectId = form.querySelector('[name="projectId"]').value;

        axios.post(endpoint, {
            userId: userId,
            projectId: projectId
        }).then((response) => {
            const construction = response.data.data;
            window.location.replace('/shares/construction/' + construction.id);
        }).catch(error => showError(error, 'errorModal'))
            .finally(() => hideLoading());
    }
}

function remove(id) {

    if (confirm(messages.shares.construction.delete.alert.replace('{0}', id))) {

        showLoading();

        axios.delete('/v1/shares/construction/' + id).then(() => {
            dTable.ajax.reload(function () {
                dTable.page(dTable.page()).draw(false);
            }, false);
            showNotify(messages.shares.construction.delete.success.replace('{0}', id));
        }).catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
    }
}