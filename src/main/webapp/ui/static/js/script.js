let messages;
let $ = jQuery.noConflict();

function getI18n(locale) {

	let messages;

	$.ajax({
		async: false,
		url: '/ui/static/js/i18n/' + locale + '.json',
		dataType: 'json',
		success: function (data) {
			messages = data;
		}
	});

	return messages;
}

function parseDate(date, format) {
	return moment(date).format(format)
}

function parseStatus(data) {

	if (data === 'status.pending') {
		return '<div class="badge badge-secondary">' + messages.status.pending + '</div>';
	} else if (data === 'status.approved') {
		return '<span class="badge badge-primary">' + messages.status.approved + '</span>';
	} else if (data === 'status.paid') {
		return '<span class="badge badge-success">' + messages.status.paid + '</span>';
	} else if (data === 'status.rejected') {
		return '<span class="badge badge-danger">' + messages.status.rejected + '</span>';
	}
	
	return '';
}

function showError(error, modalId) {
	error = error.response.data;

	if (!error.help || !error.help.url || !modalId)
	{
		showNotify(error.detail, 'danger');
		return ;
	}

	const modal = document.getElementById(modalId);

	if (!modal)
	{
		showNotify(error.detail, 'danger');
		return ;
	}

	const modalBody = modal.querySelector('.modal-body');

	if (!modalBody)
	{
		showNotify(error.detail, 'danger');
		return ;
	}

	const link = modal.querySelector('[data-modal-id="modal-href"]');

	if (link)
		link.href = error.help.url;

	modalBody.textContent = error.detail;

	$(modal).modal('show');
}

function showNotify(msg, type) {
	$.notify({ message: msg }, { type: type });
}

function showLoading() {
	$('.loading').show();
}

function hideLoading() {
	$('.loading').hide();
}

$(document).ready(function () {
	const locale = document.documentElement.lang;
	messages = getI18n(locale);
	hideLoading();
});

/* NEW */

function isValidForm(formId) {
	let isValid = true;
	const $form = $(formId);

	$form.find('[required]').each(function () {
		const $field = $(this);
		const value = $field.val();

		if (!value) {
			isValid = false;
			$field.addClass('is-invalid');
		} else {
			$field.removeClass('is-invalid');
		}
	});

	return isValid;
}

function disableForm(formId) {
	const form = document.querySelector(formId);
	const elements = form.querySelectorAll('input, textarea, select');
	elements.forEach(element => element.disabled = true);

	if (typeof $.fn.selectpicker === 'function') {
		const selects = form.querySelectorAll('select');
		selects.forEach(select => $(select).selectpicker('refresh'));
	}
}

function toBase64(file) {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = () => resolve(reader.result.split(",")[1]);
		reader.onerror = (error) => reject(error);
		reader.readAsDataURL(file);
	});
}

function getSigningText(type) {
	if (type === 'CONSTRUCTION_SHARES') {
		return messages.shares.construction.name;
	} else if (type === 'DISPLACEMENT_SHARES') {
		return messages.shares.displacement.name;
	} else if (type === 'INSPECTIONS') {
		return messages.inspections.name;
	} else if (type === 'MANUAL_SIGNINGS') {
		return messages.signings.manual.name;
	} else if (type === 'OFFICE_SIGNING') {
		return messages.signings.office.name;
	} else if (type === 'PERSONAL_SIGNINGS') {
		return messages.signings.personal.name;
	} else if (type === 'PROGRAMMED_SHARES') {
		return messages.shares.programmed.name;
	} else if (type === 'TELEWORKING_SIGNING') {
		return messages.signings.teleworking.name;
	} else if (type === 'WORK_SHARES') {
		return messages.shares.work.name;
	}

	return '';
}

function parseShareType(type) {
	const badgeText = getSigningText(type);

	if (type === 'CONSTRUCTION_SHARES') {
		return '<span class="badge badge-success">' + badgeText + '</span>';
	} else if (type === 'DISPLACEMENT_SHARES') {
		return '<span class="badge badge-warning">' + badgeText + '</span>';
	} else if (type === 'PROGRAMMED_SHARES') {
		return '<div class="badge badge-secondary">' + badgeText + '</div>';
	} else if (type === 'INSPECTIONS') {
		return '<span class="badge badge-primary">' + badgeText + '</span>';
	} else if (type === 'WORK_SHARES') {
		return '<span class="badge badge-info">' + badgeText + '</span>';
	}

	return '';
}

function preloadSignatures(signaturePad, canvas, signatureClass, signature) {
	if (signaturePad[signatureClass]) {
		signaturePad[signatureClass].clear();
	}

	canvas[signatureClass] = document.querySelector('.' + signatureClass + '-signature .signature-canvas');

	resizeCanvas(canvas[signatureClass]);

	signaturePad[signatureClass] = new SignaturePad(canvas[signatureClass]);
	if (typeof signature !== 'undefined' && signature !== '') {
		signaturePad[signatureClass].fromDataURL(signature);
	}
}

function resizeCanvas(canvas) {
	let ratio = Math.max(window.devicePixelRatio || 1, 1);

	if (typeof canvas !== 'undefined' && canvas) {
		canvas.width = canvas.offsetWidth * ratio;
		canvas.height = canvas.offsetHeight * ratio;
		canvas.getContext("2d").scale(ratio, ratio);
	}
}

async function parseFiles(editForm) {
	const selector = editForm.querySelector('[name="files"]');
	let filesData = [];
	if (selector && selector.files) {
		for (let i = 0; i < selector.files.length; i++) {
			const file = selector.files[i];

			filesData.push({
				name: file.name,
				content: await toBase64(file)
			});
		}
	}
	return filesData ? filesData : null;
}

function resetForm(formSelector) {
	const form = document.querySelector(formSelector);
	if (!form) return;

	form.reset();

	const selects = form.querySelectorAll('select');
	selects.forEach(select => {
		if ($(select).hasClass('select2-hidden-accessible')) {
			$(select).val(null).trigger('change');
		}
	});

	const checkboxes = form.querySelectorAll('input[type="checkbox"]');
	checkboxes.forEach(cb => cb.checked = false);
}