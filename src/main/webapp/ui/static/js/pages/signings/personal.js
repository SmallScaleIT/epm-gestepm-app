const endpoint = "/v1/signings/personal";
let currentId;
let rowIndex;

function initializeDataTables() {
    const columns = ['id', 'startDate', 'endDate', 'id'];

    const actions = [
        {
            action: 'edit'
            , permission: 'edit_personal_signings'
        }
    ];

    const expand = [];

    const filters = [{'userIds': userId}];

    const columnDefs = [
        {
            targets: [1, 2]
            , render: function(data) {
                return data ? moment(data).format('DD-MM-YYYY HH:mm') : null;
            }
        }
        , {
            targets: 3
            , render: function(data) {
                return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(data);
            }
        }
    ];

    customDataTable = new CustomDataTable(columns, endpoint, null, actions, expand, filters, columnDefs);
    dTable = createDataTable('#dTable', customDataTable, locale);
    customDataTable.setCurrentTable(dTable);
}

function edit(personalSigningId) {
    currentId = personalSigningId;

    let allData = dTable.rows().data().toArray();
    let currentRow = allData.find(item => item.id === personalSigningId);
    rowIndex = allData.indexOf(currentRow);

    const modal = document.getElementById('editPersonalSigning');
    
    if (!modal)
        return ;

    const form = document.getElementById('editPersonalSigningForm');

    if (!form)
        return ;

    let startDateField = form.querySelector('[name="startDate"]');
    let endDateField = form.querySelector('[name="endDate"]');

    if (startDateField)
        startDateField.value = currentRow.startDate ? moment(currentRow.startDate).format('YYYY-MM-DDTHH:mm') : '';

    if (endDateField)
        endDateField.value = currentRow.endDate ? moment(currentRow.endDate).format('YYYY-MM-DDTHH:mm') : '';

    $(modal).modal('show');
}

function actionEdit() {
    if (!currentId)
        return ;

    if (!isValidForm('#editPersonalSigningForm')) {
        $('#editPersonalSigningForm').addClass('was-validated');
        return ;
    }

    showLoading();

    $('#editPersonalSigningForm').removeClass('was-validated');

    const form = document.getElementById('editPersonalSigningForm');

    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    axios.patch(endpoint + '/' + currentId, data)
        .then(response => {
            dTable.row(rowIndex).data(response.data).draw();
            showNotify(messages.signings.personal.update.success.replace('{0}', currentId), 'success');
            currentId = null;
            rowIndex = null;
        })
        .catch(error => showNotify(error.response.data.detail, 'danger'))
        .finally(() => {
            hideLoading()
            $('#editPersonalSigning').modal('hide');
        });
}