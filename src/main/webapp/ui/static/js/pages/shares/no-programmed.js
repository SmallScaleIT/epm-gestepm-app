const endpoint = '/v1/shares/no-programmed';

function initializeDataTables() {
    let columns = ['id', 'user.name', 'project.name', 'forumTitle', 'startDate', 'endDate', 'id']
    let endpoint = '/v1/shares/no-programmed';
    let actions = [
        {
            action: 'view',
            url: '/shares/no-programmed/{id}',
            permission: 'read_no_programmed_shares'
        },
        {
            action: 'delete',
            permission: 'read_no_programmed_shares'
        }
    ]
    let expand = ['user,project']
    let filters = [];
    let orderable = [[0, 'DESC']]
    let columnsDef = [
        {
            targets: [4, 5],
            render: function (data) {
                return data ? moment(data).format('DD-MM-YYYY HH:mm') : null;
            }
        },
    ]

    customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, filters, orderable, columnsDef);
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
        $('#filterForm')[0].reset();
    }

    customDataTable.setFilters(filters);
    dTable.ajax.reload();
}

function create() {

    showLoading();

    const form = document.querySelector('#createForm');

    const projectId = form.querySelector('[name="projectId"]').value;

    axios.post(endpoint, {
        userId: userId,
        projectId: projectId
    }).then((response) => {
        const noprogrammed = response.data.data;
        window.location.replace('/shares/no-programmed/' + noprogrammed.id);
    }).catch(error => showNotify(error.response.data.detail, 'danger'))
        .finally(() => hideLoading());
}

function remove(id) {

    if (confirm(messages.shares.noprogrammed.delete.alert.replace('{0}', id))) {

        showLoading();

        axios.delete('/v1/shares/no-programmed/' + id).then(() => {
            dTable.ajax.reload(function () {
                dTable.page(dTable.page()).draw(false);
            }, false);
            showNotify(messages.shares.noprogrammed.delete.success.replace('{0}', id));
        }).catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
    }
}