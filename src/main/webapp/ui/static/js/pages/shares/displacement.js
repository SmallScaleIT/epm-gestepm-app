function create() {

	const createModal = $('#createModal');
	const createFromJQ = $('#createForm');
	const createElement = document.getElementById('createModal');
	const createForm = createElement.querySelector('#createForm');

	if (!isValidForm('#createForm')) {
		createFromJQ.addClass('was-validated');
	} else {

		showLoading();
		createFromJQ.removeClass('was-validated');

		axios.post('/v1/shares/displacement', {
			userId: userId,
			projectId: createForm.querySelector('[name="projectId"]').value,
			description: createForm.querySelector('[name="description"]').value,
			startDate:  createForm.querySelector('[name="startDate"]').value,
			endDate:  createForm.querySelector('[name="endDate"]').value,
			observations:  createForm.querySelector('[name="observations"]').value
		}).then(() => {
			createFromJQ[0].reset();
			dTable.ajax.reload(function() { dTable.page(dTable.page()).draw(false); }, false);
			showNotify(messages.shares.displacement.create.success);
		}).catch(error => showError(error, 'errorModal'))
			.finally(() => {
				hideLoading();
				createModal.modal('hide');
			});
	}
}

function remove(id) {

	if (confirm(messages.shares.displacement.delete.alert.replace('{0}', id))) {

		showLoading();

		axios.delete('/v1/shares/displacement/' + id).then(() => {
			dTable.ajax.reload(function() { dTable.page(dTable.page()).draw(false); }, false);
			showNotify(messages.shares.displacement.delete.success.replace('{0}', id));
		}).catch(error => showNotify(error.response.data.detail, 'danger'))
			.finally(() => hideLoading());
	}
}