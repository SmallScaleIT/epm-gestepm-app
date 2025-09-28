const endpoint = '/v1/signings/warehouse';

function initializeDataTables() {
    const columns = ['id', 'project.name', 'startedAt', 'closedAt', 'id'];

    const actions = [
        {
            action: 'view'
            , url: '/signings/warehouse/{id}'
            , permission: 'edit_warehouse_signings'
        },
        {
            action: 'delete',
            permission: 'edit_warehouse_signings'
        }
    ];

    const expand = ['project'];

    const filters = [{'userIds': userId}];

    const columnDefs = [
        {
            targets: [2, 3]
            , render: function(data) {
                return data ? moment(data).format('DD-MM-YYYY HH:mm') : null;
            }
        }
        , {
            targets: 4
            , render: function(data) {
                return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(data);
            }
        }
    ];

    customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, filters, columnDefs);
    dTable = createDataTable('#dTable', customDataTable, locale);
    customDataTable.setCurrentTable(dTable);
}

function initializeSelects() {
    const form = document.getElementById('form');
    const selects = form.querySelectorAll('select');

    selects.forEach(select => {
        createBasicSelect2($(select));
    });
}

function submitWarehouse() {

    const form = document.getElementById('form');

    const formJQ = $(form);

    if (!isValidForm('#form'))
        formJQ.addClass('was-validated');
    else
    {
        showLoading();

        formJQ.removeClass('was-validated');

        const projectId = form.querySelector('[name="projectId"]').value;

        axios.post(endpoint, {'projectId': projectId, 'userId': userId})
        .then(response => {
            const warehouse = response.data.data;
            window.location.replace('/signings/warehouse/' + warehouse.id);
        })
        .catch(error => showError(error, 'errorModal'))
        .finally(() => hideLoading());
    }

    hideLoading();
}

function remove(id) {
    if (!confirm(messages.signings.warehouse.delete.alert.replace('{0}', id)))
        return ;

    showLoading();

    axios.delete('/v1/signings/warehouse/' + id)
    .then(() => {
        dTable.ajax.reload(function () {
            dTable.page(dTable.page()).draw(false);
        }, false);
        showNotify(messages.signings.warehouse.delete.success.replace('{0}', id));
    })
    .catch(error => showNotify(error.response.data.detail, 'danger'))
    .finally(() => hideLoading());

    hideLoading();
}