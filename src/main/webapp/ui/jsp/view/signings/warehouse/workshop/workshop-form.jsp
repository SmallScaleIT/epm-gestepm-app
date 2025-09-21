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
                           value="${workshopSigning.startedAt}" disabled/>
                </label>
            </div>
        </div>
        <div class="col-sm-12 col-md-6" id="endDateBox">
            <div class="form-group mb-1">
                <label class="col-form-label w-100"><spring:message code="end.date"/>
                    <input type="datetime-local" name="closedAt" class="form-control mt-1"
                           value="${workshopSigning.closedAt}" disabled/>
                </label>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col text-right">
            <button id="editBtn" type="button" class="btn btn-danger btn-sm movile-full">
                <spring:message code="finish"/>
            </button>
        </div>
    </div>
</form>

<script>
    async function actionSave(finalize) {
        showLoading();

        const descriptionField = document.querySelector('[name="description"]');
        let description = null;

        if (descriptionField !== null && descriptionField !== undefined)
            description = descriptionField.value;

        axios.patch('/v1' + window.location.pathname, {
            'description': description
            , 'finalize': finalize
        })
            .then((response) => {
                const workshop = response.data.data;

                if (finalize)
                    window.location.replace('/signings/warehouse/' + workshop.warehouse.id);
                else
                    update(workshop);
            })
            .catch(error => showNotify(error.response.data.detail, 'danger'))
            .finally(() => hideLoading());
    }

    function save() {
        const editBtn = document.getElementById('editBtn');
        const editBtnJQ = $(editBtn);

        editBtnJQ.click(async () => await actionSave(true));
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

    async function initialize(canUpdate) {
        const closedAtField = document.querySelector('[name="closedAt"]');

        if (closedAtField && closedAtField.value) {
            const editBtn = document.getElementById('editBtn');

            editBtn.classList.add('d-none');

            disableForm('#editForm');
        }

        if (canUpdate) {
            const editForm = document.querySelector('#editForm');

            //editForm.querySelector('.actionable').classList.remove('d-none');

            editForm.querySelector('[name="startedAt"]').disabled = false;
            editForm.querySelector('[name="closedAt"]').disabled = false;
        }
    }
</script>