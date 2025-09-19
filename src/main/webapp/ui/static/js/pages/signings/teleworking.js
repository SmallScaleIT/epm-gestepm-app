const endpoint = '/v1/signings/teleworking';

let currentTeleworking = null;

function initializeDataTables() {
    const columns = ['id', 'project.name', 'startedAt', 'closedAt', 'id']
    const actions = [
        {
            action: 'view',
            url: '/signings/teleworking/{id}',
            permission: 'edit_teleworking_signings'
        }
    ]
    const expand = ['project']
    const filters = [{ 'userIds': userId, 'current': false }]
    const columnDefs = [
        {
            targets: [2, 3],
            render: function (data) {
                return data ? moment(data).format('DD-MM-YYYY HH:mm') : null;
            }
        }
    ]

    customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, filters, columnDefs);
    dTable = createDataTable('#dTable', customDataTable, locale);
    customDataTable.setCurrentTable(dTable);
}

function initializeSelects() {
    const form = document.querySelector('#form');
    const selects = form.querySelectorAll('select');

    selects.forEach(select => {
        // TODO: change to API project
        // const select2 = createSelect2($(select), endpoint, { '_expand': 'project' }, 'project.name');
        const select2 = createBasicSelect2($(select));

        if (currentTeleworking) {
            select2.append(new Option(currentTeleworking.project.name, currentTeleworking.project.id, true, true));
        }
    });
}

async function getCurrentTeleworking() {
    await axios.get(endpoint, {
        params: {
            userIds: userId,
            current: true,
            offset: 0,
            limit: 1,
            _expand: 'project'
        }
    }).then((response) => {
        currentTeleworking = response.data.data[0];
        updateForm();
    }).catch(error => showNotify(error.response.data.detail, 'danger'));
}

function updateForm() {
    const hasCurrent = currentTeleworking !== undefined;
    const form = document.querySelector('#form');

    if (hasCurrent) {
        const startedAt = moment(currentTeleworking.startedAt).format('DD-MM-YYYY HH:mm');
        const button = form.querySelector('[type="button"]');

        form.querySelector('[name="projectId"]').disabled = true;
        form.querySelector('.start-date-info').textContent = messages.signings.teleworking.startDate.replace('{0}', startedAt);
        button.textContent = messages.actions.finish;
        button.classList.remove('btn-default');
        button.classList.add('btn-danger');
    } else {
        form.querySelector('.start-date-info-row').remove();
        form.querySelector('#form-page-header').classList.add('d-md-flex', 'align-items-center');
    }
}

function submitTeleworking() {
    showLoading();

    const form = document.querySelector('#form');
    const location = form.querySelector('[name="geolocation"]').value;

    if (currentTeleworking) {
        endTeleworking(location);
    } else {
        const projectId = form.querySelector('[name="projectId"]').value;

        if (projectId) {
            startTeleworking(projectId, location);
        } else {
            showNotify(messages.projects.empty, 'danger')
        }
    }

    hideLoading();
}

function startTeleworking(projectId, location) {
    axios.post(endpoint, {
        userId: userId,
        projectId: projectId,
        startedLocation: location
    }).then(() => {
        window.location.reload();
    }).catch(error => showError(error, 'errorModal'));
}

function endTeleworking(location) {
    axios.patch(endpoint + '/' + currentTeleworking.id, {
        closedLocation: location
    }).then(() => {
        window.location.reload();
    }).catch(error => showNotify(error.response.data.detail, 'danger'));
}
