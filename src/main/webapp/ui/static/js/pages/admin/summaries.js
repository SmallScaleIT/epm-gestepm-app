const usersEndpoint = "/v1/users";
const projectsEndpoint = "/v1/projects";

$(document).ready(function() {
    initializeSelects();
});

function initializeSelects() {
    const userCustomName = user => `${user.name} ${user.surnames}`;
    const projectCustomName = project => `${project.name}`;

    const userIdEditor = document.querySelector('[name="userId"]');
    const projectEditor = document.querySelector('[name="projectId"]');

    createSelect2($(userIdEditor), usersEndpoint, null, null, userCustomName, 'generateForm');
    createSelect2($(projectEditor), projectsEndpoint, null, null, projectCustomName, 'generateForm');
}

function generateSummary() {
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