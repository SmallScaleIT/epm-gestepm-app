<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form id="editForm" class="needs-validation">
    <div class="row">
        <div class="col-sm-12 col-md-4">
            <div class="form-group mb-1">
                <label class="col-form-label w-100"><spring:message code="project"/>
                    <input type="text" class="form-control mt-1" value="${projectName}" disabled/>
                </label>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-12">
            <div class="form-group mb-1">
                <spring:message code="select.placeholder" var="placeholder"/>
                <label class="col-form-label w-100"><spring:message code="description"/>
                    <textarea class="form-control mt-1" placeholder="${placeholder}" name="description" rows="4"
                              style="resize: none" value="${workshopSigning.description}"
                              required>${workshopSigning.description}</textarea>
                </label>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12 col-md-6">
            <div class="form-group mb-1">
                <label class="col-form-label w-100"><spring:message code="start.date"/>
                    <input type="datetime-local" name="startedAt" class="form-control mt-1"
                           value="${workshopSigning.startedAt}" />
                </label>
            </div>
        </div>
        <div class="col-sm-12 col-md-6" id="endDateBox">
            <div class="form-group mb-1">
                <label class="col-form-label w-100"><spring:message code="end.date"/>
                    <input type="datetime-local" name="closedAt" class="form-control mt-1"
                           value="${workshopSigning.closedAt}" />
                </label>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col text-right">
            <button id="editBtn" type="button" class="btn btn-standard btn-sm movile-full">
                <spring:message code="save"/>
            </button>
        </div>
    </div>
</form>

<script>
    async function actionSave() {
        showLoading();

        const editForm = document.getElementById('editForm');

        const descriptionField = editForm.querySelector('[name="description"]');
        let description = null;

        if (descriptionField !== null && descriptionField !== undefined)
            description = descriptionField.value;

        axios.patch('/v1' + window.location.pathname, {
            'description': description
            , 'startedAt': editForm.querySelector('[name="startedAt"]')?.value
            , 'closedAt': editForm.querySelector('[name="closedAt"]')?.value
        })
            .then((response) => {
                const workshop = response.data.data;
                window.location.replace('/signings/warehouse/' + workshop.warehouse.id);
            })
            .catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
    }

    function save() {
        const editBtn = document.getElementById('editBtn');
        const editBtnJQ = $(editBtn);

        editBtnJQ.click(async () => await actionSave());
    }

    function update(workshop) {
        const descriptionField = document.querySelector('[name="description"]');
        const startAtField = document.querySelector('[name="startedAt"]');
        const closedAtField = document.querySelector('[name="closedAt"]');

        if (descriptionField && workshop.description)
            descriptionField.value = workshop.description;

        if (startAtField && workshop.startAt)
            startAtField.value = workshop.startAt;

        if (closedAtField && workshop.closedAt)
            closedAtField.value = workshop.closedAt;
    }

    async function initialize(canUpdate, isSigningFinished) {
        const editForm = document.getElementById('editForm');

        if (!canUpdate) {
            editForm.querySelector('.actionable').style.display = 'none';

            editForm.querySelectorAll('input, select, textarea, button').forEach(el => {
                el.disabled = true;
            });
        }
    }
</script>