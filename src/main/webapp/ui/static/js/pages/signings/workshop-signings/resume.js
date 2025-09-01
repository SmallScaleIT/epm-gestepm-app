
function generateResume() {
    const generateForm = document.getElementById('generateForm');
    const generateFormJQ = $(generateForm);

    if (!isValidForm('#generateForm'))
    {
        generateFormJQ.addClass('was-validated');
        return ;
    }

    generateFormJQ.removeClass('was-validated');

    const startedAt = document.querySelector('[name="startedAt"]').value;
    const closedAt = document.querySelector('[name="closedAt"]').value;

    const projectId = document.querySelector('[name="projectId"]').value;
    const userId = document.querySelector('[name="userId"]').value;

    let queryParams = '?startDate=' + startedAt + '&endDate=' + closedAt;

    if (projectId)
        queryParams += '&projectId=' + projectId;

    if (userId)
        queryParams += '&userId=' + userId;

    window.open('/v1/signings/workshop/export' + queryParams, '_blank').focus();

    generateFormJQ.modal('hide');
}