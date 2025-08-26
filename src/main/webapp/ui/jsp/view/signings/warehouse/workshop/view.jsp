<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="breadcrumbs">
    <div class="breadcrumbs-inner">
        <div class="row m-0">
            <div class="col-12 col-lg-10">
                <div class="page-header float-left">
                    <div class="page-title">
                        <h1 id="title" class="text-uppercase pt-1 pb-0"></h1>
                    </div>
                </div>
            </div>
            <div class="col-12 col-lg-2">
                <div class="page-header float-right">
                    <a id="backButton" class="btn btn-default btn-sm"><spring:message code="back"/></a>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="clearfix"></div>

<div class="content">
    <div class="card">
        <div class="card-body">
            <div class="title mb-0">
                <spring:message code="workshop" />
            </div>

            <jsp:include page="workshop-form.jsp" />
        </div>
    </div>
</div>

<script>
    let locale = '${locale}';
    let workshop;

    async function getWorkshop() {
        await axios.get('/v1' + window.location.pathname, {})
        .then(response => {
            workshop = response.data.data;
        })
        .catch(error => showNotify(error.response.data.detail, 'danger'));
    }

    function loadHeader() {
        const pageTitle = document.querySelector('#title');
        const backButton = document.querySelector('#backButton');

        pageTitle.textContent = messages.signings.workshop.title.replace('{0}', workshop.id);
        backButton.href = '/signings/warehouse/' + workshop.warehouse.id;
    }

    $(document).ready(async function () {
        await initialize();
        await getWorkshop();
        loadHeader();
        save();
    });
</script>