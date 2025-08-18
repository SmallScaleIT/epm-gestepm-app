const endpoint = '/v1/signings/warehouse';

let currentWarehouse = null;

function initializeDataTables() {
    const columns = ['id', 'project.name', 'startedAt', 'closedAt', 'id'];

    const actions = [
        {
            action: 'view'
            , url: '/signings/warehouse/{id}'
            , permission: 'edit_warehouse_signings'
        }
    ];

    const expand = ['project'];

    const filters = [{'userIds': userId, 'current': false}];

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
        const select2 = createBasicSelect2($(select));

        if (currentWarehouse)
        {
            select2.append(new Option(currentWarehouse.project.name, currentWarehouse.project.id, true, true));
        }
    });
}

async function getCurrentWarehouse() {
    await axios.get(endpoint, {
        params: {
            userIds: userId
            , current: true
            , offset: 0
            , limit: 1
            , _expand: 'project'
        }
    }).then((response) => {
        currentWarehouse = response.data.data[0];
        updateForm();
    }).catch(error => showNotify(error.response.data.detail, 'danger'));
}

function updateForm() {

}