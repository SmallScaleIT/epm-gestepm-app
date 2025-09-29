const endpoint = "/v1/signings/personal";

function initializeEdit(response) {
    let canUpdate = response.data.data.user.id == userId
        || isAdmin;

    const editForm = document.querySelector('#editForm');

    //editForm.querySelector('.actionable').style.display = 'none';

    editForm.querySelectorAll('input, select, textarea, button').forEach(el => {
        el.disabled = !canUpdate;
    });
}

function initializeDataTables() {
    const columns = ['id', 'startDate', 'endDate', 'id'];
    const actions = [
        {
            action: 'edit',
            permission: 'edit_personal_signings'
        },
        {
            action: 'delete',
            permission: 'edit_personal_signings'
        }
    ];
    const expand = [];
    const filters = [{'userIds': userId}];
    const columnDefs = [
        {
            targets: [1, 2],
            render: function(data) {
                return data ? moment(data).format('DD-MM-YYYY HH:mm') : null;
            }
        }
    ];

    customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, filters, columnDefs);
    dTable = createDataTable('#dTable', customDataTable, locale);
    customDataTable.setCurrentTable(dTable);
}

function edit(id) {

    const editModal = $('#editModal');
    const saveBtn = $('#saveBtn');
    const editElement = document.getElementById('editModal');
    const editForm = editElement.querySelector('#editForm');

    axios.get(endpoint + '/' + id).then((response) => {
        editModal.find('[name="startDate"]').val(response.data.data.startDate);
        editModal.find('[name="endDate"]').val(response.data.data.endDate);
        initializeEdit(response);
        editModal.modal('show');
    });

    saveBtn.off('click');

    saveBtn.click(function() {

        showLoading();

        axios.put(endpoint + '/' + id, {
            startDate: editForm.querySelector('[name="startDate"]').value,
            endDate: editForm.querySelector('[name="endDate"]').value
        }).then(() => {
            dTable.ajax.reload(function() { dTable.page(dTable.page()).draw(false); }, false);
            showNotify(messages.signings.personal.update.success.replace('{0}', id), 'success');
        }).catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => {
                hideLoading();
                editModal.modal('hide');
            });
    });
}

function remove(id) {

    if (confirm(messages.signings.personal.delete.alert)) {

        showLoading();

        axios.delete(endpoint + '/' + id).then(() => {
            dTable.ajax.reload(function() { dTable.page(dTable.page()).draw(false); }, false);
            showNotify(messages.signings.personal.delete.success.replace('{0}', id));
        }).catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
    }
}
